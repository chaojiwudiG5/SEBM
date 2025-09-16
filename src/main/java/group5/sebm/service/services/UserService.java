package group5.sebm.service.services;

import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.controller.dto.UserDto;
import group5.sebm.controller.vo.UserVo;
import group5.sebm.service.bo.User;
import group5.sebm.entity.UserPO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Luoimo
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-09-16 13:06:33
 */
public interface UserService extends IService<UserPO> {

  List<UserVo> getAllUsers();

  UserVo getDiscountUserById(Long id);

  User getLoginUser(HttpServletRequest request);

  UserVo addUser(UserDto userDto);
}
