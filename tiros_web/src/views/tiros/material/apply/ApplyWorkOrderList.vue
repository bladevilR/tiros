<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24">
          <a-col :md="4" :sm="24">
            <a-form-item label="工单">
              <a-input placeholder="请输入名称或编号" v-model="queryParam.searchText" allow-clear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                :hidden-array="[0,1,2]"
                placeholder="请选择状态"
                dictCode="bu_material_order_status"
              />
            </a-form-item>
          </a-col>

          <!--          <a-col :md="4" :sm="24">
                      <a-form-item label="班组">
                        <j-dict-select-tag
                          v-model="queryParam.workGroupId"
                          placeholder="请选择班组"
                          dictCode="bu_mtr_workshop_group,group_name,id"
                        />
                      </a-form-item>
                    </a-col>-->
          <a-col :md="5" :sm="24">
            <a-form-item label="工单日期">
              <a-date-picker format="YYYY-MM-DD" style="width: 100%" v-model="startTime"/>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="列计划" :labelCol="{span:5}" :wrapperCol="{span: 19}">
              <a-select
                v-model="planName"
                placeholder="请选择列计划"
                :open="false"
                :showArrow="true"
                @focus="openTrainPlanModal"
                ref="planSelect"
              >
                <div slot="suffixIcon">
                  <a-icon
                    v-if="queryParam.planId"
                    @click.stop="clearValue"
                    type="close-circle"
                    style="padding-right: 3px"
                  />
                  <a-icon v-else type="ellipsis" />
                </div>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item>
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button @click="cancelWorkOrder" :disabled="!btnStatus.del">取消工单</a-button>
                <ProcessButtons
                  ref="WfButtons"
                  v-if="selectedRow"
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
                  :can-start="false"
                ></ProcessButtons>
                <!--                <a-button @click="submitOrder" v-has="'workorder:submit'">工单提交</a-button>-->
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 225px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(100vh - 225px)"
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
        <vxe-table-column field="orderType_dictText" title="工单类型" width="100"></vxe-table-column>
        <vxe-table-column field="groupName" align="left" header-align="center" title="作业工班"
                          width="150"></vxe-table-column>
        <vxe-table-column field="orderStatus_dictText" title="工单状态" width="120">
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
        <vxe-table-column field="wfStatus" align="left" header-align="center" title="当前环节"
                          width="120"></vxe-table-column>
        <vxe-table-column
          field="processCandidate"
          title="当前处理人"
          align="left"
          header-align="center"
          width="160"
        ></vxe-table-column>
        <vxe-table-column field="startTime" title="工单日期" width="120"></vxe-table-column>
        <!-- ??????下达日期 -->
        <vxe-table-column field="issuingTime" title="下发日期" width="120"></vxe-table-column>
        <vxe-table-column field="actStart" title="实际开始" width="120"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" align="left" header-align="center" width="120"></vxe-table-column>
        <vxe-table-column field="syncTime" title="消耗发送时间" width="180"></vxe-table-column>
        <vxe-table-column field="syncResult" title="消耗状态" width="120"></vxe-table-column>
        <vxe-table-column field="syncResultTime" title="消耗状态时间" width="180"></vxe-table-column>
        <!--        <vxe-table-column title="操作" width="10%" fixed="right">
                  &lt;!&ndash; workorder:change &ndash;&gt;
                  <template v-slot="{ row }">
                    <a-button type="link" @click="handleEdit(row)">核实工单</a-button>
                  </template>
                </vxe-table-column>-->
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
    <view-work-order-modal ref="viewOrderModal"></view-work-order-modal>
    <TrainPlanList ref="trainPlanModal" @ok="onSelectPlan"></TrainPlanList>

  </div>
</template>

<script>
import { getApplyWorkOrderList, submitOrderToDispatcher } from '@/api/tirosGroupApi'
import ViewWorkOrderModal from '@views/tiros/dispatch/workOrder/ViewWorkOrderModal'
import moment from 'moment'
import ProcessButtons from '@views/workflow/ProcessButtons'
import TrainPlanList from '@views/tiros/common/selectModules/TrainPlanList'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'
import { cancelWorkOrder } from '@api/tirosDispatchApi'

export default {
  name: 'ApplyWorkOrderList',
  components: { ViewWorkOrderModal, ProcessButtons ,TrainPlanList},
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
      planName:'',
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
      startTime: null,
      queryParam: {
        planId:'',
        searchText: '',
        status: null,
        // startTime: moment(new Date()).format('YYYY-MM-DD'),
        startDate: undefined,
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      tableData: [],
      allAlign: 'center',
      selectedRow: null,
      variables: {}
    }
  },
  created() {
    this.findList()
  },
  methods: {
    moment,
    findList() {
      this.loading = true
      this.queryParam.startDate = this.startTime ? moment(this.startTime).format('YYYY-MM-DD') : undefined

      let param = Object.assign({}, this.queryParam)
      if (!param.status) {
       param.status = [2, 3, 4, 5, 6, 7, 8]
      }
      getApplyWorkOrderList(param).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result.records
        }
      })
    },
    openTrainPlanModal() {
      this.$refs.trainPlanModal.showModal()
      this.$refs.planSelect.blur()
    },
    clearValue() {
      this.queryParam.planId = ''
      this.planName = ''
    },
    onSelectPlan(data) {
      data.forEach((element) => {
        this.queryParam.planId = element.id
        this.planName = element.planName
      })
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
              .map(function(obj, index) {
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
                      }
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
          }
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
      if (selectRecords && selectRecords.length == 1) {
        this.selectedRow = selectRecords[0]
      } else {
        this.selectedRow = null
      }
      this.btnStatus.update()
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
  }
}
</script>

<style scoped>
</style>