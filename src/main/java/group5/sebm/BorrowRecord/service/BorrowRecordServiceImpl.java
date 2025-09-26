package group5.sebm.BorrowRecord.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordAddDto;
import group5.sebm.BorrowRecord.controller.dto.BorrowRecordQueryDto;
import group5.sebm.BorrowRecord.controller.vo.BorrowRecordVo;
import group5.sebm.BorrowRecord.dao.BorrowRecordMapper;
import group5.sebm.BorrowRecord.entity.BorrowRecord;
import group5.sebm.BorrowRecord.service.services.BorrowRecordService;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.service.BorrowerService;
import group5.sebm.User.service.UserService;
import group5.sebm.User.service.UserServiceImpl;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


/**
 * @author Luoimo
 * @description 针对表【borrowRecord(设备借用记录表)】的数据库操作Service实现
 * @createDate 2025-09-26 11:27:18
 */
@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord>
    implements BorrowRecordService {

  @Resource
  private BorrowRecordMapper borrowRecordMapper;
  @Resource
  private BorrowerService borrowerService;

  @Override
  public BorrowRecordVo addBorrowRecord(BorrowRecordAddDto borrowRecordAddDto,
      HttpServletRequest request) {
    //1. 校验参数
    UserVo currentUser = borrowerService.getCurrentUser(request);
    ThrowUtils.throwIf(currentUser.getId() != borrowRecordAddDto.getUserId(),
        ErrorCode.NO_AUTH_ERROR, "无权限操作");
    //2. 保存记录
    BorrowRecord borrowRecord = new BorrowRecord();
    BeanUtils.copyProperties(borrowRecordAddDto, borrowRecord);
    borrowRecordMapper.insert(borrowRecord);
    //3. 返回结果
    BorrowRecordVo borrowRecordVo = new BorrowRecordVo();
    BeanUtils.copyProperties(borrowRecord, borrowRecordVo);
    return borrowRecordVo;
  }

  @Override
  public Page<BorrowRecordVo> getBorrowRecordList(BorrowRecordQueryDto borrowRecordQueryDto) {
    //1. 根据userId查询记录
    Page<BorrowRecord> page = new Page<>(borrowRecordQueryDto.getPageNumber(), borrowRecordQueryDto.getPageSize());
    //2. 分页查询
    Page<BorrowRecord> recordPage = borrowRecordMapper.selectPage(page,
        new QueryWrapper<BorrowRecord>().eq("userId", borrowRecordQueryDto.getUserId()));
    //3. 转换结果
    Page<BorrowRecordVo> resultPage = new Page<>();
    BeanUtils.copyProperties(recordPage, resultPage);
    resultPage.setRecords(recordPage.getRecords().stream().map(borrowRecord -> {
      BorrowRecordVo borrowRecordVo = new BorrowRecordVo();
      BeanUtils.copyProperties(borrowRecord, borrowRecordVo);
      return borrowRecordVo;
    }).toList());
    return resultPage;
  }
}




