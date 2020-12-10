package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.constans.MQConst;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.enums.PaymentType;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.feign.OrderFeignClient;
import com.atguigu.gmall.payment.mapper.PaymentMapper;
import com.atguigu.gmall.payment.service.PaymentService;
import com.atguigu.gmall.util.RabbitTool;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

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
    @Autowired
    private RabbitTool rabbitTool;

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

    @Override
    public void updateByOutTradeNo(Map<String, String> paramsMap) {
        String outTradeNo = paramsMap.get("out_trade_no");
        PaymentInfo paymentInfo = paymentMapper.selectOne(new QueryWrapper<PaymentInfo>().eq("out_trade_no", outTradeNo));
        if (paymentInfo != null){
            //更新支付表信息
            paymentInfo.setTradeNo(paramsMap.get("trade_no"));
            paymentInfo.setCallbackTime(new Date());
            paymentInfo.setPaymentStatus(PaymentStatus.PAID.name());
            paymentInfo.setCallbackContent(JSONObject.toJSONString(paramsMap));
            paymentMapper.updateById(paymentInfo);
            //TODO 通过MQ让订单表修改订单状态
            rabbitTool.sendMessage(MQConst.EXCHANGE_DIRECT_PAYMENT_PAY,MQConst.ROUTING_PAYMENT_PAY,paymentInfo.getOrderId());

        }

    }
}
