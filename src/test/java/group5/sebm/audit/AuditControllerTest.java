package group5.sebm.audit;

import group5.sebm.audit.dto.*;
import group5.sebm.audit.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuditController单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("审计控制器测试")
class AuditControllerTest {

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuditController auditController;

    private OverviewDto mockOverviewDto;
    private PagedResponse mockPagedResponse;
    private AuditDeviceDto mockAuditDeviceDto;

    @BeforeEach
    void setUp() {
        // 初始化Mock数据
        mockOverviewDto = new OverviewDto();
        mockOverviewDto.totalDevices = 100;
        mockOverviewDto.currentlyBorrowed = 50;
        mockOverviewDto.currentlyInMaintenance = 5;

        mockPagedResponse = new PagedResponse();
        mockPagedResponse.total = 100;
        mockPagedResponse.page = 1;
        mockPagedResponse.size = 20;
        mockPagedResponse.items = Arrays.asList(new Object(), new Object());

        mockAuditDeviceDto = new AuditDeviceDto();
        mockAuditDeviceDto.id = "1";
        mockAuditDeviceDto.deviceId = "1";
        mockAuditDeviceDto.deviceName = "测试设备";
    }

    @Test
    @DisplayName("测试获取概览数据 - 成功")
    void testOverview_Success() {
        // Arrange
        when(auditService.getOverview()).thenReturn(mockOverviewDto);

        // Act
        ResponseEntity<OverviewDto> response = auditController.overview();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100, response.getBody().totalDevices);
        assertEquals(50, response.getBody().currentlyBorrowed);
        verify(auditService, times(1)).getOverview();
    }

    @Test
    @DisplayName("测试获取设备列表 - 成功")
    void testListDevices_Success() {
        // Arrange
        when(auditService.listDevices(anyInt(), anyInt())).thenReturn(mockPagedResponse);

        // Act
        ResponseEntity<PagedResponse> response = auditController.listDevices(
            1, 20, null, null, null, null, null, null, null, null
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100, response.getBody().total);
        assertEquals(2, response.getBody().items.size());
        verify(auditService, times(1)).listDevices(1, 20);
    }

    @Test
    @DisplayName("测试获取设备列表 - 使用默认分页参数")
    void testListDevices_DefaultParams() {
        // Arrange
        when(auditService.listDevices(anyInt(), anyInt())).thenReturn(mockPagedResponse);

        // Act
        ResponseEntity<PagedResponse> response = auditController.listDevices(
            1, 20, null, null, null, null, null, null, null, null
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(auditService, times(1)).listDevices(1, 20);
    }

    @Test
    @DisplayName("测试获取单个设备详情 - 成功")
    void testGetDevice_Success() {
        // Arrange
        String deviceId = "1";
        when(auditService.getDevice(deviceId)).thenReturn(mockAuditDeviceDto);

        // Act
        ResponseEntity<?> response = auditController.getDevice(deviceId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof AuditDeviceDto);
        AuditDeviceDto body = (AuditDeviceDto) response.getBody();
        assertEquals("测试设备", body.deviceName);
        verify(auditService, times(1)).getDevice(deviceId);
    }

    @Test
    @DisplayName("测试获取单个设备详情 - 设备不存在")
    void testGetDevice_NotFound() {
        // Arrange
        String deviceId = "999";
        when(auditService.getDevice(deviceId)).thenReturn(null);

        // Act
        ResponseEntity<?> response = auditController.getDevice(deviceId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals(404, errorResponse.code);
        assertEquals("Not Found", errorResponse.message);
        verify(auditService, times(1)).getDevice(deviceId);
    }

    @Test
    @DisplayName("测试提交借用请求 - 成功")
    void testSubmitBorrowRequest_Success() {
        // Arrange
        BorrowRequestDto requestDto = new BorrowRequestDto();
        requestDto.deviceId = "1";
        requestDto.user = "测试用户";
        
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("success", true);
        mockResult.put("borrowRecordId", 123L);
        
        when(auditService.submitBorrowRequest(any(BorrowRequestDto.class))).thenReturn(mockResult);

        // Act
        ResponseEntity<?> response = auditController.submitBorrowRequest(requestDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        verify(auditService, times(1)).submitBorrowRequest(any(BorrowRequestDto.class));
    }

    @Test
    @DisplayName("测试导出设备数据 - CSV格式")
    void testExportDevices_CSV() {
        // Arrange
        byte[] mockData = "deviceId,deviceName\n1,Test Device\n".getBytes();
        when(auditService.exportDevices("csv")).thenReturn(mockData);

        // Act
        ResponseEntity<byte[]> response = auditController.exportDevices("csv");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(mockData, response.getBody());
        assertNotNull(response.getHeaders().getContentDisposition());
        verify(auditService, times(1)).exportDevices("csv");
    }

    @Test
    @DisplayName("测试导出设备数据 - XLSX格式")
    void testExportDevices_XLSX() {
        // Arrange
        byte[] mockData = new byte[]{0x50, 0x4B}; // Mock XLSX magic number
        when(auditService.exportDevices("xlsx")).thenReturn(mockData);

        // Act
        ResponseEntity<byte[]> response = auditController.exportDevices("xlsx");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(mockData, response.getBody());
        verify(auditService, times(1)).exportDevices("xlsx");
    }

    @Test
    @DisplayName("测试获取维护记录列表 - 成功")
    void testMaintenance_Success() {
        // Arrange
        when(auditService.listMaintenance(anyInt(), anyInt())).thenReturn(mockPagedResponse);

        // Act
        ResponseEntity<PagedResponse> response = auditController.maintenance(
            1, 20, null, null, null, null
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100, response.getBody().total);
        verify(auditService, times(1)).listMaintenance(1, 20);
    }

    @Test
    @DisplayName("测试获取维护统计数据 - 成功")
    void testMaintenanceStats_Success() {
        // Arrange
        List<MaintenanceStatsItemDto> mockStats = Arrays.asList(
            new MaintenanceStatsItemDto(),
            new MaintenanceStatsItemDto()
        );
        // 修复：使用isNull()来匹配null参数
        when(auditService.getMaintenanceStats(isNull(), isNull(), eq("deviceType")))
            .thenReturn(mockStats);

        // Act
        ResponseEntity<List<MaintenanceStatsItemDto>> response = 
            auditController.maintenanceStats(null, null, "deviceType");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(auditService, times(1)).getMaintenanceStats(isNull(), isNull(), eq("deviceType"));
    }

    @Test
    @DisplayName("测试获取借用统计数据 - 成功")
    void testBorrowStats_Success() {
        // Arrange
        List<BorrowStatsItemDto> mockStats = Arrays.asList(
            new BorrowStatsItemDto(),
            new BorrowStatsItemDto()
        );
        // 修复：使用isNull()来匹配null参数
        when(auditService.getBorrowStats(isNull(), isNull(), isNull(), eq(10)))
            .thenReturn(mockStats);

        // Act
        ResponseEntity<List<BorrowStatsItemDto>> response = 
            auditController.borrowStats(null, null, null, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(auditService, times(1)).getBorrowStats(isNull(), isNull(), isNull(), eq(10));
    }

    @Test
    @DisplayName("测试获取人员借用排行 - 成功")
    void testPersonnelTop_Success() {
        // Arrange
        List<PersonnelTopItemDto> mockTop = Arrays.asList(
            new PersonnelTopItemDto(),
            new PersonnelTopItemDto()
        );
        // 修复：使用isNull()来匹配null参数
        when(auditService.getPersonnelTop(isNull(), isNull(), eq(10)))
            .thenReturn(mockTop);

        // Act
        ResponseEntity<List<PersonnelTopItemDto>> response = 
            auditController.personnelTop(null, null, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(auditService, times(1)).getPersonnelTop(isNull(), isNull(), eq(10));
    }

    @Test
    @DisplayName("测试创建导出任务 - 成功")
    void testCreateExportJob_Success() {
        // Arrange
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("format", "csv");
        requestBody.put("filters", new HashMap<>());
        
        ExportJobDto mockJobDto = new ExportJobDto();
        mockJobDto.jobId = "job-123";
        mockJobDto.status = "pending";
        
        when(auditService.createExportJob(any(Map.class))).thenReturn(mockJobDto);

        // Act
        ResponseEntity<ExportJobDto> response = auditController.createExportJob(requestBody);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("job-123", response.getBody().jobId);
        assertEquals("pending", response.getBody().status);
        verify(auditService, times(1)).createExportJob(any(Map.class));
    }

    @Test
    @DisplayName("测试获取导出任务状态 - 成功")
    void testExportJobStatus_Success() {
        // Arrange
        String jobId = "job-123";
        ExportJobDto mockJobDto = new ExportJobDto();
        mockJobDto.jobId = jobId;
        mockJobDto.status = "completed";
        
        when(auditService.getExportJobStatus(jobId)).thenReturn(mockJobDto);

        // Act
        ResponseEntity<ExportJobDto> response = auditController.exportJobStatus(jobId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(jobId, response.getBody().jobId);
        assertEquals("completed", response.getBody().status);
        verify(auditService, times(1)).getExportJobStatus(jobId);
    }

    @Test
    @DisplayName("测试获取概览数据 - 服务返回空数据")
    void testOverview_EmptyData() {
        // Arrange
        OverviewDto emptyDto = new OverviewDto();
        emptyDto.totalDevices = 0;
        emptyDto.currentlyBorrowed = 0;
        when(auditService.getOverview()).thenReturn(emptyDto);

        // Act
        ResponseEntity<OverviewDto> response = auditController.overview();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().totalDevices);
        assertEquals(0, response.getBody().currentlyBorrowed);
    }

    @Test
    @DisplayName("测试获取设备列表 - 空列表")
    void testListDevices_EmptyList() {
        // Arrange
        PagedResponse emptyResponse = new PagedResponse();
        emptyResponse.total = 0;
        emptyResponse.items = Arrays.asList();
        when(auditService.listDevices(anyInt(), anyInt())).thenReturn(emptyResponse);

        // Act
        ResponseEntity<PagedResponse> response = auditController.listDevices(
            1, 20, null, null, null, null, null, null, null, null
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().total);
        assertTrue(response.getBody().items.isEmpty());
    }
}

