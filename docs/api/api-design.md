# API接口文档

- [x] **API 设计 v0.1**

## 公共规则

### 基本信息

- API 基础路径：`/api`
- 请求与响应格式：`application/json`
- 日期格式：`yyyy-MM-dd`
- 认证方式：Session + Cookie
- 字段命名：camelCase

### 公开接口

以下接口不要求用户提前登录：

```text
POST /api/auth/register
POST /api/auth/login
```

### 需要登录的接口

以下接口需要有效登录状态：

- 课程相关接口；
- 任务相关接口；
- 查询当前用户接口；
- 退出登录接口。

用户未登录或 Session 已失效时，返回：

- 状态码：`401 Unauthorized`

```json
{
  "code": "UNAUTHORIZED",
  "message": "请先登录"
}
```

### 用户数据隔离

课程和任务接口必须由后端判断数据是否属于当前登录用户。

前端不需要提交 `userId`，也不能通过修改请求中的编号访问其他用户的数据。

当课程或任务不存在，或者不属于当前用户时，统一返回：

- 状态码：`404 Not Found`

### 统一错误响应

接口发生业务错误时，响应体统一包含：

```json
{
  "code": "ERROR_CODE",
  "message": "给用户查看的错误提示"
}
```

字段说明：

- `code`：供前端程序判断错误类型；
- `message`：供页面展示给用户。

### 空列表

查询结果没有数据时，数组返回空数组 `[]`，不返回 `null`。

```json
{
  "tasks": []
}
```

### 密码安全

- 数据库只保存密码哈希；
- 不保存或记录明文密码；
- API 响应中不返回 `password`；
- API 响应中不返回 `passwordHash`。

## 认证接口

### 用户注册

- 方法：`POST`
- 路径：`/api/auth/register`
- 用途：创建用户账号并建立登录状态

#### 请求体

```json
{
  "username": "Emilia",
  "email": "Emilia@example.com",
  "password": "用户输入的密码",
  "confirmPassword": "再次输入的密码"
}
```

说明：

- 用户名、邮箱和密码均为必填项；
- `password` 与 `confirmPassword` 必须一致；
- 后端只保存密码哈希，不保存明文密码；
- 注册成功后，后端建立登录 Session；前端收到成功响应后进入系统首页。

#### 成功响应

- 状态码：`201 Created`

```json
{
  "id": 1,
  "username": "Emilia",
  "email": "Emilia@example.com"
}
```

#### 失败响应

- `400 Bad Request`：注册信息为空、格式错误或两次密码不一致
- `409 Conflict`：用户名或邮箱已被使用

```json
{
  "code": "PASSWORD_MISMATCH",
  "message": "两次输入的密码不一致"
}
```

```json
{
  "code": "ACCOUNT_ALREADY_EXISTS",
  "message": "用户名或邮箱已被使用"
}
```

---

### 用户登录

- 方法：`POST`
- 路径：`/api/auth/login`
- 用途：使用用户名或邮箱登录系统

#### 请求体

```json
{
  "account": "Emilia",
  "password": "用户输入的密码"
}
```

说明：

- `account` 可以填写用户名或邮箱；
- 登录成功后，后端建立 Session；
- 浏览器后续请求通过 Cookie 携带登录状态。

#### 成功响应

- 状态码：`200 OK`

```json
{
  "id": 1,
  "username": "Emilia",
  "email": "Emilia@example.com"
}
```

#### 失败响应

- `400 Bad Request`：账号或密码为空
- `401 Unauthorized`：账号或密码错误

```json
{
  "code": "INVALID_CREDENTIALS",
  "message": "账号或密码错误"
}
```

登录失败时不分别提示“账号不存在”或“密码错误”，统一返回“账号或密码错误”。

---

### 用户退出

- 方法：`POST`
- 路径：`/api/auth/logout`
- 请求体：无
- 用途：退出当前登录状态

#### 成功响应

- `204 No Content`：退出成功，无响应体

说明：

- 后端清除当前认证信息；
- 当前 Session 失效；
- 前端收到退出成功响应后返回登录页面。

#### 失败响应

- `401 Unauthorized`：当前用户未登录或登录状态已失效

---

### 查询当前用户

- 方法：`GET`
- 路径：`/api/auth/me`
- 请求体：无
- 用途：页面刷新后确认登录状态，并获取当前用户信息

#### 成功响应

- 状态码：`200 OK`

```json
{
  "id": 1,
  "username": "Emilia",
  "email": "Emilia@example.com"
}
```

#### 失败响应

- `401 Unauthorized`：当前用户未登录或登录状态已失效

```json
{
  "code": "UNAUTHORIZED",
  "message": "请先登录"
}
```

## 课程接口

### 创建课程

- 请求方法：`POST`
- 请求路径：`/api/courses`

#### 请求体

```json
{
  "name": "JavaWeb",
}
```
#### 成功响应

- 状态码 `201 Created`：课程创建成功

```json
{
  "id": 1,
  "name": "JavaWeb",
  "color": "#4F46E5"
}
```

说明：

`color` 为可选字段；未提交时，由后端自动分配颜色。

#### 失败响应

- `400 Bad Request`：课程名称为空或格式不正确
- `409 Conflict`：课程名称已经存在

### 查询课程列表

- 方法：`GET`
- 路径：`/api/courses`
- 用途：查询当前用户的课程首页摘要
- 请求体：无

#### 成功响应

- 状态码：`200 OK`

