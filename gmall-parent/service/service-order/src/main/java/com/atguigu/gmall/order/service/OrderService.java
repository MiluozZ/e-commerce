package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

/**
 * @author Miluo
 * @description
 **/
public interface OrderService {
    OrderInfo createOrder(String userId);
}
