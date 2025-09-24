package group5.sebm.User.controller;

import static group5.sebm.common.constant.UserConstant.CURRENT_LOGIN_USER;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.annotation.AuthCheck;
import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import group5.sebm.User.controller.dto.DeleteDto;
import group5.sebm.User.controller.dto.PageDto;
import group5.sebm.User.controller.dto.LoginDto;
import group5.sebm.User.controller.dto.RegisterDto;
import group5.sebm.User.controller.dto.UpdateDto;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.service.BorrowerServiceImpl;
import group5.sebm.User.service.ManagerServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "User")
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

  private final BorrowerServiceImpl borrowerService;
  private final ManagerServiceImpl managerService;

  @PostMapping("/register")
  public BaseResponse<Long> userRegister(@RequestBody @Valid RegisterDto registerDto) {
    Long userId = borrowerService.userRegister(registerDto);
    log.info("UserRegister called with userId: {}", userId);
    return ResultUtils.success(userId); // 返回ID
  }

  @PostMapping("/login")
  public BaseResponse<UserVo> userLogin(@RequestBody @Valid LoginDto LoginDto,
      HttpServletRequest request) {
    UserVo userVo = borrowerService.userLogin(LoginDto, request);
    log.info("UserLogin called with userVo: {}", userVo);
    return ResultUtils.success(userVo); // 返回VO
  }

  @PostMapping("/logout")
  public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
    Boolean isLogout = borrowerService.userLogout(request);
    log.info("UserLogout called with request: {}", request);
    return ResultUtils.success(isLogout); // 返回Boolean
  }

  //TODO只有管理员能查看所有用户，需编写AOP进行权限控制
  @PostMapping("/admin/getUserList")
  @AuthCheck(mustRole = "admin")
  public BaseResponse<Page<UserVo>> getAllUsers(@RequestBody @Valid PageDto pageDto) {
    Page<UserVo> userVoPage = this.managerService.getAllBorrowers(pageDto);
    log.info("GetAllUsers called with pageDto: {}, userVoPage: {}", pageDto, userVoPage);
    return ResultUtils.success(userVoPage); // 返回Page
  }

  @PostMapping("/admin/deleteUser")
  @AuthCheck(mustRole = "admin")
  public BaseResponse<Boolean> deleteUser(@RequestBody @Valid DeleteDto deleteDto) {
    Boolean isDelete = this.managerService.deleteBorrower(deleteDto);
    log.info("DeleteUser called with deleteDto: {}, isDelete: {}", deleteDto, isDelete);
    return ResultUtils.success(isDelete); // 返回Boolean
  }

  @PostMapping("/updateUser")
  public BaseResponse<UserVo> updateUser(@RequestBody @Valid UpdateDto updateDto,HttpServletRequest request) {
      UserVo userVo = borrowerService.updateUser(updateDto,request);
      log.info("UpdateUser called with userUpdateDto: {}, userVo: {}", updateDto, userVo);
      return ResultUtils.success(userVo); // 返回VO
  }

  @GetMapping("/getCurrentUser")
  public BaseResponse<UserVo> getCurrentUser(HttpServletRequest request) {
    UserVo userVo = (UserVo) request.getSession().getAttribute(CURRENT_LOGIN_USER);
    log.info("GetCurrentUser called with request: {}, userVo: {}", request, userVo);
    return ResultUtils.success(userVo); // 返回VO
  }
}
