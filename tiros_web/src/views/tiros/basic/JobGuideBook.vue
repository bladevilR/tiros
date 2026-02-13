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
            <a-form-item label="项目">
              <a-input placeholder="请输入项目" v-model="queryParam.project" allowClear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="编制人">
              <a-input placeholder="请输入编制人" v-model="queryParam.creatorName" allowClear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="状态">
              <a-select v-model="queryParam.bizStatus" allowClear placeholder="全部">
                <a-select-option value="DRAFT">草稿</a-select-option>
                <a-select-option value="REVIEWING">审核中</a-select-option>
                <a-select-option value="REVIEW_PASS">审核通过</a-select-option>
                <a-select-option value="APPROVING">审批中</a-select-option>
                <a-select-option value="APPROVED">审批通过</a-select-option>
                <a-select-option value="PUBLISHED">发布</a-select-option>
                <a-select-option value="OBSOLETE">作废</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="车型">
              <a-select v-model="queryParam.trainTypeId" allowClear showSearch optionFilterProp="children" placeholder="全部">
                <a-select-option v-for="item in trainTypeOptions" :key="item.value" :value="item.value">
                  {{ item.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="修程">
              <a-select v-model="queryParam.repairProId" allowClear showSearch optionFilterProp="children" placeholder="全部">
                <a-select-option v-for="item in repairProgramOptions" :key="item.value" :value="item.value">
                  {{ item.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="线路">
              <a-select v-model="queryParam.lineId" allowClear showSearch optionFilterProp="children" placeholder="全部">
                <a-select-option v-for="item in lineOptions" :key="item.value" :value="item.value">
                  {{ item.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="12" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd">新建</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handleEdit(selectRows[0])">编辑信息</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handleEditContent(selectRows[0])">编辑正文</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handleSaveAsTemplate">保存为模板</a-button>
                <a-button :disabled="selectRows.length < 1" @click="handleSubmitReview()">提交审核</a-button>
                <a-button :disabled="selectRows.length < 1" @click="handleReviewDecision()">审核</a-button>
                <a-button :disabled="selectRows.length < 1" @click="handleSubmitApprove()">提交审批</a-button>
                <a-button :disabled="selectRows.length < 1" @click="handleApproveDecision()">审批</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handlePublish(selectRows[0])">发布</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handleReviseVersion(selectRows[0])">修订升版</a-button>
                <a-button :disabled="selectRows.length < 1" @click="handleDelete">删除</a-button>
                <a-button :disabled="selectRows.length < 1" @click="handleBatchExport">导出PDF</a-button>
                <a-button @click="handleExportCatalog">导出目录</a-button>
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
        <vxe-table-column field="fileNo" title="文件编号" width="11%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="fileName" title="文件名称" width="14%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="fileVer" title="版本" width="5%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="lineName" title="线路" width="8%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="trainTypeName" title="车型" width="8%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="repairProgramName" title="修程" width="7%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="project" title="项目" width="8%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="creatorName" title="编制人" width="7%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="statusText" title="状态" width="7%" header-align="center" align="center">
          <template slot-scope="scope">
            <a-tag :color="statusColor(scope.row)">{{ formatBizStatus(scope.row) }}</a-tag>
          </template>
        </vxe-table-column>
        <vxe-table-column field="updateTime" title="修改日期" width="10%" :formatter="formatDate"></vxe-table-column>
        <vxe-table-column title="操作" width="15%" header-align="center" align="center">
          <template slot-scope="{ row }">
            <a-space>
              <a @click="handleEditContent(row)">查看</a>
              <a @click="handleExport(row)">导出</a>
              <a @click="handleReviewDecision(row)">审核</a>
              <a @click="handleReviseVersion(row)">修订</a>
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

    <job-guide-book-detail-modal ref="detailModal" @ok="loadData()"></job-guide-book-detail-modal>
    <job-guide-book-editor-modal ref="editorModal" @saved="loadData"></job-guide-book-editor-modal>
    <job-guide-book-review-modal ref="reviewModal" @ok="loadData"></job-guide-book-review-modal>
    <user-list ref="reviewerSelect" :multiple="false" @ok="onReviewerSelected"></user-list>
  </div>
</template>

<script>
import moment from 'moment'
import 'moment/locale/zh-cn'
import ExcelJS from 'exceljs'
import FileSaver from 'file-saver'
import JobGuideBookDetailModal from './modules/jobguidebook/JobGuideBookDetailModal'
import JobGuideBookEditorModal from './modules/jobguidebook/JobGuideBookEditorModal'
import JobGuideBookReviewModal from './modules/jobguidebook/JobGuideBookReviewModal'
import UserList from '@views/tiros/common/selectModules/UserList'
import { ajaxGetDictItems } from '@/api/api'
import {
  getJgbPage,
  delJgbRecord,
  saveJgbAsTemplate,
  reviseJgbVersion,
  updateJgbStatus,
  submitJgbReview,
  decisionJgbReview,
  submitJgbApprove,
  decisionJgbApprove,
  exportJgbPdf
} from '@/api/tirosApi'

export default {
  components: { JobGuideBookDetailModal, JobGuideBookEditorModal, JobGuideBookReviewModal, UserList },
  data () {
      return {
        selectRows: [],
        pendingReviewIds: '',
        pendingApproveIds: '',
        reviewerAction: 'review',
      lineOptions: [],
      repairProgramOptions: [],
      trainTypeOptions: [],
      queryParam: {
        formName: '',
        project: '',
        creatorName: '',
        lineId: undefined,
        bizStatus: undefined,
        trainTypeId: undefined,
        repairProId: undefined,
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
    this.loadDictOptions()
    this.loadData()
  },
  methods: {
    loadDictOptions () {
      ajaxGetDictItems('bu_mtr_line,line_name,line_id', null).then(res => {
        const list = (res && res.success) ? (res.result || []) : []
        this.lineOptions = list.map(item => ({ label: item.text, value: item.value }))
      })
      ajaxGetDictItems('bu_train_type,name,id', null).then(res => {
        const list = (res && res.success) ? (res.result || []) : []
        this.trainTypeOptions = list.map(item => ({ label: item.text, value: item.value }))
      })
      ajaxGetDictItems('bu_repair_program,name,id', null).then(res => {
        const list = (res && res.success) ? (res.result || []) : []
        this.repairProgramOptions = list.map(item => ({ label: item.text, value: item.value }))
      })
    },
    checkboxChange (e) {
      this.selectRows = e.records
    },
    loadData () {
      this.loading = true
      getJgbPage(this.queryParam).then((res) => {
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
      this.$refs.detailModal.add()
    },
    handleEdit (record) {
      if (!this.canEdit(record)) {
        this.$message.warning('仅草稿、审核中、审批中状态可编辑')
        return
      }
      this.$refs.detailModal.edit(record)
    },
    handleEditContent (record) {
      if (!record || !record.id) return
      this.$refs.editorModal.open(record)
    },
    handleDelete () {
      if (this.selectRows.length === 0) {
        this.$message.warning('请先选择要删除的记录')
        return
      }
      const blocked = this.selectRows.filter(row => row.status === 1 || row.status === 9)
      if (blocked.length > 0) {
        this.$message.warning('发布/作废状态不允许删除')
        return
      }
      this.$confirm({
        title: '删除确认',
        content: '确认删除选中的作业指导书吗？',
        onOk: () => {
          const ids = this.selectRows.map(row => row.id).join(',')
          delJgbRecord({ ids }).then((res) => {
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
        this.$message.warning('请选择一条作业指导书')
        return
      }
      const id = this.selectRows[0].id
      saveJgbAsTemplate({ id }).then(res => {
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
        this.$message.warning('请选择作业指导书')
        return
      }
      if (record.status !== 1) {
        this.$message.warning('仅发布状态可修订升版')
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
          reviseJgbVersion({ id: record.id, newVersion }).then(res => {
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
      if (!ver) return 'V2.0'
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
    canEdit (record) {
      if (!record) return false
      if (record.status === 2) return true
      return record.status === 0 && (record.reviewStatus === 0 || record.reviewStatus === 1)
    },
    resolveTargetRows (record) {
      if (record && record.id) return [record]
      return (this.selectRows || []).filter(item => item && item.id)
    },
    canSubmitReview (row) {
      return !!row && row.status === 0 && row.reviewStatus === 0
    },
    canReviewDecision (row) {
      return !!row && row.status === 0 && row.reviewStatus === 1
    },
    canSubmitApprove (row) {
      return !!row && row.status === 0 && row.reviewStatus === 2
    },
    canApproveDecision (row) {
      return !!row && row.status === 2
    },
    handleSubmitReview (record) {
      const rows = this.resolveTargetRows(record)
      if (rows.length === 0) {
        this.$message.warning('请先选择作业指导书')
        return
      }
      const invalid = rows.filter(row => !this.canSubmitReview(row))
      if (invalid.length > 0) {
        this.$message.warning('仅草稿状态可提交审核，请重新选择')
        return
      }
      this.reviewerAction = 'review'
      this.pendingReviewIds = rows.map(row => row.id).join(',')
      this.$refs.reviewerSelect.showModal()
    },
    handleReviewDecision (record) {
      if (record && record.id) {
        if (!this.canReviewDecision(record)) {
          this.$message.warning('仅审核中状态可进行审核')
          return
        }
        this.$refs.reviewModal.open(record)
        return
      }
      const rows = this.resolveTargetRows(record)
      if (rows.length === 0) {
        this.$message.warning('请先选择作业指导书')
        return
      }
      if (rows.length === 1) {
        const single = rows[0]
        if (!this.canReviewDecision(single)) {
          this.$message.warning('仅审核中状态可进行审核')
          return
        }
        this.$refs.reviewModal.open(single)
        return
      }
      const invalid = rows.filter(row => !this.canReviewDecision(row))
      if (invalid.length > 0) {
        this.$message.warning('批量审核仅支持审核中状态，请重新选择')
        return
      }
      const ids = rows.map(row => row.id).join(',')
      this.$confirm({
        title: '批量审核',
        content: `是否批量审核通过选中的 ${rows.length} 条记录？若需退回请点"否"并填写原因。`,
        okText: '是',
        cancelText: '否',
        onOk: () => {
          decisionJgbReview({ ids, reviewStatus: 2, reviewComment: '批量审核通过' }).then(res => {
            if (res.success) {
              this.$message.success(`已批量审核通过 ${rows.length} 条`)
              this.loadData()
            } else {
              this.$message.error(res.message || '批量审核失败')
            }
          })
        },
        onCancel: async () => {
          const reason = window.prompt('请输入审核退回原因')
          if (reason === null) return
          const text = String(reason || '').trim()
          if (!text) {
            this.$message.warning('请输入退回原因')
            return
          }
          decisionJgbReview({ ids, reviewStatus: 3, reviewComment: text }).then(res => {
            if (res.success) {
              this.$message.success(`已批量退回 ${rows.length} 条`)
              this.loadData()
            } else {
              this.$message.error(res.message || '批量退回失败')
            }
          })
        }
      })
    },
    handleSubmitApprove (record) {
      const rows = this.resolveTargetRows(record)
      if (rows.length === 0) {
        this.$message.warning('请先选择作业指导书')
        return
      }
      const invalid = rows.filter(row => !this.canSubmitApprove(row))
      if (invalid.length > 0) {
        this.$message.warning('仅审核通过状态可提交审批，请重新选择')
        return
      }
      this.reviewerAction = 'approve'
      this.pendingApproveIds = rows.map(row => row.id).join(',')
      this.$refs.reviewerSelect.showModal()
    },
    handleApproveDecision (record) {
      const rows = this.resolveTargetRows(record)
      if (rows.length === 0) {
        this.$message.warning('请先选择作业指导书')
        return
      }
      const invalid = rows.filter(row => !this.canApproveDecision(row))
      if (invalid.length > 0) {
        this.$message.warning('仅审批中状态可审批，请重新选择')
        return
      }
      const ids = rows.map(row => row.id).join(',')
      this.$confirm({
        title: rows.length > 1 ? '批量审批处理' : '审批处理',
        content: rows.length > 1
          ? `是否批量审批通过选中的 ${rows.length} 条记录？若需退回请点"否"并填写原因。`
          : '是否审批通过？若需退回请点"否"并填写原因。',
        okText: '是',
        cancelText: '否',
        onOk: () => {
          decisionJgbApprove({ ids, approveStatus: 1 }).then(res => {
            if (res.success) {
              this.$message.success(rows.length > 1 ? `已批量审批通过 ${rows.length} 条` : '审批通过')
              this.loadData()
            } else {
              this.$message.error(res.message || (rows.length > 1 ? '批量审批失败' : '审批失败'))
            }
          })
        },
        onCancel: async () => {
          const reason = window.prompt('请输入审批退回原因')
          if (reason === null) return
          const text = String(reason || '').trim()
          if (!text) {
            this.$message.warning('请输入退回原因')
            return
          }
          decisionJgbApprove({ ids, approveStatus: 2, approveComment: text }).then(res => {
            if (res.success) {
              this.$message.success(rows.length > 1 ? `已批量退回 ${rows.length} 条` : '已退回')
              this.loadData()
            } else {
              this.$message.error(res.message || (rows.length > 1 ? '批量退回失败' : '退回失败'))
            }
          })
        }
      })
    },
    onReviewerSelected (users) {
      if (!users || users.length === 0) {
        this.$message.warning('请选择处理人')
        return
      }
      const handler = users[0]
      if (this.reviewerAction === 'approve' && this.pendingApproveIds) {
        const ids = this.pendingApproveIds
        const count = ids.split(',').filter(Boolean).length
        submitJgbApprove({
          ids,
          approverId: handler.id,
          approverName: handler.realname || handler.username
        }).then(res => {
          if (res.success) {
            this.$message.success(count > 1 ? `已批量提交审批 ${count} 条` : '已提交审批')
            this.loadData()
          } else {
            this.$message.error(res.message || '提交失败')
          }
        }).finally(() => {
          this.pendingApproveIds = ''
          this.reviewerAction = 'review'
        })
        return
      }
      if (this.pendingReviewIds) {
        const ids = this.pendingReviewIds
        const count = ids.split(',').filter(Boolean).length
        submitJgbReview({
          ids,
          reviewerId: handler.id,
          reviewerName: handler.realname || handler.username
        }).then(res => {
          if (res.success) {
            this.$message.success(count > 1 ? `已批量提交审核 ${count} 条` : '已提交审核')
            this.loadData()
          } else {
            this.$message.error(res.message || '提交失败')
          }
        }).finally(() => {
          this.pendingReviewIds = ''
          this.reviewerAction = 'review'
        })
      }
    },
    handlePublish (record) {
      if (!record || !record.id) return
      if (record.status !== 3) {
        this.$message.warning('请先审批通过后再发布')
        return
      }
      this.$confirm({
        title: '发布确认',
        content: '发布后将自动作废同文件编号的旧发布版本，是否继续？',
        onOk: () => {
          updateJgbStatus({ id: record.id, status: 1 }).then(res => {
            if (res.success) {
              this.$message.success('已发布')
              this.loadData()
            } else {
              this.$message.error(res.message || '发布失败')
            }
          })
        }
      })
    },
    handleExport (record) {
      if (!record || !record.id) {
        this.$message.warning('请选择作业指导书')
        return
      }
      const fileName = `${record.fileName || '作业指导书'}.pdf`
      exportJgbPdf(fileName, { ids: record.id })
    },
    handleBatchExport () {
      const rows = this.selectRows || []
      if (rows.length === 0) {
        this.$message.warning('请先选择要导出的作业指导书')
        return
      }
      const ids = rows.map(row => row.id).join(',')
      const now = moment().format('YYYYMMDDHHmmss')
      const fileName = rows.length === 1
        ? `${rows[0].fileName || '作业指导书'}.pdf`
        : `作业指导书批量导出_${now}.zip`
      exportJgbPdf(fileName, { ids })
    },
    async handleExportCatalog () {
      const params = { ...this.queryParam, pageNo: 1, pageSize: 2000 }
      const res = await getJgbPage(params)
      if (!res || !res.success) {
        this.$message.warning('目录导出失败')
        return
      }
      const rows = (res.result && res.result.records) ? res.result.records : []
      await this.downloadExcel(rows, '作业指导书目录.xlsx')
    },
    async downloadExcel (rows, filename) {
      const workbook = new ExcelJS.Workbook()
      const sheet = workbook.addWorksheet('作业指导书目录')
      sheet.columns = [
        { header: '序号', key: 'idx', width: 8 },
        { header: '线路', key: 'lineName', width: 14 },
        { header: '状态', key: 'bizStatus', width: 12 },
        { header: '车型', key: 'trainTypeName', width: 14 },
        { header: '修程', key: 'repairProgramName', width: 14 },
        { header: '项目', key: 'project', width: 16 },
        { header: '文件编号', key: 'fileNo', width: 20 },
        { header: '文件名称', key: 'fileName', width: 32 },
        { header: '版本号', key: 'fileVer', width: 12 },
        { header: '编制人', key: 'creatorName', width: 14 }
      ]
      ;(rows || []).forEach((row, idx) => {
        sheet.addRow({
          idx: idx + 1,
          lineName: row.lineName || '',
          bizStatus: this.formatBizStatus(row),
          trainTypeName: row.trainTypeName || '',
          repairProgramName: row.repairProgramName || '',
          project: row.project || '',
          fileNo: row.fileNo || '',
          fileName: row.fileName || '',
          fileVer: row.fileVer || '',
          creatorName: row.creatorName || ''
        })
      })
      sheet.getRow(1).font = { bold: true }
      const buffer = await workbook.xlsx.writeBuffer()
      const blob = new Blob([buffer], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8'
      })
      FileSaver.saveAs(blob, filename)
    },
    formatBizStatus (row) {
      if (!row) return '-'
      if (row.status === 9) return '作废'
      if (row.status === 1) return '发布'
      if (row.status === 3) return '审批通过'
      if (row.status === 2) return '审批中'
      if (row.reviewStatus === 1) return '审核中'
      if (row.reviewStatus === 2) return '审核通过'
      return '草稿'
    },
    statusColor (row) {
      const status = this.formatBizStatus(row)
      if (status === '发布') return 'green'
      if (status === '审批通过') return 'cyan'
      if (status === '审批中' || status === '审核中') return 'blue'
      if (status === '作废') return 'red'
      return 'default'
    },
    formatDate (row) {
      const time = row.updateTime || row.createTime
      if (time) return moment(time).format('YYYY-MM-DD HH:mm:ss')
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
