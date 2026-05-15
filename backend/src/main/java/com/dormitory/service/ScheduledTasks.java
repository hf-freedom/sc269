package com.dormitory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ScheduledTasks {

    @Autowired
    private DormitoryService dormitoryService;

    private Map<String, Object> lastStatistics;

    @Scheduled(fixedRate = 60000)
    public void runStatisticsTask() {
        lastStatistics = dormitoryService.getStatistics();
        System.out.println("[" + LocalDateTime.now() + "] 定时统计任务执行: " + lastStatistics);
    }

    public Map<String, Object> getLastStatistics() {
        if (lastStatistics == null) {
            lastStatistics = dormitoryService.getStatistics();
        }
        return lastStatistics;
    }
}