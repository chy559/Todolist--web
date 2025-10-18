# FocusSession 模块 API 测试文档

## 基础信息
- **Base URL**: `http://localhost:8080/api/focus`
- **返回格式**: JSON
- **字符编码**: UTF-8

---

## 功能概述

专注时间功能允许用户：
1. **创建专注会话** - 设置专注时长（分钟）
2. **开始专注** - 启动倒计时
3. **完成专注** - 正常完成一次专注
4. **取消专注** - 中途取消专注
5. **查看历史记录** - 查看所有专注会话历史
6. **统计数据** - 查看总专注时长、完成次数等

---

## 会话状态说明

- **PENDING**: 待开始 - 会话已创建但未开始
- **IN_PROGRESS**: 进行中 - 会话正在进行，倒计时中
- **COMPLETED**: 已完成 - 会话正常完成
- **CANCELLED**: 已取消 - 会话被取消

---

## 1. 创建专注会话
**接口**: `POST /api/focus`

**请求体**:
```json
{
  "duration": 25,
  "notes": "完成项目文档编写",
  "userId": 1
}
```

**字段说明**:
- `duration`: 专注时长（分钟），必填，最小值1
- `notes`: 备注信息，可选
- `userId`: 用户ID，必填

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "duration": 25,
    "startTime": null,
    "endTime": null,
    "actualDuration": null,
    "status": "PENDING",
    "createTime": "2024-01-10T09:00:00",
    "updateTime": "2024-01-10T09:00:00",
    "notes": "完成项目文档编写",
    "userId": 1,
    "username": "testuser",
    "remainingMinutes": null
  }
}
```

---

## 2. 开始专注会话（启动倒计时）
**接口**: `POST /api/focus/{id}/start`

**示例**: `POST /api/focus/1/start`

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "duration": 25,
    "startTime": "2024-01-10T09:05:00",
    "endTime": "2024-01-10T09:30:00",
    "actualDuration": null,
    "status": "IN_PROGRESS",
    "createTime": "2024-01-10T09:00:00",
    "updateTime": "2024-01-10T09:05:00",
    "notes": "完成项目文档编写",
    "userId": 1,
    "username": "testuser",
    "remainingMinutes": 25
  }
}
```

**倒计时说明**:
- `startTime`: 开始时间
- `endTime`: 预计结束时间（开始时间 + 专注时长）
- `remainingMinutes`: 剩余分钟数，用于前端显示倒计时

---

## 3. 完成专注会话
**接口**: `POST /api/focus/{id}/complete`

**示例**: `POST /api/focus/1/complete`

**说明**: 手动标记专注会话为已完成，系统会计算实际专注时长

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "duration": 25,
    "startTime": "2024-01-10T09:05:00",
    "endTime": "2024-01-10T09:30:00",
    "actualDuration": 25,
    "status": "COMPLETED",
    "createTime": "2024-01-10T09:00:00",
    "updateTime": "2024-01-10T09:30:00",
    "notes": "完成项目文档编写",
    "userId": 1,
    "username": "testuser",
    "remainingMinutes": null
  }
}
```

---

## 4. 取消专注会话
**接口**: `POST /api/focus/{id}/cancel`

**示例**: `POST /api/focus/1/cancel`

**说明**: 取消专注会话，如果会话已开始，会记录实际专注时长

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "duration": 25,
    "startTime": "2024-01-10T09:05:00",
    "endTime": "2024-01-10T09:30:00",
    "actualDuration": 15,
    "status": "CANCELLED",
    "createTime": "2024-01-10T09:00:00",
    "updateTime": "2024-01-10T09:20:00",
    "notes": "完成项目文档编写",
    "userId": 1,
    "username": "testuser",
    "remainingMinutes": null
  }
}
```

---

## 5. 根据ID获取专注会话
**接口**: `GET /api/focus/{id}`

**示例**: `GET /api/focus/1`

---

## 6. 获取用户的所有专注会话
**接口**: `GET /api/focus/user/{userId}`

