# 作业指导书管理模块集成指南

## 一、数据库集成步骤

### 1. 执行SQL脚本
```bash
# 在MySQL数据库中执行以下脚本
source jobguidebook.sql
```

### 2. 验证表创建
```sql
SHOW TABLES LIKE 'bu_job_guide%';
```

应该显示以下4个表：
- bu_job_guide_book
- bu_job_guide_book_section
- bu_job_guide_book_step
- bu_job_guide_book_quality_check

## 二、后端集成步骤

### 1. 复制Java文件到项目
```
源目录: /jobguidebook/
目标目录: jeecg-boot-module-basemanage/src/main/java/org/jeecg/modules/basemanage/jobguidebook/
```

目录结构：
```
jobguidebook/
├── bean/
│   ├── BuJobGuideBook.java
│   ├── BuJobGuideBookSection.java
│   ├── BuJobGuideBookStep.java
│   ├── BuJobGuideBookQualityCheck.java
│   └── vo/
│       └── BuJobGuideBookQueryVO.java
├── controller/
│   ├── BuJobGuideBookController.java
│   ├── BuJobGuideBookSectionController.java
│   ├── BuJobGuideBookStepController.java
│   └── BuJobGuideBookQualityCheckController.java
├── mapper/
│   ├── BuJobGuideBookMapper.java
│   ├── BuJobGuideBookSectionMapper.java
│   ├── BuJobGuideBookStepMapper.java
│   └── BuJobGuideBookQualityCheckMapper.java
└── service/
    ├── BuJobGuideBookService.java
    ├── BuJobGuideBookSectionService.java
    ├── BuJobGuideBookStepService.java
    ├── BuJobGuideBookQualityCheckService.java
    └── impl/
        ├── BuJobGuideBookServiceImpl.java
        ├── BuJobGuideBookSectionServiceImpl.java
        ├── BuJobGuideBookStepServiceImpl.java
        └── BuJobGuideBookQualityCheckServiceImpl.java
```

### 2. 复制MyBatis XML映射文件
```
源文件: BuJobGuideBookMapper.xml
目标目录: jeecg-boot-module-basemanage/src/main/resources/mapper/jobguidebook/
```

### 3. MyBatis配置（如果需要）
在 `application.yml` 或 `application-dev.yml` 中确保已配置MyBatis映射扫描：

```yaml
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: org.jeecg.modules.**.bean
```

### 4. 验证后端集成
- 重启应用
- 测试API端点：`http://localhost:8080/base/job-guide-book/page`
- 应该返回正常的JSON响应

## 三、前端集成步骤

### 1. 复制Vue文件到项目
```
源目录: /jobguidebook/
目标目录: tiros_web/src/views/tiros/basic/
```

目录结构：
```
basic/
├── JobGuideBook.vue
└── jobguidebook/
    ├── JobGuideBookModal.vue
    ├── JobGuideBookEditor.vue
    ├── SectionEditorModal.vue
    ├── StepEditorModal.vue
    ├── QualityCheckModal.vue
    ├── JobGuideBookPreview.vue
    ├── ApprovalModal.vue
    ├── JobGuideBookList.vue
    └── JobGuideBookExport.vue
```

### 2. 复制API文件
```
源文件: jobguidebook.js
目标目录: tiros_web/src/api/
```

### 3. 在路由中注册页面
在 `router/index.js` 或相应的路由配置文件中添加：

```javascript
{
  path: '/jobguidebook',
  component: () => import('@/views/tiros/basic/JobGuideBook'),
  name: 'JobGuideBook',
  meta: {
    title: '作业指导书管理',
    icon: 'book'
  }
}
```

### 4. 在菜单中添加导航
在菜单配置文件中添加：

```javascript
{
  name: '作业指导书管理',
  path: '/jobguidebook',
  component: 'JobGuideBook',
  icon: 'book',
  authority: ['admin', 'jobguidebook']
}
```

### 5. 安装必要的npm依赖

确保已安装以下依赖：
```bash
# 拖拽排序
npm install vuedraggable

# 其他Ant Design Vue相关的依赖应该已安装
```

### 6. 验证前端集成
- 启动开发服务器：`npm run serve` 或 `yarn serve`
- 在浏览器中访问：`http://localhost:8081/#/jobguidebook`
- 应该看到作业指导书列表页面

## 四、权限配置

### 1. 添加权限码
在系统权限配置中添加以下权限：

