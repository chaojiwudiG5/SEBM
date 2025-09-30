package group5.sebm.BorrowRecord.service.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordAddDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordQueryDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordQueryWithStatusDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordReturnDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordRenewDto;
import group5.sebm.BorrowRecord.controller.vo.BorrowRecordVo;
import group5.sebm.BorrowRecord.entity.BorrowRecordPo;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author Luoimo
* @description 针对表【borrowRecord(设备借用记录表)】的数据库操作Service
* @createDate 2025-09-26 11:27:18
*/
public interface BorrowRecordService extends IService<BorrowRecordPo> {

  BorrowRecordVo borrowDevice(BorrowRecordAddDto borrowRecordAddDto, HttpServletRequest request);

  Page<BorrowRecordVo> getBorrowRecordList(BorrowRecordQueryDto borrowRecordQueryDto);

  BorrowRecordVo returnDevice(BorrowRecordReturnDto borrowRecordReturnDto, HttpServletRequest request);

  Page<BorrowRecordVo> getBorrowRecordListWithStatus(BorrowRecordQueryWithStatusDto borrowRecordQueryWithStatusDto);

}
