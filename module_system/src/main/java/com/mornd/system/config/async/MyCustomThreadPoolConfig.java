package com.mornd.system.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author mornd
 * @dateTime 2022/5/20 - 23:16
 * 自定义线程池配置
 */
//@Configuration
public class MyCustomThreadPoolConfig {

    @Bean("morndTaskExecutor")
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        //此方法返回可用处理器的虚拟机的最大数量，不小于1
        // 线程数 = 核数 * 期望CPU利用率 * 总时间 (CPU计算时间 + 等待时间) / CPU 计算时间
        int core = Runtime.getRuntime().availableProcessors();
        // 核心线程数
        executor.setCorePoolSize(5);
        // 最大线程数
        executor.setMaxPoolSize(core * 2 + 1);
        // 配置队列大小
        executor.setQueueCapacity(Integer.MAX_VALUE);
        // 设置线程活跃时间
        executor.setKeepAliveSeconds(60);
        // 线程默认名称
        executor.setThreadNamePrefix("autumn-threadPool2");
        // 等待所有任务结束后再关闭线程
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 执行初始化
        executor.initialize();
        return executor;
    }
}
