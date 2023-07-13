package io.github.lunasaw.leaderboard;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Component
public class LeaderBoardServiceImpl implements LeaderBoardService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void updateScore(String user, double score) {
        redisTemplate.opsForZSet().add("leaderboard", user, score);
    }

    @Override
    public String getLeaderboard(int start, int end) {

        Set<ZSetOperations.TypedTuple<String>> tupleSet = redisTemplate.opsForZSet().reverseRangeWithScores("leaderboard", start, end);
        if (tupleSet == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\nLeaderboard : \n");
        AtomicInteger rank = new AtomicInteger(start + 1);
        tupleSet.forEach(tuple -> {
            String user = tuple.getValue();
            Double score = tuple.getScore();

            sb.append(rank.get()).append(". ").append(user).append(" - ").append(score).append("\n");
            rank.getAndIncrement();
        });

        return sb.toString();
    }
}
