package group5.sebm.Maintenance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.Maintenance.controller.MechanicanMaintenanceRecordController;
import group5.sebm.Maintenance.controller.dto.MechanicRecordQueryDto;
import group5.sebm.Maintenance.controller.dto.MechanicanQueryDto;
import group5.sebm.Maintenance.controller.dto.MechanicanUpdateDto;
import group5.sebm.Maintenance.controller.vo.MechanicanMaintenanceRecordVo;
import group5.sebm.Maintenance.service.services.MechanicanMaintenanceRecordService;
import group5.sebm.common.BaseResponse;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MechanicanMaintenanceRecordControllerTest {

  @Mock
  private MechanicanMaintenanceRecordService service;

  @InjectMocks
  private MechanicanMaintenanceRecordController controller;

  @Mock
  private HttpServletRequest request;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testAddMaintenanceTask() {
    Long mechanicId = 10L;
    Long userMaintenanceRecordId = 100L;
    when(service.addMaintenanceTask(mechanicId, userMaintenanceRecordId)).thenReturn(1L);

    BaseResponse<Long> response = controller.addMaintenanceTask(userMaintenanceRecordId, mechanicId);

    assertNotNull(response);
    assertEquals(1L, response.getData());
    verify(service).addMaintenanceTask(mechanicId, userMaintenanceRecordId);
  }

  @Test
  void testListMyTasks_Success() {
    Long mechanicId = 10L;
    when(request.getAttribute("userId")).thenReturn(mechanicId);

    MechanicanQueryDto queryDto = new MechanicanQueryDto();
    queryDto.setPageNumber(1);
    queryDto.setPageSize(10);

    Page<MechanicanMaintenanceRecordVo> page = new Page<>();
    page.setRecords(Collections.singletonList(new MechanicanMaintenanceRecordVo()));
    page.setTotal(1);

    when(service.listMechanicMaintenanceRecords(mechanicId, queryDto)).thenReturn(page);

    BaseResponse<Page<MechanicanMaintenanceRecordVo>> response = controller.listMyTasks(queryDto, request);

    assertNotNull(response);
    assertEquals(1, response.getData().getRecords().size());
    verify(service).listMechanicMaintenanceRecords(mechanicId, queryDto);
  }

  @Test
  void testListMyTasks_NotLoggedIn() {
    when(request.getAttribute("userId")).thenReturn(null);
    MechanicanQueryDto queryDto = new MechanicanQueryDto();

    BusinessException ex = assertThrows(BusinessException.class,
        () -> controller.listMyTasks(queryDto, request));

    assertEquals(ErrorCode.NOT_LOGIN_ERROR.getCode(), ex.getCode());
  }

  @Test
  void testGetRecordDetail_Success() {
    MechanicRecordQueryDto queryDto = new MechanicRecordQueryDto();
    MechanicanMaintenanceRecordVo vo = new MechanicanMaintenanceRecordVo();
    when(service.getMechanicMaintenanceRecordDetail(queryDto)).thenReturn(vo);

    BaseResponse<MechanicanMaintenanceRecordVo> response = controller.getRecordDetail(queryDto);

    assertNotNull(response);
    assertEquals(vo, response.getData());
    verify(service).getMechanicMaintenanceRecordDetail(queryDto);
  }

  @Test
  void testUpdateTaskStatus_Success() {
    Long mechanicId = 10L;
    when(request.getAttribute("userId")).thenReturn(mechanicId);

    MechanicanUpdateDto updateDto = new MechanicanUpdateDto();
    updateDto.setId(1L);
    updateDto.setStatus(2);

    when(service.updateMechanicMaintenanceRecord(mechanicId, updateDto)).thenReturn(true);

    BaseResponse<Boolean> response = controller.updateTaskStatus(updateDto, request);

    assertNotNull(response);
    assertTrue(response.getData());
    verify(service).updateMechanicMaintenanceRecord(mechanicId, updateDto);
  }

  @Test
  void testUpdateTaskStatus_NotLoggedIn() {
    when(request.getAttribute("userId")).thenReturn(null);
    MechanicanUpdateDto updateDto = new MechanicanUpdateDto();

    BusinessException ex = assertThrows(BusinessException.class,
        () -> controller.updateTaskStatus(updateDto, request));

    assertEquals(ErrorCode.NOT_LOGIN_ERROR.getCode(), ex.getCode());
  }
}
