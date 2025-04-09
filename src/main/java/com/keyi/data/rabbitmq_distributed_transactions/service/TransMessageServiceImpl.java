package com.keyi.data.rabbitmq_distributed_transactions.service;

import com.keyi.data.rabbitmq_distributed_transactions.dao.TransMessageDao;
import com.keyi.data.rabbitmq_distributed_transactions.enummeration.TransMessageType;
import com.keyi.data.rabbitmq_distributed_transactions.po.TransMessagePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: rabbitmq_distributed_transactions
 * @BelongsPackage: com.keyi.data.rabbitmq_distributed_transactions.service
 * @Author: wujun
 * @CreateTime: 2025-04-08  20:27
 * @Version: 1.0
 */
@Service
public class TransMessageServiceImpl implements TransMessageService {

    private TransMessageDao transMessageDao;

    @Autowired
    public TransMessageServiceImpl(TransMessageDao transMessageDao) {
        this.transMessageDao = transMessageDao;
    }

    @Value("${moodymq.service}")
    String serviceName;

    @Override
    public TransMessagePO messageSendReady(String exchange, String routingKey, String body) {
        final String messageId = UUID.randomUUID().toString();
        TransMessagePO transMessagePO = new TransMessagePO();
        transMessagePO.setId(messageId);
        transMessagePO.setExchange(exchange);
        transMessagePO.setRoutingKey(routingKey);
        transMessagePO.setPayload(body);
        transMessagePO.setDate(new Date());
        transMessagePO.setSequence(0);
        transMessagePO.setService(serviceName);
        transMessagePO.setType(TransMessageType.SEND);
        transMessageDao.insert(transMessagePO);
        return transMessagePO;
    }

    @Override
    public void messageSendSuccess(String id) {
        transMessageDao.delete(id, serviceName);

    }

    @Override
    public TransMessagePO messageSendReturn(String exchange, String routingKey, String body) {
        return messageSendReady(exchange, routingKey, body);
    }

    @Override
    public List<TransMessagePO> listReadyMessage() {
        return transMessageDao.selectByTypeAndService(TransMessageType.SEND.toString(), serviceName);
    }

    @Override
    public void messageResend(String id) {
        TransMessagePO transMessagePO = transMessageDao.selectByIdAndService(id, serviceName);
        //TODO 分布式系统防止并发修改 后续添加分布式锁
        transMessagePO.setSequence(transMessagePO.getSequence() + 1);
        transMessageDao.update(transMessagePO);

    }

    @Override
    public void messageDead(String id) {
        TransMessagePO transMessagePO = transMessageDao.selectByIdAndService(id, serviceName);
        transMessagePO.setType(TransMessageType.DEAD);
        transMessageDao.update(transMessagePO);
    }

    @Override
    public void messageDead(String id, String exchange, String routingKey, String body, String queue) {
        TransMessagePO transMessagePO = new TransMessagePO();
        transMessagePO.setId(id);
        transMessagePO.setExchange(exchange);
        transMessagePO.setRoutingKey(routingKey);
        transMessagePO.setPayload(body);
        transMessagePO.setDate(new Date());
        transMessagePO.setQueue(queue);
        transMessagePO.setSequence(0);
        transMessagePO.setService(serviceName);
        transMessagePO.setType(TransMessageType.DEAD);
        transMessageDao.insert(transMessagePO);
    }

    @Override
    public TransMessagePO messageReceiveRead(String id, String exchange, String routingKey, String body, String queue) {
        TransMessagePO transMessagePO = transMessageDao.selectByIdAndService(id, serviceName);
        if (null != transMessagePO) {
            transMessagePO = new TransMessagePO();
            transMessagePO.setId(id);
            transMessagePO.setExchange(exchange);
            transMessagePO.setRoutingKey(routingKey);
            transMessagePO.setPayload(body);
            transMessagePO.setDate(new Date());
            transMessagePO.setQueue(queue);
            transMessagePO.setSequence(0);
            transMessagePO.setService(serviceName);
            transMessagePO.setType(TransMessageType.RECEIVE);
            transMessageDao.insert(transMessagePO);
        } else {
            transMessagePO.setSequence(transMessagePO.getSequence() + 1);
            transMessageDao.update(transMessagePO);
        }
        return transMessagePO;
    }

    @Override
    public void messageReceiveSuccess(String id) {
        transMessageDao.delete(id, serviceName);

    }
}
