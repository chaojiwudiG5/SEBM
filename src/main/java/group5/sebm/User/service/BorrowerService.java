package group5.sebm.User.service;

import group5.sebm.User.controller.dto.DeleteDto;

/**
 * @author
 * @description 针对 Borrower 用户的额外服务接口
 */
public interface BorrowerService extends UserService {
    Long deactivateUser(DeleteDto deleteDto);
}
