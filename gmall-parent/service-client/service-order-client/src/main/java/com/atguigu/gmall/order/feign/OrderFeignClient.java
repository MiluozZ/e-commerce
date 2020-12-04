package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.order.feign.impl.OrderFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Miluo
 * @description
 **/
@FeignClient(name = "service-order",fallback = OrderFeignClientImpl.class)
public interface OrderFeignClient {
    @GetMapping("/api/order/auth/tradeNo")
    String getTradeNo();
}
