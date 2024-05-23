<template>
  <a-tabs :default-active-key="'1'" v-model="activeTab">
    <a-tab-pane key="1" tab="基本信息" class="descriptionBox">
      <a-descriptions bordered>
        <a-descriptions-item label="工单编号">{{ model.orderCode }}</a-descriptions-item>
        <a-descriptions-item label="工单名称">{{ model.orderName }}</a-descriptions-item>
        <a-descriptions-item label="工单类型">{{ model.orderType_dictText }}</a-descriptions-item>
        <a-descriptions-item label="列计划" v-if="showPanel">{{ model.planName }}</a-descriptions-item>
        <a-descriptions-item label="所属线路" v-if="showPanel">{{ model.lineName }}</a-descriptions-item>
        <a-descriptions-item label="作业车号" v-if="showPanel">{{ model.trainNo }}</a-descriptions-item>
        <a-descriptions-item label="作业班组" v-if="showPanel">{{ model.groupName }}</a-descriptions-item>
        <a-descriptions-item label="开始日期">{{ model.startTime }}</a-descriptions-item>
        <a-descriptions-item label="结束日期">{{ model.finishTime }}</a-descriptions-item>
        <a-descriptions-item label="工单状态">{{ model.orderStatus_dictText }}</a-descriptions-item>
        <a-descriptions-item label="作业进度" v-if="showPanel">{{ model.workStatus_dictText }}</a-descriptions-item>
        <a-descriptions-item label="财务项目">{{model.fdProjectCode}} - {{ model.fdProjectName }}</a-descriptions-item>
        <a-descriptions-item label="财务任务">{{ model.fdTaskCode }} - {{ model.fdTaskName }}</a-descriptions-item>
        <!-- <a-descriptions-item label="开支编码">{{ model.fdCostType }}</a-descriptions-item> -->
        <a-descriptions-item label="备注">{{ model.remark }}</a-descriptions-item>
      </a-descriptions>
    </a-tab-pane>
    <a-tab-pane key="2" tab="作业任务"  v-if="showPanel">
      <a-spin :spinning="confirmLoading">
        <div class="tableHeight">
          <vxe-table
            border
            ref="TaskListTable"
            :align="'center'"
            :data="tableDataTask"
            show-overflow="ellipsis"
            :checkbox-config="{ highlight: true, range: true }"
            :edit-config="{ trigger: 'manual', mode: 'row' }"
          >
            <vxe-table-column type="checkbox" width="40"></vxe-table-column>
            <vxe-table-column type="seq" title="任务编号" width="80"></vxe-table-column>
            <vxe-table-column field="taskType_dictText" title="任务类型" width="100"></vxe-table-column>
            <vxe-table-column field="taskName" title="任务名称" header-align="center" align="left" min-width="180">
              <template v-slot="{ row }">
                <a v-if="[1, 2, 3].includes(row.taskType) && row.taskObjId" @click.stop="viewTask(row)">{{
                  row.taskName
                }}</a>
                <a v-else-if="[6, 7, 8].includes(row.taskType)" @click.stop="viewTask(row)">{{
                  row.taskName
                }}</a>
                <span v-else>{{ row.taskName }}</span>
              </template>
            </vxe-table-column>
            <vxe-table-column
              field="taskStatus_dictText"
              title="任务状态"
              align="center"
              width="100"
            ></vxe-table-column>
            <vxe-table-column field="workTime" title="预计工时" align="center" width="100"></vxe-table-column>
            <vxe-table-column
              field="taskContent"
              title="任务要求"
              header-align="center"
              align="left"
              min-width="180"
            ></vxe-table-column>
            <vxe-table-column field="important_dictText" title="重要任务?" width="100"></vxe-table-column>
            <vxe-table-column field="method_dictText" title="作业手段" width="100"></vxe-table-column>
            <vxe-table-column
              field="trainAssetName"
              title="作业设备"
              min-width="180"
              header-align="center"
              align="left"
            >
              <template v-slot="{ row }">
                {{ getAssestName(row) }}
              </template>
            </vxe-table-column>
            <vxe-table-column field="remark" title="备注" align="left"></vxe-table-column>
            <!-- <vxe-table-column title="作业指导书" width="100">
              <template v-slot="{ row }">
                <a @click.stop="handleWorkForm(row)" v-if="row && row.isBookSteps && Number(row.isBookSteps)">查看</a>
              </template>
            </vxe-table-column> -->
          </vxe-table>
          <task-book-steps-modal ref="bookStepsModal"></task-book-steps-modal>
        </div>
      </a-spin>
    </a-tab-pane>
    <a-tab-pane key="12" tab="目标设备"  v-if="showPanel">
        <TargetDeviceListOrder :tasks="tableDataTask" :read-only="true" />
      </a-tab-pane>
    <a-tab-pane key="6" tab="作业表单"  v-if="showPanel">
      <TaskForms
        :task-forms.sync="tableDataForms"
        :tasks="tableDataTask"
        :trainNo="model.trainNo"
        :read-only="true"
        :operator="operator"
      ></TaskForms>
    </a-tab-pane>
    <a-tab-pane key="4" tab="作业物资">
      <TaskMaterials :order-type="model.orderType" :operator="-1" :task-materials.sync="tableDataMaterial" :tasks="tableDataTask" :read-only="true"></TaskMaterials>
    </a-tab-pane>
    <a-tab-pane key="5" tab="工器具"  v-if="showPanel">
      <TaskTools :task-tools.sync="tableDataTools" :tasks="tableDataTask" :read-only="true"></TaskTools>
    </a-tab-pane>
    <a-tab-pane key="9" tab="作业工时"  v-if="showPanel">
      <TaskWorkTime
        :staffArranges.sync="tableStaffArranges"
        :work-order-id="orderId"
        :tasks="tableDataTask"
        :read-only="true"
      ></TaskWorkTime>
    </a-tab-pane>
    <a-tab-pane key="3" tab="作业工位" v-if="showPanel">
      <TaskStations :stations.sync="tableDataStation" :tasks="tableDataTask" :read-only="true"></TaskStations>
    </a-tab-pane>
    <!--    <a-tab-pane key="7" tab="工艺文件">
      <TaskTechFiles :files.sync="tableDataFiles" :tasks="tableDataTask" :read-only="true"></TaskTechFiles>
    </a-tab-pane>-->
    <a-tab-pane key="10" tab="作业指导书"  v-if="showPanel">
      <TaskWorkSteps :readOnly="true" :bookSteps.sync="bookSteps" :tasks="tableDataTask"/>
    </a-tab-pane>
    <!-- <a-tab-pane key="11" tab="工单退料" v-if="model.orderType!==4">
      <OrderReturn ref="orderReturn" :tasks="tableDataTask" :read-only="true" :work-order-id="orderId" :task-materials.sync="tableDataMaterial"/>
    </a-tab-pane> -->
    <a-tab-pane key="8" tab="附件"  v-if="showPanel">
      <TaskAttached :files.sync="tableAttachedFiles" :read-only="true"></TaskAttached>
    </a-tab-pane>

    <!-- 计划任务查看 -->
    <view-plan-task-item ref="viewPlanTaskModal"></view-plan-task-item>
    <!-- 故障任务查看 -->
    <breakdown-detail-modal ref='breakdownDetail'></breakdown-detail-modal>
    <!-- 整改任务查看 -->
    <view-rectify-task-item ref="ViewRectifyTaskItem"></view-rectify-task-item>
    <!-- 委外任务查看 -->
    <outin-detail ref="outInDetail"> </outin-detail>
    <!-- 监修任务查看 -->
    <supervise-item-detail-modal ref="superviseDetail"></supervise-item-detail-modal>
  </a-tabs>
