package group5.sebm.common.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMaintenanceRecordDto {
  private Long id;

  private Long deviceId;

  private Long userId;

  private String description;

  private String image;

  private Integer status;

  private Date createTime;

  private Date updateTime;
}
