package com.atguigu.gmall.user.feign;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.feign.impl.UserFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author Miluo
 * @description
 **/
@FeignClient(name = "service-user",fallback = UserFeignClientImpl.class)
public interface UserFeignClient {

    @GetMapping("/api/user/auth/addressList")
    List<UserAddress> getAddressList();


    @GetMapping("/auth/userInfo/{userId}")
    UserInfo getUserInfo(@PathVariable String userId);
}
