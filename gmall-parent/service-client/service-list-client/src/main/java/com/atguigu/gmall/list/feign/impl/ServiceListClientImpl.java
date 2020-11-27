package com.atguigu.gmall.list.feign.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.feign.ServiceListClient;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import org.springframework.stereotype.Component;

/**
 * @author Miluo
 * @description
 **/
@Component
public class ServiceListClientImpl implements ServiceListClient {
    @Override
    public Result createIndex() {
        return null;
    }

    @Override
    public Result onSale(Long skuId) {
        return null;
    }

    @Override
    public Result cancelSale(Long skuId) {
        return null;
    }

    @Override
    public Result increaseHotScore(Long skuId) {
        return null;
    }

    @Override
    public SearchResponseVo search(SearchParam searchParam) {
        return null;
    }
}
