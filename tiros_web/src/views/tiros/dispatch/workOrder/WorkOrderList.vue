<template>
  <div>
    <a-spin :spinning="loading">
      <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="findList">
          <a-row :gutter="24">
            <a-col :md="5" :sm="24">
              <a-form-item label="工单" :labelCol="{ span: 5 }" :wrapperCol="{ span: 19 }">
                <a-input placeholder="请输入名称或编号" v-model="queryParam.searchText" allow-clear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="3" :sm="24">
              <a-form-item label="车辆" :labelCol="{ span: 5 }" :wrapperCol="{ span: 19 }">
                <j-dict-select-seach-tag
                  v-model="queryParam.trainNo"
                  placeholder="请选择"
                  dictCode="bu_train_info,train_no,train_no"
                />
              </a-form-item>
            </a-col>
            <a-col :md="4" :sm="24">
              <a-form-item label="状态" :labelCol="{ span: 5 }" :wrapperCol="{ span: 19 }">
                <j-dict-select-tag v-model="queryParam.status" placeholder="请选择状态" dictCode="bu_order_status" />
              </a-form-item>
            </a-col>
            <a-col :md="4" :sm="24">
              <a-form-item label="类型" :labelCol="{ span: 5 }" :wrapperCol="{ span: 19 }">
                <j-dict-select-tag
                  placeholder="请选择"
                  :triggerChange="true"
                  v-model="queryParam.orderType"
                  dictCode="bu_order_type"
                />
              </a-form-item>
            </a-col>
            <a-col :md="7" :sm="24">
              <a-form-item label="工单日期">
                <a-range-picker v-model="dateRange" @change="onDateChange" :defaultPickerValue="defaultDateRange" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter='24' :style="{ display: expand ? 'block' : 'none' }">
            <a-col :md="5" :sm="24">
              <a-form-item label="班组" :labelCol="{ span: 5 }" :wrapperCol="{ span: 19 }">
                <j-dict-select-tag v-model="queryParam.workGroupId" placeholder="请选择班组" :dictCode="dictGroupStr" />
              </a-form-item>
            </a-col>

            <a-col :md="5" :sm="24">
              <a-form-item label="逾期" :labelCol="{ span: 5 }" :wrapperCol="{ span: 19 }">
                <j-dict-select-tag
                  placeholder="请选择"
                  :triggerChange="true"
                  v-model="queryParam.overdue"
                  dictCode="bu_state"
                />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col :md="24" :sm="24">
              <a-space>
                <a-button @click="findList">查询</a-button>
