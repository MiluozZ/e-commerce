package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ApiProductService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Miluo
 * @description
 **/
@Service
public class ApiProductServiceImpl implements ApiProductService {
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
    private BaseCategoryViewMapper baseCategoryViewMapper;
    @Autowired
    private SkuValueIdsMapper skuValueIdsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;


    //根据skuID查询sku信息
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        String redisKey= RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        String lockKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
        SkuInfo skuInfo = (SkuInfo) redisTemplate.opsForValue().get(redisKey);

        //redis不存在，则查询数据库保存到redis中
        if (skuInfo == null){
            //避免缓存击穿，redis上分布式锁
            try {
                boolean tryLock = redissonClient.getLock(lockKey).tryLock(1, 1, TimeUnit.SECONDS);
                if (tryLock){
                    //查询数据库
                    skuInfo = skuInfoMapper.selectById(skuId);

                    //判断数据库对象如果为空，避免缓存穿透，返回空对象
                    if (skuInfo == null){
                        skuInfo = new SkuInfo();
                        redisTemplate.opsForValue().set(redisKey,skuInfo,5,TimeUnit.MINUTES);
                    }else {
                        skuInfo.setSkuImageList(skuImageMapper.selectList(new QueryWrapper<SkuImage>().eq("sku_id",skuId)));

                        //保存到redis中,避免缓存雪崩，过期时间随机设置
                        redisTemplate.opsForValue().set(redisKey,skuInfo,RedisConst.SKUKEY_TIMEOUT + new Random().nextInt(RedisConst.SECKILL__TIMEOUT),TimeUnit.SECONDS);

                    }

                }else {
                    //已经上锁则查询redis缓存
                    TimeUnit.MINUTES.sleep(1);
                    skuInfo = (SkuInfo) redisTemplate.opsForValue().get(redisKey);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            redissonClient.getLock(lockKey).unlock();
        }

        return skuInfo;
    }



    //根据skuId查询分类信息
    @Override
    public BaseCategoryView getCategoryById(Long category3Id) {
        String redisKey = RedisConst.CATEGORY_PREFIX + category3Id + RedisConst.SKUKEY_SUFFIX;
        String lockKey = RedisConst.CATEGORY_PREFIX + category3Id + RedisConst.SKULOCK_SUFFIX;
        BaseCategoryView categoryView = (BaseCategoryView) redisTemplate.opsForValue().get(redisKey);

        if (categoryView == null){
            try {
                boolean tryLock = redissonClient.getLock(lockKey).tryLock(1, 3, TimeUnit.SECONDS);
                if (tryLock){
                    categoryView = baseCategoryViewMapper.selectById((category3Id));
                    if (categoryView == null){
                        categoryView = new BaseCategoryView();
                        redisTemplate.opsForValue().set(redisKey,categoryView,5,TimeUnit.MINUTES);
                    }else{
                        redisTemplate.opsForValue().set(redisKey,categoryView,RedisConst.SKUKEY_TIMEOUT + new Random().nextInt(RedisConst.SECKILL__TIMEOUT),TimeUnit.SECONDS);
                    }
                }else{
                    TimeUnit.SECONDS.sleep(1);
                    categoryView = (BaseCategoryView) redisTemplate.opsForValue().get(redisKey);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            redissonClient.getLock(lockKey).unlock();
        }
        return categoryView;
    }



    //根据skuId获取spu销售属性
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        String redisKey = RedisConst.SALE_PREFIX + spuId + RedisConst.SKUKEY_SUFFIX;
        String lockKey = RedisConst.SALE_PREFIX + spuId + RedisConst.SKULOCK_SUFFIX;
        List<SpuSaleAttr> saleAttrs = (List<SpuSaleAttr>) redisTemplate.opsForValue().get(lockKey);

        if (saleAttrs == null){
            try {
                boolean tryLock = redissonClient.getLock(lockKey).tryLock(1, 3, TimeUnit.SECONDS);
                if (tryLock){
                    saleAttrs = spuSaleAttrMapper.getSpuSaleAttrListCheckBySku(skuId,spuId);
                    if (saleAttrs == null){
                        saleAttrs = new ArrayList<>();
                        redisTemplate.opsForValue().set(redisKey,saleAttrs,5,TimeUnit.MINUTES);
                    }else{
                        redisTemplate.opsForValue().set(redisKey,saleAttrs,RedisConst.SKUKEY_TIMEOUT + new Random().nextInt(RedisConst.SECKILL__TIMEOUT),TimeUnit.SECONDS);
                    }
                }else {
                    TimeUnit.SECONDS.sleep(1);
                    saleAttrs = (List<SpuSaleAttr>) redisTemplate.opsForValue().get(redisKey);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            redissonClient.getLock(lockKey).unlock();
        }
        return saleAttrs;
    }

    //根据spuId获取对象映射
    @Override
    public List<Map<String, String>> getSkuValueIdsMap(Long spuId) {
        String redisKey = RedisConst.POJO_PREFIX + spuId + RedisConst.SKUKEY_SUFFIX;
        String lockKey = RedisConst.POJO_PREFIX + spuId + RedisConst.SKULOCK_SUFFIX;
        List<Map<String, String>> mapList = (List<Map<String, String>>) redisTemplate.opsForValue().get(lockKey);
        if (mapList == null){
            try {
                boolean tryLock = redissonClient.getLock(lockKey).tryLock(1, 3, TimeUnit.SECONDS);
                if (tryLock){
                    mapList = skuValueIdsMapper.getSkuValueIdsMap(spuId);
                    if (mapList == null){
                        mapList = new ArrayList<>();
                        redisTemplate.opsForValue().set(redisKey,mapList,5,TimeUnit.MINUTES);
                    }else {
                        redisTemplate.opsForValue().set(redisKey,mapList,RedisConst.SKUKEY_TIMEOUT + new Random().nextInt(RedisConst.SECKILL__TIMEOUT),TimeUnit.SECONDS);
                    }
                }else {
                    TimeUnit.SECONDS.sleep(1);
                    mapList = (List<Map<String, String>>) redisTemplate.opsForValue().get(redisKey);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            redissonClient.getLock(lockKey).unlock();
        }

        return mapList;
    }

    //根据spuId获取价格，价格经常变动，每次从数据库查询,不添加缓存
    @Override
    public BigDecimal getPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (skuId != null){
            return skuInfo.getPrice();
        }
        return null;
    }
}
