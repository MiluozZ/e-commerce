package com.atguigu.gmall.common.cache;

import java.lang.annotation.*;

@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GmallCache {
    public String prefix() default "";
}
