package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Miluo
 * @description
 **/
@RestController
@Api(tags = "用户信息管理")
@RequestMapping("/api/user")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation("获取用户地址信息")
    @GetMapping("/auth/addressList")
    public List<UserAddress> getAddressList(HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        return userInfoService.getAddressList(userId);
    }

    @ApiOperation("获取用户详细信息")
    @GetMapping("/auth/userInfo/{userId}")
    public UserInfo getUserInfo(@PathVariable String userId){
        return userInfoService.getUserInfo(userId);
    }
}
