package group5.sebm.Maintenance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.Maintenance.controller.dto.MechanicanClaimDto;
import group5.sebm.Maintenance.controller.dto.MechanicanQueryDto;
import group5.sebm.Maintenance.controller.dto.MechanicanUpdateDto;
import group5.sebm.Maintenance.controller.vo.MechanicanMaintenanceRecordVo;
import group5.sebm.Maintenance.service.services.MechanicanMaintenanceRecordService;
import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
@Tag(name = "MechanicanMaintenanceRecord")
@RequestMapping("/mechanicanMaintenanceRecord")
@AllArgsConstructor
public class MechanicanMaintenanceRecordController {

    private final MechanicanMaintenanceRecordService mechanicanMaintenanceRecordService;

    @PostMapping("/claim")
    public BaseResponse<Long> claimMaintenanceTask(@RequestBody @Valid MechanicanClaimDto claimDto,
                                                   HttpServletRequest request) {
        Long mechanicId = (Long) request.getAttribute("userId");
        ThrowUtils.throwIf(mechanicId == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");
        Long recordId = mechanicanMaintenanceRecordService.claimMaintenanceTask(mechanicId, claimDto);
        log.info("Mechanic {} claimed maintenance task {}", mechanicId, recordId);
        return ResultUtils.success(recordId);
    }

    @PostMapping("/myList")
    public BaseResponse<Page<MechanicanMaintenanceRecordVo>> listMyTasks(
            @RequestBody @Valid MechanicanQueryDto queryDto, HttpServletRequest request) {
        Long mechanicId = (Long) request.getAttribute("userId");
        ThrowUtils.throwIf(mechanicId == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");
        Page<MechanicanMaintenanceRecordVo> page = mechanicanMaintenanceRecordService
                .listMechanicMaintenanceRecords(mechanicId, queryDto);
        log.info("Mechanic {} queried maintenance tasks page {}", mechanicId, page);
        return ResultUtils.success(page);
    }

    @GetMapping("/{id}")
    public BaseResponse<MechanicanMaintenanceRecordVo> getTaskDetail(@PathVariable("id") Long id,
                                                                     HttpServletRequest request) {
        Long mechanicId = (Long) request.getAttribute("userId");
        ThrowUtils.throwIf(mechanicId == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");
        MechanicanMaintenanceRecordVo record = mechanicanMaintenanceRecordService
                .getMechanicMaintenanceRecordDetail(mechanicId, id);
        log.info("Mechanic {} fetched maintenance task detail {}", mechanicId, id);
        return ResultUtils.success(record);
    }

    @PostMapping("/updateStatus")
    public BaseResponse<Boolean> updateTaskStatus(@RequestBody @Valid MechanicanUpdateDto updateDto,
                                                  HttpServletRequest request) {
        Long mechanicId = (Long) request.getAttribute("userId");
        ThrowUtils.throwIf(mechanicId == null, ErrorCode.NOT_LOGIN_ERROR, "未登录");
        Boolean result = mechanicanMaintenanceRecordService
                .updateMechanicMaintenanceRecord(mechanicId, updateDto);
        log.info("Mechanic {} updated maintenance task {} to status {}", mechanicId, updateDto.getId(),
                updateDto.getStatus());
        return ResultUtils.success(result);
    }
}