package group5.sebm.notifiation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.notifiation.controller.dto.CreateTemplateDto;
import group5.sebm.notifiation.controller.dto.TemplateQueryDto;
import group5.sebm.notifiation.controller.vo.TemplateVo;
import group5.sebm.notifiation.entity.TemplatePo;
import jakarta.servlet.http.HttpServletRequest;


/**
 * 通知模板服务接口
 */
public interface TemplateService {
    
    /**
     * 创建通知模板
     * @param createTemplateDto 创建模板请求DTO
     * @param request HTTP请求对象，用于获取当前用户信息
     * @return 创建的模板VO
     */
    TemplateVo createTemplate(CreateTemplateDto createTemplateDto, HttpServletRequest request);

    /**
     * 分页查询模板列表
     * @param templateQueryDto 查询条件
     * @return 分页结果
     */
    Page<TemplateVo> getTemplateList(TemplateQueryDto templateQueryDto);
    
    /**
     * 根据通知节点查询默认模板
     * @param notificationNode 通知节点
     * @return 模板实体
     */
    TemplatePo findTemplateByNode(Integer notificationNode, Integer notificationRole);
}
