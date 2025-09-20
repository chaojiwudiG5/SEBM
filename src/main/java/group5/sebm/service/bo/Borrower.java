package group5.sebm.service.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @description: Borrower class representing a user who borrows items.
 * @author: deshperaydon
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Borrower extends User{

  private Boolean isDelete;
  private int age;

  public void validate() {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("用户名不能为空");
    }
    if (password == null || password.trim().isEmpty()) {
      throw new IllegalArgumentException("密码不能为空");
    }
    // 可以添加更多业务规则，如密码复杂度、用户名长度等
    if (password.length() < 6) {
      throw new IllegalArgumentException("密码长度不能少于6位");
    }
  }

  public boolean isokforDiscount() {
    return this.age <= 18;
  }


}
