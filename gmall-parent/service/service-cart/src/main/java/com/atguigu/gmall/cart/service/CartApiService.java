package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;

/**
 * @author Miluo
 * @description
 **/
public interface CartApiService {
    CartInfo addCart(Long skuId, Integer skuNum, String userId);

    List<CartInfo> cartList(String userId, String userTempId);

    void checkCart(String userId,Long skuId, Integer isChecked);
}
