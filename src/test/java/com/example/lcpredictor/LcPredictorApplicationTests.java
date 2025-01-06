package com.example.lcpredictor;

import com.example.lcpredictor.service.LcUserService;
import com.example.lcpredictor.task.PredictTask;
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

    @Autowired
    PredictTask predictTask;

    @Test
    void mysqlTest() {
        System.out.println(lcUserService.count());
    }

    @Test
    void redisTest() {
        redisTemplate.opsForValue().set("test", "Hello World.", Duration.ofMinutes(1));
        System.out.println(redisTemplate.opsForValue().get("test"));
    }

    @Test
    void predictTest() {
        predictTask.execute("weekly-contest-430");
    }
}
