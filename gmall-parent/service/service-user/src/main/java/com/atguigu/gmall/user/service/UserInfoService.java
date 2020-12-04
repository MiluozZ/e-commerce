package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;

import java.util.List;

/**
 * @author Miluo
 * @description
 **/
public interface UserInfoService {
    List<UserAddress> getAddressList(String userId);

    UserInfo getUserInfo(String userId);
}
