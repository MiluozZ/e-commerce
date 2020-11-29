package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Miluo
 * @description
 **/
@RestController
@Api(tags = "用户管理")
@RequestMapping("/api/user/passport")
public class LoginApiController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private RedisTemplate redisTemplate;

    //用户登陆
    @ApiOperation("用户登陆")
    @PostMapping("/login")
    public Result login(@RequestBody UserInfo userInfo){
        String loginName = userInfo.getLoginName();
        if (null == loginName || loginName.trim() == null){
            return Result.fail().message("账户格式错误");
        }
        String passwd = userInfo.getPasswd();
        if (null == passwd || passwd.trim() == null){
            return Result.fail().message("密码格式错误");
        }
        UserInfo userInfo1 = loginService.login(userInfo);
        if (userInfo1 != null){
            //用户名密码正确 存入redis
            String uuid = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX+uuid,userInfo1);
            Map map = new HashMap();
            map.put("token",uuid);
            map.put("nickName",userInfo1.getNickName());
            return Result.ok(map);
        }else {
            return Result.fail().message("用户名密码错误");
        }
    }

    @ApiOperation("用户退出")
    @GetMapping("/logout")
    public Result logout(HttpServletRequest request){
        loginService.logout(request);
        return Result.ok();
    }
}
