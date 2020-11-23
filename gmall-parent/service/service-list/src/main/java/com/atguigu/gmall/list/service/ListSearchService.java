package com.atguigu.gmall.list.service;

/**
 * @author Miluo
 * @description
 **/
public interface ListSearchService {
    void onSale(Long skuId);

    void cancelSale(Long skuId);
}
