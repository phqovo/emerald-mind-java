package com.piheqi.emerald.mind.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author: PiHeQi
 * @Date: 2024/1/8 17:13
 * @Description: RedisUtils
 */
@Component
@Slf4j
public class RedisUtil {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean set(final String key, String value, Long expireTime) {
        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(key, value, expireTime, TimeUnit.SECONDS);
            return true;
        } catch (Exception ex) {
            log.error("设置Redis出错:key：{}，value：{}，expireTime：{}", key, value, expireTime, ex);
        }
        return false;
    }

    public boolean set(final String key, String value) {
        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(key, value);
            return true;
        } catch (Exception ex) {
            log.error("设置Redis出错:key：{}，value：{}", key, value, ex);
        }
        return false;
    }

    public boolean expire(final String key, Long expireTime) {
        try {
            stringRedisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            return true;
        } catch (Exception ex) {
            log.error("设置TTL出错:key：{}，expireTime：{}", key, expireTime, ex);
        }
        return false;
    }

    public void removeBatch(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    public void remove(final String key) {
        if (exists(key)) {
            stringRedisTemplate.delete(key);
        }
    }

    public boolean exists(final String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }


    public String get(final String key) {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        return operations.get(key);
    }


    public void hmSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }


    public boolean isHmKey(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        return hash.hasKey(key, hashKey);
    }

    public Long deleteHmKey(String key, Object... hashKey) {
        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        return hash.delete(key, hashKey);
    }

    public List<Object> getHashValues(String key) {
        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        return hash.values(key);
    }

    public Set<Object> getHashKeys(String key) {
        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        return hash.keys(key);
    }


    public Long generate(String key) {
        RedisAtomicLong counter = new RedisAtomicLong(key, Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()));
        return counter.incrementAndGet();
    }


    public Long generate(String key, Date expireTime) {
        RedisAtomicLong counter = new RedisAtomicLong(key, Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()));
        counter.expireAt(expireTime);
        return counter.incrementAndGet();
    }


    public Long generate(String key, long increment) {
        RedisAtomicLong counter = new RedisAtomicLong(key, Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()));
        return counter.addAndGet(increment);
    }


    public Long generate(String key, long increment, Date expireTime) {
        RedisAtomicLong counter = new RedisAtomicLong(key, Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()));
        counter.expireAt(expireTime);
        return counter.addAndGet(increment);
    }


    public Long generate(String key, long increment, long timeOut) {
        RedisAtomicLong counter = new RedisAtomicLong(key, Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()));
        counter.expire(timeOut, TimeUnit.SECONDS);
        return counter.addAndGet(increment);
    }

    public Long getKeyTTLForSeconds(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

}
