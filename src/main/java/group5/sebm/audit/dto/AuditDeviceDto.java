package group5.sebm.audit.dto;

import java.util.List;

public class AuditDeviceDto {
    public String id;
    public String deviceId;
    public String deviceName;
    public String deviceType;
    public String department;
    public String user;
    public String borrowTime;
    public String expectedReturn;
    public String returnTime;
    public String status;
    public Boolean isOverdue;
    public Integer overdueDays;
    public List<MaintenanceRecordDto> maintenanceRecords;

    public AuditDeviceDto() {
    }
}

