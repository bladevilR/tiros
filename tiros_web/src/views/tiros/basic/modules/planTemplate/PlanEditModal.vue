<template>
  <a-modal
    :title="title"
    width="80%"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    centered
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row style="width: 100%;">
          <a-col :span="24/3">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="模板编码">
              <a-input :maxLength="33" placeholder="模板编码" v-decorator="[ 'code', validatorRules.code]" />
            </a-form-item>
          </a-col>
          <a-col :span="24/3">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="模板名称">
              <a-input :maxLength="33" placeholder="模板名称" v-decorator="[ 'tpName', validatorRules.tpName]" />
            </a-form-item>
          </a-col>
          <a-col :span="24/3">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="计划工期">
              <a-input-number :min="1" :max="999999" style="width: 100%"
                              v-decorator="[ 'duration', validatorRules.duration]" />
            </a-form-item>
          </a-col>
          <a-col :span="24/3">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属线路">
              <line-select-list
                :trigger-change="true"
                v-decorator="['lineId', validatorRules.lineId]"
                @change="handlerLine">
              </line-select-list>
            </a-form-item>
          </a-col>
          <a-col :span="24/3">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="编组数量">
              <a-input-number :min="1" :max="99" style="width: 100%"
                              v-decorator="[ 'groupQuantity', validatorRules.groupQuantity]" />
            </a-form-item>
          </a-col>
          <a-col :span="24/3">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="修程类型">
              <j-dict-select-tag
                triggerChange
                v-decorator="['repairProgramId', validatorRules.repairProgramId]"
                dictCode="bu_repair_program,name,id"
                @change="handleProgram"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24/3">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否有效">
              <a-switch v-model="status" @change="handleStatus" />
            </a-form-item>
          </a-col>
          <a-col :span="24/3">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="基准日期">
              <a-date-picker style="width: 100%" v-decorator="[ 'baseDate', validatorRules.baseDate]" />
            </a-form-item>
          </a-col>
          <a-col :span="24/3">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联规程">
              <j-dict-select-tag
                triggerChange
                v-decorator="['reguId', validatorRules.reguId]"
                :dictCode="dicStr"
                @focus="handleFocusRegu"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24/3">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="财务项目">
              <!-- <a-input :maxLength="65" placeholder="请输入所属财务项目" v-decorator="[ 'fdProject', validatorRules.fdProject]" /> -->
              <FinanceSelect type="project" @change="financeSelectCHange" v-decorator="['fdProject', validatorRules.fdProject]" />
            </a-form-item>
          </a-col>
          <a-col :span="24/3">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="财务任务">
              <!-- <a-input :maxLength="65" placeholder="请输入所属财务任务" v-decorator="[ 'fdTask', validatorRules.fdTask]" /> -->
              <FinanceSelect :id="financeSelectId" type="task" v-decorator="['fdTask', validatorRules.fdTask]" />
            </a-form-item>
          </a-col>
          <!-- <a-col :span="24/3">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开支编码">
              <a-input :maxLength="65" placeholder="请输入所属开支编码"
                       v-decorator="[ 'fdCostType', validatorRules.fdCostType]" />
            </a-form-item>
          </a-col> -->
          <a-col :span="24">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="备注">
              <a-textarea placeholder="请输入内容" :maxLength="201" v-decorator="['remark', validatorRules.remark]" />
            </a-form-item>
          </a-col>
          <a-col :span="24/3">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="导入计划模板">
              <a-upload
                :multiple="false"
                :file-list="fileList"
                accept=".xlsx,.xls"
                :remove="handleRemove"
                :before-upload="beforeUpload"
              >
                <a-button type="primary">添加文件</a-button>
              </a-upload>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <!-- <a-form-item>
            <a-button type="primary" @click="handleSubmit">保存</a-button>
          </a-form-item>-->
        </a-row>
        <!-- {{plan}} -->
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { addPlanTemplate, delPlanTemplate, getLineInfo, getLineList, importTpPlan } from '@/api/tirosApi'
import moment from 'moment'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { everythingIsEmpty } from '@/utils/util'
import FinanceSelect from '@/views/tiros/common/finance/index.vue'

