package com.rrk.consume;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.rrk.entity.Detail;
import com.rrk.service.IDetailService;
import com.rrk.utils.RabbitmqUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class DetailConsume implements ChannelAwareMessageListener {
    /**
     * 记录消息重复消费的次数
     */
    private Integer COUNT = 0;

    @Autowired
    private IDetailService detailService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitmqUtils rabbitmqUtils;


    @RabbitListener(queues = "dead-queue")
    @RabbitHandler
    public void process(Message message, Channel channel) throws IOException {
        String msgStr = new String(message.getBody(), "UTF-8");
        Object correlation = message.getMessageProperties().getHeaders().get("spring_returned_message_correlation");
        System.out.println("correlation" + correlation);
        System.out.println("收到进入死信队列消息：" + msgStr);
        Detail detail = JSON.parseObject(msgStr, Detail.class);
        //死信消息加入
        redisTemplate.opsForHash().put("dead_data", detail.getMainId().toString(),msgStr);
        //确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }


    @RabbitListener(queues = "detail_queue")
    @RabbitHandler
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        Object correlation = message.getMessageProperties().getHeaders().get("spring_returned_message_correlation");
        Object mqData = redisTemplate.opsForHash().get("mq_data", correlation.toString());
        try {

            //判断该消息是否已经消费
            if (ObjectUtil.isNull(mqData)) {
                //说明已经消费成功，消息不重回队列（防止重发消息）
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                return;
            }
            String msgStr = new String(message.getBody(), "UTF-8");
            System.out.println("接收到要消息：msgStr->{}" + msgStr);
            if (StrUtil.isNotBlank(msgStr)) {
                Detail detail = JSON.parseObject(msgStr, Detail.class);
                int i = 1/0;
                detailService.save(detail);
                System.out.println("接收到消息保存到数据库成功");
            }
            //牵手模式设置   true:确认所有consumer获得的消息 false 只确认当前一个消息收到
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            System.out.println("接收到消息的唯一主键(消费成功)id：" + correlation);
            //消费成功就删除redis中备份的数据
            redisTemplate.opsForHash().delete("mq_data", correlation.toString());
        } catch (Exception e) {
            COUNT++;
            System.out.println("保存到库失败原因：e->{}" + e);
//            if (message.getMessageProperties().getRedelivered()) {
//                System.out.println("保存detail消息已重复处理失败,拒绝再次接收...");
//                // 拒绝消息
//                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
//            } //else {
            System.out.println("消息即将再次返回队列处理...");
            // requeue为是否重新回到队列
            throw new RuntimeException("消息失败：e->{}",e);
            // channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            // }
        } finally {
            System.out.println("重复接收消息的次数---------------------------------------：" + COUNT);
            //消息消费失败---》重试超过3次，就加入死信队列
            if (COUNT >= 3) {
                System.out.println("处理数据重复失败，消息加入死信队列");
                //不重新入队，发送到死信队列
                rabbitmqUtils.sendDead(message);
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                redisTemplate.opsForHash().delete("mq_data", correlation.toString());
                System.out.println("进入死信队列执行完成" + correlation);
            }
        }
    }


    @RabbitListener(queues = "delay_queue")
    @RabbitHandler
    public void delayMessage(Message message, Channel channel) throws Exception {
        Object correlation = message.getMessageProperties().getHeaders().get("spring_returned_message_correlation");
        Object mqData = redisTemplate.opsForHash().get("mq_data", correlation.toString());
        try {

            //判断该消息是否已经消费
            if (ObjectUtil.isNull(mqData)) {
                //说明已经消费成功，消息不重回队列（防止重发消息）
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                return;
            }
            String msgStr = new String(message.getBody(), "UTF-8");
            System.out.println("接收到要消息：msgStr->{}" + msgStr);
            if (StrUtil.isNotBlank(msgStr)) {
                Detail detail = JSON.parseObject(msgStr, Detail.class);
                //int i = 1/0;
                detailService.save(detail);
                System.out.println("接收到消息保存到数据库成功");
            }
            //牵手模式设置   true:确认所有consumer获得的消息 false 只确认当前一个消息收到 --》让消息队列删除队列的消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            System.out.println("接收到消息的唯一主键(消费成功)id：" + correlation);
            //消费成功就删除redis中备份的数据
            redisTemplate.opsForHash().delete("mq_data", correlation.toString());
        } catch (Exception e) {
            COUNT++;
            System.out.println("保存到库失败原因：e->{}" + e);
//            if (message.getMessageProperties().getRedelivered()) {
//                System.out.println("保存detail消息已重复处理失败,拒绝再次接收...");
//                // 拒绝消息
//                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
//            } //else {
            System.out.println("消息即将再次返回队列处理...");
            // requeue为是否重新回到队列
            throw new RuntimeException("消息失败：e->{}",e);
            // channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            // }
        } finally {
            System.out.println("重复接收消息的次数9999999999999999999---------------------------------------：" + COUNT);
            //消息消费失败---》重试超过3次，就加入死信队列
            if (COUNT >= 3) {
                System.out.println("处理数据重复失败，消息加入死信队列");
                //不重新入队，发送到死信队列
                rabbitmqUtils.sendDead(message);
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                redisTemplate.opsForHash().delete("mq_data", correlation.toString());
                System.out.println("进入死信队列执行完成" + correlation);
            }
        }
    }
}
