package group5.sebm.service.bo;

import lombok.*;
import org.springframework.stereotype.Repository;

/**
 * @author deshperaydon
 * Manager class representing a user with managerial privileges.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Manager extends User{
    private Boolean isDelete;
}
