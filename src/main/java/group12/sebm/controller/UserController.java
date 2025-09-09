package group12.sebm.controller;
import group12.sebm.service.UserService;
import group12.sebm.controller.dto.UserDto;
import group12.sebm.controller.vo.UserVo;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public Collection<UserVo> getAllUsers() {
        Collection<UserDto> dtos = userService.getAllUsers();
        return dtos.stream()
                .map(dto -> new UserVo(dto.getId(), "用户: " + dto.getUsername(),dto.getAge()))
                .collect(Collectors.toList());
    }


    @PostMapping("/id")
    public UserVo getUserById(@RequestBody UserVo request) {
        UserVo vo = userService.getDiscountUserById(request.getId());
        if (vo == null) return null;
        return new UserVo(vo.getId(), "用户: " + vo.getUsername(), vo.getAge());
    }

}
