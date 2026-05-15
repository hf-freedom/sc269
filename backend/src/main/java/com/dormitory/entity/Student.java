package com.dormitory.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Student {
    private String id;
    private String name;
    private String studentNo;
    private String college;
    private String grade;
    private String gender;
    private String className;
    private String dormitoryId;
    private boolean hasViolation;
    private List<String> preferredRoommates = new ArrayList<>();
    private List<String> conflictRoommates = new ArrayList<>();
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getDormitoryId() { return dormitoryId; }
    public void setDormitoryId(String dormitoryId) { this.dormitoryId = dormitoryId; }
    public boolean isHasViolation() { return hasViolation; }
    public void setHasViolation(boolean hasViolation) { this.hasViolation = hasViolation; }
    public List<String> getPreferredRoommates() { return preferredRoommates; }
    public void setPreferredRoommates(List<String> preferredRoommates) { this.preferredRoommates = preferredRoommates; }
    public List<String> getConflictRoommates() { return conflictRoommates; }
    public void setConflictRoommates(List<String> conflictRoommates) { this.conflictRoommates = conflictRoommates; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}