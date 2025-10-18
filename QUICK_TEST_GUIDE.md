# 快速测试指南

## 常见错误及解决方案

### ❌ 错误示例：缺少请求体
```bash
curl -X POST http://localhost:8080/api/users/register
```
**错误信息**:
```json
{
  "code": 400,
  "message": "请求体缺失或格式错误，请检查：1) 是否添加了 Content-Type: application/json 头；2) 是否提供了有效的 JSON 请求体"
}
```

### ✅ 正确示例：完整的 POST 请求
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456","email":"test@example.com"}'
```

---

## 完整测试流程

### 1️⃣ 用户注册
```bash
#curl -X POST http://localhost:8080/api/users/register \
#  -H "Content-Type: application/json" \
#  -d "{\"username\":\"testuser\",\"password\":\"123456\",\"email\":\"test@example.com\"}"
curl -X POST http://localhost:8080/api/users/register -H "Content-Type: application/json" -d "{\"username\":\"testuser\",\"password\":\"123456\",\"email\":\"test@example.com\"}"
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
    "createTime": "2024-01-10T10:00:00",
    "updateTime": "2024-01-10T10:00:00"
  }
}
```

### 2️⃣ 用户登录
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"testuser\",\"password\":\"123456\"}"
```

### 3️⃣ 创建待办事项
```bash
curl -X POST http://localhost:8080/api/todoitems \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"完成项目文档\",\"description\":\"编写API文档\",\"dueDate\":\"2024-12-31T18:00:00\",\"priority\":\"HIGH\",\"userId\":1}"
```

### 4️⃣ 创建专注会话
```bash
curl -X POST http://localhost:8080/api/focus \
  -H "Content-Type: application/json" \
  -d "{\"duration\":25,\"notes\":\"番茄钟\",\"userId\":1}"
```

### 5️⃣ 创建纪念日
```bash
curl -X POST http://localhost:8080/api/anniversaries \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"妈妈的生日\",\"targetDate\":\"2024-05-15\",\"isRecurring\":true,\"type\":\"BIRTHDAY\",\"userId\":1}"
```

---

## Windows PowerShell 特别说明

如果你在 **Windows PowerShell** 中使用 cURL，需要注意以下事项：

### ⚠️ PowerShell 中的引号问题

PowerShell 处理引号的方式与 CMD 不同，有以下几种解决方案：

#### 方案1: 使用转义引号（推荐）
```powershell
curl -X POST http://localhost:8080/api/users/register `
  -H "Content-Type: application/json" `
  -d '{\"username\":\"testuser\",\"password\":\"123456\",\"email\":\"test@example.com\"}'
```

#### 方案2: 使用单引号和转义
```powershell
curl -X POST http://localhost:8080/api/users/register `
  -H 'Content-Type: application/json' `
  -d '{\"username\":\"testuser\",\"password\":\"123456\",\"email\":\"test@example.com\"}'
```

#### 方案3: 将 JSON 保存到文件
```powershell
# 创建文件 register.json
{
  "username": "testuser",
  "password": "123456",
  "email": "test@example.com"
}

# 使用文件
curl -X POST http://localhost:8080/api/users/register `
  -H "Content-Type: application/json" `
  -d "@register.json"
```

#### 方案4: 使用 Invoke-RestMethod（推荐）
```powershell
$body = @{
    username = "testuser"
    password = "123456"
    email = "test@example.com"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/users/register" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

---

## 使用 Postman 测试（推荐新手）

如果 cURL 命令太复杂，建议使用 **Postman**：

1. 下载 Postman: https://www.postman.com/downloads/
2. 创建新请求
3. 设置：
   - Method: POST
   - URL: `http://localhost:8080/api/users/register`
   - Headers: 添加 `Content-Type: application/json`
   - Body: 选择 `raw` 和 `JSON`，输入：
     ```json
     {
       "username": "testuser",
       "password": "123456",
       "email": "test@example.com"
     }
     ```
4. 点击 Send

---

## 常见错误代码

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 400 | 请求参数错误 | 检查请求体格式和必填字段 |
| 404 | 资源未找到 | 检查 URL 路径和资源 ID |
| 500 | 服务器错误 | 查看服务器日志，检查数据库连接 |

---

## 测试检查清单

✅ **启动前检查**:
- [ ] MySQL 数据库已启动
- [ ] 数据库 `todolist_db` 已创建
- [ ] Spring Boot 应用已启动（端口 8080）

✅ **请求检查**:
- [ ] 添加了 `-H "Content-Type: application/json"` 头
- [ ] 使用 `-d` 参数提供了 JSON 请求体
- [ ] JSON 格式正确（使用双引号，注意转义）
- [ ] URL 路径正确

---

## 快速验证命令

### 检查服务是否启动
```bash
curl http://localhost:8080/api/users
```

如果返回用户列表（即使是空列表），说明服务正常运行。

### 注册 → 登录 → 创建待办 完整流程
```bash
# 1. 注册
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"demo\",\"password\":\"123456\",\"email\":\"demo@test.com\"}"

# 2. 登录
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"demo\",\"password\":\"123456\"}"

# 3. 创建待办事项（假设用户 ID 为 1）
curl -X POST http://localhost:8080/api/todoitems \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"测试待办\",\"userId\":1}"

# 4. 查看待办事项
curl http://localhost:8080/api/todoitems/user/1
```

---

## 相关文档

- 用户模块: [API_TEST_USER.md](API_TEST_USER.md)
- 待办事项: [API_TEST_TODOITEM.md](API_TEST_TODOITEM.md)
- 专注时间: [API_TEST_FOCUSSESSION.md](API_TEST_FOCUSSESSION.md)
- 纪念日: [API_TEST_ANNIVERSARY.md](API_TEST_ANNIVERSARY.md)
