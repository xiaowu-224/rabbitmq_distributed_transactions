package com.keyi.data.rabbitmq_distributed_transactions.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @BelongsProject: rabbitmq_distributed_transactions
 * @BelongsPackage: com.keyi.data.rabbitmq_distributed_transactions.config
 * @Author: wujun
 * @CreateTime: 2025-04-09  19:07
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration
public class DlxConfig {
    @Bean
    public TopicExchange dlxExchange() {
        return new TopicExchange("exchange.dlx", true, false);
    }

    @Bean
    public Queue dlxQueue() {
        return new Queue("queue.dlx", true, false, false);
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with("#");
    }

}