```json
{
  "courses": [
    {
      "id": 1,
      "name": "JavaWeb",
      "color": "#4F46E5",
      "todoCount": 5,
      "nearestTasks": [
        {
          "id": 11,
          "title": "完成登录接口",
          "deadline": "2026-07-26"
        }
      ]
    }
  ]
}
```

说明：

- `todoCount`：该课程的未完成任务数量；
- `nearestTasks`：最近截止的最多 3 个 `TODO` 任务；
- 没有未完成任务时，`nearestTasks` 返回 `[]`

### 查询单门课程

- 方法：`GET`
- 路径：`/api/courses/{courseId}`
- 用途：查询课程详情页的课程基本信息
- 请求体：无

#### 成功响应

- 状态码：`200 OK`

```json
{
  "id": 12,
  "name": "JavaWeb",
  "color": "#4F46E5",
  "todoCount": 5,
  "doneCount": 8
}
```

#### 失败响应

- `404 Not Found`：课程不存在或当前用户无权访问

```json
{
  "code": "COURSE_NOT_FOUND",
  "message": "课程不存在"
}
```

### 修改课程

- 方法：`PUT`
- 路径：`/api/courses/{courseId}`

#### 请求体

```json
{
  "name": "Java Web 项目实践",
  "color": "#8B5CF6"
}
```

#### 成功响应

- 状态码：`200 OK`

```json
{
  "id": 12,
  "name": "Java Web 项目实践",
  "color": "#8B5CF6"
}
```

#### 失败响应

- `400 Bad Request`：课程名称或颜色格式不正确
- `404 Not Found`：课程不存在或当前用户无权访问
- `409 Conflict`：当前用户已存在同名课程

### 删除课程

- 方法：`DELETE`
- 路径：`/api/courses/{courseId}`
- 请求体：无

#### 成功响应

- `204 No Content`：删除成功，无响应体

#### 失败响应

- `404 Not Found`：课程不存在或当前用户无权访问

说明：

- 删除课程时，该课程下的全部任务也会被永久删除。

## 任务接口

### 创建任务

- 方法：`POST`
- 路径：`/api/tasks`

#### 请求体

```json
{
  "title": "完成登录接口",
  "courseId": 1,
  "deadline": "2026-07-30"
}
```

说明：

- `status` 不需要提交，后端默认设置为 `TODO`；
- 后端需要确认课程存在，并且属于当前登录用户。

#### 成功响应

- 状态码：`201 Created`

```json
{
  "id": 15,
  "title": "完成登录接口",
  "courseId": 1,
  "deadline": "2026-07-30",
  "status": "TODO"
}
```

#### 失败响应

- `400 Bad Request`：任务标题、课程编号或截止日期格式不正确
- `404 Not Found`：课程不存在或当前用户无权访问

### 查询课程任务

- 方法：`GET`
- 路径：`/api/courses/{courseId}/tasks`
- 用途：查询指定课程下某种状态的任务
- 请求体：无

#### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | String | 是 | 任务状态，只能为 `TODO` 或 `DONE` |

请求示例：

```http
GET /api/courses/12/tasks?status=TODO
```

#### 成功响应

- 状态码：`200 OK`

```json
{
  "tasks": [
    {
      "id": 15,
      "title": "完成登录接口",
      "courseId": 12,
      "deadline": "2026-07-30",
      "status": "TODO"
    }
  ]
}
```

说明：

- 任务按照截止日期从早到晚排序；
- 没有符合条件的任务时，`tasks` 返回空数组 `[]`。

#### 失败响应

- `400 Bad Request`：`status` 缺失或不是 `TODO`、`DONE`
- `404 Not Found`：课程不存在或当前用户无权访问

### 查询单个任务

- 方法：`GET`
- 路径：`/api/tasks/{taskId}`
- 用途：获取任务完整信息，用于打开任务编辑窗口
- 请求体：无

#### 成功响应

- 状态码：`200 OK`

```json
{
  "id": 15,
  "title": "完成登录接口",
  "courseId": 12,
  "deadline": "2026-07-30",
  "status": "TODO"
}
```

#### 失败响应

- `404 Not Found`：任务不存在或当前用户无权访问

### 修改任务

- 方法：`PUT`
- 路径：`/api/tasks/{taskId}`

#### 请求体

```json
{
  "title": "完成任务接口设计",
  "courseId": 12,
  "deadline": "2026-07-30",
  "status": "TODO"
}
```

#### 成功响应

- 状态码：`200 OK`

```json
{
  "id": 15,
  "title": "完成任务接口设计",
  "courseId": 12,
  "deadline": "2026-07-30",
  "status": "TODO"
}
```

#### 失败响应

- `400 Bad Request`：任务数据或状态格式不正确
- `404 Not Found`：任务、课程不存在，或当前用户无权访问

### 修改任务状态

- 方法：`PATCH`
- 路径：`/api/tasks/{taskId}/status`
- 用途：在课程详情页快速修改任务状态

#### 请求体

```json
{
  "status": "DONE"
}
```

说明：`status` 只能为 `TODO` 或 `DONE`。

#### 成功响应

- 状态码：`200 OK`

```json
{
  "id": 15,
  "status": "DONE"
}
```

#### 失败响应

- `400 Bad Request`：任务状态不是 `TODO` 或 `DONE`
- `404 Not Found`：任务不存在或当前用户无权访问

### 删除任务

- 方法：`DELETE`
- 路径：`/api/tasks/{taskId}`
- 请求体：无

#### 成功响应

- `204 No Content`：任务删除成功，无响应体

#### 失败响应

- `404 Not Found`：任务不存在或当前用户无权访问