<template>
  <div>
    <!-- 查询区域 -->
    <div class='table-page-search-wrapper'>
      <a-form layout='inline'>
        <a-row :gutter='24'>
          <a-col :md='5' :sm='24'>
            <a-form-item label='来源'>
              <j-dict-select-tag
                v-model='queryParam.fromType'
                placeholder='请选择'
                dictCode='fault_from_type'
              />
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-item label='故障'>
              <a-input placeholder='请输入名称或编码' v-model='queryParam.searchText' allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-item label='故障状态'>
              <j-dict-select-tag
                v-model='queryParam.status'
                placeholder='请选择'
                dictCode='bu_fault_status'
              />
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-item label='提交状态'>
              <j-dict-select-tag
                v-model='queryParam.submitStatus'
                placeholder='请选择'
                dictCode='bu_fault_submit_status'
              />
            </a-form-item>
          </a-col>
          <a-col :md='6' :sm='24'>
            <a-form-item label='发现日期'>
              <a-date-picker v-model='date' />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col>
            <span class='table-page-search-submitButtons'>
              <a-space>
                 <a-button @click='findList'>查询</a-button>
                <a-button type='primary' @click='handleAdd'>新增</a-button>
                <a-button :disabled='records.length !== 1' @click.stop='handleEdit(records[0])'>编辑</a-button>
                <a-button :disabled='!records || records.length === 0' @click='subGroup'
                          style='margin-left: 8px'>提交故障</a-button>
                <!--              <a-button style="margin-left: 8px" @click="deleteRecord">删除</a-button>-->
                <a-button style='margin-left: 8px' :disabled="records.length !==1 || records[0].wfStatus !== '未发起' "
                          @click='cancelRecord'>取消</a-button>
                <ProcessButtons
                  ref='WfButtons'
                  :can-start='false'
                  :solution-code="'WF_FAULT_HANDLE'"
                  v-if='selectedRow && 1 === selectedRow.fromType'
                  @StartSuccess='onStartSuccess'
                  @StartFailure='onStartFailure'
                  @handleSuccess='onHandleSuccess'
                  @cancelSuccess='findList'
                  :business-key='selectedRow.id'
                  :business-title='selectedRow.faultDesc'
                  :process-instance-id='selectedRow.processInstanceId'
                  :variables='variables'
                  :title='selectedRow.wfStatus'
                ></ProcessButtons>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style='height: calc(100vh - 267px)'>
      <vxe-table
        border
        max-height='100%'
        style='height: calc(100vh - 267px)'
        ref='listTable'
        :align='allAlign'
        :data='tableData'
        show-overflow='tooltip'
        @checkbox-change='checkboxChange'
        @checkbox-all='checkboxChange'
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column fixed='left' type='checkbox' width='40'></vxe-table-column>
        <vxe-table-column field='faultSn' title='故障编号' width='140'>
          <template v-slot='{ row }'>
            <a @click.stop='faultDetail(row)'>{{ row.faultSn }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field='fromType_dictText' title='来源' width='120'></vxe-table-column>
        <vxe-table-column field='lineName' title='线路' width='80'></vxe-table-column>
        <vxe-table-column field='trainNo' title='车号' width='80'></vxe-table-column>
        <vxe-table-column field='sysName' title='故障系统' min-width='100'></vxe-table-column>
        <vxe-table-column field='faultAssetName' title='故障部件' align='left' header-align='center'
                          min-width='100'></vxe-table-column>
        <vxe-table-column field='faultDesc' title='故障描述' min-width='160' align='left'
                          header-align='center'></vxe-table-column>
        <vxe-table-column field='happenTime' title='发现时间' width='100'></vxe-table-column>
        <vxe-table-column field='reportUserName' title='提报人员' align='left' header-align='center'
                          width='100'></vxe-table-column>
        <vxe-table-column field='reportTime' title='提报日期' width='100'></vxe-table-column>
        <vxe-table-column field='status_dictText' title='故障状态' align='left' header-align='center'
                          width='80'></vxe-table-column>
        <vxe-table-column field='handleStatus_dictText' title='处理结果' align='left' header-align='center'
                          width='80'></vxe-table-column>
        <vxe-table-column field='wfStatus' align='left' header-align='center' title='当前环节' width='120'>
        </vxe-table-column>
        <vxe-table-column
          field='processCandidate'
          title='当前处理人'
          align='left'
          header-align='center'
          width='160'
        ></vxe-table-column>
        <vxe-table-column field='submitStatus_dictText' title='提交状态' align='left' header-align='center'
                          width='80'></vxe-table-column>
        <vxe-table-column field='workOrderId ' title='来源工单' width='100'>
          <template v-slot='{ row }'>
            <a v-if='row.workOrderId' @click.stop='preViewOrderDetail(row.workOrderId)'>查看</a>
          </template>
        </vxe-table-column>
        <vxe-table-column title='处理工单' width='100'>
          <template v-slot='{ row }'>
            <a v-if='row.handleOrderId' @click.stop='preViewOrderDetail(row.handleOrderId)'>查看</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field='handleOrderId' title='已安排?' width='80' :formatter='formatValue'></vxe-table-column>
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync='queryParam.pageNo'
        :page-size.sync='queryParam.pageSize'
        :total='totalResult'
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change='handlePageChange'
      ></vxe-pager>
    </div>
    <!--    <safe-anticipate-modal ref="modalForm" @ok="loadData()"></safe-anticipate-modal>-->
    <BreakdownModal ref='breakdownModal' @ok='loadData()'></BreakdownModal>
    <breakdown-detail-modal ref='breakdownDetail'></breakdown-detail-modal>
    <view-work-order-modal ref='viewOrderModal'></view-work-order-modal>
  </div>
</template>

<script>
import moment from 'moment'
import {
  cancelGroupFault,
  delGroupFault,
  getGroupFaultInfo,
  getGroupFaultList,
  subGroupFault
} from '@api/tirosGroupApi'
import BreakdownModal from '@views/tiros/dispatch/breakdown/BreakdownModal'
import BreakdownDetailModal from '@views/tiros/dispatch/breakdown/BreakdownDetailModal'
import ViewWorkOrderModal from '@views/tiros/dispatch/workOrder/ViewWorkOrderModal'
import ProcessButtons from '@views/workflow/ProcessButtons'

export default {
  name: 'GroupBreakdownPage',
  components: { ViewWorkOrderModal, BreakdownModal, BreakdownDetailModal, ProcessButtons },
  data() {
    return {
      date: null,
      records: [],
      selectedRow: null,
      queryParam: {
        fromType: 1,
        groupId: this.$store.getters.userInfo.departId,
        happenTime: null,
        status: '',
        searchText: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      count: '',
      variables: { businessCode: '' }
    }
  },
  created() {
    this.findList()
  },
  methods: {
    checkboxChange(e) {
      this.records = e.records
      if (this.records.length === 1) {
        this.selectedRow = this.records[0]
        this.variables.businessCode = this.selectedRow.faultSn
      } else {
        this.selectedRow = null
      }
    },
    preViewOrderDetail(id) {
      this.$refs.viewOrderModal.showModal(id)
    },
    formatValue({ cellValue }) {
      if (cellValue) {
        return '已安排'
      } else {
        return '未安排'
      }
    },
    faultDetail(record) {
      /*getBreakdownInfo({ id: record.id }).then((res) => {
        let data = res.result
        this.$refs.breakdownDetail.show(data)
      })*/
      this.$refs.breakdownDetail.show(record.id)
    },
    findList() {
      this.loading = true
      this.selectedRow = null
      this.records = []
      if (this.date) {
        this.queryParam.happenTime = moment(this.date).format('YYYY-MM-DD')
      } else {
        this.queryParam.happenTime = ''
      }
      getGroupFaultList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
        this.records = []
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData() {
      this.findList()
    },
    subGroup() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let canSubs = selectRecords.filter(r => {
          return r.submitStatus != 0
        })
        if (canSubs.length > 0) {
          this.$message.warn('只有未提交的故障才能进行提交')
          return
        }


        this.$confirm({
          content: `是否提交选中${selectRecords.length}数据？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function(obj, index) {
                return obj.id
              })
              .join(',')
            subGroupFault('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handleEdit(record) {
      // this.$router.push({ name: 'tiros-group-groupbreakdown-edit', params: { record } })
      getGroupFaultInfo({ id: record.id }).then((res) => {
        let data = res.result
        data['formType'] = 2
        this.$refs.breakdownModal.edit(data)
      })
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            var idsStr = selectRecords
              .map(function(obj, index) {
                return obj.id
              })
              .join(',')
            delGroupFault('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    cancelRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        const canSubs = selectRecords.filter(r => {
          return !!r.handleOrderId
        })
        if (canSubs.length > 0) {
          this.$message.warn('已安排工单的故障不能取消')
          return
        }
        const submitStatus = selectRecords.filter((item) => item.submitStatus == 2)
        if (submitStatus.length > 0) {
          this.$message.warn('已取消的故障不能取消')
          return
        }

        this.$confirm({
          content: `是否取消选中${selectRecords.length}数据？`,
          onOk: () => {
            var idsStr = selectRecords
              .map(function(obj, index) {
                return obj.id
              })
              .join(',')
            cancelGroupFault('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handleAdd() {
      this.$refs.breakdownModal.add(null, null, 2)
    },
    // 流程启动成功回调
    onStartSuccess(data) {
      this.findList()
    },
    // 流程启动失败回调
    onStartFailure(data) {
      console.log('启动失败:', data)
    },
    // 流程处理成功
    onHandleSuccess(data) {
      this.findList()
    }
  }
}
</script>

<style scoped>

</style>