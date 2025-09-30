package group5.sebm.BorrowRecord.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordAddDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordQueryDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordQueryWithStatusDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordReturnDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordRenewDto;
import group5.sebm.BorrowRecord.service.services.BorrowRecordService;
import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import group5.sebm.BorrowRecord.controller.vo.BorrowRecordVo;

@Slf4j
@RestController
@Tag(name = "Borrow")
@RequestMapping("/borrow")
@AllArgsConstructor
public class BorrowRecordController {

  @Resource
  private BorrowRecordService borrowRecordService;

  @PostMapping("/borrowDevice")
  public BaseResponse<BorrowRecordVo> borrowDevice(
      @RequestBody BorrowRecordAddDto borrowRecordAddDto,
      HttpServletRequest request) {
    BorrowRecordVo borrowRecordVo = borrowRecordService.borrowDevice(borrowRecordAddDto,
        request);
    log.info("addBorrowRecord success, borrowRecordVo: {}", borrowRecordVo);
    return ResultUtils.success(borrowRecordVo);
  }

  @PostMapping("/returnDevice")
  public BaseResponse<BorrowRecordVo> returnDevice(
      @RequestBody BorrowRecordReturnDto borrowRecordReturnDto,
      HttpServletRequest request) {
    BorrowRecordVo borrowRecordVo = borrowRecordService.returnDevice(borrowRecordReturnDto,
        request);
    log.info("returnDevice success, borrowRecordVo: {}", borrowRecordVo);
    return ResultUtils.success(borrowRecordVo);
  }

  @PostMapping("/getBorrowRecordList")
  public BaseResponse<List<BorrowRecordVo>> getBorrowRecordList(
      @RequestBody BorrowRecordQueryDto borrowRecordQueryDto) {
    Page<BorrowRecordVo> borrowRecordPage = borrowRecordService.getBorrowRecordList(
        borrowRecordQueryDto);
    log.info("getMyBorrowRecordList called with borrowRecordQueryDto: {}, borrowRecordPage: {}",
        borrowRecordQueryDto, borrowRecordPage);
    return ResultUtils.success(borrowRecordPage.getRecords());
  }

  @PostMapping("/getBorrowRecordListWithStatus")
  public BaseResponse<List<BorrowRecordVo>> getBorrowRecordListWithStatus(
      @RequestBody BorrowRecordQueryWithStatusDto borrowRecordQueryWithStatusDto) {
    Page<BorrowRecordVo> borrowRecordPage = borrowRecordService.getBorrowRecordListWithStatus(
        borrowRecordQueryWithStatusDto);
    log.info(
        "getBorrowRecordListWithStatus called with borrowRecordQueryWithStatusDto: {}, borrowRecordPage: {}",
        borrowRecordQueryWithStatusDto, borrowRecordPage);
    return ResultUtils.success(borrowRecordPage.getRecords());
  }

}