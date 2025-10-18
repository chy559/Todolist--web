# Anniversary 模块 API 测试文档

## 基础信息
- **Base URL**: `http://localhost:8080/api/anniversaries`
- **返回格式**: JSON
- **字符编码**: UTF-8

---

## 功能概述

纪念日功能允许用户：
1. **创建纪念日** - 设置重要的日期提醒
2. **倒计时显示** - 查看距离纪念日还有多少天
3. **循环纪念日** - 设置每年重复的纪念日（如生日）
4. **分类管理** - 按类型分类纪念日（生日、结婚、恋爱等）
5. **提醒功能** - 设置提前提醒天数
6. **查看即将到来** - 查看未来N天内的纪念日

---

## 纪念日类型说明

- **BIRTHDAY**: 生日
- **WEDDING**: 结婚纪念日
- **RELATIONSHIP**: 恋爱纪念日
- **WORK**: 工作纪念日（入职日等）
- **HOLIDAY**: 节日
- **OTHER**: 其他

---

## 1. 创建纪念日
**接口**: `POST /api/anniversaries`

**请求体**:
```json
{
  "title": "妈妈的生日",
  "description": "记得提前准备礼物",
  "targetDate": "2024-05-15",
  "isRecurring": true,
  "type": "BIRTHDAY",
  "remindDaysBefore": 7,
  "userId": 1
}
```

**字段说明**:
- `title`: 纪念日标题，必填，最大200字符
- `description`: 描述，可选
- `targetDate`: 目标日期，必填，格式：YYYY-MM-DD
- `isRecurring`: 是否每年循环，默认false
- `type`: 纪念日类型，默认OTHER
- `remindDaysBefore`: 提前提醒天数，可选
- `userId`: 用户ID，必填

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "妈妈的生日",
    "description": "记得提前准备礼物",
    "targetDate": "2024-05-15",
    "isRecurring": true,
    "type": "BIRTHDAY",
    "createTime": "2024-01-10T10:00:00",
    "updateTime": "2024-01-10T10:00:00",
    "remindDaysBefore": 7,
    "userId": 1,
    "username": "testuser",
    "daysUntil": 125,
    "isExpired": false,
    "isToday": false
  }
}
```

**倒计时字段说明**:
- `daysUntil`: 距离纪念日的天数（负数表示已过期）
- `isExpired`: 是否已过期（仅非循环纪念日）
- `isToday`: 是否是今天

---

## 2. 根据ID获取纪念日
**接口**: `GET /api/anniversaries/{id}`

**示例**: `GET /api/anniversaries/1`

---

## 3. 获取用户的所有纪念日
**接口**: `GET /api/anniversaries/user/{userId}`

**示例**: `GET /api/anniversaries/user/1`

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "title": "妈妈的生日",
      "targetDate": "2024-05-15",
      "isRecurring": true,
      "type": "BIRTHDAY",
      "daysUntil": 125,
      ...
    },
    {
      "id": 2,
      "title": "结婚纪念日",
      "targetDate": "2024-08-20",
      "isRecurring": true,
      "type": "WEDDING",
      "daysUntil": 222,
      ...
    }
  ]
}
```

---

## 4. 获取纪念日（按类型过滤）
**接口**: `GET /api/anniversaries/user/{userId}/type/{type}`

**类型选项**: `BIRTHDAY`, `WEDDING`, `RELATIONSHIP`, `WORK`, `HOLIDAY`, `OTHER`

**示例**: 
- 获取生日: `GET /api/anniversaries/user/1/type/BIRTHDAY`
- 获取结婚纪念日: `GET /api/anniversaries/user/1/type/WEDDING`

---

## 5. 获取纪念日（按是否循环过滤）
**接口**: `GET /api/anniversaries/user/{userId}/recurring/{isRecurring}`

**示例**:
- 获取循环纪念日: `GET /api/anniversaries/user/1/recurring/true`
- 获取非循环纪念日: `GET /api/anniversaries/user/1/recurring/false`

---

## 6. 排序查询

### 按目标日期升序（最近的在前）
**接口**: `GET /api/anniversaries/user/{userId}/sort/targetdate`

### 按创建时间倒序
**接口**: `GET /api/anniversaries/user/{userId}/sort/createtime`

---

## 7. 获取指定日期范围内的纪念日
**接口**: `GET /api/anniversaries/user/{userId}/range?start=2024-01-01&end=2024-12-31`

**参数**:
- `start`: 开始日期（YYYY-MM-DD）
- `end`: 结束日期（YYYY-MM-DD）

**示例**: `GET /api/anniversaries/user/1/range?start=2024-01-01&end=2024-12-31`

---

## 8. 获取即将到来的纪念日（重点功能）
**接口**: `GET /api/anniversaries/user/{userId}/upcoming?days=30`

**参数**:
- `days`: 查询未来多少天内的纪念日（默认30天）

