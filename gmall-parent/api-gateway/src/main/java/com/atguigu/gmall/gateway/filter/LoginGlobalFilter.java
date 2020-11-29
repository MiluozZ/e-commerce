package com.atguigu.gmall.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Miluo
 * @description
 **/
@Component
public class LoginGlobalFilter implements GlobalFilter, Ordered {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Value("${auth.url}")
    private String[] authUrl;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        //获取路径
        String path = request.getURI().getPath();

        //判断路径条件
        //需要登陆
        if(antPathMatcher.match("/**/auth/**",path)){

        }
        //内部网不允许访问
        if (antPathMatcher.match("/**/inner/**",path)){

        }

        for (String url : authUrl) {
            if (url.equals(path)){

            }
        }


        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
