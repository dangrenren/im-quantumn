package org.lld.tcp.redis;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: lld
 * @version: 1.0
 */
@Component
public class SingleClientStrategy {
//配置单机策略
    @Value("${redis.single.address}")
    private String address;
    @Value("${redis.database}")
    private int database;
    @Value("${redis.timeout}")
    private int timeout;
    @Value("${redis.poolMinIdle}")
    private int poolMinIdle;
    @Value("${redis.poolConnectTimeout}")
    private int poolConnTimeout;
    @Value("${redis.poolSize}")
    private int poolSize;
    @Value("${redis.password}")
    private String password;

    public RedissonClient getRedissonClient() {
        Config config = new Config();
        String node = address;
        node = node.startsWith("redis://") ? node : "redis://" + node;
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(node)
                .setDatabase(database)
                .setTimeout(timeout)
                .setConnectionMinimumIdleSize(poolMinIdle)
                .setConnectTimeout(poolConnTimeout)
                .setConnectionPoolSize(poolSize);
        //if (StringUtils.isNotBlank(password)) {
        //    serverConfig.setPassword(password);
        //}
        StringCodec stringCodec = new StringCodec();
        config.setCodec(stringCodec);
        return Redisson.create(config);
    }

}
