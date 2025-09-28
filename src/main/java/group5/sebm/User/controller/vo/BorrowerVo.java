package group5.sebm.User.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerVo extends UserVo {
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
