package com.example.canatest.config;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.atguigu.gmall.model.cart.CartInfo;
import com.xpand.starter.canal.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;

/**
 * @author chen.qian
 * @date 2018/3/19
 */
@CanalEventListener
public class MyEventListener {
//
//    @InsertListenPoint
//    public void onEvent(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
//        rowData.getAfterColumnsList().forEach((c) -> System.err.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
//    }
//
//    @UpdateListenPoint
//    public void onEvent1(CanalEntry.RowData rowData) {
//        System.err.println("UpdateListenPoint");
//        rowData.getAfterColumnsList().forEach((c) -> System.err.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
//    }
//
    @DeleteListenPoint
    public void onEvent3(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.out.println("有数据被删除了");
        CartInfo cartInfo = new CartInfo();
        StringBuilder sb = new StringBuilder();
        rowData.getBeforeColumnsList().forEach((c) -> System.err.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
        rowData.getBeforeColumnsList().forEach((c) -> {sb.append(c.getValue()).append("::");
        });
        String[] attributes = sb.toString().split("::");
        cartInfo.setId(Long.parseLong(attributes[0]));
        cartInfo.setUserId(attributes[1]);
        cartInfo.setSkuId(Long.parseLong(attributes[2]));
        cartInfo.setCartPrice(new BigDecimal(attributes[3]));
        cartInfo.setSkuNum(Integer.parseInt(attributes[4]));
        cartInfo.setImgUrl(attributes[5]);
        cartInfo.setSkuName(attributes[6]);
        cartInfo.setIsChecked(Integer.parseInt(attributes[7]));
        System.out.println("eventType:"+eventType);
        System.out.println(cartInfo.toString());
        redisTemplate.opsForHash().delete(RedisConst.USER_KEY_PREFIX + cartInfo.getUserId() + RedisConst.USER_CART_KEY_SUFFIX,cartInfo.getSkuId().toString());
    }


    @Autowired
    private RedisTemplate redisTemplate;

    @ListenPoint(destination = "example", schema = "gmall_order", table = {"cart_info"}, eventType = {CanalEntry.EventType.UPDATE,CanalEntry.EventType.INSERT})
    public void onEvent4(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {

        CartInfo cartInfo = new CartInfo();
        StringBuilder sb = new StringBuilder();
        rowData.getAfterColumnsList().forEach((c) -> System.err.println("By--Annotation: " + c.getName() + " ::   " + c.getValue()));
        rowData.getAfterColumnsList().forEach((c) -> {sb.append(c.getValue()).append("::");
        });
        System.out.println(sb);
        String[] attributes = sb.toString().split("::");
        cartInfo.setId(Long.parseLong(attributes[0]));
        cartInfo.setUserId(attributes[1]);
        cartInfo.setSkuId(Long.parseLong(attributes[2]));
        cartInfo.setCartPrice(new BigDecimal(attributes[3]));
        cartInfo.setSkuNum(Integer.parseInt(attributes[4]));
        cartInfo.setImgUrl(attributes[5]);
        cartInfo.setSkuName(attributes[6]);
        cartInfo.setIsChecked(Integer.parseInt(attributes[7]));
        System.out.println("eventType:"+eventType);
        System.out.println(cartInfo.toString());

        if (eventType == CanalEntry.EventType.INSERT || eventType == CanalEntry.EventType.UPDATE){
            redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX + cartInfo.getUserId() + RedisConst.USER_CART_KEY_SUFFIX,String.valueOf(cartInfo.getSkuId()),cartInfo);
        }

    }
}
