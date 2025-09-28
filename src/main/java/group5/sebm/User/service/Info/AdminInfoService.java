package group5.sebm.User.service.Info;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.User.controller.dto.DeleteDto;
import group5.sebm.User.controller.dto.PageDto;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.entity.AdminInfoPo;
import java.util.List;

/**
* @author Luoimo
* @description 针对表【adminInfo(管理员)】的数据库操作Service
* @createDate 2025-09-28 17:20:52
*/
public interface AdminInfoService extends IService<AdminInfoPo> {
}
