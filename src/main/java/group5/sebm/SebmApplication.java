package group5.sebm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("group5.sebm.User.dao")
@MapperScan("group5.sebm.Device.dao")
@MapperScan("group5.sebm.BorrowRecord.dao")
@MapperScan("group5.sebm.Maintenance.dao")
@MapperScan("group5.sebm.Reservation.dao")
@EnableCaching
public class SebmApplication {

  public static void main(String[] args) {
    SpringApplication.run(SebmApplication.class, args);
  }
}
