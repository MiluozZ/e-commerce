package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

/**
 * @author Miluo
 * @description
 **/
public interface OrderService {


    Boolean hasStock(Long skuId, Integer skuNum);

    Long saveOrderInfo(OrderInfo orderInfo);

    OrderInfo getOrderInfoById(Long orderId);
}