</template>

<script>
import TaskTools from '@views/tiros/dispatch/workOrder/TaskTools'
import BreakdownDetailModal from '@views/tiros/dispatch/breakdown/BreakdownDetailModal'
import TaskMaterials from '@views/tiros/dispatch/workOrder/TaskMaterials'
import OrderReturn from '@views/tiros/dispatch/workOrder/OrderReturn'
import TaskForms from '@views/tiros/dispatch/workOrder/TaskForms'
import TaskStations from '@views/tiros/dispatch/workOrder/TaskStations'
import TaskWorkSteps from '@views/tiros/dispatch/workOrder/TaskWorkSteps'
import TaskTechFiles from '@views/tiros/dispatch/workOrder/TaskTechFiles'
import TaskAttached from '@views/tiros/dispatch/workOrder/TaskAttached'
import { getWorkOrderDetail } from '@api/tirosDispatchApi'
import TaskWorkTime from '@views/tiros/group/myWork/TaskWorkTime'
import { getTaskBookSteps } from '@api/tirosGroupApi'
import TaskBookStepsModal from '@views/tiros/dispatch/workOrder/TaskBookStepsModal'
import ViewPlanTaskItem from '@views/tiros/dispatch/workOrder/ViewPlanTaskItem'
import ViewRectifyTaskItem from '@views/tiros/dispatch/workOrder/ViewRectifyTaskItem'
import OutinDetail from '@views/tiros/outsource/outin/OutinDetail'
import SuperviseItemDetailModal from '@views/tiros/outsource/modules/SuperviseItemDetailModal'
import TargetDeviceListOrder from '@/views/tiros/dispatch/workOrder/TargetDeviceListOrder.vue'
import materialUtil from '@views/tiros/util/MaterialUtil'

