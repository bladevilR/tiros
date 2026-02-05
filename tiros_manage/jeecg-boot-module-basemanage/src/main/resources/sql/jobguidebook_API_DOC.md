# 作业指导书管理模块 API 接口文档

## 基础信息

- **基础URL**: `http://localhost:8080`
- **认证方式**: Token（Bearer）
- **数据格式**: JSON
- **字符编码**: UTF-8

## 作业指导书接口

### 1. 分页查询作业指导书

**请求**
```
GET /base/job-guide-book/page
```

**参数**
| 参数名 | 类型 | 必需 | 说明 |
|-------|------|------|------|
| pageNo | integer | 否 | 页码，默认1 |
| pageSize | integer | 否 | 每页条数，默认10 |
| bookCode | string | 否 | 指导书编号 |
| bookName | string | 否 | 指导书名称 |
| contentType | string | 否 | 内容类型（simple/detailed/standard） |
| approvalStatus | string | 否 | 审批状态（DRAFT/SUBMITTED/APPROVED/REJECTED） |
| status | integer | 否 | 发布状态（0/1/2） |
| processId | string | 否 | 工序ID |
| targetStaffLevel | string | 否 | 目标员工等级 |
| companyId | string | 否 | 公司ID |
| workshopId | string | 否 | 车间ID |

**响应示例**
```json
{
  "success": true,
  "result": {
    "records": [
      {
        "id": "uuid",
        "bookCode": "JGB001",
        "bookName": "车轮组装指导书",
        "contentType": "detailed",
        "approvalStatus": "APPROVED",
        "status": 1,
        "estimatedTime": 30,
        "difficultyLevel": 3,
        "version": "1.0",
        "createTime": "2026-01-28 10:00:00"
      }
    ],
    "total": 100,
    "pages": 10,
    "current": 1
  }
}
```

### 2. 根据ID查询作业指导书

**请求**
```
GET /base/job-guide-book/get
```

**参数**
| 参数名 | 类型 | 必需 | 说明 |
|-------|------|------|------|
| id | string | 是 | 指导书ID |

**响应示例**
```json
{
  "success": true,
  "result": {
    "id": "uuid",
    "bookCode": "JGB001",
    "bookName": "车轮组装指导书",
    "contentType": "detailed",
    "approvalStatus": "APPROVED",
    "status": 1,
    "estimatedTime": 30,
    "difficultyLevel": 3,
    "version": "1.0",
    "qualityWarnings": "...",
    "sections": [
      {
        "id": "section-uuid",
        "sectionName": "准备工作",
        "sectionOrder": 1,
        "sectionType": "text",
        "content": "...",
        "steps": [
          {
            "id": "step-uuid",
            "stepTitle": "检查工具",
            "stepOrder": 1,
            "stepContent": "...",
            "operationTime": 5,
            "isCritical": 0,
            "imagesUrls": "[...]",
            "videoUrl": null
          }
        ]
      }
    ],
    "qualityChecks": [
      {
        "id": "check-uuid",
        "checkItem": "外观检查",
        "checkMethod": "目视检查",
        "acceptanceCriteria": "无破损",
        "isMandatory": 1
      }
    ]
  }
}
```

### 3. 新增/修改作业指导书

**请求**
```
POST /base/job-guide-book/save
```

**请求体**
```json
{
  "id": null,
  "bookCode": "JGB001",
  "bookName": "车轮组装指导书",
  "processId": "process-uuid",
  "targetStaffLevel": "普通",
  "contentType": "detailed",
  "estimatedTime": 30,
  "difficultyLevel": 3,
  "version": "1.0",
  "safetyWarnings": "操作时必须穿戴防护用品",
  "remarks": "这是一份详细的指导书"
}
```

**响应示例**
```json
{
  "success": true,
  "result": "uuid"
}
```

### 4. 删除作业指导书

**请求**
```
POST /base/job-guide-book/delete
```

**参数**
| 参数名 | 类型 | 必需 | 说明 |
|-------|------|------|------|
| ids | string | 是 | 指导书ID，多个逗号分隔 |

**响应示例**
```json
{
  "success": true,
  "result": true
}
```

### 5. 提交审批

**请求**
```
POST /base/job-guide-book/submit-approval
```

**参数**
| 参数名 | 类型 | 必需 | 说明 |
|-------|------|------|------|
| id | string | 是 | 指导书ID |

**响应示例**
```json
{
  "success": true,
  "result": true
}
```

### 6. 审批

**请求**
```
POST /base/job-guide-book/approve
```

