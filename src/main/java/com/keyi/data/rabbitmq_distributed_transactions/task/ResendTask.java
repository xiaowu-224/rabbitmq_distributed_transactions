package com.keyi.data.rabbitmq_distributed_transactions.task;

import com.keyi.data.rabbitmq_distributed_transactions.po.TransMessagePO;
import com.keyi.data.rabbitmq_distributed_transactions.sender.TransMessageSender;
import com.keyi.data.rabbitmq_distributed_transactions.service.TransMessageService;
import lombok.extern.slf4j.Slf4j;
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
@EnableScheduling
@Configuration
@Component
@Slf4j
public class ResendTask {

    private TransMessageService transMessageService;

    private TransMessageSender transMessageSender;
    @Autowired
    public ResendTask(TransMessageService transMessageService, TransMessageSender transMessageSender) {
        this.transMessageService = transMessageService;
        this.transMessageSender = transMessageSender;
    }



    @Value("${moodymq.resendTimes")
    Integer   resendTimes;


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
                transMessageSender.send(x.getExchange(), x.getRoutingKey(), x.getPayload());
                transMessageService.messageResend(x.getId());
            }
        });
    }
}
