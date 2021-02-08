package com.rrk.aop;

import cn.hutool.core.convert.Convert;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义切面
 */
@Aspect
@Component
public class AopAspects {

    /**
     * 拦截的业务类
     */
    @Pointcut("execution(public * com.rrk.service.impl.AopServiceImpl.add(..))")
    public void pointCut() {
    }

    /**
     * 前置通知
     */
    @Before("pointCut()")
    public void beforeAdd(JoinPoint joinPoint){
        //获取到请求参数列表
        Object[] args = joinPoint.getArgs();
        //获取到请求方法
        String name = joinPoint.getSignature().getName();
        List<Object> objects = Arrays.asList(args);
        Object o1 = objects.get(0);
        Object o2 = objects.get(1);
        System.out.println("第一个请求参数：a->"+Convert.toInt(o1));
        System.out.println("第二个请求参数：b->"+Convert.toInt(o2));
        System.out.println("@Before:当前方法为：" + name + "======参数列表是:" + Arrays.asList(args));

    }

    /**
     * 后置通知
     */
    @After(value = "pointCut()")
    public void  afterAdd(JoinPoint joinPoint){
        //获取到请求参数列表
        Object[] args = joinPoint.getArgs();
        //获取到请求方法
        String name = joinPoint.getSignature().getName();
        List<Object> objects = Arrays.asList(args);
        Object o1 = objects.get(0);
        Object o2 = objects.get(1);
        System.out.println("第一个请求参数：a->"+Convert.toInt(o1));
        System.out.println("第二个请求参数：b->"+Convert.toInt(o2));
        System.out.println("@After:当前方法为：" + name + "======参数列表是:" + Arrays.asList(args));
    }

    /**
     * 返回通知
     */
    @AfterReturning(value = "pointCut()")
    public void returnAdd(JoinPoint joinPoint){
        //获取到请求参数列表
        Object[] args = joinPoint.getArgs();
        //获取到请求方法
        String name = joinPoint.getSignature().getName();
        List<Object> objects = Arrays.asList(args);
        Object o1 = objects.get(0);
        Object o2 = objects.get(1);
        System.out.println("第一个请求参数：a->"+Convert.toInt(o1));
        System.out.println("第二个请求参数：b->"+Convert.toInt(o2));
        System.out.println("@AfterReturning:当前方法为：" + name + "======参数列表是:" + Arrays.asList(args));
    }

    @Around(value = "pointCut()")
    public Object aroundAdd(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        System.out.println("@Arount:执行目标方法之前...");
        //相当于开始调div地
        Object obj = proceedingJoinPoint.proceed();
        System.out.println("@Arount:执行目标方法之后...");
        return obj;
    }
}
