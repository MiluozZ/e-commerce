package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

/**
 * @author Miluo
 * @description
 **/
public interface CartApiService {
    CartInfo addCart(Long skuId, Integer skuNum, String userId);
}
