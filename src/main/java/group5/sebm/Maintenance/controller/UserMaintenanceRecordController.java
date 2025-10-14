package group5.sebm.Maintenance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.Maintenance.controller.dto.UserCreateDto;
import group5.sebm.Maintenance.controller.dto.UserQueryDto;
import group5.sebm.Maintenance.controller.vo.UserMaintenanceRecordVo;
import group5.sebm.Maintenance.service.services.UserMaintenanceRecordService;
import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import group5.sebm.common.dto.DeleteDto;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "UserMaintenanceRecord")
@RequestMapping("/userMaintenanceRecord")
@AllArgsConstructor
public class UserMaintenanceRecordController {

    private final UserMaintenanceRecordService userMaintenanceRecordService;

    @PostMapping("/report")
    public BaseResponse<UserMaintenanceRecordVo> createMaintenanceRecord(@RequestBody @Valid UserCreateDto createDto, HttpServletRequest request)
    {
        Long userId = (Long) request.getAttribute("userId");
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "login required");
        UserMaintenanceRecordVo record = userMaintenanceRecordService.createMaintenanceRecord(userId, createDto);
        log.info("User {} created maintenance record {}", userId, record);
        return ResultUtils.success(record);
    }

    @PostMapping("/myList")
    public BaseResponse<List<UserMaintenanceRecordVo>> listMyRecords(@RequestBody @Valid UserQueryDto queryDto, HttpServletRequest request)
    {
        Long userId = (Long) request.getAttribute("userId");
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "login required");
        Page<UserMaintenanceRecordVo> page = userMaintenanceRecordService
                .listUserMaintenanceRecords(userId, queryDto);
        log.info("User {} queried maintenance records page {}", userId, page);
        return ResultUtils.success(page.getRecords());
    }

    @GetMapping("/{id}")
    public BaseResponse<UserMaintenanceRecordVo> getRecordDetail(@PathVariable("id") Long id, HttpServletRequest request)
    {
        Long userId = (Long) request.getAttribute("userId");
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "login required");
        UserMaintenanceRecordVo record = userMaintenanceRecordService
                .getUserMaintenanceRecordDetail(userId, id);
        log.info("User {} fetched maintenance record detail {}", userId, id);
        return ResultUtils.success(record);
    }

    @PostMapping("/cancel")
    public BaseResponse<Boolean> cancelRecord(@RequestBody @Valid DeleteDto deleteDto, HttpServletRequest request)
    {
        Long userId = (Long) request.getAttribute("userId");
        ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "login required");
        Boolean result = userMaintenanceRecordService.cancelMaintenanceRecord(userId, deleteDto.getId());
        log.info("User {} cancelled maintenance record {}", userId, deleteDto.getId());
        return ResultUtils.success(result);
    }
}
