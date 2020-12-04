package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.cart.feign.impl.CartFeignClientImpl;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author Miluo
 * @description
 **/
@FeignClient(name = "service-cart",fallback = CartFeignClientImpl.class)
public interface CartFeignClient {
    @GetMapping("/api/cart/addCart/{skuId}/{skuNum}")
    CartInfo addCart(@PathVariable(value = "skuId") Long skuId, @PathVariable(value = "skuNum")Integer skuNum);


    @GetMapping("/api/cart/toOrder")
    List<CartInfo> toOrder();
}
