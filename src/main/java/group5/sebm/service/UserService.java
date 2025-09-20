package group5.sebm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.controller.dto.DeleteDto;
import group5.sebm.controller.dto.PageDto;
import group5.sebm.controller.dto.UserLoginDto;
import group5.sebm.controller.dto.UserRegisterDto;
import group5.sebm.controller.dto.UserUpdateDto;
import group5.sebm.controller.vo.UserVo;
import group5.sebm.entity.UserPo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Luoimo
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-09-16 13:06:33
 */
public interface UserService extends IService<UserPo> {

  Page<UserVo> getAllUsers(PageDto pageDto);

//  UserVo getDiscountUserById(Long id);

  UserVo getLoginUser(HttpServletRequest request);

  Boolean deleteUser(DeleteDto deleteDto);

  Long userRegister(UserRegisterDto userRegisterDto);

  UserVo userLogin(UserLoginDto userLoginDto,HttpServletRequest request);

  Boolean userLogout(HttpServletRequest request);

  UserVo updateUser(UserUpdateDto userUpdateDto);
}
