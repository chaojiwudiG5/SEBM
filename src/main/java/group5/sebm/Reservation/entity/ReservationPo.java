package group5.sebm.Reservation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 设备预约表
 * @TableName reservation
 */
@TableName(value ="reservation")
@Data
public class ReservationPo {
    /**
     * 预约ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 预约人ID，关联user表
     */
    private Long userId;

    /**
     * 预约设备ID，关联device表
     */
    private Long deviceId;

    /**
     * 预约开始时间
     */
    private Date reserveStart;

    /**
     * 预约结束时间
     */
    private Date reserveEnd;

    /**
     * 预约状态 0 - 未确认 1 - 已确认 2 - 已取消 3 - 已过期
     */
    private Integer status;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}