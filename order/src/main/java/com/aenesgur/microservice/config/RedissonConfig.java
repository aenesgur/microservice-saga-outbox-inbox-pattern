package com.aenesgur.microservice.config;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RedissonConfig {

    @Value("${spring.redisson.singleServerConfig.address}")
    private String ADDRESS;

    @Value("${spring.redisson.singleServerConfig.timeout}")
    private int TIMEOUT;


    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(ADDRESS)
                .setTimeout(TIMEOUT);

        return Redisson.create(config);
    }

}