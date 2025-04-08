package com.keyi.data.rabbitmq_distributed_transactions;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@MapperScan("")
public class RabbitmqDistributedTransactionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqDistributedTransactionsApplication.class, args);
    }

}
