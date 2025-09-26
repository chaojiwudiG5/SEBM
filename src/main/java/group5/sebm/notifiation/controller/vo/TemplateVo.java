package group5.sebm.notifiation.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 通知模板响应VO
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TemplateVo {
    
    /**
     * 模板ID
     */
    private Long id;
    
    /**
     * 模板标题
     */
    private String templateTitle;
    
    /**
     * 模板类型
     */
    private String templateType;
    
    /**
     * 通知描述
     */
    private String templateDesc;

    /**
     * 通知角色
     */
    private Integer notificationRole;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
