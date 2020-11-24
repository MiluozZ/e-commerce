package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.list.SearchAttr;

import java.util.List;

/**
 * @author Miluo
 * @description
 **/
public interface SearchAttrMapper {
    List<SearchAttr> getSearchAttr(Long skuId);
}
