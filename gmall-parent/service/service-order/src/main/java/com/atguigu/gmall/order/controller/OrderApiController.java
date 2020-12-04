package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author Miluo
 * @description
 **/
@RestController
@Api(tags = "订单信息管理")
@RequestMapping("/api/order")
public class OrderApiController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;

    //创建订单编号
    @ApiOperation("创建订单编号")
    @GetMapping("/auth/tradeNo")
    public String getTradeNo(){
        String tradeNo = UUID.randomUUID().toString().replace("-","");
        redisTemplate.opsForValue().set("tradeNo",tradeNo);
        return tradeNo;
    }

    //创建订单信息
    @ApiOperation("创建订单信息")
    @GetMapping("/orderInfo")
    public OrderInfo createOrder(HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        return orderService.createOrder(userId);
    }

    //提交订单
    @PostMapping("/auth/submitOrder")
    @ApiOperation("提交订单")
    public Result submitOrder(OrderInfo orderInfo, @RequestParam("tradeNo")String tradeNo){
        String tradeNo1 = (String) redisTemplate.opsForValue().get("tradeNo");
        if (StringUtils.isEmpty(tradeNo1)){
            return Result.fail().message("不要重复提交");
        }
        if (!tradeNo.equals(tradeNo1)){
            return Result.fail().message("非法请求");
        }


        return null;
    }
}
