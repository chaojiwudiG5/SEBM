package group5.sebm.OssTest;

import group5.sebm.Upload.controller.OssController;
import group5.sebm.common.BaseResponse;
import group5.sebm.utils.OssService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OssControllerTest {

    @InjectMocks
    private OssController ossController;

    @Mock
    private OssService ossService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 给 @Value 注入模拟值
        ReflectionTestUtils.setField(ossController, "bucket", "my-bucket");
        ReflectionTestUtils.setField(ossController, "endpoint", "oss-cn-test.aliyuncs.com");
    }

    @Test
    void testGetUploadUrl() {
        String filename = "test.png";
        String contentType = "image/png";
        String fakeSignedUrl = "http://signed-url.com";

        // mock OssService 生成签名 URL
        when(ossService.generatePresignedUrl(anyString(), eq(contentType))).thenReturn(fakeSignedUrl);

        BaseResponse<Map<String, String>> response = ossController.getUploadUrl(filename, contentType);

        assertNotNull(response);
        Map<String, String> data = response.getData();
        assertNotNull(data);
        assertEquals(fakeSignedUrl, data.get("uploadUrl"));

        String expectedFileUrlPrefix = "https://my-bucket.oss-cn-test.aliyuncs.com/uploads/";
        assertTrue(data.get("fileUrl").startsWith(expectedFileUrlPrefix));

        // 验证 OssService 调用
        verify(ossService, times(1)).generatePresignedUrl(anyString(), eq(contentType));
    }
}
