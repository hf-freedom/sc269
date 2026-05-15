package com.dormitory.entity;

import java.time.LocalDateTime;

public class TransferApplication {
    private String id;
    private String studentId;
    private String fromDormitoryId;
    private String toDormitoryId;
    private String targetStudentId;
    private String status;
    private String reason;
    private LocalDateTime applyTime;
    private LocalDateTime processTime;
    private String remarks;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getFromDormitoryId() { return fromDormitoryId; }
    public void setFromDormitoryId(String fromDormitoryId) { this.fromDormitoryId = fromDormitoryId; }
    public String getToDormitoryId() { return toDormitoryId; }
    public void setToDormitoryId(String toDormitoryId) { this.toDormitoryId = toDormitoryId; }
    public String getTargetStudentId() { return targetStudentId; }
    public void setTargetStudentId(String targetStudentId) { this.targetStudentId = targetStudentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getApplyTime() { return applyTime; }
    public void setApplyTime(LocalDateTime applyTime) { this.applyTime = applyTime; }
    public LocalDateTime getProcessTime() { return processTime; }
    public void setProcessTime(LocalDateTime processTime) { this.processTime = processTime; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}