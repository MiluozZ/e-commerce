package com.atguigu.gmall.cart.feign.impl;

import com.atguigu.gmall.cart.feign.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.stereotype.Service;

/**
 * @author Miluo
 * @description
 **/
@Service
public class CartFeignClientImpl implements CartFeignClient {
    @Override
    public CartInfo addCart(Long skuId, Integer skuNum) {
        return null;
    }
}
