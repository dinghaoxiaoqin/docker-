package com.rrk.handle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义传参校验的注解
 */
@Target({ElementType.TYPE, ElementType.METHOD}) //指定注解在类上、方法上生效
@Retention(RetentionPolicy.RUNTIME) //指定本注解在运行期起作用
public @interface ParamAnnotation {

    /**
     * 是否非空,默认不能为空
     */
    boolean notNull() default true;

    /**
     * 默认值
     * @return
     */
    String defaultValue() default "";
}
