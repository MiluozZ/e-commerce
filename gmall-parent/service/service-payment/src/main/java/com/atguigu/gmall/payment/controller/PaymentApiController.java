package com.atguigu.gmall.payment.controller;

import com.atguigu.gmall.payment.service.AlipayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Miluo
 * @description
 **/
@RestController
@Api(tags = "支付管理")
@RequestMapping("/api/payment")
public class PaymentApiController {
    @Autowired
    private AlipayService alipayService;

    @ApiOperation("支付宝支付")
    @GetMapping("/alipay/submit/{orderId}")
    public String submit(@PathVariable Long orderId){
        return alipayService.submit(orderId);
    }
}
