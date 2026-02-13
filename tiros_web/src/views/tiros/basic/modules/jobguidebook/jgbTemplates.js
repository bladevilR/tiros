/**
 * 作业指导书 HTML 模板片段
 * 通过 TinyMCE insertContent 插入编辑器
 */

const tableStyle = 'width:100%;border-collapse:collapse;'
const cellStyle = 'border:1px solid #333;padding:6px 8px;'

const FIELD_TOKEN_MAP = {
  FILE_NO: 'fileNo',
  FILE_NAME: 'fileName',
  FILE_VER: 'fileVer',
  PROJECT: 'project',
  CREATOR_NAME: 'creatorName',
  EXE_TIME: 'exeTime',
  REVIEWER_NAME: 'reviewerName',
  APPROVER_NAME: 'approverName'
}

function toStringSafe (value) {
  if (value === null || value === undefined) return ''
  return String(value)
}

function buildTokenFields (record) {
  const source = record || {}
  const fields = {}
  Object.keys(FIELD_TOKEN_MAP).forEach(token => {
    const field = FIELD_TOKEN_MAP[token]
    fields[token] = toStringSafe(source[field])
  })
  return fields
}

function tokenCell (token) {
  return `<span data-jgb-token="${token}">${'{{' + token + '}}'}</span>`
}

/** 1. 封面 */
export function coverTemplate () {
  return `
<div style="page-break-after:always;text-align:center;padding-top:120px;">
  <h1 style="font-size:28px;">${tokenCell('FILE_NAME')}</h1>
  <table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}margin-top:60px;">
    <tr>
      <td style="${cellStyle}width:25%;">文件编号</td>
      <td style="${cellStyle}width:25%;">${tokenCell('FILE_NO')}</td>
      <td style="${cellStyle}width:25%;">版本号</td>
      <td style="${cellStyle}width:25%;">${tokenCell('FILE_VER')}</td>
    </tr>
    <tr>
      <td style="${cellStyle}">编制人</td>
      <td style="${cellStyle}">${tokenCell('CREATOR_NAME')}</td>
      <td style="${cellStyle}">编制日期</td>
      <td style="${cellStyle}">${tokenCell('EXE_TIME')}</td>
    </tr>
    <tr>
      <td style="${cellStyle}">审核人</td>
      <td style="${cellStyle}">${tokenCell('REVIEWER_NAME')}</td>
      <td style="${cellStyle}">审批人</td>
      <td style="${cellStyle}">${tokenCell('APPROVER_NAME')}</td>
    </tr>
    <tr>
      <td style="${cellStyle}">项目号</td>
      <td style="${cellStyle}" colspan="3">${tokenCell('PROJECT')}</td>
    </tr>
  </table>
</div>
`
}

/** 2. 文件变更记录卡 */
export function changeRecordTemplate () {
  return `
<h2>文件变更记录卡</h2>
<table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}">
  <tr style="background:#f0f0f0;">
    <th style="${cellStyle}width:8%;">序号</th>
    <th style="${cellStyle}width:15%;">变更日期</th>
    <th style="${cellStyle}width:37%;">变更内容</th>
    <th style="${cellStyle}width:15%;">变更人</th>
    <th style="${cellStyle}width:15%;">审批人</th>
    <th style="${cellStyle}width:10%;">版本</th>
  </tr>
  <tr><td style="${cellStyle}">1</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}">2</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}">3</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
</table>
`
}

/** 3. 工艺流程图 */
export function processFlowTemplate () {
  return `
<h2>工艺流程图</h2>
<div style="border:1px dashed #999;min-height:300px;padding:20px;text-align:center;color:#999;">
  <p>（请在此处插入工艺流程图图片）</p>
</div>
`
}

/** 4. 消耗料 */
export function consumablesTemplate () {
  return `
<h2>消耗料清单</h2>
<table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}">
  <tr style="background:#f0f0f0;">
    <th style="${cellStyle}width:8%;">序号</th>
    <th style="${cellStyle}width:18%;">物料编码</th>
    <th style="${cellStyle}width:22%;">物料名称</th>
    <th style="${cellStyle}width:18%;">规格型号</th>
    <th style="${cellStyle}width:10%;">单位</th>
    <th style="${cellStyle}width:10%;">数量</th>
    <th style="${cellStyle}width:14%;">备注</th>
  </tr>
  <tr><td style="${cellStyle}">1</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}">2</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}">3</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
</table>
`
}

