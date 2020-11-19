package com.atguigu.gmall.item.client.feign.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.feign.ItemFeignClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ItemDegradeFeignClient implements ItemFeignClient {


    @Override
    public Map getItem(Long skuId) {
        return null;
    }
}
