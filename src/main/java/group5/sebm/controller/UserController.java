package group5.sebm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.annotation.AuthCheck;
import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import group5.sebm.controller.dto.DeleteDto;
import group5.sebm.controller.dto.PageDto;
import group5.sebm.controller.dto.UserLoginDto;
import group5.sebm.controller.dto.UserRegisterDto;
import group5.sebm.controller.dto.UserUpdateDto;
import group5.sebm.controller.vo.UserVo;
import group5.sebm.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "User")
@RequestMapping("/api/user")
public class UserController {

  private UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public BaseResponse<Long> userRegister(@RequestBody @Valid UserRegisterDto userRegisterDto) {
    Long userId = userService.userRegister(userRegisterDto);
    log.info("UserRegister called with userId: {}", userId);
    return ResultUtils.success(userId); // 返回ID
  }

  @PostMapping("/login")
  public BaseResponse<UserVo> userLogin(@RequestBody @Valid UserLoginDto UserLoginDto,
      HttpServletRequest request) {
    UserVo userVo = userService.userLogin(UserLoginDto, request);
    log.info("UserLogin called with userVo: {}", userVo);
    return ResultUtils.success(userVo); // 返回VO
  }

  @PostMapping("/logout")
  public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
    Boolean isLogout = userService.userLogout(request);
    log.info("UserLogout called with request: {}", request);
    return ResultUtils.success(isLogout); // 返回Boolean
  }

  //TODO只有管理员能查看所有用户，需编写AOP进行权限控制
  @PostMapping("/admin/getUserList")
  @AuthCheck(mustRole = "admin")
  public BaseResponse<Page<UserVo>> getAllUsers(@RequestBody @Valid PageDto pageDto) {
    Page<UserVo> userVoPage = userService.getAllUsers(pageDto);
    log.info("GetAllUsers called with pageDto: {}, userVoPage: {}", pageDto, userVoPage);
    return ResultUtils.success(userVoPage); // 返回Page
  }

  @PostMapping("/admin/deleteUser")
  @AuthCheck(mustRole = "admin")
  public BaseResponse<Boolean> deleteUser(@RequestBody @Valid DeleteDto deleteDto) {
    Boolean isDelete = userService.deleteUser(deleteDto);
    log.info("DeleteUser called with deleteDto: {}, isDelete: {}", deleteDto, isDelete);
    return ResultUtils.success(isDelete); // 返回Boolean
  }

  @PostMapping("/updateUser")
  public BaseResponse<UserVo> updateUser(@RequestBody @Valid UserUpdateDto userUpdateDto) {
      UserVo userVo = userService.updateUser(userUpdateDto);
      log.info("UpdateUser called with userUpdateDto: {}, userVo: {}", userUpdateDto, userVo);
      return ResultUtils.success(userVo); // 返回VO
  }
}
