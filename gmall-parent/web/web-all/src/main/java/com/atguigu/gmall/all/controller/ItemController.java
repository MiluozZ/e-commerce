package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.feign.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller//转发不用@RestController
@RequestMapping
public class ItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;


    @RequestMapping("{skuId}.html")
    public String getItem(@PathVariable Long skuId, Model model){
        // 通过skuId 查询skuInfo
        Map item = itemFeignClient.getItem(skuId);
        model.addAllAttributes(item);
        return "item/index";
    }

}
