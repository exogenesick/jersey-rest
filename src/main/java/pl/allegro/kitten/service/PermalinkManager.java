package pl.allegro.kitten.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import pl.allegro.kitten.model.Permalink;

public class PermalinkManager {

    private StringRedisTemplate redisTemplate;

    @Autowired
    public PermalinkManager(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Permalink get(String sourceUrl) {
        String destinationUrl = redisTemplate.opsForValue().get(sourceUrl);

        if (null == destinationUrl) {
            return null;
        }

        Permalink permalink = new Permalink();
        permalink.setSourceUrl(sourceUrl);
        permalink.setDestinationUrl(destinationUrl);

        return permalink;
    }

    public void set(Permalink permalink) throws Exception {
        if (null == permalink.getSourceUrl() || null == permalink.getDestinationUrl()) {
            throw new Exception("Permalink is not valid.");
        }

        redisTemplate.opsForValue().set(permalink.getSourceUrl(), permalink.getDestinationUrl());
    }

    public void delete(Permalink permalink) {
        redisTemplate.delete(permalink.getSourceUrl());
    }
}
