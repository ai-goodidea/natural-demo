# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目状态

Spring Cloud 微服务脚手架，已完成"最小可跑"基线：Nacos 服务发现 + 配置中心 / Gateway 路由 / MyBatis-Plus CRUD（user + supplier）/ OpenFeign 跨服务 / Spring AI 对话。所有模块能独立启动、通过 Nacos 自动注册、互相调通。

**前端目录 `vue-vben-admin/`** —— 暂未集成到后端，保持原样。

## 模块与端口

| 模块 | 服务名 (Nacos) | 端口 | 入口类 | 主要职责 |
|------|---------------|------|--------|---------|
| demo-common | — | — | — | 公共：`Result`、`ResultCode`、`BusinessException`、`GlobalExceptionHandler`、`BaseEntity`、`PageQuery` |
| demo-feign-client | — | — | — | 跨服务 Feign 接口契约 + DTO + Fallback |
| demo-gateway | boot-gateway | 7001 | `org.example.GatewayApplication` | Spring Cloud Gateway，按路径前缀分发到下游 |
| demo-user | boot-user | 7101 | `org.example.UserApplication` | 用户管理（MyBatis-Plus + MySQL，库名 `demo_user`） |
| demo-supplier | boot-supplier | 7102 | `org.example.SupplierApplication` | 供应商管理（MyBatis-Plus + MySQL，库名 `demo_supplier`） |
| demo-business | boot-business | 7103 | `org.example.BusinessApplication` | 业务聚合，通过 Feign 调 user + supplier |
| demo-ai | boot-ai | 7104 | `org.example.AiApplication` | Spring AI ChatClient，OpenAI 兼容协议 |

Gateway 路由规则（`demo-gateway/src/main/resources/application.yml`）：`/user/**` → boot-user，`/supplier/**` → boot-supplier，`/business/**` → boot-business，`/ai/**` → boot-ai。`spring.cloud.gateway.discovery.locator.enabled=true` 同时打开，可直接用 `lb://<service-name>` 路由。

## 技术栈与版本（根 `pom.xml`）

| 依赖 | 版本 | 说明 |
|------|------|------|
| Java | 21 | |
| Spring Boot | 3.4.5 | |
| Spring Cloud | 2024.0.1 | |
| Spring Cloud Alibaba | 2023.0.3.3 | 提供 Nacos discovery / config |
| MyBatis-Plus | 3.5.10.1 | 用 `mybatis-plus-spring-boot3-starter` |
| MySQL Connector/J | 9.1.0 | |
| Spring AI | 1.0.0 | 用 `spring-ai-starter-model-openai`（1.0 GA 改名） |
| Lombok | 1.18.34 | |
| Hutool | 5.8.32 | |

**版本兼容性注意**：Spring Cloud Alibaba 官方对 Spring Boot 3.4.x 的支持仍在收敛中。如果启动报 Nacos 相关 NoClassDefFoundError / NoSuchMethodError，**先把根 pom 的 Spring Boot 降到 3.2.x**（与 2023.0.3.3 测试过的组合）：parent 3.2.12，spring-cloud 2023.0.3。其他依赖无需改动。

## 启动与常用命令

**安装 Maven**：本机仅有 Java 21，没有 Maven。先装 Maven 3.9+ 并加 PATH，或用 IntelliJ 自带的 Maven。

```powershell
# 全量编译（在仓库根目录）
mvn clean install -DskipTests

# 启动开发依赖（MySQL + Nacos + 其他服务，docker-compose.yml 已配置完整栈）
cd docker
docker compose up -d mysql nacos      # 只起最小依赖
# 或起全部依赖（含 redis / rabbitmq / minio / es / kibana / milvus）
docker compose up -d

# 首次启动后，导入数据库初始化脚本（MySQL 容器密码 mysql@cool）
# Windows PowerShell：
Get-Content sql\init.sql | docker exec -i agent-start-mysql mysql -uroot -p"mysql@cool"

# 启动各微服务（每个开一个终端，建议顺序：gateway 之外的先起）
mvn -pl demo-user     spring-boot:run
mvn -pl demo-supplier spring-boot:run
mvn -pl demo-business spring-boot:run
mvn -pl demo-ai       spring-boot:run
mvn -pl demo-gateway  spring-boot:run

# 验证
curl http://localhost:7001/user/page          # gateway → user 分页
curl http://localhost:7001/user/1             # gateway → user 详情
curl http://localhost:7001/supplier/page      # gateway → supplier
curl http://localhost:7001/business/aggregate/1/1   # 聚合（Feign 调两个上游）
curl "http://localhost:7001/ai/chat?prompt=你好"    # AI 对话
```

