package com.mornd.system.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author mornd
 * @dateTime 2022/5/20 - 23:07
 */
@Slf4j
@Service
public class TestServiceImpl {
    @Async("taskExecutor")
    public void exec() {
        log.info(Thread.currentThread().getName());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("exec ok");
    }
}
