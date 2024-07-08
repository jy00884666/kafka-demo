package com.kafka.test.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息消费者简单例子
 * 订阅kafka中的Topic主题名称为"TopicTest"的消息
 */
@Slf4j
public class ConsumerDemo1 {
    
    public static void main(String[] args) {
        System.out.println("kafka消费者例子");
        // 创建配置对象
        Map<String, Object> configMap = new HashMap<>();
        // 连接kafka主机(集群地址)
        configMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // 生产者数据key ,将键对象作为字符串进行反序列化
        configMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // 生产者数据value ,将键对象作为字符串进行反序列化
        configMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // 设置消费组id
        configMap.put(ConsumerConfig.GROUP_ID_CONFIG, "gourpId.demo");
        
        // 创建消费者对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configMap);
        
        // 订阅 Topic 主题,主题名称与消息生产者一致
        consumer.subscribe(Collections.singletonList("TopicTest"));
        
        // 从kafka中获取消息数据,消费者从kafka中拉取数据
        try {
            while (true) {
                // 设置超时时间1000毫秒
                final ConsumerRecords<String, String> datas = consumer.poll(1000);
                for (ConsumerRecord<String, String> data : datas) {
                    System.out.println("消费者消费消息内容:" + data);
                    log.info("消费者消费消息内容:{}", data);
                }
            }
        } catch (Exception e) {
            log.error("消费者消费消息异常", e);
        } finally {
            // 关闭消费者对象
            consumer.close();
        }
    }
}
