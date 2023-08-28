package com.campus.message.socket;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaService {

    @Autowired
    WebSocket webSocket;

    /**
     * 订阅消息（用于处理本地会话转发）
     * */
    @KafkaListener(id = "${serverId}",topics = "WEBSOCKET")
    public void listener2(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String toUserId = record.key();
        String message = record.value();
        if(webSocket.isExistSession(toUserId)){ // 表示当前会话存在该服务本地
            log.info("【kafka消息同步】接收到本地会话消息，进行转发");
            webSocket.sendOneMessage(toUserId,message);
            log.info("【kafka消息同步】已发送消息");
        }
        ack.acknowledge();
    }
}
