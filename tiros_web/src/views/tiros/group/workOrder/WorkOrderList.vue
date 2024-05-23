<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="工单">
              <a-input placeholder="请输入名称或编号" v-model="queryParam.searchText" allow-clear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="车辆">
              <j-dict-select-seach-tag
                v-model="queryParam.trainNo"
                placeholder="请选择"
                dictCode="bu_train_info,train_no,train_no"
              />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择状态"
                dictCode="bu_group_order_status"
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
            <a-form-item label="类型" :labelCol="{ span: 5 }" :wrapperCol="{ span: 19 }">
              <j-dict-select-tag
                :triggerChange="true"
                placeholder="请选择"
                v-model="queryParam.orderType"
                dictCode="bu_order_type"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :md="24" :sm="24">
            <a-form-item>
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button type="primary" @click="handleAdd">创建发料工单</a-button>
                <a-button @click="handleEdit" :disabled="!btnStatus.edit">编辑</a-button>
                <a-button @click="handleDel" :disabled="!btnStatus.del">删除</a-button>
                <a-button @click="suspendWorkOrder" :disabled="!btnStatus.del">暂停工单</a-button>
                <a-button @click="activityWorkOrder" :disabled="!btnStatus.del">激活工单</a-button>
<!--           班组的取消工单暂时去掉     <a-button @click="cancelWorkOrder" :disabled="!btnStatus.del">取消工单</a-button>-->
                <a-button><a :style="{fontSize: '12px'}" @click='expand = !expand'>
                更多条件 <a-icon :type="expand ? 'up' : 'down'" />
                </a></a-button>
                <ProcessButtons
                  ref="WfButtons"
                  v-if="selectedRow"
                  v-has="'work:order:workflow2'"
                  @StartSuccess="onStartSuccess"
                  @StartFailure="onStartFailure"
                  @handleSuccess="onHandleSuccess"
                  @cancelSuccess="findList"
                  :solution-code="selectedRow.orderType !== 4 ? 'WF_WORK_ORDER':'WF_MATERIAL_APPLY'"
                  :business-key="selectedRow.id"
                  :business-title="selectedRow.orderName"
                  :process-instance-id="selectedRow.processInstanceId"
                  :variables="variables"
                  :title="selectedRow.wfStatus"
                  :can-start="selectedRow.fromType===3"
                ></ProcessButtons>
                <!--   <a-button @click="submitOrder" v-has="'workorder:submit'">工单提交</a-button>-->
              </a-space>
            </a-form-item>
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
        <vxe-table-column field="lineName" title="线路" align="center" width="100"></vxe-table-column>
        <vxe-table-column field="trainNo" title="车号" align="center" width="80"></vxe-table-column>
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
        <vxe-table-column
          field="planName"
          title="列计划"
          align="left"
          width="200"
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

        <vxe-table-column field="orderStatus_dictText" title="工单状态" align="center" width="120">
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
        <vxe-table-column
          field="wfStatus"
          title="当前环节"
          align="left"
          header-align="center"
          width="120"
        ></vxe-table-column>
        <vxe-table-column
          field="processCandidate"
          title="当前处理人"
          align="left"
          header-align="center"
          width="160"
        ></vxe-table-column>
        <vxe-table-column field="startTime" title="计划开始" align="center" width="120"></vxe-table-column>
        <!-- ??????下达日期 -->
        <vxe-table-column field="issuingTime" title="下发日期" align="center" width="120"></vxe-table-column>
        <vxe-table-column field="actStart" title="实际开始" align="center" width="120"></vxe-table-column>

        <vxe-table-column field="remark" title="备注" align="left" header-align="center" width="120"></vxe-table-column>
        <!--        <vxe-table-column title="操作" width="10%" ftitle="备注"ixed="right">
                  &lt;!&ndash; workorder:change &ndash;&gt;align="left"
                  -->
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
    <WorkOrderModal ref="workOrderModal" @ok="findList"></WorkOrderModal>
    <view-work-order-modal ref="viewOrderModal"></view-work-order-modal>
  </div>
</template>

