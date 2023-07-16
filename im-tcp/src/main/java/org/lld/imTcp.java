package org.lld;

import org.lld.tcp.reciver.MessageReciver;
import org.lld.tcp.server.LimServer;
import org.lld.tcp.server.LimWebSocketServer;
import org.lld.tcp.start.tcpStart;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableRabbit
@EnableAspectJAutoProxy
public class imTcp {
    public static void main(String[] args) {
        SpringApplication.run(imTcp.class, args);
        //tcpStart.staticStart();//启动tcpStart类中的静态方法
        System.out.println("项目启动成功...");
    }

}