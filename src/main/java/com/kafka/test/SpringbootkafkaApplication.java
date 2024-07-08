package com.kafka.test;

import com.kafka.test.producer.ProducerDemo2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * 启动后通过访问 http://localhost:8083/producerDemo2Controller/send2 发送消息
 */
@SpringBootApplication
public class SpringbootkafkaApplication {
    
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(SpringbootkafkaApplication.class, args);
    }
}