package org.lld.tcp.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${BootstrapConfig.TcpConfig.brokerId}")
    private String brokerId;

    // 定义交换机的名称
    public static final String EXCHANGE_NAME = "messageService2Pipeline";
    // 定义队列的名称
    public static final String QUEUE_NAME = "messageService2Pipeline";

    // 创建fanout类型的交换机
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(EXCHANGE_NAME+brokerId);
    }

    // 创建队列
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME+brokerId);
    }


    // 将队列绑定到交换机
    @Bean
    public Binding binding(Queue queue, FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

}
