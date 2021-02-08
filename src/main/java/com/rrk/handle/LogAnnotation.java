package com.rrk.handle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义日志注解
 */
@Target({ElementType.TYPE, ElementType.METHOD}) //指定注解在类上、方法上生效
@Retention(RetentionPolicy.RUNTIME) //指定本注解在运行期起作用
public @interface LogAnnotation {

    /**
     * 用于获取方法中的某个参数的值
     */
    String key();

    /**
     * 是否记录日志 默认不记录
     */
    boolean needLog() default false;
}
