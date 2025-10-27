package group5.sebm.notifiation.controller.dto;

import group5.sebm.common.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 管理员通知查询 DTO
 * 用于管理员查看所有已发送的通知，不受用户删除状态影响
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AdminNotificationQueryDto extends PageDto {
    
    /**
     * 用户ID（可选，用于筛选特定用户的通知）
     */
    private Long userId;
    
    /**
     * 标题关键词（可选）
     */
    private String titleKeyword;

    /**
     * 已读状态（可选），0-未读，1-已读
     */
    private Integer readStatus;

    /**
     * 删除状态（可选），0-未删除，1-已删除
     */
    private Integer isDelete;

    /**
     * 通知角色（可选），0-管理员，1-用户，2-技工
     */
    private Integer notificationRole;

    /**
     * 开始时间，秒级时间戳（可选）
     */
    private Long startTime;

    /**
     * 结束时间，秒级时间戳（可选）
     */
    private Long endTime;
}

