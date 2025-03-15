package com.kxdkcf.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kxdkcf.enity.Topic;
import com.kxdkcf.enity.User;
import com.kxdkcf.mapper.*;
import com.kxdkcf.utils.hot.HotPostEvaluator;
import com.kxdkcf.utils.hot.RandomUtil;
import com.kxdkcf.utils.hot.ThemeRatingSystem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.task
 * Author:              wenhao
 * CreateTime:          2025-03-25  14:50
 * Description:         TODO
 * Version:             1.0
 */
@Component
@RequiredArgsConstructor
public class MyTask {

    private final TopicMapper topicMapper;
    private final HotPostEvaluator hotPostEvaluator;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final LikeMapper likeMapper;
    private final ReplyMapper replyMapper;
    private final RelateMapper relateMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Async("taskExecutor")
    @Scheduled(cron = "${kxdkcf.task.corn}", zone = "${kxdkcf.task.zone}")
    public void updateHotTopic() {
        double likeAverage, collectAverage, commentAverage;
        double likeStandardDeviation, collectStandardDeviation, commentStandardDeviation;
        System.out.println("任务1执行");
        //查询所有主题帖的收藏，点赞，和评论数
        List<Topic> topicList = topicMapper.selectCount();
        double size = topicList.size(); //总数
        if (size <= 0) {
            return;
        }
        likeAverage = topicList.parallelStream().mapToDouble(Topic::getLikeCount).average().orElse(0.0);
        collectAverage = topicList.parallelStream().mapToDouble(Topic::getFavoritesNumber).average().orElse(0.0);
        commentAverage = topicList.parallelStream().mapToDouble(Topic::getReplyCount).average().orElse(0.0);

        double likeVariance = topicList.parallelStream()
                .mapToDouble(t -> Math.pow(t.getLikeCount() - likeAverage, 2))
                .sum() / size;
        likeStandardDeviation = Math.sqrt(likeVariance);
        double collectVariance = topicList.parallelStream()
                .mapToDouble(t -> Math.pow(t.getFavoritesNumber() - collectAverage, 2))
                .sum() / size;
        collectStandardDeviation = Math.sqrt(collectVariance);
        double commentVariance = topicList.parallelStream()
                .mapToDouble(t -> Math.pow(t.getReplyCount() - commentAverage, 2))
                .sum() / size;
        commentStandardDeviation = Math.sqrt(commentVariance);

        // 2. 准备统计数据（均值 和 标准差）
        Map<String, Double[]> stats = new HashMap<>();
        stats.put("likes", new Double[]{likeAverage, likeStandardDeviation});    // 点赞均值，标准差
        stats.put("collects", new Double[]{collectAverage, collectStandardDeviation});   // 收藏均值，标准差
        stats.put("comments", new Double[]{collectAverage, commentStandardDeviation});   // 评论均值，标准差

        topicList.forEach(topic -> {

            Map<String, Double> result = hotPostEvaluator
                    .calculateHotness(topic.getLikeCount(), topic.getFavoritesNumber(), topic.getReplyCount(), stats);
            topic.setIsHot(hotPostEvaluator.isHotPost(result.get("totalScore")));
            topic.setUpdateTime(LocalDateTime.now());
            topic.setLikeCount(null);
            topic.setFavoritesNumber(null);
            topic.setReplyCount(null);

        });
        topicMapper.update(topicList);

    }

    @Async("taskExecutor")
    @Scheduled(cron = "${kxdkcf.task.corn-relate}", zone = "${kxdkcf.task.zone}")
    public void updateRelate() {
        System.out.println("任务2执行");
        //分别查询出收藏，点赞，回复的最大值
        Map<String, Object> fav = topicMapper.selectMaxFav();
        BigInteger fav_max = (BigInteger) fav.get("fav_max");
        Map<String, Object> like = topicMapper.selectMaxLike();
        BigInteger like_max = (BigInteger) like.get("like_max");
        Map<String, Object> reply = topicMapper.selectMaxReply();
        BigInteger reply_max = (BigInteger) reply.get("reply_max");
        //查询所有用户
        List<User> users = null;
        //先去redis缓存查找
        String s = stringRedisTemplate.opsForValue().get("user_all");
        if (s != null && !s.trim().isEmpty()) {
            try {
                users = objectMapper.readValue(s, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            users = userMapper.selectAllUser();
            //存入缓存
            String s1 = null;
            try {
                s1 = objectMapper.writeValueAsString(users);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            stringRedisTemplate.opsForValue().set("user_all", s1, 10, TimeUnit.DAYS);
        }
        List<Topic> topics = topicMapper.selectAllTopic();

        users.forEach(user -> {

            //根据用户id查询对所有主题的点赞，收藏，回复数
            topics.forEach(topic -> {

                //查询有多少回复数
                Long count = replyMapper.getCount(topic.getId(), user.getId());
                if (count == null) {
                    count = 0L;
                }
                Boolean is_liked;
                Boolean is_collected;
                Map<String, Object> map = likeMapper.selectLikeAndFav(topic.getId(), user.getId());
                if (map == null) {

                    is_liked = false;
                    is_collected = false;
                } else {
                    is_liked = (Boolean) map.get("is_liked");
                    is_collected = (Boolean) map.get("is_collected");
                }
                double score = ThemeRatingSystem.calculateRating(is_collected, is_liked, count);
                double score1 = score + RandomUtil.createRandom();
                if (score1 >= 5 || score1 <= 0) {
                    score1 = score;
                }
                relateMapper.update(topic.getId(), user.getId(), score1);
            });


        });

    }
}
