# 天然气工程—设备到货验收记录系统

基于 Spring Cloud 微服务 + Vue Vben Admin 的设备到货验收记录系统。前端集成 Ant Design Vue，后端通过 Gateway + JWT + Redis 完成统一鉴权，验收单可调用大模型 API 自动生成结构化验收结论。

## 模块与端口

| 模块 | 服务名 (Nacos) | 端口 | 职责 |
|------|---------------|------|------|
| demo-gateway  | boot-gateway  | 7001 | 边缘网关：路由分发 + JWT 鉴权 + CORS |
| demo-user     | boot-user     | 7101 | 用户登录、签发 JWT、用户信息缓存到 Redis |
| demo-supplier | boot-supplier | 7102 | 供应商管理（脚手架自带，本期未启用业务） |
| demo-business | boot-business | 7103 | 验收单主子表 / 状态流转 / 操作日志 / 统计 / Excel 导出 |
| demo-ai       | boot-ai       | 7104 | Spring AI ChatClient，提供 `/ai/summary/acceptance` 摘要接口 |
| demo-common   | —             | —    | JWT/Result/异常/操作日志注解 等公共组件 |
| demo-feign-client | —         | —    | Feign 接口契约 + DTO + Fallback |

前端：`vue-vben-admin/apps/web-antd`，端口 5888，dev 模式下 `/api` 通过 Vite 代理转发到 gateway:7001。

## 快速开始

1. **启动依赖容器**（mysql/redis/nacos 必需）：

   ```powershell
   cd docker
   docker compose up -d mysql redis nacos
   ```

2. **导入数据库脚本**：

   ```powershell
   Get-Content sql\init.sql | docker exec -i agent-start-mysql mysql -uroot -p"mysql@cool"
   ```

   会创建：`demo_user`、`demo_supplier`、`demo_business` 三个库。

3. **启动各微服务**（每个开一个终端）：

   ```powershell
   mvn -pl demo-user     spring-boot:run
   mvn -pl demo-business spring-boot:run
   mvn -pl demo-ai       spring-boot:run
   mvn -pl demo-gateway  spring-boot:run
   ```

4. **启动前端**：

   ```powershell
   cd vue-vben-admin
   pnpm install
   pnpm --filter @vben/web-antd dev
   ```

   访问 http://localhost:5888，默认用户名 `admin`（密码任意，不校验）。

## 鉴权流程

```
浏览器  ──登录──►  Gateway:7001/user/auth/login  (白名单放行)
                       │
                       ▼
                  demo-user:7101
                    1. DB 查 / 自动创建用户
                    2. 签发 JWT (HS256)
                    3. 把 userInfo JSON 写入 Redis：auth:token:{token}, TTL 24h
                    4. 返回 { accessToken, userInfo }
                       │
                       ▼
浏览器  ──后续业务请求, Header: Authorization Bearer ...──►  Gateway
                       │
                       ▼
        JwtAuthGlobalFilter:
          ① 签名校验
          ② Reactive 查 Redis（确认 token 仍在有效期）
          ③ 解析 payload，把 X-User-Id / X-User-Name / X-User-Roles 注入下游请求
                       │
                       ▼
                业务服务 (demo-business / demo-user / demo-ai)
                  UserContextFilter (servlet-only)
                    把 X-User-* 读入 ThreadLocal: UserContextHolder.get()
```

白名单（不需要 token）：`/user/auth/login`、`/actuator/**`。

退出登录走 `POST /user/auth/logout`，会删除 Redis 中的 token 缓存；客户端再发请求会被网关拦下，引导重新登录。

## 验收业务核心 API

