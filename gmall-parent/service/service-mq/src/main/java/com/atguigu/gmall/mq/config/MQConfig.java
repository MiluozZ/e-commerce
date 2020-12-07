package com.atguigu.gmall.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Miluo
 * @description
 * 死信队列配置
 **/
@Configuration
public class MQConfig {

    public static final String exchange_dead = "exchange.dead";
    public static final String routing_dead_1 = "routing.dead.1";
    public static final String routing_dead_2 = "routing.dead.2";
    public static final String queue_dead_1 = "queue.dead.1";
    public static final String queue_dead_2 = "queue.dead.2";

    @Bean
    public Queue queue1(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", exchange_dead);
        arguments.put("x-dead-letter-routing-key", routing_dead_2);
        arguments.put("x-message-ttl", 60 * 1000);//过期时间 1分钟  全局时间
        return QueueBuilder.durable(queue_dead_1).withArguments(arguments).build();
    }

    @Bean
    public Queue queue2(){
        return QueueBuilder.durable(queue_dead_2).build();
    }

    @Bean
    public Exchange exchange(){
        return ExchangeBuilder.directExchange(exchange_dead).build();
    }

    @Bean
    public Binding binding1(){
        return BindingBuilder.bind(queue1()).to(exchange()).with(routing_dead_1).noargs();
    }

    @Bean
    public Binding bind2ing(){
        return BindingBuilder.bind(queue2()).to(exchange()).with(routing_dead_2).noargs();
    }
}
