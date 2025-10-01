package group5.sebm.User.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.User.service.UserServiceInterface.BorrowerService;
import group5.sebm.User.service.UserServiceInterface.ManagerService;
import group5.sebm.User.service.UserServiceInterface.UserService;
import group5.sebm.annotation.AuthCheck;
import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import group5.sebm.User.controller.dto.DeleteDto;
import group5.sebm.User.controller.dto.PageDto;
import group5.sebm.User.controller.dto.LoginDto;
import group5.sebm.User.controller.dto.RegisterDto;
import group5.sebm.User.controller.dto.UpdateDto;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.common.enums.UserRoleEnum;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "User")
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

  private final BorrowerService borrowerService;
  private final ManagerService managerService;
  private final UserService userServiceImpl;

  @PostMapping("/register")
  public BaseResponse<Long> userRegister(@RequestBody @Valid RegisterDto registerDto) {
    Long userId = borrowerService.userRegister(registerDto);
    log.info("UserRegister called with userId: {}", userId);
    return ResultUtils.success(userId); // 返回ID
  }

  @PostMapping("/login")
  public BaseResponse<UserVo> userLogin(@RequestBody @Valid LoginDto LoginDto) {
    UserVo userVo = borrowerService.userLogin(LoginDto);
    log.info("UserLogin called with userVo: {}", userVo);
    return ResultUtils.success(userVo); // 返回VO
  }


  @PostMapping("/admin/getUserList")
  @AuthCheck(mustRole = UserRoleEnum.ADMIN)
  public BaseResponse<Page<UserVo>> getAllUsers(@RequestBody @Valid PageDto pageDto) {
    Page<UserVo> userVoPage = this.managerService.getAllBorrowers(pageDto);
    log.info("GetAllUsers called with pageDto: {}, userVoPage: {}", pageDto, userVoPage);
    return ResultUtils.success(userVoPage); // 返回Page
  }

  @PostMapping("/admin/deleteUser")
  @AuthCheck(mustRole = UserRoleEnum.ADMIN)
  public BaseResponse<Boolean> deleteUser(@RequestBody @Valid DeleteDto deleteDto) {
    Boolean isDelete = this.managerService.deleteBorrower(deleteDto);
    log.info("DeleteUser called with deleteDto: {}, isDelete: {}", deleteDto, isDelete);
    return ResultUtils.success(isDelete); // 返回Boolean
  }

  @PostMapping("/admin/updateUser")
  @AuthCheck(mustRole = UserRoleEnum.ADMIN)
  public BaseResponse<Boolean> updateUser(@RequestBody @Valid UserVo userVo) {
    Boolean isUpdate = managerService.updateBorrower(userVo);
    log.info("UpdateUser called with userVo: {}, result: {}", userVo, isUpdate);
    return ResultUtils.success(isUpdate); // 返回Boolean
  }




  @PostMapping("/deactivateUser")
  public BaseResponse<Boolean> deactivateUser(@RequestBody @Valid DeleteDto deactivateUser) {
    Boolean isDeactivate = borrowerService.deactivateUser(deactivateUser);
    log.info("DeactivateUser called with userVo: {}, isDeactivate: {}", deactivateUser, isDeactivate);
    return ResultUtils.success(isDeactivate); // 返回Boolean
  }



  @PostMapping("/updateUser")
  public BaseResponse<UserVo> updateUser(@RequestBody @Valid UpdateDto updateDto,HttpServletRequest request) {

      UserVo userVo = borrowerService.updateUser(updateDto,request);
      log.info("UpdateUser called with userUpdateDto: {}, userVo: {}", updateDto, userVo);
      return ResultUtils.success(userVo); // 返回VO
  }

  @GetMapping("/getCurrentUser")
  public BaseResponse<UserVo> getCurrentUser(HttpServletRequest request) {
    UserVo currentUser = userServiceImpl.getCurrentUser(request);
    log.info("GetCurrentUser called with currentUser: {}", currentUser);
    return ResultUtils.success(currentUser); // 返回VO
  }
}
