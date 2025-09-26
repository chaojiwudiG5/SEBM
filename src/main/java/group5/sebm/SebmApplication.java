package group5.sebm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("group5.sebm.User.dao")
@MapperScan("group5.sebm.Device.dao")
@MapperScan("group5.sebm.BorrowRecord.dao")
@MapperScan("group5.sebm.Maintenance.dao")
public class SebmApplication {

  public static void main(String[] args) {
    SpringApplication.run(SebmApplication.class, args);
  }
}
