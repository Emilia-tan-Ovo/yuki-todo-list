# YuKi's TODO List 项目状态

> 最后更新时间：2026-07-28

## 当前目标

- 认证模块四个接口已经完成，并通过用户使用 Apifox 执行的成功与失败场景测试。
- 创建课程接口已经完成，并通过用户使用 Apifox 执行的成功、失败和边界场景测试。
- 下一阶段进入课程查询接口，首先分析 `GET /api/courses` 的业务流程。

## 当前进度

- 已完成 `POST /api/auth/register` 用户注册。
- 已完成 `POST /api/auth/login` 用户登录，支持使用用户名或邮箱登录。
- 已完成 `POST /api/auth/logout` 用户登出。
- 已完成 `GET /api/auth/me` 查询当前用户。
- 已完成 `POST /api/courses` 创建课程。
- 注册和登录成功后使用 Session 属性 `"userId"` 保存当前用户 id；需要登录的接口可据此识别当前用户。
- `ErrorResponse` 和 `GlobalExceptionHandler` 已统一处理认证及创建课程接口的参数校验和业务异常。
- 创建课程时，所属用户由后端根据 Session 中的 `userId` 查询并设置，请求 DTO 不包含 `userId`。
- 课程名称在查重和保存前执行 `trim()`，同一用户范围内使用忽略大小写的方式查重。
- 创建课程未提交 `color` 或提交 `null` 时，后端从 8 个预设颜色中随机选择；合法的前端颜色会被保留。
- 创建课程成功返回只包含 `id`、`name`、`color` 的响应 DTO，不直接暴露 `Course` 实体。
- 指定 Maven 命令已成功编译 27 个 Java 源文件，Java release 为 21，无警告或错误。

## 正在进行

- 认证模块和创建课程接口的代码实现、编译及用户手动测试均已完成。
- 正在准备分析课程列表接口 `GET /api/courses`，尚未实现课程查询代码。

## 本次更新

- 根据 `backend/src/main/java` 实际源码重新核对认证模块和创建课程完整链路。
- 根据用户已经完成的 Apifox 测试，同步四个认证接口及创建课程接口的成功、失败和边界场景结果。
- 将下一阶段目标调整为课程查询接口。
- 本次只整理项目状态，没有修改或新增后端业务代码。

## 完成内容

### 项目基础

- 完成 MVP、页面、数据库和 API v0.1 设计；任务状态统一为 `TODO / DONE`。
- 初始化 `backend/` Spring Boot 项目，使用 Java 21、Spring Boot 4.1.0 和 Maven。
- 配置 Web MVC、Validation、Spring Data JPA、MySQL Driver 和 BCrypt 密码编码。
- 配置 `yuki_todo` 数据源，通过 `${DB_PASSWORD}` 环境变量读取数据库密码。
- 配置 `spring.jpa.hibernate.ddl-auto=validate`，实体与现有数据库表结构验证通过。
- 完成 `User`、`Course`、`Task` 实体和 `TaskStatus` 枚举。
- 完成 `UserRepository`、`CourseRepository`、`TaskRepository` 基础数据访问层。
- 曾使用 `UserRepository.count()` 完成真实数据库访问链路测试；临时 `CommandLineRunner` 已删除。

### 认证模块

- `AuthController` 已提供注册、登录、登出和查询当前用户四个接口。
- `AuthService` 已完成注册、登录和查询当前用户业务。
- 已完成注册、登录和当前用户响应 DTO，以及请求字段校验。
- 注册使用 BCrypt 保存密码哈希；接口不返回明文密码或 `passwordHash`。
- 登录账号或密码错误统一返回 `401 INVALID_CREDENTIALS`。
- 未登录、重复登出或 Session 失效统一返回 `401 UNAUTHORIZED`。
- 注册、登录成功建立 Session；登出成功使 Session 失效并返回 `204 No Content`。

### 创建课程

