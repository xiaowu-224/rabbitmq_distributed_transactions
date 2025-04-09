package com.keyi.data.rabbitmq_distributed_transactions;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.keyi.data.rabbitmq_distributed_transactions.dao")
@ComponentScan(basePackages = "com.keyi.data")
@EnableScheduling
public class RabbitmqDistributedTransactionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqDistributedTransactionsApplication.class, args);
    }

}
