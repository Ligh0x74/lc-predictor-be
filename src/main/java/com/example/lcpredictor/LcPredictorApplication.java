package com.example.lcpredictor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.example.lcpredictor.mapper")
@SpringBootApplication
@EnableScheduling
public class LcPredictorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LcPredictorApplication.class, args);
    }

}
