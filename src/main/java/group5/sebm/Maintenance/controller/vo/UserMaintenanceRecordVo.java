package group5.sebm.Maintenance.controller.vo;

import java.util.Date;
import lombok.Data;

/**
 * 用户报修单响应体
 */
@Data
public class UserMaintenanceRecordVo {

    private Long id;

    private String deviceName;

    private Long userId;

    private String description;

    private String image;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}