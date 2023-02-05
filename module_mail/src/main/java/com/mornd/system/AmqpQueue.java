package com.mornd.system;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: mornd
 * @dateTime: 2023/2/1 - 20:03
 */

@Configuration
public class AmqpQueue {

    @Bean
    public Queue mailQueue() {
        return new Queue(MailConstants.QUEUE_NAME);
    }
}
