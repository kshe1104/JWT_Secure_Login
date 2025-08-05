package org.project.securelogin.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtTokenRedisRepository {

    private static final String BLACKLIST_KEY_PREFIX = "jwt:blacklist";
    private final RedisTemplate<String, String> redisTemplate;

    //주어진 JWT토큰을 블랙리스트에 추가한다. 이미 추가된 경우->false, 성공적으로 추가하면 true 반환
    public boolean addTokenToBlackList(String tokenId, long expireInSeconds) {
        try{
            String key = BLACKLIST_KEY_PREFIX + tokenId;
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "true", expireInSeconds, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(result);
        } catch(Exception e){
            return false;
        }

    }

    public boolean isTokenBlackListed(String tokenId) {
        if (redisTemplate == null) {
            return false;
        }
        String key = BLACKLIST_KEY_PREFIX + tokenId;
        return redisTemplate.hasKey(key);
    }

}
