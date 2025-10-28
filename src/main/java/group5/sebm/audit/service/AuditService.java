package group5.sebm.audit.service;

import group5.sebm.audit.dto.*;

import java.util.List;
import java.util.Map;

public interface AuditService {
    OverviewDto getOverview();

    PagedResponse listDevices(int page, int size);

    AuditDeviceDto getDevice(String id);

    Map<String, Object> submitBorrowRequest(BorrowRequestDto request);

    byte[] exportDevices(String format);

    PagedResponse listMaintenance(int page, int size);

    List<MaintenanceStatsItemDto> getMaintenanceStats(String start, String end, String groupBy);

    List<BorrowStatsItemDto> getBorrowStats(String start, String end, String groupBy, int top);

    List<PersonnelTopItemDto> getPersonnelTop(String start, String end, int top);

    ExportJobDto createExportJob(Map<String, Object> body);

    ExportJobDto getExportJobStatus(String id);
}