export default {
  components: { LineSelectList,FinanceSelect },
  data () {
    return {
      moment,
      status: true,
      form: this.$form.createForm(this),
      // form: this.$form.createForm(this, { onValuesChange: this.changeField }),
      model: {},
      financeSelectId:'',
      fileList: [],
      trainTypeId: '',
      validatorRules: {
        code: { rules: [{ required: true, message: '请输入编码!' }, { max: 32, message: '输入长度不能超过32字符!' }] },
        tpName: { rules: [{ required: true, message: '请输入名称!' }, { max: 32, message: '输入长度不能超过32字符!' }] },
        duration: { rules: [{ required: true, message: '请输入工期!' }] },
        lineId: { rules: [{ required: true, message: '请选择线路!' }] },
        groupQuantity: { rules: [{ required: true, message: '请输入编组数量!' }] },
        repairProgramId: { rules: [{ required: true, message: '请选择修程类型!' }] },
        reguId: { rules: [{ required: true, message: '请选择关联规程!' }] },
        baseDate: { rules: [{ required: true, message: '请选择基准日期!' }] },
        fdProject: { rules: [{ max: 64, message: '输入长度不能超过64字符!' }] },
        fdTask: { rules: [{ max: 64, message: '输入长度不能超过64字符!' }] },
        fdCostType: { rules: [{ max: 64, message: '输入长度不能超过64字符!' }] },
        remark: { rules: [{ max: 200, message: '输入长度不能超过200字符!' }] }
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 2 }
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 22 }
      },
      title: '操作',
      visible: false,
      confirmLoading: false,
      dicStr: '',
      regeuStatus: false
    }
  },
  // computed: {
  //   plan() {
  //     return this.$store.getters.planForm
  //   },
  // },
  methods: {
    financeSelectCHange(value){
      this.financeSelectId = value;
      this.form.setFieldsValue({fdTask:undefined});
    },
    handlerLine (e, options) {
      if (options) this.trainTypeId = options.trainTypeId
    },
    selectLine (lineId) {
      getLineInfo({ lineId }).then((res) => {
        if (res.success) this.trainTypeId = res.result.trainTypeId
      })
    },
    handleRemove (file) {
      const index = this.fileList.indexOf(file)
      const newFileList = this.fileList.slice()
      newFileList.splice(index, 1)
      this.fileList = newFileList
    },
    beforeUpload (file) {
      if (this.model.id) {
        this.$message.warn('该操作会覆盖原有的数据,请谨慎操作!')
      }
      const isLt10M = file.size / 1024 / 1024 < 10
      if (!isLt10M) {
        this.$message.error('文件不得大于10MB!')
        return
      }
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        || file.type === 'application/vnd.ms-excel'
      if (!isExcel) {
        this.$message.error('只支持Excel文件导入!')
        return
      }
      this.$set(this.fileList, 0, file)
      return false
    },
    customRequest (params) { // 上传提交
      let { fileList } = this
      fileList.forEach(file => {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('lineId', params.lineId)
        formData.append('groupQuantity', params.groupQuantity)
        formData.append('repairProgramId', params.repairProgramId)
        formData.append('reguId', params.reguId)
        formData.append('status', params.status)
        formData.append('trainTypeId', this.trainTypeId)
        formData.append('baseDate', params.baseDate)
        formData.append('code', params.code)
        formData.append('tpName', params.tpName)
        formData.append('duration', params.duration)
        formData.append('fdProject', params.fdProject ? params.fdProject : '')
        formData.append('fdTask', params.fdTask ? params.fdTask : '')
        formData.append('fdCostType', params.fdCostType ? params.fdCostType : '')
        formData.append('remark', params.remark ? params.remark : '')
        if (this.model.id) {
          formData.append('id', this.model.id)
        } else {
          formData.append('id', '')
        }
        importTpPlan(formData).then((res) => {
          if (res.success) {
            this.$message.success(res.message)
            this.$emit('ok')
            this.close()
          } else {
            this.$message.error(res.message)
          }
        }).finally(() => {
          this.confirmLoading = false
        })
      })
    },
    handleFocusRegu () {
      let programId = this.form.getFieldValue('repairProgramId')
      if (everythingIsEmpty(programId)) this.$message.warn('请先选择修程类型')
    },
    handleProgram (data) {
      if (data) {
        this.dicStr = `bu_repair_regu_info,name,id,status=1 and repair_pro_id='${data}'`
      } else {
        this.dicStr = ''
      }
      if (!this.regeuStatus) {
        this.form.setFieldsValue({ reguId: '' })
      }
      this.regeuStatus = false
    },
    add () {
      this.edit({})
    },
    edit (record) {
      if (record.id) {
        this.visiblekey = true
        this.regeuStatus = true
      } else {
        this.visiblekey = false
      }
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.status = this.model.status === 1
      if (!everythingIsEmpty(this.model.lineId)) {
        this.selectLine(this.model.lineId)
      }
      this.$nextTick(() => {
        console.log(this.model.fdTask)
        if(this.model.fdProject){
          this.financeSelectId = this.model.fdProject
        }
        this.form.setFieldsValue({
          code: this.model.code,
          tpName: this.model.tpName,
          duration: this.model.duration,
          lineId: this.model.lineId,
          groupQuantity: this.model.groupQuantity,
          repairProgramId: this.model.repairProgramId,
          reguId: this.model.reguId,
          status: this.status,
          baseDate: moment(this.model.baseDate || new Date(), 'YYYY-MM-DD'),
          remark: this.model.remark,
          fdProject: this.model.fdProject,
          fdTask: this.model.fdTask,
          fdCostType: this.model.fdCostType
        })
      })
    },
    changeField (props, values) {
      this.$store.dispatch('setBasicInfo', values)
    },
    handleStatus (value) {
      this.status = value
    },
    handleOk () {
      let that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          let formData = Object.assign(that.model, values, {
            baseDate: moment(values.baseDate).format('YYYY-MM-DD'),
            status: this.status ? 1 : 0
          })
          that.confirmLoading = true
          if (that.fileList.length > 0) {
            that.customRequest(formData)
          } else {
            addPlanTemplate(formData)
              .then((res) => {
                if (res.success) {
                  that.$message.success(res.message)
                  that.$emit('ok')
                  that.close()
                } else {
                  that.$message.error(res.message)
                }
              }).catch(err => {
              that.$message.error('保存异常',err)
            }).finally(() => {
              that.confirmLoading = false
            })
          }

        }
      })
    },
    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
      this.regeuStatus = false
      this.fileList = []
    }
  }
}
</script>

<style>
</style>