package com.kxdkcf.properties;

import com.kxdkcf.utils.hot.PostScoreCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PropertiesTests {

    @Autowired
    PostScoreCalculator postScoreCalculator;

    @Test
    void test() {
        System.out.println(postScoreCalculator.getFAV_WEIGHT());
        System.out.println(postScoreCalculator.getLIKE_WEIGHT());
        System.out.println(postScoreCalculator.getREPLY_WEIGHT());
        System.out.println(postScoreCalculator.getHOT_WEIGHT());
    }

}
