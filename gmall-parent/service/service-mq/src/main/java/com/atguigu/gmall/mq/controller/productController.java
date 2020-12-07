package com.atguigu.gmall.mq.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.util.RabbitTool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Miluo
 * @description
 * 消费生产者
 **/
@RestController
@Api("消息生产")
@RequestMapping("/api/mq")
public class productController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitTool rabbitTool;

//    @GetMapping("/product")
//    @ApiOperation("消息生产")
//    public Result product(){
//        rabbitTemplate.convertAndSend("exchangeTest","routingKeyTest","Rabbit测试");
//        return Result.ok();
//    }

    @GetMapping("/product")
    @ApiOperation("消息生产")
    public Result product(){
        rabbitTool.sendMessage("exchangeTest","routingKeyTest1","Rabbit测试");
        return Result.ok();
    }
}
