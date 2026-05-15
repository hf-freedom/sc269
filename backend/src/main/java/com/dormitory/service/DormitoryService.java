package com.dormitory.service;

import com.dormitory.entity.*;
import com.dormitory.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DormitoryService {

    @Autowired
    private InMemoryStorage storage;

    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    public Student createStudent(Student student) {
        student.setId(generateId());
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        storage.students.put(student.getId(), student);
        return student;
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(storage.students.values());
    }

    public Dormitory createDormitory(Dormitory dormitory) {
        dormitory.setId(generateId());
        dormitory.setOccupiedBeds(0);
        dormitory.setStatus("AVAILABLE");
        dormitory.setCreateTime(LocalDateTime.now());
        dormitory.setUpdateTime(LocalDateTime.now());
        storage.dormitories.put(dormitory.getId(), dormitory);
        return dormitory;
    }

    public List<Dormitory> getAllDormitories() {
        return new ArrayList<>(storage.dormitories.values());
    }

    public AllocationApplication applyAllocation(AllocationApplication application) {
        Student student = storage.students.get(application.getStudentId());
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        if (student.getDormitoryId() != null) {
            throw new RuntimeException("学生已分配宿舍");
        }

        application.setId(generateId());
        application.setStatus("PENDING");
        application.setApplyTime(LocalDateTime.now());
        storage.allocationApplications.put(application.getId(), application);
        return application;
    }

    public AllocationApplication processAllocation(String applicationId) {
        AllocationApplication application = storage.allocationApplications.get(applicationId);
        if (application == null || !"PENDING".equals(application.getStatus())) {
            throw new RuntimeException("申请不存在或已处理");
        }

        Student student = storage.students.get(application.getStudentId());
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        if (student.isHasViolation()) {
            application.setStatus("REJECTED");
            application.setRemarks("违规学生不能申请优先宿舍");
            application.setProcessTime(LocalDateTime.now());
            return application;
        }

        List<Dormitory> matchingDorms = storage.dormitories.values().stream()
                .filter(d -> !d.isUnderMaintenance())
                .filter(d -> d.getOccupiedBeds() < d.getCapacity())
                .filter(d -> d.getGender().equals(student.getGender()))
                .filter(d -> d.getCollege().equals(student.getCollege()))
                .filter(d -> d.getGrade().equals(student.getGrade()))
                .collect(Collectors.toList());

        if (matchingDorms.isEmpty()) {
            matchingDorms = storage.dormitories.values().stream()
                    .filter(d -> !d.isUnderMaintenance())
                    .filter(d -> d.getOccupiedBeds() < d.getCapacity())
                    .filter(d -> d.getGender().equals(student.getGender()))
                    .collect(Collectors.toList());
        }

        matchingDorms.sort((a, b) -> {
            int scoreA = calculateAllocationScore(a, student);
            int scoreB = calculateAllocationScore(b, student);
            return Integer.compare(scoreB, scoreA);
        });

        if (matchingDorms.isEmpty()) {
            application.setStatus("PENDING");
            application.setRemarks("暂无可用宿舍");
            return application;
        }

        Dormitory selectedDorm = matchingDorms.get(0);
        assignStudentToDormitory(student, selectedDorm);

        application.setStatus("APPROVED");
        application.setAllocatedDormitoryId(selectedDorm.getId());
        application.setProcessTime(LocalDateTime.now());

        return application;
    }

    private int calculateAllocationScore(Dormitory dormitory, Student student) {
        int score = 0;

        if (dormitory.getStudentIds().isEmpty()) {
            score += 50;
        }

        for (String sid : dormitory.getStudentIds()) {
            Student s = storage.students.get(sid);
            if (s != null && s.getClassName().equals(student.getClassName())) {
                score += 30;
            }
            if (student.getPreferredRoommates().contains(sid)) {
                score += 20;
            }
            if (student.getConflictRoommates().contains(sid)) {
                score -= 50;
            }
        }

        return score;
    }

    private void assignStudentToDormitory(Student student, Dormitory dormitory) {
        student.setDormitoryId(dormitory.getId());
        student.setUpdateTime(LocalDateTime.now());

        dormitory.getStudentIds().add(student.getId());
        dormitory.setOccupiedBeds(dormitory.getOccupiedBeds() + 1);
        dormitory.setUpdateTime(LocalDateTime.now());

        if (dormitory.getOccupiedBeds().equals(dormitory.getCapacity())) {
            dormitory.setStatus("FULL");
        }
    }

    public TransferApplication applyTransfer(TransferApplication application) {
        Student student = storage.students.get(application.getStudentId());
        if (student == null || student.getDormitoryId() == null) {
            throw new RuntimeException("学生不存在或未分配宿舍");
        }

        Dormitory fromDorm = storage.dormitories.get(student.getDormitoryId());
        Dormitory toDorm = storage.dormitories.get(application.getToDormitoryId());

        if (toDorm == null) {
            throw new RuntimeException("目标宿舍不存在");
        }

        if (toDorm.isUnderMaintenance()) {
            throw new RuntimeException("目标宿舍正在维修");
        }

        if (toDorm.getOccupiedBeds() >= toDorm.getCapacity()) {
            throw new RuntimeException("目标宿舍已满");
        }

        if (!toDorm.getGender().equals(student.getGender())) {
            throw new RuntimeException("宿舍性别不匹配");
        }

        application.setId(generateId());
        application.setFromDormitoryId(student.getDormitoryId());
        application.setStatus("PENDING");
        application.setApplyTime(LocalDateTime.now());
        storage.transferApplications.put(application.getId(), application);
        return application;
    }

    public TransferApplication processTransfer(String applicationId) {
        TransferApplication application = storage.transferApplications.get(applicationId);
        if (application == null || !"PENDING".equals(application.getStatus())) {
            throw new RuntimeException("申请不存在或已处理");
        }

        Student student = storage.students.get(application.getStudentId());
        Dormitory fromDorm = storage.dormitories.get(application.getFromDormitoryId());
        Dormitory toDorm = storage.dormitories.get(application.getToDormitoryId());

        if (toDorm.getOccupiedBeds() >= toDorm.getCapacity()) {
            application.setStatus("REJECTED");
            application.setRemarks("目标宿舍已满");
            application.setProcessTime(LocalDateTime.now());
            return application;
        }

        fromDorm.getStudentIds().remove(student.getId());
        fromDorm.setOccupiedBeds(fromDorm.getOccupiedBeds() - 1);
        fromDorm.setStatus("AVAILABLE");
        fromDorm.setUpdateTime(LocalDateTime.now());

        assignStudentToDormitory(student, toDorm);

        application.setStatus("APPROVED");
        application.setProcessTime(LocalDateTime.now());
        return application;
    }

    public ViolationRecord recordViolation(ViolationRecord record) {
        record.setId(generateId());
        record.setCreateTime(LocalDateTime.now());
        storage.violationRecords.put(record.getId(), record);

        Student student = storage.students.get(record.getStudentId());
        if (student != null) {
            student.setHasViolation(true);
            student.setUpdateTime(LocalDateTime.now());
        }
        return record;
    }

    public List<ViolationRecord> getAllViolations() {
        return new ArrayList<>(storage.violationRecords.values());
    }

    public MaintenanceRecord startMaintenance(String dormitoryId, String reason, String temporaryDormitoryId) {
        Dormitory dormitory = storage.dormitories.get(dormitoryId);
        if (dormitory == null) {
            throw new RuntimeException("宿舍不存在");
        }

        int residentCount = dormitory.getStudentIds().size();
        if (residentCount > 0 && temporaryDormitoryId == null) {
            throw new RuntimeException("有住户的宿舍需要指定临时房间才能开始维修");
        }

        Dormitory tempDorm = null;
        if (temporaryDormitoryId != null) {
            tempDorm = storage.dormitories.get(temporaryDormitoryId);
            if (tempDorm == null) {
                throw new RuntimeException("临时宿舍不存在");
            }
            if (tempDorm.isUnderMaintenance()) {
                throw new RuntimeException("临时宿舍正在维修中");
            }
            if (!tempDorm.getGender().equals(dormitory.getGender())) {
                throw new RuntimeException("临时宿舍性别不匹配");
            }
            int availableBeds = tempDorm.getCapacity() - tempDorm.getOccupiedBeds();
            if (availableBeds < residentCount) {
                throw new RuntimeException("临时宿舍床位不足，需要 " + residentCount + " 个床位，仅余 " + availableBeds + " 个");
            }
        }

        MaintenanceRecord record = new MaintenanceRecord();
        record.setId(generateId());
        record.setDormitoryId(dormitoryId);
        record.setReason(reason);
        record.setStatus("IN_PROGRESS");
        record.setStartTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());

        dormitory.setUnderMaintenance(true);
        dormitory.setUpdateTime(LocalDateTime.now());

        if (tempDorm != null && residentCount > 0) {
            record.setTemporaryDormitoryId(tempDorm.getId());

            List<String> studentIds = new ArrayList<>(dormitory.getStudentIds());
            for (String studentId : studentIds) {
                Student student = storage.students.get(studentId);
                if (student != null) {
                    dormitory.getStudentIds().remove(studentId);
                    assignStudentToDormitory(student, tempDorm);
                    record.getMigratedStudentIds().add(studentId);
                }
            }
            dormitory.setOccupiedBeds(0);
        }

        storage.maintenanceRecords.put(record.getId(), record);
        return record;
    }

    public MaintenanceRecord endMaintenance(String recordId) {
        MaintenanceRecord record = storage.maintenanceRecords.get(recordId);
        if (record == null) {
            throw new RuntimeException("维修记录不存在");
        }

        Dormitory dormitory = storage.dormitories.get(record.getDormitoryId());
        dormitory.setUnderMaintenance(false);
        dormitory.setUpdateTime(LocalDateTime.now());

        record.setStatus("COMPLETED");
        record.setEndTime(LocalDateTime.now());
        return record;
    }

    public void graduateStudent(String studentId) {
        Student student = storage.students.get(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        if (student.getDormitoryId() != null) {
            Dormitory dormitory = storage.dormitories.get(student.getDormitoryId());
            if (dormitory != null) {
                dormitory.getStudentIds().remove(studentId);
                dormitory.setOccupiedBeds(dormitory.getOccupiedBeds() - 1);
                dormitory.setStatus("AVAILABLE");
                dormitory.setUpdateTime(LocalDateTime.now());
            }
        }

        student.setDormitoryId(null);
        student.setUpdateTime(LocalDateTime.now());
    }

    public Map<String, Object> graduateStudentsByGrade(String grade) {
        List<Student> gradeStudents = storage.students.values().stream()
                .filter(s -> grade.equals(s.getGrade()))
                .collect(Collectors.toList());

        int releasedBeds = 0;
        List<String> processedStudents = new ArrayList<>();

        for (Student student : gradeStudents) {
            if (student.getDormitoryId() != null) {
                Dormitory dormitory = storage.dormitories.get(student.getDormitoryId());
                if (dormitory != null) {
                    dormitory.getStudentIds().remove(student.getId());
                    dormitory.setOccupiedBeds(dormitory.getOccupiedBeds() - 1);
                    dormitory.setStatus("AVAILABLE");
                    dormitory.setUpdateTime(LocalDateTime.now());
                    releasedBeds++;
                }
            }
            student.setDormitoryId(null);
            student.setUpdateTime(LocalDateTime.now());
            processedStudents.add(student.getName());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalStudents", gradeStudents.size());
        result.put("releasedBeds", releasedBeds);
        result.put("processedStudents", processedStudents);
        return result;
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long emptyBeds = storage.dormitories.values().stream()
                .mapToLong(d -> d.getCapacity() - d.getOccupiedBeds())
                .sum();

        long pendingApplications = storage.allocationApplications.values().stream()
                .filter(a -> "PENDING".equals(a.getStatus()))
                .count();

        long violationCount = storage.violationRecords.size();

        stats.put("emptyBeds", emptyBeds);
        stats.put("pendingApplications", pendingApplications);
        stats.put("violationCount", violationCount);
        stats.put("totalStudents", storage.students.size());
        stats.put("totalDormitories", storage.dormitories.size());

        return stats;
    }

    public List<AllocationApplication> getAllAllocationApplications() {
        return new ArrayList<>(storage.allocationApplications.values());
    }

    public List<TransferApplication> getAllTransferApplications() {
        return new ArrayList<>(storage.transferApplications.values());
    }
}