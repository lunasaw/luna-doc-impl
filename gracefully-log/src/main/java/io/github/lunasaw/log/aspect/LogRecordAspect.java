package io.github.lunasaw.log.aspect;

import com.google.common.collect.ImmutableMap;
import io.github.lunasaw.log.anno.LogRecord;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * @author luna
 * @date 2023/5/1
 */
@Aspect
@Component
public class LogRecordAspect implements ApplicationContextAware {

    public static final Logger log = LoggerFactory.getLogger("OPERATE");

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    private ApplicationContext applicationContext;


    @Around("@annotation(io.github.lunasaw.log.anno.LogRecord)")
    public Object invoke(ProceedingJoinPoint joinPoint) {
        // 记录日志
        return execute(joinPoint);
    }

    /**
     * @param joinPoint 切点
     * @return
     */
    @SneakyThrows
    private Object execute(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogRecord annotation = method.getAnnotation(LogRecord.class);
        ExpressionEvaluatorContext evaluationContext = new ExpressionEvaluatorContext(joinPoint.getTarget(), method, joinPoint.getArgs());
        evaluationContext.setBeanResolver(new BeanFactoryResolver(applicationContext));


        // 只打印用户操作日志
        String operator = parse(annotation.operator(), evaluationContext, String.class);
        String fail = parse(annotation.fail(), evaluationContext, String.class);
        String bizNo = parse(annotation.bizNo(), evaluationContext, String.class);
        String category = parse(annotation.category(), evaluationContext, String.class);
        Boolean condition = parse(annotation.condition(), evaluationContext, Boolean.class);

        Map<String, Object> paramMap = new HashMap<>();
        Object ret;
        try {
            ret = joinPoint.proceed();
            paramMap.put("_result", ret);
        } catch (Throwable e) {
            String message = e.getMessage();
            paramMap.put("_error", message);
            log.info("bizNo:{}, category:{}, operator:{}, fail:{}", bizNo, category, operator, fail);
            throw e;
        }
        ExpressionEvaluatorContext.putVariable(paramMap);


        // 获取父线程的数据
        Map<String, Object> variables = ExpressionEvaluatorContext.getVariables();
        if (variables.size() > 0) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                evaluationContext.setVariable(entry.getKey(), entry.getValue());
            }
        }


        String success = parse(annotation.success(), evaluationContext, String.class);

        if (condition) {
            log.info("bizNo:{}, category:{}, operator:{}, success:{}", bizNo, category, operator, success);
        }

        ExpressionEvaluatorContext.popVariable();
        return ret;
    }


    public <T> T parse(String content, StandardEvaluationContext context, Class<T> clazz) {
        return PARSER.parseExpression(resolve(content)).getValue(context, clazz);
    }

    /**
     * 作用是读取yml里面的值
     *
     * @param value 例如：1. #{${ttt.xxx}}会读取yml的ttt.xxx: read配置值，替换为#{read}
     *              2.#{read}直接返回#{read}
     * @return #{read}
     */
    private String resolve(String value) {
        if (this.applicationContext != null && this.applicationContext instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) this.applicationContext).resolveEmbeddedValue(value);
        }
        return value;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
