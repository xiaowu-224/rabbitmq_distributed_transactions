package com.keyi.data.rabbitmq_distributed_transactions.listener;

import com.keyi.data.rabbitmq_distributed_transactions.service.TransMessageService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: rabbitmq_distributed_transactions
 * @BelongsPackage: com.keyi.data.rabbitmq_distributed_transactions.listener
 * @Author: wujun
 * @CreateTime: 2025-04-09  19:12
 * @Description: TODO
 * @Version: 1.0
 */
@Component
@Slf4j
@ConditionalOnProperty("moodymq.dlxEnabled")
public class DlxListener  implements ChannelAwareMessageListener {

    @Autowired
    private TransMessageService transMessageService;


    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        log.error("dead letter! message:{}", msg);
        //TODO 业务警告

        MessageProperties messageProperties = message.getMessageProperties();
         transMessageService.messageDead(messageProperties.getMessageId(),messageProperties.getReceivedExchange(),messageProperties.getReceivedRoutingKey(),new String(message.getBody()), messageProperties.getConsumerQueue());
         channel.basicAck(messageProperties.getDeliveryTag(), false);
    }
}
