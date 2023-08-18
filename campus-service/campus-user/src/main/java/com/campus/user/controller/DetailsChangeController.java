package com.campus.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.common.util.R;
import com.campus.user.domain.DetailsChange;
import com.campus.user.service.DetailsChangeService;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @auther xiaolin
 * @create 2023/8/18 16:09
 */
@RestController
@RequestMapping("/detailsChange")
@Log4j2
@Api(tags = "零钱明细接口")
public class DetailsChangeController {

    @Autowired
    private DetailsChangeService detailsChangeService;


    //添加零钱明细记录
    @PostMapping("/addDetailsChange")
    public R addDetailsChange(Map<String, Object> map) {
        DetailsChange detailsChange = new DetailsChange();
        detailsChange.setUid((String) map.get("uid"));
        detailsChange.setMoney((BigDecimal) map.get("money"));
        detailsChange.setType((Integer) map.get("type"));
        detailsChange.setRemark((String) map.get("remark"));
        detailsChange.setBalance((BigDecimal) map.get("balance"));
        detailsChange.setAvatar((String) map.get("avatar"));
        boolean save = detailsChangeService.save(detailsChange);
        if (save) {
            return R.ok(null,"添加零钱明细成功");
        } else {
            return R.failed("添加零钱明细失败");
        }
    }

    //根据用户id查询零钱明细
    @PostMapping("/getDetailsChangeByUid")
    public R getDetailsChangeByUid(@RequestHeader("uid") String uid) {
        QueryWrapper<DetailsChange> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        //按照时间降序排列
        queryWrapper.orderByDesc("create_time");
        if (detailsChangeService.list(queryWrapper).size() == 0) {
            return R.failed("该用户暂时没有零钱明细");
        }
        return R.ok(detailsChangeService.list(queryWrapper),"查询零钱明细成功");
    }



}
