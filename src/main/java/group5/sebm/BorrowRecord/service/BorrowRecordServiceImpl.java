package group5.sebm.BorrowRecord.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.BorrowRecord.dao.BorrowRecordMapper;
import group5.sebm.BorrowRecord.entity.BorrowRecord;
import group5.sebm.BorrowRecord.service.services.BorrowRecordService;
import org.springframework.stereotype.Service;


/**
* @author Luoimo
* @description 针对表【borrowRecord(设备借用记录表)】的数据库操作Service实现
* @createDate 2025-09-26 11:27:18
*/
@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord>
    implements BorrowRecordService {

}




