package kg.alymzhan.petchatgpt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public Set<String> findKeysByPattern(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public Long deleteAllKeys(Set<String> keys) {
        return redisTemplate.delete(keys);
    }

    public Long saveKeys(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    public List<String> getObjectsByKey(String key) {
        return redisTemplate.opsForList().range(key, -10, -1);
    }

}
