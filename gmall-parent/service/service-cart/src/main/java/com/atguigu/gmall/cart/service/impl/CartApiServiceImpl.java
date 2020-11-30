package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartApiMapper;
import com.atguigu.gmall.cart.service.CartApiService;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.feign.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public CartInfo addCart(Long skuId, Integer skuNum, String userId) {
        //判断购物车里是否拥有商品
        List<CartInfo> cartInfos = cartApiMapper.selectList(new QueryWrapper<CartInfo>().eq("user_id", userId));
        for (CartInfo cartInfo : cartInfos) {
            if (cartInfo.getSkuId() == skuId) {
                //在购物车中，如包含商品，增加商品数量
                cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);
                //保存到数据库中
                cartApiMapper.updateById(cartInfo);

                //返回添加的商品
                CartInfo cartInfo1 = cartInfo;
                cartInfo1.setSkuNum(skuNum);
                return cartInfo1;
            }
        }
        //如不包含商品，则新增商品
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        cartInfo.setSkuId(skuId);
        cartInfo.setSkuNum(skuNum);
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfo.getSkuName());
        cartInfo.setIsChecked(1);
        cartInfo.setCartPrice(skuInfo.getPrice());
        //保存到数据库中
        cartApiMapper.insert(cartInfo);

        return cartInfo;
    }
}
