package com.atguigu.gmall.list.service;

import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;

/**
 * @author Miluo
 * @description
 **/
public interface ListSearchService {
    void onSale(Long skuId);

    void cancelSale(Long skuId);

    void increaseHotScore(Long skuId);

    SearchResponseVo search(SearchParam searchParam);
}
