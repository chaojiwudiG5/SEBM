package group5.sebm.notifiation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户退订实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("notificationUnsubscribe")
public class UnsubscribePo {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("userId")
    private Long userId;

    /**
     * 对应模板的 notificationEvent 字段（用于按事件退订）
     */
    @TableField("notificationEvent")
    private Integer notificationEvent;

    /**
     * 是否删除（软删除） 0-未删除 1-已删除
     */
    @TableField("isDelete")
    private Integer isDelete;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField("updateTime")
    private LocalDateTime updateTime;
}

