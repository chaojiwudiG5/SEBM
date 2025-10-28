package group5.sebm.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDto {
  private Long id;

  private String deviceName;

  private String deviceType;

  private Integer status;

  private String location;

  private String description;

  private String image;
}
