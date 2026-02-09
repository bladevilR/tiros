<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="handleSearch">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="名称/编码">
              <a-input placeholder="请输入名称或编码" v-model="queryParam.formName" allowClear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="线路">
              <a-input placeholder="请输入线路" v-model="queryParam.lineId" allowClear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="类型">
              <a-select v-model="queryParam.templateFlag" allowClear placeholder="全部">
                <a-select-option :value="1">模板</a-select-option>
                <a-select-option :value="0">正式</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd">新建指导书</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handleEdit(selectRows[0])">编辑基础信息</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handleEditContent(selectRows[0])">编辑正文</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handleSaveAsTemplate">保存为模板</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handleReviseVersion(selectRows[0])">修订升版</a-button>
                <a-button :disabled="selectRows.length < 1" @click="handleDelete">删除</a-button>
                <a-button @click="handleSearch">查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div style="height: calc(100vh - 225px)">
      <vxe-table
        border
        auto-resize
        ref="listTable"
        max-height="100%"
        style="height: calc(100vh - 225px)"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :checkbox-config="{trigger: 'row', highlight: true, range: true}"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="fileNo" title="文件编号" width="12%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="fileName" title="文件名称" width="18%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="fileVer" title="版本" width="8%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="lineName" title="线路" width="10%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="repairProgramName" title="修程" width="10%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="status" title="发布" width="7%" header-align="center" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.status === 1">发布</span>
            <span v-else>草稿</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="reviewStatus" title="审阅" width="8%" header-align="center" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.reviewStatus === 1">待审</span>
            <span v-else-if="scope.row.reviewStatus === 2">通过</span>
            <span v-else-if="scope.row.reviewStatus === 3">驳回</span>
            <span v-else>草稿</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="reviewerName" title="审阅人" width="8%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="templateFlag" title="类型" width="8%" header-align="center" align="center">
          <template slot-scope="scope">
            <a-tag :color="scope.row.templateFlag === 1 ? 'blue' : 'green'">
              {{ scope.row.templateFlag === 1 ? '模板' : '正式' }}
            </a-tag>
          </template>
        </vxe-table-column>
        <vxe-table-column field="updateTime" title="修改日期" width="10%" :formatter="formatDate"></vxe-table-column>
        <vxe-table-column title="操作" width="29%" header-align="center" align="center">
          <template slot-scope="{ row }">
            <a-space>
              <a @click="handleEditContent(row)">编辑正文</a>
              <a @click="handleExport(row)">导出</a>
              <a @click="handleSubmitReview(row)">提交审阅</a>
              <a @click="handleReviewDecision(row)">审阅</a>
              <a @click="handlePublish(row)">更新指导书</a>
              <a @click="handleReviseVersion(row)">修订升版</a>
            </a-space>
          </template>
        </vxe-table-column>
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>

    <techmanual-detail-modal ref="techmanualModal" @ok="loadData()"></techmanual-detail-modal>
    <tech-book-editor-modal ref="editorModal" @saved="loadData"></tech-book-editor-modal>
    <tech-book-review-modal ref="reviewModal" @ok="loadData"></tech-book-review-modal>
    <user-list ref="reviewerSelect" :multiple="false" @ok="onReviewerSelected"></user-list>
  </div>
</template>

<script>
import moment from 'moment'
import 'moment/locale/zh-cn'
import TechmanualDetailModal from './modules/techmanual/TechmanualDetailModal'
import TechBookEditorModal from './modules/techmanual/TechBookEditorModal'
import TechBookReviewModal from './modules/techmanual/TechBookReviewModal'
import UserList from '@views/tiros/common/selectModules/UserList'
import { getSopPage, delSopRecord, saveSopAsTemplate, reviseSopVersion, updateSopStatus, submitSopReview, getSopDetailPage, getSopDetailRecord, saveSopContent } from '@/api/tirosApi'

