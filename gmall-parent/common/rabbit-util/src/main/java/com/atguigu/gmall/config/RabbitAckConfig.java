package com.atguigu.gmall.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    //交换机应答
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        if (b){
            log.info("交换机接收成功");
        }else{
            log.error("交换机接收失败");
            //重新发送
        }
    }

    //队列应答
    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        System.out.println("消息主体: " + new String(message.getBody()));
        System.out.println("应答码: " + i);
        System.out.println("描述：" + s);
        System.out.println("消息使用的交换器 exchange : " + s1);
        System.out.println("消息使用的路由键 routing : " + s2);
        log.error("队列接收失败");
    }
}
