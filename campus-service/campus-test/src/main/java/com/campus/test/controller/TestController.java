package com.campus.test.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.test.dao.TestDao;
import com.campus.test.domain.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    ServiceCenter serviceCenter;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    TestDao testDao;

    @GetMapping("/test/{num}")
    public R test(@PathVariable Integer num) throws InterruptedException {
//        for (int i = 100; i < 120; i++) {
//            Test test = new Test("test"+i, i);
//            serviceCenter.insert(test);
//        }
        Map<String,Object> map = new HashMap(){{
           put("create_time","2023-07-12 21:27:46#2023-07-12 21:27:56");
        }};
        List<Test> list =  serviceCenter.search(map,Test.class);
        return R.ok(list);
    }

    @PostMapping("/test2")
    public R test2(@RequestBody Map<String,Object> map){
        List<Test> list =  serviceCenter.search(map,Test.class);
        return R.ok(list);
    }

}
