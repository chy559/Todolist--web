# User 模块 API 测试文档

## 基础信息
- **Base URL**: `http://localhost:8080/api/users`
- **返回格式**: JSON
- **字符编码**: UTF-8

---

## 1. 用户注册
**接口**: `POST /api/users/register`

**请求体**:
```json
{
  "username": "testuser",
  "password": "123456",
  "email": "test@example.com"
}
```

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
  }
}
```

**失败响应**:
```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null
}
```

---

## 2. 用户登录
**接口**: `POST /api/users/login`

**请求体**:
```json
{
  "username": "testuser",
  "password": "123456"
}
```

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
  }
}
```

**失败响应**:
```json
{
  "code": 400,
  "message": "用户名或密码错误",
  "data": null
}
```

---

## 3. 根据ID获取用户信息
**接口**: `GET /api/users/{id}`

**示例**: `GET /api/users/1`

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
  }
}
```

**失败响应**:
```json
{
  "code": 404,
  "message": "用户未找到: id = 1",
  "data": null
}
```

---

## 4. 根据用户名获取用户信息
**接口**: `GET /api/users/username/{username}`

**示例**: `GET /api/users/username/testuser`

**成功响应**: 同上

---

## 5. 获取所有用户
**接口**: `GET /api/users`

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-01T10:00:00"
    },
    {
      "id": 2,
      "username": "testuser2",
      "email": "test2@example.com",
      "createTime": "2024-01-01T11:00:00",
      "updateTime": "2024-01-01T11:00:00"
    }
  ]
}
```

---

## 6. 更新用户信息
**接口**: `PUT /api/users/{id}`

**请求体** (字段可选):
```json
{
  "email": "newemail@example.com",
  "password": "newpassword123"
}
```

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "newemail@example.com",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T12:00:00"
  }
}
```

---

## 7. 删除用户
**接口**: `DELETE /api/users/{id}`

**示例**: `DELETE /api/users/1`

**成功响应**:
```json
{
  "code": 200,
  "message": "用户删除成功",
  "data": null
}
```

---

## 数据验证规则

### 注册时
- `username`: 必填，长度3-50字符
- `password`: 必填，长度至少6字符
- `email`: 必填，必须是有效的邮箱格式

### 登录时
- `username`: 必填
- `password`: 必填

### 更新时
- `email`: 可选，必须是有效的邮箱格式
- `password`: 可选，长度至少6字符

---

## 使用 cURL 测试示例

### 注册用户
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456","email":"test@example.com"}'
```

### 登录用户
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

### 获取用户信息
```bash
curl -X GET http://localhost:8080/api/users/1
```

### 更新用户信息
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"email":"newemail@example.com"}'
```

### 删除用户
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

---

## 使用 Postman 测试

1. 创建一个新的 Collection 命名为 "TodoList API"
2. 为每个接口创建 Request
3. 设置 Header: `Content-Type: application/json`
4. 在 Body 中选择 "raw" 和 "JSON" 格式
5. 按顺序测试：注册 → 登录 → 获取信息 → 更新 → 删除

---

## 常见错误码

- **200**: 成功
- **400**: 请求参数错误或业务逻辑错误
- **404**: 资源未找到
- **500**: 服务器内部错误

