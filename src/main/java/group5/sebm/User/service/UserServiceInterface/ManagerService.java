package group5.sebm.User.service.UserServiceInterface;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.User.controller.dto.DeleteDto;
import group5.sebm.User.controller.dto.PageDto;
import group5.sebm.User.controller.vo.UserVo;

import java.util.List;

/**
 * @author Deshperaydon
 * @date 2025/9/26
 */
public interface ManagerService {
    Boolean deleteBorrower(DeleteDto deleteDto);
    Boolean deleteBorrowers(List<Long> ids);
    Page<UserVo> getAllBorrowers(PageDto pageDto);
}
