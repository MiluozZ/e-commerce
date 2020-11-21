package com.atguigu.gmall.common.threadpool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Miluo
 * @description
 **/
@Configuration
public class ThreadPool {

    //创建线程池，Alibaba禁止使用Executors创建线程池
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){

        return new ThreadPoolExecutor(5, 10,30 , TimeUnit.SECONDS, new ArrayBlockingQueue(10));
    }
}
