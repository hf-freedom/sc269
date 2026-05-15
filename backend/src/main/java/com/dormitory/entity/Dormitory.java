package com.dormitory.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Dormitory {
    private String id;
    private String buildingNo;
    private String roomNo;
    private Integer capacity;
    private Integer occupiedBeds;
    private String gender;
    private String college;
    private String grade;
    private String status;
    private boolean underMaintenance;
    private String temporaryDormitoryId;
    private List<String> studentIds = new ArrayList<>();
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBuildingNo() { return buildingNo; }
    public void setBuildingNo(String buildingNo) { this.buildingNo = buildingNo; }
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Integer getOccupiedBeds() { return occupiedBeds; }
    public void setOccupiedBeds(Integer occupiedBeds) { this.occupiedBeds = occupiedBeds; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isUnderMaintenance() { return underMaintenance; }
    public void setUnderMaintenance(boolean underMaintenance) { this.underMaintenance = underMaintenance; }
    public String getTemporaryDormitoryId() { return temporaryDormitoryId; }
    public void setTemporaryDormitoryId(String temporaryDormitoryId) { this.temporaryDormitoryId = temporaryDormitoryId; }
    public List<String> getStudentIds() { return studentIds; }
    public void setStudentIds(List<String> studentIds) { this.studentIds = studentIds; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}