<!--                <a-button type="primary" @click="handleAdd">新增</a-button>-->
                <a-dropdown>
                  <a-menu slot="overlay" @click="handleMenuClick">
                    <a-menu-item key="1">
                      新增工单
                    </a-menu-item>
                    <a-menu-item key="2">
                      新增车间消耗
                    </a-menu-item>
                  </a-menu>
                  <a-button type="primary"> 新增 <a-icon type="down" /> </a-button>
                </a-dropdown>
                <a-button @click="handleEdit" :disabled="!btnStatus.edit">编辑</a-button>
                <a-button @click="handleDel" :disabled="!btnStatus.del">删除</a-button>
                <a-button @click="handleIssueWorkOrder()" :disabled="!btnStatus.del">下发工单</a-button>
                <a-button @click="closeWorkOrder" :disabled="!btnStatus.del">关闭工单</a-button>
                <a-button @click="suspendWorkOrder" :disabled="!btnStatus.del">暂停工单</a-button>
                <a-button @click="activityWorkOrder" :disabled="!btnStatus.del">激活工单</a-button>
                <a-button @click="cancelWorkOrder" :disabled="!btnStatus.del">取消工单</a-button>
                <a-button><a :style="{fontSize: '12px'}" @click='expand = !expand'>
                更多条件 <a-icon :type="expand ? 'up' : 'down'" />
                </a></a-button>
              </a-space>
              <!--           <a-button @click="closeWorkOrder()">关闭工单</a-button>-->
              <ProcessButtons
                ref="WfButtons"
                v-if="selectedRow"
                v-has="'work:order:workflow1'"
                @StartSuccess="onStartSuccess"
                @StartFailure="onStartFailure"
                @handleSuccess="onHandleSuccess"
                @cancelSuccess="findList"
                :can-start="false"
                :solution-code="solutionCode"
                :business-key="selectedRow.id"
                :business-title="selectedRow.orderName"
                :process-instance-id="selectedRow.processInstanceId"
                :variables="variables"
                :title="selectedRow.wfStatus"
              ></ProcessButtons>
            </a-col>
          </a-row>
          <a-row>
            <a-col>
              <span class='table-page-search-submitButtons'>
                <a-space>

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
          :edit-config="{ trigger: 'manual', mode: 'row' }"
          @checkbox-change="rowSelectChange"
          @checkbox-all="btnStatus.update()"
        >
          <vxe-table-column type="checkbox" width="40"></vxe-table-column>
          <vxe-table-column field="lineName" title="线路" width="100"></vxe-table-column>
          <vxe-table-column field="trainNo" title="车号" width="80"></vxe-table-column>
          <vxe-table-column field="orderCode" title="工单编号" align="center" width="120">
            <template v-slot="{ row }">
              <!-- :disabled="row.orderStatus !== 0" -->
              <a-button type="link" @click.stop="viewOrder(row)">{{ row.orderCode }}</a-button>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="orderName"
            title="工单名称"
            align="left"
            width="250"
            header-align="center"
          ></vxe-table-column>
          <vxe-table-column
            field="planName"
            title="列计划"
            align="left"
            width="250"
            header-align="center"
          ></vxe-table-column>
          <vxe-table-column field="orderType_dictText" title="工单类型" align="center" width="100"></vxe-table-column>
          <vxe-table-column
            field="groupName"
            title="作业工班"
            align="left"
            header-align="center"
            width="150"
          ></vxe-table-column>

          <vxe-table-column field="orderStatus_dictText" title="工单状态" width="120">
            <template v-slot="{ row }">
              <div :style="{ backgroundColor: orderStatusColor[row.orderStatus + ''], borderRadius: '4px' }">
                {{ row.orderStatus_dictText }}
              </div>
            </template>
          </vxe-table-column>
          <vxe-table-column field="workStatus_dictText" title="作业进度" width="120">
            <template v-slot="{ row }">
              <div :style="{ backgroundColor: workStatusColor[row.workStatus + ''], borderRadius: '4px' }">
                {{ row.workStatus_dictText }}
              </div>
            </template>
          </vxe-table-column>
          <vxe-table-column field="wfStatus" align="left" header-align="center" title="当前环节" width="120">
          </vxe-table-column>
          <vxe-table-column
            field="processCandidate"
            title="当前处理人"
            align="left"
            header-align="center"
            width="160"
          ></vxe-table-column>
          <vxe-table-column field="startTime" title="计划开始" width="120"></vxe-table-column>
          <!-- ??????下达日期 -->
          <vxe-table-column field="issuingTime" title="下发日期" width="120"></vxe-table-column>
          <vxe-table-column field="actStart" title="实际开始" width="120"></vxe-table-column>
          <vxe-table-column field="overdue_dictText" title="是否逾期" width="120"></vxe-table-column>
          <vxe-table-column
            field="remark"
            title="备注"
            align="left"
            min-width="120"
            header-align="center"
          ></vxe-table-column>
          <vxe-table-column field="syncTime" title="消耗发送时间" width="180"></vxe-table-column>
          <vxe-table-column field="syncResult" title="消耗状态" width="120"></vxe-table-column>
          <vxe-table-column field="syncResultTime" title="消耗状态时间" width="180"></vxe-table-column>
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
    </a-spin>
    <edit-work-order-modal ref="workOrderModal" @ok="findList"></edit-work-order-modal>
    <edit-order-modal ref="orderModal" @ok="findList"></edit-order-modal>
    <view-work-order-modal ref="viewOrderModal"></view-work-order-modal>
  </div>
</template>

