package com.example.lcpredictor;

import com.example.lcpredictor.service.LcUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LcPredictorApplicationTests {

    @Autowired
    LcUserService lcUserService;

    @Test
    void contextLoads() {
        System.out.println(lcUserService.count());
    }

}
