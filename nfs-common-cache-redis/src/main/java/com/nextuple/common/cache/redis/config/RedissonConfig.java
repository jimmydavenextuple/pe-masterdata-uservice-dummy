package com.nextuple.common.cache.redis.config;

import com.nextuple.controltower.common.config.conditions.RedissonRequiredCondition;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.stream.Collectors;

@Configuration
@Conditional({RedissonRequiredCondition.class})
public class RedissonConfig {

    private final RedisProperties redisProperties;

    public RedissonConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Profile({"!prod"})
    @Bean("redissonClient")
    public RedissonClient nonProdRedissonClient() {
        Config config = new Config();
        String[] addresses = new String[redisProperties.getCluster().getNodes().size()];
        addresses =
                redisProperties.getCluster().getNodes().stream()
                        .map(x -> "redis://" + x)
                        .collect(Collectors.toList())
                        .toArray(addresses);
        config.useClusterServers().addNodeAddress(addresses).setPassword(redisProperties.getPassword());

        return Redisson.create(config);
    }

    @Profile({"prod"})
    @Bean("redissonClient")
    public RedissonClient prodRedissonClient() {
        Config config = new Config();
        config
                .useSingleServer()
                .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setPassword(redisProperties.getPassword())
                .setDatabase(redisProperties.getDatabase());
        return Redisson.create(config);
    }
}