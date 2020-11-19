package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.client.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public Map getItem(Long skuId) {
        Map map = new HashMap();

        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());

        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);

        List<SpuSaleAttr> spuSaleAttrListCheckBySkuList = productFeignClient.
                getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());

        Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());

        map.put("skuInfo",skuInfo);
        map.put("categoryView",categoryView);
        map.put("price",skuPrice);
        map.put("spuSaleAttrList",spuSaleAttrListCheckBySkuList);
        map.put("valuesSkuJson", JSON.toJSONString(skuValueIdsMap));

        return map;
    }

}
