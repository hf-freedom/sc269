package com.dormitory.storage;

import com.dormitory.entity.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryStorage {
    public final ConcurrentHashMap<String, Student> students = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, Dormitory> dormitories = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, AllocationApplication> allocationApplications = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, TransferApplication> transferApplications = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, ViolationRecord> violationRecords = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, MaintenanceRecord> maintenanceRecords = new ConcurrentHashMap<>();
}