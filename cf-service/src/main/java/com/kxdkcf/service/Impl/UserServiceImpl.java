package com.kxdkcf.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpStatus;
import com.kxdkcf.Result.Result;
import com.kxdkcf.constant.TokenKeyConstant;
import com.kxdkcf.context.ThreadLocalValueUser;
import com.kxdkcf.dto.UserDTO;
import com.kxdkcf.dto.UserUpdate;
import com.kxdkcf.enity.Dialog;
import com.kxdkcf.enity.Topic;
import com.kxdkcf.enity.User;
import com.kxdkcf.mapper.*;
import com.kxdkcf.properties.JwtProperties;
import com.kxdkcf.service.IUserService;
import com.kxdkcf.utils.jwts.JwtUtils;
import com.kxdkcf.utils.pool.ThreadPool;
import com.kxdkcf.vo.UserVO;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.service.Impl
 * Author:              wenhao
 * CreateTime:          2024-12-27  13:50
 * Description:         TODO
 * Version:             1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final AiMapper aiMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    private final RelateMapper relateMapper;
    private final TopicMapper topicMapper;
    private final ReplyMapper replyMapper;
    private final LikeMapper likeMapper;
    private final HealthMapper healthMapper;
    private final JavaMailSender sender;
    private final StringRedisTemplate stringRedisTemplate;


    public Result getById(Long userId) {
        User user = User.builder().id(userId).createTime(LocalDateTime.of(2024, 12, 27, 14, 41, 18)).build();
        User userDo = userMapper.queryUser(user);
        UserVO userVO = UserVO.builder().id(userDo.getId())
                .userName(userDo.getUserName())
                .avatar(userDo.getAvatar())
                .email(userDo.getEmail())
                .createTime(userDo.getCreateTime())
                .updateTime(userDo.getUpdateTime())
                .build();
        return Result.success(userVO);
    }

    public Result insert(UserDTO userDTO) {
        User user = User.builder().build();
        BeanUtil.copyProperties(userDTO, user);
        String password = userDTO.getPassword();
        String encode = passwordEncoder.encode(password);
        user.setPassword(encode);
        userMapper.insertUser(user);
        log.info("userId: {}", user.getId());
        UserVO userVO = UserVO.builder().build();
        BeanUtil.copyProperties(user, userVO);
        //查询所有主题id
        List<Long> topicIds = topicMapper.getAllTopicId();

        // 异步初始化并插入到relate表
        ExecutorService threadPool = ThreadPool.getThreadPool();
        threadPool.execute(() -> {
            relateMapper.insert(user.getId(), topicIds);
            stringRedisTemplate.delete("relate_user_id");
        });

        stringRedisTemplate.delete("user_all");

        return Result.success(userVO);
    }


    public Result login(UserDTO userDTO, HttpServletResponse response) {

        String userName = userDTO.getUserName();
        if (userName == null || userName.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        String password = userDTO.getPassword();
        //根据用户名查找用户
        User user = userMapper.getByUserName(userName);
        if (user == null) {
            return Result.error("用户不存在!");
        }
        String encode = user.getPassword();
        log.info(password);
        log.info(encode);
        //验证登录密码
        boolean matches = passwordEncoder.matches(password, encode);
        if (!matches) {
            return Result.error("登录密码错误!");
        }
        //验证码是否正确
        String captcha = userDTO.getCaptcha();
        String captcha1 = stringRedisTemplate.opsForValue().get(userName);
        if (captcha1 == null) {
            return Result.error("验证码失效");
        }
        if (!passwordEncoder.matches(captcha, captcha1)) {

            return Result.error("验证码错误");
        }
        //生成token
        Map<String, Object> claims = new ConcurrentHashMap<>();
        claims.put(TokenKeyConstant.USER_ID, user.getId());
        String token = JwtUtils.createJwt(jwtProperties.getUserTokenKey(), claims, jwtProperties.getTtl(), SignatureAlgorithm.HS256);
        //设置响应头
        response.setHeader(TokenKeyConstant.TOKEN, token);
        UserVO userVO = UserVO.builder().build();
        BeanUtil.copyProperties(user, userVO);

        //判断登录用户是否有会话主题，没有，插入新的返回dialogId,有，返回最新id
        // 1.判断登录用户是否有会话主题

        List<Dialog> dialogs = aiMapper.selectByUserId(user.getId());
        if (dialogs == null || dialogs.isEmpty()) {

            Dialog dialog = new Dialog();

            dialog.setUserId(user.getId());

            dialog.setCreateTime(LocalDateTime.now());
            dialog.setUpdateTime(LocalDateTime.now());
            aiMapper.insert(dialog);

            userVO.setDialogId(dialog.getId());

        } else {

            Dialog dialog = dialogs.get(dialogs.size() - 1);

            userVO.setDialogId(dialog.getId());

        }

        stringRedisTemplate.delete(userName);
        return Result.success(userVO);
    }

    public ResponseEntity<?> update(UserUpdate user) {
        //查询用户
        Long userId = ThreadLocalValueUser.getThreadLocalValue(Long.class);
        User user1 = userMapper.getUserAllById(userId);
        if (user1 == null) {
            return ResponseEntity.status(HttpStatus.HTTP_NOT_FOUND)
                    .body(Result.error("用户不存在"));
        }
        //进行密码校验
        String password = user1.getPassword();
        String currentPassword = user.getCurrentPassword();
        log.info(currentPassword);
        log.info(password);
        boolean matches = passwordEncoder.matches(currentPassword, password);
        if (!matches) {

            return ResponseEntity.status(HttpStatus.HTTP_FORBIDDEN).body(Result.error("密码错误"));
        }
        //查询是否存在该用户名的用户
        User user2 = userMapper.getByUserName(user.getUserName());
        if (user2 != null && !user2.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.HTTP_BAD_REQUEST)
                    .body(Result.error("已存在该用户名"));
        }
        String encode = null;
        //更新用户信息
        if (user.getNewPassword() != null && !user.getNewPassword().trim().isEmpty()) {
            encode = passwordEncoder.encode(user.getNewPassword());
        }
        User user3 = new User();
        user3.setPassword(encode);
        user3.setUserName(user.getUserName());
        user3.setEmail(user.getEmail());
        user3.setAvatar(user.getAvatar());
        user3.setId(userId);
        userMapper.updateUser(user3);
        return ResponseEntity.ok(Result.success());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> deleteById(Long userId) {

        //查询此用户的主题
        List<Topic> topicList = topicMapper.selectTopicByUserId(userId);
        List<Long> topicIds = topicList.stream().map(Topic::getId).collect(Collectors.toList());
        //查询用户的会话
        List<Dialog> dialogs = aiMapper.selectByUserId(userId);
        List<Long> dialogIds = dialogs.stream().map(Dialog::getId).collect(Collectors.toList());
        //根据id删除用户表用户
        userMapper.deleteById(userId);
        //删除用户相关数据
        //1.用户相关主题
        topicMapper.deleteByUserId(userId);
        //2.用户回复数据
        replyMapper.deleteByUserId(userId);
        //3.用户回复点赞数据
        replyMapper.deleteReplyLikedByUserId(userId);
        //4.用户相关评分数据
        relateMapper.deleteByTopicIds(topicIds);
        //5.用户主题点赞的相关数据
        likeMapper.deleteByUserId(userId);
        likeMapper.deleteByTopicIds(topicIds);
        //6.用户的健康状态数据
        healthMapper.deleteByUserId(userId);
        //7.用户的会话记录
        aiMapper.deleteByUserId(userId);
        aiMapper.deleteMessageByDialogIds(dialogIds);
        stringRedisTemplate.delete("user_all");
        return ResponseEntity.ok(Result.success());
    }

    /**
     * @param user
     * @return
     */
    public ResponseEntity<?> createCaptcha(UserDTO user) {
        //根据用户名查询邮箱
        String userName = user.getUserName();
        if (userName == null) {
            return ResponseEntity.status(HttpStatus.HTTP_BAD_REQUEST).body(Result.error("用户名不能为空"));
        }
        User user1 = userMapper.getByUserName(userName);
        if (user1 == null || user1.getEmail() == null) {
            return ResponseEntity.status(HttpStatus.HTTP_BAD_REQUEST).body(Result.error("用户邮箱已无效,请重新注册登录！"));
        }
        boolean matches = passwordEncoder.matches(user.getPassword(), user1.getPassword());
        if (!matches) {
            return ResponseEntity.status(HttpStatus.HTTP_NOT_ACCEPTABLE).body(Result.error("密码错误"));
        }
        String captcha = RandomStringUtils.randomAlphanumeric(6);
        long ex = 3;

        try {
            String email = user1.getEmail();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("2073578174@qq.com");
            message.setSubject("授权登录");
            message.setText("欢迎使用个人健康管理系统,您的登录验证码为:" + captcha + ",有效期" + ex + "分钟");
            message.setTo(email);
            String encode = passwordEncoder.encode(captcha);
            stringRedisTemplate.opsForValue().set(userName, encode, ex, TimeUnit.MINUTES);
            sender.send(message);
        } catch (MailException e) {
            stringRedisTemplate.delete(userName);
            log.error("e: ", e);
            return ResponseEntity.status(HttpStatus.HTTP_INTERNAL_ERROR).body(Result.error("发送失败请重试"));
        }

        return ResponseEntity.ok(Result.success());
    }

}
