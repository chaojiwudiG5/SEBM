package group5.sebm.Reservation.controller.vo;

import lombok.Data;

/**
 * 设备预约返回对象
 */
@Data
public class ReservationVo {
  /**
   * 预约ID
   */
  private Long id;

  /**
   * 预约人ID
   */
  private Long userId;

  /**
   * 预约人姓名（冗余字段，返回给前端用）
   */
  private String userName;

  /**
   * 预约设备ID
   */
  private Long deviceId;

  /**
   * 预约设备名称（冗余字段，返回给前端用）
   */
  private String deviceName;

  /**
   * 预约开始时间（格式化为 yyyy-MM-dd HH:mm）
   */
  private String reserveStart;

  /**
   * 预约结束时间（格式化为 yyyy-MM-dd HH:mm）
   */
  private String reserveEnd;

  /**
   * 预约状态 0 - 未确认 1 - 已确认 2 - 已取消 3 - 已过期
   */
  private Integer status;

  /**
   * 状态说明（方便前端直接展示）
   */
  private String statusDesc;

  /**
   * 备注
   */
  private String remarks;
}
