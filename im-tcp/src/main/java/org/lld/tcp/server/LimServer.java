package org.lld.tcp.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.lld.codec.MeaasgeDecoder;
import org.lld.codec.MessageEncoder;
import org.lld.tcp.handler.HeartBeatHandler;
import org.lld.tcp.handler.NettyServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Component
public class LimServer {
    @Value("${BootstrapConfig.TcpConfig.bossThreadSize}")
    private int bossThreadSize;
    @Value("${BootstrapConfig.TcpConfig.workThreadSize}")
    private int workThreadSize;
    @Value("${BootstrapConfig.TcpConfig.heartBeatTime}")
    private int heartBeatTime;
    @Value("${BootstrapConfig.TcpConfig.tcpPort}")
    private int tcpPort;

    private final static Logger logger = LoggerFactory.getLogger(LimServer.class);

    EventLoopGroup mainGroup;
    EventLoopGroup subGroup;
    ServerBootstrap server;

    public LimServer() {
        mainGroup = new NioEventLoopGroup(bossThreadSize);
        subGroup = new NioEventLoopGroup(workThreadSize);
        server = new ServerBootstrap();
        server.group(mainGroup,subGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 10240) // 服务端可连接队列大小
                .option(ChannelOption.SO_REUSEADDR, true) // 参数表示允许重复使用本地地址和端口
                .childOption(ChannelOption.TCP_NODELAY, true) // 是否禁用Nagle算法 简单点说是否批量发送数据 true关闭 false开启。 开启的话可以减少一定的网络开销，但影响消息实时性
                .childOption(ChannelOption.SO_KEEPALIVE, true) // 保活开关2h没有数据服务端会发送心跳包
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        System.out.println("initChannel "+ch);
                        ch.pipeline().addLast(new MeaasgeDecoder())
                                .addLast(new MessageEncoder())
                                .addLast(new IdleStateHandler(0,0,1))
                                .addLast(new HeartBeatHandler((long) heartBeatTime))
                                .addLast(new NettyServerHandler());
                        //ch.pipeline().addLast(new MessageEncoder());
//                        ch.pipeline().addLast(new IdleStateHandler(
//                                0, 0,
//                                10));
                        //ch.pipeline().addLast(new HeartBeatHandler(heartBeatTime));
                    }
                });
    }

    public void start(){
        this.server.bind(tcpPort);
        System.out.println("tcp server start "+tcpPort);
    }
    public void testAop(){
        System.out.println("testAop");
    }

    public String testAopAround(){
        System.out.println("dangren test @Around 第一次");
        return "dangren test @Around";
    }


    //用于测试AOP异常抛出后处理
    public void testAopException(){
        System.out.println("testAopException");
        throw new RuntimeException("testAopException");
    }


}
