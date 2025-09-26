package group5.sebm.BorrowRecord.service.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordAddDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordQueryDto;
import group5.sebm.BorrowRecord.controller.vo.BorrowRecordVo;
import group5.sebm.BorrowRecord.entity.BorrowRecord;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author Luoimo
* @description 针对表【borrowRecord(设备借用记录表)】的数据库操作Service
* @createDate 2025-09-26 11:27:18
*/
public interface BorrowRecordService extends IService<BorrowRecord> {

  BorrowRecordVo addBorrowRecord(BorrowRecordAddDto borrowRecordAddDto, HttpServletRequest request);

  Page<BorrowRecordVo> getBorrowRecordList(BorrowRecordQueryDto borrowRecordQueryDto);
}
