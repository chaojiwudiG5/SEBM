package group5.sebm.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {

  private Long id;
  private String username;
  private String password;
  private Integer age;

  public UserDto(Long id, String username, String password, Integer age) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.age = age;
  }

}
