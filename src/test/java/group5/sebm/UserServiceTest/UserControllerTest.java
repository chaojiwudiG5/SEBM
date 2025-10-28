package group5.sebm.UserServiceTest;

import group5.sebm.User.controller.UserController;
import group5.sebm.User.controller.dto.DeleteDto;
import group5.sebm.User.controller.dto.PageDto;
import group5.sebm.User.controller.dto.LoginDto;
import group5.sebm.User.controller.dto.RegisterDto;
import group5.sebm.User.controller.dto.UpdateDto;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.service.UserServiceInterface.BorrowerService;
import group5.sebm.User.service.UserServiceInterface.ManagerService;
import group5.sebm.User.service.UserServiceInterface.UserService;
import group5.sebm.common.BaseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;




import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


public class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private BorrowerService borrowerService;

    @Mock
    private ManagerService managerService;

    @Mock
    private UserService userServiceImpl;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userController=new UserController(borrowerService,managerService,userServiceImpl);
    }

    @Test
    public void testUserRegister() {
        RegisterDto dto = new RegisterDto();
        when(borrowerService.userRegister(dto)).thenReturn(1L);

        BaseResponse<Long> response = userController.userRegister(dto);
        assertEquals(1L, response.getData());
    }

    @Test
    public void testUserLogin() {
        LoginDto dto = new LoginDto();
        UserVo vo = new UserVo();
        vo.setId(1L);
        when(borrowerService.userLogin(dto)).thenReturn(vo);

        BaseResponse<UserVo> response = userController.userLogin(dto);
        assertEquals(1L, response.getData().getId());
    }

    @Test
    public void testDeleteUser() {
        DeleteDto dto = new DeleteDto();
        dto.setId(1L);
        when(managerService.deleteBorrower(dto)).thenReturn(true);

        BaseResponse<Boolean> response = userController.deleteUser(dto);
        assertEquals(true, response.getData());
    }

    @Test
    public void testUpdateUserAdmin() {
        UserVo vo = new UserVo();
        vo.setId(1L);
        when(managerService.updateBorrower(vo)).thenReturn(true);

        BaseResponse<Boolean> response = userController.updateUser(vo);
        assertEquals(true, response.getData());
    }

    @Test
    public void testDeactivateUser() {
        DeleteDto dto = new DeleteDto();
        dto.setId(1L);
        when(borrowerService.deactivateUser(dto)).thenReturn(true);

        BaseResponse<Boolean> response = userController.deactivateUser(dto);
        assertEquals(true, response.getData());
    }

    @Test
    public void testUpdateUser() {
        UpdateDto dto = new UpdateDto();
        UserVo vo = new UserVo();
        vo.setId(1L);
        when(borrowerService.updateUser(dto, request)).thenReturn(vo);

        BaseResponse<UserVo> response = userController.updateUser(dto, request);
        assertEquals(1L, response.getData().getId());
    }

    @Test
    public void testGetCurrentUser() {
        UserVo vo = new UserVo();
        vo.setId(1L);
        vo.setUsername("testUser");

        // 验证 Mock 设置
        when(userServiceImpl.getCurrentUser(request)).thenReturn(vo);

        // 测试 Mock 是否工作
        UserVo testResult = userServiceImpl.getCurrentUser(request);
        System.out.println("Mock result: " + testResult); // 应该输出非 null

        BaseResponse<UserVo> response = userController.getCurrentUser(request);

        System.out.println("Controller response: " + response);
        if (response != null) {
            System.out.println("Response data: " + response.getData());
        }

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getData(), "User data should not be null");
        assertEquals(1L, response.getData().getId());
    }

    @Test
    public void testGetAllUsers() {
        // 准备参数
        PageDto pageDto = new PageDto();
        pageDto.setPageNumber(1);
        pageDto.setPageSize(10);

        // 构造返回的分页数据
        Page<UserVo> userVoPage = new Page<>(1,10);
        UserVo user1 = new UserVo();
        user1.setId(1L);
        user1.setUsername("Alice");
        UserVo user2 = new UserVo();
        user2.setId(2L);
        user2.setUsername("Bob");
        userVoPage.setRecords(Arrays.asList(user1, user2));
        userVoPage.setTotal(2);

        // mock managerService.getAllBorrowers 返回分页
        when(managerService.getAllBorrowers(pageDto)).thenReturn(userVoPage);

        // 调用 controller
        BaseResponse<Page<UserVo>> response = userController.getAllUsers(pageDto);

        // 断言
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().getRecords().size());
        assertEquals("Alice", response.getData().getRecords().get(0).getUsername());
        assertEquals("Bob", response.getData().getRecords().get(1).getUsername());

        // 验证方法被调用
        verify(managerService, times(1)).getAllBorrowers(pageDto);
    }


}
