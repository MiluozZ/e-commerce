package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.feign.CartFeignClient;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Miluo
 * @description
 **/
@Controller
public class CartController {
    @Autowired
    private CartFeignClient cartFeignClient;

    @GetMapping("/addCart.html")
    public String addCart(@RequestParam(name = "skuId") Long skuId, @RequestParam(name = "skuNum") Integer skuNum, HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);
        CartInfo cartInfo = cartFeignClient.addCart(skuId,skuNum);
        request.setAttribute("cartInfo",cartInfo);
        return "cart/addCart";
    }
}
