package com.rrk.controller;

import com.alibaba.fastjson.JSON;
import com.rrk.dao.TbRegionMapper;
import com.rrk.entity.TbRegion;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private TbRegionMapper regionMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping(value = "/testA")
    public String testA(){
        return "第一次采用dockerFile制作jar的镜像，多一点支持哈";
    }

    @GetMapping(value = "/testB")
    public String testB(){
        return "-----------------------testB";
    }

    /**
     * 获取省市区（县）三级联动的数据
     */
    @GetMapping(value = "/getRegionList")
    public String getRegionList(){
        List<TbRegion> tbRegions = regionMapper.selectList(null);
        redisTemplate.opsForValue().set("test","docker test");
        Message message = MessageBuilder.withBody(new String("生产者发送一条消息").getBytes()).build();
        rabbitTemplate.convertAndSend("docker_queue",message);
        return JSON.toJSONString(tbRegions);
    }
}
