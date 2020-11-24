package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.list.dao.ListSearchDao;
import com.atguigu.gmall.list.service.ListSearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Miluo
 * @description
 **/
@Service
public class ListSearchServiceImpl implements ListSearchService {
    @Autowired
    private ListSearchDao listSearchDao;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;


    //上架
    @Override
    public void onSale(Long skuId) {
        Goods goods = new Goods();
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        goods.setId(skuInfo.getId());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setCreateTime(new Date());
        BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
        goods.setCategory1Id(categoryView.getCategory1Id());
        goods.setCategory1Name(categoryView.getCategory1Name());
        goods.setCategory2Id(categoryView.getCategory2Id());
        goods.setCategory2Name(categoryView.getCategory2Name());
        goods.setCategory3Id(categoryView.getCategory3Id());
        goods.setCategory3Name(categoryView.getCategory3Name());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        BaseTrademark tradeMark = productFeignClient.getTradeMark(skuInfo.getTmId());
        goods.setTmId(tradeMark.getId());
        goods.setTmName(tradeMark.getTmName());
        goods.setTmLogoUrl(tradeMark.getLogoUrl());
        List<SearchAttr> searchAttrList = productFeignClient.getSearchAttr(skuId);
        goods.setAttrs(searchAttrList);

        listSearchDao.save(goods);
    }

    //下架
    @Override
    public void cancelSale(Long skuId) {
        listSearchDao.deleteById(skuId);
    }

    //根据查询商品增加热度
    @Override
    public void increaseHotScore(Long skuId) {
        Double score = redisTemplate.opsForZSet().incrementScore("hotScore", skuId, 1);
        if ((score%10) == 0){
            Optional<Goods> byId = listSearchDao.findById(skuId);
            Goods goods = byId.get();
            goods.setHotScore(Math.round(score));
            listSearchDao.save(goods);
        }
    }
}