**示例**: 
- 未来7天: `GET /api/anniversaries/user/1/upcoming?days=7`
- 未来30天: `GET /api/anniversaries/user/1/upcoming?days=30`

**用途**: 在首页显示即将到来的纪念日列表

---

## 9. 获取今天的纪念日
**接口**: `GET /api/anniversaries/user/{userId}/today`

**示例**: `GET /api/anniversaries/user/1/today`

**用途**: 显示"今天是XXX的生日"等提示

---

## 10. 获取本月的纪念日
**接口**: `GET /api/anniversaries/user/{userId}/thismonth`

**示例**: `GET /api/anniversaries/user/1/thismonth`

**用途**: 日历视图显示本月所有纪念日

---

## 11. 获取需要提醒的纪念日
**接口**: `GET /api/anniversaries/user/{userId}/remind`

**示例**: `GET /api/anniversaries/user/1/remind`

**说明**: 返回所有设置了提醒天数且在提醒范围内的纪念日

**例如**: 
- 纪念日A设置提前7天提醒，距离还有5天 → 会返回
- 纪念日B设置提前7天提醒，距离还有10天 → 不会返回
- 纪念日C没有设置提醒 → 不会返回

---

## 12. 获取纪念日统计信息
**接口**: `GET /api/anniversaries/user/{userId}/statistics`

**示例**: `GET /api/anniversaries/user/1/statistics`

**成功响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalAnniversaries": 15,
    "birthdayCount": 5,
    "weddingCount": 1,
    "relationshipCount": 1,
    "workCount": 2,
    "holidayCount": 4,
    "otherCount": 2
  }
}
```

---

## 13. 更新纪念日
**接口**: `PUT /api/anniversaries/{id}`

**请求体** (所有字段可选):
```json
{
  "title": "修改后的标题",
  "description": "修改后的描述",
  "targetDate": "2024-05-20",
  "isRecurring": false,
  "type": "OTHER",
  "remindDaysBefore": 3
}
```

**成功响应**: 返回更新后的纪念日

---

## 14. 删除纪念日
**接口**: `DELETE /api/anniversaries/{id}`

**示例**: `DELETE /api/anniversaries/1`

**成功响应**:
```json
{
  "code": 200,
  "message": "纪念日删除成功",
  "data": null
}
```

---

## 使用 cURL 测试示例

### 创建纪念日（生日）
```bash
curl -X POST http://localhost:8080/api/anniversaries \
  -H "Content-Type: application/json" \
  -d '{
    "title": "妈妈的生日",
    "description": "记得提前准备礼物",
    "targetDate": "2024-05-15",
    "isRecurring": true,
    "type": "BIRTHDAY",
    "remindDaysBefore": 7,
    "userId": 1
  }'
```

### 创建纪念日（结婚纪念日）
```bash
curl -X POST http://localhost:8080/api/anniversaries \
  -H "Content-Type: application/json" \
  -d '{
    "title": "结婚纪念日",
    "targetDate": "2020-08-20",
    "isRecurring": true,
    "type": "WEDDING",
    "remindDaysBefore": 14,
    "userId": 1
  }'
```

### 获取用户所有纪念日
```bash
curl -X GET http://localhost:8080/api/anniversaries/user/1
```

### 获取即将到来的纪念日（未来30天）
```bash
curl -X GET http://localhost:8080/api/anniversaries/user/1/upcoming?days=30
```

### 获取今天的纪念日
```bash
curl -X GET http://localhost:8080/api/anniversaries/user/1/today
```

### 获取需要提醒的纪念日
```bash
curl -X GET http://localhost:8080/api/anniversaries/user/1/remind
```

### 获取生日类型的纪念日
```bash
curl -X GET http://localhost:8080/api/anniversaries/user/1/type/BIRTHDAY
```

### 获取统计信息
```bash
curl -X GET http://localhost:8080/api/anniversaries/user/1/statistics
```

### 更新纪念日
```bash
curl -X PUT http://localhost:8080/api/anniversaries/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "修改后的标题",
    "remindDaysBefore": 10
  }'
```

### 删除纪念日
```bash
curl -X DELETE http://localhost:8080/api/anniversaries/1
```

---

## 前端倒计时实现建议

### 1. 显示纪念日列表（带倒计时）
```javascript
// 获取用户所有纪念日
const response = await fetch('http://localhost:8080/api/anniversaries/user/1/sort/targetdate');
const data = await response.json();

data.data.forEach(anniversary => {
  const daysUntil = anniversary.daysUntil;
  
  if (anniversary.isToday) {
    console.log(`今天是${anniversary.title}！🎉`);
  } else if (daysUntil > 0) {
    console.log(`${anniversary.title}还有${daysUntil}天`);
  } else if (!anniversary.isRecurring && anniversary.isExpired) {
    console.log(`${anniversary.title}已过期`);
  }
});
```

### 2. 首页显示即将到来的纪念日
```javascript
// 获取未来7天内的纪念日
const response = await fetch('http://localhost:8080/api/anniversaries/user/1/upcoming?days=7');
const data = await response.json();

