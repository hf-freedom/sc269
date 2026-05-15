package com.dormitory.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceRecord {
    private String id;
    private String dormitoryId;
    private String reason;
    private String status;
    private List<String> migratedStudentIds = new ArrayList<>();
    private String temporaryDormitoryId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDormitoryId() { return dormitoryId; }
    public void setDormitoryId(String dormitoryId) { this.dormitoryId = dormitoryId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<String> getMigratedStudentIds() { return migratedStudentIds; }
    public void setMigratedStudentIds(List<String> migratedStudentIds) { this.migratedStudentIds = migratedStudentIds; }
    public String getTemporaryDormitoryId() { return temporaryDormitoryId; }
    public void setTemporaryDormitoryId(String temporaryDormitoryId) { this.temporaryDormitoryId = temporaryDormitoryId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}