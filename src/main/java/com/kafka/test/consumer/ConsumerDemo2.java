package com.kafka.test.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 注解-消息消费者
 */
@Slf4j
@Component
public class ConsumerDemo2 {
    
    /**
     * 监听器{topics:监听主题}
     * @param record
     */
    @KafkaListener(topics = {"${spring.kafka.topic.name}"})
    public void listener(ConsumerRecord record) {
        log.info("消费者对象ConsumerRecord:{}", record);
        // 可能为 null 的值包装到 Optional 容器中。如果该值为 null，则返回一个空的 Optional 对象；否则，返回一个包含该值的 Optional 对象。
        Optional msg = Optional.ofNullable(record.value());
        // 避免直接访问 Optional 对象中的值时出现空指针异常
        if (msg.isPresent()) {
            log.info("消息消费者主题:{},分区:{},偏移量:{},key:{},value:{}",
                    record.topic(), record.partition(), record.offset(), record.key(), msg.get());
        }
    }
}
