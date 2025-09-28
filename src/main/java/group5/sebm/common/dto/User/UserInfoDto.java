package group5.sebm.common.dto.User;

import lombok.Data;

@Data
public class UserInfoDto {

  /**
   * 用户ID
   */
  private Long id;
  /**
   * 用户昵称
   */
  private String username;
  /**
   * 用户角色
   */
  private Integer userRole;

}
