package group5.sebm.User.controller.dto;

/**
 * @author Deshperaydon
 * @date 2025/9/29
 */

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * 用户数据传输对象
 * 用于接收前端传递的用户参数（新增 / 修改）
 * @author Deshperaydon
 * @date 2025/9/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6位")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String phone;

    /** 性别：0=未知，1=男，2=女 */
    private Integer gender;

    /** 头像 URL */
    private String avatarUrl;

    /** 用户角色，例如 0=普通用户, 1=管理员 */
    private Integer userRole;

    /** 用户状态，例如 0=正常, 1=禁用 */
    private Integer userStatus;

    /** 年龄 */
    private Integer age;

    /** 等级（可选，某些业务需要） */
    private Integer level;
}
