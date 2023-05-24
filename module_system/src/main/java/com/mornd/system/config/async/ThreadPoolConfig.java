package com.mornd.system.config.async;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置：
 *  springBoot 项目启动有个默认线程池 ↓
 *      applicationTaskExecutor -> {ThreadPoolTaskExecutor@6893}，你创建的会覆盖掉spring的
 *
 * 四种阻塞队列：
 * 1、SynchronousQueue ------- 直接提交队列（直接就提交，没有队列进行等待）
 * 2、ArrayBlockingQueue -------有界任务队列（不能及时执行的任务，放到自定义的队列中等待执行）
 * 3、LinkedBlockingDeque -----无界任务队列（等待队列的大小是无界(int最大值)，理论上大小取决于内存大小）
 * 4、PriorityBlockingQueue ----优先任务队列（这是一种特殊的无界任务队列，可以按照优先级来执行任务）
 **/
@Configuration
@EnableAsync // 开启多线程异步处理
public class ThreadPoolConfig
{
    /**
     * 核心线程池大小
     */
    private int corePoolSize = 50;

    /**
     * 最大可创建的线程数
     */
    private int maxPoolSize = 200;

    /**
     * 队列最大长度
     */
    private int queueCapacity = 1000;

    /**
     * 线程池维护线程所允许的空闲时间
     */
    private int keepAliveSeconds = 300;

    /**
     * 自定义线程名称
     */
    private String threadNamePrefix = "autumn-threadPool_";

    @Primary
    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        //如果传入值大于0，底层队列使用的是LinkedBlockingQueue，否则默认使用SynchronousQueue
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix(threadNamePrefix);
        /**
         * 线程池对拒绝任务(无线程可用)的处理策略 （策略模式）
         * 1、AbortPolicy-------(默认策略) 让调用者抛出RejectedExecutionException异常
         * 2、CallerRunsPolicy------- 让调用者运行任务(比如主线程提交，就用主线程自己去执行该任务)
         * 3、DiscardPolicy------- 放弃本次任务，直接不处理
         * 4、DiscardOldestPolicy------- 放弃队列中最早的任务，添加本次任务
         */

        /**
         * 一些框架的策略
         * Dubbo的实现：在抛出RejectedExecutionException异常之前会记录日志，
         *              并dump线程栈信息（就类似我们手动执行jps加jstack命令打印指定线程栈信息一样）方便之后我们定位问题；
         * Netty的实现：直接创建一个新线程来执行该任务
         * ActiveMQ的实现：提供超时等待(60s）尝试放入队列，超时之后就放弃本任务
         * PinPoint的实现：它使用了一个拒绝策略链，会逐一尝试策略链中每种拒绝策略
         */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    /**
     * 执行周期性或定时任务
     */
    @Bean(name = "scheduledExecutorService")
    protected ScheduledExecutorService scheduledExecutorService()
    {
        return new ScheduledThreadPoolExecutor(corePoolSize,
                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build(),
                new ThreadPoolExecutor.CallerRunsPolicy())
        {
            @Override
            protected void afterExecute(Runnable r, Throwable t)
            {
                super.afterExecute(r, t);
                Threads.printException(r, t);
            }
        };
    }
}
