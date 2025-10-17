# TodoList应用 - Entity层设计文档

## 概述
本文档描述了TodoList应用的数据实体层设计，包含4个核心实体类。

## 实体类清单

### 1. User（用户实体）
**文件位置**: `src/main/java/com/example/todolist/entity/User.java`

**功能**: 用户账户管理，支持登录系统

**主要字段**:
- `id`: 用户唯一标识（主键）
- `username`: 用户名（唯一，3-50字符）
- `password`: 密码（至少6字符，需要加密存储）
- `email`: 邮箱（唯一）
- `createTime`: 创建时间
- `updateTime`: 更新时间

**关联关系**:
- 一对多关系：一个用户可以有多个待办事项（TodoItem）
- 一对多关系：一个用户可以有多个专注会话（FocusSession）
- 一对多关系：一个用户可以有多个纪念日（Anniversary）

**验证规则**:
- 用户名不能为空，长度3-50字符
- 密码不能为空，至少6字符
- 邮箱格式验证

---

### 2. TodoItem（待办事项实体）
**文件位置**: `src/main/java/com/example/todolist/entity/TodoItem.java`

**功能**: 待办事项清单管理，支持增删改查

**主要字段**:
- `id`: 待办事项唯一标识（主键）
- `title`: 事项标题（必填，最多200字符）
- `description`: 详细描述
- `completed`: 完成状态（默认false）
- `priority`: 优先级（LOW/MEDIUM/HIGH）
- `dueDate`: 截止日期
- `completedTime`: 完成时间
- `createTime`: 创建时间
- `updateTime`: 更新时间
- `user`: 所属用户（外键）

**优先级枚举**:
- `LOW`: 低优先级
- `MEDIUM`: 中优先级（默认）
- `HIGH`: 高优先级

**特性**:
- 自动记录完成时间（当任务被标记为完成时）
- 取消完成状态时自动清除完成时间

---

### 3. FocusSession（专注时间会话实体）
**文件位置**: `src/main/java/com/example/todolist/entity/FocusSession.java`

**功能**: 专注时间管理，支持倒计时功能

**主要字段**:
- `id`: 会话唯一标识（主键）
- `duration`: 计划专注时长（分钟，至少1分钟）
- `startTime`: 开始时间
- `endTime`: 结束时间
- `actualDuration`: 实际专注时长（分钟）
- `status`: 会话状态（PENDING/IN_PROGRESS/COMPLETED/CANCELLED）
- `notes`: 备注信息
- `createTime`: 创建时间
- `updateTime`: 更新时间
- `user`: 所属用户（外键）

**会话状态枚举**:
- `PENDING`: 待开始
- `IN_PROGRESS`: 进行中
- `COMPLETED`: 已完成
- `CANCELLED`: 已取消

**核心方法**:
- `start()`: 开始专注会话
- `complete()`: 完成专注会话
- `cancel()`: 取消专注会话

---

### 4. Anniversary（纪念日实体）
**文件位置**: `src/main/java/com/example/todolist/entity/Anniversary.java`

**功能**: 纪念日管理，支持倒计时和循环纪念日

**主要字段**:
- `id`: 纪念日唯一标识（主键）
- `title`: 纪念日标题（必填，最多200字符）
- `description`: 详细描述
- `targetDate`: 目标日期（必填）
- `isRecurring`: 是否每年循环（默认false）
- `type`: 纪念日类型
- `remindDaysBefore`: 提前提醒天数
- `createTime`: 创建时间
- `updateTime`: 更新时间
- `user`: 所属用户（外键）

**纪念日类型枚举**:
- `BIRTHDAY`: 生日
- `WEDDING`: 结婚纪念日
- `RELATIONSHIP`: 恋爱纪念日
- `WORK`: 工作纪念日
- `HOLIDAY`: 节日
- `OTHER`: 其他（默认）

**核心方法**:
- `getDaysUntil()`: 计算距离纪念日的天数（支持循环纪念日）
- `isExpired()`: 检查是否已过期（循环纪念日不会过期）
- `isToday()`: 检查是否是今天

---

## 数据库表关系

```
users (用户表)
  ├── todo_items (待办事项表) - 通过 user_id 关联
  ├── focus_sessions (专注会话表) - 通过 user_id 关联
  └── anniversaries (纪念日表) - 通过 user_id 关联
```

## 技术特性

### JPA注解
- `@Entity`: 标记为JPA实体
- `@Table`: 指定数据库表名
- `@Id`: 主键标识
- `@GeneratedValue`: 主键自动生成策略
- `@Column`: 列属性配置
- `@OneToMany`: 一对多关系
- `@ManyToOne`: 多对一关系
- `@Enumerated`: 枚举类型映射

### 数据验证
- `@NotBlank`: 非空验证
- `@NotNull`: 非null验证
- `@Size`: 长度验证
- `@Email`: 邮箱格式验证
- `@Min`: 最小值验证

### 生命周期回调
- `@PrePersist`: 持久化前自动设置创建时间
- `@PreUpdate`: 更新前自动设置更新时间

## 级联操作
所有实体都配置了级联删除（`CascadeType.ALL, orphanRemoval = true`），当删除用户时，会自动删除其关联的所有数据。

## 下一步工作
1. 创建Repository层（数据访问层）
2. 创建Service层（业务逻辑层）
3. 创建DTO层（数据传输对象）
4. 创建Controller层（控制器层）
5. 配置数据库连接
6. 实现用户认证和授权
7. 开发前端界面

## 注意事项
1. 用户密码需要在Service层使用加密算法（如BCrypt）进行加密
2. 建议添加索引以提高查询性能
3. 考虑添加软删除功能（标记删除而不是物理删除）
4. 可以添加审计字段（如：创建人、修改人等）

