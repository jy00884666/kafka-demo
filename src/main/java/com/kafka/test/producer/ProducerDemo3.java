package com.kafka.test.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * springboot整合-消息生产者-支持事务
 * 要支持事务需要配置事务前缀
 * spring.kafka.producer.transaction-id-prefix:
 * 还需要配置监听类型,否则会报错
 * spring.kafka.listener.type:
 */
@Slf4j
@RestController
@RequestMapping("/producerDemo3Controller")
public class ProducerDemo3 {
    
    /**
     * Topic名称(send时主题不存在会自动创建)
     */
    @Value("${spring.kafka.topic.name}")
    private String TOPIC_NAME;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    /**
     * 简单的事务发送消息例子
     * http://localhost:8083/producerDemo3Controller/send3/error 发送消息
     * 若传入的是error则会回滚事务
     */
    @RequestMapping("/send3/{input}")
    public String send3(@PathVariable String input) {
        // 事务支持
        kafkaTemplate.executeInTransaction(p -> {
            p.send(TOPIC_NAME, "kafka-demo3", "事务发送消息1");
            log.info("send3发送消息成功1");
            if ("error".equals(input)) {
                throw new RuntimeException("故意的抛出kafka发送消息异常");
            }
            p.send(TOPIC_NAME, "kafka-demo3", "事务发送消息2");
            log.info("send3发送消息成功2");
            return true;
        });
        return "send3发送消息成功";
    }
    
    /**
     * http://localhost:8083/producerDemo3Controller/send4/error 发送消息
     * 注解事务发送方式 @Transactional(rollbackFor = Exception.class 表示抛出何种异常时回滚事务
     * --                             )
     * @param input
     * @return
     */
    @RequestMapping("/send4/{input}")
    @Transactional(rollbackFor = Exception.class)
    public String send4(@PathVariable String input) {
        // 事务支持
        kafkaTemplate.send(TOPIC_NAME, "kafka-demo4", "事务发送消息3");
        log.info("send4发送消息成功3");
        if ("error".equals(input)) {
            throw new RuntimeException("故意的抛出kafka发送消息异常");
        }
        kafkaTemplate.send(TOPIC_NAME, "kafka-demo4", "事务发送消息4");
        log.info("send4发送消息成功4");
        return "send4发送消息成功";
    }
}
