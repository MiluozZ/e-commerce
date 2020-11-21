package com.atguigu.gmall.common.cache;

import com.atguigu.gmall.common.constant.RedisConst;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author Miluo
 * @description
 **/
@Aspect
@Component
public class GmallCacheAspect {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Around(value = "@annotation(com.atguigu.gmall.common.cache.GmallCache)")
    public Object GmallAround(ProceedingJoinPoint pjp){
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        GmallCache annotation = signature.getMethod().getAnnotation(GmallCache.class);
        Object[] args = pjp.getArgs();
        String mark = Arrays.asList(args).toString();
        String redisKey = annotation.prefix() + mark + RedisConst.SKUKEY_SUFFIX;
        String lockKey = annotation.prefix() + mark + RedisConst.SKULOCK_SUFFIX;

        //先判断redis是否存在
        Object o = redisTemplate.opsForValue().get(redisKey);
        if (o == null){
            try {
                //对象为空，查询数据库，为了避免缓存击穿，上锁
                Boolean lock = redissonClient.getLock(lockKey).tryLock(1,1, TimeUnit.SECONDS);
                if (lock){
                    //如果抢到锁，查询数据库
                     o = pjp.proceed(args);
                     if (o == null){
                         //如果对象为空，避免缓存穿透，返回空对象
                         o = signature.getReturnType().newInstance();
                         redisTemplate.opsForValue().set(redisKey,o,5,TimeUnit.MINUTES);
                     }else{
                         //不为空，但为了避免缓存雪崩，过期时间随机分布
                         redisTemplate.opsForValue().set(redisKey,o,RedisConst.SKUKEY_TIMEOUT+RedisConst.SECKILL__TIMEOUT,TimeUnit.SECONDS);
                     }
                }else{
                    //如果没抢到锁，等待过后从redis中查询
                    TimeUnit.SECONDS.sleep(1);
                    o = redisTemplate.opsForValue().get(redisKey);
                }
                //保存到redis之后释放锁
                redissonClient.getLock(lockKey).unlock();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }
        return o;
    }
}
