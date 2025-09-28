package group5.sebm.User.service.bo;

import lombok.*;

/**
 * @author deshperaydon
 * Manager class representing a user with managerial privileges.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Admin extends User{
  private String level;
}
