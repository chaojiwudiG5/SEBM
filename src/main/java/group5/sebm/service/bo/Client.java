package group5.sebm.service.bo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Client extends User{

  private int age;
  private boolean isActive;

  public Client(Integer id, String username, String password, Integer age) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.age = age;
  }

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
