package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.common.util.HttpClientUtil;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.enums.PaymentWay;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.CartMapper;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderServiceMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.product.client.feign.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author Miluo
 * @description
 **/
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private OrderServiceMapper orderServiceMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private CartMapper cartMapper;
    @Value("${ware.url}")
    private String wareUrl;

    ////判断是否有货
    @Override
    public Boolean hasStock(Long skuId, Integer skuNum) {
        //http://www.gware.com/hasStock?skuId=10221&num=2
        return "1".equals(HttpClientUtil.doGet(wareUrl + "/hasStock?skuId=" + skuId + "&num=" + skuNum));
    }

    //保存订单详细信息
    @Override
    public Long saveOrderInfo(OrderInfo orderInfo) {
        //订单状态
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        //订单交易编号  （支付宝之间 交易唯一主键）
        String outTradeNo = "ATGUIGU" + System.currentTimeMillis() + "" +
                new Random().nextInt(1000);
        orderInfo.setOutTradeNo(outTradeNo);

        //订单创建、结束时间
        Date createTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,2);
        Date expireTime = calendar.getTime();
        orderInfo.setCreateTime(createTime);
        orderInfo.setExpireTime(expireTime);
        orderInfo.setPaymentWay(PaymentWay.ONLINE.name());

        //订单内容
        StringBuilder sb = new StringBuilder();

        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            sb.append(orderDetail.getSkuName() + " ");
            //真实价格
            BigDecimal price = productFeignClient.getSkuPrice(orderDetail.getSkuId());
            orderDetail.setOrderPrice(price);
        }
        if(sb.length() > 100){
            orderInfo.setTradeBody(sb.toString().substring(0,100));
        }else{
            orderInfo.setTradeBody(sb.toString());
        }
        orderInfo.sumTotalAmount();
        orderServiceMapper.insert(orderInfo);

        QueryWrapper<CartInfo> wrapper = new QueryWrapper<CartInfo>();
        //保存订单商品
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insert(orderDetail);
            wrapper.eq("sku_id",orderDetail.getSkuId())
                    .eq("user_id",orderInfo.getUserId());
            wrapper.or();
        }
        //保存订单后删除购物车商品
        cartMapper.delete(wrapper);

        //TODO 通过MQ延迟删除订单 （两小时未支付）


        return orderInfo.getId();
    }

    @Override
    public OrderInfo getOrderInfoById(Long orderId) {
        return orderServiceMapper.selectById(orderId);
    }

    @Override
    public void updateInfoAfterPay(Long orderId) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setOrderStatus(PaymentStatus.PAID.name());
        orderInfo.setProcessStatus(PaymentStatus.PAID.name());
        orderServiceMapper.updateById(orderInfo);
        System.out.println("订单已修改");
    }
}
