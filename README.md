
### Pulsar-SpringBoot-Starter

#### 版本:
    pulsar client: 2.7.0

#### 引入:
    可直接引入jar, 可publish到mvn后按需引用

#### 使用配置

```yaml
spring:
  pulsar:
    # pulsar 连接地址
    service-url: pulsar://localhost:6650
    # 监听的消息队列名, 按需配置
    listener-name: persistent://${user-name}/${name-space}/${topic}
    # pulsar client io threads
    io-threads: 1
    # 消息队列监听线程
    listener-threads: 1
    # no delay tcp
    enable-tcp-no-delay: true
    # 连接超时时间
    connection-timeout: 5000
    # 心跳检测时间
    keep-alive-interval: 30000
    # 读写超时时间
    operation-timeout: 3000
```

#### 使用声明

    关于消息编解码, 直接传入对应的 class 即可, 内部会根据class类型进行编解码适配, 原生类型会被映射到pulsar的原生类型,
    自定义类型会使用json, 编解码匹配在文末.

##### 生产者(以下为示例)

```java
// 声明消息内容
@Data
class Meta{
    private int type;
    
    private String message;
}

//声明消息内容
@Data
class User{
    private String userId;
    
    private String userName;
}

// 生产者声明
@Configuration
class PulsarConfig{
    
    /**
     * Meta producer
     **/
    @Bean
    public PulsarProducer<Meta> metaProducer(){
        return new PulsarProducer<Meta>()
                .setSchema(Schema.JSON(Meta.class))
                .setTopic("persistent://${user-name}/${name-space}/${topic}")
                .setBlockIfQueueFull(true);
    }

    /**
     * user producer
     **/
    @Bean
    public PulsarProducer<User> userProducer(){
        return new PulsarProducer<User>()
                .setSchema(Schema.JSON(User.class))
                .setTopic("persistent://${user-name}/${name-space}/${topic}")
                .setBlockIfQueueFull(true);
    }

    /**
     * raw producer
     **/
    @Bean
    public PulsarProducer<byte[]> rawProducer(){
        return new PulsarProducer<byte[]>()
                .setSchema(Schema.BYTES)
                .setTopic("persistent://${user-name}/${name-space}/${topic}")
                .setBlockIfQueueFull(true);
    }
}

// 生产者调用
@Service
class MessageService{
    
    @Autowired
    private PulsarProducerTemplate<Meta> metaTemplate;

    @Autowired
    private PulsarProducerTemplate<User> userTemplate;
    
    public void sendMeta(Meta meta){
        final Optional<MessageId> opt = metaTemplate.send("persistent://${user-name}/${name-space}/${topic}", meta);
        if(opt.ifPresent()){
            // ok
        }else{
            // error
        }
    }

    public void sendMessage(User user){
        final Optional<MessageId> opt = userTemplate.send("persistent://${user-name}/${name-space}/${topic}", user);
        if(opt.ifPresent()){
            // ok
        }else{
            // error
        }
    }
}
```

##### 消费者(示例)

自动确认消息被消费

```java
@Component // or @Service or any other bean definition
@Slf4j    
class PulsarMessageHandler{

    @Consumer(topics = "persistent://${user-name}/${name-space}/${topic}", description = "my test desc", type = Meta.class)
    public void metaMessageHandler(Meta meta){
        log.error("receive meta message[{}]", meta);
    }

    @Consumer(topics = "persistent://${user-name}/${name-space}/${topic}", description = "my test desc", type = User.class)
    public void userMessageHandler(User user){
        log.error("receive user message[{}]", user);
    }

    @Consumer(topics = "persistent://${user-name}/${name-space}/${topic}", description = "my test desc", type = byte[].class)
    public void rawMessageHandler(byte[] raw){
        log.error("receive raw message[{}]", raw);
    }
}
```

手动确认消息被消费, 入参必须是 pulsar中的(Consumer<?> consumer, Message<?> message) 包裹类, 否则无法手动ack
```java
@Component // or @Service or any other bean definition
@Slf4j    
class PulsarMessageHandler{

    @Consumer(topics = "persistent://${user-name}/${name-space}/${topic}", autoAck = false, description = "my test desc", type = Meta.class)
    public void metaMessageHandler(Consumer<?> consumer, Message<Meta> message){
        try{
            log.error("receive meta message[{}]", message.getValue());
            consumer.ack(message);
        }catch (Exception ex){
            consumer.negativeAcknowledge(message); // or not negative ack, deprecated this message
        }
    }

    @Consumer(topics = "persistent://${user-name}/${name-space}/${topic}", autoAck = false, description = "my test desc", type = Message.class)
    public void userMessageHandler(Consumer<?> consumer, Message<User> message){
        try{
            log.error("receive meta message[{}]", message.getValue());
            consumer.ack(message);
        }catch (Exception ex){
            consumer.negativeAcknowledge(message); // or not negative ack, deprecated this message
        }
    }
}
```


##### 编解码

```java
public class Schema{
    private static Schema<?> schema(Class<?> clazz){
        if(byte[].class.equals(clazz)){
            return Schema.BYTES;
        }else if(ByteBuffer.class.equals(clazz)){
            return Schema.BYTEBUFFER;
        }else if(String.class.equals(clazz)){
            return Schema.STRING;
        }else if(byte.class.equals(clazz)){
            return Schema.INT8;
        }else if(Short.class.equals(clazz) || short.class.equals(clazz)){
            return Schema.INT16;
        }else if(Integer.class.equals(clazz) || int.class.equals(clazz)){
            return Schema.INT32;
        }else if(Long.class.equals(clazz) || long.class.equals(clazz)){
            return Schema.INT64;
        }else if(Boolean.class.equals(clazz) || boolean.class.equals(clazz)){
            return Schema.BOOL;
        }else if(Float.class.equals(clazz) || float.class.equals(clazz)){
            return Schema.FLOAT;
        }else if(Double.class.equals(clazz) || double.class.equals(clazz)){
            return Schema.DOUBLE;
        }else if(Date.class.equals(clazz)){
            return Schema.DATE;
        }else if(Time.class.equals(clazz)){
            return Schema.TIME;
        }else if(Timestamp.class.equals(clazz)){
            return Schema.TIMESTAMP;
        }else if(Instant.class.equals(clazz)){
            return Schema.INSTANT;
        }else if(LocalDate.class.equals(clazz)){
            return Schema.LOCAL_DATE;
        }else if(LocalTime.class.equals(clazz)){
            return Schema.LOCAL_TIME;
        }else if(LocalDateTime.class.equals(clazz)){
            return Schema.LOCAL_DATE_TIME;
        }
        return Schema.JSON(clazz);
    }
}
```