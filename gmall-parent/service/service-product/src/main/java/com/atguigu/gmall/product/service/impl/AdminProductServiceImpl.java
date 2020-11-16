package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.AdminProductService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Miluo
 * @description 商品业务实现类
 **/
@Service
public class AdminProductServiceImpl implements AdminProductService {
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    @Override
    public List<BaseCategory2> getCategory2(String id) {
        QueryWrapper<BaseCategory2> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category1_id",id);
        return baseCategory2Mapper.selectList(queryWrapper);
    }

    @Override
    public List<BaseCategory3> getCategory3(String id) {
        QueryWrapper<BaseCategory3> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category2_id",id);
        return baseCategory3Mapper.selectList(queryWrapper);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoById(String category1Id,String category2Id,String category3Id) {

        return baseAttrInfoMapper.getAttrInfoById(category1Id,category2Id,category3Id);
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        baseAttrInfoMapper.insert(baseAttrInfo);
        baseAttrInfo.getAttrValueList().forEach((baseAttrValue) ->{
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.insert(baseAttrValue);
        });
    }

    //根据Id获取平台属性
    @Override
    public List<BaseAttrValue> getAttrValueList(long id) {
        QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_id",id);
        return baseAttrValueMapper.selectList(queryWrapper);

    }

    //获取品牌列表
    @Override
    public List<BaseTrademark> getTrademarkList2() {
        return baseTrademarkMapper.selectList(null);
    }

    //获取品牌分页列表
    @Override
    public IPage<BaseTrademark> getTrademarkList(int page, int limit) {
        return baseTrademarkMapper.selectPage(new Page<>(page,limit),null);
    }

    //获取销售属性
    @Override
    public List<BaseSaleAttr> getSaleAttr() {
        return baseSaleAttrMapper.selectList(null);
    }

    @Override
    public IPage<SpuInfo> getSpuPagesList(int page, int limit,int id) {
        QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id",id);
        return spuInfoMapper.selectPage(new Page<>(page,limit),queryWrapper);
    }
}
