<template >
  <div id="workOrderItemContent" na-flex-height-full>
    <a-tabs
      :default-active-key="'1'"
      v-model="activeTab"
      @change="onTabChange"
      @tabClick="onTabClick"
      na-flex-height-full
    >
      <a-space slot="tabBarExtraContent" v-if="!fromFlow" na-flex-height-full na-fd-irow>
        <a-button :loading="confirmLoading" type="primary" class="iconBtn" @click="saveAndStart(false)">保存</a-button>
        <a-button :loading="confirmLoading" class="iconBtn" @click="saveAndStart(true)" title="保存并下发">保存并下发</a-button
        >
        <!--        <a-button class="iconBtn" @click="handleCancel">返回</a-button>-->
      </a-space>
      <a-tab-pane key="1" tab="基本信息">
        <a-form :form="basicInfoForm" style="height: calc(100vh - 225px)">
          <a-row>
            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="列计划">
                <div @click="openPlanModel">
                  <a-select
                    allow-clear
                    placeholder="请选择列计划"
                    :disabled="generate === 1"
                    :open="false"
                    :showArrow="true"
                    v-decorator="['planName', validatorRules.planName]"
                    ref="myPlanSelect"
                    @change="changePlanSelect"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                </div>

                <!-- <j-dict-select-tag
                  placeholder="选择列计划"
                  :triggerChange="true"
                  v-decorator="['planId', validatorRules.planId]"
                  dictCode="bu_order_type"
                  @select="onSelectOrderType"
                /> -->
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工单编号">
                <a-input disabled :value="orderCode" placeholder="自动生成无需填写"></a-input>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
                <j-dict-select-tag
                  :triggerChange="true"
                  v-decorator="['lineId', validatorRules.lineId]"
                  dictCode="bu_mtr_line,line_name,line_id"
                  :disabled="generate === 1"
                  @change="onLineChange"
                  @select="onLineSelect"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车号">
                <j-dict-select-seach-tag
                  :triggerChange="true"
                  :disabled="generate === 1"
                  v-decorator="['trainNo', validatorRules.trainNo]"
                  :dictCode="dictTrainStr"
                  @select="onTrainNoSelect"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工单名称">
                <a-input v-decorator="['orderName', validatorRules.orderName]"></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工单类型">
                <j-dict-select-tag
                  :triggerChange="true"
                  :disabled="generate === 1 || oType !== -1"
                  v-decorator="['orderType', validatorRules.orderType]"
                  dictCode="bu_order_type"
                  @select="onSelectOrderType"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属车间">
                <j-dict-select-tag
                  :triggerChange="true"
                  v-decorator="['workshopId', validatorRules.workshopId]"
                  dictCode="bu_mtr_workshop,name,id"
                  @select="onWorkshopSelect"
                />
              </a-form-item>
            </a-col>

            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作业班组">
                <j-dict-select-tag
                  :triggerChange="true"
                  v-decorator="['groupId', validatorRules.groupId]"
                  :dictCode="dictGroupStr"
                  @select="onGroupSelect"
                />
              </a-form-item>
            </a-col>

            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开始日期">
                <a-date-picker
                  format="YYYY-MM-DD"
                  style="width: 100%"
                  v-decorator.trim="['startTime', validatorRules.startTime]"
                  @change="changeStartDate"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结束日期">
                <a-date-picker
                  format="YYYY-MM-DD"
                  style="width: 100%"
                  v-decorator.trim="['finishTime', validatorRules.finishTime]"
                  :disabled-date="disabledEndDate"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="财务项目">
                <!-- <a-input v-decorator="['fdProject', validatorRules.fdProject]"></a-input> -->
                <FinanceSelect type="project" @change="financeSelectCHange" v-decorator="['fdProject', validatorRules.fdProject]" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="财务任务">
                <!-- <a-input v-decorator="['fdTask', validatorRules.fdTask]"></a-input> -->
                <!-- {{financeSelectId}} -->
                <FinanceSelect :id="financeSelectId" type="task" v-decorator="['fdTask', validatorRules.fdTask]" />
              </a-form-item>
            </a-col>
            <!-- <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开支编码">
                <a-input v-decorator="['fdCostType', validatorRules.fdCostType]"></a-input>
              </a-form-item>
            </a-col> -->
          </a-row>
          <a-row>
            <a-col>
              <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="备注说明">
                <a-textarea :auto-size="{ minRows: 3 }" v-decorator="['remark', validatorRules.remark]" />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </a-tab-pane>
      <a-tab-pane key="2" tab="作业任务" style="height: calc(100% - 5px)" v-if="orderType !== 4">
        <!-- 核实时也不能 修改任务  || operator === 1  -->
        <div class="buttonDiv" v-if="operator === 0">
          <a-space style="padding-bottom: 5px">
            <a-button type="dashed" class="primary-color" @click="handleAddTask">新增</a-button>
            <a-button type="dashed" @click="handleDelTask">删除</a-button>
          </a-space>
        </div>
        <div style="height: calc(100% - 45px)">
          <vxe-table
            border
            resizable
            ref="TaskListTable"
            max-height="100%"
            :align="allAlign"
            :data="tableDataTask"
            show-overflow="tooltip"
            :checkbox-config="{ highlight: true, range: true }"
            :edit-config="{ trigger: 'manual', mode: 'row' }"
          >
            <vxe-table-column type="checkbox" width="40"></vxe-table-column>
            <vxe-table-column type="seq" title="任务编号" width="80"></vxe-table-column>
            <vxe-table-column field="taskType_dictText" title="任务类型" width="100"></vxe-table-column>
            <vxe-table-column field="taskName" title="任务名称" header-align="center" align="left" min-width="180">
              <template v-slot="{ row }">
                <a v-if="[1, 2, 3].includes(parseInt(row.taskType)) && row.taskObjId" @click.stop="viewTask(row)">{{
                  row.taskName
                }}</a>
                <a v-else-if="[6, 7].includes(parseInt(row.taskType))" @click.stop="viewTask(row)">{{
                  row.taskName
                }}</a>
                <a v-else-if="[8].includes(parseInt(row.taskType)) && operator === 4" @click.stop="viewTask(row)">{{
                  row.taskName
                }}</a>
                <span v-else>{{ row.taskName }}</span>
              </template>
            </vxe-table-column>
            <vxe-table-column field="workTime" title="预计工时" align="right" width="100"></vxe-table-column>
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
              title="目标设备"
              min-width="180"
              header-align="center"
              align="left"
            >
              <template v-slot="{ row }">
                <!--   -->
                {{ getAssestName(row) }}
              </template>
            </vxe-table-column>
            <vxe-table-column field="remark" title="备注" header-align="center" align="left"></vxe-table-column>
            <!-- 核实时也不能编辑任务 || operator === 1 -->
            <vxe-table-column title="操作" width="70" v-if="operator === 0 ||  operator === 1">
              <template v-slot="{ row }">
                <a @click.stop="handleEditTask(row)">编辑</a>&nbsp; &nbsp;
                <!--                  <a @click="linkForm(row)">关联表单</a>-->
              </template>
            </vxe-table-column>
          </vxe-table>
        </div>
      </a-tab-pane>
      <a-tab-pane key="12" tab="目标设备" v-if="orderType !== 4">
        <TargetDeviceListOrder
          ref="targetDeviceListOrder"
          :train-no="trainNo"
          :lineId="lineId"
          :tasks.sync="tableDataTask"
          :select-task="selectTask"
          :forms="tableDataForms"
          :operator="operator"
        />
      </a-tab-pane>
      <a-tab-pane key="6" tab="作业表单" v-if="orderType !== 4">
        <TaskForms
          ref="taskFormsRef"
          :workOrderInfo="workOrderInfo"
          :task-forms.sync="tableDataForms"
          :tasks="tableDataTask"
          :planId="planId"
          :repairId="repairProgramId"
          :operator="operator"
          :train-no="trainNo"
        ></TaskForms>
      </a-tab-pane>
      <a-tab-pane key="4" tab="作业物料" na-flex-height-full>
        <TaskMaterials
          ref="material"
          v-if="model"
          :work-order-id="orderId"
          :task-materials.sync="tableDataMaterial"
          :tasks="tableDataTask"
          :lineId="lineId"
          :repairId="repairProgramId"
          :group-id="groupId"
          :select-task="selectTask"
          :operator="operator"
          :order-type="orderType"
          @ok="materialSaveOk"
          @fail="materialSaveFail"
          @cancel="onCancel"
          @loaded="onLoaded"
        ></TaskMaterials>
      </a-tab-pane>
      <a-tab-pane key="5" tab="工器具" v-if="orderType !== 4">
        <TaskTools
          ref="taskToolsRef"
          :task-tools.sync="tableDataTools"
          :tasks="tableDataTask"
          :select-task="selectTask"
          :forms="tableDataForms"
          :operator="operator"
        ></TaskTools>
      </a-tab-pane>
      <a-tab-pane key="3" tab="作业工位" v-if="(operator === 0 || operator === 1) && orderType !== 4">
        <TaskStations
          ref="taskStationsRef"
          :stations.sync="tableDataStation"
          :tasks="tableDataTask"
          :select-task="selectTask"
          :groupId="groupId"
          :operator="operator"
          @selectUser="onSelectUser"
        ></TaskStations>
      </a-tab-pane>
      <a-tab-pane key="9" tab="作业工时" v-if="operator === 4 && orderType !== 4">
        <TaskWorkTime
          ref="taskWorkTimeRef"
          :staffArranges.sync="tableStaffArranges"
          :work-order-id="orderId"
          :tasks="tableDataTask"
          :operator="operator"
          @ok="onSaveTimeSuccess"
        ></TaskWorkTime>
      </a-tab-pane>
      <!--      <a-tab-pane key="7" tab="工艺文件">
        <TaskTechFiles
          :files.sync="tableDataFiles"
          :tasks="tableDataTask"
          :select-task="selectTask"
          :operator="operator"
        ></TaskTechFiles>
      </a-tab-pane>-->
      <a-tab-pane key="10" tab="作业指导书" v-if="orderType !== 4">
        <TaskWorkSteps :bookSteps.sync="bookSteps" :businessKey="businessKey" :tasks="tableDataTask" />
      </a-tab-pane>
      <!--   工班不允许退料   <a-tab-pane key="11" tab="工单退料" v-if="orderType !== 4 && operator !== 0 && operator !== 1">
        <OrderReturn
          ref="orderReturn"
          :tasks="tableDataTask"
          :select-task="selectTask"
          :work-order-id="orderId"
          :task-materials.sync="tableDataMaterial"
        />
      </a-tab-pane>-->
      <a-tab-pane key="8" tab="附件" v-if="orderType !== 4">
        <TaskAttached
          ref="taskAttachedRef"
          :files.sync="tableAttachedFiles"
          :tasks="tableDataTask"
          :workOrderId="orderId"
          :operator="operator"
        ></TaskAttached>
      </a-tab-pane>
    </a-tabs>
    <task-item-modal
      ref="taskModal"
      @addTask="onAddTask"
      @editTask="onEditTask"
      @addStation="onAddStation"
      :operator="operator"
      :line-id="lineId"
      :train-no="trainNo"
      :group-id="groupId"
      :structure-id="structureId"
      :orderType="orderType"
    ></task-item-modal>
    <a-modal
      title="工单提交确认"
      centered
      :width="500"
      :visible="orderSubmitVisible"
      :confirmLoading="confirmLoading"
      @ok="onConfirmSubmit"
      @cancel="onCancelSubmit"
      :destroyOnClose="true"
    >
      <OrderConfirmForm ref="orderConfirm" :forms="tableDataForms" @ok="onConfirmSaveOk" @fail="onConfirmSaveFail"></OrderConfirmForm>
    </a-modal>
    <!-- 计划任务查看 -->
    <view-plan-task-item ref="viewPlanTaskModal"></view-plan-task-item>
    <!-- 故障任务查看 -->
    <breakdown-detail-modal ref="breakdownDetail"></breakdown-detail-modal>
    <!-- 整改任务查看 -->
    <view-rectify-task-item ref="ViewRectifyTaskItem"></view-rectify-task-item>
    <train-plan-list ref="planModalForm" @ok="onSelectPlan"></train-plan-list>
    <!-- 委外任务查看 -->
    <outin-detail ref="outInDetail"> </outin-detail>
    <!-- 监修任务查看 -->
    <supervise-item-detail-modal ref="superviseDetail"></supervise-item-detail-modal>
  </div>
