package group5.sebm.User.service.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @description: Borrower class representing a user who borrows items.
 * @author: deshperaydon
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Borrower extends User{

  private Boolean isDelete;
  private Integer age;


  public boolean isokforDiscount() {
    return this.age <= 18;
  }


}
