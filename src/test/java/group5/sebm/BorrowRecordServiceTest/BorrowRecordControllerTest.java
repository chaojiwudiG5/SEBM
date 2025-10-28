package group5.sebm.BorrowRecordServiceTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.BorrowRecord.controller.BorrowRecordController;
import group5.sebm.BorrowRecord.controller.dto.*;
import group5.sebm.BorrowRecord.controller.vo.BorrowRecordVo;
import group5.sebm.BorrowRecord.entity.BorrowRecordPo;
import group5.sebm.BorrowRecord.service.BorrowRecordServiceImpl;
import group5.sebm.BorrowRecord.service.services.BorrowRecordService;
import group5.sebm.Device.entity.DevicePo;
import group5.sebm.User.controller.dto.UserDto;
import group5.sebm.common.BaseResponse;
import group5.sebm.exception.ErrorCode;
import group5.sebm.notifiation.controller.dto.SendNotificationDto;
import group5.sebm.notifiation.service.impl.NotificationServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BorrowRecordControllerTest {

    @InjectMocks
    private BorrowRecordController borrowRecordController;

    @Mock
    private BorrowRecordServiceImpl borrowRecordService;

    @Mock
    private HttpServletRequest request;


    @Mock
    private group5.sebm.BorrowRecord.dao.BorrowRecordMapper borrowRecordMapper;

    @Mock
    private group5.sebm.Device.service.services.DeviceService deviceService;

    @Mock
    private NotificationServiceImpl notificationService;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testBorrowDevice() {
        BorrowRecordAddDto addDto = new BorrowRecordAddDto();
        BorrowRecordVo vo = new BorrowRecordVo();
        vo.setId(1L);

        when(request.getAttribute("userId")).thenReturn(100L);
        when(borrowRecordService.borrowDevice(addDto, 100L)).thenReturn(vo);

        BaseResponse<BorrowRecordVo> response = borrowRecordController.borrowDevice(addDto, request);

        assertNotNull(response.getData());
        assertEquals(1L, response.getData().getId());
        verify(borrowRecordService, times(1)).borrowDevice(addDto, 100L);
    }

    @Test
    public void testReturnDevice() {
        BorrowRecordReturnDto returnDto = new BorrowRecordReturnDto();
        BorrowRecordVo vo = new BorrowRecordVo();
        vo.setId(2L);

        when(request.getAttribute("userId")).thenReturn(100L);
        when(borrowRecordService.returnDevice(returnDto, 100L)).thenReturn(vo);

        BaseResponse<BorrowRecordVo> response = borrowRecordController.returnDevice(returnDto, request);

        assertNotNull(response.getData());
        assertEquals(2L, response.getData().getId());
        verify(borrowRecordService, times(1)).returnDevice(returnDto, 100L);
    }

    @Test
    public void testGetBorrowRecordList() {
        BorrowRecordQueryDto queryDto = new BorrowRecordQueryDto();
        queryDto.setUserId(100L);
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);

        BorrowRecordVo vo = new BorrowRecordVo();
        vo.setId(3L);
        Page<BorrowRecordVo> page = new Page<>();
        page.setRecords(Collections.singletonList(vo));

        when(borrowRecordService.getBorrowRecordList(queryDto)).thenReturn(page);

        BaseResponse<List<BorrowRecordVo>> response = borrowRecordController.getBorrowRecordList(queryDto);

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals(3L, response.getData().get(0).getId());
        verify(borrowRecordService, times(1)).getBorrowRecordList(queryDto);
    }

    @Test
    public void testGetBorrowRecordListWithStatus() {
        BorrowRecordQueryWithStatusDto queryDto = new BorrowRecordQueryWithStatusDto();
        queryDto.setUserId(100L);
        queryDto.setStatus(0);
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);

        BorrowRecordVo vo = new BorrowRecordVo();
        vo.setId(4L);
        Page<BorrowRecordVo> page = new Page<>();
        page.setRecords(Collections.singletonList(vo));

        when(borrowRecordService.getBorrowRecordListWithStatus(queryDto)).thenReturn(page);

        BaseResponse<List<BorrowRecordVo>> response = borrowRecordController.getBorrowRecordListWithStatus(queryDto);

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals(4L, response.getData().get(0).getId());
        verify(borrowRecordService, times(1)).getBorrowRecordListWithStatus(queryDto);
    }
    
}