**参数**
| 参数名 | 类型 | 必需 | 说明 |
|-------|------|------|------|
| id | string | 是 | 指导书ID |
| approved | boolean | 是 | 是否批准（true/false） |
| approvalComment | string | 否 | 审批意见 |

**响应示例**
```json
{
  "success": true,
  "result": true
}
```

### 7. 发布

**请求**
```
POST /base/job-guide-book/publish
```

**参数**
| 参数名 | 类型 | 必需 | 说明 |
|-------|------|------|------|
| id | string | 是 | 指导书ID |

**响应示例**
```json
{
  "success": true,
  "result": true
}
```

### 8. 作废

**请求**
```
POST /base/job-guide-book/obsolete
```

**参数**
| 参数名 | 类型 | 必需 | 说明 |
|-------|------|------|------|
| id | string | 是 | 指导书ID |

**响应示例**
```json
{
  "success": true,
  "result": true
}
```

## 章节接口

### 1. 查询章节

**请求**
```
GET /base/job-guide-book-section/get
```

**参数**
| 参数名 | 类型 | 必需 | 说明 |
|-------|------|------|------|
| id | string | 是 | 章节ID |

**响应示例**
```json
{
  "success": true,
  "result": {
    "id": "section-uuid",
    "bookId": "book-uuid",
    "sectionCode": "S001",
    "sectionName": "准备工作",
    "sectionOrder": 1,
    "sectionType": "text",
    "content": "详细内容",
    "mediaUrls": "[...]"
  }
}
```

### 2. 保存章节

**请求**
```
POST /base/job-guide-book-section/save
```

**请求体**
```json
{
  "id": null,
  "bookId": "book-uuid",
  "sectionCode": "S001",
  "sectionName": "准备工作",
  "sectionOrder": 1,
  "sectionType": "text",
  "content": "详细内容",
  "mediaUrls": "[...]",
  "parentSectionId": null
}
```

**响应示例**
```json
{
  "success": true,
  "result": "section-uuid"
}
```

### 3. 删除章节

**请求**
```
POST /base/job-guide-book-section/delete
```

**参数**
| 参数名 | 类型 | 必需 | 说明 |
|-------|------|------|------|
| id | string | 是 | 章节ID |

**响应示例**
```json
{
  "success": true,
  "result": true
}
```

## 步骤接口

### 1. 查询步骤

**请求**
```
GET /base/job-guide-book-step/get
```

**参数**
| 参数名 | 类型 | 必需 | 说明 |
|-------|------|------|------|
| id | string | 是 | 步骤ID |

**响应示例**
```json
{
  "success": true,
  "result": {
    "id": "step-uuid",
    "sectionId": "section-uuid",
    "stepOrder": 1,
    "stepTitle": "检查工具",
    "stepContent": "确保所有工具都完好无损",
    "operationTime": 5,
    "isCritical": 0,
    "imagesUrls": "[...]",
    "videoUrl": null,
    "toolsRequired": "[...]",
    "materialsRequired": "[...]",
    "safetyNotes": "小心锐角"
  }
}
```

### 2. 保存步骤

**请求**
```
POST /base/job-guide-book-step/save
```

**请求体**
```json
{
  "id": null,
  "sectionId": "section-uuid",
  "stepOrder": 1,
  "stepTitle": "检查工具",
  "stepContent": "确保所有工具都完好无损",
  "operationTime": 5,
  "isCritical": 0,
  "imagesUrls": "[...]",
  "videoUrl": null,
  "toolsRequired": "[\"扳手\", \"螺丝刀\"]",
  "materialsRequired": "[\"螺栓\", \"垫圈\"]",
  "safetyNotes": "小心锐角"
}
```

**响应示例**
```json
{
  "success": true,
  "result": "step-uuid"
}
```

### 3. 删除步骤

**请求**
```
POST /base/job-guide-book-step/delete
```

**参数**
| 参数名 | 类型 | 必需 | 说明 |
|-------|------|------|------|
| id | string | 是 | 步骤ID |

**响应示例**
```json
{
  "success": true,
  "result": true
}
```

## 质量检查接口

### 1. 查询质量检查项

**请求**
```
GET /base/job-guide-book-quality/get
```

**参数**
| 参数名 | 类型 | 必需 | 说明 |
|-------|------|------|------|
| id | string | 是 | 质量检查项ID |

**响应示例**
```json
{
  "success": true,
  "result": {
    "id": "check-uuid",
    "bookId": "book-uuid",
    "stepId": "step-uuid",
    "checkOrder": 1,
    "checkItem": "外观检查",
    "checkMethod": "目视检查",
    "acceptanceCriteria": "无破损无变形",
    "isMandatory": 1,
    "referenceStandard": "GB/T 1234-2020"
  }
}
```

