package VTTPmini.mini_project.repository;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LoginRepository {
    @Autowired @Qualifier("redis-string")
    private RedisTemplate<String, Object> template;


    //HKEYS {key}
    public Set<String> getAllEmails(String key){
        HashOperations<String, String, Object> hashOps = template.opsForHash();
        return hashOps.keys(key);
    }

    //HGET {key} {email}
    public String getPassword (String key, String email){
        HashOperations<String, String, Object> hashOps = template.opsForHash();
        return (String) hashOps.get(key, email);
    }

    //HSET {key} {email} {password}
    public void saveCredentials(String email, String password){
        HashOperations<String, String, Object> hashOps = template.opsForHash();
        hashOps.put("credentials", email, password);   
    }


}
