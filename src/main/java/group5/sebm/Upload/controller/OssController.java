package group5.sebm.Upload.controller;

import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import group5.sebm.utils.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oss")
@Slf4j
public class OssController {

    @Autowired
    private OssService ossService;

    @Value("${aliyun.oss.bucketName}")
    private String bucket;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @GetMapping("/uploadUrl")
    public BaseResponse<Map<String, String>> getUploadUrl(@RequestParam String filename,
                                            @RequestParam String contentType) {
        String objectName = "uploads/" + System.currentTimeMillis() + "-" + filename;
        String signedUrl = ossService.generatePresignedUrl(objectName, contentType);

        String publicUrl = String.format("https://%s.%s/%s", bucket, endpoint, objectName);

        Map<String, String> result = new HashMap<>();
        result.put("uploadUrl", signedUrl);
        result.put("fileUrl", publicUrl);
        log.info("Upload URL: " + signedUrl);
        log.info("File URL: " + publicUrl);
        return ResultUtils.success(result);
    }
}
