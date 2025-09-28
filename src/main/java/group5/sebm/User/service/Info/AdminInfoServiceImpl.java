package group5.sebm.User.service.Info;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.User.dao.AdminInfoMapper;
import group5.sebm.User.entity.AdminInfoPo;
import org.springframework.stereotype.Service;

/**
* @author Luoimo
* @description 针对表【adminInfo(管理员)】的数据库操作Service实现
* @createDate 2025-09-28 17:20:52
*/
@Service
public class AdminInfoServiceImpl extends ServiceImpl<AdminInfoMapper, AdminInfoPo>
    implements AdminInfoService{

}




