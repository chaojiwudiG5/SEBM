package group5.sebm.common.dto.User;

import lombok.Data;

@Data
public class MechanicInfoDto extends UserInfoDto {

  /**
   * 管理员等级
   */
  private Integer level;
}
