package com.atguigu.gmall.payment.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gmall.payment.config.AlipayConfig;
import com.atguigu.gmall.payment.service.AlipayService;
import com.atguigu.gmall.payment.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Miluo
 * @description
 **/
@Controller
@Api(tags = "支付管理")
@RequestMapping("/api/payment/alipay")
public class PaymentApiController {
    @Autowired
    private AlipayService alipayService;
    @Autowired
    private PaymentService paymentService;

    @ResponseBody
    @ApiOperation("支付宝支付")
    @GetMapping("/submit/{orderId}")
    public String submit(@PathVariable Long orderId){
        return alipayService.submit(orderId);
    }

    @ApiOperation("支付宝下单同步通知回调")
    @GetMapping("/callback/return")
    public String callbackReturn(){
        return "redirect:" + AlipayConfig.return_order_url;
    }

    @ApiOperation("支付宝下单异步通知回调")
    @PostMapping("/callback/notify")
    @ResponseBody
    public String callbackNotify(@RequestParam Map<String, String> paramsMap){
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(paramsMap, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名
            if(signVerified){
                if("TRADE_SUCCESS".equals(paramsMap.get("trade_status"))){
                    System.out.println("支付宝异步回调接收失成功");
                    //异步回调接收成功了更新支付表相关状态及数据
                    paymentService.updateByOutTradeNo(paramsMap);
                }else{
                    //
                }
                return "success";
            }else{
                System.out.println("支付宝异步回调接收失败");
                return "failure";
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        System.out.println("支付宝异步回调接收失败");
        return "failure";
    }
}
