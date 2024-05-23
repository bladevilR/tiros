<template>
  <div na-flex-height-full>
    <na-splitter
      style="height: calc(100vh - 115px)"
      :horizontal="true"
      activeSide="top"
      :defaultSize="400"
      @resize="onResize"
    >
      <div slot="left-panel" style="overflow: hidden; position:relative" na-flex-height-full>
        <div class="table-page-search-wrapper" style="border: 0px">
          <a-form layout="inline">
            <a-row :gutter="24">
              <a-col :md="5" :sm="24">
                <a-form-item label="工单">
                  <a-input placeholder="请输入名称或编号" v-model="workOrderParam.searchText" allow-clear></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="3" :sm="24">
                <a-form-item label="车辆">
                  <j-dict-select-seach-tag
                    v-model="workOrderParam.trainNo"
                    placeholder="请选择"
                    dictCode="bu_train_info,train_no,train_no"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="24">
                <a-form-item label="工单类型">
                  <j-dict-select-tag v-model="workOrderParam.orderType" dict-code="bu_order_type"  :disabled-array="[1,2,3]"/>
                </a-form-item>
              </a-col>
              <!--          <a-col :md="4" :sm="24">
                          <a-form-item label="状态">
                            <j-dict-select-tag v-model="workOrderParam.status" placeholder="请选择状态" dictCode="bu_group_order_status" />
                          </a-form-item>
                        </a-col>-->
              <a-col :md="6" :sm="24">
                <a-form-item label="工单日期">
                  <a-range-picker v-model="dateRange" @change="onDateChange" :defaultPickerValue="defaultDateRange" />
                </a-form-item>
              </a-col>
              <a-col :md="3" :sm="24">
                <a-form-item>
                  <a-space>
                    <a-button @click="loadWorkOrderList">查询</a-button>
                  </a-space>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </div>
        <!--  工单列表start  -->
        <div na-flex-height-full>
          <vxe-table
            border
            row-id="id"
            height="auto"
            max-height="auto"
            ref="workOrderListTable"
            :align="allAlign"
            :data="workOrderTable"
            show-overflow="tooltip"
            auto-resize
            :radio-config="{ trigger: 'row', highlight: true, reserve: true }"
            @radio-change="onWorkOrderSelectChange"
          >
            <vxe-table-column type="radio" width="35"></vxe-table-column>
            <vxe-table-column field="lineName" title="线路" align="center" width="80"></vxe-table-column>
            <vxe-table-column field="trainNo" title="车号" align="center" width="70"></vxe-table-column>
            <vxe-table-column title="工单编号" width="120">
              <template v-slot="{ row }">
                <!-- :disabled="row.orderStatus !== 0" -->
                <a-button type="link" @click.stop="viewOrder(row)">{{ row.orderCode }}</a-button>
              </template>
            </vxe-table-column>
            <vxe-table-column
              field="orderName"
              title="工单名称"
              align="left"
              header-align="center"
              min-width="200"
            ></vxe-table-column>
            <vxe-table-column field="orderType_dictText" title="工单类型" align="center" width="100"></vxe-table-column>
            <!--
                    <vxe-table-column field="groupName" title="作业工班" align="center" width="150"></vxe-table-column>
            -->

            <vxe-table-column field="orderStatus_dictText" title="工单状态" align="center" width="100">
              <template v-slot="{ row }">
                <div :style="{ backgroundColor: orderStatusColor[row.orderStatus + ''], borderRadius: '4px' }">
                  {{ row.orderStatus_dictText }}
                </div>
              </template>
            </vxe-table-column>
            <vxe-table-column field="workStatus_dictText" title="作业进度" align="center" width="120">
              <template v-slot="{ row }">
                <div :style="{ backgroundColor: workStatusColor[row.workStatus + ''], borderRadius: '4px' }">
                  {{ row.workStatus_dictText }}
                </div>
              </template>
            </vxe-table-column>
            <!--        <vxe-table-column field="wfStatus" title="当前环节" align="center" width="120"></vxe-table-column>-->
            <vxe-table-column field="startTime" title="计划开始" align="center" width="100"></vxe-table-column>
            <vxe-table-column field="issuingTime" title="下发日期" align="center" width="100"></vxe-table-column>
            <vxe-table-column field="actStart" title="实际开始" align="center" width="100"></vxe-table-column>
            <vxe-table-column field="remark" title="备注" align="left" width="120"></vxe-table-column>
          </vxe-table>
        </div>
        <div style="height: 45px; background-color: #fff">
          <vxe-pager
            perfect
            :current-page.sync="queryParam.pageNo"
            :page-size.sync="queryParam.pageSize"
            :total="totalResult"
            :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
            @page-change="handlePageChange"
          ></vxe-pager>
        </div>
        <!--  工单列表end  -->
      </div>
      <div slot="right-panel" style="height: calc(100% - 20px)">
        <!--  任务列表start  -->
        <a-divider>任务列表</a-divider>
        <div v-if="selectWorkOrder" style="height: calc(100% - 38px)">
          <!--          <div style="margin-bottom: 5px">
            <a-space>
              <a-button type="primary"  v-if="selectTask" @click="taskReport">作业填报</a-button>
            </a-space>
          </div>-->
          <div style="height: calc(100% - 5px)">
            <vxe-table
              :stripe="true"
              border
              style="height: calc(100% - 5px)"
              max-height="100%"
              row-id="id"
              ref="taskListTable"
              :align="allAlign"
              :data="taskTable"
              show-overflow="tooltip"
              :radio-config="{ trigger: 'row', highlight: true, reserve: true }"
              @radio-change="onTaskSelectChange"
            >
              <vxe-table-column type="radio" width="35"></vxe-table-column>
              <vxe-table-column
                field="taskName"
                title="任务名称"
                align="left"
                min-width="180"
                header-align="center"
              ></vxe-table-column>
              <vxe-table-column field="taskStatus_dictText" title="状态" width="80"></vxe-table-column>
              <vxe-table-column field="taskType_dictText" title="任务类型" width="100"></vxe-table-column>
              <!--              <vxe-table-column field="trainNo" title="车号"  width="100"></vxe-table-column>
              <vxe-table-column field="orderCode" title="工单号"  min-width="120" ></vxe-table-column>
              <vxe-table-column field="orderName" title="工单名称" align="left" min-width="160" header-align="center"></vxe-table-column>-->
              <vxe-table-column field="method_dictText" title="作业手段" width="100"></vxe-table-column>
              <vxe-table-column field="workTime" title="预计工时" width="100"></vxe-table-column>
              <vxe-table-column
                field="taskContent"
                title="作业内容"
                align="left"
                min-width="160"
                header-align="center"
              ></vxe-table-column>
              <vxe-table-column
                field="remark"
                title="备注"
                align="left"
                min-width="160"
                header-align="center"
              ></vxe-table-column>
              <vxe-table-column title=" 任务填报">
                <template v-slot="{ row }">
                  <a-button v-if="row.taskStatus < 2" type="primary" size="small" @click="taskReport(row)">填报</a-button>
                  <a-button v-if="row.taskStatus===2" size="small" @click="taskReport(row)">查看</a-button>
                </template>
              </vxe-table-column>
            </vxe-table>
          </div>
        </div>
        <!--  任务列表end  -->
      </div>
    </na-splitter>

    <!-- 作业填报模态框     -->
    <a-modal
      :width="'100%'"
      :visible="visible"
      dialogClass="fullDialog no-footer no-title"
      :footer="null"
      @cancel="handleCancel"
      :destroyOnClose="true"
    >
