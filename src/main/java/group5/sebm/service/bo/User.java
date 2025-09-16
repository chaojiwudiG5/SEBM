package group5.sebm.service.bo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class User {
    protected Integer id;
    protected String username;
    protected String password;
    protected boolean isActive;
    protected String role;
    //protected List<String> permission;
}