/** 5. 物料明细 */
export function materialDetailTemplate () {
  return `
<h2>物料明细</h2>
<table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}">
  <tr style="background:#f0f0f0;">
    <th style="${cellStyle}width:8%;">序号</th>
    <th style="${cellStyle}width:15%;">物料编码</th>
    <th style="${cellStyle}width:20%;">物料名称</th>
    <th style="${cellStyle}width:15%;">规格型号</th>
    <th style="${cellStyle}width:8%;">单位</th>
    <th style="${cellStyle}width:8%;">数量</th>
    <th style="${cellStyle}width:12%;">单价</th>
    <th style="${cellStyle}width:14%;">备注</th>
  </tr>
  <tr><td style="${cellStyle}">1</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}">2</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}">3</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
</table>
`
}

/** 6. 作业前准备 */
export function preWorkTemplate () {
  return `
<h2>作业前准备</h2>
<table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}">
  <tr style="background:#f0f0f0;">
    <th style="${cellStyle}width:8%;">序号</th>
    <th style="${cellStyle}width:52%;">检查项目</th>
    <th style="${cellStyle}width:20%;">标准/要求</th>
    <th style="${cellStyle}width:20%;">确认结果</th>
  </tr>
  <tr><td style="${cellStyle}">1</td><td style="${cellStyle}">人员资质确认</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}">2</td><td style="${cellStyle}">工具工装检查</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}">3</td><td style="${cellStyle}">物料准备确认</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}">4</td><td style="${cellStyle}">安全防护确认</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
</table>
`
}

/** 7. 工序工步 */
export function processStepTemplate () {
  return `
<h2>工序工步</h2>
<table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}">
  <tr style="background:#f0f0f0;">
    <th style="${cellStyle}width:8%;">工序号</th>
    <th style="${cellStyle}width:8%;">工步号</th>
    <th style="${cellStyle}width:34%;">作业内容</th>
    <th style="${cellStyle}width:20%;">技术要求</th>
    <th style="${cellStyle}width:15%;">工具/工装</th>
    <th style="${cellStyle}width:15%;">备注</th>
  </tr>
  <tr><td style="${cellStyle}">1</td><td style="${cellStyle}">1</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}"></td><td style="${cellStyle}">2</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}">2</td><td style="${cellStyle}">1</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
</table>
`
}

/** 8. 工具工装 */
export function toolsTemplate () {
  return `
<h2>工具工装清单</h2>
<table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}">
  <tr style="background:#f0f0f0;">
    <th style="${cellStyle}width:8%;">序号</th>
    <th style="${cellStyle}width:18%;">工具编码</th>
    <th style="${cellStyle}width:24%;">工具名称</th>
    <th style="${cellStyle}width:20%;">规格型号</th>
    <th style="${cellStyle}width:10%;">数量</th>
    <th style="${cellStyle}width:20%;">备注</th>
  </tr>
  <tr><td style="${cellStyle}">1</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}">2</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}">3</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
</table>
`
}

/** 9. 扭力值清单 */
export function torqueListTemplate () {
  return `
<h2>扭力值清单</h2>
<table border="1" cellspacing="0" cellpadding="6" style="${tableStyle}">
  <tr style="background:#f0f0f0;">
    <th style="${cellStyle}width:8%;">序号</th>
    <th style="${cellStyle}width:22%;">安装部位</th>
    <th style="${cellStyle}width:20%;">螺栓规格</th>
    <th style="${cellStyle}width:15%;">扭力值(N·m)</th>
    <th style="${cellStyle}width:15%;">工具</th>
    <th style="${cellStyle}width:20%;">备注</th>
  </tr>
  <tr><td style="${cellStyle}">1</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}">2</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
  <tr><td style="${cellStyle}">3</td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td><td style="${cellStyle}"></td></tr>
</table>
`
}

/** 10. 附录 */
export function appendixTemplate () {
  return `
<h2>附录</h2>
<div style="border:1px dashed #999;min-height:200px;padding:20px;color:#999;">
  <p>（请在此处填写附录内容，可插入图片、表格等）</p>
</div>
`
}

export function resolveTemplateTokens (html, record) {
  let output = html || ''
  const fields = buildTokenFields(record)
  Object.keys(fields).forEach(token => {
    const value = fields[token]
    const pattern = new RegExp(`\\{\\{${token}\\}\\}`, 'g')
    output = output.replace(pattern, value)
  })
  return output
}
