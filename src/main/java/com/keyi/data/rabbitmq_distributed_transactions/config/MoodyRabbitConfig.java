package com.keyi.data.rabbitmq_distributed_transactions.config;

import com.keyi.data.rabbitmq_distributed_transactions.service.TransMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

import org.springframework.context.annotation.Configuration;

/**
 * @BelongsProject: rabbitmq_distributed_transactions
 * @BelongsPackage: com.keyi.data.rabbitmq_distributed_transactions.config
 * @Author: wujun
 * @CreateTime: 2025-04-08  21:27
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration
@Slf4j
public class MoodyRabbitConfig implements ApplicationContextAware {
    private final TransMessageService transMessageService;

    public MoodyRabbitConfig(@Qualifier("transMessageService") TransMessageService transMessageService) {
        this.transMessageService = transMessageService;
    }

    /**
     * @description: Spring容器获取rabbitTemplate并且监听消息路由
     * @author:wujun
     * @date: 2025/4/8 21:32
     * @param:
     * @param: applicationContext
     * @return: void
     **/
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //获取RabbitTemplate
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {

            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("ConfirmCallback:" + correlationData + " ack " + ack + " cause " + cause);
                //correlationData存放有落盘消息id 如果为空无法找到对应落盘消息
                if (ack&&null!=correlationData) {
                    String messageId = correlationData.getId();
                    log.info("消息已经正确投递到交换机,id:{}",messageId);
                    transMessageService.messageSendSuccess(messageId);
                }else {
                    log.info("消息投递至交换机失败,correlationData:{}",correlationData);
                }
            }
        });
        //设置ReturnCallBack
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                log.debug("消息无法路由!" + returnedMessage.getExchange() + returnedMessage.getRoutingKey() + returnedMessage.getMessage() + returnedMessage.getReplyText());
                Message message = returnedMessage.getMessage();
                MessageProperties messageProperties = message.getMessageProperties();
                String messageId = messageProperties.getMessageId();
                transMessageService.messageSendReturn(messageId,messageProperties.getReceivedRoutingKey(),new String(message.getBody()));
            }
        });
    }

}
