package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.constans.MQConst;
import com.atguigu.gmall.list.feign.ServiceListClient;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.AdminProductService;
import com.atguigu.gmall.util.RabbitTool;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private ServiceListClient serviceListClient;
    @Autowired
    private RabbitTool rabbitTool;



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


    //添加spu
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //添加info表
        spuInfoMapper.insert(spuInfo);

        //添加image表
        spuInfo.getSpuImageList().forEach(spuImage -> {
            spuImage.setSpuId(spuInfo.getId());
            spuImageMapper.insert(spuImage);
        });

        //添加attrlist表
        spuInfo.getSpuSaleAttrList().forEach(spuSaleAttr -> {
            spuSaleAttr.setSpuId(spuInfo.getId());
            spuSaleAttrMapper.insert(spuSaleAttr);

            //添加attrValuelist表
            spuSaleAttr.getSpuSaleAttrValueList().forEach(spuSaleAttrValue -> {
                spuSaleAttrValue.setSpuId(spuInfo.getId());
                spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            });
        });

    }

    //根据spuId获取图片列表
    @Override
    public List<SpuImage> getSpuImageListBySpuId(long spuId) {
        return spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id",spuId));
    }

    //根据spuId获取销售属性
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(long spuId) {

        return spuSaleAttrMapper.getSpuSaleAttrList(spuId);
    }

    //添加sku
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //添加skuInfo表
        skuInfoMapper.insert(skuInfo);

        //添加skuImage表
        skuInfo.getSkuImageList().forEach(skuImage -> {
            skuImage.setSkuId(skuInfo.getId());
            skuImageMapper.insert(skuImage);
        });

        //添加skuAttrValue表
        skuInfo.getSkuAttrValueList().forEach(skuAttrValue -> {
            skuAttrValue.setSkuId(skuInfo.getId());
            skuAttrValueMapper.insert(skuAttrValue);
        });

        //添加skuSaleAttrValue表
        skuInfo.getSkuSaleAttrValueList().forEach(skuSaleAttrValue -> {
            skuSaleAttrValue.setSkuId(skuInfo.getId());
            skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        });
    }

    //获取sku分页列表
    @Override
    public IPage<SkuInfo> getSkuPagesList(int page, int limit) {
        return skuInfoMapper.selectPage(new Page<>(page,limit),null);
    }

    //商品上架
    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
        //上架同时将信息传入ES索引库
        rabbitTool.sendMessage(MQConst.EXCHANGE_DIRECT_GOODS,MQConst.ROUTING_GOODS_UPPER,skuId);

    }

    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
        //下架同时将信息从ES索引库删除
        rabbitTool.sendMessage(MQConst.EXCHANGE_DIRECT_GOODS,MQConst.ROUTING_GOODS_LOWER,skuId);

    }
}
