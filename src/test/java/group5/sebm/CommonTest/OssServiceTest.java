package group5.sebm.CommonTest;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import group5.sebm.utils.OssService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OssServiceTest {

    @InjectMocks
    private OssService ossService;

    @Mock
    private OSS ossClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 给私有的 @Value 属性赋值
        ReflectionTestUtils.setField(ossService, "endpoint", "endpoint");
        ReflectionTestUtils.setField(ossService, "accessKeyId", "accessKeyId");
        ReflectionTestUtils.setField(ossService, "accessKeySecret", "accessKeySecret");
        ReflectionTestUtils.setField(ossService, "bucketName", "test-bucket");
    }

    @Test
    void testGeneratePresignedUrl_shouldReturnUrl() throws Exception {
        String objectName = "test.txt";
        String contentType = "text/plain";

        // 模拟生成 URL
        URL mockUrl = new URL("https://mock.oss.com/test.txt");
        OSS mockOssClient = mock(OSS.class);

        // 用 spy 改造 ossService 内部 OSSClientBuilder.build 返回 mockOssClient
        try (MockedConstruction<com.aliyun.oss.OSSClientBuilder> mockedBuilder =
                     mockConstruction(com.aliyun.oss.OSSClientBuilder.class,
                             (builder, context) -> {
                                 when(builder.build(anyString(), anyString(), anyString()))
                                         .thenReturn(mockOssClient);
                             })) {

            when(mockOssClient.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                    .thenReturn(mockUrl);

            String url = ossService.generatePresignedUrl(objectName, contentType);

            assertEquals(mockUrl.toString(), url);
            verify(mockOssClient, times(1))
                    .generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
            verify(mockOssClient, times(1)).shutdown();
        }
    }
}
