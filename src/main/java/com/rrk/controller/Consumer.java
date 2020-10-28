package com.rrk.controller;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer implements ChannelAwareMessageListener {

    @RabbitListener(queues = "docker_queue")
    @RabbitHandler
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String str = new String(message.getBody(),"utf-8");
        //打印消息
        System.out.println("消费者："+str);
    }
}
