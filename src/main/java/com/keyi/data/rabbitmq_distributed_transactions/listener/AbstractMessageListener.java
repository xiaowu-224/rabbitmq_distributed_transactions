package com.keyi.data.rabbitmq_distributed_transactions.listener;

import com.keyi.data.rabbitmq_distributed_transactions.po.TransMessagePO;
import com.keyi.data.rabbitmq_distributed_transactions.service.TransMessageService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.omg.IOP.TransactionService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @BelongsProject: rabbitmq_distributed_transactions
 * @BelongsPackage: com.keyi.data.rabbitmq_distributed_transactions.listener
 * @Author: wujun
 * @CreateTime: 2025-04-09  16:50
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public abstract class AbstractMessageListener implements ChannelAwareMessageListener {

    @Autowired
    private TransMessageService transMessageService;
    @Value("${moodymq.resendTimes}")
    private Integer resendTimes;
    public  abstract  void  receviceMessage(Message message) throws Exception;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        String messageId = messageProperties.getMessageId();
        long deliveryTag = messageProperties.getDeliveryTag();//消息唯一标识
        TransMessagePO transMessagePO = transMessageService.messageReceiveRead(messageId, messageProperties.getReceivedExchange(), messageProperties.getReceivedRoutingKey(),
                new String(message.getBody()), messageProperties.getConsumerQueue());
        log.info("收到消息{},消费次数{}",messageProperties.getMessageId(),transMessagePO.getSequence());
        try{
            receviceMessage(message);
            channel.basicAck(deliveryTag,false);
            transMessageService.messageReceiveSuccess(messageId);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            if (transMessagePO.getSequence()>resendTimes){
                channel.basicReject(deliveryTag,false);
            }else {
                Thread.sleep((long) Math.pow(2,transMessagePO.getSequence()*1000));
               channel.basicNack(deliveryTag,false,true);
            }
        }
    }
}
