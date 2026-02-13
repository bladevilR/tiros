<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="handleSearch">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="通知单号">
              <a-input placeholder="请输入通知单号" v-model="queryParam.noticeNo" allowClear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="状态">
              <a-select
                placeholder="请选择状态"
                v-model="queryParam.status"
                allowClear
                @change="handleQueryChange"
              >
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="0">草稿</a-select-option>
                <a-select-option value="1">审核中</a-select-option>
                <a-select-option value="2">已发布</a-select-option>
                <a-select-option value="3">已关闭</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <template v-if="advanced">
            <a-col :md="6" :sm="24">
              <a-form-item label="标题">
                <a-input placeholder="请输入标题" v-model="queryParam.title" allowClear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="类型">
                <a-select
                  placeholder="请选择类型"
                  v-model="queryParam.noticeType"
                  allowClear
                >
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="1">技术类</a-select-option>
                  <a-select-option value="2">变更类</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="车型">
                <a-input placeholder="请输入车型" v-model="queryParam.trainType" allowClear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="线别">
                <a-input placeholder="请输入线别" v-model="queryParam.line" allowClear></a-input>
              </a-form-item>
            </a-col>
          </template>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handleEdit(selectRows[0])">编辑</a-button>
                <a-button :disabled="selectRows.length < 1" @click="handleDelete">删除</a-button>
                <a-button :disabled="!canSubmit" @click="handleSubmit">提交审核</a-button>
                <a-button :disabled="!canPublish" @click="handlePublish">发布</a-button>
                <a-button :disabled="!canClose" @click="handleClose">关闭</a-button>
                <a-button :disabled="selectRows.length != 1" @click="loadProgressDetail">进度明细</a-button>
                <a-button :disabled="selectRows.length != 1" @click="loadFormProgressDetail">表单填报明细</a-button>
                <a-button :disabled="selectRows.length != 1" @click="loadRelatedFiles">关联文件</a-button>
                <a-button @click="handleExport">导出</a-button>
                <a-upload
                  name="file"
                  :showUploadList="false"
                  :multiple="false"
                  :headers="tokenHeader"
                  :action="importExcelUrl"
                  @change="handleImportExcel"
                >
                  <a-button>导入</a-button>
                </a-upload>
                <a-button @click="handleSearch">查询</a-button>
                <a @click="advanced = !advanced">
                  {{ advanced ? '收起' : '展开' }}
                  <a-icon :type="advanced ? 'up' : 'down'" />
                </a>
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
        <vxe-table-column field="noticeNo" title="通知单号" width="15%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="title" title="通知单标题" width="25%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="status" title="状态" width="12%" header-align="center" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.status === '0'">草稿</span>
            <span v-else-if="scope.row.status === '1'">审核中</span>
            <span v-else-if="scope.row.status === '2'">已发布</span>
            <span v-else-if="scope.row.status === '3'">已关闭</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="progress" title="进度" width="16%" header-align="center" align="center">
          <template slot-scope="scope">
            {{ formatProgress(scope.row) }}
          </template>
        </vxe-table-column>
        <vxe-table-column field="createTime" title="创建时间" width="18%" :formatter="formatDate"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" width="15%" header-align="center" align="left"></vxe-table-column>
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

    <production-notice-modal ref="noticeModal" @ok="loadData()"></production-notice-modal>

    <a-modal
      title="生产通知执行进度明细"
      :visible="progressVisible"
      :footer="null"
      :width="860"
      @cancel="progressVisible = false"
      :maskClosable="false"
    >
      <vxe-table
        border
        auto-resize
        max-height="420"
        :align="allAlign"
        :data="progressDetails"
      >
        <vxe-table-column field="trainNo" title="车号" width="180"></vxe-table-column>
        <vxe-table-column field="totalOrders" title="关联工单数" width="120"></vxe-table-column>
        <vxe-table-column field="completedOrders" title="已完成工单数" width="120"></vxe-table-column>
        <vxe-table-column field="progressRatio" title="完成比" width="120"></vxe-table-column>
        <vxe-table-column field="progressText" title="进度百分比" width="120"></vxe-table-column>
        <vxe-table-column field="lastFinishTime" title="最近完成时间" min-width="220" :formatter="formatDate"></vxe-table-column>
      </vxe-table>
    </a-modal>

    <a-modal
      title="生产通知作业记录表填报明细"
      :visible="formProgressVisible"
      :footer="null"
      :width="1100"
      @cancel="formProgressVisible = false"
      :maskClosable="false"
    >
      <vxe-table
        border
        auto-resize
        max-height="420"
        :align="allAlign"
        :data="formProgressDetails"
      >
        <vxe-table-column field="trainNo" title="车号" width="120"></vxe-table-column>
        <vxe-table-column field="orderCode" title="工单号" width="150"></vxe-table-column>
        <vxe-table-column field="formCode" title="记录表编码" width="150"></vxe-table-column>
        <vxe-table-column field="formTitle" title="记录表名称" min-width="220"></vxe-table-column>
        <vxe-table-column field="fillStatusText" title="填报状态" width="100"></vxe-table-column>
        <vxe-table-column field="checkResultText" title="检查结果" width="100"></vxe-table-column>
        <vxe-table-column field="checkDate" title="检查日期" width="130"></vxe-table-column>
      </vxe-table>
    </a-modal>

    <a-modal
      title="生产通知关联文件"
      :visible="relatedFilesVisible"
      :footer="null"
      :width="900"
      @cancel="relatedFilesVisible = false"
      :maskClosable="false"
    >
      <vxe-table border auto-resize max-height="420" :align="allAlign" :data="relatedFiles">
        <vxe-table-column field="name" title="文件名称" min-width="280"></vxe-table-column>
        <vxe-table-column field="type" title="类型" width="120"></vxe-table-column>
        <vxe-table-column field="fileSize" title="大小(KB)" width="100"></vxe-table-column>
        <vxe-table-column title="操作" width="180">
          <template slot-scope="scope">
            <a @click="previewFile(scope.row)">预览</a>
            <a-divider type="vertical" />
            <a @click="downloadRelatedFile(scope.row)">下载</a>
          </template>
        </vxe-table-column>
      </vxe-table>
    </a-modal>

    <doc-preview-modal ref="docPreview"></doc-preview-modal>
  </div>
