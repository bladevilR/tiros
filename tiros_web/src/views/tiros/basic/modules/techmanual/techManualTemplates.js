/**
 * 作业指导书 HTML 模板片段 - 改进版本
 * 参考《苏州1号线电客车车体转向架连接大修作业指导书-2》样例格式
 */

// 统一的表格和单元格样式 - 参考样例Word文档
const tableStyle = 'border-collapse:collapse;width:100%;border:1px solid #000;font-family:宋体,SimSun;font-size:10.5pt;'
const headerCellStyle = 'border:1px solid #000;background:#e7e6e6;padding:4px;text-align:center;font-weight:bold;vertical-align:middle;'
const normalCellStyle = 'border:1px solid #000;padding:4px;vertical-align:top;'
const pageBreakStyle = 'page-break-after:always;'

/** 1. 文档标题页 */
export function coverTemplate (record) {
  const r = record || {}
  return `
<div style="${pageBreakStyle}text-align:center;padding-top:80px;">
  <h1 style="font-size:26pt;margin-bottom:40px;font-family:宋体;font-weight:bold;">${r.fileName || '作业指导书'}</h1>
  <table border="1" cellspacing="0" cellpadding="8" style="${tableStyle}margin-top:60px;width:80%;margin-left:auto;margin-right:auto;">
    <tr>
      <td style="${headerCellStyle}width:25%;">文件编号</td>
      <td style="${normalCellStyle}width:25%;">${r.fileNo || ''}</td>
      <td style="${headerCellStyle}width:25%;">版本号</td>
      <td style="${normalCellStyle}width:25%;">${r.fileVer || '1.0'}</td>
    </tr>
    <tr>
      <td style="${headerCellStyle}">编制人</td>
      <td style="${normalCellStyle}">${r.creatorName || ''}</td>
      <td style="${headerCellStyle}">编制日期</td>
      <td style="${normalCellStyle}">${r.exeTime || ''}</td>
    </tr>
    <tr>
      <td style="${headerCellStyle}">审核人</td>
      <td style="${normalCellStyle}">${r.reviewerName || ''}</td>
      <td style="${headerCellStyle}">审批人</td>
      <td style="${normalCellStyle}">${r.approverName || ''}</td>
    </tr>
    <tr>
      <td style="${headerCellStyle}">项目号</td>
      <td style="${normalCellStyle}">{{PROJECT_NO}}</td>
      <td style="${headerCellStyle}">文件名称</td>
      <td style="${normalCellStyle}">{{FILE_NAME}}</td>
    </tr>
  </table>
</div>
`
}

/** 2. 文件变更记录卡 - 标准格式 */
export function changeRecordTemplate () {
  return `
<div style="${pageBreakStyle}">
  <h2 style="font-size:14pt;font-family:宋体;margin-bottom:10px;margin-top:20px;">文件变更记录卡</h2>
  <p style="font-family:宋体;font-size:10.5pt;margin-bottom:10px;">文件编号：{{FILE_NO}} &nbsp; 文件名称：{{FILE_NAME}} &nbsp; 版本：{{FILE_VER}} &nbsp; 项目号：{{PROJECT_NO}}</p>
  <table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}">
    <tr style="background:#d9d9d9;">
      <th style="${headerCellStyle}width:8%;">序号</th>
      <th style="${headerCellStyle}width:15%;">变更日期</th>
      <th style="${headerCellStyle}width:37%;">变更内容</th>
      <th style="${headerCellStyle}width:15%;">变更人</th>
      <th style="${headerCellStyle}width:15%;">审批人</th>
      <th style="${headerCellStyle}width:10%;">版本</th>
    </tr>
    <tr><td style="${normalCellStyle}">1</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
    <tr><td style="${normalCellStyle}">2</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
    <tr><td style="${normalCellStyle}">3</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
  </table>
</div>
`
}

/** 3. 工艺流程图 */
export function processFlowTemplate () {
  return `
<div style="${pageBreakStyle}">
  <h2 style="font-size:14pt;font-family:宋体;margin-bottom:10px;margin-top:20px;">工艺流程图</h2>
  <p style="font-family:宋体;font-size:10.5pt;margin-bottom:10px;">文件编号：{{FILE_NO}} &nbsp; 文件名称：{{FILE_NAME}} &nbsp; 版本：{{FILE_VER}}</p>
  <div style="border:2px dashed #666;min-height:300px;padding:20px;text-align:center;color:#666;background:#f9f9f9;margin-top:10px;">
    <p style="font-family:宋体;font-size:11pt;">（请在此处插入工艺流程图或相关图片）</p>
  </div>
</div>
`
}

