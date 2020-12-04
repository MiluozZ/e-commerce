package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartApiService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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


    //更新购物车选中列表
    @ApiOperation("更新购物车选中列表")
    @GetMapping("/checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable Long skuId,@PathVariable Integer isChecked,HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        cartApiService.checkCart(userId,skuId,isChecked);
        return Result.ok();
    }

    //删除购物车商品
    @ApiOperation("删除购物车商品")
    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteCart(@PathVariable Long skuId,HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        cartApiService.deleteCart(userId,skuId);
        return Result.ok();
    }

    //购物车结算
    @ApiOperation("购物车结算")
    @GetMapping("/toOrder")
    public List<CartInfo> toOrder(HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        return cartApiService.toOrder(userId);
    }
}
