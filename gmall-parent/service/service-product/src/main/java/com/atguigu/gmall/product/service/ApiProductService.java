package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import org.springframework.beans.factory.annotation.Autowired;

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
}
