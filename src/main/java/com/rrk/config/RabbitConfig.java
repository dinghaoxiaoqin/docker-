package com.rrk.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单的队列相关配置
 */
@Configuration

public class RabbitConfig {



    /**
     * 死信队列
     */
    @Bean
    public Queue deadQueue(){
        Queue queue =new Queue("dead-queue",true);
        queue.setShouldDeclare(true);
        return queue;
    }

    /**
     * 创建detail的direct交换机
     */
    @Bean
    public Exchange detailExchage(){
        //交换机做了持久化durable
       return ExchangeBuilder.directExchange("detail_exchange").durable(true).build();
    }


    /**
     * detail的队列
     */
    @Bean
    public Queue detailQueue(){
        //队列做了持久化
        Queue queue =new Queue("detail_queue",true);
        queue.setShouldDeclare(true);
        return queue;
    }

    /**
     * detail的路由键
     */
    @Bean
    public Binding detailBinding(){
        return BindingBuilder
                .bind(detailQueue())
                .to(detailExchage())
                .with("detail_routing").noargs();
    }

    /**
     * dead的路由键
     */
    @Bean
    public Binding deadBinding(){
        return BindingBuilder
                .bind(deadQueue())
                .to(detailExchage())
                .with("dead_routing").noargs();
    }


    /**
     * 创建延迟交换机
     */
    @Bean
    public CustomExchange delayExchange(){
        Map<String, Object> args = new HashMap<>();
        //交换机的类型
        args.put("x-delayed-type","direct");
        return new CustomExchange("delay_exchange", "x-delayed-message",true, false,args);
    }

    /**
     * 延迟队列
     */
    @Bean
    public Queue delayQueue(){
        Queue queue =new Queue("delay_queue",true);
        queue.setShouldDeclare(true);
        return queue;
    }

    /**
     * 取消订单的路由键
     */
    @Bean
    public Binding cancelOrderBinding(){
        return BindingBuilder
                .bind(delayQueue())
                .to(delayExchange())
                .with("delay_routing").noargs();
    }


}
