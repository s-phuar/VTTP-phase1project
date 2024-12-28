package VTTPmini.mini_project.repository;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import VTTPmini.mini_project.model.Stock;

@Repository
public class StockRepository {
    @Autowired @Qualifier("redis-object")
    private RedisTemplate<String, Object> template;

    
    //HKEYS {key}
    public Set<String> getAllStocks(String key){
        HashOperations<String, String, Object> hashOps = template.opsForHash();
        return hashOps.keys(key);
    }

    public void saveStock(String key, String ticker, Stock stock){
        HashOperations<String, String, Object> hashOps = template.opsForHash();
        hashOps.put(key, ticker, stock);
    }

    public void deleteStock(String email, String ticker){
        HashOperations<String, String, Object> hashOps = template.opsForHash();
        hashOps.delete(email, ticker);
    }

    public Stock getStock(String key, String ticker) {
        HashOperations<String, String, Object> hashOps = template.opsForHash();
        return (Stock) hashOps.get(key, ticker);
    }

    public List<Object> getAllStockObj(String email){
        HashOperations<String, String, Object> hashOps = template.opsForHash();
        List<Object> listObj = hashOps.values(email);
        return listObj;
    }


}
