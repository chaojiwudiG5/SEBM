package group5.sebm.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("user") // 数据库表名
public class UserPo {

  @TableId
  private Integer id;
  private String username;
  private String password;
  private Integer age;
  private Integer role;
}
