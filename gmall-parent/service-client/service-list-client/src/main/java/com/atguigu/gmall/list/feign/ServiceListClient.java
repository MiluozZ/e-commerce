package com.atguigu.gmall.list.feign;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Miluo
 * @description
 **/
@FeignClient(name = "service-list")
public interface ServiceListClient {
    @GetMapping("/api/list/createIndex")
    Result createIndex();

    //上架存入ES
    @GetMapping("/api/list/onSale/{skuId}")
    Result onSale(@PathVariable(name = "skuId") Long skuId);

    //下架删除ES
    @GetMapping("/api/list/cancelSale/{skuId}")
    Result cancelSale(@PathVariable(name = "skuId") Long skuId);
}
