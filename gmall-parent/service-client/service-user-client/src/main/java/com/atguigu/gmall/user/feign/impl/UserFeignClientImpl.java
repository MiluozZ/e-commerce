package com.atguigu.gmall.user.feign.impl;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.feign.UserFeignClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Miluo
 * @description
 **/
@Component
public class UserFeignClientImpl implements UserFeignClient {
    @Override
    public List<UserAddress> getAddressList() {
        return null;
    }

    @Override
    public UserInfo getUserInfo(String userId) {
        return null;
    }
}
