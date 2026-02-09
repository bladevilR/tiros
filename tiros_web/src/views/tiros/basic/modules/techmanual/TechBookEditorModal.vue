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
        <a-button @click="handleRebuild">重新生成模板</a-button>
        <a-button @click="handleExport">导出DOCX</a-button>
        <a-button @click="handleClose">关闭</a-button>
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
    async open (record) {
      this.record = record || {}
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
          await saveSopContent(this.record.id, this.contentHtml)
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
    handleExport () {
      const html = `<html><head><meta charset="utf-8"></head><body>${this.contentHtml || '<p>暂无正文</p>'}</body></html>`
      const blob = new Blob(['\ufeff', html], { type: 'application/msword' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `${this.record.fileName || '作业指导书'}.docx`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
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
      const header = `
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
        <h2>作业步骤</h2>
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
      return header + body
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
