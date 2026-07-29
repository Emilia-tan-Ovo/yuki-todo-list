# YuKi's TODO List 项目状态

> 最后更新时间：2026-07-29

## 当前目标

- 认证模块四个接口已经完成，并通过 Apifox 成功与失败场景测试。
- 课程模块五个接口已经全部完成，并通过 Maven 编译和真实 HTTP 请求测试。
- 下一阶段进入任务模块，先分析 API 文档中的第一个任务接口 `POST /api/tasks`。

## 当前进度

### 认证模块

- `POST /api/auth/register` 用户注册：已完成。
- `POST /api/auth/login` 用户登录：已完成，支持用户名或邮箱。
- `POST /api/auth/logout` 用户登出：已完成。
- `GET /api/auth/me` 查询当前用户：已完成。
- 注册和登录成功后建立 Session；登出后 Session 失效。
- 未登录、Session 失效或 Session 用户不存在时返回 `401 UNAUTHORIZED`。

### 课程模块

- `POST /api/courses` 创建课程：已完成。
- `GET /api/courses` 查询当前用户课程列表：已完成。
- `GET /api/courses/{courseId}` 查询单门课程：已完成。
- `PUT /api/courses/{courseId}` 修改课程：已完成。
- `DELETE /api/courses/{courseId}` 删除课程：已完成。
- 创建和查询课程均从已有 Session 读取 `userId`，前端不能提交或决定课程所属用户。
- 课程列表只查询当前用户的数据，并按照 `createdAt ASC` 排列。
- 每门课程返回 `todoCount` 和截止日期最早的最多 3 条 `TODO` 任务。
- `DONE` 任务不参与 `todoCount` 和 `nearestTasks`。
- 没有课程时返回 `{"courses":[]}`；没有 TODO 任务时返回 `nearestTasks: []`。
- 课程列表使用响应 DTO，不直接返回 `Course` 或 `Task` 实体。
- 查询单门课程会同时按 `courseId + userId` 查询并校验课程归属。
- 课程不存在或属于其他用户时统一返回 `404 COURSE_NOT_FOUND`，避免泄露其他用户的课程是否存在。
- 课程详情返回 `todoCount` 和 `doneCount`，不直接返回实体或用户信息。
- 修改课程会对名称执行 `trim()`，忽略大小写查重并排除当前课程自身。
- 删除课程前会校验课程归属，关联任务由数据库外键 `ON DELETE CASCADE` 删除。

## 正在进行

- 课程模块五个接口的代码、编译、真实 HTTP 测试和数据库对照均已完成。
- 正在准备分析 `POST /api/tasks` 创建任务业务流程，尚未编写任务接口代码。

## 本次更新

- 确认 `PUT /api/courses/{courseId}` 和 `DELETE /api/courses/{courseId}` 已实现。
- 两个接口完成后 Maven 编译 34 个 Java 源文件并 `BUILD SUCCESS`。
- PUT 的名称 `trim()`、排除当前课程自身的忽略大小写查重、完整参数校验、`404` 和 `409` 均验证正常。
- DELETE 的 `204 No Content`、用户数据隔离和数据库 `ON DELETE CASCADE` 均验证正常。
- 两个接口的真实 HTTP 测试全部通过。
- 临时用户、课程和任务已全部清理，原有数据未删除；测试后端已停止，8080 端口已释放。
- 本轮状态同步未重新编译或执行接口测试。
- 课程模块五个接口全部完成，下一阶段进入任务模块。
- 根据 API 文档，将下一步确定为分析 `POST /api/tasks`。

## 完成内容

### 项目基础

- 完成 MVP、页面结构、数据库结构和 API v0.1 设计；任务状态统一为 `TODO / DONE`。
- 初始化 `backend/` Spring Boot 项目，使用 Java 21、Spring Boot 4.1.0 和 Maven。
- 配置 Web MVC、Validation、Spring Data JPA、MySQL Driver 和 BCrypt 密码编码。
- 配置 `yuki_todo` 数据源，通过 `${DB_PASSWORD}` 环境变量读取数据库密码。
- 配置 `spring.jpa.hibernate.ddl-auto=validate`，实体与现有数据库表结构验证通过。
- 完成 `User`、`Course`、`Task` 实体和 `TaskStatus` 枚举。
- 完成 `UserRepository`、`CourseRepository` 和 `TaskRepository` 基础数据访问层。
- 曾使用 `UserRepository.count()` 完成真实数据库访问链路测试；临时 `CommandLineRunner` 已删除。

### 认证功能

