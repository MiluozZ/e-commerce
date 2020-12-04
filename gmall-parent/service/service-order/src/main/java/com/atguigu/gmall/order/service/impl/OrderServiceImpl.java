package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.order.mapper.OrderServiceMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.user.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Miluo
 * @description
 **/
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderServiceMapper orderServiceMapper;
    @Autowired
    private UserFeignClient userFeignClient;

    //创建订单号
    @Override
    public OrderInfo createOrder(String userId) {
        UserInfo userInfo = userFeignClient.getUserInfo(userId);

        return null;
    }
}
