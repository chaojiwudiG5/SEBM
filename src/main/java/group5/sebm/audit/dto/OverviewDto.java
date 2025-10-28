package group5.sebm.audit.dto;

import java.util.List;

public class OverviewDto {
    public int totalDevices;
    public int currentlyBorrowed;
    public int currentlyInMaintenance;
    public int totalBorrowInPeriod;
    public int totalMaintenanceInPeriod;
    public List<BorrowStatsItemDto> topBorrowDepartments;
    public List<PersonnelTopItemDto> topBorrowUsers;

    public OverviewDto() {
    }
}

