package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

//自定义定时任务类
@Slf4j
@Component
public class MyTask {
    //每隔5秒触发一次
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeTask() {
        log.info("任务开始执行：{}",new Date());
    }
}
