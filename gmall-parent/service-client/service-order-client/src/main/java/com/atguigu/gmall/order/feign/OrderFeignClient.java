package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.feign.impl.OrderFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Miluo
 * @description
 **/
@FeignClient(name = "service-order",fallback = OrderFeignClientImpl.class)
public interface OrderFeignClient {
    @GetMapping("/api/order/auth/tradeNo")
    String getTradeNo();

    //根据Id获取订单信息
    @GetMapping("/api/order/inner/orderInfo/{orderId}")
    OrderInfo getOrderInfoById(@PathVariable Long orderId);
}
