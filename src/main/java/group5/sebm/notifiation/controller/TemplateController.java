package group5.sebm.notifiation.controller;

import group5.sebm.annotation.AuthCheck;
import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import group5.sebm.common.constant.NotificationConstant;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import group5.sebm.notifiation.controller.dto.CreateTemplateDto;
import group5.sebm.notifiation.controller.vo.TemplateVo;
import group5.sebm.notifiation.enums.NotificationMethodEnum;
import group5.sebm.notifiation.enums.NotificationNodeEnum;
import group5.sebm.notifiation.enums.NotificationRoleEnum;
import group5.sebm.notifiation.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知模板控制器
 */
@Slf4j
@RestController
@Tag(name = "通知模板管理", description = "管理员通知模板相关接口")
@RequestMapping("/template")
@AllArgsConstructor
public class TemplateController {
    
    private final TemplateService templateService;
    
    /**
     * 创建通知模板 - 仅限管理员
     * @param createTemplateDto 创建模板请求DTO
     * @param request HTTP请求对象
     * @return 创建的模板信息
     */
    @PostMapping("/admin/create")
    @AuthCheck(mustRole = "admin")
    @Operation(summary = "创建通知模板", description = "管理员创建新的通知模板")
    public BaseResponse<TemplateVo> createTemplate(@RequestBody @Valid CreateTemplateDto createTemplateDto,
            HttpServletRequest request) {
        
        // 参数验证
        validateCreateTemplateParams(createTemplateDto);
        
        TemplateVo templateVo = templateService.createTemplate(createTemplateDto, request);
        log.info("createTemplate Dto: {}, Vo: {}", createTemplateDto, templateVo);
        
        return ResultUtils.success(templateVo);
    }
    
    /**
     * 验证创建模板的参数
     * @param createTemplateDto 创建模板请求DTO
     */
    private void validateCreateTemplateParams(CreateTemplateDto createTemplateDto) {
        // 验证通知节点是否有效
        ThrowUtils.throwIf(!NotificationNodeEnum.isValidCode(createTemplateDto.getNotificationNode()),
                ErrorCode.PARAMS_ERROR);

        // 验证通知节点是否有效
        ThrowUtils.throwIf(!NotificationRoleEnum.isValidCode(createTemplateDto.getNotificationRole()),
                ErrorCode.PARAMS_ERROR);
        
        // 验证模板标题长度
        ThrowUtils.throwIf(!StringUtils.hasText(createTemplateDto.getTemplateTitle()) || 
                createTemplateDto.getTemplateTitle().length() > NotificationConstant.MAX_TEMPLATE_TITLE_LENGTH,
                ErrorCode.PARAMS_ERROR);
        
        // 验证模板类型
        ThrowUtils.throwIf(!StringUtils.hasText(createTemplateDto.getTemplateType()), ErrorCode.PARAMS_ERROR);
        
        // 验证模板内容长度
        ThrowUtils.throwIf(!StringUtils.hasText(createTemplateDto.getContent()) || 
                createTemplateDto.getContent().length() > NotificationConstant.MAX_TEMPLATE_CONTENT_LENGTH,
                ErrorCode.PARAMS_ERROR);
        
        // 验证时间偏移量（如果不为空）
        if (createTemplateDto.getRelateTimeOffset() != null) {
            ThrowUtils.throwIf(createTemplateDto.getRelateTimeOffset() < NotificationConstant.MIN_TIME_OFFSET,
                    ErrorCode.PARAMS_ERROR);
            
            // 限制最大时间偏移量
            ThrowUtils.throwIf(createTemplateDto.getRelateTimeOffset() > NotificationConstant.MAX_TIME_OFFSET_SECONDS,
                    ErrorCode.PARAMS_ERROR);
        }

        // 验证传入的通知方式是否合法
        ThrowUtils.throwIf(!NotificationMethodEnum.isValidCode(createTemplateDto.getNotificationMethod()),
                ErrorCode.PARAMS_ERROR);
    }
}
