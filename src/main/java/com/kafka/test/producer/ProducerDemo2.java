package com.kafka.test.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注解-消息生产者
 */
@Slf4j
@RestController
@RequestMapping("/producerDemo2Controller")
public class ProducerDemo2 {
    
    /**
     * Topic名称(send时主题不存在会自动创建)
     */
    @Value("${spring.kafka.topic.name}")
    private String TOPIC_NAME;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    /**
     * http://localhost:8083/producerDemo2Controller/send2 发送消息
     * 最简单的例子发送消息,不接收任何参数
     */
    @RequestMapping("/send2")
    public String send2() {
        kafkaTemplate.send(TOPIC_NAME, "kafka-demo2", "hello:kafka-demo2");
        log.info("send2发送消息成功");
        return "send2发送消息成功";
    }
    
    /**
     * http://localhost:8083/producerDemo2Controller/send3 发送消息
     * 同步发送消息
     * @return
     */
    @RequestMapping("/send3")
    public String send3() {
        final ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC_NAME, "kafka-demo2",
                "hello:kafka-demo2同步");
        try {
            // get方法等待返回结果
            SendResult<String, String> sendResult = future.get();
            RecordMetadata recordMetadata = sendResult.getRecordMetadata();
            log.info("主题:{},分区:{},偏移量:{}", recordMetadata.topic(), recordMetadata.partition(),
                    recordMetadata.offset());
        } catch (Exception e) {
        
        }
        return "同步发送消息成功";
    }
    
    /**
     * http://localhost:8083/producerDemo2Controller/send4 发送消息
     * 异步发送消息
     * @return
     */
    @RequestMapping("/send4")
    public String send4() {
        final ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC_NAME, "kafka-demo2",
                "hello:kafka-demo2异步");
        try {
            // 添加回调,异步等待broker端的返回结果
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onFailure(Throwable throwable) {
                    log.error("异步发送消息失败:{}", throwable.getMessage(), throwable);
                }
                
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    RecordMetadata recordMetadata = result.getRecordMetadata();
                    log.info("异步发送消息成功:主题:{},分区:{},偏移量:{}", recordMetadata.topic(),
                            recordMetadata.partition(),
                            recordMetadata.offset());
                }
            });
        } catch (Exception e) {
            log.error("异常:{}", e.getMessage(), e);
        }
        return "异步发送消息成功";
    }
    
}