// 显示在首页提醒区域
data.data.forEach(anniversary => {
  showReminder(`${anniversary.title}还有${anniversary.daysUntil}天`);
});
```

### 3. 每日检查今天的纪念日
```javascript
// 页面加载时检查今天是否有纪念日
const response = await fetch('http://localhost:8080/api/anniversaries/user/1/today');
const data = await response.json();

if (data.data.length > 0) {
  data.data.forEach(anniversary => {
    showNotification(`今天是${anniversary.title}！🎉`);
  });
}
```

### 4. 显示需要提醒的纪念日
```javascript
// 获取需要提醒的纪念日
const response = await fetch('http://localhost:8080/api/anniversaries/user/1/remind');
const data = await response.json();

// 在通知区域显示
data.data.forEach(anniversary => {
  const daysUntil = anniversary.daysUntil;
  if (daysUntil === 0) {
    showAlert(`今天是${anniversary.title}！`);
  } else {
    showAlert(`${anniversary.title}还有${daysUntil}天，记得准备！`);
  }
});
```

---

## 业务逻辑说明

### 1. 循环纪念日的倒计时计算
- **循环纪念日**: 如果今年的日期已过，自动计算到明年
- **示例**: 
  - 生日：2024-05-15
  - 今天：2024-06-01
  - 倒计时：计算到2025-05-15（明年）

### 2. 非循环纪念日的过期判断
- 非循环纪念日如果目标日期已过，会标记为 `isExpired: true`
- 循环纪念日永远不会过期

### 3. 提醒功能
- 设置 `remindDaysBefore` 后，在指定天数内会出现在提醒列表
- 提醒列表按剩余天数升序排列

### 4. 类型统计
- 可以统计各类型纪念日的数量
- 用于数据可视化展示

---

## 使用场景示例

### 场景1: 生日提醒
```bash
# 创建生日纪念日（每年循环）
curl -X POST http://localhost:8080/api/anniversaries \
  -H "Content-Type: application/json" \
  -d '{
    "title": "爸爸的生日",
    "targetDate": "1970-06-15",
    "isRecurring": true,
    "type": "BIRTHDAY",
    "remindDaysBefore": 7,
    "userId": 1
  }'

# 查看即将到来的生日
curl -X GET "http://localhost:8080/api/anniversaries/user/1/type/BIRTHDAY"
```

### 场景2: 结婚周年纪念
```bash
# 创建结婚纪念日
curl -X POST http://localhost:8080/api/anniversaries \
  -H "Content-Type: application/json" \
  -d '{
    "title": "结婚5周年",
    "targetDate": "2020-08-20",
    "isRecurring": true,
    "type": "WEDDING",
    "remindDaysBefore": 14,
    "userId": 1
  }'
```

### 场景3: 重要项目截止日期
```bash
# 创建非循环纪念日（一次性）
curl -X POST http://localhost:8080/api/anniversaries \
  -H "Content-Type: application/json" \
  -d '{
    "title": "项目交付截止日",
    "targetDate": "2024-12-31",
    "isRecurring": false,
    "type": "WORK",
    "remindDaysBefore": 30,
    "userId": 1
  }'
```

### 场景4: 查看本月所有纪念日
```bash
# 获取本月纪念日（日历视图）
curl -X GET http://localhost:8080/api/anniversaries/user/1/thismonth
```

---

## 常见错误码

- **200**: 成功
- **400**: 请求参数错误或业务逻辑错误
- **404**: 资源未找到（纪念日不存在或用户不存在）
- **500**: 服务器内部错误

---

## 数据验证规则

### 创建时
- `title`: 必填，最大200字符
- `targetDate`: 必填，日期格式YYYY-MM-DD
- `userId`: 必填，用户必须存在
- `description`: 可选
- `isRecurring`: 可选，默认false
- `type`: 可选，默认OTHER
- `remindDaysBefore`: 可选

### 更新时
- 所有字段可选
- `title`: 最大200字符
- `targetDate`: 日期格式YYYY-MM-DD

---

## 前端UI建议

### 纪念日列表页面
1. 按类型分组显示（生日、纪念日、节日等）
2. 每个纪念日显示倒计时天数
3. 用颜色标识紧急程度：
   - 绿色：30天以上
   - 黄色：7-30天
   - 红色：7天以内
   - 高亮：今天

### 首页提醒卡片
1. 显示今天的纪念日（如果有）
2. 显示未来7天内的纪念日
3. 点击可跳转到详情

### 日历视图
1. 在日历上标记纪念日
2. 不同类型用不同颜色图标
3. 点击日期显示当天的纪念日详情
