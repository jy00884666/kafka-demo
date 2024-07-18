package com.kafka.test.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 简单的消息发送例子
 * 消息生产者往kafka Borker的Topic中发送消息
 *
 * (Broker)代理：Broker 是 Kafka 集群中的服务器节点。每个 Broker 负责管理多个分区和副本，并接收来自生产者的消息并为消费者提供消息。
 * --------------多个 Broker 连接到相同的 ZooKeeper 就组成了一个 Kafka 集群，形成高可用性的数据存储和处理平台。
 *
 * (Topics)主题：是数据记录存放的地方，类似于数据库的表。
 * --------------在 Kafka 中消息被发布到一个或多个主题中。每个主题可以有多个分区，每个分区在不同的 Broker 上进行副本复制，以实现负载均衡和故障容错
 *
 * (Consumer Group)消费组：消费组是消费者的逻辑组织形式，多个消费者可以组成一个消费组，共同消费一个或多个主题的消息。
 * ------------------------在一个消费组中，每个分区的消息只能被一个消费者消费，但不同分区的消息可以被不同消费者并行消费。
 *
 * ZooKeeper 是 Kafka 依赖的外部服务，用于管理和维护 Kafka 集群的元数据，包括 Broker 的状态、Topic 和 Consumer Group 的信息等。
 * Kafka 使用 ZooKeeper 来实现集群的协调和领导者选举等功能
 */
@Slf4j
public class ProducerDemo1 {
    
    public static void main(String[] args) {
        System.out.println("kafka生产者例子");
        // 发送消息
        //createTopic0();
        // 创建Topic同步
        //createTopic1();
        // 创建Topic异步
        //createTopic2();
        // 异步发送消息
        createTopic3();
    }
    
    /**
     * 简单生产者例子,无返回值
     */
    public static void createTopic0() {
        // 创建配置对象
        Map<String, Object> configMap = createKafkaConfigMap();
        
        // 创建生产者对象
        // 生产者对象需要设置泛型,数据的类型约束
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configMap);
        
        // 创建数据
        /**
         * 构建数据需要传递三个参数
         * 第一个参数:Topic名称(主题不存在会自动创建)
         * 第二个参数:数据的key
         * 第三个参数:数据的value
         */
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(
                "TopicTest", "kafka-demo1", "hello,kafka!"
        );
        
        // 通过生产者对象将数据发送到kafka
        try {
            producer.send(record);
            System.out.println("消息发送成功");
            log.info("消息发送成功");
        } catch (Exception e) {
            log.error("发送消息异常", e);
        } finally {
            // 关闭生产者对象
            producer.close();
        }
        
    }
    
    /**
     * 创建kafka配置对象
     * @return
     */
    private static Map<String, Object> createKafkaConfigMap() {
        Map<String, Object> configMap = new HashMap<>();
        // 连接kafka主机(集群地址)
        configMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // 生产者数据key序列化,将键对象作为字符串进行序列化
        configMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // 生产者数据value序列化,将键对象作为字符串进行序列化
        configMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // 设置重试次数
        configMap.put(ProducerConfig.RETRIES_CONFIG, 3);
        // 生产者发送消息的确认模式。可选的值有 “0”（不需要任何确认）、“1”（只需要 Leader 确认）和 “all”（需要 Leader 和所有副本确认）
        configMap.put(ProducerConfig.ACKS_CONFIG, "all");
        return configMap;
    }
    
    /**
     * 使用Kafka的AdminClient API来创建Topic (同步)
     */
    public static void createTopic1() {
        // 创建配置对象
        Map<String, Object> configMap = createKafkaConfigMap();
        
        // 使用Kafka的管理员对象 AdminClient API来创建Topic (同步)
        AdminClient adminClient = AdminClient.create(configMap);
        /**参数解析:
         * 参数1:主题名称(可以是字母,数字,点,下划线,中横线),kafka担心主题名称与内部监控指标的名称重复,所以不推荐点和下划线同时使用
         * 参数2:分区数量 int
         * 参数3:主题分区副本因子数量 short
         * 创建了一个名为"TopicTest"的topic，它有一个分区，一个副本因子，并且指定了连接到的kafka broker
         */
        NewTopic topic = new NewTopic("TopicTest", 1, (short) 1);
        try {
            // 创建主题,调用.all()方法来创建所有主题
            CreateTopicsResult result = adminClient.createTopics(Collections.singleton(topic));
            KafkaFuture<Void> createTopicFuture = result.all();
            // get()方法阻塞等待操作完成
            createTopicFuture.get();
        } catch (Exception e) {
            log.error("同步创建Topic异常", e);
        } finally {
            // 关闭连接
            adminClient.close();
        }
    }
    
    /**
     * 使用Kafka的AdminClient API来创建Topic (异步)
     */
    public static void createTopic2() {
        // 创建配置对象
        Map<String, Object> configMap = createKafkaConfigMap();
        
        // 使用Kafka的管理员对象 AdminClient API来创建Topic (异步)
        AdminClient adminClient = AdminClient.create(configMap);
        /**参数解析:
         * 参数1:主题名称(可以是字母,数字,点,下划线,中横线),kafka担心主题名称与内部监控指标的名称重复,所以不推荐点和下划线同时使用
         * 参数2:分区数量 int
         * 参数3:主题分区副本因子数量 short
         * 创建了一个名为"TopicTest"的topic，它有一个分区，一个副本因子，并且指定了连接到的kafka broker
         */
        NewTopic topic = new NewTopic("TopicTest", 1, (short) 1);
        try {
            // 创建主题,调用.all()方法来创建所有主题
            CreateTopicsResult result = adminClient.createTopics(Collections.singleton(topic));
            KafkaFuture<Void> createTopicFuture = result.all();
            // 后置处理器异步操作完成
            createTopicFuture.whenComplete((unused, throwable) -> {
                if (throwable != null) {
                    log.error("主题Topic创建异常:{} ", throwable);
                } else {
                    log.info("异步创建Topic成功:{}", unused);
                }
            });
        } catch (Exception e) {
            log.error("异步创建Topic异常", e);
        } finally {
            // 关闭连接
            adminClient.close();
        }
    }
    
    /**
     * 通过异步回调的方式发送消息
     */
    public static void createTopic3() {
        // 创建配置对象
        Map<String, Object> configMap = createKafkaConfigMap();
        
        // 创建生产者对象
        // 生产者对象需要设置泛型,数据的类型约束
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configMap);
        
        // 创建数据
        /**
         * 构建数据需要传递三个参数
         * 第一个参数:Topic名称(主题不存在会自动创建)
         * 第二个参数:数据的key
         * 第三个参数:数据的value
         */
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(
                "TopicTest", "kafka-demo1", "hello,kafka!"
        );
        
        // 通过异步回调的方式发送消息
        try {
            producer.send(record, new Callback() {
                /**
                 * 该接口中表示kafka服务器响应给客户端,会自动调用 onCompletion 方法
                 * metadata:消息的源数据(属于哪个Topic,属于哪个Partition,对应的偏移量是什么)
                 * exception:封装kafka消息出现的异常,如果为null表示发送成功
                 */
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    // 消息发送成功或失败
                    if (exception == null) {
                        log.info("异步发送成功:Topic:{},分区id:{},偏移量:{}", metadata.topic(), metadata.partition(),
                                metadata.offset());
                    } else {
                        log.error("异步发送异常:", exception);
                    }
                }
            });
        } catch (Exception e) {
            log.error("发送消息异常", e);
        } finally {
            // 关闭生产者对象
            producer.close();
        }
        
    }
}
