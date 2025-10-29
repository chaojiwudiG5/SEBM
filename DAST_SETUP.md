# DAST 测试配置完成 ✅

## 📝 变更摘要

为项目成功添加了DAST（动态应用安全测试）集成，使用OWASP ZAP作为扫描工具。

## 🔧 修改的文件

### 1. `.github/workflows/main.yml`
添加了新的CI作业 `dast-scan`，包含以下步骤：
- ✅ 构建应用并打包JAR
- ✅ 使用DAST专用配置启动应用（`application-dast.yml`）
  - 自动切换到H2内存数据库
  - 禁用Redis、RabbitMQ、邮件等外部依赖
  - 监听端口29578（应用默认端口）
- ✅ 智能健康检查（尝试多个端点，详细日志）
- ✅ 运行OWASP ZAP Baseline扫描（快速扫描，10分钟超时）
- ✅ 运行OWASP ZAP Full扫描（深度扫描，15分钟超时）
- ✅ 生成并上传扫描报告（HTML/JSON/Markdown格式）
- ✅ 在GitHub Actions Summary中显示结果
- ✅ 所有扫描步骤设置宽松模式，不会导致CI失败

### 2. `.zap/rules.tsv`（运行时生成）
配置了40+条安全扫描规则，包括：
- 信息泄露检测
- XSS漏洞扫描
- 安全头检查
- Cookie安全验证
- 服务器配置检查

### 3. `.zap/api-scan.yaml`（新建）
API扫描配置模板，定义了：
- 所有主要API端点（用户、设备、借用、维护、通知、审计）
- 扫描策略和强度
- 安全检查项清单
- 认证配置模板
- 路径排除规则

### 4. `.zap/README.md`（新建）
详细的使用文档，包括：
- 配置文件说明
- 本地运行指南
- 自定义配置方法
- 常见安全问题列表
- 改进建议和最佳实践

### 5. `.gitignore`
添加了ZAP扫描报告文件的忽略规则

### 6. `src/main/resources/application-dast.yml`（新建）
DAST测试专用配置文件，特点：
- ✅ 使用H2内存数据库替代MySQL
- ✅ 禁用Redis缓存
- ✅ 禁用RabbitMQ消息队列
- ✅ 禁用邮件发送功能
- ✅ 禁用阿里云OSS
- ✅ 最小化日志输出
- ✅ 完全独立运行，无外部依赖

## 🚀 如何使用

### 自动运行（推荐）
1. 将代码推送到 `main` 分支
2. 创建Pull Request到 `main` 分支
3. GitHub Actions会自动运行DAST扫描
4. 在Actions页面查看扫描结果和下载报告

### 本地运行
```bash
# 启动应用（使用DAST配置）
java -jar target/*.jar --spring.profiles.active=dast

# 在另一个终端运行ZAP扫描
docker run -v $(pwd)/.zap:/zap/wrk/:rw -t owasp/zap2docker-stable zap-baseline.py \
  -t http://host.docker.internal:29578 \
  -r report.html
```

## 📊 扫描报告

扫描完成后会生成三种格式的报告：
- `report_html.html` - HTML格式，适合浏览器查看
- `report_md.md` - Markdown格式，适合GitHub显示
- `report_json.json` - JSON格式，适合程序化处理

在GitHub Actions中，报告会作为artifacts上传（名称：`dast-zap-reports`），可在Actions运行页面下载，保留30天。

## 🔍 扫描内容

DAST测试会检测以下安全问题：

### 高危漏洞
- ❌ SQL注入
- ❌ 跨站脚本攻击(XSS)
- ❌ 远程代码执行
- ❌ 路径遍历
- ❌ 命令注入

### 中危漏洞
- ⚠️ 跨站请求伪造(CSRF)
- ⚠️ 信息泄露
- ⚠️ 不安全的重定向
- ⚠️ Cookie安全问题

### 低危漏洞
- ℹ️ 缺失安全头
- ℹ️ 服务器信息泄露
- ℹ️ 内容安全策略未设置

## ⚙️ 配置说明

### ⭐ 宽松模式（当前配置）

**当前配置采用宽松模式，确保DAST扫描不会导致CI失败：**

✅ **所有扫描步骤都设置了 `continue-on-error: true`**
✅ **所有扫描规则的严重级别最高为 WARN（警告）**
✅ **没有任何规则会导致构建失败（无FAIL级别）**
✅ **添加了超时限制，防止扫描时间过长**

### 调整扫描严格度

修改 `.github/workflows/main.yml` 中的配置：

**1. 调整失败行为：**
```yaml
fail_action: false        # true = 发现漏洞时失败构建
continue-on-error: true   # 即使步骤失败也继续执行
```

