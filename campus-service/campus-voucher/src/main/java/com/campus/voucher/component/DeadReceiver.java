package com.campus.voucher.component;

import com.campus.voucher.domain.Voucher;
import com.campus.voucher.domain.VoucherOrder;
import com.campus.voucher.service.VoucherOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeadReceiver {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    VoucherOrderService voucherOrderService;

    @KafkaListener(topics = "VOUCHER")
    public void handleDeadMsg(ConsumerRecord<String, VoucherOrder> record, Acknowledgment ack) {
        try{
            log.info("接收到的消息是:{}",record.value());
            voucherOrderService.createOrder(record.value());
            ack.acknowledge();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