**示例**: `GET /api/focus/user/1`

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "duration": 25,
      "status": "COMPLETED",
      "actualDuration": 25,
      ...
    },
    {
      "id": 2,
      "duration": 45,
      "status": "PENDING",
      "actualDuration": null,
      ...
    }
  ]
}
```

---

## 7. 获取专注会话（按状态过滤）
**接口**: `GET /api/focus/user/{userId}/status/{status}`

**状态选项**: `PENDING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`

**示例**: 
- 获取已完成: `GET /api/focus/user/1/status/COMPLETED`
- 获取进行中: `GET /api/focus/user/1/status/IN_PROGRESS`

---

## 8. 获取正在进行的专注会话（倒计时）
**接口**: `GET /api/focus/user/{userId}/inprogress`

**示例**: `GET /api/focus/user/1/inprogress`

**用途**: 前端用于获取当前正在进行的专注会话，显示倒计时

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 3,
    "duration": 25,
    "startTime": "2024-01-10T10:00:00",
    "endTime": "2024-01-10T10:25:00",
    "status": "IN_PROGRESS",
    "remainingMinutes": 15
  }
}
```

**失败响应**（无进行中的会话）:
```json
{
  "code": 404,
  "message": "专注会话未找到: status = IN_PROGRESS",
  "data": null
}
```

---

## 9. 排序查询

### 按创建时间倒序
**接口**: `GET /api/focus/user/{userId}/sort/createtime`

### 按开始时间倒序
**接口**: `GET /api/focus/user/{userId}/sort/starttime`

---

## 10. 获取指定时间范围内的专注会话
**接口**: `GET /api/focus/user/{userId}/range?start=2024-01-01T00:00:00&end=2024-01-31T23:59:59`

**参数**:
- `start`: 开始时间（ISO 8601格式）
- `end`: 结束时间（ISO 8601格式）

**示例**: `GET /api/focus/user/1/range?start=2024-01-01T00:00:00&end=2024-01-31T23:59:59`

---

## 11. 获取今天的专注会话
**接口**: `GET /api/focus/user/{userId}/today`

**示例**: `GET /api/focus/user/1/today`

**用途**: 查看今天的专注记录

---

## 12. 获取专注统计信息
**接口**: `GET /api/focus/user/{userId}/statistics`

**示例**: `GET /api/focus/user/1/statistics`

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalSessions": 50,
    "completedSessions": 42,
    "totalFocusMinutes": 1050
  }
}
```

**字段说明**:
- `totalSessions`: 总会话数
- `completedSessions`: 已完成的会话数
- `totalFocusMinutes`: 总专注时长（分钟）

---

## 13. 更新专注会话
**接口**: `PUT /api/focus/{id}`

**说明**: 只有状态为PENDING的会话可以更新

**请求体** (字段可选):
```json
{
  "duration": 30,
  "notes": "修改后的备注"
}
```

**成功响应**: 返回更新后的专注会话

---

## 14. 删除专注会话
**接口**: `DELETE /api/focus/{id}`

**说明**: 不能删除正在进行的会话

**示例**: `DELETE /api/focus/1`

**成功响应**:
```json
{
  "code": 200,
  "message": "专注会话删除成功",
  "data": null
}
```

---

## 使用 cURL 测试示例

### 创建专注会话（25分钟）
```bash
curl -X POST http://localhost:8080/api/focus \
  -H "Content-Type: application/json" \
  -d '{
    "duration": 25,
    "notes": "番茄工作法 - 第一个番茄钟",
    "userId": 1
  }'
