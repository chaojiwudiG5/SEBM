package group12.sebm.controller;
import group12.sebm.service.UserService;
import group12.sebm.controller.dto.UserDto;
import group12.sebm.controller.vo.UserVo;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserVo> getAllUsers() {
        return userService.getAllUsers(); // 直接返回VO
    }


    @PostMapping("/id")
    public UserVo getUserById(@RequestBody UserVo request) {
        return userService.getDiscountUserById(request.getId());
    }

}