</template>

<script>
import TaskItemModal from '@views/tiros/dispatch/workOrder/TaskItemModal'
import TaskTools from '@views/tiros/dispatch/workOrder/TaskTools'
import TaskMaterials from '@views/tiros/dispatch/workOrder/TaskMaterials'
import OrderReturn from '@views/tiros/dispatch/workOrder/OrderReturn'
import TaskForms from '@views/tiros/dispatch/workOrder/TaskForms'
import TaskStations from '@views/tiros/dispatch/workOrder/TaskStations'
import TaskTechFiles from '@views/tiros/dispatch/workOrder/TaskTechFiles'
import TaskWorkSteps from '@views/tiros/dispatch/workOrder/TaskWorkSteps'
import TaskAttached from '@views/tiros/dispatch/workOrder/TaskAttached'
import TaskWorkTime from '@views/tiros/group/myWork/TaskWorkTime'
import OrderConfirmForm from '@views/tiros/dispatch/workOrder/OrderConfirmForm'
import ViewPlanTaskItem from '@views/tiros/dispatch/workOrder/ViewPlanTaskItem'
import BreakdownDetailModal from '@views/tiros/dispatch/breakdown/BreakdownDetailModal'
import ViewRectifyTaskItem from '@views/tiros/dispatch/workOrder/ViewRectifyTaskItem'
import TrainPlanList from '@/views/tiros/common/selectModules/TrainPlanList'
import OutinDetail from '@views/tiros/outsource/outin/OutinDetail'
import SuperviseItemDetailModal from '@views/tiros/outsource/modules/SuperviseItemDetailModal'
import TargetDeviceListOrder from '@/views/tiros/dispatch/workOrder/TargetDeviceListOrder.vue'

