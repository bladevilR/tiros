<template>
  <a-modal
    v-model="visible"
    title="编辑作业指导书正文"
    :width="'96%'"
    :maskClosable="false"
    :footer="null"
    :bodyStyle="modalBodyStyle"
    :dialogStyle="{ top: '10px' }"
    wrapClassName="tech-book-editor-modal"
  >
    <div class="editor-toolbar">
      <a-space>
        <a-button type="primary" @click="handleSave" :loading="loading">保存</a-button>
        <a-dropdown>
          <a-button>插入模板</a-button>
          <a-menu slot="overlay" @click="handleInsertTemplate">
            <a-menu-item key="cover">封面</a-menu-item>
            <a-menu-item key="changeRecord">文件变更记录卡</a-menu-item>
            <a-menu-item key="processFlow">工艺流程图</a-menu-item>
            <a-menu-item key="preWork">作业前准备</a-menu-item>
            <a-menu-item key="processStep">工序工步</a-menu-item>
            <a-menu-item key="tools">工具工装</a-menu-item>
            <a-menu-item key="consumables">消耗料</a-menu-item>
            <a-menu-item key="materialDetail">物料明细</a-menu-item>
            <a-menu-item key="torqueList">扭力值清单</a-menu-item>
            <a-menu-item key="appendix">附录</a-menu-item>
          </a-menu>
        </a-dropdown>
        <a-button @click="handleRebuild">重新生成模板</a-button>
        <a-button type="primary" @click="handleExport" :loading="loading">导出规范Word</a-button>
        <a-button @click="handleClose">关闭</a-button>
      </a-space>
      <a-space>
        <a-button size="small" @click="insertPageBreak">插入分页符</a-button>
      </a-space>
      <div class="tips">
        当前模板：{{ record.fileName || '-' }}（版本：{{ record.fileVer || '-' }}）
      </div>
    </div>
    <div class="editor-body">
      <j-editor v-model="contentHtml" :height="editorHeight"></j-editor>
    </div>
  </a-modal>
</template>

<script>
import JEditor from '@/components/jeecg/JEditor'
import { saveSopContent, getSopDetailPage, getSopDetailRecord } from '@/api/tirosApi'
import * as tpl from './techManualTemplates'

const placeholderTokens = ['{{FILE_NO}}', '{{FILE_NAME}}', '{{FILE_VER}}', '{{PROJECT_NO}}']

