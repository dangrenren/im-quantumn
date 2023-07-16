package org.lld.tcp.reciver;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lld.codec.entity.MessagePack;
import org.lld.common.constant.Constants;
import org.lld.tcp.utils.MqFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Slf4j
@Component
public class MessageReciver {

    private static void startReciverMessage() {
        System.out.println("开始startReciverMessage方法，准备绑定");
        try {
            Channel channel = MqFactory.getChannel(Constants.RabbitConstants.MessageService2Im);
            if(channel == null){
                System.out.println("channel为空");
            }
            channel.queueDeclare(Constants.RabbitConstants.MessageService2Im,
                    true, false, false, null
            );
            channel.exchangeDeclare(Constants.RabbitConstants.MessageService2Im, "fanout");
            channel.queueBind(Constants.RabbitConstants.MessageService2Im,
                    Constants.RabbitConstants.MessageService2Im, "");

            channel.basicConsume(Constants.RabbitConstants
                            .MessageService2Im , false,
                    new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            try {
                                String msgStr = new String(body);
                                log.info(msgStr);//打印消息
                                log.info("我收不到Rabbit的消息啊");
                            }catch (Exception e){
                                e.printStackTrace();
                                channel.basicNack(envelope.getDeliveryTag(),false,false);
                            }
                        }
                    }
            );
        } catch (Exception e) {
           // System.out.println(e.getMessage());
            System.out.println("绑定失败,无法接收消息");
        }
    }

    public static void init() {
        startReciverMessage();
    }

}
