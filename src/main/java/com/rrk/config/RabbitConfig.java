package com.rrk.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 订单的队列相关配置
 */
@Configuration

public class RabbitConfig {



    /**
     * 简单队列
     */
    @Bean
    public Queue cancelOrderQueue(){
        Queue queue =new Queue("docker_queue",true);
        queue.setShouldDeclare(true);
        return queue;
    }



}
