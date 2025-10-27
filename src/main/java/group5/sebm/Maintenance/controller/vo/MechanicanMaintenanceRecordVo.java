package group5.sebm.Maintenance.controller.vo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 技工维修单响应体
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MechanicanMaintenanceRecordVo {

    private Long id;

    private Long deviceId;

    private Long userId;

    private String description;

    private String image;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private Long userMaintenanceRecordId;
}