package group5.sebm.User.service.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    protected Long id;

    /**
     * 用户昵称
     */
    protected String username;

    /**
     * 密码
     */
    protected String password;

    /**
     * 邮箱
     */
    protected String email;

    /**
     * 电话
     */
    protected String phone;

    /**
     * 性别
     */
    protected Integer gender;

    /**
     * 用户头像
     */
    protected String avatarUrl;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    protected Integer userRole;

    /**
     * 是否删除
     */
    protected Boolean isDelete;

    /**
     * 更新时间
     */
    protected Date updateTime;
    /**
     * 创建时间
     */
    protected Date createTime;

    /**
     * 状态 0 - 正常
     */
    protected Integer userStatus;
    /**
     * 年龄
     */
    protected Integer age;

    /**
     * 等级
     */
    protected Integer level;


    /**
     * 验证两次输入的密码是否一致
     * @param password 密码
     * @param checkPassword 确认密码
     * @return 是否一致
     */
    public boolean validateTwicePassword(String password, String checkPassword) {
        return password != null && password.equals(checkPassword);
    }

    public boolean validatePassword(String input, String dbPassword, BCryptPasswordEncoder encoder) {
        return encoder.matches(input, dbPassword);
    }
}
