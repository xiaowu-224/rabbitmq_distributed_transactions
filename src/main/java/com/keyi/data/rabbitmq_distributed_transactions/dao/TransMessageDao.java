package com.keyi.data.rabbitmq_distributed_transactions.dao;

import com.keyi.data.rabbitmq_distributed_transactions.po.TransMessagePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public interface TransMessageDao {

    void insert(TransMessagePO transMessagePO);

    void update(TransMessagePO transMessagePO);

    TransMessagePO selectByIdAndService(@Param("id") String id,@Param("service") String service);

    List<TransMessagePO> selectByTypeAndService(@Param("type") String type, @Param("service") String service);

    void  delete(@Param("id") String id,@Param("service") String service);
}
