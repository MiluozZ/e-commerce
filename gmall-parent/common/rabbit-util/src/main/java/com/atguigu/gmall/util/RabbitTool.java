package com.atguigu.gmall.util;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.entity.GmallCorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Miluo
 * @description
 **/
@Component
public class RabbitTool {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    //发消息（普通）
    public void sendMessage(String exchange, String routingKey, Object message) {

        //为了防止消息发送失败
        GmallCorrelationData correlationData = new GmallCorrelationData();
        //UUID
        String uuid = UUID.randomUUID().toString();
        correlationData.setId(uuid);
        //交换机
        correlationData.setExchange(exchange);
        //路由Key
        correlationData.setRoutingKey(routingKey);
        //消息
        correlationData.setMessage(message);

        //缓存
        redisTemplate.opsForValue().set(uuid, JSONObject.toJSONString(correlationData));
        //发消息
        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
    }

    public void sendDeadMessage(String exchange,String routingKey , String message,int delayTime){
        GmallCorrelationData gmallCorrelationData = new GmallCorrelationData();
        //UUID
        String uuid = UUID.randomUUID().toString();
        gmallCorrelationData.setId(uuid);
        //交换机
        gmallCorrelationData.setExchange(exchange);
        //路由Key
        gmallCorrelationData.setRoutingKey(routingKey);
        //消息
        gmallCorrelationData.setMessage(message);
        //过期时间
        gmallCorrelationData.setDelayTime(delayTime);

        redisTemplate.opsForValue().set(uuid, JSONObject.toJSONString(gmallCorrelationData));
        rabbitTemplate.convertAndSend(exchange,routingKey,message,(msg) ->{
            //死信过期时间  TTL  ms     6*1000  == 6s
            msg.getMessageProperties().setExpiration(String.valueOf(delayTime*1000));
            return msg;
        },gmallCorrelationData);
    }


//    public void sendMessage(String exchange,String routingKey,String message){
//        GmallCorrelationData gmallCorrelationData = new GmallCorrelationData();
//        String s = UUID.randomUUID().toString();
//        gmallCorrelationData.setId(s);
//        gmallCorrelationData.setExchange(exchange);
//        gmallCorrelationData.setRoutingKey(routingKey);
//        gmallCorrelationData.setMessage(message);
//
//        redisTemplate.opsForValue().set(s, JSONObject.toJSONString(gmallCorrelationData));
//
//        rabbitTemplate.convertAndSend(exchange,routingKey,message,gmallCorrelationData);
//    }
}
