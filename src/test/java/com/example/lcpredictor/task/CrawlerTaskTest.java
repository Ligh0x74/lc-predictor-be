package com.example.lcpredictor.task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CrawlerTaskTest {

    @Autowired
    CrawlerTask crawlerTask;

    @Test
    void execute() throws InterruptedException {
        crawlerTask.execute("weekly-contest-428");
    }
}