- 完成注册、登录、登出和查询当前用户的 Controller、Service、DTO、Repository 查询和异常处理。
- 注册使用 BCrypt 保存密码哈希；接口不返回明文密码或 `passwordHash`。
- 登录失败统一返回 `401 INVALID_CREDENTIALS`。
- 未登录、重复登出或 Session 失效统一返回 `401 UNAUTHORIZED`。
- `ErrorResponse` 和 `GlobalExceptionHandler` 已用于统一错误响应。

### 创建课程

- 创建课程名称会先执行 `trim()`，同一用户范围内忽略大小写查重。
- 未提交 `color` 或提交 `null` 时，从 8 个预设颜色中随机选择。
- 同名课程返回 `409 COURSE_ALREADY_EXISTS`。
- 不同用户可以创建同名课程。

### 查询课程列表

- `CourseController` 已提供 `GET /api/courses`。
- `CourseService.getCourses()` 会确认 Session 用户存在，并只查询该用户的课程。
- 课程使用 `findByUser_IdOrderByCreatedAtAsc` 按创建时间从早到晚查询。
- `todoCount` 使用 `countByCourse_IdAndStatus(..., TaskStatus.TODO)` 统计。
- `nearestTasks` 使用 `findTop3ByCourse_IdAndStatusOrderByDeadlineAsc(..., TaskStatus.TODO)` 查询。
- 响应 DTO 层级为：
  `CourseListResponse → CourseSummaryResponse → NearestTaskResponse`。

### 查询单门课程

- `CourseController` 已提供 `GET /api/courses/{courseId}`。
- Controller 使用 `@PathVariable` 接收 `courseId`，并从已有 Session 读取 `userId`。
- `CourseService.getCourseDetail()` 会先确认 Session 用户仍然存在。
- 课程使用 `findByIdAndUser_Id` 同时完成查询和用户归属校验。
- TODO 和 DONE 数量复用 `countByCourse_IdAndStatus` 分别统计。
- 响应使用 `CourseDetailResponse`，只返回 `id`、`name`、`color`、`todoCount` 和 `doneCount`。
- 课程不存在或不属于当前用户时，`CourseNotFoundException` 由 `GlobalExceptionHandler` 转换为 `404 COURSE_NOT_FOUND`。

### 修改课程

- `CourseController` 已提供 `PUT /api/courses/{courseId}`。
- `UpdateCourseRequest` 完整接收并校验 `name` 和 `color`。
- 课程名称在查重和保存前执行 `trim()`。
- `existsByUser_IdAndNameIgnoreCaseAndIdNot` 会忽略大小写查重，并排除当前课程自身。
- 修改成功返回只包含 `id`、`name` 和 `color` 的 `UpdateCourseResponse`。

### 删除课程

- `CourseController` 已提供 `DELETE /api/courses/{courseId}`。
- 删除前使用 `findByIdAndUser_Id` 同时查询课程并校验用户归属。
- 删除成功返回 `204 No Content`。
- `tasks.course_id` 外键已确认包含 `ON DELETE CASCADE`，删除课程时由数据库级联删除关联任务。

## 修改/新增文件

- 查询单门课程实现新增：
  - `CourseDetailResponse.java`
  - `CourseNotFoundException.java`
- 查询单门课程实现修改：
  - `CourseController.java`
  - `CourseService.java`
  - `CourseRepository.java`
  - `GlobalExceptionHandler.java`
- 修改和删除课程实现新增：
  - `UpdateCourseRequest.java`
  - `UpdateCourseResponse.java`
- 修改和删除课程实现修改：
  - `CourseController.java`
  - `CourseService.java`
  - `CourseRepository.java`
- 本轮状态同步仅修改 `PROJECT_STATUS.md`，未修改源码、API 文档、开发日志、数据库或配置文件。

## 测试方式

- Maven 编译：
  `D:\develop\Maven\apache-maven-3.9.16\bin\mvn.cmd compile`
- Codex 使用 PowerShell `Invoke-WebRequest` 和 `WebRequestSession` 请求真实地址：
  `http://localhost:8080`
- 使用 Session Cookie 分别测试两个临时用户。
- 使用唯一前缀的临时用户、课程和任务，并通过 MySQL 对照接口结果。
- 数据库操作使用项目指定的 MySQL 8.0 `mysql.exe`。
- HTTP 请求使用 PowerShell `Invoke-WebRequest` 和 `WebRequestSession`。
- 用户使用 Apifox 对关键接口进行了抽查。
- 本轮状态同步未重新执行上述测试。

## 测试结果

