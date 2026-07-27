# YuKi's TODO List 项目状态

> 最后更新时间：2026-07-27

## 当前目标

- 用户注册功能已完成并通过一次真实 HTTP 请求测试。
- 下一阶段先补测注册失败场景。
- 完成后按照业务驱动流程开始用户登录功能。

## 当前进度

- 当前阶段：用户注册的 Controller → Service → Repository → MySQL → Session 完整链路已实现。
- `AuthService.register()`、注册 DTO、请求校验、BCrypt 密码编码和注册所需 Repository 方法已完成。
- `PasswordMismatchException`、`AccountAlreadyExistsException`、`ErrorResponse` 和 `GlobalExceptionHandler` 已完成。
- `AuthController` 已提供 `POST /api/auth/register`，注册成功写入 Session 并返回 `201 Created`。
- 2026-07-27 用户使用 Apifox 完成一次真实注册测试，并将注册请求保存为正式接口。

## 正在进行

- 已完成注册代码与 API 设计的一致性核对。
- 正在补充用户注册失败场景测试。

## 本次更新

- 2026-07-26：根据 2026-07-25、2026-07-26 开发日志及 `backend/` 实际代码同步项目进度。
- 确认临时 `CommandLineRunner` 数据库测试代码已从当前源码中删除。
- 2026-07-26：完成 Maven 调用诊断，使用本机 Maven 绝对路径成功执行 `compile`。
- 后端后续开发顺序调整为由具体 Service 业务需求推导 Repository 方法，最后实现 Controller。
- 2026-07-27：完成 `AuthService.register()`、注册异常处理、请求校验、`AuthController` 和 Session 建立。
- 2026-07-27：Apifox 注册请求返回 `201 Created`，响应包含 id、username、email，并保存了 Session Cookie。
- 本次收尾检查确认当前注册主链路与 API 设计一致。

## 完成内容

- 完成 MVP、页面、数据库和 API v0.1 设计；任务状态统一为 `TODO / DONE`。
- 初始化 `backend/` Spring Boot 项目，当前使用 Java 21、Spring Boot 4.1.0 和 Maven。
- 配置 Web MVC、Validation、Spring Data JPA、MySQL Driver 等基础依赖。
- 配置 `yuki_todo` 数据源，通过 `${DB_PASSWORD}` 环境变量读取数据库密码。
- 配置 `spring.jpa.hibernate.ddl-auto=validate`，启动时实体与现有表结构验证通过。
- 完成 `User`、`Course`、`Task` 实体和 `TaskStatus` 枚举。
- 建立 `Course → User`、`Task → Course` 的多对一实体关联。
- 创建 `UserRepository`、`CourseRepository`、`TaskRepository`，均继承 `JpaRepository`。
- 使用临时 `CommandLineRunner` 调用 `UserRepository.count()`，完成真实数据库查询测试并查询成功。
- 数据库查询测试完成后，临时 `CommandLineRunner` 已删除。
- `UserRepository` 已提供注册当前需要的 `existsByUsername`、`existsByEmail` 和继承自 `JpaRepository` 的 `save`。
- 已配置 BCrypt `PasswordEncoder` Bean，并在 `AuthService` 中通过构造器注入。
- 完成 `AuthService.register()`，返回值只包含用户 id、用户名和邮箱。
- `User.passwordHash` 映射长度现为 255，与数据库设计一致。
- `RegisterRequest` 已使用 `@NotBlank`、`@Email`、`@Size` 定义字段校验，并由 Controller 的 `@Valid` 触发。
- 密码不一致映射为 `400 Bad Request`；用户名或邮箱重复映射为 `409 Conflict`。
- 参数校验失败通过 `GlobalExceptionHandler` 返回统一的 `ErrorResponse`。
- 注册成功后 Session 使用属性名 `userId` 保存新用户 id。
- 当前源码未发现 `UserService` 的导包、变量名或引用残留。

## 修改/新增文件

- 本次仅更新 `PROJECT_STATUS.md`。
- Maven 编译只更新了已被 Git 忽略的 `backend/target/` 构建产物。
- 未修改 Java 源码、`pom.xml`、数据库、API 文档、开发日志、Maven Wrapper 或其他项目文件。

## 测试方式

- 使用 `D:\develop\Maven\apache-maven-3.9.16\bin\mvn.cmd compile` 执行编译。
- 逐个检查注册 Controller、Service、DTO、Repository、实体、密码配置、业务异常和全局异常处理。
- 对照 API 文档核对路径、状态码、响应字段、Session 和错误响应。
- 根据 2026-07-27 日志记录用户已经完成的 Apifox 外部测试。

## 测试结果

- Maven 编译结果：`BUILD SUCCESS`。
- 本次 Maven 判断全部 class 已是最新，重新编译源文件数为 0；当前主源码目录共有 17 个 Java 文件。
- 编译过程没有警告或错误。
- 用户在 2026-07-27 使用 Apifox 实测注册成功：返回 `201 Created`，响应包含 id、username、email，并收到 Session Cookie。
- 本次 Codex 未启动后端，也未重新执行接口测试。
- 当前响应和日志代码未发现返回或记录明文密码、`passwordHash` 的情况。

## 当前问题

- 阻塞问题：未发现。
- 功能问题：数据库唯一约束异常没有额外兜底处理；如果查重后并发写入发生冲突，目前可能无法转换为 API 约定的 `409 Conflict`。
- 改进建议：密码不一致、字段校验失败、用户名或邮箱重复等注册失败场景尚未通过 Apifox 实测。
- 改进建议：Session 属性名 `"userId"` 目前是字符串字面量，后续认证接口增多时可再统一为常量。
- 改进建议：参数校验失败目前只返回第一条字段错误，当前 MVP 可以接受，后续可根据前端展示需要决定是否返回全部错误。
- 改进建议：当前没有用户注册自动化测试。
- 当前环境中的 `mvnw.cmd` 仍存在上述启动问题；在不修改 Wrapper 的前提下，可使用 Maven 绝对路径执行构建。

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

1. 使用 Apifox 测试两次密码不一致。
2. 测试邮箱格式错误或必填字段为空。
3. 测试用户名或邮箱重复。
4. 确认错误状态码和统一响应体。
5. 开始用中文梳理用户登录业务流程。
6. 从登录业务推导所需 Repository 方法。
7. 再实现登录 DTO、Service、异常处理和 Controller。
