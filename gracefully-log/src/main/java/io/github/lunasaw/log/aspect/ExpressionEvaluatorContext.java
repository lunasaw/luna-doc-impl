package io.github.lunasaw.log.aspect;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author luna
 */
public class ExpressionEvaluatorContext extends MethodBasedEvaluationContext {

    public ExpressionEvaluatorContext(Object rootObject, Method method, Object[] arguments) {
        super(rootObject, method, arguments, new DefaultParameterNameDiscoverer());
    }

    private static final InheritableThreadLocal<Stack<Map<String, Object>>> LOG_CONTEXT = new InheritableThreadLocal<>();

    /**
     * 获取当前线程的变量
     * @return
     */
    public static Map<String, Object> getVariables() {
        Stack<Map<String, Object>> stack = LOG_CONTEXT.get();
        if (stack == null || stack.size() == 0) {
            return new HashMap<>(0);
        }
        HashMap<String, Object> map = new HashMap<>();
        stack.forEach(map::putAll);
        return map;
    }

    public static void putVariable(Map<String, Object> funcVars) {
        Stack<Map<String, Object>> stack = LOG_CONTEXT.get();
        if (stack == null) {
            stack = new Stack<>();
        }
        stack.add(funcVars);
        LOG_CONTEXT.set(stack);
    }

    public static void clear() {
        LOG_CONTEXT.remove();
    }

    /**
     * 同线程多个方法调用，需要把变量放到栈中
     */
    public static void popVariable() {
        Stack<Map<String, Object>> stack = LOG_CONTEXT.get();
        if (stack != null && stack.size() > 0){
            stack.pop();
        } else {
            clear();
        }
    }
}