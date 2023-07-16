package org.lld.tcp.utils;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Component
public class MqFactory {
    @Value("${rabbitmq.host}")
    private String host;
    @Value("${rabbitmq.port}")
    private Integer port;
    @Value("${rabbitmq.userName}")
    private String userName;
    @Value("${rabbitmq.password}")
    private String password;
    @Value("${rabbitmq.virtualHost}")
    private String virtualHost;


    private static ConnectionFactory factory = null;

    private static Channel defaultChannel;

    private static ConcurrentHashMap<String,Channel> channelMap = new ConcurrentHashMap<>();

    private static Connection getConnection() throws IOException, TimeoutException {
        Connection connection = factory.newConnection();//在这个地方报错!!!!!连接失败
        System.out.println("获取rabbitmq连接"+connection);
        return connection;
    }

    public static Channel getChannel(String channelName) throws IOException, TimeoutException {
        Channel channel = channelMap.get(channelName);
        if(channel == null){
            channel = getConnection().createChannel();
            channelMap.put(channelName,channel);
        }
        return channel;
    }

    public  void init(){
        if(factory == null){
            factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.setUsername(userName);
            factory.setPassword(password);
            factory.setVirtualHost(virtualHost);
            System.out.println("factory初始化rabbitmq连接工厂"+factory);
        }
    }

}
