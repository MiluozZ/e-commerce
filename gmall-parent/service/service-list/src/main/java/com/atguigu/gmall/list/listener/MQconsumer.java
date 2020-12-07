package com.atguigu.gmall.list.listener;

import com.atguigu.gmall.constans.MQConst;
import com.atguigu.gmall.list.service.ListSearchService;
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
 * @description
 **/
@Component
public class MQconsumer {
    @Autowired
    private ListSearchService listSearchService;

    @RabbitListener(bindings = {@QueueBinding(
            exchange = @Exchange(MQConst.EXCHANGE_DIRECT_GOODS),
            value = @Queue(value = MQConst.QUEUE_GOODS_UPPER , durable = "true",autoDelete = "false"),
            key = {MQConst.ROUTING_GOODS_UPPER}
    )})
    public void onSaleConsumer(Long skuId, Channel channel, Message message){
        try {
            listSearchService.onSale(skuId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(bindings = {@QueueBinding(
            exchange = @Exchange(MQConst.EXCHANGE_DIRECT_GOODS),
            value = @Queue(value = MQConst.QUEUE_GOODS_LOWER , durable = "true",autoDelete = "false"),
            key = {MQConst.ROUTING_GOODS_LOWER}
    )})
    public void cancleSaleConsumer(Long skuId, Channel channel, Message message){
        try {
            listSearchService.cancelSale(skuId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
