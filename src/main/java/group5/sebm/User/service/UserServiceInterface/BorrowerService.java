package group5.sebm.User.service.UserServiceInterface;

import group5.sebm.User.controller.dto.DeleteDto;

/**
 * @author
 * @description 针对 Borrower 用户的额外服务接口
 */
public interface BorrowerService extends UserService {

    /**
     * 用户注销
     *
     * @param deleteDto 用户id
     * @return 是否删除成功
     */
    Boolean deactivateUser(DeleteDto deleteDto);

    Boolean updateBorrowedCount(Long userId, Integer borrowedCount);
}
