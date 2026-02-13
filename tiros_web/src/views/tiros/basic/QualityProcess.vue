<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="handleSearch">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="项目名称">
              <a-input placeholder="请输入项目名称" v-model="queryParam.projectName" allowClear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="车型">
              <a-select
                placeholder="请选择车型"
                v-model="queryParam.trainType"
                allowClear
              >
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="电客车">电客车</a-select-option>
                <a-select-option value="电动车组">电动车组</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <template v-if="advanced">
            <a-col :md="6" :sm="24">
              <a-form-item label="车号">
                <a-input placeholder="请输入车号" v-model="queryParam.trainNo" allowClear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="计划ID">
                <a-input placeholder="请输入计划ID" v-model="queryParam.planId" allowClear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="质量等级">
                <a-select v-model="queryParam.qualityLevel" placeholder="请选择质量等级" allowClear>
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="A">A</a-select-option>
                  <a-select-option value="B">B</a-select-option>
                  <a-select-option value="C">C</a-select-option>
                  <a-select-option value="D">D</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </template>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handleEdit(selectRows[0])">编辑</a-button>
                <a-button :disabled="selectRows.length < 1" @click="handleDelete">删除</a-button>
                <a-button @click="handleBatchRefreshByPlan">按计划批量刷新</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handleViewProcessSteps">工序明细</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handleExtractPlanning">质量策划抽取</a-button>
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
        <vxe-table-column field="projectName" title="项目名称" width="20%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="trainType" title="车型" width="12%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="trainNo" title="车号" width="12%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="qualityLevel" title="质量等级" width="12%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="progress" title="进度" width="12%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="createTime" title="创建日期" width="18%" :formatter="formatDate"></vxe-table-column>
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

    <quality-visual-modal ref="qualityModal" @ok="loadData()"></quality-visual-modal>

    <a-drawer
      title="工序进度与质量可视化明细"
      placement="right"
      :width="1100"
      :visible="processDrawerVisible"
      :maskClosable="false"
      @close="processDrawerVisible = false"
    >
      <a-spin :spinning="processStepsLoading">
        <vxe-table
          border
          auto-resize
          max-height="calc(100vh - 170px)"
          :align="allAlign"
          :data="processSteps"
          show-overflow="tooltip"
        >
          <vxe-table-column field="orderCode" title="工单号" width="120"></vxe-table-column>
          <vxe-table-column field="orderName" title="工单名称" width="180" align="left"></vxe-table-column>
          <vxe-table-column field="taskNo" title="序号" width="70"></vxe-table-column>
          <vxe-table-column field="taskName" title="工序/工步" width="210" align="left"></vxe-table-column>
          <vxe-table-column field="checkedPoints" title="已检查点" width="100"></vxe-table-column>
          <vxe-table-column field="totalPoints" title="总检查点" width="100"></vxe-table-column>
          <vxe-table-column field="progressPercent" title="完成度" width="100">
            <template v-slot="{ row }">
              <a-progress :percent="row.progressPercent || 0" size="small"></a-progress>
            </template>
          </vxe-table-column>
          <vxe-table-column field="qualityIssueCount" title="故障数" width="90"></vxe-table-column>
          <vxe-table-column field="openItemCount" title="开口项" width="90"></vxe-table-column>
          <vxe-table-column field="qualityLevel" title="质量等级" width="90">
            <template v-slot="{ row }">
              <a-tag :color="row.qualityLevel === 'A' ? 'green' : row.qualityLevel === 'B' ? 'blue' : row.qualityLevel === 'C' ? 'orange' : 'red'">
                {{ row.qualityLevel || '-' }}
              </a-tag>
            </template>
          </vxe-table-column>
          <vxe-table-column field="statusColor" title="状态提示" width="100">
            <template v-slot="{ row }">
              <a-badge :status="row.statusColor === 'green' ? 'success' : row.statusColor === 'red' ? 'error' : 'warning'"
                       :text="row.statusColor === 'green' ? '正常' : row.statusColor === 'red' ? '风险' : '进行中'" />
            </template>
          </vxe-table-column>
          <vxe-table-column field="startTime" title="开始" width="110" :formatter="formatDateYmd"></vxe-table-column>
          <vxe-table-column field="finishTime" title="结束" width="110" :formatter="formatDateYmd"></vxe-table-column>
        </vxe-table>
      </a-spin>
    </a-drawer>

    <a-modal
      title="质量策划抽取结果"
      :visible="planningVisible"
      :width="1200"
      :footer="null"
      :maskClosable="false"
      @cancel="planningVisible = false"
    >
      <a-spin :spinning="planningLoading">
        <a-row :gutter="16" style="margin-bottom: 12px">
          <a-col :span="6"><a-statistic title="总项点" :value="planningSummary.totalItems || 0" /></a-col>
          <a-col :span="6"><a-statistic title="无需填报" :value="planningSummary.noNeedFillItems || 0" /></a-col>
          <a-col :span="6"><a-statistic title="已填报" :value="planningSummary.filledItems || 0" /></a-col>
          <a-col :span="6"><a-statistic title="待填报" :value="planningSummary.pendingItems || 0" /></a-col>
        </a-row>
        <vxe-table
          border
          auto-resize
          max-height="520"
          :align="allAlign"
          :data="planningSummary.items || []"
          show-overflow="tooltip"
        >
          <vxe-table-column field="orderCode" title="工单号" width="120"></vxe-table-column>
          <vxe-table-column field="taskName" title="工序/工步" width="200" align="left"></vxe-table-column>
          <vxe-table-column field="workContent" title="作业内容" width="280" align="left"></vxe-table-column>
          <vxe-table-column field="techRequire" title="技术要求" width="260" align="left"></vxe-table-column>
          <vxe-table-column field="checkLevelText" title="检查级别" width="100"></vxe-table-column>
          <vxe-table-column field="noNeedFill" title="无需填报" width="90">
            <template v-slot="{ row }">
              <a-tag :color="row.noNeedFill === 1 ? 'default' : 'green'">{{ row.noNeedFill === 1 ? '是' : '否' }}</a-tag>
            </template>
          </vxe-table-column>
          <vxe-table-column field="filled" title="填报状态" width="100">
            <template v-slot="{ row }">
              <a-tag :color="row.filled === 1 ? 'green' : 'orange'">{{ row.filled === 1 ? '已填报' : '待填报' }}</a-tag>
            </template>
          </vxe-table-column>
        </vxe-table>
      </a-spin>
    </a-modal>
  </div>
