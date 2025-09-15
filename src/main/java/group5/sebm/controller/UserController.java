package group5.sebm.controller;

import group5.sebm.service.UserService;
import group5.sebm.controller.vo.UserVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "User")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  public List<UserVo> getAllUsers() {
    return userService.getAllUsers();
  }


  @PostMapping("/id")
  public UserVo getUserById(@RequestBody UserVo request) {
    return userService.getDiscountUserById(request.getId());
  }

}
