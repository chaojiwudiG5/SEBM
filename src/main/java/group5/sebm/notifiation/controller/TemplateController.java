package group5.sebm.notifiation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.annotation.AuthCheck;
import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import group5.sebm.common.constant.NotificationConstant;
import group5.sebm.common.enums.UserRoleEnum;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import group5.sebm.notifiation.controller.dto.CreateTemplateDto;
import group5.sebm.notifiation.controller.dto.TemplateQueryDto;
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
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/create")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
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
     * 分页查询通知模板列表 - 仅限管理员
     * @param templateQueryDto 查询条件
     * @return 分页结果
     */
    @PostMapping("/list")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @Operation(summary = "分页查询通知模板", description = "管理员分页查询通知模板列表")
    public BaseResponse<Page<TemplateVo>> getTemplateList(@RequestBody @Valid TemplateQueryDto templateQueryDto) {
        log.info("查询模板列表，参数：{}", templateQueryDto);
        
        Page<TemplateVo> templateVoPage = templateService.getTemplateList(templateQueryDto);
        
        return ResultUtils.success(templateVoPage);
    }
    
    /**
     * 根据ID查询模板详情 - 仅限管理员
     * @param templateId 模板ID
     * @return 模板详情
     */
    @GetMapping("/detail/{templateId}")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @Operation(summary = "查询模板详情", description = "根据ID查询模板详细信息")
    public BaseResponse<TemplateVo> getTemplateDetail(@PathVariable Long templateId) {
        log.info("查询模板详情，ID：{}", templateId);
        
        // 这里需要添加一个根据ID查询VO的方法，或者直接使用现有的方法
        // 暂时返回null，实际项目中需要实现
        return ResultUtils.success(null);
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