/** 4. 物料清单 - 标准格式 */
export function consumablesTemplate () {
  return `
<div style="${pageBreakStyle}">
  <h2 style="font-size:14pt;font-family:宋体;margin-bottom:10px;margin-top:20px;">消耗料清单</h2>
  <p style="font-family:宋体;font-size:10.5pt;margin-bottom:10px;">文件编号：{{FILE_NO}} &nbsp; 文件名称：{{FILE_NAME}} &nbsp; 版本：{{FILE_VER}}</p>
  <table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}">
    <tr style="background:#d9d9d9;">
      <th style="${headerCellStyle}width:8%;">序号</th>
      <th style="${headerCellStyle}width:15%;">物料编码</th>
      <th style="${headerCellStyle}width:25%;">物料名称</th>
      <th style="${headerCellStyle}width:20%;">规格型号</th>
      <th style="${headerCellStyle}width:8%;">单位</th>
      <th style="${headerCellStyle}width:8%;">数量</th>
      <th style="${headerCellStyle}width:16%;">备注</th>
    </tr>
    <tr><td style="${normalCellStyle}">1</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
    <tr><td style="${normalCellStyle}">2</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
    <tr><td style="${normalCellStyle}">3</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
  </table>
</div>
`
}

/** 5. 物料明细 */
export function materialDetailTemplate () {
  return `
<div style="${pageBreakStyle}">
  <h2 style="font-size:14pt;font-family:宋体;margin-bottom:10px;margin-top:20px;">物料明细表</h2>
  <p style="font-family:宋体;font-size:10.5pt;margin-bottom:10px;">文件编号：{{FILE_NO}} &nbsp; 文件名称：{{FILE_NAME}} &nbsp; 版本：{{FILE_VER}}</p>
  <table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}">
    <tr style="background:#d9d9d9;">
      <th style="${headerCellStyle}width:8%;">序号</th>
      <th style="${headerCellStyle}width:12%;">物料编��</th>
      <th style="${headerCellStyle}width:20%;">物料名称</th>
      <th style="${headerCellStyle}width:15%;">规格型号</th>
      <th style="${headerCellStyle}width:8%;">单位</th>
      <th style="${headerCellStyle}width:8%;">数量</th>
      <th style="${headerCellStyle}width:12%;">单价(元)</th>
      <th style="${headerCellStyle}width:17%;">备注</th>
    </tr>
    <tr><td style="${normalCellStyle}">1</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
    <tr><td style="${normalCellStyle}">2</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
  </table>
</div>
`
}

/** 6. 作业前准备 */
export function preWorkTemplate () {
  return `
<div style="${pageBreakStyle}">
  <h2 style="font-size:14pt;font-family:宋体;margin-bottom:10px;margin-top:20px;">作业前准备</h2>
  <p style="font-family:宋体;font-size:10.5pt;margin-bottom:10px;">文件编号：{{FILE_NO}} &nbsp; 文件名称：{{FILE_NAME}} &nbsp; 版本：{{FILE_VER}}</p>
  <table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}">
    <tr style="background:#d9d9d9;">
      <th style="${headerCellStyle}width:10%;">序号</th>
      <th style="${headerCellStyle}width:50%;">检查项目</th>
      <th style="${headerCellStyle}width:20%;">标准/要求</th>
      <th style="${headerCellStyle}width:20%;">确认结果</th>
    </tr>
    <tr><td style="${normalCellStyle}">1</td><td style="${normalCellStyle}">人员资质确认</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
    <tr><td style="${normalCellStyle}">2</td><td style="${normalCellStyle}">工具工装检查</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
    <tr><td style="${normalCellStyle}">3</td><td style="${normalCellStyle}">物料准备确认</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
    <tr><td style="${normalCellStyle}">4</td><td style="${normalCellStyle}">安全防护确认</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
  </table>
</div>
`
}