<script>
import {
  delWorkOrder,
  getWorkOrderList,
  issueWorkOrderList,
  approveCloseWorkOrder,
  activityWorkOrder, suspendWorkOrder, cancelWorkOrder
} from '@/api/tirosDispatchApi'
import EditWorkOrderModal from '@views/tiros/dispatch/workOrder/EditWorkOrderModal'
import EditOrderModal from '@views/tiros/dispatch/workOrder/special/EditOrderModal'
import ViewWorkOrderModal from '@views/tiros/dispatch/workOrder/ViewWorkOrderModal'
import moment from 'moment'
import ProcessButtons from '@views/workflow/ProcessButtons'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  components: { EditWorkOrderModal, ViewWorkOrderModal, ProcessButtons, EditOrderModal },
  computed: {
    solutionCode: function () {
      // selectedRow.orderType !== 4 ? 'WF_WORK_ORDER':'WF_MATERIAL_APPLY
      if (this.selectedRow) {
        switch (this.selectedRow.orderType) {
          case 4:
            return 'WF_MATERIAL_APPLY'
        case 5:
          return "WF_WORKSHOP_CONSUME"
        default:
          return 'WF_WORK_ORDER'
        }
      } else {

      }
    }
  },
  data () {
    return {
      expand: false,
      btnStatus: new TableBtnUtil(this, 'listTable', {
        attrs: [
          {
            key: 'edit',
            judge: (e) => {
              return e.orderStatus == 0
            }
          }, {
            key: 'approve',
            judge: (e) => {
              return e.orderStatus == 3
            }
          }
        ]
      }),
      loading: false,
      dictGroupStr:
        'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
      orderStatusColor: {
        0: '#dedede',
        1: '#b9caa7',
        2: '#bad795',
        3: '#eaaca5',
        4: '#b1cac0',
        5: '#e2b38c'
      },
      workStatusColor: {
        0: '#dedede',
        1: '#d4efba',
        2: '#bbdeec'
      },
      queryParam: {
        searchText: '',
        status: undefined,
        workGroupId: undefined,
        orderType: undefined,
        startDate: undefined, //moment(new Date()).format('YYYY-MM-DD'),
        endDate: undefined, //moment(new Date()).format('YYYY-MM-DD'),
        trainNo: '',
        overdue: undefined,
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      tableData: [],
      dateRange: null,
      allAlign: 'center',
      selectedRow: null,
      variables: { groupId: '' }
    }
  },
  /* created() {
     console.log('执行created')
     window.addEventListener('initOrderListQueryParam', (e) => this.initOrderListQueryParamHandler(e))
   },
   mounted() {
     console.log('执行mounted')
     window.addEventListener('initOrderListQueryParam', (e) => this.initOrderListQueryParamHandler(e))
     this.findList()
   },
   activated() {
     console.log('执行activated')
     this.initOrderListQueryParamHandler()
   },
   beforeDestroy() {
     console.log('执行beforeDestroy')
     window.removeEventListener('initOrderListQueryParam', (e) => this.initOrderListQueryParamHandler(e))
   },
   destroyed() {
     console.log('执行destroyed')
     window.removeEventListener('initOrderListQueryParam', (e) => this.initOrderListQueryParamHandler(e))
   },*/
  mounted () {
    if (sessionStorage.getItem('DEFAULT') === 'true') {
      this.queryParam.overdue = 1
      sessionStorage.removeItem('DEFAULT')
    }
    this.findList()
  },
  methods: {
    moment,
    onDateChange () {
      if (this.dateRange[0]) {
        this.queryParam.startDate = this.dateRange[0].format('YYYY-MM-DD')
      } else {
        this.queryParam.startDate = null
      }
      if (this.dateRange[1]) {
        this.queryParam.endDate = this.dateRange[1].format('YYYY-MM-DD')
      } else {
        this.queryParam.endDate = null
      }
    },
    handleMenuClick (click) {
      if (click.key === '1') {
        // this.handleAdd()
        this.$refs.workOrderModal.showModal(null)
      }
      if (click.key === '2') {
        this.$refs.orderModal.showModal(null)
      }
    },
    findList () {
      this.loading = true
      // this.queryParam.startTime = this.queryParam.startTime ? moment(this.queryParam.startTime).format('YYYY-MM-DD') : undefined
      this.selectedRow = null
      this.selectRecords = []
      getWorkOrderList(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result.records
        }
        this.btnStatus.update()
      })
    },
    handleEdit () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length == 1) {
        this.$refs.workOrderModal.title = '编辑'
        if (selectRecords[0].orderType !== 5) {
          this.$refs.workOrderModal.showModal(selectRecords[0].id)
        } else {
          this.$refs.orderModal.showModal(selectRecords[0].id)
        }
      } else {
        this.$message.warn('请选择一项数据！')
      }
    },
    handleAdd () {
      this.$refs.workOrderModal.showModal(null)
    },
    handleDel () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (
          selectRecords.filter((r) => {
            return r.wfStatus != '未发起'
          }).length > 0
        ) {
          this.$message.warn('审批中或已审批的数据不能删除')
          return
        }
        // let flag = false
        // 0 未下达  1 已下达(未接受)  2  已下达(已接受)  3 已提交(工班张)  4 已关闭(调度)   5 已暂停
        /*selectRecords.forEach(row => {
          if (row.orderStatus !== 0) {
            flag = true
            return false
          }
        })
        if (flag) {
          this.$message.error('只能删除未下达的工单')
          return
        }*/
        this.$confirm({
          content: `是否删除选中的工单？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delWorkOrder('ids=' + idsStr)
              .then((res) => {
                if (res.success) {
                  this.$message.success('删除成功')
                  this.findList()
                } else {
                  this.$message.error('删除失败')
                  console.log('删除失败:', res.message)
                }
              })
              .catch((err) => {
                this.$message.error('删除异常')
                console.log('删除异常：', err)
              })
          }
        })
      } else {
        this.$message.error('尚未选中工单数据!')
      }
    },
    handleIssueWorkOrder () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        // 0 未下达  1 已下达(未接受)  2  已下达(已接受)  3 已提交(工班张)  4 已关闭(调度)   5 已暂停
        let nooks = selectRecords.filter((w) => {
          return w.wfStatus !== '未发起' || w.orderStatus === 5 || w.orderStatus ===9
        })

        if (nooks && nooks.length > 0) {
          this.$message.error('只能对未发起、未暂停、未取消的工单进行下发操作')
          return
        }

        this.$confirm({
          content: `是否下发选中的工单？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            this.loading = true
            issueWorkOrderList('ids=' + idsStr)
              .then((res) => {
                this.loading = false
                if (res.success) {
                  this.$message.success('下发工单成功')
                  this.findList()
                } else {
                  this.$message.warning('下发工单失败：' + res.message)
                  console.error('下发工单失败:', res.message)
                }
              })
              .catch((err) => {
                this.$message.error('下发工单异常')
                console.error('下发工单异常：', err)
              })
          }
        })
      } else {
        this.$message.error('尚未选中工单数据!')
      }
    },
    closeWorkOrder () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let flag = false
        // 0 未下达  1 已下达(未接受)  2  已下达(已接受)  3 已提交(工班张)  4 已关闭(调度)   5 已暂停
        selectRecords.forEach((row) => {
          if (row.orderStatus !== 3) {
            flag = true
            return false
          }
        })
        if (flag) {
          this.$message.error('只能对 已提交 的工单进行关闭')
          return
        }

        this.$confirm({
          content: `是否关闭选中的工单？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords.map(function (obj, index) {
              return obj.id
            }).join(',')
            this.loading = true
            approveCloseWorkOrder('ids=' + idsStr)
              .then((res) => {
                this.loading = false
                if (res.success) {
                  this.$message.success('关闭工单成功')
                  this.findList()
                } else {
                  this.$message.error(res.message)
                  console.error('关闭工单失败:', res.message)
                }
              })
              .catch((err) => {
                this.$message.error('关闭工单异常')
                console.error('关闭工单异常：', err)
              })
          }
        })
      } else {
        this.$message.error('尚未选中工单数据!')
      }
    },
    suspendWorkOrder () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let flag = false
        // 0 未下达  1 已下达(未接受)  2  已下达(已接受)  3 已提交(工班张)  4 已关闭(调度)   5 已暂停
        selectRecords.forEach((row) => {
          if (row.orderStatus === 3 || row.orderStatus === 4 || row.orderStatus === 5) {
            flag = true
            return false
          }
        })
        if (flag) {
          this.$message.error('已经【提交、关闭、暂停】的工单不能暂停！')
          return
        }

        this.$confirm({
          content: `是否暂停选中的工单？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords.map(function (obj, index) {
              return obj.id
            }).join(',')
            this.loading = true
            suspendWorkOrder('ids=' + idsStr)
              .then((res) => {
                this.loading = false
                if (res.success) {
                  this.$message.success('暂停工单成功')
                  this.findList()
                } else {
                  this.$message.error(res.message)
                }
              })
              .catch((err) => {
                this.$message.error('暂停工单异常')
              })
          }
        })
      } else {
        this.$message.error('尚未选中工单数据!')
      }
    },
    activityWorkOrder () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let flag = false
        // 0 未下达  1 已下达(未接受)  2  已下达(已接受)  3 已提交(工班张)  4 已关闭(调度)   5 已暂停
        selectRecords.forEach((row) => {
          if (row.orderStatus != 5) {
            flag = true
            return false
          }
        })
        if (flag) {
          this.$message.error('只能对已暂停的工单进行关闭')
          return
        }

        this.$confirm({
          content: `是否激活选中的工单？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords.map(function (obj, index) {
              return obj.id
            }).join(',')
            this.loading = true
            activityWorkOrder('ids=' + idsStr)
              .then((res) => {
                this.loading = false
                if (res.success) {
                  this.$message.success('激活工单成功')
                  this.findList()
                } else {
                  this.$message.error(res.message)
                }
              })
              .catch((err) => {
                this.$message.error('激活工单异常')
              })
          }
        })
      } else {
        this.$message.error('尚未选中工单数据!')
      }
    },
    cancelWorkOrder () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let flag = false
        selectRecords.forEach((row) => {
          if (row.orderStatus === 4 || row.orderStatus === 9) {
            flag = true
            return false
          }
        })
        if (flag) {
          this.$message.error('只能对未关闭或未取消的工单进行取消')
          return
        }

        this.$confirm({
          content: `是否取消选中的工单？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords.map((obj) => obj.id).join(',')
            this.loading = true
            cancelWorkOrder('ids=' + idsStr)
              .then((res) => {
                this.loading = false
                if (res.success) {
                  this.$message.success('取消工单成功')
                  this.findList()
                } else {
                  this.$message.error('取消工单失败')
                }
              }).catch((err) => {
              this.$message.error('取消工单异常')
            })
          }
        })
      } else {
        this.$message.error('尚未选中工单数据!')
      }
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    viewOrder (row) {
      this.$refs.viewOrderModal.showModal(row.id)
    },
    // 流程启动成功回调
    onStartSuccess (data) {
      this.findList()
    },
    // 流程启动失败回调
    onStartFailure (data) {
      console.log('启动失败:', data)
    },
    // 流程处理成功
    onHandleSuccess (data) {
      this.findList()
    },
    rowSelectChange () {
      this.selectRecords = this.$refs.listTable.getCheckboxRecords()
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords && selectRecords.length == 1) {
        this.selectedRow = selectRecords[0]
        this.variables['groupId'] = this.selectedRow.groupId
        this.variables['businessCode'] = this.selectedRow.orderCode
      } else {
        delete this.variables.groupId
        this.selectedRow = null
      }
      this.btnStatus.update()
    },
    // 初始化param
    initQueryParam () {
      this.queryParam = {
        searchText: '',
        status: undefined,
        workGroupId: undefined,
        startDate: moment(new Date()).format('YYYY-MM-DD'),
        endDate: moment(new Date()).format('YYYY-MM-DD'),
        pageNo: 1,
        pageSize: 10
      }
      this.dateRange = [moment(new Date(), 'YYYY-MM-DD'), moment(new Date(), 'YYYY-MM-DD')]
    },
    // 事件监听方法
    initOrderListQueryParamHandler (e) {
      this.initQueryParam()
    }
  }
}
</script>

<style></style>
