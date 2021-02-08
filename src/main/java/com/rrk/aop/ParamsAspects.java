package com.rrk.aop;

import cn.hutool.core.util.StrUtil;
import com.rrk.exception.ParamIsNullException;
import com.rrk.handle.ParamAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 自定义前端传参非空校验
 */
@Component
@Aspect
@Slf4j
public class ParamsAspects {

    /**
     * 自定义切入点
     */
    @Pointcut("execution(public * com.rrk.service.impl..*.*(..))")
    public void checkParam() {
    }


    /**
     * 这里采用环绕通知
     */
    @Around("@annotation(paramAnnotation)")
    public Object paramAround(ProceedingJoinPoint pjp, ParamAnnotation paramAnnotation) throws Throwable {
        //从连接点里获取到当前方法
        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();
        //获取方法形参的名字
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(method);
        //获取参数的值
        Object[] args = pjp.getArgs();
        //获取方法参数类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        //是否允许为空
        boolean b = paramAnnotation.notNull();
        if (b) {
            //不允许为空
            for (int i = 0; i < parameterNames.length; i++) {
               // System.out.println("参数名字："+parameterNames[i]+"参数类型："+parameterTypes[i]+"参数值："+args[i]);
                if (StrUtil.isBlank(args[i].toString())) {
                    System.out.println(parameterNames[i].toString()+"参数不能为空");
                    throw new ParamIsNullException(parameterNames[i],parameterTypes[i].toString(),"参数不能为空");
                }
            }
        }
        return pjp.proceed();
    }
}
