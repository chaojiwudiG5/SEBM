package group5.sebm.User.controller;//package group5.sebm.User.controller;
//
//import com.tencent.cloud.Response;
//import group5.sebm.common.BaseResponse;
//import group5.sebm.common.ResultUtils;
//import group5.sebm.exception.BusinessException;
//import group5.sebm.exception.ErrorCode;
//import group5.sebm.User.service.StsServiceImpl;
//import jakarta.annotation.Resource;
//import java.util.HashMap;
//import java.util.Map;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/cos")
//public class StsController {
//
//    @Resource
//    private StsServiceImpl stsServiceImpl;
//
//    @GetMapping("/getTempCredentials")
//    public BaseResponse<Map<String, String>> getTempCredentials() {
//        Map<String, String> credentials = new HashMap<>();
//        try {
//            Response response = stsServiceImpl.getTempCredentials();
//            credentials.put("tmpSecretId", response.credentials.tmpSecretId);
//            credentials.put("tmpSecretKey", response.credentials.tmpSecretKey);
//            credentials.put("sessionToken", response.credentials.sessionToken);
//            credentials.put("startTime", String.valueOf(response.startTime));
//            credentials.put("expiredTime", String.valueOf(response.expiredTime));
//        } catch (Exception e) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"Tencent COS error");
//        }
//        return ResultUtils.success(credentials);
//    }
//}
