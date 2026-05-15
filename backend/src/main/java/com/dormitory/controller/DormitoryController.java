package com.dormitory.controller;

import com.dormitory.entity.*;
import com.dormitory.service.DormitoryService;
import com.dormitory.service.ScheduledTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DormitoryController {

    @Autowired
    private DormitoryService dormitoryService;

    @Autowired
    private ScheduledTasks scheduledTasks;

    @PostMapping("/students")
    public Student createStudent(@RequestBody Student student) {
        return dormitoryService.createStudent(student);
    }

    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return dormitoryService.getAllStudents();
    }

    @PostMapping("/dormitories")
    public Dormitory createDormitory(@RequestBody Dormitory dormitory) {
        return dormitoryService.createDormitory(dormitory);
    }

    @GetMapping("/dormitories")
    public List<Dormitory> getAllDormitories() {
        return dormitoryService.getAllDormitories();
    }

    @PostMapping("/allocations")
    public AllocationApplication applyAllocation(@RequestBody AllocationApplication application) {
        return dormitoryService.applyAllocation(application);
    }

    @GetMapping("/allocations")
    public List<AllocationApplication> getAllAllocationApplications() {
        return dormitoryService.getAllAllocationApplications();
    }

    @PostMapping("/allocations/{id}/process")
    public AllocationApplication processAllocation(@PathVariable String id) {
        return dormitoryService.processAllocation(id);
    }

    @PostMapping("/transfers")
    public TransferApplication applyTransfer(@RequestBody TransferApplication application) {
        return dormitoryService.applyTransfer(application);
    }

    @GetMapping("/transfers")
    public List<TransferApplication> getAllTransferApplications() {
        return dormitoryService.getAllTransferApplications();
    }

    @PostMapping("/transfers/{id}/process")
    public TransferApplication processTransfer(@PathVariable String id) {
        return dormitoryService.processTransfer(id);
    }

    @PostMapping("/violations")
    public ViolationRecord recordViolation(@RequestBody ViolationRecord record) {
        return dormitoryService.recordViolation(record);
    }

    @GetMapping("/violations")
    public List<ViolationRecord> getAllViolations() {
        return dormitoryService.getAllViolations();
    }

    @PostMapping("/maintenance/start")
    public MaintenanceRecord startMaintenance(@RequestBody Map<String, String> request) {
        return dormitoryService.startMaintenance(
            request.get("dormitoryId"), 
            request.get("reason"),
            request.get("temporaryDormitoryId")
        );
    }

    @PostMapping("/maintenance/{id}/end")
    public MaintenanceRecord endMaintenance(@PathVariable String id) {
        return dormitoryService.endMaintenance(id);
    }

    @PostMapping("/students/{id}/graduate")
    public void graduateStudent(@PathVariable String id) {
        dormitoryService.graduateStudent(id);
    }

    @PostMapping("/students/graduate/grade/{grade}")
    public Map<String, Object> graduateStudentsByGrade(@PathVariable String grade) {
        return dormitoryService.graduateStudentsByGrade(grade);
    }

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        return scheduledTasks.getLastStatistics();
    }
}