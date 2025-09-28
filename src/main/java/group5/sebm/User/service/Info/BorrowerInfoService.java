package group5.sebm.User.service.Info;

import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.User.entity.BorrowerInfoPo;

/**
* @author Luoimo
* @description 针对表【borrower_info(借用者)】的数据库操作Service
* @createDate 2025-09-28 15:48:22
*/
public interface BorrowerInfoService extends IService<BorrowerInfoPo> {
  Integer addOverdueTimes(Long userId);

  Integer updateBorrowedCount(Long userId, Integer num);
}
