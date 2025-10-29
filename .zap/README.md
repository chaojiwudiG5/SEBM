# OWASP ZAP DAST 配置说明

本目录包含用于动态应用安全测试（DAST）的配置文件。

## 📋 文件说明

### `rules.tsv`
ZAP扫描规则配置文件，定义了要检测的安全问题类型和严重级别。

**规则格式：**
```
RULE_ID    THRESHOLD    COMMENT
```

**阈值说明：**
- `IGNORE`: 忽略此规则
- `INFO`: 信息级别
- `WARN`: 警告级别（推荐）
- `FAIL`: 失败级别（严格模式）

### `api-scan.yaml`
API扫描配置文件，定义了要扫描的API端点和安全检查项。

## 🚀 使用方法

### 1. 在GitHub Actions中自动运行

DAST测试已集成到GitHub Actions工作流中，每次推送到`main`分支或创建PR时都会自动运行。

工作流步骤：
1. ✅ 构建并启动应用
2. ⏳ 等待应用就绪
3. 🔍 运行ZAP Baseline扫描（快速扫描常见漏洞）
4. 🔍 运行ZAP Full扫描（深度扫描，可选）
5. 📊 生成并上传扫描报告

### 2. 本地运行

#### 使用Docker运行ZAP扫描

```bash
# 1. 启动您的应用（使用DAST配置）
java -jar target/*.jar --spring.profiles.active=dast

# 2. 在新终端运行ZAP扫描
docker run -v $(pwd)/.zap:/zap/wrk/:rw -t owasp/zap2docker-stable zap-baseline.py \
  -t http://host.docker.internal:29578 \
  -g gen.conf \
  -r report.html

# 3. 查看报告
open .zap/report.html
```

#### 使用ZAP桌面版

1. 下载并安装 [OWASP ZAP](https://www.zaproxy.org/download/)
2. 启动ZAP
3. 配置目标URL：`http://localhost:29578`
4. 运行自动扫描
5. 查看扫描结果

## 📊 扫描报告

扫描完成后，会生成三种格式的报告：

- **HTML报告** (`report_html.html`): 适合在浏览器中查看
- **Markdown报告** (`report_md.md`): 适合在GitHub中查看
- **JSON报告** (`report_json.json`): 适合程序化处理

在GitHub Actions中，这些报告会作为构建产物上传，可以在Actions运行页面下载。

## 🔧 自定义配置

### 添加新的扫描规则

编辑 `rules.tsv` 文件，添加新的规则行：

```
10111    WARN    # 新规则描述
```

### 调整API端点

编辑 `api-scan.yaml` 文件的 `endpoints` 部分：

```yaml
endpoints:
  - path: /api/your-endpoint
    method: GET
```

### 配置认证

如果您的API需要认证，在 `api-scan.yaml` 中取消注释并配置：

```yaml
authentication:
  type: form-based
  loginUrl: /api/user/login
  username: test_user
  password: test_password
```

### 排除特定路径

在 `api-scan.yaml` 的 `exclude` 部分添加不需要扫描的路径：

```yaml
exclude:
  - /admin/.*
  - /internal/.*
```

## 🔍 常见安全问题

ZAP会检测以下常见安全漏洞：

### 高危漏洞
- ❌ SQL注入
- ❌ 跨站脚本攻击 (XSS)
- ❌ 远程代码执行
- ❌ XML外部实体注入 (XXE)
- ❌ 不安全的反序列化

### 中危漏洞
- ⚠️ 跨站请求伪造 (CSRF)
- ⚠️ 不安全的直接对象引用
- ⚠️ 安全配置错误
- ⚠️ 敏感数据泄露

### 低危漏洞
- ℹ️ 缺少安全头
- ℹ️ Cookie安全属性缺失
- ℹ️ 信息泄露
- ℹ️ 版本信息暴露

## 📈 改进建议

如果扫描发现问题，建议采取以下措施：

### 1. 添加安全头
在Spring Boot中配置安全头：

```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers
            .contentSecurityPolicy("default-src 'self'")
            .xssProtection()
            .contentTypeOptions()
            .frameOptions().deny()
        );
        return http.build();
    }
}
```

### 2. 输入验证
使用Spring Validation进行输入验证：

```java
@PostMapping("/api/user/create")
public ResponseEntity<?> createUser(@Valid @RequestBody UserDto user) {
    // ...
}
```

### 3. SQL注入防护
使用参数化查询（MyBatis-Plus已默认防护）：

```java
@Select("SELECT * FROM users WHERE id = #{id}")
User selectById(@Param("id") Long id);
```

### 4. XSS防护
对输出进行HTML编码：

```java
import org.springframework.web.util.HtmlUtils;

String safe = HtmlUtils.htmlEscape(userInput);
```

## 🎯 CI/CD集成最佳实践

1. **定期扫描**: 在每次代码提交时运行DAST
2. **失败策略**: 根据严重性决定是否阻止部署
3. **报告归档**: 保存历史扫描报告以跟踪改进
4. **团队通知**: 发现高危漏洞时及时通知团队

## 📚 参考资源

- [OWASP ZAP官方文档](https://www.zaproxy.org/docs/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [ZAP GitHub Actions](https://github.com/zaproxy/action-baseline)
- [Spring Security文档](https://spring.io/projects/spring-security)

## 🤝 贡献

如果您发现配置可以改进或有新的安全检查建议，欢迎提交PR或Issue。

