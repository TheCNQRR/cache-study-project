package by.java.enterprise.service;

import by.java.enterprise.model.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
public class ChatCacheService {

    private static final String KEY_PATTERN = "chat:%d:messages";
    private static final Duration TTL = Duration.ofMinutes(10);

    private final RedisTemplate<String, Object> redisTemplate;

    public ChatCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void pushMessage(Long chatId, Message msg) {
        String key = KEY_PATTERN.formatted(chatId);
        redisTemplate.opsForList().leftPush(key, msg);
        redisTemplate.expire(key, TTL);
    }

    public List<Message> lastMessages(Long chatId, int count) {
        String key = KEY_PATTERN.formatted(chatId);
        List<Object> raw = redisTemplate.opsForList().range(key, 0, count - 1);
        if (raw == null) return Collections.emptyList();
        return raw.stream()
                .map(o -> (Message) o)
                .toList();
    }

    public void evictChat(Long chatId) {
        redisTemplate.delete(KEY_PATTERN.formatted(chatId));
    }
}
