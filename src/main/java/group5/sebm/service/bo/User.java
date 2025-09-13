package group5.sebm.service.bo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class User {
    private Integer id;
    private String username;
    private String password;
    private Integer age;

    public User(Integer id, String username, String password, Integer age) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.age = age;
    }

    public boolean isokforDiscount() {
        return this.age <= 18;
    }

}
