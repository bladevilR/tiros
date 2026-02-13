<template>
  <div class="bodyWrapper">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="4" :sm="24">
            <a-form-item label="转序单">
              <a-input placeholder="编号/描述" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <a-select v-model="queryParam.status" allowClear placeholder="请选择">
                <a-select-option :value="0">待处理</a-select-option>
                <a-select-option :value="1">已放行</a-select-option>
                <a-select-option :value="2">已驳回</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="车号">
              <a-input placeholder="请输入车号" v-model="queryParam.trainNo" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="列计划ID">
              <a-input placeholder="请输入列计划ID" v-model="queryParam.planId" allowClear></a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col>
            <span class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleCreateByFault">由故障创建</a-button>
                <a-button :disabled="records.length !== 1 || records[0].status !== 0" @click="handleEdit(records[0])">编辑</a-button>
                <a-button :disabled="records.length !== 1 || records[0].status !== 0" @click="handleAllow(records[0])">一键放行</a-button>
                <a-button :disabled="records.length !== 1 || records[0].status !== 0" @click="handleReject(records[0])">驳回</a-button>
                <a-button :disabled="records.length < 1" @click="handleDelete">删除</a-button>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div style="height: calc(100vh - 267px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(100vh - 267px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="transferCode" title="转序编号" width="160"></vxe-table-column>
        <vxe-table-column field="trainNo" title="车号" width="90"></vxe-table-column>
        <vxe-table-column field="orderCode" title="工单号" width="130"></vxe-table-column>
        <vxe-table-column field="taskName" title="异常工步" width="180" align="left" header-align="center"></vxe-table-column>
        <vxe-table-column field="nextProcess" title="受影响后续工序" width="200" align="left" header-align="center"></vxe-table-column>
        <vxe-table-column field="transferDesc" title="转序说明" min-width="220" align="left" header-align="center"></vxe-table-column>
        <vxe-table-column field="status" title="状态" width="90">
          <template v-slot="{ row }">
            <a-tag :color="row.status === 1 ? 'green' : row.status === 2 ? 'red' : 'orange'">
              {{ row.status === 1 ? '已放行' : row.status === 2 ? '已驳回' : '待处理' }}
            </a-tag>
          </template>
        </vxe-table-column>
        <vxe-table-column field="decisionType" title="完成方式" width="90">
          <template v-slot="{ row }">
            {{ row.decisionType === 1 ? '审批流' : row.decisionType === 2 ? '一键放行' : '-' }}
          </template>
        </vxe-table-column>
        <vxe-table-column field="submitUserName" title="提报人" width="100"></vxe-table-column>
        <vxe-table-column field="submitTime" title="提报时间" width="160"></vxe-table-column>
        <vxe-table-column field="decisionUserName" title="决策人" width="100"></vxe-table-column>
        <vxe-table-column field="decisionTime" title="决策时间" width="160"></vxe-table-column>
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

    <a-modal :visible="editVisible" title="例外转序" @ok="handleSave" @cancel="editVisible = false" :confirmLoading="saveLoading" :maskClosable="false">
      <a-form-model :model="editModel" :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-model-item label="故障ID">
          <a-input v-model="editModel.faultId" :disabled="!!editModel.id"></a-input>
        </a-form-model-item>
        <a-form-model-item label="工单ID">
          <a-input v-model="editModel.orderId" :disabled="!!editModel.id"></a-input>
        </a-form-model-item>
        <a-form-model-item label="工单任务ID">
          <a-input v-model="editModel.orderTaskId"></a-input>
        </a-form-model-item>
        <a-form-model-item label="工序名称">
          <a-input v-model="editModel.processName"></a-input>
        </a-form-model-item>
        <a-form-model-item label="工步名称">
          <a-input v-model="editModel.stepName"></a-input>
        </a-form-model-item>
        <a-form-model-item label="后续工序">
          <a-input v-model="editModel.nextProcess"></a-input>
        </a-form-model-item>
        <a-form-model-item label="转序说明">
          <a-textarea v-model="editModel.transferDesc" :rows="3"></a-textarea>
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
import {
  pageExceptionTransfer,
  saveExceptionTransfer,
  updateExceptionTransfer,
  decideExceptionTransfer,
  deleteExceptionTransfer
} from '@/api/tirosQualityApi'

