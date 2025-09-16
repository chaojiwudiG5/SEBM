package group5.sebm.controller;

import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import group5.sebm.controller.dto.UserDto;
import group5.sebm.service.UserServiceImpl;
import group5.sebm.controller.vo.UserVo;
import group5.sebm.service.bo.User;
import group5.sebm.service.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "User")
@RequestMapping("/api/user")
public class UserController {

  @Resource
  private UserService userService;

  //TODO只有管理员能查看所有用户，需编写AOP进行权限控制
  @GetMapping("/getUserList")
  public BaseResponse<List<UserVo>> getAllUsers() {
    return ResultUtils.success(userService.getAllUsers()); // 直接返回VO
  }

  @PostMapping("/addUser")
  public BaseResponse<UserVo> addUser(@RequestBody UserDto userDto) {
    return ResultUtils.success(userService.addUser(userDto)); // 直接返回VO
  }

  @PostMapping("/id")
  public UserVo getUserById(@RequestBody UserDto userDto) {
    Long id = userDto.getId();
    return userService.getDiscountUserById(id); // 直接返回VO
  }


}
