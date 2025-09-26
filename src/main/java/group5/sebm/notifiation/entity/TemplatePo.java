package group5.sebm.notifiation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知模板实体类
 */
@Data
@TableName("notificationTemplate")
public class TemplatePo {
    
    /**
     * 模板ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 模板标题
     */
    @TableField("templateTitle")
    private String templateTitle;
    
    /**
     * 模板类型
     */
    @TableField("templateType")
    private String templateType;
    
    /**
     * 通知节点
     */
    @TableField("notificationNode")
    private String notificationNode;

    /**
     * 通知角色
     */
    @TableField("notificationRole")
    private String notificationRole;
    
    /**
     * 相关时间偏移
     */
    @TableField("relateTimeOffset")
    private Integer relateTimeOffset;
    
    /**
     * 用户ID
     */
    @TableField("userId")
    private Long userId;
    
    /**
     * 内容
     */
    @TableField("content")
    private String content;

    /**
     * 模版描述
     */
    @TableField("templateDesc")
    private String templateDesc;
    
    /**
     * 状态
     */
    @TableField("status")
    private String status;
    
    /**
     * 是否删除 (0-未删除, 1-已删除)
     */
    @TableField("isDelete")
    private Integer isDelete;
    
    /**
     * 创建时间
     */
    @TableField("createTime")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField("updateTime")
    private LocalDateTime updateTime;
}