**2. 自定义扫描规则阈值：**
编辑工作流中的 `rules.tsv` 内容，调整规则阈值：
- `IGNORE` - 完全忽略此规则
- `INFO` - 仅作信息提示（当前大部分规则）
- `WARN` - 警告级别（当前严重问题规则）
- `FAIL` - 失败级别（会导致构建失败，**当前未使用**）

**3. 调整超时时间：**
```yaml
timeout-minutes: 10  # Baseline扫描超时
timeout-minutes: 15  # Full扫描超时
```

### 添加API端点
编辑 `.zap/api-scan.yaml`，在 `endpoints` 部分添加新端点。

## 🎯 CI/CD工作流程

```
┌─────────────────┐
│   Push到main    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  build-and-scan │
│  ✓ Maven构建    │
│  ✓ Snyk SCA     │
│  ✓ Snyk SAST    │
│  ✓ 单元测试      │
│  ✓ SonarQube    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   dast-scan     │
│  ✓ 启动应用      │
│  ✓ ZAP扫描      │
│  ✓ 生成报告      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  扫描报告上传    │
│  • HTML报告     │
│  • JSON报告     │
│  • Markdown报告 │
└─────────────────┘
```

## 📈 性能影响

- **Baseline扫描时间**: 约2-5分钟
- **Full扫描时间**: 约5-15分钟
- **总CI时间增加**: 约10-20分钟

Full扫描设置为 `continue-on-error: true`，不会因超时而阻塞CI。

## 🔐 安全最佳实践

DAST扫描后，建议采取以下措施：

1. **添加安全响应头**
   ```java
   http.headers()
       .contentSecurityPolicy("default-src 'self'")
       .xssProtection()
       .contentTypeOptions();
   ```

2. **启用HTTPS**
   ```yaml
   server:
     ssl:
       enabled: true
   ```

3. **配置Cookie安全属性**
   ```yaml
   server:
     servlet:
       session:
         cookie:
           secure: true
           http-only: true
           same-site: strict
   ```

## 📚 参考资源

- [OWASP ZAP官方文档](https://www.zaproxy.org/docs/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [ZAP Baseline Scan Action](https://github.com/zaproxy/action-baseline)
- [ZAP Full Scan Action](https://github.com/zaproxy/action-full-scan)

## ✨ 下一步

1. ✅ 提交并推送代码
2. ✅ 观察第一次DAST扫描结果
3. ✅ 根据报告修复发现的安全问题
4. ✅ 调整扫描配置以适应项目需求
5. ✅ 定期审查扫描报告

## 🔧 故障排查

### 问题1：应用启动失败或卡住

**症状**：健康检查一直失败，应用无法响应

**解决方案**：
1. 检查应用日志（在GitHub Actions日志中查看）
2. 确认 `application-dast.yml` 配置正确
3. 验证H2数据库依赖已添加到 `pom.xml`
4. 检查是否有必须的外部服务未正确禁用

### 问题2：DAST扫描超时

**症状**：扫描运行时间过长被强制终止

**解决方案**：
```yaml
# 调整超时时间
timeout-minutes: 20  # 增加到20分钟
```

### 问题3：扫描报告显示大量误报

**症状**：报告中有许多不适用的安全问题

**解决方案**：
1. 调整扫描规则，将误报规则设为 `IGNORE`
2. 在 `api-scan.yaml` 中添加排除路径
3. 使用 `-m 3` 限制扫描深度（当前为5）

### 问题4：CI因为DAST失败

**症状**：DAST扫描失败导致整个CI流程失败

**解决方案**：
当前配置已经设置了宽松模式，不应该出现此问题。如果仍然失败：
1. 确认所有DAST步骤都有 `continue-on-error: true`
2. 确认 `fail_action: false` 已设置
3. 检查是否是应用启动失败（非扫描失败）

### 问题5：本地无法复现CI中的问题

**症状**：本地运行正常，CI中失败

**解决方案**：
```bash
# 在本地使用相同的配置测试
mvn clean package -DskipTests
java -jar target/*.jar --spring.profiles.active=dast

# 在另一个终端测试
curl http://localhost:29578
```

### 获取帮助

如果遇到问题：
1. 查看GitHub Actions的完整日志
2. 检查应用日志（app.log）
3. 查看 [OWASP ZAP文档](https://www.zaproxy.org/docs/)
4. 在项目Issues中提问

---

**配置完成时间**: 2025-10-29  
**测试工具**: OWASP ZAP  
**集成方式**: GitHub Actions  
**扫描模式**: 宽松模式（不会导致CI失败）

