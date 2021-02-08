package com.rrk.dataSources;

import java.lang.annotation.*;

/**
 * 自定义数据源切换的注解 ，用于aop切点使用
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceAnnonation {

    //默认值为ONE，因为后面我们选择配置这个ONE为默认数据库
    String value() default DataSourceNames.ONE;
}


