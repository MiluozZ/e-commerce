package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.constans.MQConst;
import com.atguigu.gmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Miluo
 * @description MQ消费者
 **/
@Component
public class OrderListener {
    @Autowired
    private OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_PAYMENT_PAY,durable = "true",autoDelete = "false"),
            exchange = @Exchange(value = MQConst.EXCHANGE_DIRECT_PAYMENT_PAY),
            key = MQConst.ROUTING_PAYMENT_PAY
    ))
    public void updateInfoAfterPay(Long orderId, Channel channel, Message message){
        try {
            orderService.updateInfoAfterPay(orderId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
