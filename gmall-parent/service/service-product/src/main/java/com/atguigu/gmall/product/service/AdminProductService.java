package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author Miluo
 * @description
 **/
public interface AdminProductService {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(String id);

    List<BaseCategory3> getCategory3(String id);

    List<BaseAttrInfo> getAttrInfoById(String category1Id,String category2Id,String category3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(long id);

    IPage<BaseTrademark> getTrademarkList(int page, int limit);
}