Nacos 控制台 http://localhost:8848/nacos（默认 nacos/nacos），看左侧"服务列表"应能看到所有 `boot-*` 服务上线。

## 环境变量（所有服务可覆盖）

| 变量 | 默认 | 用途 |
|------|------|------|
| `NACOS_SERVER` | `127.0.0.1:8848` | Nacos 地址 |
| `NACOS_USERNAME` / `NACOS_PASSWORD` | `nacos` / `nacos` | Nacos 鉴权 |
| `NACOS_NAMESPACE` | `public` | Nacos 命名空间 |
| `SPRING_ACTIVE` | `dev` | profile |
| `MYSQL_HOST` / `MYSQL_PORT` | `127.0.0.1` / `3306` | MySQL |
| `MYSQL_USERNAME` / `MYSQL_PASSWORD` | `root` / `mysql@cool` | MySQL 凭据（默认对齐 `docker/docker-compose.yml` 里的 mysql 容器） |
| `OPENAI_API_KEY` / `OPENAI_BASE_URL` / `OPENAI_MODEL` | — / `https://api.openai.com` / `gpt-4o-mini` | demo-ai；可指向 DeepSeek/通义/Ollama 等 OpenAI 兼容服务 |

## 仓库结构要点

- **`_legacy/`** —— 用户从其他项目复制过来的高级网关代码（OAuth2 资源服务器 + JWT + Redis 动态路由 + Nacos 路由监听 + 鉴权管理器），暂存于此**不参与编译**。所有 `com.goodidea.*` 包都已废弃，要回搬时必须改包名为 `org.example.*` 并补 spring-security / oauth2-resource-server / spring-boot-starter-data-redis-reactive / fastjson2 等依赖。同目录还有原 demo-common 的 `AppResult` / `LangUtils`（依赖 fastjson2、commons-lang3 和废弃包，参考价值有限）。
- **`docker/`** —— 完整开发依赖栈（nginx、redis、mysql、rabbitmq、nacos、minio、etcd、milvus、elasticsearch、kibana）。本项目实际只用 mysql + nacos，其他按需启动。`docker-compose-nacos.yml` 是只起 nacos 的精简版。
- **`sql/init.sql`** —— 建库（`demo_user` + `demo_supplier`）和初始种子数据。
- **`vue-vben-admin/`** —— 前端项目，暂未与后端集成。

## 二次开发要点

- 新加微服务：参照 `demo-user` 复制一份 pom + bootstrap.yml + application.yml，调端口和服务名即可，自动注册到 Nacos。
- 跨服务调用：把 Feign 接口写到 `demo-feign-client`（业务方共享），调用方加 `@EnableFeignClients(basePackages = "org.example.feign.client")`。
- 全局异常：业务里抛 `BusinessException(ResultCode.XXX)`，已被 `demo-common` 的 `GlobalExceptionHandler` 统一捕获返回 `Result`。
- BaseEntity 的 `createTime` / `updateTime` 由 `MyBatisMetaHandler` 自动填充，每个服务都需要一份（已分别在 demo-user/demo-supplier 复制）。
- 想把 `_legacy/` 的 JWT 鉴权层叠回来：先在 demo-gateway pom 加 `spring-boot-starter-oauth2-resource-server` + `spring-boot-starter-data-redis-reactive` + `jjwt-api/jjwt-impl/jjwt-jackson`，再把 `_legacy/demo-gateway/src/main/java/org/example/gateway/` 整目录拷回 `demo-gateway/src/main/java/org/example/`，全局替换 `com.goodidea` → `org.example`，按报错逐个修。

## 已知坑

- 根 pom **没有** `mvnw`/Maven Wrapper，`.mvn/` 目录是空的。要么装系统 Maven，要么用 IntelliJ 自带 Maven 跑。
- Spring AI 1.0 GA 的 starter 已从 `spring-ai-openai-spring-boot-starter` 改名为 `spring-ai-starter-model-openai`，老教程的坐标拷过来会 404。
- MyBatis-Plus 在 Spring Boot 3 必须用 `mybatis-plus-spring-boot3-starter`，普通 `mybatis-plus-boot-starter` 是 Boot 2.x 的，引入会 ClassNotFound。
- 全局逻辑删除字段名 `deleted` 写在 `application.yml` 是全局默认，只有实体里加 `@TableLogic` 注解才生效；当前实体都没加，是安全的。
- demo-business 的 `feign.circuitbreaker.enabled=true` 依赖 `spring-cloud-starter-circuitbreaker-resilience4j`（已在 pom 中），否则 Feign 的 `fallback` 不会生效。
