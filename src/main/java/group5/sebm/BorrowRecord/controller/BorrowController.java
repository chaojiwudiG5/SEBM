package group5.sebm.BorrowRecord.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Borrow")
@RequestMapping("/borrow")
@AllArgsConstructor
public class BorrowController {

}
