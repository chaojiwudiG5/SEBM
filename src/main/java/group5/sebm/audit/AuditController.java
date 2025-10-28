package group5.sebm.audit;

import group5.sebm.audit.dto.*;
import group5.sebm.audit.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditService auditService;

    @Autowired
    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/overview")
    public ResponseEntity<OverviewDto> overview() {
        OverviewDto dto = auditService.getOverview();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/devices")
    public ResponseEntity<PagedResponse> listDevices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) Boolean overdueOnly,
            @RequestParam(required = false) String sort
    ) {
        PagedResponse resp = auditService.listDevices(page, size);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/devices/{id}")
    public ResponseEntity<?> getDevice(@PathVariable String id) {
        AuditDeviceDto device = auditService.getDevice(id);
        if (device == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Not Found", null));
        }
        return ResponseEntity.ok(device);
    }

    @PostMapping("/borrow-requests")
    public ResponseEntity<?> submitBorrowRequest(@RequestBody BorrowRequestDto request) {
        Map<String, Object> result = auditService.submitBorrowRequest(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/devices/export")
    public ResponseEntity<byte[]> exportDevices(@RequestParam(defaultValue = "csv") String format) {
        byte[] data = auditService.exportDevices(format);
        String filename = "devices." + ("xlsx".equals(format) ? "xlsx" : "csv");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @GetMapping("/maintenance")
    public ResponseEntity<PagedResponse> maintenance(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end
    ) {
        PagedResponse resp = auditService.listMaintenance(page, size);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/maintenance/stats")
    public ResponseEntity<List<MaintenanceStatsItemDto>> maintenanceStats(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(defaultValue = "deviceType") String groupBy
    ) {
        List<MaintenanceStatsItemDto> stats = auditService.getMaintenanceStats(start, end, groupBy);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/borrow/stats")
    public ResponseEntity<List<BorrowStatsItemDto>> borrowStats(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) String groupBy,
            @RequestParam(defaultValue = "10") int top
    ) {
        List<BorrowStatsItemDto> stats = auditService.getBorrowStats(start, end, groupBy, top);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/personnel/top")
    public ResponseEntity<List<PersonnelTopItemDto>> personnelTop(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(defaultValue = "10") int top
    ) {
        List<PersonnelTopItemDto> list = auditService.getPersonnelTop(start, end, top);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/export-job")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<ExportJobDto> createExportJob(@RequestBody Map<String, Object> body) {
        ExportJobDto job = auditService.createExportJob(body);
        return ResponseEntity.accepted().body(job);
    }

    @GetMapping("/export-job/{id}/status")
    public ResponseEntity<ExportJobDto> exportJobStatus(@PathVariable String id) {
        ExportJobDto job = auditService.getExportJobStatus(id);
        if (job == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ExportJobDto(id, "not_found", null));
        }
        return ResponseEntity.ok(job);
    }
}

