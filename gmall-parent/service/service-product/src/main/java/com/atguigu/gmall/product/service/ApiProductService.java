package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Miluo
 * @description
 **/
public interface ApiProductService {

    SkuInfo getSkuInfo(Long skuId);

    BaseCategoryView getCategoryById(Long category3Id);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

    List<Map<String, String>> getSkuValueIdsMap(Long spuId);

    BigDecimal getPrice(Long skuId);

    List<BaseCategoryView> getCategory();

    BaseTrademark getTradeMark(Long skuId);

    List<SearchAttr> getSearchAttr(Long skuId);
}
