package com.keyi.data.rabbitmq_distributed_transactions.po;

import com.keyi.data.rabbitmq_distributed_transactions.enummeration.TransMessageType;

import java.util.Date;

public class TransMessagePO {
    private  String id;
    private  String service;
    private TransMessageType type;
    private  String exchange;
    private  String routingKey;
    private  String queue;
    private  Integer  sequence;
    private  String  payload;
    private Date date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public TransMessageType getType() {
        return type;
    }

    public void setType(TransMessageType type) {
        this.type = type;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "TransMessagePO{" +
                "id='" + id + '\'' +
                ", service='" + service + '\'' +
                ", type=" + type +
                ", exchange='" + exchange + '\'' +
                ", routingKey='" + routingKey + '\'' +
                ", queue='" + queue + '\'' +
                ", sequence='" + sequence + '\'' +
                ", payload='" + payload + '\'' +
                ", date=" + date +
                '}';
    }
}
