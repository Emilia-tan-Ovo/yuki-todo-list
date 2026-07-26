# YuKi's TODO List 项目状态

> 最后更新时间：2026-07-26

## 当前目标

- 实体层、基础 Repository 和数据库访问链路已经完成。
- 下一阶段进入用户认证模块。
- 当前第一步不是直接添加 Repository 查询方法，而是先梳理“用户注册”功能的 Service 业务流程。

## 当前进度

- 当前阶段：Spring Boot 后端基础项目、JPA 实体和基础 Repository 已完成，准备进入用户模块业务实现。
- 已完成数据库连接、实体映射验证和一次真实 Repository 查询。

## 正在进行

- 正在梳理用户注册业务的完整步骤。
- 尚未决定或添加注册所需的 Repository 条件查询。
- Service、Controller 和正式认证接口仍未开始实现。

## 本次更新

- 2026-07-26：根据 2026-07-25、2026-07-26 开发日志及 `backend/` 实际代码同步项目进度。
- 确认临时 `CommandLineRunner` 数据库测试代码已从当前源码中删除。
- 2026-07-26：完成 Maven 调用诊断，使用本机 Maven 绝对路径成功执行 `compile`。
- 后端后续开发顺序调整为由具体 Service 业务需求推导 Repository 方法，最后实现 Controller。

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

## 修改/新增文件

- 本次仅更新 `PROJECT_STATUS.md`。
- Maven 编译只更新了已被 Git 忽略的 `backend/target/` 构建产物。
- 未修改 Java 源码、Repository、`pom.xml`、Maven Wrapper、开发日志或其他设计文档。

## 测试方式

- 确认 `backend/mvnw.cmd`、`backend/.mvn/wrapper/` 和本机 Maven 可执行文件均存在。
- 在 `backend/` 中依次尝试 PowerShell Wrapper 和 `cmd /c` Wrapper。
- 使用 `D:\develop\Maven\apache-maven-3.9.16\bin\mvn.cmd compile` 执行编译。

## 测试结果

- PowerShell 与 `cmd /c` 调用 `mvnw.cmd` 均在启动 Maven 前失败，错误为 `Cannot index into a null array`。
- 原因定位到 Wrapper 脚本读取普通 `.m2` 目录的空 `Target` 属性后访问 `[0]`，不是项目 Java 代码的编译错误。
- 未永久修改 PATH，改用本机 Maven 绝对路径完成编译。
- Maven 编译结果：`BUILD SUCCESS`，使用 Java 21 编译 8 个源文件，未发现编译错误。
- 本次只执行 `compile`，未运行测试或重新验证数据库连接。

## 当前问题

- 三个 Repository 目前只有 `JpaRepository` 提供的基础方法，注册、登录所需的条件查询尚未定义。
- 用户模块 Service、请求/响应对象、Controller 和认证流程尚未实现。
- 数据库设计将 `password_hash` 定义为 `VARCHAR(255)`，当前 `User` 实体使用 `@Column(length = 100)`，两处长度不一致，待确认后再单独处理。
- Session、密码哈希、统一异常响应和用户数据隔离尚未通过业务代码验证。
- 当前环境中的 `mvnw.cmd` 仍存在上述启动问题；在不修改 Wrapper 的前提下，可使用 Maven 绝对路径执行构建。

## 后端开发原则

- 先从 Service 业务入手。
- 再分析需要访问的数据。
- 最后决定 Repository 方法。
- 不提前批量添加尚未被当前业务使用的查询。
- Controller 放在 Service 业务明确以后实现。

## 下一步

1. 阅读 API 设计和数据库设计中与用户注册有关的内容。
2. 用中文整理用户注册的完整业务流程。
3. 标出流程中需要访问数据库的步骤。
4. 根据这些步骤，推导 `UserRepository` 当前真正需要的方法。
5. 经学习确认后，再实现用户注册 Service。
6. 最后实现注册 Controller 和接口测试。
7. 注册流程完整跑通后，再使用相同方式实现登录。
