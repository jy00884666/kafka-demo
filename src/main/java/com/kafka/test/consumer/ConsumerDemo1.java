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
        createConsumer0();
    }
    
    /**
     * 简单的消费者例子,while中一直打印kafka日志没解决
     */
    private static void createConsumer0() {
        // 创建配置对象
        Map<String, Object> configMap = createKafkaConfigMap();
    
        // 创建消费者对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configMap);
    
        // 订阅 Topic 主题,主题名称与消息生产者一致
        consumer.subscribe(Collections.singletonList("TopicTest"));
    
        // 从kafka中获取消息数据,消费者从kafka中拉取数据
        try {
            while (true) {
                // 设置超时时间1000毫秒,poll()拉取一批消息数据
                final ConsumerRecords<String, String> datas = consumer.poll(1000);
                // 如果有消息才执行业务处理
                if (datas.count() > 0) {
                    for (ConsumerRecord<String, String> data : datas) {
                        System.out.println("消费者消费消息内容:" + data);
                        log.info("消费者消费消息内容:{}", data);
                        log.info("消费者消费消息内容:topic:{},partition:{},offset:{},key:{},value:{}", data.topic(),
                                data.partition(),
                                data.offset(), data.key(), data.value());
                    }
                } else {
                    log.info("未消费到任何消息.");
                    break;
                }
            }
        } catch (Exception e) {
            log.error("消费者消费消息异常", e);
        } finally {
            // 关闭消费者对象
            consumer.close();
        }
    }
    
    /**
     * 设置kafka消费者配置信息
     * @return
     */
    public static Map createKafkaConfigMap() {
        Map<String, Object> configMap = new HashMap<>();
        // 连接kafka主机(集群地址)
        configMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // 生产者数据key ,将键对象作为字符串进行反序列化
        configMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // 生产者数据value ,将键对象作为字符串进行反序列化
        configMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // 设置消费组id
        configMap.put(ConsumerConfig.GROUP_ID_CONFIG, "gourpId.demo");
        // 指定当消费者加入一个新的消费组或者偏移量无效时的重置策略。常见的取值有 earliest（从最早的偏移量开始消费）和 latest（从最新的偏移量开始消费）
        configMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        // 指定是否开启自动提交消费位移（offset）的功能。设置为 true 则开启自动提交，设置为 false 则需要手动调用 Acknowledgment 接口的 acknowledge() 方法进行位移提交。
        configMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        // 当开启自动提交时，指定自动提交的间隔时间（以毫秒为单位）,默认值为 5000 毫秒,也就是 5 秒
        configMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        return configMap;
    }
}
