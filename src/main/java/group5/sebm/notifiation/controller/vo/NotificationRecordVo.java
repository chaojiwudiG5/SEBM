package group5.sebm.notifiation.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知记录 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRecordVo {
    
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 通知任务ID
     */
    private Long notificationTaskId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 通知标题（来自任务表）
     */
    private String title;
    
    /**
     * 通知内容（来自任务表）
     */
    private String content;
    
    /**
     * 通知角色（来自任务表）
     */
    private Integer notificationRole;
    
    /**
     * 通知方式 (1-邮件, 2-短信, 3-站内信)
     */
    private Integer notificationMethod;
    
    /**
     * 发送状态 (0-未发送, 1-发送成功, 2-发送失败)
     */
    private Integer status;
    
    /**
     * 发送状态描述
     */
    private String statusDesc;
    
    /**
     * 已读状态 (0-未读, 1-已读)
     */
    private Integer readStatus;
    
    /**
     * 已读状态描述
     */
    private String readStatusDesc;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    
    /**
     * 错误信息
     */
    private String errorMsg;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

