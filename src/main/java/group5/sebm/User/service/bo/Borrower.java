package group5.sebm.User.service.bo;

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
   * 借用设备数量
   */
  private Integer borrowedDeviceCount;

}
