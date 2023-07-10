package org.lld.tcp.start;

import org.lld.tcp.server.LimServer;
import org.lld.tcp.server.LimWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
//@EnableAspectJAutoProxy
public class tcpStart {
    @Autowired
    private LimServer limServer;
    @Autowired
    private LimWebSocketServer limWebSocketServer;

    @PostConstruct
    public void start(){
        // @PostConstruct 注解是一个生命周期回调方法，用于在依赖注入完成后执行初始化操作。
        // 在容器管理的组件中，当对象实例化并完成依赖注入后，容器会自动调用被 @PostConstruct 注解标记的方法。
        limServer.start();
        limWebSocketServer.start();

        limServer.testAop();
        String getAroundResult = limServer.testAopAround();
        System.out.println(getAroundResult);
        // limServer.testAopException();
    }

}
