package com.atguigu.gmall.payment.service;

import com.atguigu.gmall.model.enums.PaymentType;
import com.atguigu.gmall.model.payment.PaymentInfo;

import java.util.Map;

/**
 * @author Miluo
 * @description
 **/
public interface PaymentService {
    PaymentInfo savePaymentInfo(Long orderId, PaymentType alipay);

    void updateByOutTradeNo(Map<String, String> paramsMap);
}
