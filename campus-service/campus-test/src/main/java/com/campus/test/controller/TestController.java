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
        Map<String,Object> map = new HashMap(){{
           put("create_time","2023-07-12 21:27:46 2023-07-12 21:27:56");
        }};
        List<Test> list =  serviceCenter.search(map,Test.class);
        return R.ok(list);
    }

    @PostMapping("/test2")
    public R test2(@RequestBody Map<String,Object> map){
        List<Test> list =  serviceCenter.search(map,Test.class);
        return R.ok(list);
    }

    @GetMapping("/test3")
    public R test3(){
        String id = "1679120366271119362";
        serviceCenter.increment(id,Test.class,true,"testNumber");
        return R.ok();
    }

    @GetMapping("/testing")
    public R testing() {




        return R.ok();
    }


}
