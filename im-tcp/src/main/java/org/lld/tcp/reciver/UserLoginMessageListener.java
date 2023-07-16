package org.lld.tcp.reciver;

import com.alibaba.fastjson.JSONObject;

import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.lld.codec.entity.MessagePack;
import org.lld.common.ClientType;
import org.lld.common.constant.Constants;
import org.lld.common.enums.DeviceMultiLoginEnum;
import org.lld.common.enums.command.SystemCommand;
import org.lld.common.model.UserClientDto;
import org.lld.tcp.redis.redisManager;
import org.lld.tcp.utils.SessionSocketHolder;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @description:
 * 多端同步：1单端登录：一端在线：踢掉除了本clinetType + imel 的设备
 *          2双端登录：允许pc/mobile 其中一端登录 + web端 踢掉除了本clinetType + imel 以外的web端设备
 *        3 三端登录：允许手机+pc+web，踢掉同端的其他imei 除了web
 *        4 不做任何处理
 *
 * @author: lld
 * @version: 1.0
 */
public class UserLoginMessageListener {

    private final static Logger logger = LoggerFactory.getLogger(UserLoginMessageListener.class);

    private Integer loginModel;

    public UserLoginMessageListener(Integer loginModel) {
        this.loginModel = loginModel;
    }

    public void listenerUserLogin(){
        RTopic topic = redisManager.getRedissonClient().getTopic(Constants.RedisConstants.UserLoginChannel);
        topic.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence charSequence, String msg) {
                logger.info("收到用户上线通知：" + msg);
                UserClientDto dto = JSONObject.parseObject(msg, UserClientDto.class);
                List<NioSocketChannel> nioSocketChannels = SessionSocketHolder.get(dto.getAppId(), dto.getUserId());

                for (NioSocketChannel nioSocketChannel : nioSocketChannels) {
                    if(loginModel == DeviceMultiLoginEnum.ONE.getLoginMode()){
                        Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
                        String imei = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.Imei)).get();

                        if(!(clientType + ":" + imei).equals(dto.getClientType()+":"+dto.getImei())){
                            //服务器给用户端发送下线通知
                            MessagePack<Object> pack = new MessagePack<>();
                            pack.setToId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                            pack.setUserId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                            pack.setCommand(SystemCommand.MUTUALLOGIN.getCommand());
                            nioSocketChannel.writeAndFlush(pack);
                        }

                    }else if(loginModel == DeviceMultiLoginEnum.TWO.getLoginMode()){
                        if(dto.getClientType() == ClientType.WEB.getCode()){
                            continue;
                        }
                        Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();

                        if (clientType == ClientType.WEB.getCode()){
                            continue;
                        }
                        String imei = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.Imei)).get();
                        if(!(clientType + ":" + imei).equals(dto.getClientType()+":"+dto.getImei())){
                            MessagePack<Object> pack = new MessagePack<>();
                            pack.setToId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                            pack.setUserId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                            pack.setCommand(SystemCommand.MUTUALLOGIN.getCommand());
                            nioSocketChannel.writeAndFlush(pack);
                        }

                    }else if(loginModel == DeviceMultiLoginEnum.THREE.getLoginMode()){

                        Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
                        String imei = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.Imei)).get();
                        if(dto.getClientType() == ClientType.WEB.getCode()){
                            continue;
                        }

                        Boolean isSameClient = false;
                        if((clientType == ClientType.IOS.getCode() ||
                                clientType == ClientType.ANDROID.getCode()) &&
                                (dto.getClientType() == ClientType.IOS.getCode() ||
                                        dto.getClientType() == ClientType.ANDROID.getCode())){
                            isSameClient = true;
                        }

                        if((clientType == ClientType.MAC.getCode() ||
                                clientType == ClientType.WINDOWS.getCode()) &&
                                (dto.getClientType() == ClientType.MAC.getCode() ||
                                        dto.getClientType() == ClientType.WINDOWS.getCode())){
                            isSameClient = true;
                        }

                        if(isSameClient && !(clientType + ":" + imei).equals(dto.getClientType()+":"+dto.getImei())){
                            MessagePack<Object> pack = new MessagePack<>();
                            pack.setToId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                            pack.setUserId((String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                            pack.setCommand(SystemCommand.MUTUALLOGIN.getCommand());
                            nioSocketChannel.writeAndFlush(pack);
                        }
                    }
                }


            }
        });
    }
}
