# 作业指导书管理模块（Job Guide Book Management Module）

## 模块概述

作业指导书管理模块是为一线作业人员提供详细操作指导的管理系统。该模块基于TechManual（技术手册）模块的设计理念，但更加专注于实际操作流程。

## 核心功能

### 1. 作业指导书管理
- **新增/编辑** - 创建和修改作业指导书
- **删除** - 删除未发布的作业指导书
- **查询** - 支持多条件查询（编号、名称、类型、审批状态、发布状态等）
- **预览** - 实时预览指导书内容

### 2. 版本和审批管理
- **草稿** - 初始状态，可自由编辑
- **提交审批** - 将草稿提交给审批人
- **审批流程** - 审批人可以批准或驳回
- **发布** - 批准后的指导书可发布使用
- **作废** - 发布后可标记为作废

### 3. 内容编辑
- **章节管理** - 支持多级章节结构
- **步骤编辑** - 每个章节可包含多个操作步骤
- **多媒体支持** - 支持文字、图片、视频
- **拖拽排序** - 支持拖拽调整章节和步骤顺序

### 4. 质量管理
- **质量检查项** - 定义每个步骤的质量检查要点
- **验收标准** - 明确的验收和质量标准
- **必检项管理** - 区分必检和可选检查项

### 5. 安全管理
- **安全警示** - 为整个指导书设置统一的安全警示
- **步骤级安全提示** - 为每个步骤添加具体的安全注意事项

## 数据库表

### 1. bu_job_guide_book（作业指导书主表）
- 存储指导书的基本信息、版本、审批状态等

### 2. bu_job_guide_book_section（章节表）
- 存储指导书的章节信息，支持多级结构

### 3. bu_job_guide_book_step（步骤表）
- 存储章节下的操作步骤，包含多媒体链接、工具、物料等信息

### 4. bu_job_guide_book_quality_check（质量检查项表）
- 存储质量检查项，可关联到指导书或具体步骤

## 后端接口

### 作业指导书接口
```
GET  /base/job-guide-book/page              - 分页查询
GET  /base/job-guide-book/get               - 根据ID查询
POST /base/job-guide-book/save              - 新增/修改
POST /base/job-guide-book/delete            - 删除
POST /base/job-guide-book/submit-approval   - 提交审批
POST /base/job-guide-book/approve           - 审批
POST /base/job-guide-book/publish           - 发布
POST /base/job-guide-book/obsolete          - 作废
```

### 章节接口
```
GET  /base/job-guide-book-section/get       - 查询
POST /base/job-guide-book-section/save      - 新增/修改
POST /base/job-guide-book-section/delete    - 删除
```

### 步骤接口
```
GET  /base/job-guide-book-step/get          - 查询
POST /base/job-guide-book-step/save         - 新增/修改
POST /base/job-guide-book-step/delete       - 删除
```

### 质量检查接口
```
GET  /base/job-guide-book-quality/get       - 查询
POST /base/job-guide-book-quality/save      - 新增/修改
POST /base/job-guide-book-quality/delete    - 删除
```

## 前端文件

### 主要页面
- `JobGuideBook.vue` - 作业指导书主页面（列表和操作）

### 弹窗组件
- `JobGuideBookModal.vue` - 编辑弹窗（基本信息和内容）
- `JobGuideBookEditor.vue` - 内容编辑器（章节和步骤编辑）
- `SectionEditorModal.vue` - 章节编辑弹窗
- `StepEditorModal.vue` - 步骤���辑弹窗
- `QualityCheckModal.vue` - 质量检查项编辑
- `ApprovalModal.vue` - 审批弹窗
- `JobGuideBookPreview.vue` - 预览弹窗

### 辅助组件
- `JobGuideBookList.vue` - 列表展示组件
- `JobGuideBookExport.vue` - 导出组件

### API模块
- `api/jobguidebook.js` - 所有API调用

## 使用流程

### 作业指导书创建流程

1. **创建阶段**
   - 点击"新增"按钮打开编辑弹窗
   - 填写基本信息（编号、名称、类型等）
   - 添加安全警示和备注

2. **内容编辑阶段**
   - 在"内容编辑"选项卡中添加章节
   - 为每个章节添加步骤
   - 上传步骤相关的图片和视频

3. **质量设置阶段**
   - 在"质量检查项"选项卡中定义检查项
   - 设置验收标准和参考标准

4. **提交审批**
   - 保存完成后，点击"提交审批"按钮
   - 指导书状态变为"已提交"

5. **审批阶段**
   - 审批人收到审批请求
   - 审批人可以批准或驳回
   - 驳回会返回到草稿状态，可重新编辑

6. **发布阶段**
   - 审批通过后，点击"发布"按钮
   - 发布后状态变为"已发布"，一线人员可访问

7. **作废处理**
   - 需要停用时，点击"作废"按钮
   - 状态变为"已作废"，一线人员无法访问

## 技术栈

### 后端
- Spring Boot
- MyBatis Plus
- MySQL
- Shiro（权限管理）

### 前端
- Vue.js
- Ant Design Vue
- Vuedraggable（拖拽排序）

## 关键实现细节

### 1. 状态管理
```
审批状态流程: DRAFT -> SUBMITTED -> APPROVED/REJECTED -> DRAFT
发布状态流程: 0(草稿) -> 1(发布) -> 2(作废)
```

### 2. 数据关联
- 指导书包含多个章节
- 章节包含多个步骤
- 质量检查项可关联到指导书或步骤

### 3. 多媒体处理
- 图片和视频URL存储在JSON字段中
- 支持上传和本地URL引用
- 预览时自动加载和显示

### 4. 拖拽排序
- 使用Vuedraggable库实现
- 章节和步骤都支持拖拽排序
- 拖拽结束后自动更新顺序字段

## 扩展建议

### 1. 导出功能
- 当前导出PDF功能为占位符
- 建议集成 iTextPDF 或 Apache POI 库
- 可导出为PDF、Word、Excel等格式

### 2. 版本管理
- 可以增强版本管理功能
- 支持版本对比和版本回退

### 3. 附件管理
- 可以添加通用附件功能
- 支持更多类型的文件上传

### 4. 统计分析
- 可以添加指导书使用统计
- 跟踪一线人员的访问和使用情况

### 5. 集成工作流
- 当前审批流程简单，可集成成熟的工作流引擎
- 支持更复杂的流程定义

## 常见问题

### Q: 如何处理大型视频文件？
A: 建议使用第三方视频服务（如阿里云、腾讯云等），存储URL即可。

### Q: 是否支持离线访问？
A: 当前不支持，可以通过添加缓存机制实现部分离线功能。

### Q: 如何确保质量检查的完整性？
A: 系统会标记必检项，一线人员完成工作时需要逐一确认。

## 许可证

该模块为项目内部模块，遵循项目的许可证条款。

## 联系方式

如有问题或建议，请联系项目开发团队。
