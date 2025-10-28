package group5.sebm.Maintenance.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MechanicRecordQueryDto {
  /**
   * 设备筛选
   */
  @NotNull(message = "DeviceId cannot be null.")
  private Long deviceId;

  /**
   * 状态筛选
   */
  @NotNull(message = "Status cannot be null.")
  private Integer status;
}
