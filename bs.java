import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Set;

public class LeaderboardService {
    private Jedis jedis  = new Jedis(redisHost);

    // 更新玩家积分
    public void updateScore(String playerId, double score) {
        jedis.zadd("player_rank", score, playerId);
    }

    // 获取玩家当前排名
    public long getRank(String playerId) {
        Long rank = jedis.zrevrank("player_rank", playerId);
        return (rank != null) ? rank + 1 : -1;
    }

    // 获取前 N 名玩家
    public Set<Tuple> getTopN(int n) {
        return jedis.zrevrangeWithScores("player_rank", 0, n - 1);
    }

    // 获取自己名次前后共 N 名玩家
    public Set> getSurroundingRanks(String playerId, int n) {
        long rank = getRank(playerId);
        if (rank == -1) {
            return null;
        }

        long start = Math.max(0, rank - n - 1);
        long end = rank + n - 1;

        return jedis.zrevrangeWithScores("player_rank", start, end);
    }
}
