package group5.sebm.Maintenance.controller.vo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户报修单响应体
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMaintenanceRecordVo {

    private Long id;

    private Long deviceId;

    private String deviceName;

    private Long userId;

    private String description;

    private String image;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}