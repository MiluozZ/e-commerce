package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.list.dao.ListSearchDao;
import com.atguigu.gmall.list.service.ListSearchService;
import com.atguigu.gmall.model.list.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Miluo
 * @description
 **/
@Service
public class ListSearchServiceImpl implements ListSearchService {
    @Autowired
    private ListSearchDao listSearchDao;


    @Override
    public void onSale(Long skuId) {
        Goods goods = new Goods();
        goods.setId(123L);
        goods.setCreateTime(new Date());
        goods.setPrice(123d);

        listSearchDao.save(goods);
    }

    @Override
    public void cancelSale(Long skuId) {

    }
}
