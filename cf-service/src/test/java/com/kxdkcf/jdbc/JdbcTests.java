package com.kxdkcf.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kxdkcf.enity.User;
import com.kxdkcf.task.MyTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JdbcTests {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MyTask myTask;

    @Test
    void jdbc1() throws JsonProcessingException {

        Map<String, Object> map = jdbcTemplate.queryForMap("select * from users where id = ?", 26);
        System.out.println(map);
        User user = jdbcTemplate.queryForObject("select * from users where id = ?", new BeanPropertyRowMapper<>(User.class), 26);
        if (user != null) {

            System.out.println(objectMapper.writeValueAsString(user));
        }
    }

    @Test
    void task() {
        myTask.updateHotTopic();

    }

}
