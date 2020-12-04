package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.feign.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.order.feign.OrderFeignClient;
import com.atguigu.gmall.user.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Miluo
 * @description
 **/
@Controller
public class OrderController {
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private CartFeignClient cartFeignClient;




    @GetMapping("/trade.html")
    public String trade(Model model){
        //创建订单号
        String tradeNo = orderFeignClient.getTradeNo();
        model.addAttribute("tradeNo",tradeNo);

        //添加用户地址信息列表
        List<UserAddress> userAddressList = userFeignClient.getAddressList();
        model.addAttribute("userAddressList",userAddressList);

        //订单商品集合
        List<CartInfo> cartInfoList = cartFeignClient.toOrder();
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (CartInfo cartInfo : cartInfoList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getSkuPrice());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
//            orderDetail.setHasStock();
            orderDetailList.add(orderDetail);
        }
        model.addAttribute("detailArrayList",orderDetailList);

        //总数量
        long sum = orderDetailList.stream()
                .collect(Collectors.summarizingInt(OrderDetail::getSkuNum)).getSum();
        model.addAttribute("totalNum",sum);
        //总金额
        BigDecimal totalAmount = new BigDecimal("0");
        for (OrderDetail orderDetail : orderDetailList) {
            totalAmount = totalAmount.add(orderDetail.getOrderPrice().multiply(new BigDecimal(orderDetail.getSkuNum())));
        }
        model.addAttribute("totalAmount",totalAmount);
        return "order/trade";
    }
}
