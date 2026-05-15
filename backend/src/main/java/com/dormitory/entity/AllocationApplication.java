package com.dormitory.entity;

import java.time.LocalDateTime;

public class AllocationApplication {
    private String id;
    private String studentId;
    private String preferredBuilding;
    private String preferredRoomType;
    private String status;
    private String allocatedDormitoryId;
    private LocalDateTime applyTime;
    private LocalDateTime processTime;
    private String remarks;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getPreferredBuilding() { return preferredBuilding; }
    public void setPreferredBuilding(String preferredBuilding) { this.preferredBuilding = preferredBuilding; }
    public String getPreferredRoomType() { return preferredRoomType; }
    public void setPreferredRoomType(String preferredRoomType) { this.preferredRoomType = preferredRoomType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAllocatedDormitoryId() { return allocatedDormitoryId; }
    public void setAllocatedDormitoryId(String allocatedDormitoryId) { this.allocatedDormitoryId = allocatedDormitoryId; }
    public LocalDateTime getApplyTime() { return applyTime; }
    public void setApplyTime(LocalDateTime applyTime) { this.applyTime = applyTime; }
    public LocalDateTime getProcessTime() { return processTime; }
    public void setProcessTime(LocalDateTime processTime) { this.processTime = processTime; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}