export default {
  name: 'ExceptionTransferPage',
  data() {
    return {
      queryParam: {
        searchText: '',
        status: undefined,
        trainNo: '',
        planId: '',
        pageNo: 1,
        pageSize: 10
      },
      allAlign: 'center',
      records: [],
      totalResult: 0,
      tableData: [],
      editVisible: false,
      saveLoading: false,
      editModel: {}
    }
  },
  created() {
    this.handleRouteCreate()
    this.findList()
  },
  methods: {
    handleRouteCreate() {
      const query = this.$route && this.$route.query ? this.$route.query : {}
      if (query.create !== '1' || !query.faultId || !query.orderId) {
        return
      }
      this.editModel = {
        faultId: query.faultId,
        orderId: query.orderId,
        orderTaskId: query.orderTaskId || '',
        processName: query.processName || '',
        stepName: '',
        nextProcess: '',
        transferDesc: query.transferDesc || ''
      }
      this.editVisible = true
      this.$nextTick(() => {
        this.$router.replace({ path: this.$route.path, query: {} })
      })
    },
    checkboxChange(e) {
      this.records = e.records
    },
    findList() {
      pageExceptionTransfer(this.queryParam).then((res) => {
        if (res.success && res.result) {
          this.tableData = res.result.records || []
          this.totalResult = res.result.total || 0
          this.records = []
        }
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleCreateByFault() {
      this.editModel = {
        faultId: '',
        orderId: '',
        orderTaskId: '',
        processName: '',
        stepName: '',
        nextProcess: '',
        transferDesc: ''
      }
      this.editVisible = true
    },
    handleEdit(row) {
      this.editModel = { ...row }
      this.editVisible = true
    },
    handleSave() {
      if (!this.editModel.faultId || !this.editModel.orderId) {
        this.$message.warning('故障ID和工单ID不能为空')
        return
      }
      this.saveLoading = true
      const req = this.editModel.id ? updateExceptionTransfer(this.editModel) : saveExceptionTransfer(this.editModel)
      req.then((res) => {
        if (res.success) {
          this.$message.success('保存成功')
          this.editVisible = false
          this.findList()
        } else {
          this.$message.error(res.message || '保存失败')
        }
      }).finally(() => {
        this.saveLoading = false
      })
    },
    handleAllow(row) {
      this.$confirm({
        title: '确认放行',
        content: `确认将【${row.transferCode}】标记为可放行？`,
        onOk: () => {
          decideExceptionTransfer({ id: row.id, status: 1, decisionType: 2, decisionRemark: '一键放行' }).then((res) => {
            if (res.success) {
              this.$message.success('放行成功')
              this.findList()
            } else {
              this.$message.error(res.message || '放行失败')
            }
          })
        }
      })
    },
    handleReject(row) {
      this.$confirm({
        title: '确认驳回',
        content: `确认驳回【${row.transferCode}】？`,
        onOk: () => {
          decideExceptionTransfer({ id: row.id, status: 2, decisionType: 2, decisionRemark: '驳回' }).then((res) => {
            if (res.success) {
              this.$message.success('驳回成功')
              this.findList()
            } else {
              this.$message.error(res.message || '驳回失败')
            }
          })
        }
      })
    },
    handleDelete() {
      if (!this.records.length) {
        this.$message.warning('请先选择记录')
        return
      }
      this.$confirm({
        title: '确认删除',
        content: `确认删除选中${this.records.length}条记录？`,
        onOk: () => {
          const ids = this.records.map(item => item.id).join(',')
          deleteExceptionTransfer({ ids }).then((res) => {
            if (res.success) {
              this.$message.success('删除成功')
              this.findList()
            } else {
              this.$message.error(res.message || '删除失败')
            }
          })
        }
      })
    }
  }
}
</script>

<style scoped>
</style>
