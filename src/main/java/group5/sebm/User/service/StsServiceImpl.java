//package group5.sebm.User.service;
//
//import com.qcloud.cos.utils.Jackson;
//import com.tencent.cloud.CosStsClient;
//import com.tencent.cloud.Policy;
//import com.tencent.cloud.Response;
//import com.tencent.cloud.Statement;
//import java.util.TreeMap;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
///**
// * Tencent Cloud STS Service Implementation
// *
// */
//@Service
//@Slf4j
//public class StsServiceImpl {
//
//    @Value("${tencent.cos.secretId}")
//    private String secretId;
//
//    @Value("${tencent.cos.secretKey}")
//    private String secretKey;
//
//    @Value("${tencent.cos.bucketName}")
//    private String bucketName;
//
//    @Value("${tencent.cos.region}")
//    private String region;
//
//    @Value("${tencent.cos.durationSeconds}")
//    private int durationSeconds;
//
//    public Response getTempCredentials() {
//        TreeMap<String, Object> config = new TreeMap<>();
//        try {
//            config.put("secretId", secretId);
//            config.put("secretKey", secretKey);
//            config.put("durationSeconds", durationSeconds);
//            config.put("bucket", bucketName);
//            config.put("region", region);
//
//            Policy policy = new Policy();
//            Statement statement = new Statement();
//            statement.setEffect("allow");
//
//            // 权限列表
//            statement.addActions(new String[]{
//                "cos:PutObject",
//                "cos:PostObject",
//                "cos:InitiateMultipartUpload",
//                "cos:ListMultipartUploads",
//                "cos:ListParts",
//                "cos:UploadPart",
//                "cos:CompleteMultipartUpload",
//                "ci:CreateMediaJobs",
//                "ci:CreateFileProcessJobs"
//            });
//
//            // 资源表达式，允许访问所有对象
//            statement.addResources(new String[]{
//                String.format("qcs::cos:%s:uid/%s:%s/*", region, bucketName.split("-")[1], bucketName),
//                String.format("qcs::ci:%s:uid/%s:bucket/%s/*", region, bucketName.split("-")[1], bucketName)
//            });
//
//            policy.addStatement(statement);
//            config.put("policy", Jackson.toJsonPrettyString(policy));
//            return CosStsClient.getCredential(config);
//        } catch (Exception e) {
//            log.error("获取临时密钥失败: {}", e.getMessage(), e);
//            throw new RuntimeException("Failed to get temporary credentials: " + e.getMessage());
//        }
//    }
//}
//
