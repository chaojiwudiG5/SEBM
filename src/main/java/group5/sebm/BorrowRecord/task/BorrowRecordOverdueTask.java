package group5.sebm.BorrowRecord.task;

import group5.sebm.BorrowRecord.dao.BorrowRecordMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BorrowRecordOverdueTask {

  @Resource
  private BorrowRecordMapper borrowRecordMapper;

  /**
   * 每分钟执行一次，更新逾期借用记录
   */
  @Scheduled(cron = "0 * * * * ?") // 每分钟第0秒执行
  public void updateOverdueRecords() {
    int updatedCount = borrowRecordMapper.updateStatusToOverdue();
    if (updatedCount > 0) {
      log.info("BorrowRecordOverdueTask: 更新 {} 条逾期记录", updatedCount);
    }
  }
}
