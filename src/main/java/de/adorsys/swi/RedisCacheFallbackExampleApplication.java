package de.adorsys.swi;

import de.adorsys.swi.example.Item;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
@EnableCaching
public class RedisCacheFallbackExampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(RedisCacheFallbackExampleApplication.class, args);
  }

  @Bean
  JedisConnectionFactory jedisConnectionFactory() {
    return new JedisConnectionFactory();
  }

  @Bean
  RedisTemplate<String, Item> redisTemplate() {
    RedisTemplate<String, Item> redisTemplate = new RedisTemplate<String, Item>();
    redisTemplate.setConnectionFactory(jedisConnectionFactory());
    return redisTemplate;
  }
}