<script>
import { getWorkOrderList, submitOrderToDispatcher } from '@/api/tirosGroupApi'
import WorkOrderModal from '@views/tiros/group/workOrder/WorkOrderModal'
import ViewWorkOrderModal from '@views/tiros/dispatch/workOrder/ViewWorkOrderModal'
import moment from 'moment'
import ProcessButtons from '@views/workflow/ProcessButtons'
import { activityWorkOrder, cancelWorkOrder, delWorkOrder, suspendWorkOrder } from '@api/tirosDispatchApi'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  components: { WorkOrderModal, ViewWorkOrderModal, ProcessButtons },
  data() {
    return {
      btnStatus: new TableBtnUtil(this, 'listTable', {
        attrs: [
          {
            key: 'edit',
            judge: (e) => {
              return e.fromType===3 && e.orderStatus===0
            }
          },
        ],
      }),
      expand: false,
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
      queryParam: {
        searchText: '',
        status: null,
        orderType:undefined,
        workGroupId: this.$store.getters.userInfo.departId,
        startDate: undefined, // moment(new Date()).format('YYYY-MM-DD'),
        endDate: undefined, // moment(new Date()).format('YYYY-MM-DD'),
        trainNo: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      tableData: [],
      allAlign: 'center',
      selectedRow: null,
      variables: {},
      dateRange: [], // [moment(new Date(), 'YYYY-MM-DD'), moment(new Date(), 'YYYY-MM-DD')],
    }
  },
  created() {
    this.findList()
  },
  methods: {
    moment,
    onDateChange() {
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
    findList() {
      this.loading = true
      this.selectedRow=null;

      let param = Object.assign({}, this.queryParam)
      if (!param.status) {
        // param.status = [1, 2, 3, 4, 5, 6, 7, 8]
      }
      getWorkOrderList(param).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result.records
        }
      })
    },
    handleAdd() {
      this.$refs.workOrderModal.showModal(null)
    },
    handleEdit() {
      this.$refs.workOrderModal.showModal(this.selectedRow.id)
    },
    handleDel() {
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
          },
        })
      } else {
        this.$message.error('尚未选中工单数据!')
      }
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    viewOrder(row) {
      this.$refs.viewOrderModal.showModal(row.id)
    },
    submitOrder() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let flag = false
        // 0 未下达  1 已下达(未接受)  2  已下达(已接受)  3 已提交(工班张)  4 已关闭(调度)   5 已暂停
        selectRecords.forEach((row) => {
          // 只有状态2才能提交
          if (row.orderStatus !== 2) {
            flag = true
            return false
          }
        })
        if (flag) {
          this.$message.error('只能对未提交过的工单进行提交')
          return
        }
        this.$confirm({
          content: `是否提交选中的工单？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            submitOrderToDispatcher({ ids: idsStr, force: false })
              .then((res) => {
                if (res.success) {
                  this.$message.success('提交工单成功')
                  this.findList()
                } else {
                  if (res.code === 9001) {
                    this.$confirm({
                      content: `工单中还有任务未完成，是否强制提交工单？`,
                      okText: '确定',
                      cancelText: '取消',
                      onOk: () => {
                        this.submitOrderForce()
                      },
                    })
                  } else {
                    this.$message.error('提交工单失败：' + res.message)
                    console.error('提交工单失败:', res.message)
                  }
                }
              })
              .catch((err) => {
                this.$message.error('提交工单异常')
                console.error('提交工单异常：', err)
              })
          },
        })
      } else {
        this.$message.error('尚未选中工单数据!')
      }
    },
    submitOrderForce() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      let idsStr = selectRecords
        .map((obj, index) => {
          return obj.id
        })
        .join(',')
      submitOrderToDispatcher({ ids: idsStr, force: true })
        .then((res) => {
          if (res.success) {
            this.$message.success('强制提交工单成功')
            this.findList()
          } else {
            this.$message.error('强制提交工单失败：' + res.message)
            console.error('强制提交工单失败:', res.message)
          }
        })
        .catch((err) => {
          this.$message.error('强制提交工单异常')
          console.error('强制提交工单异常：', err)
        })
    },
    // 流程启动成功回调
    onStartSuccess(data) {
      this.findList()
    },
    // 流程启动失败回调
    onStartFailure(data) {
      console.error('流程启动失败:', data)
    },
    // 流程处理成功
    onHandleSuccess(data) {
      this.findList()
    },
    rowSelectChange() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      this.variables['groupId'] = null
      if (selectRecords && selectRecords.length == 1) {
        this.selectedRow = selectRecords[0]
        this.variables['groupId'] = this.selectedRow.groupId
        this.variables['businessCode'] = this.selectedRow.orderCode
      } else {
        this.selectedRow = null
      }
      this.btnStatus.update()
    },
    suspendWorkOrder() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let flag = false
        // 0 未下达  1 已下达(未接受)  2  已下达(已接受)  3 已提交(工班张)  4 已关闭(调度)   5 已暂停
        selectRecords.forEach((row) => {
          if (row.orderStatus == 4 || row.orderStatus ==5) {
            flag = true
            return false
          }
        })
        if (flag) {
          this.$message.error('只能对非暂停状态且没有关闭的工单进行关闭')
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
          },
        })
      } else {
        this.$message.error('尚未选中工单数据!')
      }
    },
    activityWorkOrder() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let flag = false
        // 0 未下达  1 已下达(未接受)  2  已下达(已接受)  3 已提交(工班张)  4 已关闭(调度)   5 已暂停
        selectRecords.forEach((row) => {
          if (row.orderStatus !=5) {
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
          },
        })
      } else {
        this.$message.error('尚未选中工单数据!')
      }
    },
    cancelWorkOrder() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let flag = false
        selectRecords.forEach((row) => {
          if (row.orderStatus ===4 ||row.orderStatus===9) {
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
            let idsStr = selectRecords.map((obj)=>obj.id).join(',')
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
          },
        })
      } else {
        this.$message.error('尚未选中工单数据!')
      }
    },
  },
}
</script>

<style></style>
