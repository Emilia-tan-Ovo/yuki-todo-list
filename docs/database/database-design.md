# YuKi's TODO List 数据库设计

## 1. 文档说明

本文档记录 YuKi's TODO List 首版 MVP 的数据库设计。

当前版本只围绕三个核心业务对象展开：

- 用户；
- 课程；
- 任务。

首版不提前加入子任务、提醒、课程表、AI 拆解等扩展数据结构。

---

## 2. 数据库设计目标

数据库需要支持以下核心功能：

1. 用户注册和登录；
2. 每个用户管理自己的课程；
3. 每门课程管理自己的任务；
4. 首页展示课程及最近截止的 TODO 任务；
5. 课程详情页分别展示 TODO 和 DONE 任务；
6. 用户可以创建、修改、删除课程和任务；
7. 不同用户之间的数据相互隔离。

---

## 3. 核心表

首版共设计三张表：

```text
users
  1
  │ user_id
  │ 一个用户拥有多门课程
  N
courses
  1
  │ course_id
  │ 一门课程拥有多个任务
  N
tasks
```

三张表的职责如下：

| 表名 | 作用 |
|---|---|
| `users` | 保存用户账号信息 |
| `courses` | 保存用户创建的课程 |
| `tasks` | 保存课程下的学习任务 |

---

## 4. 表关系

### 4.1 用户与课程

一个用户可以拥有多门课程。

一门课程只能属于一个用户。

```text
User 1 —— N Course
```

通过以下字段建立关系：

```text
courses.user_id → users.id
```

---

### 4.2 课程与任务

一门课程可以拥有多个任务。

一个任务只能属于一门课程。

```text
Course 1 —— N Task
```

通过以下字段建立关系：

```text
tasks.course_id → courses.id
```

---

### 4.3 为什么 tasks 不保存 user_id

任务已经可以通过课程找到所属用户：

```text
task → course → user
```

因此首版不在 `tasks` 表重复保存 `user_id`，避免出现重复数据和数据不一致。

---

## 5. users 用户表

### 5.1 字段设计

| 字段名 | 类型 | 约束 | 说明 |
|---|---|---|---|
| `id` | `BIGINT` | 主键、自增 | 用户唯一编号 |
| `username` | `VARCHAR(50)` | 非空、唯一 | 用户名 |
| `email` | `VARCHAR(100)` | 非空、唯一 | 用户邮箱 |
| `password_hash` | `VARCHAR(255)` | 非空 | 密码哈希值 |
| `created_at` | `DATETIME` | 自动生成 | 注册时间 |
| `updated_at` | `DATETIME` | 自动生成、自动更新 | 最后更新时间 |

### 5.2 设计说明

注册页面中的“确认密码”只用于验证两次输入是否一致，不保存到数据库。

数据库不保存明文密码，只保存经过哈希处理后的密码，因此字段名使用：

```text
password_hash
```

---

## 6. courses 课程表

### 6.1 字段设计

| 字段名 | 类型 | 约束 | 说明 |
|---|---|---|---|
| `id` | `BIGINT` | 主键、自增 | 课程唯一编号 |
| `user_id` | `BIGINT` | 非空、外键 | 所属用户 |
| `name` | `VARCHAR(100)` | 非空 | 课程名称 |
| `color` | `VARCHAR(7)` | 非空 | 课程颜色 |
| `created_at` | `DATETIME` | 自动生成 | 创建时间 |
| `updated_at` | `DATETIME` | 自动生成、自动更新 | 最后更新时间 |

### 6.2 课程颜色

课程颜色使用十六进制颜色字符串保存，例如：

```text
#4F46E5
```

因此字段长度设置为：

```text
VARCHAR(7)
```

新建课程时，系统自动分配一个默认颜色，用户可以后续修改。

### 6.3 课程名称唯一约束

同一个用户不能创建两门同名课程，但不同用户可以创建同名课程。

使用联合唯一约束：

