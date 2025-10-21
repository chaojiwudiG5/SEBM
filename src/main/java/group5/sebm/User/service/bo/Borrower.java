package group5.sebm.User.service.bo;

import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @description: Borrower class representing a user who borrows items.
 * @author: deshperaydon
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Borrower extends User{

  /**
   * 逾期次数
   */
  private Integer overdueTimes;
  /**
   * 已借设备数
   */
  private Integer borrowedDeviceCount;

  /**
   * 最大可借设备数
   */
  private Integer maxBorrowedDeviceCount;
  /**
   * 最大逾期次数
   */
  private Integer maxOverdueTimes;
  /**
   * 预约设备数
   */
  private Integer reserveDeviceCount;


  private Integer maxReserveDeviceCount;


  public boolean isokforDiscount() {
    return this.age <= 18;
  }
    /**
     * 增加已借设备数
     * @param delta 增加的数量，可以为负数表示减少
     */
  public void updateBorrowedCount(Integer delta) {
    if (delta == null) {
      throw new IllegalArgumentException("Borrowed count delta cannot be null");
    }
    if (this.borrowedDeviceCount == null) {
      this.borrowedDeviceCount = 0;
    }
    this.borrowedDeviceCount += delta;
    if (this.borrowedDeviceCount < 0) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR,"Borrowed device count cannot be negative");
    }
  }

}
