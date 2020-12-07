package com.atguigu.gmall.config;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.entity.GmallCorrelationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Miluo
 * @description
 **/
@Component
@Slf4j
public class RabbitAckConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;//SpringIOC 单例  getBean


    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);            //指定 ConfirmCallback
        rabbitTemplate.setReturnCallback(this);             //指定 ReturnCallback
    }




    //接收交换机应答  都要应答 不管成功或失败
    //参数1：CorrelationData 重新发送的对象
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if(ack){
            //交换机接收消息 成功    成功应答
            log.info("交换机应答成功");
        }else{
            //交换机接收消息 失败    失败应答
            log.error("cause:" + cause );
            //重新发送
            retryMessage(correlationData);

        }
    }

    //重新发送   马上  等待 5分钟
    public void retryMessage(CorrelationData correlationData){
        GmallCorrelationData gmallCorrelationData = (GmallCorrelationData) correlationData;
        //判断重发次数
        //1:先重发2次
        //2:重发2次之后 打印日志
        int retryCount = gmallCorrelationData.getRetryCount();
        if(retryCount < 2){
            retryCount++;
            gmallCorrelationData.setRetryCount(retryCount);

            //更新缓存
            redisTemplate.opsForValue().set(gmallCorrelationData.getId(),
                    JSONObject.toJSONString(correlationData));
            //发消息
            rabbitTemplate.convertAndSend(gmallCorrelationData.getExchange(),
                    gmallCorrelationData.getRoutingKey(),gmallCorrelationData.getMessage()
                    ,gmallCorrelationData);
        }else{
            log.error("重新发送次数用完：" + JSONObject.toJSONString(gmallCorrelationData));
        }
    }

    @Autowired
    private RedisTemplate redisTemplate;
    //接收队列应答   只能失败了才应答
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        // 反序列化对象输出
        System.out.println("消息主体: " + new String(message.getBody()));
        System.out.println("应答码: " + replyCode);
        System.out.println("描述：" + replyText);
        System.out.println("消息使用的交换器 exchange : " + exchange);
        System.out.println("消息使用的路由键 routing : " + routingKey);
        Object uuid = message.getMessageProperties().
                getHeader("spring_returned_message_correlation");
        if(null == uuid){
            return;
        }
        String value = (String) redisTemplate.opsForValue().get(uuid);
        if(null == value){
            return;
        }
        //从缓存获取
        GmallCorrelationData gmallCorrelationData =
                JSONObject.parseObject(value,
                        GmallCorrelationData.class);
        if(gmallCorrelationData.isDelay()){
            System.out.println("是延迟消息 不要重新发送");
            return;
        }

        //队列应答失败  重新发送
        retryMessage(gmallCorrelationData);
    }




//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//    @Autowired
//    private RedisTemplate redisTemplate;
////    @Autowired
////    private GmallCorrelationData gmallCorrelationData;
//
//    @PostConstruct
//    public void init(){
//        rabbitTemplate.setConfirmCallback(this);
//        rabbitTemplate.setReturnCallback(this);
//    }
//
//    //交换机应答
//    @Override
//    public void confirm(CorrelationData correlationData, boolean b, String s) {
//        if (b){
//            System.out.println("success");
//        }else{
//            System.out.println("交换机接收失败");
//            //重新发送
//            reSendMsg(correlationData);
//
//        }
//    }
//
//    //重新发送
//    private void reSendMsg(CorrelationData correlationData) {
//        GmallCorrelationData gmallCorrelationData = (GmallCorrelationData) correlationData;
//        Integer retryCount = gmallCorrelationData.getRetryCount();
//        if ( retryCount < 2){
//            retryCount++;
//            gmallCorrelationData.setRetryCount(retryCount);
//            rabbitTemplate.convertAndSend(gmallCorrelationData.getExchange(),gmallCorrelationData.getRoutingKey(),gmallCorrelationData.getMessage(),gmallCorrelationData);
//        }else {
//            System.out.println("重发次数用完，停止发送");
//        }
//    }
//
//    //队列应答
//    @Override
//    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
//        System.out.println("消息主体: " + new String(message.getBody()));
//        System.out.println("应答码: " + i);
//        System.out.println("描述：" + s);
//        System.out.println("消息使用的交换器 exchange : " + s1);
//        System.out.println("消息使用的路由键 routing : " + s2);
//        Object hashKey = message.getMessageProperties().getHeader("spring_returned_message_correlation");
//        Boolean isDelay = (Boolean) redisTemplate.opsForHash().get("Rabbit:"+hashKey, "isDelay");
//        if (isDelay != null){
//            if (isDelay){
//                System.out.println("这是延迟队列，不用发送");
//            }else{
//                GmallCorrelationData gmallCorrelationData = new GmallCorrelationData();
//                gmallCorrelationData.setExchange(s1);
//                gmallCorrelationData.setRoutingKey(s2);
//                gmallCorrelationData.setMessage(message.getBody().toString());
//                reSendMsg(gmallCorrelationData);
//            }
//        }
//
//
//    }
}
