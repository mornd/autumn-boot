package com.mornd.system.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author mornd
 * @dateTime 2021/11/8 - 9:16
 * redis工具类
 */

@Slf4j
@Component
public class RedisUtil {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 存值
     * @param key
     * @param value
     * @return
     */
    public boolean setValue(String key, Object value){
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Throwable e) {
            log.error("向redis存入值时异常：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 取值
     * @param key
     * @return
     */
    public Object getValue(String key){
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 存值并设置过期时间
     * 如果time小于等于0，则设置无期限
     * @param key
     * @param value
     * @param time
     * @param timeUnit
     * @return
     */
    public boolean setValue(String key, Object value, long time, TimeUnit timeUnit){
        try {
            if(time > 0){
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            }else{
                setValue(key, value);
            }
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return  false;
        }
    }

    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    public boolean hasKey(String key){
        try {
            return redisTemplate.hasKey(key);
        } catch (Throwable e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 根据key删除value
     * @param keys
     */
    public void deleteKey(String... keys){
        if(keys != null && keys.length > 0){
            if(keys.length == 1){
                redisTemplate.delete(keys[0]);
            }else{
                for (String key : keys) {
                    redisTemplate.delete(key);
                }
            }
        }
    }

    /**
     * 递增
     * @param key
     * @param factor
     * @return
     */
    public long increment(String key, long factor){
        if(factor < 0){
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, factor);
    }

    /**
     * 递减
     * @param key
     * @param factor
     * @return
     */
    public long decrement(String key, long factor){
        if (factor < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -factor);
    }

}