- Maven：`BUILD SUCCESS`。
- Java release：21。
- `GET /api/courses` 实现完成时编译 30 个 Java 源文件，无警告和编译错误。
- `GET /api/courses/{courseId}` 实现完成时编译 32 个 Java 源文件，无警告和编译错误。
- `PUT /api/courses/{courseId}` 和 `DELETE /api/courses/{courseId}` 实现完成时编译 34 个 Java 源文件，无警告和编译错误。
- Codex 真实本地接口测试全部通过：
  - 未登录访问返回 `401 UNAUTHORIZED`
  - 空课程列表返回 `courses: []`
  - 无任务课程返回 `todoCount: 0` 和 `nearestTasks: []`
  - 课程按照 `createdAt ASC` 返回
  - `todoCount` 只统计 TODO
  - 最近任务只返回截止日期最早的最多 3 条 TODO
  - DONE 任务被正确排除
  - 多门课程的任务统计相互独立
  - 不同用户数据相互隔离
  - `JSESSIONID` 正常保存和携带
  - 响应只包含 API 规定字段
  - 接口结果与数据库数据一致
- 用户使用 Apifox 抽查关键接口，结果正常。
- `GET /api/courses/{courseId}` 真实 HTTP 测试 `11/11` 通过：
  - 未登录访问返回 `401 UNAUTHORIZED`
  - Session 用户不存在时返回 `401 UNAUTHORIZED`
  - 正常查询自己的课程返回 `200 OK`
  - 没有任务时 `todoCount` 和 `doneCount` 均为 `0`
  - TODO 和 DONE 数量统计正确
  - 课程不存在时返回 `404 COURSE_NOT_FOUND`
  - 访问其他用户课程时返回相同的 `404 COURSE_NOT_FOUND`
  - 用户数据相互隔离
  - `JSESSIONID` 正常保存和携带
  - 响应只包含 `id`、`name`、`color`、`todoCount` 和 `doneCount`
  - 接口结果与数据库数据一致
- 临时用户、课程和任务已安全清理，数据库确认无残留；原有数据未删除。
- 临时测试辅助文件已清理。
- 测试结束后后端已停止，8080 端口已释放。
- PUT/DELETE 真实 HTTP 测试全部通过：
  - PUT 正常修改名称和颜色返回 `200 OK`
  - 名称前后空格会正确执行 `trim()`
  - 名称不变或只改变大小写时可以成功修改
  - 与当前用户另一门课程忽略大小写重名时返回 `409 COURSE_ALREADY_EXISTS`
  - 名称或颜色缺失、为空或格式非法时返回 `400`
  - 课程不存在或属于其他用户时统一返回 `404 COURSE_NOT_FOUND`
  - 未登录或 Session 用户不存在时返回 `401 UNAUTHORIZED`
  - DELETE 删除无任务和有任务课程均返回 `204 No Content`
  - 删除带任务课程后，关联任务通过 `ON DELETE CASCADE` 被删除
  - 其他用户的课程和任务不受影响
- PUT/DELETE 测试临时数据已全部清理，数据库残留数量为 `0/0/0`。

## 当前问题

- 未发现会阻塞当前已完成接口的编译、启动或运行问题。
- Session 属性名 `"userId"` 仍在多个 Controller 中以字符串字面量出现，后续可考虑提取为常量。
- 当前项目尚未提交可重复执行的自动化测试代码；本次 Codex 测试是临时执行的真实 HTTP 测试。
- `GET /api/courses` 当前需要 2 次基础查询，并为每门课程执行 2 次任务查询，总体为 `2 + 2N` 次数据库查询；MVP 阶段可以接受，后续可使用批量查询或聚合查询优化。
- `users` 和 `courses` 已有数据库唯一约束，但注册和创建课程仍采用“先查询、再保存”；极端并发冲突发生时，尚未将数据库唯一约束异常兜底转换为约定的 `409 Conflict`。
- 当前环境中的 Maven Wrapper 仍不可用，继续使用本机 Maven 绝对路径构建。
- 随机课程颜色可能连续选择到相同颜色，当前 MVP 可以接受。

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
12. 执行真实 HTTP 测试并按需使用 Apifox 抽查。
13. 检查数据库并更新日志和项目状态。

> 想业务 → 找数据 → 写 Service → 补边界 → 接 Controller → 做测试

## 下一步

下一个尚未实现的接口是：

```text
POST /api/tasks
```

下一次只分析该接口：

1. 阅读 API 文档中的创建任务契约。
2. 用中文梳理创建任务的完整业务流程。
3. 明确请求和响应 DTO 字段及校验规则。
4. 标出确认 Session 用户、查询课程归属和保存任务所需的数据库操作。
5. 推导当前功能真正需要新增或复用的 Repository 方法。
6. 明确课程不存在或不属于当前用户时的 `404 COURSE_NOT_FOUND`。
7. 方案确认后再实现代码、编译并按照接口测试规则执行测试。
