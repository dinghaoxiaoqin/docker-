package com.rrk.utils;

import com.alibaba.fastjson.JSON;
import com.rrk.entity.Detail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RabbitmqUtils implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {


    private Integer COUNT = 0;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 确定消息是否发送到交换机
     * @param correlationData
     * @param
     * @param
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("发送的消息的唯一主键："+correlationData.getId());
        System.out.println("消息发送到交换机的结果："+ack);
        System.out.println("要是失败了：原因是："+cause);
    }

    /**
     * 发送的消息是否到rabbitmq
     * @param message
     * @param
     * @param
     * @param
     * @param
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("消息主体message->{}"+message);
        log.info("消息主体replyCode->{}"+replyCode);
        log.info("描述replyText->{}"+replyText);
        log.info("消息使用的交换机->{}"+exchange);
        log.info("消息使用的路由键->{}",routingKey);

    }

    public void sendDetail(Detail detail) {
        COUNT++;
        System.out.println("发送到mq的消息："+detail);
        try {
            Message build = MessageBuilder.withBody(JSON.toJSONString(detail).getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8").build();
            //给定唯一标识（保证幂等性）
            CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
            //将mq发送的消息在redis中进行备份
            redisTemplate.opsForHash().put("mq_data",correlationData.getId(),JSON.toJSONString(detail));
            //int i = 1/0;
            rabbitTemplate.convertAndSend("detail_exchange","detail_routing",build,correlationData);
            System.out.println("发送消息对mq成功的id:"+correlationData.getId());
        } catch (Exception e){
            System.out.println("发送消息失败："+e);
        }finally {
            System.out.println("重新发送消息的次数："+COUNT);
        }

    }

    /**
     * 对于消费失败的消息加入死信队列
     * @param message
     */
    public void sendDead(Message message) {
        try {
            String msgStr = new String(message.getBody(), "UTF-8");
            Detail detail = JSON.parseObject(msgStr, Detail.class);
            Message build = MessageBuilder.withBody(JSON.toJSONString(detail).getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8").build();
            //给定唯一标识（保证幂等性）
            CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
            //将mq发送的消息在redis中进行备份
            redisTemplate.opsForHash().put("mq_data",correlationData.getId(),JSON.toJSONString(detail));
            //int i = 1/0;
            rabbitTemplate.convertAndSend("detail_exchange","dead_routing",build,correlationData);
            System.out.println("发送死信消息对mq成功的id:"+correlationData.getId());
        } catch (Exception e){
            System.out.println("发送死信消息失败："+e);
        }
    }

    public void sendDelay(Detail detail) {
        try {
            Message build = MessageBuilder.withBody(JSON.toJSONString(detail).getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8").build();
            //给定唯一标识（保证幂等性）
            CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
            //将mq发送的消息在redis中进行备份
            rabbitTemplate.convertAndSend("delay_exchange", "delay_routing", (Object) build, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    //延迟半个小时
                    message.getMessageProperties().setHeader("x-delay", 2*60*1000);
                    return message;
                }
            },correlationData);
            System.out.println("发送延迟消息对mq成功的id:"+correlationData.getId());
        } catch (Exception e){
            System.out.println("发送死信消息失败："+e);
        }
    }
}