/** 7. 主要工序工步表 - 核心表单 */
export function processStepTemplate () {
  return `
<div style="${pageBreakStyle}">
  <h2 style="font-size:14pt;font-family:宋体;margin-bottom:10px;margin-top:20px;">工序工步</h2>
  <p style="font-family:宋体;font-size:10.5pt;margin-bottom:10px;">文件编号：{{FILE_NO}} &nbsp; 文件名称：{{FILE_NAME}} &nbsp; 版本：{{FILE_VER}}</p>
  <table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}">
    <tr style="background:#d9d9d9;">
      <th style="${headerCellStyle}width:8%;">工序号</th>
      <th style="${headerCellStyle}width:8%;">工步号</th>
      <th style="${headerCellStyle}width:35%;">作业内容</th>
      <th style="${headerCellStyle}width:20%;">技术要求</th>
      <th style="${headerCellStyle}width:14%;">工具/工装</th>
      <th style="${headerCellStyle}width:15%;">备注</th>
    </tr>
    <tr><td style="${normalCellStyle}">1</td><td style="${normalCellStyle}">1</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
    <tr><td style="${normalCellStyle}"></td><td style="${normalCellStyle}">2</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
    <tr><td style="${normalCellStyle}">2</td><td style="${normalCellStyle}">1</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
  </table>
</div>
`
}

/** 8. 工具工装清单 */
export function toolsTemplate () {
  return `
<div style="${pageBreakStyle}">
  <h2 style="font-size:14pt;font-family:宋体;margin-bottom:10px;margin-top:20px;">工具工装清单</h2>
  <p style="font-family:宋体;font-size:10.5pt;margin-bottom:10px;">文件编号：{{FILE_NO}} &nbsp; 文件名称：{{FILE_NAME}} &nbsp; 版本：{{FILE_VER}}</p>
  <table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}">
    <tr style="background:#d9d9d9;">
      <th style="${headerCellStyle}width:10%;">序号</th>
      <th style="${headerCellStyle}width:18%;">工具编码</th>
      <th style="${headerCellStyle}width:24%;">工具名称</th>
      <th style="${headerCellStyle}width:20%;">规格型号</th>
      <th style="${headerCellStyle}width:10%;">数量</th>
      <th style="${headerCellStyle}width:18%;">备注</th>
    </tr>
    <tr><td style="${normalCellStyle}">1</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
    <tr><td style="${normalCellStyle}">2</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
  </table>
</div>
`
}

/** 9. 扭力值清单 */
export function torqueListTemplate () {
  return `
<div style="${pageBreakStyle}">
  <h2 style="font-size:14pt;font-family:宋体;margin-bottom:10px;margin-top:20px;">扭力值清单</h2>
  <p style="font-family:宋体;font-size:10.5pt;margin-bottom:10px;">文件编号：{{FILE_NO}} &nbsp; 文件名称：{{FILE_NAME}} &nbsp; 版本：{{FILE_VER}}</p>
  <table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}">
    <tr style="background:#d9d9d9;">
      <th style="${headerCellStyle}width:10%;">序号</th>
      <th style="${headerCellStyle}width:25%;">安装部位</th>
      <th style="${headerCellStyle}width:20%;">螺栓规格</th>
      <th style="${headerCellStyle}width:15%;">扭力值(N·m)</th>
      <th style="${headerCellStyle}width:15%;">工具</th>
      <th style="${headerCellStyle}width:15%;">备注</th>
    </tr>
    <tr><td style="${normalCellStyle}">1</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
    <tr><td style="${normalCellStyle}">2</td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td><td style="${normalCellStyle}"></td></tr>
  </table>
</div>
`
}

/** 10. 附录 */
export function appendixTemplate () {
  return `
<div style="${pageBreakStyle}">
  <h2 style="font-size:14pt;font-family:宋体;margin-bottom:10px;margin-top:20px;">附录</h2>
  <p style="font-family:宋体;font-size:10.5pt;margin-bottom:10px;">文件编号：{{FILE_NO}} &nbsp; 文件名称：{{FILE_NAME}} &nbsp; 版本：{{FILE_VER}}</p>
  <div style="border:1px solid #333;min-height:250px;padding:15px;color:#666;background:#fafafa;margin-top:10px;">
    <p style="font-family:宋体;font-size:10.5pt;color:#999;">（请在此处填写附录内容，可插入图片、其他表格、补充说明等）</p>
  </div>
</div>
`
}
