package group5.sebm.User.controller.vo;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {

  private Long id;
  private String username;

  /** 密码前端无需看到，标记 JsonIgnore */
  @JsonIgnore
  private String password;

  private String email;
  private String phone;
  private Integer gender;
  private String avatarUrl;
  private Integer userRole;
  private Integer userStatus;

  /** 是否删除（逻辑删除）不暴露前端 */
  @JsonIgnore
  private Integer isDelete;

  private Integer age;
  private Integer level;
  private Integer overdueTimes;
  private Integer borrowedDeviceCount;
  private Integer reservedDeviceCount;
  private Integer maxBorrowedDeviceCount;
  private Integer maxOverdueTimes;
  private Integer maxReservedDeviceCount;

  private Date createTime;
  private Date updateTime;

  /** 可由 Service 层设置 */
  private boolean isActive;
  private String token;
}
