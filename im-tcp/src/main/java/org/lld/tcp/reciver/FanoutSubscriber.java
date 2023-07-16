package org.lld.tcp.reciver;


import org.lld.tcp.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FanoutSubscriber {

    //private static final String queue1= "messageService2Pipeline1001";
    // 指定监听的队列名称
   // @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    //@RabbitListener(queues = {queue1})
    @RabbitListener(queues =RabbitMQConfig.QUEUE_NAME+"#{${BootstrapConfig.TcpConfig.brokerId}}")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
        // 处理接收到的消息
    }
}
