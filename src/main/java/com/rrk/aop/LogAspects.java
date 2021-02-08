package com.rrk.aop;

import com.rrk.handle.LogAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义日志记录切面
 */
@Component
@Aspect
@Slf4j
public class LogAspects {



    @Around("@annotation(logAnnotation)")
    public Object doAround(ProceedingJoinPoint pjp, LogAnnotation logAnnotation) throws Throwable {
        //(0) 获取到key的值可能会有其他用途，比如去缓存里查询数据
        String key = logAnnotation.key();
        //解析注解中el表达式对应的变量的值
        log.info("获取指定的形参的值" + getKey(key, pjp));

        boolean needLog = logAnnotation.needLog();
        //(1) 记录日志
        //模拟获取当前登陆用户的方法
        String loginUserName = "james";
        //获取方法名和参数
        String methodName = pjp.getSignature().getName();
        List<Object> args = Arrays.asList(pjp.getArgs());
        if (needLog) {
            log.info("【{}】调用【{}】方法获取信息，参数为:【{}】", loginUserName, methodName, args);
        }
        //(2) 异常捕捉
        try {
            //(3)真正的业务代码
            Object proceed = pjp.proceed();
            //(3)-1  还可能有将拿到的数据存到缓存等其他操作
            return proceed;
        } catch (Throwable throwable) {
            //(4) 捕捉到异常打印一句日志
            log.error("【{}】调用【{}】方法查询数据库失败，参数为:【{}】", loginUserName, methodName, args);
            //(5) 将异常抛出交由统一异常处理类处理
            throw throwable;
        }
    }


    /***
     *  解析获得key中的真实值
     * @param key
     * @param pjp
     * @return
     */
    private String getKey(String key, ProceedingJoinPoint pjp) {
        //从连接点里获取到当前方法
        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();
        //获取方法形参的名字
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(method);
        String result = key+"{}"+parameterNames+"{}"+pjp.getArgs();
        return result;
    }
}
