package com.keyi.data.rabbitmq_distributed_transactions.service;

import com.keyi.data.rabbitmq_distributed_transactions.po.TransMessagePO;

import java.util.List;

public interface TransMessageService {

    /**
     * @description: 发送前暂存消息
     * @author:wujun
     * @date: 2025/4/8 20:08
     * @param: exchange
     * @param: routingKey
     * @param: body
     * @return: TransMessagePO
     **/
    TransMessagePO messageSendReady(String exchange, String routingKey, String body);

    /**
     * @description: 设置消息发送成功
     * @author:wujun
     * @date: 2025/4/8 20:09
     * @param: id
     * @return: void
     **/
    void messageSendSuccess(String id);

    /**
     * @description: 设置消息返回重新持久化
     * @author:wujun
     * @date: 2025/4/8 20:18
     * @param: exchange
     * @param: routingKey
     * @param: body
     * @return: TransMessagePO
     **/
    TransMessagePO messageSendReturn(String exchange, String routingKey, String body);

    /**
     * @description: 查询应发未发消息
     * @author:wujun
     * @date: 2025/4/8 20:21
     * @return: List<TransMessagePO>
     **/
    List<TransMessagePO> listReadyMessage();
    /**
     * @description: 记录消息发送次数
     * @author:wujun
     * @date: 2025/4/8 20:22
     * @param: 
     * @param: id
     * @return: void
     **/
    void  messageResend(String id);
    /**
     * @description: 消息重发多次,放弃
     * @author:wujun
     * @date: 2025/4/8 20:23
     * @param:
     * @param: id
     * @return: void
     **/
    void  messageDead(String id);

    /**
     * 保存监听到的死信消息
     * @param id
     * @param exchange
     * @param routingKey
     * @param body
     * @param queue
     */
    void  messageDead(String id,String exchange,String routingKey,String body,String queue);

    /**
     * 消息消费前落库
     * @param id
     * @param exchange
     * @param routingKey
     * @param body
     * @param queue
     * @return
     */
    TransMessagePO messageReceiveRead(String id,String exchange,String routingKey,String body,String queue);

    /**
     * 消息成功消费
     * @param id
     */
    void   messageReceiveSuccess(String id);


}
