package org.lld.tcp.utils;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.lld.codec.entity.MessageHeader;
import org.lld.common.constant.Constants;
import org.lld.common.enums.ImConnectStatusEnum;
import org.lld.common.model.UserClientDto;
import org.lld.common.model.userSession;
import org.lld.tcp.redis.redisManager;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SessionSocketHolder {
    private  static  final Map<UserClientDto , NioSocketChannel> CHANNELS = new ConcurrentHashMap<>();

    public static void put(Integer appId,String userId,Integer clientType,
                           String imei
            ,NioSocketChannel channel){
        UserClientDto dto = new UserClientDto();
        dto.setImei(imei);
        dto.setAppId(appId);
        dto.setClientType(clientType);
        dto.setUserId(userId);
        CHANNELS.put(dto,channel);
    }

    public static NioSocketChannel get(Integer appId,String userId,
                                       Integer clientType,String imei){
        UserClientDto dto = new UserClientDto();
        dto.setImei(imei);
        dto.setAppId(appId);
        dto.setClientType(clientType);
        dto.setUserId(userId);
        return CHANNELS.get(dto);
    }

    public static List<NioSocketChannel> get(Integer appId , String id) {

        Set<UserClientDto> channelInfos = CHANNELS.keySet();
        List<NioSocketChannel> channels = new ArrayList<>();

        channelInfos.forEach(channel ->{
            if(channel.getAppId().equals(appId) && id.equals(channel.getUserId())){
                channels.add(CHANNELS.get(channel));
            }
        });

        return channels;
    }
    public static void remove(Integer appId,String userId,Integer clientType,String imei){
        UserClientDto dto = new UserClientDto();
        dto.setAppId(appId);
        dto.setImei(imei);
        dto.setClientType(clientType);
        dto.setUserId(userId);
        CHANNELS.remove(dto);
    }

    public static void removeUserSession(NioSocketChannel nioSocketChannel){
        //用户退出登录
        String userId = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get();
        Integer appId = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.AppId)).get();
        Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
        String imei = (String) nioSocketChannel
                .attr(AttributeKey.valueOf(Constants.Imei)).get();

        SessionSocketHolder.remove(appId,userId,clientType,imei);
        RedissonClient redissonClient = redisManager.getRedissonClient();
        RMap<Object, Object> map = redissonClient.getMap(appId +
                Constants.RedisConstants.UserSessionConstants + userId);
        map.remove(clientType+":"+imei);
        nioSocketChannel.close();
    }

    public static void offlineUserSession(NioSocketChannel nioSocketChannel){
        //离线不删除redis里用户session,只是修改状态
        String userId = (String) nioSocketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get();
        Integer appId = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.AppId)).get();
        Integer clientType = (Integer) nioSocketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
        String imei = (String) nioSocketChannel
                .attr(AttributeKey.valueOf(Constants.Imei)).get();
        SessionSocketHolder.remove(appId,userId,clientType,imei);
        RedissonClient redissonClient = redisManager.getRedissonClient();
        RMap<String, String> map = redissonClient.getMap(appId +
                Constants.RedisConstants.UserSessionConstants + userId);
        String sessionStr = map.get(clientType.toString()+":" + imei);

        if(!StringUtils.isBlank(sessionStr)){
            userSession userSession = JSONObject.parseObject(sessionStr, org.lld.common.model.userSession.class);
            userSession.setConnectState(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
            //修改用户session状态后再放入redis
            map.put(clientType.toString()+":"+imei,JSONObject.toJSONString(userSession));
        }

    }

}
