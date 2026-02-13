<template>
  <a-modal
    v-model="visible"
    title="编辑作业指导书正文"
    :width="'96%'"
    :maskClosable="false"
    :footer="null"
    :bodyStyle="modalBodyStyle"
    :dialogStyle="{ top: '10px' }"
    wrapClassName="jgb-editor-modal"
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
            <a-menu-item key="consumables">消耗料</a-menu-item>
            <a-menu-item key="materialDetail">物料明细</a-menu-item>
            <a-menu-item key="preWork">作业前准备</a-menu-item>
            <a-menu-item key="processStep">工序工步</a-menu-item>
            <a-menu-item key="tools">工具工装</a-menu-item>
            <a-menu-item key="torqueList">扭力值清单</a-menu-item>
            <a-menu-item key="appendix">附录</a-menu-item>
          </a-menu>
        </a-dropdown>
        <a-button @click="handleExport">导出DOCX</a-button>
        <a-button @click="handleClose">关闭</a-button>
      </a-space>
      <div class="tips">
        当前：{{ record.fileName || '-' }}（版本：{{ record.fileVer || '-' }}）
      </div>
    </div>
    <div class="editor-body">
      <j-editor ref="jeditor" v-model="contentHtml" :height="editorHeight"></j-editor>
    </div>
  </a-modal>
</template>

<script>
import JEditor from '@/components/jeecg/JEditor'
import { saveJgbContent } from '@/api/tirosApi'
import * as tpl from './jgbTemplates'

export default {
  name: 'JobGuideBookEditorModal',
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
      return { height: 'calc(100vh - 120px)', overflow: 'hidden' }
    }
  },
  methods: {
    open (record) {
      this.record = record || {}
      this.contentHtml = tpl.resolveTemplateTokens((record && record.contentHtml) || '', this.record)
      this.visible = true
      this.setEditorHeight()
      window.addEventListener('resize', this.setEditorHeight)
    },
    handleInsertTemplate ({ key }) {
      const templateMap = {
        cover: tpl.coverTemplate,
        changeRecord: tpl.changeRecordTemplate,
        processFlow: tpl.processFlowTemplate,
        consumables: tpl.consumablesTemplate,
        materialDetail: tpl.materialDetailTemplate,
        preWork: tpl.preWorkTemplate,
        processStep: tpl.processStepTemplate,
        tools: tpl.toolsTemplate,
        torqueList: tpl.torqueListTemplate,
        appendix: tpl.appendixTemplate
      }
      const fn = templateMap[key]
      if (fn) {
        const html = tpl.resolveTemplateTokens(fn(this.record), this.record)
        this.contentHtml = (this.contentHtml || '') + html
      }
    },
    handleSave () {
      if (!this.record.id) {
        this.$message.warning('未选择作业指导书')
        return
      }
      const canEdit = this.record.status === 2 || (this.record.status === 0 && this.record.reviewStatus !== 2)
      if (!canEdit) {
        this.$message.warning('仅草稿、审核中、审批中状态可编辑保存')
        return
      }
      this.loading = true
      saveJgbContent(this.record.id, this.contentHtml).then(res => {
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
.tips { color: #888; }
.editor-body { height: calc(100% - 48px); overflow: auto; }
</style>
