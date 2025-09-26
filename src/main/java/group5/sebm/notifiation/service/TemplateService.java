package group5.sebm.notifiation.service;

import group5.sebm.notifiation.controller.dto.CreateTemplateDto;
import group5.sebm.notifiation.controller.vo.TemplateVo;
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
}
