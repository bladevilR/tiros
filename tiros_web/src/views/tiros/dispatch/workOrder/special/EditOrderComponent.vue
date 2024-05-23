<template>
  <div id="workOrderItemContent" na-flex-height-full>
    <a-tabs
      :default-active-key="'1'"
      v-model="activeTab"
      @change="onTabChange"
      @tabClick="onTabClick"
      na-flex-height-full
    >
      <a-space slot="tabBarExtraContent" v-if="!fromFlow" na-flex-height-full na-fd-irow>
        <a-button :loading="loading" type="primary" class="iconBtn" @click="saveAndStart(false)">保存</a-button>
        <a-button :loading="loading" class="iconBtn" @click="saveAndStart(true)" title="保存并下发">保存并下发</a-button>
      </a-space>
      <a-tab-pane key="1" tab="基本信息">
        <a-form-model ref="form" :model="formModel" :rules="formRules" :label-col="deLayout.labelCol"
                      :wrapper-col="deLayout.wrapperCol">
          <a-row>
            <a-col :span="8">
              <a-form-model-item label="工单名称" prop="orderName">
                <a-input v-model="formModel.orderName"></a-input>
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="工单编号">
                <a-input disabled :value="formModel.orderCode" placeholder="自动生成无需填写"></a-input>
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="工单类型">
                <j-dict-select-tag
                  :triggerChange="true"
                  dictCode="bu_order_type"
                  v-model="formModel.orderType"
                  :disabled="true"
                />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="所属车间" prop="workshopId">
                <j-dict-select-tag
                  :triggerChange="true"
                  v-model="formModel.workshopId"
                  dictCode="bu_mtr_workshop,name,id"
                />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="所属线路" prop="lineId">
                <j-dict-select-tag
                  :triggerChange="true"
                  v-model="formModel.lineId"
                  dictCode="bu_mtr_line,line_name,line_id"
                />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="开始日期" prop="startTime">
                <a-date-picker
                  format="YYYY-MM-DD"
                  style="width: 100%"
                  v-model="formModel.startTime"
                  @change="changeStartDate"
                />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="结束日期" prop="finishTime">
                <a-date-picker
                  format="YYYY-MM-DD"
                  style="width: 100%"
                  v-model="formModel.finishTime"
                  :disabled-date="disabledEndDate"
                />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="财务项目">
                <FinanceSelect type="project" @change="financeSelectChange" v-model="formModel.fdProject" />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item label="财务任务">
                <FinanceSelect type="task" v-model="formModel.fdTask" />
              </a-form-model-item>
            </a-col>
            <a-col :span="24">
              <a-form-model-item label="备注" prop="remark" :labelCol="{sm:{span:2},xs: { span: 24 }}" :wrapperCol="{sm:{span:22},xs: { span: 24 }}">
                <a-textarea :auto-size="{ minRows: 3 }" v-model="formModel.remark" />
              </a-form-model-item>
            </a-col>
          </a-row>
        </a-form-model>
      </a-tab-pane>
      <a-tab-pane key="4" tab="作业物料" na-flex-height-full>
        <TaskMaterials
          ref="material"
          :work-order-id="formModel.id"
          :task-materials.sync="formModel.materials"
          :tasks="formModel.tasks"
          :lineId="formModel.lineId"
          :select-task="selectTask"
          :operator="operator"
          :order-type="numOrderType"
          @ok="materialSaveOk"
          @fail="materialSaveFail"
          @cancel="onCancel"
          @loaded="onLoaded"
        ></TaskMaterials>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>
import FinanceSelect from '@/views/tiros/common/finance/index.vue'
import TaskMaterials from '@views/tiros/dispatch/workOrder/TaskMaterials'
import { addWorkOrder, editWorkOrder, getWorkOrderDetail } from '@api/tirosDispatchApi'

