package com.atguigu.gmall.user.mapper;

import com.atguigu.gmall.model.user.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

/**
 * @author Miluo
 * @description
 **/
@Component
public interface LoginMapper extends BaseMapper<UserInfo> {
}
