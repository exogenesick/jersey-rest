package pl.allegro.kitten.permalinks.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PermalinkRepository {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String findDestinationUrlBySourceUrl(String sourceUrl) {
        return redisTemplate.opsForValue().get(sourceUrl);
    }

    public void save(String sourceUrl, String destinationUrl) {
        redisTemplate.opsForValue().set(sourceUrl, destinationUrl);
    }

    public void deleteBySourceUrl(String sourceUrl) {
        redisTemplate.delete(sourceUrl);
    }
}
