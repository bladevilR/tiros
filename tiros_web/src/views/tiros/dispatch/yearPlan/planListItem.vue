<template>
  <a-card id="yearPlanItemContent">
    <div class="info-wrapper info-top-wrapper">
      <h4>基本信息</h4>
      <a-form :form="form">
        <a-row>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="计划名称">
              <a-input placeholder="请输入内容" v-decorator.trim="['title', validatorRules.title]" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="计划年份">
              <a-date-picker
                mode="year"
                placeholder="请选择年份"
                format="YYYY"
                style="width: 100%"
                v-decorator.trim="['year', validatorRules.year]"
                :open="yearPickShow"
                @panelChange="yearPanelChange"
                @openChange="yearOpenChange"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="首列时间">
              <a-date-picker
                format="YYYY-MM-DD"
                style="width:100%"
                v-decorator.trim="['firstTime', validatorRules.firstTime]"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆段">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['depotId', validatorRules.depotId]"
                placeholder="请选择"
                dictCode="bu_mtr_depot,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车间">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['workshopId', validatorRules.workshopId]"
                placeholder="请选择"
                dictCode="bu_mtr_workshop,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="架修模板">
              <j-dict-select-tag
                :triggerChange="true"
                placeholder="请选择"
                v-decorator.trim="['middlePlanTemplate']"
                dictCode="bu_tp_repair_plan,tp_name,id,repair_program_id in (select id from bu_repair_program where pro_type = 1)"
                style="width:100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="架修数">
              <a-input-number
                :max="99999999"
                v-decorator.trim="['middleAmount', validatorRules.middleAmount]"
                style="width:100%"
              ></a-input-number>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="大修数">
              <a-input-number
                :max="99999999"
                v-decorator.trim="['hightAmount', validatorRules.hightAmount]"
                style="width:100%"
              ></a-input-number>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="大修模板">
              <j-dict-select-tag
                :triggerChange="true"
                placeholder="请选择"
                v-decorator.trim="['hightPlanTemplate']"
                dictCode="bu_tp_repair_plan,tp_name,id,repair_program_id in (select id from bu_repair_program where pro_type = 2)" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="备注">
              <a-textarea v-decorator.trim="['remark',validatorRules.remark]"></a-textarea>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="info-wrapper info-top-wrapper">
      <h4>明细信息</h4>
      <div class="buttonDiv" v-if="!isReadonly">
        <a-space>
          <a-button @click="handleAdd">新增计划</a-button>
          <a-button @click="handleAutoGenerate">自动生成</a-button>
          <a-button @click="handlePlanChart" :disabled="!canEdit" title="必须先保存年计划才能使用">图形化编辑</a-button>
          <span style="color: #a19e9e" v-if="!canEdit">&lt;- 必须先保存年计划才能使用图形化编辑</span>
        </a-space>
      </div>

      <div style="margin: 10px 0; height: 430px;">
        <vxe-table border ref="listTable" max-height="100%" :data="tableData" align="center" show-overflow="tooltip">
          <vxe-table-column width="120" title="线路" field="lineName"></vxe-table-column>
          <vxe-table-column width="100" title="总列次" field="trainIndex" align="right"
                            header-align="center"></vxe-table-column>
          <!-- <vxe-table-column title="第几列" field="trainIndex"></vxe-table-column> -->
<!--          <vxe-table-column width="100" title="数量" field="amount" align="right"
                            header-align="center"></vxe-table-column>-->
          <vxe-table-column width="100" title="修程次数" field="programIndex" align="right"
                            header-align="center"></vxe-table-column>
          <vxe-table-column width="200" align="left" header-align="center" title="检修修程"
                            field="programName"></vxe-table-column>
          <vxe-table-column width="200" title="开始时间" field="startDate"></vxe-table-column>
          <vxe-table-column width="200" title="完成时间" field="finishDate"></vxe-table-column>
          <vxe-table-column title="备注" field="remark" align="left" header-align="center"></vxe-table-column>
          <vxe-table-column title="操作" width="10%" fixed="right" v-if="!isReadonly">
            <template v-slot="{ row }">
              <a-space>
                <a @click.stop="handleEdit(row)">编辑</a>
                <a @click.stop="handleDel(row)">删除</a>
              </a-space>
            </template>
          </vxe-table-column>
        </vxe-table>
      </div>
    </div>
    <!--    <div class="footerDIv">
          <a-button type="primary" @click="save">保存</a-button>
          <a-button style="margin-left: 8px" @click="handleBack">返回</a-button>
        </div>-->
    <plan-item-modal ref="modalForm" @ok="addPlan"></plan-item-modal>

    <a-modal
      centered
      :width="'100%'"
      dialogClass="fullDialog"
      :visible="visible"
      @ok="saveChart"
      @cancel="visible=false"
      title="年计划排程"
      :closable="true"
      :destroyOnClose="true"
    >
      <plan-chart ref="planChart" :plan-id="planId" @ok="saveSuccess()"></plan-chart>
    </a-modal>

  </a-card>