### 2. 保存质量检查项

**请求**
```
POST /base/job-guide-book-quality/save
```

**请求体**
```json
{
  "id": null,
  "bookId": "book-uuid",
  "stepId": "step-uuid",
  "checkOrder": 1,
  "checkItem": "外观检查",
  "checkMethod": "目视检查",
  "acceptanceCriteria": "无破损无变形",
  "isMandatory": 1,
  "referenceStandard": "GB/T 1234-2020"
}
```

**响应示例**
```json
{
  "success": true,
  "result": "check-uuid"
}
```

### 3. 删除质量检查项

**请求**
```
POST /base/job-guide-book-quality/delete
```

**参数**
| 参数名 | 类型 | 必需 | 说明 |
|-------|------|------|------|
| id | string | 是 | 质量检查项ID |

**响应示例**
```json
{
  "success": true,
  "result": true
}
```

## 错误响应

所有API错误均返回以下格式：

```json
{
  "success": false,
  "message": "错误描述信息",
  "code": "ERROR_CODE"
}
```

### 常见错误码

| 错误码 | 说明 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

## 字段说明

### BuJobGuideBook（作业指导书主表）
- **id**: UUID，主键
- **bookCode**: 指导书编号，唯一值
- **bookName**: 指导书名称
- **contentType**: 内容类型（simple/detailed/standard）
- **approvalStatus**: 审批状态（DRAFT/SUBMITTED/APPROVED/REJECTED）
- **status**: 发布状态（0-草稿/1-已发布/2-已作废）
- **estimatedTime**: 预计时长（分钟）
- **difficultyLevel**: 难度等级（1-5）
- **version**: 版本号

### BuJobGuideBookSection（章节）
- **id**: UUID，主键
- **bookId**: 关联指导书ID
- **sectionCode**: 章节编码
- **sectionName**: 章节名称
- **sectionType**: 章节类型（text/image/video/step/table）
- **sectionOrder**: 章节顺序
- **content**: 章节内容

### BuJobGuideBookStep（步骤）
- **id**: UUID，主键
- **sectionId**: 关联章节ID
- **stepTitle**: 步骤标题
- **stepOrder**: 步骤顺序
- **stepContent**: 步骤内容
- **operationTime**: 操作时长（分钟）
- **isCritical**: 是否关键步骤（0/1）

### BuJobGuideBookQualityCheck（质量检查项）
- **id**: UUID，主键
- **bookId**: 关联指导书ID
- **stepId**: 关联步骤ID
- **checkItem**: 检查项目
- **checkMethod**: 检查方法
- **acceptanceCriteria**: 验收标准
- **isMandatory**: 是否必检（0/1）

## 使用示例

### 创建一个完整的作业指导书

```bash
# 1. 创建指导书
curl -X POST http://localhost:8080/base/job-guide-book/save \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer token" \
  -d '{
    "bookCode": "JGB001",
    "bookName": "车轮组装指导书",
    "contentType": "detailed",
    "estimatedTime": 30,
    "difficultyLevel": 3,
    "version": "1.0"
  }'

# 2. 添加章节
curl -X POST http://localhost:8080/base/job-guide-book-section/save \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer token" \
  -d '{
    "bookId": "book-uuid",
    "sectionName": "准备工作",
    "sectionOrder": 1,
    "sectionType": "text",
    "content": "开始工作前的准备"
  }'

# 3. 添加步骤
curl -X POST http://localhost:8080/base/job-guide-book-step/save \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer token" \
  -d '{
    "sectionId": "section-uuid",
    "stepTitle": "检查工具",
    "stepOrder": 1,
    "stepContent": "确保所有工具完好",
    "operationTime": 5
  }'

# 4. 提交审批
curl -X POST http://localhost:8080/base/job-guide-book/submit-approval \
  -H "Authorization: Bearer token" \
  -d 'id=book-uuid'

# 5. 审批
curl -X POST http://localhost:8080/base/job-guide-book/approve \
  -H "Authorization: Bearer token" \
  -d 'id=book-uuid&approved=true&approvalComment=同意'

# 6. 发布
curl -X POST http://localhost:8080/base/job-guide-book/publish \
  -H "Authorization: Bearer token" \
  -d 'id=book-uuid'
```

## 限流和限制

- 每个API端点有速率限制：100请求/分钟
- 单个请求超时时间：30秒
- 最大请求体大小：10MB
- 最大响应体大小：50MB

## 版本历史

| 版本 | 发布时间 | 变更内容 |
|-----|---------|---------|
| 1.0 | 2026-01-28 | 初始版本 |

## 联系方式

如有疑问，请联系API支持团队。
