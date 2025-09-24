package group5.sebm.User.controller.vo;

import lombok.*;

import java.util.Date;

/**
 * 用户展示 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {

  /**
   * 用户ID
   */
  private Long id;

  /**
   * 用户昵称
   */
  private String username;

  /**
   * 邮箱
   */
  private String email;

  /**
   * 电话
   */
  private String phone;

  /**
   * 性别
   */
  private Integer gender;

  /**
   * 用户头像
   */
  private String avatarUrl;

  /**
   * 用户角色 0 - 普通用户 1 - 管理员
   */
  private Integer userRole;

  /**
   * 用户状态 0 - 正常
   */
  private Integer userStatus;

  /**
   * 年龄
   */
  private Integer age;

    /**
     * 是否激活
     */
  private boolean isActive;

  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 更新时间
   */
  private Date updateTime;
}