import moment from 'moment'
import { addWorkOrder, editWorkOrder, getWorkOrderDetail, getTrainPlanDetail } from '@api/tirosDispatchApi'
import { submitOrderToDispatcher, getOutTaskInfo, getTaskRelevanceInfo } from '@api/tirosGroupApi'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { randomUUID } from '@/utils/util'
import { getStockSum, getGroupStockSum } from '@api/tirosMaterialApi'
import FinanceSelect from '@/views/tiros/common/finance/index.vue'

export default {
  name: 'EditWorkOrderComponent',
  components: {
    FinanceSelect,
    TaskItemModal,
    TaskTools,
    TaskMaterials,
    TaskForms,
    TaskStations,
    TaskWorkSteps,
    TaskTechFiles,
    LineSelectList,
    TaskAttached,
    TaskWorkTime,
    OrderConfirmForm,
    ViewPlanTaskItem,
    BreakdownDetailModal,
    ViewRectifyTaskItem,
    TrainPlanList,
    OutinDetail,
    SuperviseItemDetailModal,
    OrderReturn,
    TargetDeviceListOrder,
  },
  props: {
    businessKey: {
      type: String,
      default: null,
    },
    isReadonly: {
      type: Boolean,
      default: false,
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
    oType: {
      // 1 计划工单  2 故障工单  3  临时工单  4  领料工单
      type: Number,
      default: -1,
    },
    fromType: {
      // 1  自动生成  2 调度创建   3   班组创建
      type: [Number, String],
      default: 2,
    },
  },
  data() {
    return {
      repairProgramId:'',
      equipments:[],
      financeSelectId:'',
      bookSteps: [],
      model: null,
      orderSubmitVisible: false,
      confirmLoading: false,
      dictGroupStr:
        "bu_mtr_workshop_group,group_name,id,workshop_id='" +
        this.$store.getters.userInfo.workshopId +
        "'|workshop_id,monitor",
      orderId: null,
      orderType: null,
      orderFromType: 2,
      trainTypeId: '',
      lineId: '',
      trainNo: '',
      orderCode: '',
      structureId: '',
      groupId: '',
      monitorId: '',
      dictTrainStr: '',
      activeTab: '1',
      selectTask: null,
      generate: null,
      tableDataTask: [],
      taskMaps: {},
      tableDataStation: [],
      tableDataMaterial: [],
      tableDataTools: [],
      tableDataForms: [],
      tableDataFiles: [],
      tableAttachedFiles: [],
      tableStaffArranges: [],
      workOrderInfo: {},
      allAlign: 'center',
      basicInfoForm: this.$form.createForm(this),
      planId: '',
      validatorRules: {
        orderType: { rules: [{ required: true, message: '请选择工单类型!' }] },
        orderName: {
          rules: [
            { required: true, message: '请输入工单名称!' },
            { max: 100, message: '输入长度不能超过100字符!' },
          ],
        },
        // planName: { rules: [{ required: true, message: '请选择列计划!' }] },
        groupId: { rules: [{ required: true, message: '请选择作业班组!' }] },
        workshopId: { rules: [{ required: true, message: '请选择所属车间!' }] },
        lineId: { rules: [{ required: true, message: '请选择线路!' }] },
        planName: { rules: [{ required: true, message: '请选择列计划!' }] },
        trainNo: { rules: [{ required: true, message: '请选择车号!' }] },
        startTime: { rules: [{ required: true, message: '请选择开始日期!' }] },
        finishTime: { rules: [{ required: true, message: '请选择结束日期!' }] },
        fdProject: { rules: [{ max: 64, message: '输入长度不能超过64字符!' }] },
        fdTask: { rules: [{ max: 64, message: '输入长度不能超过64字符!' }] },
        fdCostType: { rules: [{ max: 64, message: '输入长度不能超过64字符!' }] },
        remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] },
      },
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
      flwOpts: { vars: {} },
      operator: 0, // 0 创建编辑工单  1 核实工单  2 物料发放  3 物料确认 4 提交工单
    }
  },
  computed:{
    getAssestName(){
      return (row)=>{
        // console.log(row)
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
      this.edit(this.businessKey)
    } else {
      this.edit(null)
    }
    this.operator = 0
    this.readOnly = false
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

    if (this.operator === 1 || this.operator === 2 || this.operator === 3) {
      this.activeTab = '4'
    }

    if (this.operator === 6) {
      this.activeTab = 6
    }

    if (this.fromType) {
      this.orderFromType = this.fromType
    }
  },
  methods: {
    moment,
    financeSelectCHange(value){
      this.financeSelectId = value;
      this.basicInfoForm.setFieldsValue({fdTask:undefined});
    },
    viewTask(record) {
      switch (parseInt(record.taskType)) {
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
    openPlanModel() {
      if (this.generate === 1) {
        return
      }
      this.$refs.planModalForm.showModal()
    },
    onSelectPlan(data) {
      // console.log(data)
      this.basicInfoForm.setFieldsValue({
        planName: data[0].planName,
        lineId: data[0].lineId,
        trainNo: data[0].trainNo,
      })
      this.onLineSelect(data[0].lineId)
      this.planId = data[0].id
      // console.log(232323)
      this.getTrainPlanDetail()
    },
    getTrainPlanDetail(){
      if(!this.planId) return;
      // 获取计划信息及任务信息
      getTrainPlanDetail(this.planId).then((res) => {
        if(res.success && res.result){
          this.repairProgramId = res.result.repairProgramId
        }
      })
    },
    changePlanSelect(value) {
      if (!value) {
        this.basicInfoForm.setFieldsValue({
          planName: '',
        })
        this.planId = ''
      }
    },
    edit(orderId) {
      this.orderId = orderId
      this.activeTab = '1'
      this.selectTask = null
      if (this.orderId) {
        getWorkOrderDetail(this.orderId).then((res) => {
          if (res.success) {
            let detail = res.result
            this.basicInfoForm.resetFields()
            this.orderType = detail.orderType // 转成字符型
            this.orderFromType = detail.fromType

            this.$nextTick(() => {
              this.trainTypeId = ''
              this.lineId = detail.lineId
              this.trainNo = detail.trainNo
              this.structureId = ''
              this.groupId = detail.groupId
              this.monitorId = detail.monitor || ''
              this.orderCode = detail.orderCode
              this.generate = detail.generate
              this.bookSteps = detail.bookSteps

              if(detail.fdProject){
                this.financeSelectId = detail.fdProject
              }

              this.basicInfoForm.setFieldsValue({
                orderType: detail.orderType,
                orderName: detail.orderName,
                groupId: detail.groupId,
                workshopId: detail.workshopId,
                lineId: detail.lineId,
                trainNo: detail.trainNo,
                planName: detail.planName,
                startTime: moment(detail.startTime || new Date(), 'YYYY-MM-DD'),
                finishTime: moment(detail.finishTime || new Date(), 'YYYY-MM-DD'),
                fdProject: detail.fdProject,
                fdTask: detail.fdTask,
                fdCostType: detail.fdCostType,
                remark: detail.remark,
              })

              this.planId = detail.planId
              this.getTrainPlanDetail()

              this.tableDataTask = detail.tasks
              this.tableDataStation = detail.workstations
              this.tableDataMaterial = detail.materials
              this.equipments = detail.equipments
              this.tableDataTools = detail.tools
              this.tableDataForms = detail.forms
              this.tableDataFiles = detail.techFiles
              this.tableAttachedFiles = detail.annexList
              this.updateStaffArranges()

              // 工单核实状态中使用的工单信息对象
              this.workOrderInfo.planName = detail.planName
              this.workOrderInfo.planId = detail.planId
              this.workOrderInfo.lineId = detail.lineId
              this.workOrderInfo.trainNo = detail.trainNo
              this.workOrderInfo.orderId = this.orderId
              this.workOrderInfo.staffArranges = this.tableDataStation

              // 处理相关的任务名称
              this.tableDataTask.forEach((task) => {
                this.taskMaps['T' + task.id] = task.taskName
              })
              this.handleTaskName(this.tableDataStation)
              this.handleTaskName(this.tableDataTools)
              this.handleTaskName(this.tableDataMaterial)
              this.handleTaskName(this.tableDataFiles)
              this.handleTaskName(this.tableAttachedFiles)

              this.model = Object.assign({}, detail)

              if (this.model.lineId) {
                this.dictTrainStr =
                  'bu_train_info,train_no,train_no,line_id=' + this.model.lineId + '|train_type_id, train_struct_id'
              }
            })
          } else {
            this.$message.warning(res.message)
          }

          if (this.operator != 2 && this.operator != 3) {
            // 不是发料和和领料，表中已加载完成
            this.$emit('loaded')
          }
        })
      } else {
        this.orderType = null
        if (this.oType !== -1) {
          this.orderType = this.oType
          this.basicInfoForm.setFieldsValue({
            orderType: this.orderType,
          })
        }

        this.trainTypeId = ''
        this.lineId = ''
        this.trainNo = ''
        this.structureId = ''
        this.groupId = ''
        if (this.fromType === 3 && this.$store.getters.userInfo.departIsGroup) {
          this.groupId = this.$store.getters.userInfo.departId
          this.basicInfoForm.setFieldsValue({
            groupId: this.groupId,
          })
        }
        this.monitorId = ''
        this.basicInfoForm.setFieldsValue({
          workshopId: this.$store.getters.userInfo.workshopId,
        })
        this.tableDataTask = []
        this.tableDataStation = []
        this.tableDataMaterial = []
        this.tableDataTools = []
        this.tableDataForms = []
        this.tableDataFiles = []
        this.tableAttachedFiles = []
        this.tableStaffArranges = []
        this.model = {}

        if (this.operator != 2 && this.operator != 3) {
          // 不是发料和和领料，表中已加载完成
          this.$emit('loaded')
        }
      }
    },
    onLoaded (data) {
      // 发料和领料的相关数据已加载了
      this.$emit('loaded')
    },
    createFaultOrder(orderInfo, tasks) {
      // console.log('order:', orderInfo, tasks)
      this.orderType = null
      this.trainTypeId = ''
      this.lineId = ''
      this.trainNo = ''
      this.structureId = ''
      this.groupId = ''
      this.monitorId = ''

      Object.assign(this, orderInfo)

      this.basicInfoForm.setFieldsValue({
        planName: orderInfo.planName,
        orderName: orderInfo.orderName,
        orderType: orderInfo.orderType,
        lineId: orderInfo.lineId,
        trainNo: orderInfo.trainNo,
        workshopId: orderInfo.workshopId,
        startTime: moment(orderInfo.startTime || new Date(), 'YYYY-MM-DD'),
        finishTime: moment(orderInfo.finishTime || new Date(), 'YYYY-MM-DD'),
      })

      this.planId = orderInfo.planId

      this.getTrainPlanDetail()

      this.tableDataTask = tasks
      this.tableDataStation = []
      this.tableDataMaterial = []
      this.tableDataTools = []
      this.tableDataForms = []
      this.tableDataFiles = []

      this.$emit('loaded')
    },
    handleTaskName(items) {
      items.forEach((item) => {
        item.taskName = this.taskMaps['T' + item.orderTaskId]
      })
    },
    saveAndStart(start) {
      if (this.operator === 0) {
        this.basicInfoForm.validateFields((err, values) => {
          if (!err) {
            let formData = Object.assign(this.model, { startFlow: start, fromType: this.orderFromType })

            Object.assign(formData, values, {
              tasks: this.tableDataTask,
              workstations: this.tableDataStation,
              materials: this.tableDataMaterial,
              equipments: this.equipments,
              tools: this.tableDataTools,
              forms: this.tableDataForms,
              techFiles: this.tableDataFiles,
              annexList: this.tableAttachedFiles,
              bookSteps: this.bookSteps,
              monitor: this.monitorId,
              planId: this.planId,
              finishTime: moment(values.finishTime).format('YYYY-MM-DD'),
              startTime: moment(values.startTime).format('YYYY-MM-DD'),
            })

            if (!this.checkWorkOrderInfo(formData)) {
              return;
            }

            this.saveWorkOrderInfo(formData)
          } else {
            this.$emit('fail')
            this.$message.warning('请填写完整基本信息')
          }
        })
      }
    },
    save(opts) {
      // 判断工单是否有状态，如果状态为暂停，则不能进行处理
      if (this.model && this.model.orderStatus === 5) {
        this.flwOpts.res = { message: '该工单已暂停，请先激活在进行操作！' }
        this.$emit('fail', this.flwOpts)
       /* this.$message.warning('该工单已暂停，请先激活在进行操作！')
        this.$emit('fail')*/
        return
      }
      let onlySave = opts.onlySave
      Object.assign(this.flwOpts, opts)
      // 工单编辑或者核实工单（保存工单所有数据）
      if (this.operator === 0 || this.operator === 1 || this.operator === 4 || this.operator === 5) {
        this.basicInfoForm.validateFields((err, values) => {
          if (!err) {
            let formData = Object.assign({}, this.model)
            Object.assign(formData, { fromType: this.orderFromType })
            Object.assign(formData, values, {
              tasks: this.tableDataTask,
              workstations: this.tableDataStation,
              materials: this.tableDataMaterial,
              tools: this.tableDataTools,
              forms: this.tableDataForms,
              techFiles: this.tableDataFiles,
              annexList: this.tableAttachedFiles,
              bookSteps: this.bookSteps,
              monitor: this.monitorId,
              planId: this.planId,
              finishTime: moment(values.finishTime).format('YYYY-MM-DD'),
              startTime: moment(values.startTime).format('YYYY-MM-DD'),
            })

            if (!this.checkWorkOrderInfo(formData)) {
              return;
            }

            // 0 未下达 1 已下达(未接受) 2 已下达(已接受) 3 已提交(工班张) 4 已关闭(调度) 5 已暂停
            // 核实工单
            if (this.operator === 1) {
              // 班组长核实工单时，判断工单物料中是否有非必换件的物资，设置流程变量
              if (this.orderType === 4) {
                // 发料工单所有物资都要发放
                this.flwOpts.vars.hasMaterial = this.tableDataMaterial.length > 0
              } else {
                // 非发料工单，只有needDispatchin为1的才需要发料
                this.flwOpts.vars.hasMaterial = this.tableDataMaterial.filter((m) => {
                  return m.needDispatchin === 1
                }).length
              }

              if (!onlySave) {
                //判断物料是否足够
                async function saveStep() {
                  let allPromise = this.tableDataMaterial.map((material) => {
                    // 如果物料需要发放，则表示车间仓库出，判断车间库存，如果不需要发料，表示班组仓库出，判断班组仓库库存
                    if (material.needDispatchin === 1) {
                      // 判断该物资总库存是否够
                      return getStockSum
                        .call(this, {
                          materialTypeId: material.materialTypeId,
                        })
                        .then((res) => {
                          if (res.success) {
                            if (res.result < material.amount) {
                              material.noenough = 1
                              material.stock = res.result
                              return material
                            } else {
                              material.noenough = null
                              material.stock = res.result
                              return null
                            }
                          } else {
                            material.noenough = null
                            material.stock = res.result
                            console.error(res.message)
                            return material
                          }
                        })
                    } else {
                      // 判断班组的库存
                      return getGroupStockSum({
                        groupId: this.groupId,
                        materialTypeId: material.materialTypeId,
                      }).then((res) => {
                        if (res.success) {
                          if (res.result < material.amount) {
                            material.noenough = 1
                            material.stock = res.result
                            return material
                          } else {
                            material.noenough = null
                            material.stock = res.result
                            return null
                          }
                        } else {
                          material.noenough = null
                          material.stock = res.result
                          console.error(res.message)
                          return material
                        }
                      })
                    }
                    /*
                    if (this.orderType !== 4 && material.useCategory === 1) {
                      // 非发料工单中的必换件，判断班组的库存
                      return getGroupStockSum({
                        groupId: this.groupId,
                        materialTypeId: material.materialTypeId
                      }).then(res => {
                        if (res.success) {
                          if (res.result < material.amount) {
                            material.noenough = 1
                            material.stock= res.result
                            return material
                          } else {
                            material.noenough = null
                            material.stock= res.result
                            return null
                          }
                        } else {
                          material.noenough = null
                          material.stock= res.result
                          console.error(res.message)
                          return material
                        }
                      })
                    } else {
                      // 判断该物资总库存是否够
                      return getStockSum
                        .call(this, {
                          materialTypeId: material.materialTypeId,
                        })
                        .then((res) => {
                          if (res.success) {
                            if (res.result < material.amount) {
                              material.noenough = 1
                              material.stock= res.result
                              return material
                            } else {
                              material.noenough = null
                              material.stock= res.result
                              return null
                            }
                          } else {
                            material.noenough = null
                            material.stock= res.result
                            console.error(res.message)
                            return material
                          }
                        })
                    }*/
                  })
                  return Promise.all(allPromise).then((results) => {
                    return results.filter((e) => (e ? true : false))
                  })
                }
                saveStep.call(this).then((overMaterials) => {
                  if (overMaterials.length > 0) {
                    this.$refs.material.refreshTable()
                    overMaterials.forEach((e) => {
                      if (e.needDispatchin === 0) {
                        this.$message.warn(`班组仓库中的：${e.code + ' | ' + e.name}的库存量不足`)
                      } else {
                        this.$message.warn(`车间仓库中的：${e.code + ' | ' + e.name}的库存量不足`)
                      }
                    })
                    this.activeTab = '4'
                    this.$emit('cancel', this.flwOpts)
                    return
                  } else {
                    // 物料没问题
                    // 修改工单状态为 2
                    Object.assign(formData, { orderStatus: 2 })

                    if (this.orderType !== 4) {
                      // 非领料工单
                      // region 判断是否有分配人员
                      let haveEmployee = true
                      let alertMsg = ''
                      if (!this.tableDataStation || this.tableDataStation.length === 0) {
                        alertMsg = '工单没有分配任何人员'
                        haveEmployee = false
                      }
                      if (haveEmployee && this.tableDataStation.length > 0) {
                        for (let i = 0; i < this.tableDataStation.length; i++) {
                          if (!this.tableDataStation[i].personNames) {
                            haveEmployee = false
                            alertMsg = '工单还有工位没有分配人员'
                            break
                          }
                        }
                      }
                      // endregion

                      if (!haveEmployee) {
                        // 还有未分配人员的
                        this.$confirm({
                          content: alertMsg + `，你确定继续提交？`,
                          okText: '确定',
                          cancelText: '取消',
                          onOk: () => {
                            this.saveWorkOrderInfo(formData)
                          },
                          onCancel: () => {
                            this.$emit('cancel', this.flwOpts)
                          },
                        })
                      } else {
                        // 都有分配哦
                        this.saveWorkOrderInfo(formData)
                      }
                    } else {
                      // 领料工单,直接保存，不提示没有安排人员
                      this.saveWorkOrderInfo(formData)
                    }
                  }
                })
              } else {
                // 只保存啊
                this.saveWorkOrderInfo(formData)
              }
            } else if (this.operator === 4) {
              // 提交工单
              // 只是保存
              if (onlySave) {
                // this.$emit('ok')
                this.saveWorkOrderInfo(formData)
                return
              } else {
                // 检查物料是否填写消耗, 暂时不检查了，有可能实际消耗为0
                /*
                let isTaskMaterialOk = true
                this.tableDataMaterial.forEach((e) => {
                  if (e.actAmount === 0 && e.amount !== 0 && (e.opType === 1 || e.opType === 2)) {
                    isTaskMaterialOk = false
                  }
                })
                if (!isTaskMaterialOk) {
                  this.$message.warn('物料消耗填报未完成，请检查！')
                  this.$emit('fail')
                  return
                }
                */
                // 显示工单提交确认
                this.orderSubmitVisible = true
              }
            } else if (this.operator === 5) {
              //关闭工单操作
              if (onlySave) {// 直接保存，或者审批不同意时，只做保存
                this.saveWorkOrderInfo(formData)
                return
              } else {
                //  修改工单状态为 4, 作业状态改成 2
                Object.assign(formData, { orderStatus: 4, workStatus: 2 })
                // 1 审批  2 提交  3 审批&提交
                if (opts.actionType === 2 || opts.actionType === 3) {
                  // 如果是提交，弹出提示确认
                  this.$confirm({
                    content: '你确定要关闭该工单？',
                    onOk: () => {
                      this.saveWorkOrderInfo(formData)
                    },
                    onCancel: () => {
                      this.$emit('cancel', this.flwOpts)
                    },
                  })
                } else {
                  this.saveWorkOrderInfo(formData)
                }
              }
            } else {
              this.saveWorkOrderInfo(formData)
            }
          } else {
            this.flwOpts.res = { message: '请填写完整基本信息' }
            this.$emit('fail', this.flwOpts)
          }
        })
        return
      }

      // 物料发放  ||  领用确认 （只保存物料数据）
      if (this.operator === 2 || this.operator === 3) {
        if(this.$refs.material && this.$refs.material.save) {
          this.$refs.material.save(onlySave)
          return
        } else {
          this.flwOpts.res = { message: '数据尚未加载完成，请稍后操作！' }
          this.$emit('fail', this.flwOpts)
        }
      } else {
        this.flwOpts.res = { message: '无效的操作类型，请联系关联员！' }
        this.$emit('fail', this.flwOpts)
      }
    },

    checkWorkOrderInfo (formData) {
      if (this.operator === 0 || this.operator === 1) {
        if (this.orderType === 4) {
          // 发料工单，必须有物料
          if (!formData.materials || formData.materials.length < 1) {
            this.flwOpts.res = { message: '发料工单的物料不能为空' }
            this.$emit('fail', this.flwOpts)
            this.activeTab = '4'
            return false
          }
        }

        if (this.orderType !== 4 && (!formData.tasks || formData.tasks.length < 1)) {
          // this.$message.warning('至少需要添加一条工单任务')
          this.flwOpts.res = { message: '至少需要添加一条工单任务' }
          this.$emit('fail', this.flwOpts)
          this.activeTab = '2'
          return false
        }

        // 检查物料的类别是否选择正确
        if (formData.materials && formData.materials.length > 0) {
          let noOks = formData.materials.filter(m => {
            return !!!m.useCategory || m.useCategory <= 0
          })
          if (noOks.length > 0) {
            this.flwOpts.res = { message: '工单物料类别不能为“其他”，请选择！' }
            this.$emit('fail', this.flwOpts)
            this.activeTab = '4'
            return false
          }
        }

        // region 判断任务是否有设置具体的设备
        if (this.orderType !== 4 && this.operator === 1) {

          //   必须选择设备
          // let noOks = formData.tasks.filter(t => !t.assetTypeId && !t.structDetailId && !t.trainAssetId)
          // if (noOks.length > 0) {
          //   this.flwOpts.res = { message: '工单任务必须关联“目标设备”，请点击任务的“编辑”按钮进行设置' }
          //   this.$emit('fail', this.flwOpts)
          //   this.activeTab = '2'
          //   return false
          // }

          // 必须设置工位
          /*现在物资所属工位，单独设置了，所以工单没必要单独检查了，if (!formData.workstations || formData.workstations.length < 1) {
           this.flwOpts.res = { message: '工单必须设置作业工位' }
           this.$emit('fail', this.flwOpts)
           this.activeTab = '3'
           return false
           }
          else if (formData.workstations.length > 1) {
             // 这里限制只能一个，是因为报表中心的物料消耗明细 ，如果多个工位，那么工单的物资有重复了
             this.flwOpts.res = { message: '一个工单只能设置一个作业工位' }
             this.$emit('fail', this.flwOpts)
             this.activeTab = '3'
             return false
           }
          */

          // 检查物料是否填写了所属系统和工位
          if (formData.materials && formData.materials.length > 0) {
            let noOks = formData.materials.filter(m => {
              return !!!m.systemId || m.systemId === ''
            })

            if (noOks.length > 0) {
              this.flwOpts.res = { message: '工单物料必须选择所需系统，请选择！' }
              this.$emit('fail', this.flwOpts)
              this.activeTab = '4'
              return false
            }

            noOks = formData.materials.filter(m => {
              return !!!m.workstationId || m.workstationId === ''
            })
            if (noOks.length > 0) {
              this.flwOpts.res = { message: '工单物料必须选择所需工位，请选择！' }
              this.$emit('fail', this.flwOpts)
              this.activeTab = '4'
              return false
            }
          }
        }
        // endregion
      }
      return true
    },
    saveWorkOrderInfo(formData) {
      // 处理工单任务id的问题
      if (this.orderType !== 4) {
        let taskId = formData.tasks[0].id
        this.insertTaskId(formData.materials, 'orderTaskId', taskId)
        this.insertTaskId(formData.forms, 'workOrderTaskId', taskId)
        this.insertTaskId(formData.tools, 'orderTaskId', taskId)
        this.insertTaskId(formData.workstations, 'orderTaskId', taskId)
        this.insertTaskId(formData.techFiles, 'orderTaskId', taskId)
        this.insertTaskId(formData.annexList, 'taskId', taskId)
      }
      // 工单物料中，临时新增的物资，设置planAmount 为 amount
      formData.materials.forEach((m) => {
        if (m.opType === 2) {
          m.planAmount = m.amount
        }
      })

      this.confirmLoading = true
      if (this.orderId) {
        Object.assign(formData, { id: this.orderId })
        editWorkOrder(formData)
          .then((res) => {
            this.flwOpts.res = res
            this.confirmLoading = false
            if (res.success) {
              this.handleOk()
            } else {
              this.$emit('fail', this.flwOpts)
              console.error('保存工单失败:', res.message)
            }
          })
          .catch((err) => {
            this.confirmLoading = false
            this.flwOpts.res = { message: '保存异常' }
            this.$emit('fail')
            console.error('保存工单异常:', err)
          })
      } else {
        formData['fromType'] = this.orderFromType
        addWorkOrder(formData)
          .then((res) => {
            this.flwOpts.res = res
            if (res.success) {
              this.handleOk()
            } else {
              this.confirmLoading = false
              this.$emit('fail', this.flwOpts)
              console.error('保存工单失败:', res.message)
            }
          })
          .catch((err) => {
            this.confirmLoading = false
            this.flwOpts.res = { message: '保存异常' }
            this.$emit('fail')
            console.error('保存工单异常:', err)
          })
      }
    },
    submitOrder(vars) {
      this.flwOpts.vars = vars
      submitOrderToDispatcher({ ids: this.orderId, force: false })
        .then((res) => {
          this.flwOpts.res = res
          if (res.success) {
            // this.$message.success('提交工单成功')
            this.orderSubmitVisible = false
            this.confirmLoading = false
            this.$emit('ok', this.flwOpts)
          } else {
            if (res.code === 9001) {
              this.$confirm({
                content: `工单中还有任务未完成，是否强制提交工单？`,
                okText: '确定',
                cancelText: '取消',
                onOk: () => {
                  this.submitOrderForce()
                },
                onCancel: () => {
                  this.$emit('cancel', this.flwOpts)
                },
              })
            } else {
              this.$emit('fail', this.flwOpts)
              // this.$message.error('提交工单失败：' + res.message)
              console.error('提交工单失败:', res.message)
            }
          }
        })
        .catch((err) => {
          this.flwOpts.res = { message: '提交工单异常' }
          this.$emit('fail', this.flwOpts)
          this.$message.error('提交工单异常')
          console.error('提交工单异常：', err)
        })
    },
    submitOrderForce() {
      submitOrderToDispatcher({ ids: this.orderId, force: true })
        .then((res) => {
          if (res.success) {
            this.orderSubmitVisible = false
            this.confirmLoading = false
            this.$emit('ok', this.flwOpts)
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
    insertTaskId(list, attr, taskId) {
      list.forEach((item) => {
        if (!item[attr]) {
          // 没有设置才
          item[attr] = taskId
        }
      })
    },
    onConfirmSubmit() {
      this.confirmLoading = true
      this.$refs.orderConfirm.save()
    },
    onCancelSubmit() {
      this.orderSubmitVisible = false
      this.confirmLoading = false
      this.$emit('cancel', this.flwOpts)
    },
    onConfirmSaveOk(data) {
      let vars = {
        needCheck: parseInt(data.needCheck),
        checkUserId: data.checkUserId || '',
      }

      this.confirmLoading = false
      this.submitOrder(vars)
    },
    onConfirmSaveFail() {
      this.confirmLoading = false
      this.$emit('cancel', this.flwOpts)
    },
    materialSaveOk() {
      this.$emit('ok', this.flwOpts)
    },
    materialSaveFail(e) {
      let errMsg = e && e.message ? ':' + e.message : ''
      this.flwOpts.res = { message: errMsg }
      // this.$message.error('保存失败' + errMsg)
      this.$emit('fail', this.flwOpts)
    },
    onTabChange(key) {
      this.activeTab = '1'
      this.basicInfoForm.validateFields((err, values) => {
        if (!err) {
          this.activeTab = key
        } else {
          this.$message.warning('请先填写完基本信息')
          return false
        }
      })
    },
    onTabClick(value) {
      switch (this.activeTab) {
        case '5':
          !this.$refs.taskToolsRef || this.$refs.taskToolsRef.clearValidate()
          break
        case '6':
          !this.$refs.taskFormsRef || this.$refs.taskFormsRef.clearValidate()
          break
        case '4':
          !this.$refs.material || this.$refs.material.clearValidate()
          break
        case '3':
          !this.$refs.taskStationsRef || this.$refs.taskStationsRef.clearValidate()
          break
        case '9':
          !this.$refs.taskWorkTimeRef || this.$refs.taskWorkTimeRef.clearValidate()
          break
        case '8':
          !this.$refs.taskAttachedRef || this.$refs.taskAttachedRef.clearValidate()
          break
      }
    },
    handleAddTask() {
      this.$refs.taskModal.add()
      this.$refs.taskModal.title = '新增'
    },
    handleEditTask(record) {
      this.$refs.taskModal.edit(record)
      this.$refs.taskModal.title = '编辑'
    },
    linkForm(record) {
      this.$refs.formsModal.showModal()
    },
    onAddTask(record) {
      // console.log(1)
      this.tableDataTask.push(record)
    },
    onEditTask(record) {
      let index = this.tableDataTask.findIndex((item) => item.id === record.id)
      Object.assign(this.tableDataTask[index], record)
      // console.log(this.tableDataTask)
      // console.log(this.tableDataTask)
      this.$refs.TaskListTable.loadData(this.tableDataTask)
    },
    handleDelTask() {
      let selectRecords = this.$refs.TaskListTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}条数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            this.tableDataTask = this.tableDataTask.filter((item) => !selectRecords.some((ele) => ele.id === item.id))
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    radioChangeEvent({ row }) {
      this.selectTask = row
    },
    onAddStation(record) {
      // console.log(3)
      if (!record) {
        return
      }
      // 先判断是否存在同样任务id的，存在则删除，然后添加
      let staffUserIds, personNames
      for (let i = this.tableDataStation.length - 1; i >= 0; i--) {
        let sta = this.tableDataStation[i]

        if (sta.workstationId === record.id) {
          if (sta.staffUserIds) {
            staffUserIds = sta.staffUserId
            personNames = sta.personNames
          }
        }
        if (sta.orderTaskId === record.orderTaskId) {
          this.tableDataStation.splice(i, 1)
        }
      }
      //  然后找，看有没有相同的工位id，如果有，相同的工位分配的人员复制过来
      // console.log(record)
      let data = {
        orderTaskId: record.orderTaskId,
        taskName: record.taskName,
        workstationId: record.id,
        workstationNo: record.stationNo,
        workstationName: record.name,
        staffUserIds: staffUserIds,
        personNames: personNames,
        remark: record.remark,
      }
      this.tableDataStation.push(data)
    },

    // 确定
    handleOk() {
      this.$emit('ok', this.flwOpts)
      this.close()
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
    },
    onCancel() {
      this.confirmLoading = false
      this.$emit('cancel')
    },
    onLineChange(value) {},
    onLineSelect(value, itemData) {
      this.lineId = value
      if (value) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + value + '|train_type_id, train_struct_id'
      } else {
        this.dictTrainStr = ''
      }
    },
    onTrainNoSelect(value, itemData) {
      this.trainNo = value
      if (itemData.extFields && itemData.extFields.train_type_id) {
        this.trainTypeId = itemData.extFields.train_type_id
      }
      if (itemData.extFields && itemData.extFields.train_struct_id) {
        this.structureId = itemData.extFields.train_struct_id
      }
    },
    onWorkshopSelect(value, itemData) {
      if (value) {
        this.dictGroupStr = "bu_mtr_workshop_group,group_name,id,workshop_id='" + value + "'|workshop_id,monitor"
      } else {
        this.dictGroupStr = ''
      }
    },
    onGroupSelect(value, itemData) {
      this.groupId = value
      /*if (itemData.extFields && itemData.extFields.workshop_id) {
        this.$nextTick(() => {
          this.basicInfoForm.setFieldsValue({
            workshopId: itemData.extFields.workshop_id
          })
        })
      }*/
      if (itemData.extFields && itemData.extFields.monitor) {
        this.monitorId = itemData.extFields.monitor
      }
    },
    // 开始日期改变
    changeStartDate(value) {
      if (value) {
        // 设置结束日期
        this.$nextTick(() => {
          this.basicInfoForm.setFieldsValue({
            finishTime: value,
          })
        })
      }
    },
    // 设置不可选的结束日期
    disabledEndDate(value) {
      let startValue = this.basicInfoForm.getFieldValue('startTime')
      if (!startValue || !value) {
        return false
      }
      return value < startValue
    },
    onSelectOrderType(value, itemData) {
      this.orderType = parseInt(value)
    },
    onSelectUser() {
      this.updateStaffArranges()
    },
    onSaveTimeSuccess() {
      getWorkOrderDetail(this.orderId).then((res) => {
        if (res.success) {
          let detail = res.result
          this.tableDataStation = detail.workstations
        }
      })
    },
    updateStaffArranges() {
      this.tableStaffArranges.length = 0
      if (this.tableDataStation) {
        this.tableDataStation.forEach((element) => {
          // element.staffUserIds.forEach((userItem) =>{
          //   if(element.staffArrangeList.find(e => e.userId === userItem)){
          //     // 已存在
          //     this.tableStaffArranges.push(element.staffArrangeList.find(e => e.userId === userItem))
          //   }else {
          //     // 新增
          //     let item = {
          //       id: "",
          //       ordTskWkStationId: element.id,
          //       orderId: element.orderId,
          //       orderTaskId: element.orderTaskId,
          //       planWorkTime: 0,
          //       userId: userId,
          //       userName: element.personNames.split(',')[index],
          //       workTime: 0,
          //       workstationId: element.workstationId,
          //       workstationName: element.workstationName,
          //       workstationNo: element.workstationNo
          //     }
          //     element.staffArrangeList.push(item)
          //     this.tableStaffArranges.push(item)
          //   }
          // })
          if (!element.staffUserIds) {
            element.staffUserIds = []
          }
          if (!element.staffArrangeList) {
            element.staffArrangeList = []
          }
          if (element.staffUserIds.length === element.staffArrangeList) {
            element.staffArrangeList.forEach((e) => {
              this.tableStaffArranges.push(e)
            })
          } else {
            // element.staffArrangeList.length
            element.staffUserIds.forEach((userId, index) => {
              //新增前判断
              if (element.staffArrangeList.find((e) => e.userId === userId)) {
                this.tableStaffArranges.push(element.staffArrangeList.find((e) => e.userId === userId))
              } else {
                let item = {
                  id: randomUUID(),
                  ordTskWkStationId: element.id,
                  orderId: element.orderId,
                  orderTaskId: element.orderTaskId,
                  orderTaskName: element.taskName,
                  planWorkTime: null,
                  userId: userId,
                  userName: element.personNames.split(',')[index],
                  workTime: null,
                  workstationId: element.workstationId,
                  workstationName: element.workstationName,
                  workstationNo: element.workstationNo,
                }
                element.staffArrangeList.push(item)
                this.tableStaffArranges.push(item)
              }
            })
          }
        })
      }
    },
    showOutInView(record) {
      if (record.taskType === 6) {
        this.$refs.outInDetail.title = `委外送修任务查看-${record.taskName}`
      } else if (record.taskType === 7) {
        this.$refs.outInDetail.title = `委外送修任务查看-${record.taskName}`
      }
      this.$refs.outInDetail.showByTaskId(record.id)
    },
  },
}
</script>

<style lang="less">
#workOrderItemContent {
  border: none;

  .buttonDiv {
    padding: 5px 0;
  }

  .extraDiv {
    position: absolute;
    top: 30px;
    right: 0;
    z-index: 1;
    // font-size: 16px;
    .iconBtn {
      margin-right: 10px;
      // font-size: 20px;
    }
  }
}
</style>