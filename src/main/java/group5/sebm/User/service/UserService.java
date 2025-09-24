package group5.sebm.User.service;

import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.User.controller.dto.LoginDto;
import group5.sebm.User.controller.dto.RegisterDto;
import group5.sebm.User.controller.dto.UpdateDto;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.entity.UserPo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Luoimo
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-09-16 13:06:33
 */
public interface UserService extends IService<UserPo> {

//  Page<UserVo> getAllUsers(PageDto pageDto);

//  UserVo getDiscountUserById(Long id);

  UserVo getLoginUser(HttpServletRequest request);

  //Boolean deleteUser(DeleteDto deleteDto);

  Long userRegister(RegisterDto registerDto);

  UserVo userLogin(LoginDto loginDto, HttpServletRequest request);

  Boolean userLogout(HttpServletRequest request);

  UserVo updateUser(UpdateDto updateDto);
}
