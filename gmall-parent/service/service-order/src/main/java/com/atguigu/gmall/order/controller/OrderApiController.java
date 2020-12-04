package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
    public String getTradeNo(HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        String tradeNo = UUID.randomUUID().toString().replace("-","");
        redisTemplate.opsForValue().set("tradeNo"+userId,tradeNo);
        return tradeNo;
    }


    //提交订单
    @PostMapping("/auth/submitOrder")
    @ApiOperation("提交订单")
    public Result submitOrder(@RequestBody OrderInfo orderInfo, @RequestParam("tradeNo")String tradeNo,HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        String tradeNo1 = (String) redisTemplate.opsForValue().get("tradeNo"+userId);
        if (StringUtils.isEmpty(tradeNo1)){
            return Result.fail().message("不要重复提交");
        }
        if (!tradeNo.equals(tradeNo1)){
            return Result.fail().message("非法请求");
        }
        redisTemplate.delete("tradeNo"+userId);
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        //判断是否有货
        for (OrderDetail orderDetail : orderDetailList) {
            Long skuId = orderDetail.getSkuId();
            Integer skuNum = orderDetail.getSkuNum();
            Boolean hasStock = orderService.hasStock(skuId,skuNum);
            if (!hasStock){
                return Result.fail().message(orderDetail.getSkuName() + "无货");
            }
        }
        orderInfo.setUserId(Long.parseLong(userId));
        Long orderId = orderService.saveOrderInfo(orderInfo);
        return Result.ok(orderId);

    }
}
