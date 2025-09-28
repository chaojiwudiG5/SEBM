package group5.sebm.User.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MechanicVo extends UserVo {

  /**
   * 技工等级
   */
  private Integer level;
}
