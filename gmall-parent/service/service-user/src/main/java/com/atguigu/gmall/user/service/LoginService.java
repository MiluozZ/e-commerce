package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Miluo
 * @description
 **/
public interface LoginService {
    UserInfo login(UserInfo userInfo);

    void logout(HttpServletRequest request);
}
