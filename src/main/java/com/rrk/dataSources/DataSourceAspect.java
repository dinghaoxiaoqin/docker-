package com.rrk.dataSources;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;



/**
 * 自定义多数据源切换的切面
 */
@Aspect
@Component
@Slf4j
@Order(-100)
public class DataSourceAspect {

    /**
     * 确定数据源的切点
     */
//    @Pointcut("@annotation(com.rrk.dataSources.DataSourceAnnonation)")
//    public void dataSourcePointCut() {
//    }

    @Pointcut("@within(com.rrk.dataSources.DataSource) || @annotation(com.rrk.dataSources.DataSource)")
    public void pointcut() {
    }

//    /**
//     * 采用环绕通知
//     */
//    @Around(value = "dataSourcePointCut()")
//    public Object pointcut(ProceedingJoinPoint pjp) throws Throwable {
//        //从连接点里获取到当前方法
//        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();
//        DataSourceAnnonation ds = method.getAnnotation(DataSourceAnnonation.class);
//        // 通过判断 DataSourceAnnonation 中的值来判断当前方法应用哪个数据源
//        DynamicDataSource.setDataSource(ds.value());
//        log.info("AOP切换数据源成功，数据源为: " + ds.value());
//        try {
//            return pjp.proceed();
//        } finally {
//            //清除数据
//             DynamicDataSource.clearDataSource();
//            log.info("clean datasource");
//        }
//
//    }


    @Before("pointcut() && @annotation(dataSource)")
    public void doBefore(DataSource dataSource) {
        log.info("选择的数据源是：{}",dataSource.value().getValue());
        DataSourceContextHolder.setDataSource(dataSource.value().getValue());
    }

    @After("pointcut()")
    public void jarvisWxDb () {
        log.info("清除上下文数据");
        DataSourceContextHolder.clear();
    }
}
