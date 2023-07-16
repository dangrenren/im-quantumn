package org.lld.tcp.redis;

import org.lld.tcp.reciver.UserLoginMessageListener;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class redisManager {
    @Value("${BootstrapConfig.TcpConfig.loginModel}")
    private  Integer loginModel;
    private static RedissonClient redissonClient;
    @Autowired
    private SingleClientStrategy singleClientStrategy;

    public  void  init(){
        redissonClient = singleClientStrategy.getRedissonClient();
        UserLoginMessageListener userLoginMessageListener = new UserLoginMessageListener(loginModel);
        userLoginMessageListener.listenerUserLogin();
    }

    public static RedissonClient getRedissonClient(){
        return redissonClient;
    }
}
