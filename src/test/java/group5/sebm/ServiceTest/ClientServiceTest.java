//package group5.sebm.ServiceTest;
//
//import group5.sebm.controller.vo.UserVo;
//import group5.sebm.dao.UserMapper;
//import group5.sebm.entity.UserPo;
//import group5.sebm.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ClientServiceTest {
//
//    @Mock
//    private UserMapper userMapper;
//
//    @InjectMocks
//    private UserService userService;
//
//    @Test
//    void getAllUsers_WhenUsersExist_ShouldReturnListOfUserVos() {
//        UserPo userPo1 = new UserPo();
//        userPo1.setId(1); userPo1.setUsername("user1");
//        UserPo userPo2 = new UserPo();
//        userPo2.setId(2); userPo2.setUsername("user2");
//
//        when(userMapper.selectList(null)).thenReturn(Arrays.asList(userPo1, userPo2));
//
//        List<UserVo> result = userService.getAllUsers();
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals("user1", result.get(0).getUsername());
//        assertEquals("user2", result.get(1).getUsername());
//        verify(userMapper, times(1)).selectList(null);
//    }
//
//    @Test
//    void getDiscountUserById_WhenUserExistsAndQualifiesForDiscount_ShouldReturnUserVo() {
//        UserPo userPo = new UserPo();
//        userPo.setId(3); userPo.setUsername("user3");
//        when(userMapper.selectById(3)).thenReturn(userPo);
//
//        UserVo result = userService.getDiscountUserById(3);
//
//        assertNotNull(result);
//        assertEquals("user3", result.getUsername());
//        verify(userMapper, times(1)).selectById(3);
//    }
//
//    @Test
//    void getDiscountUserById_WhenUserDoesNotExist_ShouldReturnNull() {
//        when(userMapper.selectById(999)).thenReturn(null);
//
//        UserVo result = userService.getDiscountUserById(999);
//
//        assertNull(result);
//        verify(userMapper, times(1)).selectById(999);
//    }
//
//    @Test
//    void deleteUser_WhenUserExists_ShouldDeleteUser() {
//        UserPo userPo = new UserPo();
//        userPo.setId(6);
//        when(userMapper.selectById(6)).thenReturn(userPo);
//        doNothing().when(userMapper).deleteById(6);
//
//        userService.deleteUser(6);
//
//        verify(userMapper, times(1)).selectById(6);
//        verify(userMapper, times(1)).deleteById(6);
//    }
//
//    @Test
//    void deleteUser_WhenUserDoesNotExist_ShouldNotAttemptDeletion() {
//        when(userMapper.selectById(999)).thenReturn(null);
//
//        userService.deleteUser(999);
//
//        verify(userMapper, times(1)).selectById(999);
//        verify(userMapper, never()).deleteById(anyInt());
//    }
//
//    @Test
//    void updateUser_WhenUserExists_ShouldUpdateUser() {
//        UserPo userPo7 = new UserPo();
//        userPo7.setId(7); userPo7.setUsername("user7"); userPo7.setPassword("pass7");
//
//        UserVo userVo8 = new UserVo();
//        userVo8.setId(7); userVo8.setUsername("user8");
//
//        when(userMapper.selectById(7)).thenReturn(userPo7);
//        doNothing().when(userMapper).updateById(any(UserPo.class));
//
//        userService.updateUser(userVo8, null);
//
//        ArgumentCaptor<UserPo> captor = ArgumentCaptor.forClass(UserPo.class);
//        verify(userMapper).updateById(captor.capture());
//
//        UserPo updated = captor.getValue();
//        assertEquals("user8", updated.getUsername());
//        assertEquals("pass7", updated.getPassword());
//    }
//}
