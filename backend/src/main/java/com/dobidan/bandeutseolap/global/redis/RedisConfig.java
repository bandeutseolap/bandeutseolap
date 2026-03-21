package com.dobidan.bandeutseolap.global.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/*
* StringRedisTemplate : Redis에 문자열 데이터를 저장/조회하는 도구
* RedisConnectionFactory : application.yml에서 설정한 정보로 Redis 연결해줌
* */

@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate (RedisConnectionFactory connectionFactory){
        return new StringRedisTemplate(connectionFactory);
    }
}
