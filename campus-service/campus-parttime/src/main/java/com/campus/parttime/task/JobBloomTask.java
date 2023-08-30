package com.campus.parttime.task;

import com.campus.common.bloomFilter.BloomFilterService;
import com.campus.parttime.dao.JobDao;
import com.campus.parttime.domain.Job;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: chb
 * @Date: 2023/08/30/20:43
 * @Description:
 */
@Component
public class JobBloomTask {

    @Autowired
    private JobDao jobDao;

    @Autowired
    private BloomFilterService bloomFilterService;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void task(){
        try {
            List<String> idList = jobDao.list();
            for (String id : idList) {
                String key = "job" + id;
                bloomFilterService.bloomSet(key);
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(JobBloomTask.class).error("JobBloomTask定时器出错");
        }
    }

}
