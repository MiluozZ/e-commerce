package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.feign.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Miluo
 * @description
 **/
@Controller
public class PayController {
    @Autowired
    private OrderFeignClient orderFeignClient;

    @GetMapping("/pay.html")
    public String payment(Long orderId, Model model){
        OrderInfo orderInfo = orderFeignClient.getOrderInfoById(orderId);
        model.addAttribute("orderInfo",orderInfo);
        return "payment/pay";
    }

    @GetMapping("/pay/success.html")
    public String success(){
        return "payment/success";
    }
}
