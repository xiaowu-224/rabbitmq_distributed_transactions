package com.keyi.data.rabbitmq_distributed_transactions.task;

import com.keyi.data.rabbitmq_distributed_transactions.po.TransMessagePO;
import com.keyi.data.rabbitmq_distributed_transactions.sender.TransMessageSender;
import com.keyi.data.rabbitmq_distributed_transactions.service.TransMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息重发的定时任务
 */
@Configuration
@Component
@Slf4j
public class ResendTask {

    private TransMessageService transMessageService;

    private TransMessageSender transMessageSender;

    private RabbitTemplate rabbitTemplate;

    @Value("${moodymq.resendTimes}")
    Integer resendTimes;

    @Autowired
    public ResendTask(TransMessageService transMessageService, TransMessageSender transMessageSender, RabbitTemplate rabbitTemplate) {
        this.transMessageService = transMessageService;
        this.transMessageSender = transMessageSender;
        this.rabbitTemplate = rabbitTemplate;
    }


    @Scheduled(fixedDelayString = "${moodymq.resendFreq}")
    public void resendMessage() {
        log.info("Resending Message");
        List<TransMessagePO> messagePOS = transMessageService.listReadyMessage();
        log.info("resendMessage():messagePOS:{}", messagePOS);
        messagePOS.stream().forEach(x -> {
            log.info("resendMessage():{}", x);
            if (x.getSequence() > resendTimes) {
                log.error("resend too many times! msgId:{}", x.getId());
                transMessageService.messageDead(x.getId());
            } else {
                MessageProperties messageProperties = new MessageProperties();
                messageProperties.setMessageId(x.getId());
                messageProperties.setContentType("application/json");
                Message message = new Message(x.getPayload().getBytes(), messageProperties);
                rabbitTemplate.convertAndSend(x.getExchange(), x.getRoutingKey(), message, new CorrelationData(x.getId()));
                log.info("send(): success");
                transMessageService.messageResend(x.getId());
            }
        });
    }
}
