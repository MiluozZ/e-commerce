package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartApiService;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Miluo
 * @description
 **/
@RestController
@RequestMapping("/api/cart")
@Api(tags = "购物车管理")
public class CartApiController {
    @Autowired
    private CartApiService cartApiService;

    @GetMapping("/addCart/{skuId}/{skuNum}")
    public CartInfo addCart(@PathVariable(value = "skuId") Long skuId, @PathVariable(value = "skuNum")Integer skuNum, HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)){
            userId = AuthContextHolder.getUserTempId(request);
        }
        CartInfo cartInfo = cartApiService.addCart(skuId,skuNum,userId);

        return cartInfo;
    }
}