</template>

<script>
import moment from 'moment'
import 'moment/locale/zh-cn'
import ProductionNoticeModal from './modules/productionnotice/ProductionNoticeModal'
import {
  pageProductionNotice,
  deleteProductionNotice,
  submitProductionNotice,
  publishProductionNotice,
  closeProductionNotice,
  listProductionNoticeProgressDetail,
  listProductionNoticeFormProgress,
  getProductionNoticeRelationPayload
} from '@/api/tirosApi'
import { downFile } from '@/api/manage'
import Vue from 'vue'
import { ACCESS_TOKEN } from '@/store/mutation-types'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import { download } from '@/api/tirosFileApi'

export default {
  name: 'ProductionNotice',
  components: { ProductionNoticeModal, DocPreviewModal },
  computed: {
    canSubmit() {
      return this.selectRows.length === 1 && this.selectRows[0].status === '0'
    },
    canPublish() {
      return this.selectRows.length === 1 && this.selectRows[0].status === '1'
    },
    canClose() {
      return this.selectRows.length === 1 && this.selectRows[0].status === '2'
    },
    importExcelUrl() {
      return `${window._CONFIG['domianURL']}/base/production-notice/importExcel`
    }
  },
  data() {
    return {
      selectRows: [],
      advanced: false,
      tokenHeader: { 'X-Access-Token': Vue.ls.get(ACCESS_TOKEN) },
      queryParam: {
        noticeNo: '',
        status: '',
        title: '',
        noticeType: '',
        trainType: '',
        line: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      loading: false,
      progressVisible: false,
      progressDetails: [],
      formProgressVisible: false,
      formProgressDetails: [],
      relatedFilesVisible: false,
      relatedFiles: []
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    checkboxChange(e) {
      this.selectRows = e.records
    },
    loadData() {
      this.loading = true
      pageProductionNotice(this.queryParam).then(res => {
        if (res.success) {
          this.tableData = (res.result.records || []).map(item => ({
            ...item,
            progressRatio: this.buildProgressRatio(item)
          }))
          this.totalResult = res.result.total || 0
        }
      }).finally(() => {
        this.loading = false
      })
    },
    handleSearch() {
      this.queryParam.pageNo = 1
      this.loadData()
    },
    handleQueryChange() {
      this.handleSearch()
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.loadData()
    },
    handleAdd() {
      this.$refs.noticeModal.add()
    },
    handleEdit(record) {
      this.$refs.noticeModal.edit(record)
    },
    handleDelete() {
      if (this.selectRows.length === 0) {
        this.$message.warning('请先选择要删除的记录')
        return
      }
      this.$confirm({
        title: '删除确认',
        content: '确认删除选中的生产通知单吗？',
        onOk: () => {
          const ids = this.selectRows.map(row => row.id).join(',')
          deleteProductionNotice({ ids }).then(res => {
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
    handleExport() {
      const params = { ...this.queryParam }
      downFile('/base/production-notice/exportXls', params).then((data) => {
        if (!data) {
          this.$message.warning('文件下载失败')
          return
        }
        const blob = new Blob([data], { type: 'application/vnd.ms-excel' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.style.display = 'none'
        link.href = url
        link.setAttribute('download', '生产通知单.xls')
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      })
    },
    handleSubmit() {
      if (!this.canSubmit) {
        return
      }
      const row = this.selectRows[0]
      this.$confirm({
        title: '提交审核确认',
        content: `确认提交通知单【${row.noticeNo}】进入审核流程吗？`,
        onOk: () => {
          submitProductionNotice({ id: row.id }).then(res => {
            if (res.success) {
              this.$message.success('提交审核成功')
              this.loadData()
            } else {
              this.$message.error(res.message || '提交失败')
            }
          })
        }
      })
    },
    handlePublish() {
      if (!this.canPublish) {
        return
      }
      const row = this.selectRows[0]
      this.$confirm({
        title: '发布确认',
        content: `确认发布通知单【${row.noticeNo}】吗？`,
        onOk: () => {
          publishProductionNotice({ id: row.id }).then(res => {
            if (res.success) {
              this.$message.success('发布成功')
              this.loadData()
            } else {
              this.$message.error(res.message || '发布失败')
            }
          })
        }
      })
    },
    handleClose() {
      if (!this.canClose) {
        return
      }
      const row = this.selectRows[0]
      this.$confirm({
        title: '关闭确认',
        content: `确认关闭通知单【${row.noticeNo}】吗？关闭前会校验执行进度是否100%。`,
        onOk: () => {
          closeProductionNotice({ id: row.id }).then(res => {
            if (res.success) {
              this.$message.success('关闭成功')
              this.loadData()
            } else {
              this.$message.error(res.message || '关闭失败')
            }
          })
        }
      })
    },
    loadProgressDetail() {
      if (this.selectRows.length !== 1) {
        return
      }
      const row = this.selectRows[0]
      listProductionNoticeProgressDetail({ id: row.id }).then(res => {
        if (res.success) {
          this.progressDetails = (res.result || []).map(item => {
            const totalOrders = Number(item.totalOrders || 0)
            const completedOrders = Number(item.completedOrders || 0)
            const progressText = totalOrders > 0 ? `${Math.round((completedOrders * 100) / totalOrders)}%` : '0%'
            const progressRatio = totalOrders > 0 ? `${completedOrders}/${totalOrders}` : '0/0'
            return {
              ...item,
              progressRatio,
              progressText
            }
          })
          this.progressVisible = true
        } else {
          this.$message.error(res.message || '加载进度明细失败')
        }
      })
    },
    loadFormProgressDetail() {
      if (this.selectRows.length !== 1) {
        return
      }
      const row = this.selectRows[0]
      listProductionNoticeFormProgress({ id: row.id }).then(res => {
        if (res.success) {
          this.formProgressDetails = (res.result || []).map(item => ({
            ...item,
            fillStatusText: Number(item.fillStatus) === 1 ? '已填写' : '未填写',
            checkResultText: Number(item.checkResult) === 1 ? '通过' : (Number(item.checkResult) === 0 ? '未通过' : '-')
          }))
          this.formProgressVisible = true
        } else {
          this.$message.error(res.message || '加载填报明细失败')
        }
      })
    },
    loadRelatedFiles() {
      if (this.selectRows.length !== 1) {
        return
      }
      const row = this.selectRows[0]
      getProductionNoticeRelationPayload({ id: row.id }).then(res => {
        if (res && res.success) {
          const payload = res.result || {}
          this.relatedFiles = payload.relatedFiles || []
          this.relatedFilesVisible = true
        } else {
          this.$message.error((res && res.message) || '加载关联文件失败')
        }
      })
    },
    previewFile(file) {
      if (!file || !file.savepath) {
        this.$message.warning('文件路径为空')
        return
      }
      this.$refs.docPreview.handleFilePath(file.savepath)
    },
    downloadRelatedFile(file) {
      if (!file || !file.savepath) {
        this.$message.warning('文件路径为空')
        return
      }
      download(file.savepath)
    },
    handleImportExcel(info) {
      if (info.file.status === 'done') {
        if (info.file.response && info.file.response.success) {
          this.$message.success(info.file.response.message || `${info.file.name} 导入成功`)
          this.loadData()
        } else {
          this.$message.error(info.file.response ? info.file.response.message : '导入失败')
        }
      } else if (info.file.status === 'error') {
        this.$message.error('文件上传失败')
      }
    },
    formatDate(row) {
      const time = row.lastFinishTime || row.createTime
      if (time) {
        return moment(time).format('YYYY-MM-DD HH:mm:ss')
      }
      return ''
    },
    buildProgressRatio(row) {
      const total = Number((row && row.totalTrains) || 0)
      const completed = Number((row && row.completedTrains) || 0)
      if (total > 0) {
        return `${Math.min(completed, total)}/${total}`
      }
      if (completed > 0) {
        return `${completed}/${completed}`
      }
      return ''
    },
    formatProgress(row) {
      const ratio = this.buildProgressRatio(row)
      const percent = (row && row.progress) ? row.progress : '0%'
      if (!ratio) {
        return percent
      }
      return `${ratio} (${percent})`
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