export default {
  name: 'ViewWorkOrderComponent',
  components: {
    TaskTools,
    TaskMaterials,
    OrderReturn,
    TaskForms,
    TaskStations,
    TaskTechFiles,
    TaskWorkSteps,
    TaskAttached,
    TaskWorkTime,
    TaskBookStepsModal,
    ViewPlanTaskItem,
    ViewRectifyTaskItem,
    BreakdownDetailModal,
    OutinDetail,
    SuperviseItemDetailModal,
    TargetDeviceListOrder
  },
  data() {
    return {
      bookSteps:[],
      operator: 0, // 0 创建编辑工单  1 核实工单  2 物料发放  3 物料确认
      confirmLoading: false,
      activeTab: '1',
      orderId: '',
      model: {},
      tableDataTask: [],
      taskMaps: {},
      tableDataStation: [],
      tableDataMaterial: [],
      tableDataTools: [],
      tableDataForms: [],
      tableDataFiles: [],
      tableAttachedFiles: [],
      tableStaffArranges: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 },
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 3 },
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 21 },
      },
    }
  },
  props: {
    businessKey: {
      type: String,
      default: null,
    },
    isReadonly: {
      type: Boolean,
      default: true,
    },
    fromFlow: {
      type: Boolean,
      default: false,
    },
    attributes: {
      type: Object,
      default: function () {
        return {}
      },
    },
  },
  computed:{
    showPanel: function () {
      return this.model.id && !(this.model.orderType === 4 || this.model.orderType === 5)
    },
    getAssestName(){
      return (row)=>{
        if (!row.equipments) {
          return ''
        }
        const name = row.equipments.map((item) => {
          return  item.trainAssetName //条件;
        }).join('，');
        return name
      }
    },
  },
  mounted() {
    if (this.businessKey) {
      this.show(this.businessKey)
    }

    if (this.attributes) {
      // 核实工单
      if (this.attributes.isVerify && this.attributes.isVerify === 1) {
        this.operator = 1
      }
      // 物料发放
      if (this.attributes.isMaterial && this.attributes.isMaterial === 1) {
        this.operator = 2
      }
      // 物料确认
      if (this.attributes.isConfirm && this.attributes.isConfirm === 1) {
        this.operator = 3
      }
      // 提交工单
      if (this.attributes.isSubmit && this.attributes.isSubmit === 1) {
        this.operator = 4
      }

      // 专业工程师检查
      if (this.attributes.isCheck && this.attributes.isCheck === 1) {
        this.operator = 6
      }

      // 关闭工单
      if (this.attributes.isClose && this.attributes.isClose === 1) {
        this.operator = 5
      }
    }

    if (this.operator === 2 || this.operator === 3) {
      this.activeTab = '4'
    }

    if (this.operator === 6) {
      this.activeTab = '6'
    }
  },
  methods: {
    show(orderId) {
      this.orderId = orderId
      this.loadWorkOrderDetail(orderId)
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    handleWorkForm(row) {
      this.confirmLoading = true
      getTaskBookSteps(row.id)
        .then((res) => {
          if (res.success) {
            this.$refs.bookStepsModal.show(res.result)
          } else {
            this.$message.error('请求数据失败!')
          }
        })
        .finally(() => (this.confirmLoading = false))
    },
    loadWorkOrderDetail(orderId) {
      getWorkOrderDetail(orderId).then((res) => {
        if (res.success) {
          this.tableStaffArranges.length = 0
          let detail = res.result
          this.model = Object.assign({}, detail)
          this.tableDataTask = detail.tasks
          this.tableDataStation = detail.workstations
          this.tableDataMaterial = detail.materials
          // 拼接分配明细
          materialUtil.joinDistributionInfo(this.tableDataMaterial)
          this.tableDataTools = detail.tools
          this.tableDataForms = detail.forms
          this.tableDataFiles = detail.techFiles
          this.tableAttachedFiles = detail.annexList
          detail.workstations.forEach((element) => {
            element.staffArrangeList.forEach((e) => {
              this.tableStaffArranges.push(e)
            })
          })

          this.bookSteps = detail.bookSteps;

          // 处理相关的任务名称
          this.tableDataTask.forEach((task) => {
            this.taskMaps['T' + task.id] = task.taskName
          })
          this.handleTaskName(this.tableDataStation)
          this.handleTaskName(this.tableDataTools)
          this.handleTaskName(this.tableDataMaterial)
          this.handleTaskName(this.tableDataFiles)
        } else {
          this.$message.warning(res.message)
        }
        this.$emit('loaded')
      })
    },
    handleTaskName(items) {
      items.forEach((item) => {
        item.taskName = this.taskMaps['T' + item.orderTaskId]
      })
    },
    viewTask(record) {
      console.log(record)
      console.log(record.taskType)
      switch (record.taskType) {
        case 1:
          this.$refs.viewPlanTaskModal.view(record)
          this.$refs.viewPlanTaskModal.title = `计划任务查看-${record.taskName}`
          break
        case 2:
          this.$refs.breakdownDetail.show(record.taskObjId)
          break
        case 3:
          this.$refs.ViewRectifyTaskItem.view(record)
          this.$refs.ViewRectifyTaskItem.title = `故障任务查看-${record.taskName}`
          break
        case 6:
          this.showOutInView(record)
          break
        case 7:
          this.showOutInView(record)
          break
        case 8:
          this.$refs.superviseDetail.showByTask(record)
          break
        default:
          break
      }
    },
    showOutInView(record){
      if (record.taskType === 6) {
        this.$refs.outInDetail.title = `委外送修任务查看-${record.taskName}`
      } else if(record.taskType === 7) {
        this.$refs.outInDetail.title = `委外送修任务查看-${record.taskName}`
      }
      this.$refs.outInDetail.showByTaskId(record.id)
    }
  },
}
</script>

<style lang="less" scoped>
.descriptionBox {
  /deep/.ant-descriptions-item-label {
    width: 10%;
    text-align: right;
  }
  /deep/.ant-descriptions-item-content {
    width: 20%;
  }
}
</style>