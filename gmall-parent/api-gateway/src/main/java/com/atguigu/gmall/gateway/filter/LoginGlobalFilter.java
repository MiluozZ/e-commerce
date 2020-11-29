package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.gateway.constant.RedisConst;
import com.atguigu.gmall.model.user.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Miluo
 * @description
 **/
@Component
public class LoginGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisTemplate redisTemplate;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Value("${auth.url}")
    private String[] authUrl;
    @Value("${login.url}")
    private String loginUrl;



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        //获取路径
        String path = request.getURI().getPath();
        //获取路径及参数
        String specificPart = request.getURI().getRawSchemeSpecificPart();

        //获取用户Id判断是否登陆
        String userId = getToken(request);

        //判断路径条件
        //异步请求：需要登陆
        if(antPathMatcher.match("/**/auth/**",path)){
            //需要用户登陆状态，判断用户是否登陆
            if (userId == null){
                //用户未登录
                return out(response,ResultCodeEnum.LOGIN_AUTH);
            }

        }
        //内部网不允许访问
        if (antPathMatcher.match("/**/inner/**",path)){
            //内部网址访问，返回禁止访问code
            return out(response,ResultCodeEnum.PERMISSION);
        }

        //同步请求：需要登陆
        for (String url : authUrl) {
            if (url.equals(path)){
                if (userId == null){
                    //用户未登录，跳转到登陆页面，并携带当前页面地址，
                    try {
                        response.getHeaders().add(HttpHeaders.LOCATION,loginUrl + URLEncoder.encode(request.getURI().getRawSchemeSpecificPart(),"utf-8") );
                        response.setStatusCode(HttpStatus.SEE_OTHER);
                        return response.setComplete();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //购物车页面
        //已经登录了  userId 不为 null
        if(null != userId){
            //传递用户ID给后面需要转发的微服务
            request.mutate().header("userId",userId);
        }
        //未登录使用临时用户
        String userTempId = getUserTempId(request);
        if(null != userTempId){
            //传递用户ID给后面需要转发的微服务 使用请求头
            request.mutate().header("userTempId",userTempId);
        }


        return chain.filter(exchange);
    }

    //获取用户的临时ID
    private String getUserTempId(ServerHttpRequest request){
        //1：获取请求头
        String userTempId = request.getHeaders().getFirst("userTempId");
        if(null == userTempId) {
            //2：没有 再获取Cookie
            HttpCookie cookie = request.getCookies().getFirst("userTempId");
            if (null != cookie) {
                userTempId = cookie.getValue();
            }
        }
        return userTempId;
    }

    //不允许访问时候的响应结果
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        Result<Object> result = Result.build(null,resultCodeEnum);
        String json = JSONObject.toJSONString(result);
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        DataBuffer dataBuffer = dataBufferFactory.wrap(json.getBytes());
        //响应编码
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE,"application/json;charset=utf-8");
        return response.writeWith(Mono.just(dataBuffer));
    }

    private String getToken(ServerHttpRequest request) {
        //因为页面是同步异步混合请求，token需要分别查询
        //当时异步请求时，需要在请求头中获取
        String headToken = request.getHeaders().getFirst("token");
        if (headToken == null){
            //是同步请求，在Cookie中获取
            HttpCookie cookieToken = request.getCookies().getFirst("token");
            if(cookieToken != null){
                String value = cookieToken.getValue();
                UserInfo userInfo = (UserInfo) redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX + value);
                if (userInfo != null){
                    return userInfo.getId().toString();
                }
            }
        }else{
            //是异步请求，已经获取到了token
            UserInfo userInfo = (UserInfo) redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX + headToken);
            if (userInfo != null){
                return userInfo.getId().toString();
            }
        }
        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