export default {
  name: 'EditOrderComponent',
  components: { TaskMaterials, FinanceSelect },
  props: {
    businessKey: {
      type: String,
      default: null
    },
    isReadonly: {
      type: Boolean,
      default: false
    },
    fromFlow: {
      type: Boolean,
      default: false
    },
    attributes: {
      type: Object,
      default: function () {
        return {}
      }
    },
    oType: {
      // 1 计划工单  2 故障工单  3  临时工单  4  领料工单 5 车间消耗
      type: Number,
      default: 5
    },
    fromType: {
      // 1  自动生成  2 调度创建   3   班组创建  4 材料员创建
      type: [Number, String],
      default: 4
    }
  },
  computed: {
    numOrderType: function() {
      return parseInt(this.formModel.orderType)
    }
  },
  data () {
    return {
      activeTab: '1',
      operator: 0, // 0 创建编辑工单  1 核实工单  2 物料发放  3 物料确认 4 提交工单
      loading: false,
      formModel: {
        id: '',
        planId: '',
        lineId: '1',
        trainNo: '',
        orderCode: '',
        orderName: '',
        orderType: '5',
        workshopId: '',
        groupId: '',
        startTime: '',
        finishTime: '',
        fdProject:'',
        fdTask: '',
        remark: '',
        tasks: [],
        materials: [],
        monitor: '',
        orderStatus: 0,
        fromType: 4,
      },
      formRules: {
        orderName: [{ required: true, message: '工单名称必须填写!', trigger: 'blur' }],
        workshopId: [{ required: true, message: '车间必须选择!', trigger: 'blur' }],
        lineId: [{ required: true, message: '线路必须选择!', trigger: 'blur' }],
        startTime: [{ required: true, message: '开始日期必须选择!', trigger: 'change' }],
        finishTime: [{ required: true, message: '结束日期必须选择!', trigger: 'change' }]
      },
      selectTask: null,
      flwOpts: { vars: {} },
    }
  },
  mounted () {
    if (this.businessKey) {
      this.edit(this.businessKey)
    } else {
      this.edit(null)
    }
    this.operator = 0
    this.readOnly = false
    if (this.attributes) {
      // 物料发放
      if (this.attributes.isMaterial && this.attributes.isMaterial === 1) {
        this.operator = 2
      }
      // 物料确认
      if (this.attributes.isConfirm && this.attributes.isConfirm === 1) {
        this.operator = 3
      }
    }
   // 2 物料发放  3 物料确认
    if ( this.operator === 2 || this.operator === 3) {
      this.activeTab = '4'
    }
  },
  methods: {
    onTabChange (key) {
      this.activeTab = '1'
      this.$refs.form.validate(valid => {
        if (valid) {
          this.activeTab = key
        } else {
          this.$message.warning('请先填写完基本信息')
          return false
        }
      });
    },
    onTabClick (value) {
      /*switch (this.activeTab) {

      }*/
    },
    // 开始日期改变
    changeStartDate (value) {
      if (value) {
        // 设置结束日期
        this.$nextTick(() => {
          this.formModel.finishTime = value
        })
      }
    },
    // 设置不可选的结束日期
    disabledEndDate (value) {
      let startValue = this.formModel.startTime
      if (!startValue || !value) {
        return false
      }
      return value < startValue
    },
    financeSelectChange (value) {
      this.financeSelectId = value
      this.formModel.fdTask = undefined
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

    edit (id) {
      this.activeTab = '1'
      this.selectTask = null
      if (id) {
        // 加载工单信息
        getWorkOrderDetail(id).then((res) => {
          if (res.success) {
            let detail = res.result
            this.formModel.orderType = detail.orderType + '' // 转成字符型
            this.formModel.fromType = detail.fromType

            this.$nextTick(() => {
              Object.assign(this.formModel, detail)
            })
          } else {
            this.$message.warning(res.message)
          }

          if (this.operator !== 2 && this.operator !== 3) {
            // 不是发料和和领料，表中已加载完成
            this.$emit('loaded')
          }
        })
      } else {
        this.formModel.workshopId = this.$store.getters.userInfo.workshopId
        if (this.operator !== 2 && this.operator !== 3) {
          // 不是发料和和领料，表中已加载完成
          this.$emit('loaded')
        }
      }
    },
    saveAndStart(start) {
      if (this.operator === 0) {
        this.$refs.form.validate(valid => {
          if (valid) {
            let formData = Object.assign({ }, this.formModel)

            formData = Object.assign(formData, { startFlow: start, fromType: this.fromType })

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
    // 检查工单数据
    checkWorkOrderInfo (formData) {
      if (this.operator === 0) {
        if (formData.orderType === "5") {
          // 发料工单，必须有物料
          if (!formData.materials || formData.materials.length < 1) {
            this.flwOpts.res = { message: '车间消耗工单的物料不能为空' }
            this.$emit('fail', this.flwOpts)
            this.activeTab = '4'
            return false
          }
        }
      }
      return true
    },
    // 保存工单信息
    saveWorkOrderInfo(formData) {
      // 工单物料中，临时新增的物资，设置planAmount 为 amount
      formData.materials.forEach((m) => {
        if (m.opType === 2) {
          m.planAmount = m.amount
        }
      })

      this.loading = true
      if (formData.id) {
        editWorkOrder(formData)
          .then((res) => {
            this.flwOpts.res = res
            this.loading = false
            if (res.success) {
              this.handleOk()
            } else {
              this.$emit('fail', this.flwOpts)
              console.error('保存工单失败:', res.message)
            }
          })
          .catch((err) => {
            this.loading = false
            this.flwOpts.res = { message: '保存异常' }
            this.$emit('fail')
            console.error('保存工单异常:', err)
          })
      } else {
        addWorkOrder(formData)
          .then((res) => {
            this.flwOpts.res = res
            if (res.success) {
              this.handleOk()
            } else {
              this.loading = false
              this.$emit('fail', this.flwOpts)
              console.error('保存工单失败:', res.message)
            }
          })
          .catch((err) => {
            this.loading = false
            this.flwOpts.res = { message: '保存异常' }
            this.$emit('fail')
            console.error('保存工单异常:', err)
          })
      }
    },
    // 流程组件中调用的
    save(opts) {
      // 判断工单是否有状态，如果状态为暂停，则不能进行处理
      if (this.formModel && this.formModel.orderStatus === 5) {
        this.flwOpts.res = { message: '该工单已暂停，请先激活在进行操作！' }
        this.$emit('fail', this.flwOpts)
        /* this.$message.warning('该工单已暂停，请先激活在进行操作！')
         this.$emit('fail')*/
        return
      }
      let onlySave = opts.onlySave
      Object.assign(this.flwOpts, opts)

      // 工单编辑（保存工单所有数据）
      if (this.operator === 0) {
        this.$refs.form.validate(valid => {
          if (valid) {
            let formData = Object.assign({ }, this.formModel)

            if (!this.checkWorkOrderInfo(formData)) {
              return;
            }

            this.saveWorkOrderInfo(formData)
          } else {
            this.$emit('fail')
            this.$message.warning('请填写完整基本信息')
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
    onLoaded (data) {
      // 发料和领料的相关数据已加载了
      this.$emit('loaded')
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
      this.loading = false
      this.$emit('cancel')
    },
  }
}
</script>

<style scoped>

</style>