</template>

<script>
import moment from 'moment'
import PlanItemModal from './PlanItemModal'
import {
  addYearPlan,
  editYearPlan,
  getPlanAmount,
  getYearPlanDetail,
  yearPlanDetailAutoGenerate
} from '@/api/tirosDispatchApi'
import { everythingIsEmpty } from '@/utils/util'
import PlanChart from '@views/tiros/dispatch/yearPlan/PlanChart'

export default {
  components: { PlanItemModal, PlanChart },
  data () {
    return {
      visible: false,
      canEdit: false,
      title: '操作',
      planId: '',
      tableData: [],
      yearPickShow: false,
      form: this.$form.createForm(this, { onValuesChange: this.changeField }),
      model: {},
      validatorRules: {
        title: { rules: [{ required: true, message: '请输入标题!' }, { max: 32, message: '输入长度不能超过32字符!' }] },
        year: { rules: [{ required: true, message: '请选择计划年份!' }] },
        depotId: { rules: [{ required: true, message: '请选择车辆段!' }] },
        workshopId: { rules: [{ required: true, message: '请选择车间!' }] },
        firstTime: { rules: [{ required: true, message: '请选择首列时间!' }] },
        hightAmount: { rules: [{ required: true, message: '请输入大修数!' }] },
        middleAmount: { rules: [{ required: true, message: '请输入架修数!' }] },
        remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] }
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 }
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 2 }
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 21 }
      }
    }
  },
  props: {
    'businessKey': {
      type: String,
      default: null
    },
    'isReadonly': {
      type: Boolean,
      default: false
    },
    'fromFlow': {
      type: Boolean,
      default: false
    }
  },
  mounted () {
    if (this.businessKey) {
      this.show(this.businessKey)
    }
  },
  methods: {
    handlePlanChart () {
      //this.$refs.planChart.show()
      this.visible = true
    },
    saveChart () {
      this.$confirm({
        okText: '继续',
        content: '确定后将覆盖原有的年计划排程，是否继续？',
        onOk: () => {
          this.$refs.planChart.save()
        }
      })

    },
    saveSuccess () {
      this.loadYearPlanDetail()
      this.visible = false
    },
    moment,
    show (value) {
      this.planId = value
      if (!everythingIsEmpty(this.planId)) {
        this.loadYearPlanDetail()
      } else {
        this.form.resetFields()
        this.tableData = []
      }
    },
    loadYearPlanDetail () {
      getYearPlanDetail({ id: this.planId }).then(res => {
        if (res.success) {
          let detail = res.result
          this.canEdit = true

          this.form.resetFields()
          this.model = Object.assign({}, detail)
          this.$nextTick(() => {
            this.form.setFieldsValue({
              title: detail.title,
              depotId: detail.depotId,
              workshopId: detail.workshopId,
              year: moment(detail.year || new Date(), 'YYYY'),
              firstTime: moment(detail.firstTime || new Date(), 'YYYY-MM-DD'),
              hightAmount: detail.hightAmount,
              middleAmount: detail.middleAmount,
              hightPlanTemplate: detail.hightPlanTemplate,
              middlePlanTemplate: detail.middlePlanTemplate,
              remark: detail.remark
            })
            this.tableData = detail.detailList
            this.sortFun()
          })
        }
      })
    },
    handleDel (row) {
      this.tableData = this.tableData.filter(item => item !== row)
    },
    handleEdit (row) {
      this.$refs.modalForm.edit(row)
      this.$refs.modalForm.title = '编辑'
    },
    handleAutoGenerate () {
      this.form.validateFields((err, values) => {

        if (!err) {
          let formData = Object.assign(values, {
            year: moment(values.year).format('YYYY'),
            firstTime: moment(values.firstTime).format('YYYY-MM-DD')
          })
          if (values.middleAmount > 0 && everythingIsEmpty(values.middlePlanTemplate)) {
            this.$message.warning('请先选择架修模板')
            return
          }
          if (values.hightAmount > 0 && everythingIsEmpty(values.hightPlanTemplate)) {
            this.$message.warning('请先选择大修模板')
            return
          }

          this.$confirm({
            okText: '继续',
            content: '自动生成将覆盖原有的年计划排程，是否继续？',
            onOk: () => {
              this.commonOK(formData)
            }
          })
        }
      })
    },
    commonOK (data) {
      let formData = data
      formData.middleTpRepairPlanId = formData.middlePlanTemplate
      formData.hightTpRepairPlanId = formData.hightPlanTemplate
      yearPlanDetailAutoGenerate(formData).then(res => {
        if (res.success) {
          if (!everythingIsEmpty(res.result)) {
            this.tableData = [...res.result]
            this.sortFun()
            this.$message.success(res.message)
            // 年计划明细变化了，不能图形编辑了，要先保存
            this.canEdit = false
          } else {
            this.$message.warning('没有数据!')
          }
        } else {
          this.$message.error(res.message)
        }
      })
    },
    save (opts) {
      return new Promise((resolve, reject) => { // resolve(res) 抛出成功  reject(err)抛出失败
        this.form.validateFields((err, values) => {
          if (!err) {
            let formData = Object.assign(this.model, values, {
              year: moment(values.year).format('YYYY'),
              firstTime: moment(values.firstTime).format('YYYY-MM-DD'),
              detailList: this.tableData
            })
            // 接受对象是甘特图对象，用Tasks存年计划明细
            formData.Tasks = this.tableData
            if (!everythingIsEmpty(this.planId)) {
              Object.assign(formData, { id: this.planId })
              editYearPlan(formData).then(res => {
                if (res.success) {
                  this.close()
                  if (this.fromFlow) {
                    opts.res = res
                    this.$emit('ok', opts)
                  } else {
                    this.$emit('ok', res)
                  }
                } else {
                  if (this.fromFlow) {
                    opts.res = res
                    this.$emit('fail', opts)
                  } else {
                    this.$emit('fail', res)
                  }
                }
              })
            } else {
              addYearPlan(formData).then(res => {
                if (res.success) {
                  this.close()
                  if (this.fromFlow) {
                    opts.res = res
                    this.$emit('ok', opts)
                  } else {
                    this.$emit('ok', res)
                  }
                } else {
                  if (this.fromFlow) {
                    opts.res = res
                    this.$emit('fail', opts)
                  } else {
                    this.$emit('fail', res)
                  }
                }
              })
            }
          } else {
            reject()
            this.$message.warning('请填写完整基本信息')
          }
        })
      })

    },
    //表单域发生变化
    changeField (props, values) {
      let key = Object.keys(values)[0]
      if (key === 'year') {
        if (this.form.getFieldValue('year') && this.form.getFieldValue('depotId')) {
          // console.log(this.form.getFieldValue('depotId'), this.form.getFieldValue('year'))
          let query = {
            year: moment(this.form.getFieldValue('year')).format('YYYY'),
            depotId: this.form.getFieldValue('depotId')
          }
          this.getAmount(query)
        }
      } else if (key === 'depotId') {
        if (this.form.getFieldValue('year') && values[key]) {
          // console.log(this.form.getFieldValue('year'), values[key])
          let query = {
            year: moment(this.form.getFieldValue('year')).format('YYYY'),
            depotId: values[key]
          }
          this.getAmount(query)
        }
      }
    },
    getAmount (data) {
      getPlanAmount(data).then(res => {
        if (res.success) {
          let amount = res.result
          if (amount.hightRepair || amount.middleRepair) {
            this.form.setFieldsValue({
              hightAmount: amount.hightRepair,
              middleAmount: amount.middleRepair
            })
          }
        }
      })
    },
    handleAdd () {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    addPlan (data) {
      let ishave = false
      for (let i = 0, len = this.tableData.length; i < len; i++) {
        if (data.id != null && data.id && this.tableData[i].id === data.id) {
          ishave = true
          Object.assign(this.tableData[i], data)
        }
      }
      if (!ishave) {
        this.tableData.push(data)
      }
      // 从新排序
      this.sortFun()
    },
    sortFun () {
      this.tableData.sort((a, b) => {
        return a.trainIndex - b.trainIndex
        // return new Date(a.startDate).getTime() - new Date(b.startDate).getTime()

      })
    },
    // changeYear(date, dateStr) {
    //   console.log(date, dateStr)
    // },
    yearOpenChange (status) {
      this.yearPickShow = status
    },
    // 得到年份选择器的值
    yearPanelChange (value) {
      this.form.setFieldsValue({
        year: value
      })
      this.yearPickShow = false
    },
    handleBack () {
      this.$router.back()
    },
    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
      this.tableData = []
    }
  }
}
</script>

<style lang="less">
#yearPlanItemContent {
  border: none;

  .ant-card-body {
    height: calc(100% - 10px);
    /*    overflow-y: auto;*/
  }

  .info-wrapper {
    border: 1px solid #eee;
    position: relative;
    border-radius: 8px;
    padding: 10px;
    margin-bottom: 20px;
  }

  .info-wrapper h4 {
    position: absolute;
    top: -14px;
    padding: 1px 8px;
    margin-left: 16px;
    color: #777;
    border-radius: 2px 2px 0 0;
    background: #fff;
    font-size: 14px;
    width: auto;
  }

  .buttonDiv {
    padding: 10px 0;
  }

  .tableHeight {
    margin: 10px 0;
  }

  .footerDIv {
    padding: 10px;
    background: #fff;
    z-index: 1;
    position: fixed;
    bottom: 0;
    right: 24px;
    text-align: right;
  }
}
</style>
