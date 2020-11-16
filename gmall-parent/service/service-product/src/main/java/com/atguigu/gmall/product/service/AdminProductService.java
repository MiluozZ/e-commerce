package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;

import java.util.List;

/**
 * @author Miluo
 * @description
 **/
public interface AdminProductService {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(String id);

    List<BaseCategory3> getCategory3(String id);
}
