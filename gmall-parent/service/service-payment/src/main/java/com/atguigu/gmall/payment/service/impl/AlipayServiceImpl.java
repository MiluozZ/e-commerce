package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.atguigu.gmall.model.enums.PaymentType;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.config.AlipayConfig;
import com.atguigu.gmall.payment.service.AlipayService;
import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Miluo
 * @description
 **/
@Service
public class AlipayServiceImpl implements AlipayService {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private AlipayConfig alipayConfig;

    @Override
    public String submit(Long orderId) {
        //先生成支付流水
        PaymentInfo paymentInfo = paymentService.savePaymentInfo(orderId, PaymentType.ALIPAY);
        if (paymentInfo != null){
            AlipayClient alipayClient = alipayConfig.alipayClient();
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();

//            request.setReturnUrl( AlipayConfig.return_payment_url );
//            request.setNotifyUrl( AlipayConfig.notify_payment_url ); //在公共参数中设置回跳和通知地址

            Map<String, Object> map = new HashMap<>();
            //官方文档必填项
            map.put("out_trade_no",paymentInfo.getOutTradeNo());
            map.put("product_code","FAST_INSTANT_TRADE_PAY");
            map.put("total_amount",paymentInfo.getTotalAmount());
            map.put("subject",paymentInfo.getSubject());

            request.setBizContent(JSONObject.toJSONString(map));
            //获取需提交的form表单
            AlipayTradePagePayResponse response = null;
            try {
                response = alipayClient.pageExecute(request);
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
            //客户端拿到submitFormData做表单提交
            String submitFormData = response.getBody();
            return submitFormData;
        }
        return null;
    }
}
