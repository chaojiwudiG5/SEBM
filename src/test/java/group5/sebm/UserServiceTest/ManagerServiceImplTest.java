package group5.sebm.UserServiceTest;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.User.controller.dto.DeleteDto;
import group5.sebm.User.controller.dto.PageDto;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.dao.UserMapper;
import group5.sebm.User.entity.UserPo;
import group5.sebm.User.service.ManagerServiceImpl;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ManagerServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ManagerServiceImpl managerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 使用 Spring 的测试工具类设置 baseMapper
        ReflectionTestUtils.setField(managerService, "baseMapper", userMapper);
    }

    // ========== deleteBorrower 测试 ==========

    @Test
    void testDeleteBorrower_Success() {
        DeleteDto deleteDto = new DeleteDto();
        deleteDto.setId(1L);

        UserPo mockUser = new UserPo();
        mockUser.setId(1L);

        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(userMapper.deleteById(1L)).thenReturn(1);

        // 添加更详细的调试
        System.out.println("Mock user: " + mockUser);
        System.out.println("DeleteDto ID: " + deleteDto.getId());

        // 检查 baseMapper 是否正确设置
        Object actualBaseMapper = ReflectionTestUtils.getField(managerService, "baseMapper");
        System.out.println("BaseMapper in managerService: " + actualBaseMapper);
        System.out.println("Our mock userMapper: " + userMapper);

        Boolean result = managerService.deleteBorrower(deleteDto);
        System.out.println("Result: " + result);

        assertNotNull(result, "Result should not be null");
        assertTrue(result);

        verify(userMapper, times(1)).selectById(1L);
        verify(userMapper, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteBorrower_UserNotExists() {
        DeleteDto deleteDto = new DeleteDto();
        deleteDto.setId(99L);

        when(userMapper.selectById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> managerService.deleteBorrower(deleteDto));

        assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
        assertEquals("User not exists", ex.getMessage());
    }

    @Test
    void testDeleteBorrower_DeleteFails() {
        DeleteDto deleteDto = new DeleteDto();
        deleteDto.setId(1L);

        UserPo mockUser = new UserPo();
        mockUser.setId(1L);

        when(userMapper.selectById(1L)).thenReturn(mockUser);
        when(userMapper.deleteById(1L)).thenThrow(new RuntimeException("DB error"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> managerService.deleteBorrower(deleteDto));

        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), ex.getCode());
        assertEquals("Delete failed", ex.getMessage());
    }

    // ========== deleteBorrowers 测试 ==========

    @Test
    void testDeleteBorrowers_Success() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        // 创建 spy 对象
        ManagerServiceImpl spyService = spy(managerService);

        // 模拟 removeByIds 方法
        doReturn(true).when(spyService).removeByIds(ids);

        Boolean result = spyService.deleteBorrowers(ids);

        assertTrue(result);
        verify(spyService, times(1)).removeByIds(ids);
    }

    @Test
    void testDeleteBorrowers_EmptyList() {
        List<Long> ids = Arrays.asList();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> managerService.deleteBorrowers(ids));

        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
        assertEquals("id list is empty", ex.getMessage());
    }

    @Test
    void testDeleteBorrowers_NullList() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> managerService.deleteBorrowers(null));

        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
        assertEquals("id list is empty", ex.getMessage());
    }

    @Test
    void testDeleteBorrowers_DeleteFails() {
        List<Long> ids = Arrays.asList(1L, 2L);

        // 创建 spy 对象
        ManagerServiceImpl spyService = spy(managerService);

        // 模拟 removeByIds 方法抛出异常
        doThrow(new RuntimeException("DB error")).when(spyService).removeByIds(ids);

        // 执行并验证
        BusinessException ex = assertThrows(BusinessException.class,
                () -> spyService.deleteBorrowers(ids));

        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), ex.getCode());
        assertEquals("Batch delete failed", ex.getMessage());

        // 验证方法确实被调用
        verify(spyService, times(1)).removeByIds(ids);
    }

    // ========== getAllBorrowers 测试 ==========

    @Test
    void testGetAllBorrowers_Success() {
        PageDto pageDto = new PageDto();
        pageDto.setPageNumber(1);
        pageDto.setPageSize(10);

        // 创建模拟的分页结果
        Page<UserPo> mockPage = new Page<>(1, 10, 25);
        UserPo user1 = new UserPo();
        user1.setId(1L);
        user1.setUsername("user1");
        UserPo user2 = new UserPo();
        user2.setId(2L);
        user2.setUsername("user2");
        mockPage.setRecords(Arrays.asList(user1, user2));

        when(userMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        Page<UserVo> result = managerService.getAllBorrowers(pageDto);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        assertEquals(1, result.getCurrent());
        assertEquals(10, result.getSize());
        assertEquals(25, result.getTotal());

        // 验证转换是否正确
        assertEquals("user1", result.getRecords().get(0).getUsername());
        assertEquals("user2", result.getRecords().get(1).getUsername());
    }

    @Test
    void testGetAllBorrowers_EmptyResult() {
        PageDto pageDto = new PageDto();
        pageDto.setPageNumber(1);
        pageDto.setPageSize(10);

        Page<UserPo> mockPage = new Page<>(1, 10, 0);
        mockPage.setRecords(Arrays.asList());

        when(userMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);

        Page<UserVo> result = managerService.getAllBorrowers(pageDto);

        assertNotNull(result);
        assertEquals(0, result.getRecords().size());
        assertEquals(0, result.getTotal());
    }

    // ========== updateBorrower 测试 ==========

    @Test
    void testUpdateBorrower_Success() {
        UserVo userVo = new UserVo();
        userVo.setId(1L);
        userVo.setUsername("updatedUser");
        userVo.setPhone("1234567890");

        UserPo existingUser = new UserPo();
        existingUser.setId(1L);
        existingUser.setUsername("oldUser");

        when(userMapper.selectById(1L)).thenReturn(existingUser);
        when(userMapper.updateById(any(UserPo.class))).thenReturn(1);

        Boolean result = managerService.updateBorrower(userVo);

        assertTrue(result);
        verify(userMapper, times(1)).selectById(1L);
        verify(userMapper, times(1)).updateById(any(UserPo.class));
    }

    @Test
    void testUpdateBorrower_NullUserVo() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> managerService.updateBorrower(null));

        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
        assertEquals("User id is required", ex.getMessage());
    }

    @Test
    void testUpdateBorrower_NullUserId() {
        UserVo userVo = new UserVo();
        userVo.setUsername("testUser");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> managerService.updateBorrower(userVo));

        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
        assertEquals("User id is required", ex.getMessage());
    }

    @Test
    void testUpdateBorrower_UserNotExists() {
        UserVo userVo = new UserVo();
        userVo.setId(99L);
        userVo.setUsername("nonexistent");

        when(userMapper.selectById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> managerService.updateBorrower(userVo));

        assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
        assertEquals("User not exists", ex.getMessage());
    }

    @Test
    void testUpdateBorrower_UpdateFails() {
        UserVo userVo = new UserVo();
        userVo.setId(1L);
        userVo.setUsername("updatedUser");

        UserPo existingUser = new UserPo();
        existingUser.setId(1L);

        when(userMapper.selectById(1L)).thenReturn(existingUser);
        when(userMapper.updateById(any(UserPo.class))).thenReturn(0);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> managerService.updateBorrower(userVo));

        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), ex.getCode());
        assertEquals("Update failed: Update failed", ex.getMessage());
    }

    @Test
    void testUpdateBorrower_UpdateThrowsException() {
        UserVo userVo = new UserVo();
        userVo.setId(1L);
        userVo.setUsername("updatedUser");

        UserPo existingUser = new UserPo();
        existingUser.setId(1L);

        when(userMapper.selectById(1L)).thenReturn(existingUser);
        when(userMapper.updateById(any(UserPo.class))).thenThrow(new RuntimeException("DB error"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> managerService.updateBorrower(userVo));

        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), ex.getCode());
        assertTrue(ex.getMessage().contains("Update failed"));
    }
}