- `CourseController` 已提供 `POST /api/courses`，并使用 `@Valid` 校验请求 DTO。
- `CourseService` 已完成查询当前用户、名称规范化、同名检查、颜色选择、实体保存和响应转换。
- `CourseRepository` 已提供 `existsByUser_IdAndNameIgnoreCase(Long userId, String name)`。
- `CourseAlreadyExistsException` 已映射为 `409 Conflict`，错误码为 `COURSE_ALREADY_EXISTS`，消息为“课程名称已存在”。
- 创建课程的前端请求不能提交或决定课程所属用户。
- 不同用户可以创建同名课程，同一用户不能创建忽略大小写后同名的课程。

## 修改/新增文件

- 本次仅修改 `PROJECT_STATUS.md`。
- 未修改 Java 源码、`pom.xml`、数据库结构、API 设计文档、开发日志、Maven Wrapper、`AGENTS.md` 或其他项目文件。

## 测试方式

- 编译记录来自创建课程实现完成后执行的：
  `D:\develop\Maven\apache-maven-3.9.16\bin\mvn.cmd compile`
- 接口结果来自用户使用 Apifox 完成的认证模块和创建课程手动测试。
- 本次状态同步通过只读检查 Controller、Service、DTO、Repository、实体、异常和全局异常处理核对实现。
- 本次 Codex 未启动后端，也未重新执行 Apifox 接口测试。

## 测试结果

- Maven：`BUILD SUCCESS`，编译 27 个 Java 源文件，Java release 21，无警告和错误。
- 注册、登录、登出和查询当前用户的成功与失败场景均已通过用户手动测试。
- 创建课程的成功、未登录、参数校验、随机颜色、自定义颜色、名称 `trim()`、同用户忽略大小写查重及不同用户同名场景均已通过用户手动测试。
- 创建课程返回的 HTTP 状态码和统一错误响应符合预期。
- 本次 Codex 未重新执行编译或接口测试。

## 当前问题

- 未发现会阻塞当前已完成功能的编译或运行问题。
- Session 属性名 `"userId"` 仍是分散使用的字符串字面量，后续可考虑统一为常量。
- 当前接口以 Apifox 手动测试为主，尚未建立自动化测试。
- 注册和创建课程都采用“先查询、再保存”的查重方式；数据库唯一约束冲突尚无并发兜底处理，极端并发下可能不能转换为约定的 `409 Conflict`。
- 随机课程颜色可能连续选择到相同颜色，当前 MVP 可以接受。
- API 文档与创建课程核心契约没有直接冲突，但尚未详细记录随机颜色池、名称 `trim()`、忽略大小写查重和 `COURSE_ALREADY_EXISTS` 错误响应。
- 当前环境中的 `mvnw.cmd` 仍存在启动问题；可继续使用本机 Maven 绝对路径执行构建。

## 后端开发原则

1. 选择一个具体功能。
2. 阅读 API 文档。
3. 用中文写出业务流程。
4. 确定请求和响应数据。
5. 标出需要访问数据库的步骤。
6. 推导 Repository 方法。
7. 在 Service 中实现业务。
8. 分析可能出现的业务错误。
9. 补 DTO 校验和异常处理。
10. 用 Controller 接收请求。
11. Maven 编译。
12. 使用 Apifox 测试。
13. 检查数据库并更新日志。

> 想业务 → 找数据 → 写 Service → 补边界 → 接 Controller → 做测试

## 下一步

1. 开始分析 `GET /api/courses`。
2. 根据 API 文档梳理课程列表业务流程。
3. 推导课程列表需要的 Repository 查询。
4. 实现课程列表响应 DTO。
5. 确保只返回当前登录用户的课程。
6. 统计每门课程的 `todoCount`。
7. 返回最近截止的最多 3 个 `TODO` 任务。
8. 使用 Maven 编译。
9. 使用 Apifox 测试成功、空列表、未登录和用户数据隔离。
