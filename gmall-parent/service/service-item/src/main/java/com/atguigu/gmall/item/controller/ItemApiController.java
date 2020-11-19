package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Api(tags = "接口调用集合")
@RequestMapping("/api/item")
public class ItemApiController {


    @Autowired
    private ItemService itemService;

    //查询汇总数据
    @GetMapping("/{skuId}")
    public Map getItem(@PathVariable(name = "skuId") Long skuId){
        return itemService.getItem(skuId);
    }

}
