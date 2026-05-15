package com.dormitory.entity;

import java.time.LocalDateTime;

public class ViolationRecord {
    private String id;
    private String studentId;
    private String dormitoryId;
    private String violationType;
    private String description;
    private LocalDateTime violationTime;
    private LocalDateTime createTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getDormitoryId() { return dormitoryId; }
    public void setDormitoryId(String dormitoryId) { this.dormitoryId = dormitoryId; }
    public String getViolationType() { return violationType; }
    public void setViolationType(String violationType) { this.violationType = violationType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getViolationTime() { return violationTime; }
    public void setViolationTime(LocalDateTime violationTime) { this.violationTime = violationTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}