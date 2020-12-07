package com.atguigu.gmall.mq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Miluo
 * @description
 **/
@Component
public class consumer {

    @RabbitListener(queues = "queueTest")
    public void consumeMsg(String msg, Channel channel, Message message){
        System.out.println(msg);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
