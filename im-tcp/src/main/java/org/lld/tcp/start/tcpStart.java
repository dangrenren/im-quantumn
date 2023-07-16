package org.lld.tcp.start;

import org.I0Itec.zkclient.ZkClient;
import org.lld.tcp.reciver.MessageReciver;
import org.lld.tcp.register.RegistryZK;
import org.lld.tcp.register.ZKit;
import org.lld.tcp.server.LimServer;
import org.lld.tcp.server.LimWebSocketServer;
import org.lld.tcp.utils.MqFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
//@EnableAspectJAutoProxy
public class tcpStart {
    @Autowired
    private LimServer limServer;
    @Autowired
    private LimWebSocketServer limWebSocketServer;

    @Autowired
    public org.lld.tcp.redis.redisManager redisManager;

    @Autowired
    public MqFactory mqFactory;

    @Autowired
    public MessageReciver messageReciver;

    @Value("${zkConfig.zkAddr}")
    private String zkAddr;
    @Value("${zkConfig.zkConnectTimeOut}")
    private int zkConnectTimeOut;

    @Value("${BootstrapConfig.TcpConfig.tcpPort}")
    private int tcpPort;




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

        redisManager.init();// 初始化redis
        // 不再使用这两个方法初始化mq和消息MQ消息接收,用spring的注解RabbitConfig和FanoutSubscriber代替
        //mqFactory.init();// 初始化mq
        //messageReciver.init();// 初始化mq消息接收器,开始接受消息
        try {
            startRegistryZK();//开始注册到zookeeper
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }


    }
    /*
    public static void  staticStart(){
        System.out.println("静态方法启动成功...");
    }
    */

    public void startRegistryZK() throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        ZkClient zkClient = new ZkClient(zkAddr, zkConnectTimeOut);
        ZKit zKit = new ZKit(zkClient);
        RegistryZK registryZK = new RegistryZK(zKit, hostAddress,tcpPort);
        Thread thread = new Thread(registryZK);
        thread.start();
        System.out.println("注册到zookeeper...");
    }
}
