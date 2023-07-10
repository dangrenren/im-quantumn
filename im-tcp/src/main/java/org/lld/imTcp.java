package org.lld;

import org.lld.tcp.server.LimServer;
import org.lld.tcp.server.LimWebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class imTcp {
    public static void main(String[] args) {
        SpringApplication.run(imTcp.class, args);
        System.out.println("项目启动成功...");
    }
}