```
jobguidebook:list      - 查看作业指导书列表
jobguidebook:add       - 新增作业指导书
jobguidebook:edit      - 编辑作业指导书
jobguidebook:delete    - 删除作业指导书
jobguidebook:approve   - 审批作业指导书
jobguidebook:publish   - 发布作业指导书
```

### 2. 配置角色权限
根据业务需求为不同角色配置权限：
- 管理员：全部权限
- 编辑员：list, add, edit, delete
- 审批员：list, approve
- 查看员：list

## 五、应用配置文件修改（可选）

### 1. application.yml 配置
```yaml
# 如果需要自定义配置
jobguidebook:
  # 允许上传的图片大小限制（MB）
  max-image-size: 10
  # 允许上传的视频大小限制（MB）
  max-video-size: 500
  # 版本管理
  enable-version-control: true
```

## 六、字典配置

在系统字典中添加以下字典项：

### 1. approval_status（审批状态）
```
DRAFT     - 草稿
SUBMITTED - 已提交
APPROVED  - 已批准
REJECTED  - 已驳回
```

### 2. job_guide_book_status（发布状态）
```
0 - 草稿
1 - 已发布
2 - 已作废
```

### 3. content_type（内容类型）
```
simple    - 简易
detailed  - 详细
standard  - 对标
```

### 4. target_staff_level（目标员工等级）
```
新员工 - 新员工
普通   - 普通
高级   - 高级
```

## 七、测试检查清单

### 后端测试
- [ ] 数据库表已创建
- [ ] 后端服务已启动
- [ ] API端点可访问
- [ ] 分页查询正常工作
- [ ] CRUD操作正常工作
- [ ] 审批流程正确
- [ ] 级联删除正常

### 前端测试
- [ ] 页面可以加载
- [ ] 列表可以显示数据
- [ ] 可以新增记录
- [ ] 可以编辑记录
- [ ] 可以删除记录
- [ ] 拖拽排序正常工作
- [ ] 预览功能正常
- [ ] 审批流程界面显示正确

### 集成测试
- [ ] 新增流程完整
- [ ] 编辑流程完整
- [ ] 审批流程完整
- [ ] 发布流程完整
- [ ] 作废流程完整
- [ ] 权限控制正确

## 八、常见问题排查

### 问题1：后端找不到Mapper
**解决方案：**
```yaml
# 在application.yml中检查并添加
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
```

### 问题2：前端请求404
**解决方案：**
- 检查API路由是否正确注册
- 检查后端是否已启动
- 查看浏览器控制台的网络请求

### 问题3：拖拽功能不工作
**解决方案：**
```bash
# 确保已安装vuedraggable
npm install vuedraggable --save
```

### 问题4：样式未加载
**解决方案：**
- 确保Ant Design Vue已安装
- 检查less/css预处理器配置
- 清空浏览器缓存

## 九、性能优化建议

### 1. 数据库优化
```sql
-- 添加必要的索引
CREATE INDEX idx_book_status ON bu_job_guide_book(status);
CREATE INDEX idx_approval_status ON bu_job_guide_book(approval_status);
CREATE INDEX idx_section_book_id ON bu_job_guide_book_section(book_id);
```

### 2. 列表分页
- 默认每页10条记录
- 建议最多显示50条

### 3. 图片和视频处理
- 使用CDN存储多媒体文件
- 图片进行压缩和缩略图处理
- 视频使用流媒体服务

## 十、备份和恢复

### 备份数据库
```bash
mysqldump -u user -p database_name bu_job_guide_book > jobguidebook_backup.sql
```

### 恢复数据
```bash
mysql -u user -p database_name < jobguidebook_backup.sql
```

## 十一、后续维护

### 定期任务
- [ ] 每月检查未发布的草稿
- [ ] 定期清理已作废的记录
- [ ] 监控系统日志
- [ ] 备份关键数据

### 性能监控
- [ ] 查询响应时间
- [ ] 数据库连接池状态
- [ ] 文件存储空间
- [ ] 用户访问统计

## 技术支持

如遇到问题，请提供以下信息：
1. 错误日志截图或文本
2. 重现问题的步骤
3. 系统环境信息（OS、Java版本、MySQL版本等）
4. 浏览器控制台错误信息

## 相关文档

- [作业指导书管理模块说明书](./jobguidebook_README.md)
- [API接口文档](./API_DOC.md)
- [前端组件文档](./COMPONENT_DOC.md)
