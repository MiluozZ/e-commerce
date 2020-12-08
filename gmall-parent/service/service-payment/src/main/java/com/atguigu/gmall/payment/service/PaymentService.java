package com.atguigu.gmall.payment.service;

import com.atguigu.gmall.model.enums.PaymentType;
import com.atguigu.gmall.model.payment.PaymentInfo;

/**
 * @author Miluo
 * @description
 **/
public interface PaymentService {
    PaymentInfo savePaymentInfo(Long orderId, PaymentType alipay);
}
