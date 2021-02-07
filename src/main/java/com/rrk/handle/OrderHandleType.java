package com.rrk.handle;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * 定义根据不同订单来源注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface OrderHandleType {
    String source();
}
