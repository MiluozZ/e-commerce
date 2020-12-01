package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartApiService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    //购物车列表
    @ApiOperation("购物车列表")
    @GetMapping("/cartList")
    public Result cartList(HttpServletRequest request){
        //永久用户
        String userId = AuthContextHolder.getUserId(request);
        //临时用户
        String userTempId = AuthContextHolder.getUserTempId(request);
        List<CartInfo> data = cartApiService.cartList(userId,userTempId);
        return Result.ok(data);
    }
}
