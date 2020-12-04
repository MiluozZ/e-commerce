package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Miluo
 * @description
 **/
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserAddressMapper userAddressMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    //查询用户地址列表
    @Override
    public List<UserAddress> getAddressList(String userId) {
        return userAddressMapper.selectList(new QueryWrapper<UserAddress>().eq("user_id",userId));
    }

    @Override
    public UserInfo getUserInfo(String userId) {
        return userInfoMapper.selectOne(new QueryWrapper<UserInfo>().eq("id",userId));
    }
}
