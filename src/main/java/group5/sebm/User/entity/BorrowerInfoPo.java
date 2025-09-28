package group5.sebm.User.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 借用者
 * @TableName borrowerInfo
 */
@TableName(value ="borrowerInfo")
@Data
public class BorrowerInfoPo {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 逾期次数
     */
    private Integer overdueTimes;

    /**
     * 借用设备数
     */
    private Integer borrowedDeviceCount;

    /**
     * 预约设备数
     */
    private Integer reservedDeviceCount;

    /**
     * 最大可借用设备数
     */
    private Integer maxBorrowedDeviceCount;

    /**
     * 最大逾期次数
     */
    private Integer maxOverdueTimes;

    /**
     * 最大预约设备数
     */
    private Integer maxReservedDeviceCount;

    /**
     * 对应user表的id
     */
    private Long userId;
}