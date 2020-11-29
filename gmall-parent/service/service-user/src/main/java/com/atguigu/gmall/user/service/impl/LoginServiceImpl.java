package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.LoginMapper;
import com.atguigu.gmall.user.service.LoginService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Miluo
 * @description
 **/
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //登陆用户校验
    @Override
    public UserInfo login(UserInfo userInfo) {
        String loginName = userInfo.getLoginName();
        UserInfo userInfo1 = loginMapper.selectOne(new QueryWrapper<UserInfo>().eq("login_name", loginName));
        //判断用户名、密码是否正确
        if (null != userInfo1 &&MD5.encrypt(userInfo.getPasswd()).equals(userInfo1.getPasswd())){
            return userInfo1;
        }
        return null;
    }
}