</template>

<script>
import moment from 'moment'
import 'moment/locale/zh-cn'
import QualityVisualModal from './modules/qualityvisual/QualityVisualModal'
import {
  pageQualityVisual,
  deleteQualityVisual,
  refreshQualityVisualByPlan,
  listQualityVisualProcessSteps,
  extractQualityPlanning
} from '@/api/tirosApi'
import { downFile } from '@/api/manage'
import Vue from 'vue'
import { ACCESS_TOKEN } from '@/store/mutation-types'

export default {
  name: 'QualityProcess',
  components: { QualityVisualModal },
  computed: {
    importExcelUrl() {
      return `${window._CONFIG['domianURL']}/base/quality-visual/importExcel`
    }
  },
  data () {
    return {
      selectRows: [],
      advanced: false,
      tokenHeader: { 'X-Access-Token': Vue.ls.get(ACCESS_TOKEN) },
      queryParam: {
        projectName: '',
        trainType: '',
        trainNo: '',
        planId: '',
        qualityLevel: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      loading: false,
      processDrawerVisible: false,
      processStepsLoading: false,
      processSteps: [],
      planningVisible: false,
      planningLoading: false,
      planningSummary: {}
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
      pageQualityVisual(this.queryParam).then((res) => {
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
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.loadData()
    },
    handleAdd () {
      this.$refs.qualityModal.add()
    },
    handleEdit (record) {
      this.$refs.qualityModal.edit(record)
    },
    handleDelete () {
      if (this.selectRows.length === 0) {
        this.$message.warning('请先选择要删除的记录')
        return
      }
      this.$confirm({
        title: '删除确认',
        content: '确认删除选中的质量可视化记录吗？',
        onOk: () => {
          const ids = this.selectRows.map(row => row.id).join(',')
          deleteQualityVisual({ ids }).then((res) => {
            if (res.success) {
              this.$message.success('删除成功')
              this.selectRows = []
              this.loadData()
            }
          })
        }
      })
    },
    handleBatchRefreshByPlan () {
      const planId = this.queryParam.planId
      if (!planId) {
        this.$message.warning('请先在查询条件输入计划ID')
        return
      }
      this.$confirm({
        title: '批量刷新确认',
        content: `确认按计划【${planId}】批量刷新质量可视化数据吗？`,
        onOk: () => {
          refreshQualityVisualByPlan({ planId }).then((res) => {
            if (res.success) {
              const count = (res.result || []).length
              this.$message.success(`批量刷新完成，共处理 ${count} 列车`)
              this.loadData()
            } else {
              this.$message.error(res.message || '批量刷新失败')
            }
          })
        }
      })
    },
    handleViewProcessSteps () {
      const row = this.selectRows[0]
      if (!row) {
        this.$message.warning('请先选择一条记录')
        return
      }
      if (!row.planId) {
        this.$message.warning('所选记录缺少计划ID')
        return
      }
      this.processDrawerVisible = true
      this.processStepsLoading = true
      listQualityVisualProcessSteps({ planId: row.planId, trainNo: row.trainNo }).then((res) => {
        if (res.success) {
          this.processSteps = res.result || []
        } else {
          this.$message.error(res.message || '加载工序明细失败')
        }
      }).finally(() => {
        this.processStepsLoading = false
      })
    },
    handleExtractPlanning () {
      const row = this.selectRows[0]
      if (!row) {
        this.$message.warning('请先选择一条记录')
        return
      }
      if (!row.planId || !row.trainNo) {
        this.$message.warning('所选记录缺少计划ID或车号')
        return
      }
      this.planningVisible = true
      this.planningLoading = true
      extractQualityPlanning({ planId: row.planId, trainNo: row.trainNo, excludeNoNeed: true }).then((res) => {
        if (res.success) {
          this.planningSummary = res.result || {}
        } else {
          this.$message.error(res.message || '抽取质量策划失败')
        }
      }).finally(() => {
        this.planningLoading = false
      })
    },
    handleExport() {
      const params = { ...this.queryParam }
      downFile('/base/quality-visual/exportXls', params).then((data) => {
        if (!data) {
          this.$message.warning('文件下载失败')
          return
        }
        const blob = new Blob([data], { type: 'application/vnd.ms-excel' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.style.display = 'none'
        link.href = url
        link.setAttribute('download', '质量可视化.xls')
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      })
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
    formatDate (row) {
      if (row.createTime) {
        return moment(row.createTime).format('YYYY-MM-DD HH:mm:ss')
      }
      return ''
    },
    formatDateYmd (row, col, value) {
      if (value) {
        return moment(value).format('YYYY-MM-DD')
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
</style>
