package com.urim.cloudpicturebackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedisConnection() {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();

            // 写入 Redis
            ops.set("test:key", "Hello Redis!");

            // 读取 Redis
            Object value = ops.get("test:key");

            System.out.println("✅ Redis 读取结果: " + value);
        } catch (Exception e) {
            System.err.println("❌ Redis 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
