package com.keyi.data.rabbitmq_distributed_transactions.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyi.data.rabbitmq_distributed_transactions.po.TransMessagePO;
import com.keyi.data.rabbitmq_distributed_transactions.service.TransMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: rabbitmq_distributed_transactions
 * @BelongsPackage: com.keyi.data.rabbitmq_distributed_transactions.sender
 * @Author: wujun
 * @CreateTime: 2025-04-08  21:15
 * @Description: TODO
 * @Version: 1.0
 */
@Component
@Slf4j
public class TransMessageSender {
    private RabbitTemplate rabbitTemplate;


    private TransMessageService transMessageService;

    @Autowired
    public TransMessageSender(RabbitTemplate rabbitTemplate, TransMessageService transMessageService) {
        this.rabbitTemplate = rabbitTemplate;
        this.transMessageService = transMessageService;
    }

    public void send(String exchange, String routingKey, Object payload) {
        log.info("send(): exchange:{},routingKey:{},payload:{} ", exchange, routingKey, payload);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String payloadString = objectMapper.writeValueAsString(payload);
            TransMessagePO transMessagePO = transMessageService.messageSendReady(exchange, routingKey, payloadString);
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setMessageId(transMessagePO.getId());
            messageProperties.setContentType("application/json");
            Message message = new Message(payloadString.getBytes(), messageProperties);
            rabbitTemplate.convertAndSend(exchange, routingKey, message, new CorrelationData(transMessagePO.getId()));
            log.info("send(): success");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}