export default {
  components: { TechmanualDetailModal, TechBookEditorModal, TechBookReviewModal, UserList },
  data () {
    return {
      selectRows: [],
      pendingReviewRecord: null,
      queryParam: {
        formName: '',
        lineId: '',
        templateFlag: undefined,
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      loading: false
    }
  },
  created () {
    this.loadData()
  },
  methods: {
    checkboxChange (e) {
      this.selectRows = e.records
    },
    loadData () {
      this.loading = true
      getSopPage(this.queryParam).then((res) => {
        if (res.success) {
          this.tableData = res.result.records || []
          this.totalResult = res.result.total || 0
        }
      }).finally(() => {
        this.loading = false
      })
    },
    handleSearch () {
      this.queryParam.pageNo = 1
      this.loadData()
    },
    handleQueryChange () {
      this.handleSearch()
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.loadData()
    },
    handleAdd () {
      this.$refs.techmanualModal.add()
    },
    handleEdit (record) {
      this.$refs.techmanualModal.edit(record)
    },
    handleEditContent (record) {
      this.$refs.editorModal.open(record)
    },
    handleDelete () {
      if (this.selectRows.length === 0) {
        this.$message.warning('请先选择要删除的记录')
        return
      }
      this.$confirm({
        title: '删除确认',
        content: '确认删除选中的工艺电子手册吗？',
        onOk: () => {
          const ids = this.selectRows.map(row => row.id).join(',')
          delSopRecord({ ids }).then((res) => {
            if (res.success) {
              this.$message.success('删除成功')
              this.selectRows = []
              this.loadData()
            } else {
              this.$message.error(res.message || '删除失败')
            }
          })
        }
      })
    },
    handleSaveAsTemplate () {
      if (this.selectRows.length !== 1) {
        this.$message.warning('请选择一条指导书')
        return
      }
      const id = this.selectRows[0].id
      saveSopAsTemplate({ id }).then(res => {
        if (res.success) {
          this.$message.success('已另存为模板')
          this.loadData()
        } else {
          this.$message.error(res.message || '操作失败')
        }
      })
    },
    handleReviseVersion (record) {
      if (!record || !record.id) {
        this.$message.warning('请选择指导书')
        return
      }
      const currentVer = String(record.fileVer || '').trim()
      this.$confirm({
        title: '修订升版',
        content: `将版本【${currentVer || '-'}】升版并生成新草稿，是否继续？`,
        onOk: () => {
          const newVersion = this.buildNextVersion(currentVer)
          if (!newVersion) {
            this.$message.warning('无法自动计算新版本号，请先补充当前版本号')
            return
          }
          reviseSopVersion({ id: record.id, newVersion }).then(res => {
            if (res.success) {
              this.$message.success(`修订升版成功，新版本：${newVersion}`)
              this.loadData()
            } else {
              this.$message.error(res.message || '修订升版失败')
            }
          })
        }
      })
    },
    buildNextVersion (version) {
      const ver = String(version || '').trim()
      if (!ver) {
        return 'V2.0'
      }
      const vMatch = ver.match(/^([Vv])(\d+)(?:\.(\d+))?$/)
      if (vMatch) {
        const major = Number(vMatch[2] || 0)
        return `V${major + 1}.0`
      }
      const numberMatch = ver.match(/(\d+)(?!.*\d)/)
      if (numberMatch) {
        const oldNum = numberMatch[1]
        const nextNum = String(Number(oldNum) + 1)
        return ver.replace(new RegExp(`${oldNum}(?!.*\\d)`), nextNum)
      }
      return ''
    },
    handleSubmitReview (record) {
      if (record.reviewStatus === 1) {
        this.$message.warning('该指导书已提交审阅，请勿重复提交')
        return
      }
      if (record.status === 1) {
        this.$message.warning('已发布指导书无需再提交审阅')
        return
      }
      this.pendingReviewRecord = record
      this.$refs.reviewerSelect.showModal()
    },
    onReviewerSelected (users) {
      if (!this.pendingReviewRecord) {
        return
      }
      if (!users || users.length === 0) {
        this.$message.warning('请选择审阅人')
        return
      }
      const reviewer = users[0]
      submitSopReview({
        id: this.pendingReviewRecord.id,
        reviewerId: reviewer.id,
        reviewerName: reviewer.realname || reviewer.username
      }).then(res => {
        if (res.success) {
          this.$message.success('已提交审阅')
          this.loadData()
        } else {
          this.$message.error(res.message || '提交失败')
        }
      }).finally(() => {
        this.pendingReviewRecord = null
      })
    },
    handleReviewDecision (record) {
      if (record.reviewStatus !== 1) {
        this.$message.warning('仅待审状态可进行审阅')
        return
      }
      this.$refs.reviewModal.open(record)
    },
    handlePublish (record) {
      if (record.reviewStatus !== 2) {
        this.$message.warning('请先通过审阅再更新指导书')
        return
      }
      updateSopStatus({ id: record.id, status: 1 }).then(res => {
        if (res.success) {
          this.$message.success('已更新并发布')
          this.loadData()
        }
      })
    },
    async handleExport (record) {
      const content = await this.ensureContentHtml(record)
      const html = `<html><head><meta charset="utf-8"></head><body>${content}</body></html>`
      const blob = new Blob(['\ufeff', html], { type: 'application/msword' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `${record.fileName || '作业指导书'}.docx`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    },
    async ensureContentHtml (record) {
      if (record.contentHtml) {
        return record.contentHtml
      }
      try {
        const listRes = await getSopDetailPage({ bookId: record.id })
        const detailList = (listRes && listRes.result) ? listRes.result : []
        const detailPromises = detailList.map(item => getSopDetailRecord({ detailId: item.id }))
        const detailResults = await Promise.all(detailPromises)
        const fullDetails = detailResults
          .filter(r => r && r.success)
          .map(r => r.result)
          .sort((a, b) => (a.stepNum || 0) - (b.stepNum || 0))
        const html = this.$refs.editorModal.renderTemplate(record, fullDetails)
        await saveSopContent(record.id, html)
        record.contentHtml = html
        return html
      } catch (e) {
        this.$message.warning('生成模板正文失败，请先编辑正文')
        return '<p>暂无正文</p>'
      }
    },
    formatDate (row) {
      if (row.createTime) {
        return moment(row.createTime).format('YYYY-MM-DD HH:mm:ss')
      }
      return ''
    }
  }
}
</script>

<style scoped>
.table-page-search-wrapper {
  padding: 12px;
  background-color: #f5f5f5;
  border-radius: 2px;
  margin-bottom: 12px;
}

.table-page-search-submitButtons {
  margin-left: 8px;
}
</style>
