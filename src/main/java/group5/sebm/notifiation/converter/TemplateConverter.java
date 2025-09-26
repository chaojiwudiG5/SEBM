package group5.sebm.notifiation.converter;


import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import group5.sebm.notifiation.controller.dto.CreateTemplateDto;
import group5.sebm.notifiation.controller.vo.TemplateVo;
import group5.sebm.notifiation.entity.TemplatePo;
import jakarta.servlet.http.HttpServletRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;

/**
 * 通知模板对象转换器
 */
@Mapper(componentModel = "spring")
public interface TemplateConverter {

    /**
     * CreateTemplateDto 转 TemplatePo
     * @param createTemplateDto 创建模板DTO
     * @param request HTTP请求对象
     * @return 模板PO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "request", qualifiedByName = "getUserIdFromRequest")
    @Mapping(target = "isDelete", constant = "0")
    @Mapping(target = "createTime", expression = "java(getCurrentTime())")
    @Mapping(target = "updateTime", expression = "java(getCurrentTime())")
    TemplatePo toPo(CreateTemplateDto createTemplateDto, HttpServletRequest request);

    /**
     * TemplatePo 转 TemplateVo
     * @param templatePo 模板PO
     * @return 模板VO
     */
    //@Mapping(target = "notificationNodeDesc", source = "notificationNode", qualifiedByName = "mapNotificationNodeDesc")
    TemplateVo toVo(TemplatePo templatePo);

    /**
     * 从 HttpServletRequest 中获取当前用户ID
     * @param request HTTP请求对象
     * @return 用户ID
     */
    @Named("getUserIdFromRequest")
    default Long getUserIdFromRequest(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        return userId;
    }

    
    /**
     * 获取当前时间
     * @return 当前时间
     */
    default LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }
}