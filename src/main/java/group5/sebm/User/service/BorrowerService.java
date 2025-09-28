package group5.sebm.User.service;

import group5.sebm.User.controller.dto.DeleteDto;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author
 * @description 针对 Borrower 用户的额外服务接口
 */
public interface BorrowerService extends UserService {
    Long deactivateUser(DeleteDto deleteDto);

    Integer addOverdueTimes(Long userId);

    Integer updateBorrowedCount(Long userId, Integer num);
}
