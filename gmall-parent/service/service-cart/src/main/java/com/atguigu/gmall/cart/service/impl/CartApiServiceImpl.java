package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartApiMapper;
import com.atguigu.gmall.cart.service.CartApiService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.feign.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Miluo
 * @description
 **/
@Service
public class CartApiServiceImpl implements CartApiService {
    @Autowired
    private CartApiMapper cartApiMapper;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;


    //加入购物车
    @Override
    public CartInfo addCart(Long skuId, Integer skuNum, String userId) {
        //优化:从redis查询是否拥有商品
        CartInfo cartInfo = (CartInfo) redisTemplate.opsForHash().get(RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX, String.valueOf(skuId));
        if (null != cartInfo){
            //在购物车中，如包含商品，增加商品数量
            cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);
            cartApiMapper.updateById(cartInfo);
            //返回添加的商品
            CartInfo cartInfo1 = cartInfo;
            cartInfo1.setSkuNum(skuNum);
            return cartInfo1;
        }

//        //判断购物车里是否拥有商品
//        List<CartInfo> cartInfos = cartApiMapper.selectList(new QueryWrapper<CartInfo>().eq("user_id", userId));
//        for (CartInfo cartInfo : cartInfos) {
//            if (cartInfo.getSkuId() == skuId) {
//                //在购物车中，如包含商品，增加商品数量
//                cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);
//                //保存到数据库中
//                cartApiMapper.updateById(cartInfo);
//
//                //返回添加的商品
//                CartInfo cartInfo1 = cartInfo;
//                cartInfo1.setSkuNum(skuNum);
//                return cartInfo1;
//            }
//        }
        //如不包含商品，则新增商品
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        CartInfo cartInfo2 = new CartInfo();
        cartInfo2.setUserId(userId);
        cartInfo2.setSkuId(skuId);
        cartInfo2.setSkuNum(skuNum);
        cartInfo2.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo2.setSkuName(skuInfo.getSkuName());
        cartInfo2.setIsChecked(1);
        cartInfo2.setCartPrice(skuInfo.getPrice());
        //保存到数据库中
        cartApiMapper.insert(cartInfo2);

        return cartInfo2;
    }


    //购物车列表
    @Override
    public List<CartInfo> cartList(String userId, String userTempId) {
        //判断永久用户是否存在
        if (!StringUtils.isEmpty(userId)){
            //永久用户存在，查询临时用户
            if (!StringUtils.isEmpty(userTempId)){
                //临时用户存在时，对临时、永久用户购物车进行合并
                List<CartInfo> cartListByUserId = cartListById(userId);
                List<CartInfo> cartListByTempId = cartListById(userTempId);
                //将永久用户购物车列表转为以skuid为key的map，方便判断永久用户和临时用户是否购物车是否重合
                Map<Long, CartInfo> userCartInfoMap = cartListByUserId.stream().collect(Collectors.toMap(
                        CartInfo::getSkuId, (cartInfo) -> {
                            return cartInfo;
                        }
                ));

                //将临时用户合并到永久账户中，遍历临时用户
                for (CartInfo tempCartInfo : cartListByTempId) {
                    //判断永久用户和临时用户是否购物车是否重合
                    CartInfo userCartInfo = userCartInfoMap.get(tempCartInfo.getSkuId());
                    if(userCartInfo != null){
                        //重合，修改永久用户重合购物车数量，删除临时用户重合购物车
                        userCartInfo.setSkuNum(userCartInfo.getSkuNum() + tempCartInfo.getSkuNum());
                        cartApiMapper.updateById(userCartInfo);
                        cartApiMapper.deleteById(tempCartInfo);
                    }else{
                        //不重合，修改临时用户的userId，变为永久用户购物车列表
                        CartInfo cartInfo = new CartInfo();
                        cartInfo.setUserId(userId);
                        cartApiMapper.update(cartInfo,new QueryWrapper<CartInfo>().eq("user_id",userTempId));
                    }
                }
                return new ArrayList<>(userCartInfoMap.values());
            }else {
                //临时用户不存在，只显示永久用户购物车
                return cartListById(userId);
            }
        }else {
            //永久用户不存在，只有临时用户，显示临时用户购物车
            return cartListById(userTempId);
        }
    }

    //更新购物车选中列表
    @Override
    public void checkCart(String userId, Long skuId, Integer isChecked) {
        CartInfo cartInfo = new CartInfo();
        cartInfo.setSkuId(skuId);
        cartInfo.setUserId(userId);
        cartInfo.setIsChecked(isChecked);
        cartApiMapper.update(cartInfo,new QueryWrapper<CartInfo>().eq("user_id",userId).eq("sku_id",skuId));
    }

    //    //删除购物车商品
    @Override
    public void deleteCart(String userId, Long skuId) {
        cartApiMapper.delete(new QueryWrapper<CartInfo>().eq("user_id",userId).eq("sku_id",skuId));
    }

    //购物车结算商品
    @Override
    public List<CartInfo> toOrder(String userId) {
        List<CartInfo> cartInfoList = cartApiMapper.selectList(new QueryWrapper<CartInfo>().eq("user_id", userId).eq("is_Checked", 1));
        for (CartInfo cartInfo : cartInfoList) {
            BigDecimal skuPrice = productFeignClient.getSkuPrice(cartInfo.getSkuId());
            cartInfo.setSkuPrice(skuPrice);
        }
        return cartInfoList;
    }


    //临时、永久用户只存其一时的购物车列表
    private List<CartInfo> cartListById(String id) {
        //优化：从redis当中查询
        List<CartInfo> cartInfoList = redisTemplate.opsForHash().values(RedisConst.USER_KEY_PREFIX + id + RedisConst.USER_CART_KEY_SUFFIX);
//        List<CartInfo> cartInfoList = cartApiMapper.selectList(new QueryWrapper<CartInfo>().eq("user_id", id));

        for (CartInfo cartInfo : cartInfoList) {
            cartInfo.setSkuPrice(productFeignClient.getSkuPrice(cartInfo.getSkuId()));
        }
        return cartInfoList;
    }
}
