package com.example.lcpredictor.task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PredictTaskTest {

    @Autowired
    PredictTask predictTask;

    @Test
    void execute() {
        predictTask.execute("weekly-contest-428");
    }
}
