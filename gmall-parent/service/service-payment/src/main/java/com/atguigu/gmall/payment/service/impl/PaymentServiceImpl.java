package com.atguigu.gmall.payment.service.impl;

import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.enums.PaymentType;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.feign.OrderFeignClient;
import com.atguigu.gmall.payment.mapper.PaymentMapper;
import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Miluo
 * @description
 **/
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public PaymentInfo savePaymentInfo(Long orderId, PaymentType alipay) {
        OrderInfo orderInfo = orderFeignClient.getOrderInfoById(orderId);
        if (orderId != null){
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setCreateTime(new Date());
            paymentInfo.setOrderId(orderInfo.getId());
            paymentInfo.setPaymentType(alipay.name());
            paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
            paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());
            paymentInfo.setSubject(orderInfo.getTradeBody());
            paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
            paymentMapper.insert(paymentInfo);
            return paymentInfo;
        }
        return null;
    }
}
