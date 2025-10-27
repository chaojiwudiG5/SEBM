package group5.sebm.common.dto;

import lombok.Data;

@Data
public class DeviceDto {
  private Long id;

  private String deviceName;

  private String deviceType;

  private Integer status;

  private String location;

  private String description;

  private String image;
}
