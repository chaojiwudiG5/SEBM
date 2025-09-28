package group5.sebm.common.dto.User;

import lombok.Data;

@Data
public class BorrowerInfoDto extends UserInfoDto{
  /**
   * 逾期次数
   */
  private Integer overdueTimes;

  /**
   * 借用设备数
   */
  private Integer borrowedDeviceCount;

  /**
   * 预约设备数
   */
  private Integer reservedDeviceCount;

  /**
   * 最大可借用设备数
   */
  private Integer maxBorrowedDeviceCount;

  /**
   * 最大逾期次数
   */
  private Integer maxOverdueTimes;

  /**
   * 最大预约设备数
   */
  private Integer maxReservedDeviceCount;
}