```

### 开始专注会话
```bash
curl -X POST http://localhost:8080/api/focus/1/start
```

### 完成专注会话
```bash
curl -X POST http://localhost:8080/api/focus/1/complete
```

### 取消专注会话
```bash
curl -X POST http://localhost:8080/api/focus/1/cancel
```

### 获取正在进行的会话（用于倒计时）
```bash
curl -X GET http://localhost:8080/api/focus/user/1/inprogress
```

### 获取今天的专注记录
```bash
curl -X GET http://localhost:8080/api/focus/user/1/today
```

### 获取专注统计信息
```bash
curl -X GET http://localhost:8080/api/focus/user/1/statistics
```

### 获取已完成的专注会话
```bash
curl -X GET http://localhost:8080/api/focus/user/1/status/COMPLETED
```

---

## 前端倒计时实现建议

### 1. 创建并开始专注会话
```javascript
// 1. 创建专注会话
const createResponse = await fetch('http://localhost:8080/api/focus', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    duration: 25,
    notes: '专注工作',
    userId: 1
  })
});
const session = await createResponse.json();

// 2. 开始专注会话
await fetch(`http://localhost:8080/api/focus/${session.data.id}/start`, {
  method: 'POST'
});
```

### 2. 获取倒计时信息
```javascript
// 定期轮询获取剩余时间
setInterval(async () => {
  const response = await fetch('http://localhost:8080/api/focus/user/1/inprogress');
  const data = await response.json();
  
  if (data.code === 200) {
    const remainingMinutes = data.data.remainingMinutes;
    // 更新UI显示剩余时间
    updateCountdown(remainingMinutes);
  } else {
    // 没有进行中的会话
    stopCountdown();
  }
}, 1000); // 每秒更新一次
```

### 3. 完成或取消专注
```javascript
// 完成
await fetch(`http://localhost:8080/api/focus/${sessionId}/complete`, {
  method: 'POST'
});

// 取消
await fetch(`http://localhost:8080/api/focus/${sessionId}/cancel`, {
  method: 'POST'
});
```

---

## 业务逻辑说明

1. **一次只能有一个进行中的会话**
   - 用户同一时间只能有一个状态为IN_PROGRESS的专注会话
   - 创建新会话或开始其他会话前，系统会检查是否有进行中的会话

2. **实际时长自动记录**
   - 完成或取消会话时，系统会自动计算实际专注时长
   - `actualDuration` = 当前时间 - 开始时间（分钟）

3. **状态转换规则**
   - PENDING → IN_PROGRESS：调用start接口
   - IN_PROGRESS → COMPLETED：调用complete接口
   - IN_PROGRESS → CANCELLED：调用cancel接口
   - PENDING → CANCELLED：调用cancel接口

4. **倒计时计算**
   - 后端返回 `remainingMinutes` 字段
   - 前端可以基于此实现倒计时显示
   - 建议每秒或每分钟轮询一次接口更新倒计时

---

## 使用场景示例

### 场景1: 番茄工作法
```bash
# 1. 创建25分钟专注会话
curl -X POST http://localhost:8080/api/focus \
  -H "Content-Type: application/json" \
  -d '{"duration": 25, "userId": 1, "notes": "番茄钟#1"}'

# 2. 开始专注
curl -X POST http://localhost:8080/api/focus/1/start

# 3. 25分钟后完成
curl -X POST http://localhost:8080/api/focus/1/complete
```

### 场景2: 查看今日专注数据
```bash
# 获取今天的专注记录
curl -X GET http://localhost:8080/api/focus/user/1/today

# 获取统计信息
curl -X GET http://localhost:8080/api/focus/user/1/statistics
```

### 场景3: 中途打断处理
```bash
# 如果需要中途停止，取消当前会话
curl -X POST http://localhost:8080/api/focus/1/cancel
```

---

## 常见错误码

- **200**: 成功
- **400**: 请求参数错误或业务逻辑错误
  - "您有正在进行的专注会话，无法创建新会话"
  - "只有待开始的专注会话可以启动"
  - "只有进行中的专注会话可以完成"
  - "无法删除进行中的专注会话，请先完成或取消"
- **404**: 资源未找到（会话不存在或用户不存在）
- **500**: 服务器内部错误

---

## 数据验证规则

### 创建时
- `duration`: 必填，最小值1分钟
- `userId`: 必填，用户必须存在
- `notes`: 可选，最大500字符

### 更新时
- `duration`: 可选，最小值1分钟
- `notes`: 可选，最大500字符
- 只能更新状态为PENDING的会话
