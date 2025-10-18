# TodoItem 模块 API 测试文档

## 基础信息
- **Base URL**: `http://localhost:8080/api/todoitems`
- **返回格式**: JSON
- **字符编码**: UTF-8

---

## 1. 创建待办事项
**接口**: `POST /api/todoitems`

**请求体**:
```json
{
  "title": "完成项目文档",
  "description": "编写API测试文档和用户手册",
  "startTime": "2024-01-10T09:00:00",
  "dueDate": "2024-01-15T18:00:00",
  "priority": "HIGH",
  "userId": 1
}
```

**优先级选项**: `LOW`, `MEDIUM`, `HIGH`

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "完成项目文档",
    "description": "编写API测试文档和用户手册",
    "completed": false,
    "createTime": "2024-01-10T08:30:00",
    "updateTime": "2024-01-10T08:30:00",
    "startTime": "2024-01-10T09:00:00",
    "dueDate": "2024-01-15T18:00:00",
    "completedTime": null,
    "priority": "HIGH",
    "userId": 1,
    "username": "testuser"
  }
}
```

---

## 2. 根据ID获取待办事项
**接口**: `GET /api/todoitems/{id}`

**示例**: `GET /api/todoitems/1`

**成功响应**: 同上

---

## 3. 获取用户的所有待办事项
**接口**: `GET /api/todoitems/user/{userId}`

**示例**: `GET /api/todoitems/user/1`

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "title": "完成项目文档",
      "completed": false,
      "priority": "HIGH",
      ...
    },
    {
      "id": 2,
      "title": "修复Bug",
      "completed": true,
      "priority": "MEDIUM",
      ...
    }
  ]
}
```

---

## 4. 获取待办事项（按完成状态过滤）
**接口**: `GET /api/todoitems/user/{userId}/completed/{completed}`

**示例**: 
- 未完成: `GET /api/todoitems/user/1/completed/false`
- 已完成: `GET /api/todoitems/user/1/completed/true`

---

## 5. 获取待办事项（按优先级过滤）
**接口**: `GET /api/todoitems/user/{userId}/priority/{priority}`

**示例**: `GET /api/todoitems/user/1/priority/HIGH`

**优先级选项**: `LOW`, `MEDIUM`, `HIGH`

---

## 6. 获取已过期未完成的待办事项
**接口**: `GET /api/todoitems/user/{userId}/overdue`

**示例**: `GET /api/todoitems/user/1/overdue`

---

## 7. 获取即将到期的待办事项
**接口**: `GET /api/todoitems/user/{userId}/upcoming?days=7`

**参数**:
- `days`: 查询未来多少天内到期（默认7天）

**示例**: `GET /api/todoitems/user/1/upcoming?days=3`

---

## 8. 排序查询

### 按创建时间倒序
**接口**: `GET /api/todoitems/user/{userId}/sort/createtime`

### 按截止日期升序
**接口**: `GET /api/todoitems/user/{userId}/sort/duedate`

### 按优先级降序
**接口**: `GET /api/todoitems/user/{userId}/sort/priority`

---

## 9. 更新待办事项
**接口**: `PUT /api/todoitems/{id}`

**请求体** (所有字段可选):
```json
{
  "title": "修改后的标题",
  "description": "修改后的描述",
  "completed": true,
  "startTime": "2024-01-11T09:00:00",
  "dueDate": "2024-01-16T18:00:00",
  "priority": "MEDIUM"
}
```

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "修改后的标题",
    "completed": true,
    "completedTime": "2024-01-10T10:00:00",
    ...
  }
}
```

---

## 10. 切换待办事项完成状态
**接口**: `PATCH /api/todoitems/{id}/toggle`

**说明**: 将未完成改为已完成，或将已完成改为未完成

**示例**: `PATCH /api/todoitems/1/toggle`

**成功响应**: 返回更新后的待办事项

---

## 11. 删除待办事项
**接口**: `DELETE /api/todoitems/{id}`

**示例**: `DELETE /api/todoitems/1`

**成功响应**:
```json
{
  "code": 200,
  "message": "待办事项删除成功",
  "data": null
}
```

---

## 12. 批量删除已完成的待办事项
**接口**: `DELETE /api/todoitems/user/{userId}/completed`

**示例**: `DELETE /api/todoitems/user/1/completed`

**成功响应**:
```json
{
  "code": 200,
  "message": "已完成的待办事项批量删除成功",
  "data": null
}
```

---

## 使用 cURL 测试示例

### 创建待办事项
```bash
curl -X POST http://localhost:8080/api/todoitems \
  -H "Content-Type: application/json" \
  -d '{
    "title": "完成项目文档",
    "description": "编写API测试文档",
    "startTime": "2024-01-10T09:00:00",
    "dueDate": "2024-01-15T18:00:00",
    "priority": "HIGH",
    "userId": 1
  }'
```

### 获取用户的所有待办事项
```bash
curl -X GET http://localhost:8080/api/todoitems/user/1
```

### 获取未完成的待办事项
```bash
curl -X GET http://localhost:8080/api/todoitems/user/1/completed/false
```

### 获取高优先级待办事项
```bash
curl -X GET http://localhost:8080/api/todoitems/user/1/priority/HIGH
```

### 获取已过期的待办事项
```bash
curl -X GET http://localhost:8080/api/todoitems/user/1/overdue
```

### 更新待办事项
```bash
curl -X PUT http://localhost:8080/api/todoitems/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "修改后的标题",
    "completed": true
  }'
```

### 切换完成状态
```bash
curl -X PATCH http://localhost:8080/api/todoitems/1/toggle
```

### 删除待办事项
```bash
curl -X DELETE http://localhost:8080/api/todoitems/1
```

### 批量删除已完成的待办事项
```bash
curl -X DELETE http://localhost:8080/api/todoitems/user/1/completed
```

---

## 数据验证规则

### 创建时
- `title`: 必填，最大200字符
- `userId`: 必填，用户必须存在
- `description`: 可选
- `startTime`: 可选，开始时间
- `dueDate`: 可选，截止日期
- `priority`: 可选，默认MEDIUM（LOW/MEDIUM/HIGH）

### 更新时
- 所有字段可选
- `title`: 最大200字符
- `completed`: true/false
- `priority`: LOW/MEDIUM/HIGH

---

## 常见错误码

- **200**: 成功
- **400**: 请求参数错误或业务逻辑错误
- **404**: 资源未找到（待办事项不存在或用户不存在）
- **500**: 服务器内部错误

---

## 业务逻辑说明

1. **完成时间自动记录**: 当待办事项被标记为完成时，系统会自动记录 `completedTime`
2. **取消完成状态**: 当已完成的待办事项被取消完成状态时，`completedTime` 会被清除
3. **用户关联**: 每个待办事项必须关联到一个存在的用户
4. **自动时间戳**: `createTime` 和 `updateTime` 由系统自动管理

---

## 使用场景示例

### 场景1: 查看今天的待办事项
1. 获取用户所有未完成的待办事项
2. 前端根据日期筛选今天的事项

### 场景2: 查看本周高优先级任务
```bash
# 获取高优先级且未完成的待办事项
curl -X GET http://localhost:8080/api/todoitems/user/1/priority/HIGH
```

### 场景3: 清理已完成的任务
```bash
# 批量删除已完成的待办事项
curl -X DELETE http://localhost:8080/api/todoitems/user/1/completed
```

### 场景4: 查看逾期任务
```bash
# 获取已过期未完成的待办事项
curl -X GET http://localhost:8080/api/todoitems/user/1/overdue
```
