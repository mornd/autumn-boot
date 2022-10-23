package com.mornd.system.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;
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

    public RedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }
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
        return redisTemplate.opsForValue().get(key);
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
            redisTemplate.opsForValue().set(key, value, time, timeUnit);
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
    public boolean delete(String keys){
        return redisTemplate.delete(keys);
    }

    /**
     * 删除
     * @param keys
     * @return
     */
    public long delete(Collection keys) {
        return redisTemplate.delete(keys);
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

    /**
     * 模糊匹配key
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
         return redisTemplate.keys(pattern);
    }

    /**
     * 删除匹配的 key
     * @param pattern
     */
    public long deleteKeysPattern(String pattern) {
        Set<String> keys = this.keys(pattern);
        return this.delete(keys);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key      键
     * @param time     时间(秒)
     * @param timeUnit 单位
     */
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, timeUnit);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 获取key的过期时间
     * @param key
     * @return
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 获取key的过期时间，并指定单位
     * @param key redis key
     * @param timeUnit 存储的时间单位
     * @return
     */
    public long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }
}
