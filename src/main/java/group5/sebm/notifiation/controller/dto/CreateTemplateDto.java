package group5.sebm.notifiation.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建通知模板请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTemplateDto {
    
    /**
     * 模板标题
     */
    @NotBlank(message = "模板标题不能为空")
    private String templateTitle;
    
    /**
     * 模板类型
     */
    @NotBlank(message = "模板类型不能为空")
    private String templateType;
    
    /**
     * 通知节点 (使用NotificationNodeEnum的code值)
     */
    @NotNull(message = "通知节点不能为空")
    private Integer notificationNode;

    /**
     * 通知节点 (使用NotificationMethodEnum的code值)
     */
    @NotNull(message = "通知方式不能为空")
    private List<Integer> notificationMethod;

    /**
     * 相关时间偏移（秒）
     */
    private Integer relateTimeOffset;

    /**
     * 内容
     */
    @NotBlank(message = "模板内容不能为空")
    private String content;

    /**
     * 通知角色(使用NotificationRoleEnum的code值)
     */
    private Integer notificationRole;

    /**
     * 模版描述
     */
    private String templateDesc;
}
