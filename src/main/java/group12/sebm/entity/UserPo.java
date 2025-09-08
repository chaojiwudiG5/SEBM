package group12.sebm.entity;
import lombok.Data;

@Data
public class UserPo {
    private Long id;
    private String username;
    private String password;
    public UserPo(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
