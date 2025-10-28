package group5.sebm.Maintenance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.Maintenance.controller.UserMaintenanceRecordController;
import group5.sebm.Maintenance.controller.dto.UserCreateDto;
import group5.sebm.Maintenance.controller.dto.UserQueryDto;
import group5.sebm.Maintenance.controller.vo.UserMaintenanceRecordVo;
import group5.sebm.Maintenance.service.services.UserMaintenanceRecordService;
import group5.sebm.common.BaseResponse;
import group5.sebm.common.dto.DeleteDto;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserMaintenanceRecordControllerTest {

    @Mock
    private UserMaintenanceRecordService service;

    @InjectMocks
    private UserMaintenanceRecordController controller;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateMaintenanceRecord_Success() {
        Long userId = 10L;
        when(request.getAttribute("userId")).thenReturn(userId);

        UserCreateDto createDto = new UserCreateDto();
        UserMaintenanceRecordVo vo = new UserMaintenanceRecordVo();
        when(service.createMaintenanceRecord(userId, createDto)).thenReturn(vo);

        BaseResponse<UserMaintenanceRecordVo> response = controller.createMaintenanceRecord(createDto, request);

        assertNotNull(response);
        assertEquals(vo, response.getData());
        verify(service).createMaintenanceRecord(userId, createDto);
    }

    @Test
    void testCreateMaintenanceRecord_NotLoggedIn() {
        when(request.getAttribute("userId")).thenReturn(null);
        UserCreateDto createDto = new UserCreateDto();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> controller.createMaintenanceRecord(createDto, request));

        assertEquals(ErrorCode.NOT_LOGIN_ERROR.getCode(), ex.getCode());
    }

    @Test
    void testListMyRecords_Success() {
        Long userId = 10L;
        when(request.getAttribute("userId")).thenReturn(userId);

        UserQueryDto queryDto = new UserQueryDto();
        Page<UserMaintenanceRecordVo> page = new Page<>();
        UserMaintenanceRecordVo vo = new UserMaintenanceRecordVo();
        page.setRecords(Collections.singletonList(vo));
        page.setTotal(1);

        when(service.listUserMaintenanceRecords(userId, queryDto)).thenReturn(page);

        BaseResponse<List<UserMaintenanceRecordVo>> response = controller.listMyRecords(queryDto, request);

        assertNotNull(response);
        assertEquals(1, response.getData().size());
        assertEquals(vo, response.getData().get(0));
        verify(service).listUserMaintenanceRecords(userId, queryDto);
    }

    @Test
    void testListMyRecords_NotLoggedIn() {
        when(request.getAttribute("userId")).thenReturn(null);
        UserQueryDto queryDto = new UserQueryDto();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> controller.listMyRecords(queryDto, request));

        assertEquals(ErrorCode.NOT_LOGIN_ERROR.getCode(), ex.getCode());
    }

    @Test
    void testGetRecordDetail_Success() {
        Long userId = 10L;
        when(request.getAttribute("userId")).thenReturn(userId);

        Long recordId = 1L;
        UserMaintenanceRecordVo vo = new UserMaintenanceRecordVo();
        when(service.getUserMaintenanceRecordDetail(userId, recordId)).thenReturn(vo);

        BaseResponse<UserMaintenanceRecordVo> response = controller.getRecordDetail(recordId, request);

        assertNotNull(response);
        assertEquals(vo, response.getData());
        verify(service).getUserMaintenanceRecordDetail(userId, recordId);
    }

    @Test
    void testGetRecordDetail_NotLoggedIn() {
        when(request.getAttribute("userId")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> controller.getRecordDetail(1L, request));

        assertEquals(ErrorCode.NOT_LOGIN_ERROR.getCode(), ex.getCode());
    }

    @Test
    void testCancelRecord_Success() {
        Long userId = 10L;
        when(request.getAttribute("userId")).thenReturn(userId);

        DeleteDto deleteDto = new DeleteDto();
        deleteDto.setId(1L);

        when(service.cancelMaintenanceRecord(userId, deleteDto.getId())).thenReturn(true);

        BaseResponse<Boolean> response = controller.cancelRecord(deleteDto, request);

        assertNotNull(response);
        assertTrue(response.getData());
        verify(service).cancelMaintenanceRecord(userId, deleteDto.getId());
    }

    @Test
    void testCancelRecord_NotLoggedIn() {
        when(request.getAttribute("userId")).thenReturn(null);
        DeleteDto deleteDto = new DeleteDto();
        deleteDto.setId(1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> controller.cancelRecord(deleteDto, request));

        assertEquals(ErrorCode.NOT_LOGIN_ERROR.getCode(), ex.getCode());
    }
}