| Method | Path | 说明 |
|--------|------|------|
| POST   | `/user/auth/login`                  | 登录并签发 token |
| POST   | `/user/auth/logout`                 | 退出登录 |
| GET    | `/user/auth/userinfo`               | 当前用户信息（gateway 注入 X-User-Id） |
| GET    | `/user/auth/codes`                  | 权限码（admin 全权限） |
| GET    | `/business/acceptance/page`         | 验收单分页 |
| GET    | `/business/acceptance/{id}`         | 验收单详情（含明细 + 整改） |
| POST   | `/business/acceptance`              | 新建验收单 |
| PUT    | `/business/acceptance`              | 更新验收单（终态不可编辑） |
| DELETE | `/business/acceptance/{id}`         | 删除（终态不可删） |
| PATCH  | `/business/acceptance/{id}/status`  | 状态流转（进入整改必填整改记录） |
| POST   | `/business/acceptance/{id}/ai-summary` | 调大模型生成 4 段式结构化摘要 |
| POST   | `/business/acceptance/{id}/save-summary` | 保存（人工修订后的）摘要 |
| GET    | `/business/acceptance/{id}/logs`    | 操作日志时间线 |
| GET    | `/business/acceptance/export`       | 导出 Excel（含主子表两层） |
| GET    | `/business/stats/supplier`          | 按供应商统计 |
| GET    | `/business/stats/device-type`       | 按设备类型统计 |
| POST   | `/ai/summary/acceptance`            | 内部 Feign：业务 → AI 服务摘要 |

## 状态机

```
PENDING → IN_PROGRESS
              ├─→ PASSED        （终态，不可编辑）
              ├─→ RETURNED      （终态，不可编辑）
              └─→ RECTIFYING ───→ RECTIFIED ─→ PASSED / RETURNED
```

进入 `RECTIFYING` 时必须填写整改内容；`RECTIFIED` 时切面会把该单的所有未完成整改记录置为完成。

## 大模型集成

* API Key 走 `demo-ai/src/main/resources/application.yml`，通过环境变量 `DEEPSEEK_API_KEY` 注入，**前端从不接触 Key**。
* 默认走 DeepSeek（OpenAI 兼容协议），可改 `DEEPSEEK_BASE_URL` / `DEEPSEEK_MODEL` 切到通义 / Ollama 等。
* 系统提示词在 `AiSummaryController.SYSTEM_PROMPT`，要求模型返回固定 JSON 字段。
* 后端做了：
  - 25 秒硬超时（504）；
  - JSON 解析失败时降级把原文塞到 `inspectionOverview` 字段，前端可继续编辑；
  - Feign Resilience4j 熔断 + fallback 防止下游异常拖死 demo-business。

## 操作日志

`@LogOperation` 注解 + `LogOperationAspect` 切面：成功执行的方法异步落 `t_operation_log`。`AcceptanceService` 内的关键操作（新增 / 修改 / 删除 / 状态流转 / AI 摘要）调用 `OperationLogService.writeManually` 显式记录，便于带上 before/after 信息。

## 操作演示

1. 登录 `admin` → 进入"验收统计看板"，看到 4 个汇总指标 + 供应商柱状图 + 合格率柱状图 + 设备类型饼图。
2. 左侧菜单"验收管理 → 验收单列表"，查看 3 条种子数据，按状态/订单号/供应商筛选。
3. 点"新建验收单"，填表 + 添加明细行 → 保存。
4. 详情页点"验收中" → "整改中"（填整改内容）→ "整改完成" → "验收通过"，观察右上角状态 tag 变化和"操作日志"时间线追加。
5. 点"生成摘要"调用 AI，结果填到下方 4 个字段；人工修改后点"保存摘要"持久化到 `summary_json`。
6. 列表页"导出 Excel"，浏览器直接下载 xlsx。

## 已知约束

* `demo-supplier` 沿用脚手架，本期不参与业务，gateway 仍保留 `/supplier/**` 路由（受同一套 JWT 校验保护）。
* JWT 没做 refreshToken，token 失效（默认 24h）后前端会自动跳回登录页。
* Excel 导出走 Apache POI，没分页一次性输出，数据量大时建议改流式（`SXSSFWorkbook`）。

## 关键技术点

| 关注点 | 实现位置 |
|--------|---------|
| Reactive 网关里读 Redis 校验 token | `demo-gateway/.../filter/JwtAuthGlobalFilter.java` |
| 把 servlet-only 的 UserContextFilter 与网关隔离 | `org.example.common.web.UserContextFilter` + `@ConditionalOnWebApplication(SERVLET)` |
| 验收单状态机 | `demo-business/.../domain/AcceptanceStatus.java` |
| AI 摘要 prompt + JSON 校验 | `demo-ai/.../controller/AiSummaryController.java` |
| Feign 熔断兜底 | `demo-feign-client/.../fallback/AiFeignFallback.java` |
| 前端登录 + 路由守卫 | `apps/web-antd/src/store/auth.ts` + `src/router/guard.ts` |
| 前端图表 | `apps/web-antd/src/views/dashboard/index.vue` |
