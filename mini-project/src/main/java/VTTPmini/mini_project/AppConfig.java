package VTTPmini.mini_project;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class AppConfig {
    
    private final Logger logger = Logger.getLogger(AppConfig.class.getName());

    //injects values from the applcation.properties file into the fields
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.database}")
    private int redisDatabase;

    @Value("${spring.data.redis.username}")
    private String redisUsername;

    @Value("${spring.data.redis.password}")
    private String redisPassword;


    @Bean("redis-object")
    public RedisTemplate<String, Object> createRedisTemplateObject(){
        //create basic redis database connection paramaters like host/port/database etc.
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        //sets the database index - redisDatabase has been hardcoded as 0 in application.properties
        config.setDatabase(redisDatabase);
        //set the username and password if they are set
        if(!redisUsername.trim().equals("")){
            logger.info("Setting Redis username and password: ");
            config.setUsername(redisUsername);
            config.setPassword(redisPassword);
        }

        //create a connection to the database
        JedisClientConfiguration jedisClient = JedisClientConfiguration.builder().build();

        //create factory to connect to Redis, with the configuration provided earlier
        JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
        jedisFac.afterPropertiesSet();

        //create the Redis template
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisFac);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    //a method to create redis template, named redis-0, assuming all values are strings
    @Bean("redis-string")
    public RedisTemplate<String, Object> createRedisTemplate(){
        //create database configuration
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        //sets the database - equivalent to select 0 in redis-cli
        config.setDatabase(redisDatabase);
        //set the username and password if they are set
        if(!redisUsername.trim().equals("")){
            logger.info("Setting Redit username and password: ");
            config.setUsername(redisUsername);
            config.setPassword(redisPassword);
        }

        //create a connection to the database
        JedisClientConfiguration jedisClient = JedisClientConfiguration.builder().build();

        //create factory to connect to Redis
        JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
        jedisFac.afterPropertiesSet();

        //create the Redis template
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisFac);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }


}
