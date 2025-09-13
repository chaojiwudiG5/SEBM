package group5.sebm.ServiceTest;

import group5.sebm.controller.vo.UserVo;
import group5.sebm.dao.UserRepository;
import group5.sebm.entity.UserPo;
import group5.sebm.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_WhenUsersExist_ShouldReturnListOfUserVos() {
        UserPo userPo1 = new UserPo(1, "user1", "password1", 30);
        UserPo userPo2 = new UserPo(2, "user2", "password2", 25);
        when(userRepository.findAll()).thenReturn(Arrays.asList(userPo1, userPo2));

        List<UserVo> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_WhenNoUsersExist_ShouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        List<UserVo> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getDiscountUserById_WhenUserExistsAndQualifiesForDiscount_ShouldReturnUserVo() {
        UserPo userPo3 = new UserPo(3, "user3", "password3", 16);
        when(userRepository.findById(3)).thenReturn(Optional.of(userPo3));

        UserVo result = userService.getDiscountUserById(3);

        assertNotNull(result);
        assertEquals("user3", result.getUsername());
        verify(userRepository, times(1)).findById(3);
    }

    @Test
    void getDiscountUserById_WhenUserExistsButDoesNotQualifyForDiscount_ShouldReturnNull() {
        UserPo userPo4 = new UserPo(4, "user4", "password4", 40);
        when(userRepository.findById(4)).thenReturn(Optional.of(userPo4));

        UserVo result = userService.getDiscountUserById(4);

        assertNull(result);
        verify(userRepository, times(1)).findById(4);
    }

    @Test
    void getDiscountUserById_WhenUserDoesNotExist_ShouldReturnNull() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        UserVo result = userService.getDiscountUserById(999);

        assertNull(result);
        verify(userRepository, times(1)).findById(999);
    }

    @Test
    void createUser_ShouldSaveUserToRepository() {

        UserPo userPo5 = new UserPo(5, "user5", "password5", 40);
        UserVo userVo = new UserVo(5, "user5", 40);
        when(userRepository.save(any(UserPo.class))).thenReturn(userPo5);

        userService.createUser(userVo, "password123");

        ArgumentCaptor<UserPo> userPoCaptor = ArgumentCaptor.forClass(UserPo.class);
        verify(userRepository).save(userPoCaptor.capture());

        UserPo capturedUserPo = userPoCaptor.getValue();

        assertEquals(userVo.getUsername(), capturedUserPo.getUsername(),
                "Username should be consistent between VO and PO");

        verify(userRepository, times(1)).save(any(UserPo.class));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        when(userRepository.existsById(6)).thenReturn(true);
        doNothing().when(userRepository).deleteById(6);

        userService.deleteUser(6);

        verify(userRepository, times(1)).existsById(6);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldNotAttemptDeletion() {
        UserVo nonExistentUser = new UserVo(999, "nonexistent", 30);
        when(userRepository.existsById(999)).thenReturn(false);

        userService.deleteUser(nonExistentUser.getId());

        verify(userRepository, times(1)).existsById(999);
        verify(userRepository, never()).deleteById(anyInt());
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateUser() {
        UserPo userPo7 = new UserPo(7, "user7", "password7", 40);
        UserVo userVo8 = new UserVo(7, "user8", 50);
        when(userRepository.existsById(7)).thenReturn(true);
        when(userRepository.findById(7)).thenReturn(Optional.of(userPo7));
        when(userRepository.save(any(UserPo.class))).thenReturn(userPo7);

        userService.updateUser(userVo8, null);

        verify(userRepository, times(1)).existsById(7);
        verify(userRepository, times(1)).findById(7);

        ArgumentCaptor<UserPo> userPoCaptor = ArgumentCaptor.forClass(UserPo.class);
        verify(userRepository, times(1)).save(userPoCaptor.capture());
        UserPo savedUserPo = userPoCaptor.getValue();

        assertEquals(7, savedUserPo.getId());
        assertEquals("user8", savedUserPo.getUsername());
        assertEquals(50, savedUserPo.getAge());
        assertEquals("password7", savedUserPo.getPassword());

        verify(userRepository, times(1)).save(any(UserPo.class));
    }
    @Test
    void updateUser_WhenUserExistsAndPassword_ShouldUpdateUser() {
        UserPo userPo7 = new UserPo(7, "user7", "password7", 40);
        UserVo userVo8 = new UserVo(7, "user8", 50);
        when(userRepository.existsById(7)).thenReturn(true);
        when(userRepository.findById(7)).thenReturn(Optional.of(userPo7));
        when(userRepository.save(any(UserPo.class))).thenReturn(userPo7);

        userService.updateUser(userVo8, "");

        verify(userRepository, times(1)).existsById(7);
        verify(userRepository, times(1)).findById(7);

        ArgumentCaptor<UserPo> userPoCaptor = ArgumentCaptor.forClass(UserPo.class);
        verify(userRepository, times(1)).save(userPoCaptor.capture());
        UserPo savedUserPo = userPoCaptor.getValue();

        assertEquals(7, savedUserPo.getId());
        assertEquals("user8", savedUserPo.getUsername());
        assertEquals(50, savedUserPo.getAge());
        assertEquals("password7", savedUserPo.getPassword());

        verify(userRepository, times(1)).save(any(UserPo.class));
    }

    @Test
    void updateUser_WhenUserExistsAndPasswordProvided_ShouldUpdatePassword() {
        UserPo UserPo9 = new UserPo(9, "user9", "oldPassword", 30);
        UserVo UserVo10 = new UserVo(9, "user9_updated", 35);

        when(userRepository.existsById(9)).thenReturn(true);
        when(userRepository.findById(9)).thenReturn(Optional.of(UserPo9));
        when(userRepository.save(any(UserPo.class))).thenReturn(UserPo9);

        String newPassword = "newPassword123";
        userService.updateUser(UserVo10, newPassword);

        ArgumentCaptor<UserPo> captor = ArgumentCaptor.forClass(UserPo.class);
        verify(userRepository).save(captor.capture());
        UserPo savedUser = captor.getValue();

        assertEquals("user9_updated", savedUser.getUsername());
        assertEquals(35, savedUser.getAge());
        assertEquals("newPassword123", savedUser.getPassword());

        // 验证仓库方法调用
        verify(userRepository, times(1)).existsById(9);
        verify(userRepository, times(1)).findById(9);
        verify(userRepository, times(1)).save(any(UserPo.class));
    }


    @Test
    void updateUser_WhenUserDoesNotExist_ShouldNotAttemptUpdate() {
        UserVo nonExistentUser = new UserVo(999, "nonexistent", 30);
        when(userRepository.existsById(999)).thenReturn(false);

        userService.updateUser(nonExistentUser, "newpassword");

        verify(userRepository, times(1)).existsById(999);
        verify(userRepository, never()).findById(anyInt());
        verify(userRepository, never()).save(any(UserPo.class));
    }
}

