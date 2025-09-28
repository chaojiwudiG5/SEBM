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
  /**
   * 逾期次数
   */
  private Integer overdueTimes;
  /**
   * 借用设备数量
   */
  private Integer borrowedDeviceCount;
}
