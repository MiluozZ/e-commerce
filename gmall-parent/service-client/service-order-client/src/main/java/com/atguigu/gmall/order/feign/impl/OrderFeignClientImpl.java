package com.atguigu.gmall.order.feign.impl;

import com.atguigu.gmall.order.feign.OrderFeignClient;
import org.springframework.stereotype.Service;

/**
 * @author Miluo
 * @description
 **/
@Service
public class OrderFeignClientImpl implements OrderFeignClient {
    @Override
    public String getTradeNo() {
        return null;
    }
}