```sql
UNIQUE (user_id, name)
```

示例：

```text
用户 A：可以创建 JavaWeb
用户 B：也可以创建 JavaWeb
用户 A：不能再次创建第二门 JavaWeb
```

---

## 7. tasks 任务表

### 7.1 字段设计

| 字段名 | 类型 | 约束 | 说明 |
|---|---|---|---|
| `id` | `BIGINT` | 主键、自增 | 任务唯一编号 |
| `course_id` | `BIGINT` | 非空、外键 | 所属课程 |
| `title` | `VARCHAR(200)` | 非空 | 任务名称 |
| `deadline` | `DATE` | 非空 | 截止日期 |
| `status` | `VARCHAR(20)` | 非空、默认 `TODO` | 任务状态 |
| `created_at` | `DATETIME` | 自动生成 | 创建时间 |
| `updated_at` | `DATETIME` | 自动生成、自动更新 | 最后更新时间 |

### 7.2 任务状态

首版只包含两种任务状态：

| 状态值 | 含义 |
|---|---|
| `TODO` | 未完成 |
| `DONE` | 已完成 |

数据库、Java 枚举、接口 JSON 和前端标签统一使用：

```text
TODO
DONE
```

### 7.3 截止日期

首版只要求用户选择截止日期，不要求填写具体时刻，因此使用：

```text
DATE
```

示例：

```text
2026-07-30
```

---

## 8. 页面数据如何得到

### 8.1 首页未完成任务数量

课程卡片中的未完成任务数量不直接保存在 `courses` 表中，而是根据 `tasks` 表实时计算。

查询某门课程的 TODO 任务数量：

```sql
SELECT COUNT(*) AS todo_count
FROM tasks
WHERE course_id = ?
  AND status = 'TODO';
```

---

### 8.2 首页最近截止的三项任务

```sql
SELECT *
FROM tasks
WHERE course_id = ?
  AND status = 'TODO'
ORDER BY deadline ASC
LIMIT 3;
```

含义：

1. 查询当前课程；
2. 只查询 TODO 任务；
3. 按截止日期从近到远排列；
4. 只取前三条。

---

### 8.3 TODO 和 DONE 页面

TODO 和 DONE 任务不需要分别创建两张表。

所有任务都保存在 `tasks` 表中，通过 `status` 区分。

查询 TODO：

```sql
WHERE status = 'TODO'
```

查询 DONE：

```sql
WHERE status = 'DONE'
```

首版所说的“归档”，实际含义是修改任务状态，并在不同页面中查询展示。

---

## 9. 删除规则

### 9.1 删除课程

首版暂定：

> 删除课程时，同时删除该课程下的全部任务。

前端必须提供明确的二次确认，例如：

```text
确定删除“JavaWeb”吗？

该课程及其全部任务都会被永久删除，
此操作无法撤销。
```

数据库通过以下规则实现：

```sql
ON DELETE CASCADE
```

### 9.2 删除用户

删除用户时：

```text
删除用户
→ 删除用户的课程
→ 删除课程下的任务
```

同样通过外键级联删除实现。

---

## 10. 索引设计

首页经常需要执行以下查询：

```text
查询某门课程
状态为 TODO 的任务
按截止日期排序
只取前三条
```

因此为 `tasks` 表建立联合索引：

```sql
INDEX idx_tasks_course_status_deadline
    (course_id, status, deadline)
```

可以先把索引理解为数据库的“查找目录”，用于提高常用查询的效率。

---

## 11. 建表 SQL

[建表sql](./sql/init.sql)

---

## 12. 当前结论

数据库设计 v0.1 使用三张核心表：

```text
users
courses
tasks
```

当前设计已经能够支持首版 MVP 的核心功能。

首版暂不创建以下数据表：

- 子任务表；
- 提醒表；
- 课程表导入记录；
- AI 分析记录；
- 文件表；
- 多人协作关系表。

这些功能在后续版本真正需要时再扩展。

