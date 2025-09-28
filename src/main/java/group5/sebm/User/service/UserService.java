package group5.sebm.User.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.User.controller.dto.DeleteDto;
import group5.sebm.User.controller.dto.LoginDto;
import group5.sebm.User.controller.dto.PageDto;
import group5.sebm.User.controller.dto.RegisterDto;
import group5.sebm.User.controller.dto.UpdateDto;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.entity.UserPo;
import group5.sebm.common.dto.User.UserInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * @author Luoimo
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-09-16 13:06:33
 */
public interface UserService extends IService<UserPo> {

  UserVo getCurrentUser(HttpServletRequest request);

  Long userRegister(RegisterDto registerDto);

  UserVo userLogin(LoginDto loginDto);

  UserInfoDto getCurrentUserDto(HttpServletRequest request);

  UserVo updateUser(UpdateDto updateDto, HttpServletRequest request);

  Long deactivateUser(DeleteDto deactivateUser);

  Boolean deleteBorrower(DeleteDto deleteDto);

  Boolean deleteBorrowers(List<Long> ids);

  Page<UserVo> getAllBorrowers(PageDto pageDto);

}
