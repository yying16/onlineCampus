package com.campus.message.component;

import com.campus.message.dao.MatchDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MatchTimeRefreshJob {

    @Autowired
    MatchDao matchDao;

    //每天0点刷新匹配次数
    @Scheduled(cron = "0 0 0 * * *")
    public void refreshMatchTime() {
        log.info("刷新用户的匹配次数");
        matchDao.refreshMatchTime();
        log.info("刷新成功");
    }
}