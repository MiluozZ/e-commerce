package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Miluo
 * @description
 **/
public interface ApiProductService {

    SkuInfo getSkuInfo(Long skuId);
}
