package group5.sebm.controller.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserVo {
    private Integer id;
    private String username;
    private Integer age;
    public UserVo(Integer id, String username, Integer age) {
        this.id = id;
        this.username = username;
        this.age = age;
    }

}
