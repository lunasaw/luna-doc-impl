package io.github.lunasaw.log.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Throwables;
import com.luna.common.sensitive.SensitiveUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author luna
 */
@Slf4j(topic = "LP")
@Aspect
@Component
public class LpLogAspect {


    @Pointcut("execution(* io.github.lunasaw.log.service.*.*(..))")
    public void lpLogAspect() {
    }

    @Around("lpLogAspect()")
    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = getRealMethod(joinPoint);
        StringBuilder sbLog = new StringBuilder();
        Class<?> clazz = method.getDeclaringClass();
        String methodName = clazz.getName() + "." + method.getName();
        sbLog.append(methodName).append(" >> param=");
        if (joinPoint.getArgs() != null) {
            try {
                sbLog.append(JSON.toJSONString(joinPoint.getArgs(), SerializerFeature.IgnoreNonFieldGetter)).append("");
            } catch (Exception e) {
                sbLog.append("json parse error >> e=").append(e.getMessage());
            }
        }

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable var10) {
            log.error("{} >> error={}", sbLog, Throwables.getStackTraceAsString(var10), var10);
            throw var10;
        }

        try {
            if (result != null) {
                String jsonResult = JSON.toJSONString(result, SerializerFeature.IgnoreNonFieldGetter);
                jsonResult = SensitiveUtil.regexReplaceTelPhone(jsonResult);

                sbLog.append(" >> result=").append(jsonResult);
            }
        } catch (Throwable var8) {
            sbLog.append(" >> result=").append(Throwables.getStackTraceAsString(var8));
        }

        log.info(sbLog.toString());
        return result;
    }

    public static Method getRealMethod(JoinPoint pjp) throws NoSuchMethodException {
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        Method method = ms.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            method = pjp.getTarget().getClass().getDeclaredMethod(ms.getName(), method.getParameterTypes());
        }

        return method;
    }
}