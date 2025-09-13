package group5.sebm.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user") // 数据库表名
public class UserPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String password;
    private Integer age;

    public UserPo() {} // JPA 必须有默认构造器

    public UserPo(Integer id, String username, String password, Integer age) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.age = age;
    }
}