<!--      <work-reports-content ref="reportsContent" @changeTaskStatus="changeTaskStatus"></work-reports-content>-->
      <workReportContent_WF ref="reportsContent" @close="closeHandler" @changeTaskStatus="changeTaskStatus"></workReportContent_WF>
    </a-modal>
    <view-work-order-modal ref="viewOrderModal"></view-work-order-modal>
  </div>
</template>

<script>
import moment from 'moment'
import { getWorkOrderList, getWorkTaskList } from '@api/tirosGroupApi'
import workReportContent_WF from '@views/tiros/group/myWork/workReportContent_WF'
import ViewWorkOrderModal from '@views/tiros/dispatch/workOrder/ViewWorkOrderModal'

export default {
  name: 'workReports',
  components: { ViewWorkOrderModal, workReportContent_WF },
  computed: {
    readOnly: function () {
      return this.selectTask && this.selectTask.taskStatus === 2
    },
  },
  data() {
    return {
      visible: false,
      queryParam: {
        planId: null,
        planName: undefined,
        startDate: null,
        status: null,
        taskName: null,
        orderName: undefined,
        workOrderId: null,
        groupId: this.$store.getters.userInfo.departId,
      },
      workOrderParam: {
        searchText: '',
        status: 8,
        workGroupId: this.$store.getters.userInfo.departId,
        startDate: undefined, // moment(new Date()).format('YYYY-MM-DD'),
        endDate: undefined, //moment(new Date()).format('YYYY-MM-DD'),
        trainNo: '',
        orderType:'',
        isWrite: true,
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      loading: false,
      loadingTask: false,
      curDepartId: this.$store.getters.userInfo.departId,
      dateRange: [], //[moment(new Date(), 'YYYY-MM-DD'), moment(new Date(), 'YYYY-MM-DD')]
      selectTask: null,
      selectWorkOrder: null,
      allAlign: 'center',
      workOrderTable: [],
      taskTable: [],
      orderStatusColor: {
        0: '#dedede',
        1: '#b9caa7',
        2: '#bad795',
        3: '#eaaca5',
        4: '#b1cac0',
        5: '#e2b38c',
      },
      workStatusColor: {
        0: '#dedede',
        1: '#d4efba',
        2: '#bbdeec',
      },
      timeout: null
    }
  },
  created() {
    this.loadWorkOrderList()
  },
  methods: {
    viewOrder(row) {
      this.$refs.viewOrderModal.showModal(row.id)
    },
    //工单选择改变
    onWorkOrderSelectChange({ row }) {
      this.queryParam.workOrderId = row.id
      this.selectWorkOrder = row
      this.loadTaskList()
    },
    //任务选择改变
    onTaskSelectChange({ row }) {
      this.selectTask = row
      this.readed = false
    },

    //加载工单列表
    loadWorkOrderList() {
      this.loading = true
      getWorkOrderList(this.workOrderParam)
        .then((res) => {
          if (res.success) {
            this.totalResult = res.result.total
            this.workOrderTable = res.result.records
            this.taskTable = []
            this.selectWorkOrder = null
            this.$refs.workOrderListTable.clearAll()
          }
        })
        .finally(() => (this.loading = false))
    },
    // 加载任务列表
    loadTaskList() {
      this.loadingTask = true
      this.selectTask = null
      if (this.$store.getters.userInfo.username === 'admin') {
        this.queryParam.groupId = null
      }
      getWorkTaskList(this.queryParam)
        .then((res) => {
          if (res.success) {
            this.taskTable = res.result
            if (this.taskTable && this.taskTable.length > 0) {
              this.$refs.taskListTable.setRadioRow(this.taskTable[0])
              this.selectTask = this.taskTable[0]
            }
          } else {
            this.$message.error(res.message)
          }
        })
        .finally(() => (this.loadingTask = false))
    },
    changeTaskStatus(data) {
      for (let i = 0; i < this.taskTable.length; i++) {
        if (this.taskTable[i].id == this.selectTask.id) {
          this.taskTable[i].taskStatus_dictText = data.text
          this.taskTable[i].taskStatus = data.status
          break
        }
      }
      if (data.status === 2) {
        this.visible=false
      }
    },
    closeHandler (status) {
      this.visible = false
      if (status === "ok") {
        this.loadWorkOrderList()
      }
    },
    onDateChange() {
      if (this.dateRange[0]) {
        this.workOrderParam.startDate = this.dateRange[0].format('YYYY-MM-DD')
      } else {
        this.workOrderParam.startDate = null
      }
      if (this.dateRange[1]) {
        this.workOrderParam.endDate = this.dateRange[1].format('YYYY-MM-DD')
      } else {
        this.workOrderParam.endDate = null
      }
    },
    handlePageChange({ currentPage, pageSize }) {
      this.workOrderParam.pageNo = currentPage
      this.workOrderParam.pageSize = pageSize
      this.loadWorkOrderList()
    },
    taskReport(row) {
      if (this.selectWorkOrder.orderStatus === 5) {
        this.$message.warn('工单当前是暂停状态，不能进行填报，请先激活')
        return
      }

      this.visible = true
      this.selectTask = row
      this.$nextTick(() => {
        this.$refs.reportsContent.selectTask = this.selectTask;
        this.$refs.reportsContent.orderDetail = this.selectWorkOrder;
        this.$refs.reportsContent.onTaskSelectChange()
      })
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    onResize() {
      // delay(() => {
      //   this.$refs.workOrderListTable.recalculate().then(res => {
      //     console.log('aa啊啊 ');
      //   })
      // }, 500)
    },
  },
}

const delay = (function () {
 let timer = 0
 return function (callback, ms) {
  clearTimeout(timer)
  timer = setTimeout(callback, ms)
 }
})()
</script>

<style scoped lang="less">
</style>