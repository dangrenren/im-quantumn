package org.lld.tcp.handler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.lld.codec.entity.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.lld.codec.pack.LoginPack;
import org.lld.common.constant.Constants;
import org.lld.common.enums.ImConnectStatusEnum;
import org.lld.common.model.UserClientDto;
import org.lld.common.model.userSession;

import org.lld.tcp.redis.redisManager;
import org.lld.tcp.utils.SessionSocketHolder;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.LoggerFactory;
import org.lld.common.enums.command.SystemCommand;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetAddress;


public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {
    @Value("${BootstrapConfig.TcpConfig.brokerId}")
    private Integer brokerId;

    private final static Logger logger =  LoggerFactory.getLogger(NettyServerHandler.class);
   // @Autowired
    //public org.lld.tcp.redis.redisManager redisManager;
    //@Autowired
   // private org.lld.tcp.start.tcpStart tcpStart;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
       // System.out.println("收到消息：" + msg);
        Integer command = msg.getMessageHeader().getCommand();
        //登录命令command
        if(command==SystemCommand.LOGIN.getCommand() ){
            //如果是登录命令，就把session 信息保存下来
            LoginPack loginPack = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()), LoginPack.class);
            System.out.println("登录信息 id："+loginPack);
            //将channel设置属性
            //为channel设置userId
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(loginPack.getUserId());
            String clientImei = msg.getMessageHeader().getClientType() + ":" + msg.getMessageHeader().getImei();
            /** 为channel设置appId **/
            ctx.channel().attr(AttributeKey.valueOf(Constants.AppId)).set(msg.getMessageHeader().getAppId());
            /** 为channel设置ClientType **/
            ctx.channel().attr(AttributeKey.valueOf(Constants.ClientType))
                    .set(msg.getMessageHeader().getClientType());
            /** 为channel设置Imei **/
            ctx.channel().attr(AttributeKey.valueOf(Constants.Imei))
                    .set(msg.getMessageHeader().getImei());

            //将用户登录信息用redis map 保存
            userSession userSession = new userSession();
            userSession.setAppId(msg.getMessageHeader().getAppId());
            userSession.setClientType(msg.getMessageHeader().getClientType());
            userSession.setUserId(loginPack.getUserId());
            userSession.setVersion(msg.getMessageHeader().getVersion());
            userSession.setConnectState(ImConnectStatusEnum.ONLINE_STATUS.getCode());
            userSession.setImei(msg.getMessageHeader().getImei());
            userSession.setBrokerId(brokerId);
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            userSession.setBrokerHost(hostAddress);


            RedissonClient redissonClient = redisManager.getRedissonClient();
            RMap<String, String> map = redissonClient.getMap(msg.getMessageHeader().getAppId() + Constants.RedisConstants.UserSessionConstants + loginPack.getUserId());
            /*
            map.put(msg.getMessageHeader().getClientType()+":" + msg.getMessageHeader().getImei()
                    ,JSONObject.toJSONString(userSession));
            */
            map.put(msg.getMessageHeader().getClientType()+":"
                    ,JSONObject.toJSONString(userSession));

            //将用户登录信息再用map保存
            SessionSocketHolder.put(msg.getMessageHeader().getAppId(),loginPack.getUserId(),msg.getMessageHeader().getClientType(),msg.getMessageHeader().getImei(),(NioSocketChannel) ctx.channel());

            UserClientDto dto = new UserClientDto();
            dto.setImei(msg.getMessageHeader().getImei());
            dto.setUserId(loginPack.getUserId());
            dto.setClientType(msg.getMessageHeader().getClientType());
            dto.setAppId(msg.getMessageHeader().getAppId());
            RTopic topic = redissonClient.getTopic(Constants.RedisConstants.UserLoginChannel);
            topic.publish(JSONObject.toJSONString(dto));

        } else if (command==SystemCommand.LOGOUT.getCommand()) {
            //删除map里的seesion
            //删除redis里的session
            SessionSocketHolder.removeUserSession((NioSocketChannel) ctx.channel());
        } else if (command==SystemCommand.PING.getCommand()) {
            //把最后一次心跳时间更新设置进去
            ctx.channel().attr(AttributeKey.valueOf(Constants.ReadTime)).set(System.currentTimeMillis());
        }
    }
}
