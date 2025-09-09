package group12.sebm.entity;
import lombok.Data;

@Data
public class UserPo {
    private Integer id;
    private String username;
    private String password;
    private Integer age;
    public UserPo(Integer id, String username, String password, Integer age) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.age = age;
    }
}