export default {
  name: 'TechBookEditorModal',
  components: { JEditor },
  data () {
    return {
      visible: false,
      record: {},
      contentHtml: '',
      loading: false,
      editorHeight: 520
    }
  },
  computed: {
    modalBodyStyle () {
      return {
        height: 'calc(100vh - 120px)',
        overflow: 'hidden'
      }
    }
  },
  methods: {
    handleInsertTemplate ({ key }) {
      const templateMap = {
        cover: tpl.coverTemplate,
        changeRecord: tpl.changeRecordTemplate,
        processFlow: tpl.processFlowTemplate,
        preWork: tpl.preWorkTemplate,
        processStep: tpl.processStepTemplate,
        tools: tpl.toolsTemplate,
        consumables: tpl.consumablesTemplate,
        materialDetail: tpl.materialDetailTemplate,
        torqueList: tpl.torqueListTemplate,
        appendix: tpl.appendixTemplate
      }
      const fn = templateMap[key]
      if (fn) {
        const html = this.resolveTemplatePlaceholders(fn(this.record || {}))
        this.contentHtml = (this.contentHtml || '') + html
      }
    },
    resolveTemplatePlaceholders (html) {
      let output = html || ''
      const valueMap = {
        '{{FILE_NO}}': (this.record && this.record.fileNo) ? this.record.fileNo : '',
        '{{FILE_NAME}}': (this.record && this.record.fileName) ? this.record.fileName : '',
        '{{FILE_VER}}': (this.record && this.record.fileVer) ? this.record.fileVer : '',
        '{{PROJECT_NO}}': (this.record && (this.record.projectNo || this.record.project || this.record.fileNo))
          ? (this.record.projectNo || this.record.project || this.record.fileNo)
          : ''
      }
      placeholderTokens.forEach(token => {
        output = output.split(token).join(valueMap[token])
      })
      return output
    },
    insertPageBreak () {
      const pageBreakHtml = '<div style="page-break-after:always;"><span style="color:#999;">—— 分页 ——</span></div>'
      this.contentHtml = (this.contentHtml || '') + pageBreakHtml
    },
    async open (record) {
      this.record = record || {}
      const canEdit = this.record.status === 0 || this.record.status === 2
      this.contentHtml = record.contentHtml || ''
      this.visible = true
      this.setEditorHeight()
      window.addEventListener('resize', this.setEditorHeight)
      if (!this.contentHtml && this.record.id) {
        this.loading = true
        try {
          const listRes = await getSopDetailPage({ bookId: this.record.id })
          const detailList = (listRes && listRes.result) ? listRes.result : []
          const detailPromises = detailList.map(item => getSopDetailRecord({ detailId: item.id }))
          const detailResults = await Promise.all(detailPromises)
          const fullDetails = detailResults
            .filter(r => r && r.success)
            .map(r => r.result)
            .sort((a, b) => (a.stepNum || 0) - (b.stepNum || 0))
          this.contentHtml = this.renderTemplate(this.record, fullDetails)
          if (canEdit) {
            await saveSopContent(this.record.id, this.contentHtml)
          }
        } finally {
          this.loading = false
        }
      }
    },
    handleSave () {
      if (!this.record.id) {
        this.$message.warning('未选择指导书')
        return
      }
      const editableByStatus = this.record.status === 2 || (this.record.status === 0 && this.record.reviewStatus !== 2)
      if (!editableByStatus) {
        this.$message.warning('仅草稿、审核中、审批中状态可编辑保存')
        return
      }
      this.loading = true
      saveSopContent(this.record.id, this.contentHtml).then(res => {
        if (res.success) {
          this.$message.success('正文已保存')
          this.$emit('saved')
          this.handleClose()
        } else {
          this.$message.error(res.message || '保存失败')
        }
      }).finally(() => { this.loading = false })
    },
    async handleExport () {
      if (!this.contentHtml) {
        this.$message.warning('暂无正文内容，无法导出')
        return
      }

      this.loading = true
      try {
        const response = await this.$axios({
          method: 'post',
          url: '/api/techmanual/export-word',
          data: {
            htmlContent: this.contentHtml,
            manualName: this.record.fileName || '作业指导书',
            fileNo: this.record.fileNo || '',
            fileVer: this.record.fileVer || '1.0',
            projectNo: this.record.projectNo || this.record.project || ''
          },
          responseType: 'blob'
        })

        // 下载文件
        const fileName = `${this.record.fileName || '作业指导书'}.docx`
        const blob = response.data
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = fileName
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        this.$message.success('导出成功！')
      } catch (error) {
        this.$message.error('导出失败：' + (error.message || '未知错误'))
        console.error('导出失败', error)
      } finally {
        this.loading = false
      }
    },
    handleRebuild () {
      if (!this.record.id) {
        this.$message.warning('未选择指导书')
        return
      }
      this.$confirm({
        title: '重新生成模板',
        content: '将根据步骤/物料/工器具重新生成正文，当前编辑内容会被覆盖，是否继续？',
        onOk: async () => {
          this.loading = true
          try {
            const listRes = await getSopDetailPage({ bookId: this.record.id })
            const detailList = (listRes && listRes.result) ? listRes.result : []
            const detailPromises = detailList.map(item => getSopDetailRecord({ detailId: item.id }))
            const detailResults = await Promise.all(detailPromises)
            const fullDetails = detailResults
              .filter(r => r && r.success)
              .map(r => r.result)
              .sort((a, b) => (a.stepNum || 0) - (b.stepNum || 0))
            this.contentHtml = this.renderTemplate(this.record, fullDetails)
            await saveSopContent(this.record.id, this.contentHtml)
            this.$message.success('已重新生成')
          } finally {
            this.loading = false
          }
        }
      })
    },
    handleClose () {
      this.visible = false
      this.record = {}
      this.contentHtml = ''
      this.loading = false
      window.removeEventListener('resize', this.setEditorHeight)
    },
    setEditorHeight () {
      const h = window.innerHeight || 900
      this.editorHeight = Math.max(420, h - 220)
    },
    renderTemplate (record, details) {
      if (!details || details.length === 0) {
        return ''
      }
      const autoHeader = `
        <h1 style="text-align:center;">${record.fileName || '作业指导书'}</h1>
        <table border="1" cellspacing="0" cellpadding="6" style="width:100%;border-collapse:collapse;">
          <tr>
            <td style="width:15%;">文件编号</td><td style="width:35%;">${record.fileNo || ''}</td>
            <td style="width:15%;">版本</td><td style="width:35%;">${record.fileVer || ''}</td>
          </tr>
          <tr>
            <td>线路</td><td>${record.lineName || ''}</td>
            <td>修程</td><td>${record.repairProgramName || ''}</td>
          </tr>
          <tr>
            <td>实施日期</td><td colspan="3">${record.exeTime || ''}</td>
          </tr>
        </table>
        <h2>工序工步</h2>
      `
      const body = (details || []).map((d, idx) => {
        const stepContent = d.stepContent ? `<div>${d.stepContent}</div>` : '<div>（无内容）</div>'
        const materialTable = (d.materialList && d.materialList.length) ? `
          <h4>物料</h4>
          <table border="1" cellspacing="0" cellpadding="6" style="width:100%;border-collapse:collapse;">
            <tr><th>编码</th><th>名称</th><th>规格</th><th>单位</th><th>数量</th></tr>
            ${d.materialList.map(m => `
              <tr>
                <td>${m.code || ''}</td>
                <td>${m.name || ''}</td>
                <td>${m.spec || ''}</td>
                <td>${m.unit || ''}</td>
                <td>${m.amount || ''}</td>
              </tr>`).join('')}
          </table>` : ''
        const toolTable = (d.toolList && d.toolList.length) ? `
          <h4>工器具</h4>
          <table border="1" cellspacing="0" cellpadding="6" style="width:100%;border-collapse:collapse;">
            <tr><th>编码</th><th>名称</th><th>规格</th><th>单位</th><th>数量</th></tr>
            ${d.toolList.map(t => `
              <tr>
                <td>${t.code || ''}</td>
                <td>${t.name || ''}</td>
                <td>${t.spec || ''}</td>
                <td>${t.unit || ''}</td>
                <td>${t.amount || ''}</td>
              </tr>`).join('')}
          </table>` : ''
        return `
          <h3>步骤${d.stepNum || (idx + 1)}：${d.stepTitle || ''}</h3>
          ${stepContent}
          ${materialTable}
          ${toolTable}
        `
      }).join('')
      return autoHeader + body
    }
  }
}
</script>

<style scoped>
.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  position: sticky;
  top: 0;
  background: #fff;
  z-index: 2;
  padding: 8px 0;
}
.tips {
  color: #888;
}
.editor-body {
  height: calc(100% - 48px);
  overflow: auto;
}
</style>
