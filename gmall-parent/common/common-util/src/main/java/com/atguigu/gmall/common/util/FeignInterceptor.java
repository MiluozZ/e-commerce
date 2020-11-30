package com.atguigu.gmall.common.util;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Miluo
 * @description
 **/
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null != servletRequestAttributes){
            HttpServletRequest request = servletRequestAttributes.getRequest();
            if (request != null){
                String userId = request.getHeader("userId");
                if (!StringUtils.isEmpty(userId)){
                    requestTemplate.header("userId",userId);
                }
                String userTempId = request.getHeader("userTempId");
                if (!StringUtils.isEmpty(userTempId)){
                    requestTemplate.header("userTempId",userTempId);
                }
            }

        }

    }
}
