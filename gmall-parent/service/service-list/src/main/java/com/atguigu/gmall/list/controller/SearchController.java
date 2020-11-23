package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.ListSearchService;
import com.atguigu.gmall.model.list.Goods;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Miluo
 * @description
 **/
@RestController
@Api(tags = "ES索引管理")
@RequestMapping("/api/list")
public class SearchController {
    @Autowired
    private ListSearchService listSearchService;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @GetMapping("/createIndex")
    @ApiOperation("创建库")
    public Result createIndex(){
        //创建库
        elasticsearchRestTemplate.createIndex(Goods.class);
        //建立映射
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    //上架存入ES
    @GetMapping("/onSale/{skuId}")
    @ApiOperation("商品上架")
    public Result onSale(@PathVariable(name = "skuId") Long skuId){
        listSearchService.onSale(skuId);
        return Result.ok();
    }

    //下架删除ES
    @GetMapping("/cancelSale/{skuId}")
    @ApiOperation("商品下架")
    public Result cancelSale(@PathVariable(name = "skuId") Long skuId){
        listSearchService.cancelSale(skuId);
        return Result.ok();
    }

}
