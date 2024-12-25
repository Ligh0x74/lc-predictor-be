package com.example.lcpredictor;

import com.example.lcpredictor.service.LcUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@SpringBootTest
class LcPredictorApplicationTests {

    @Autowired
    private LcUserService lcUserService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void mysqlTest() {
        System.out.println(lcUserService.count());
    }

    @Test
    void redisTest() {
        redisTemplate.opsForValue().set("test", "Hello World.", Duration.ofMinutes(1));
        System.out.println(redisTemplate.opsForValue().get("test"));
    }
}
