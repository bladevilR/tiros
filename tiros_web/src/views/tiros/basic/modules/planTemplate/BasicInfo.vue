<template>

  <a-card>
  <a-form :form="form">
    <a-row style="width: 100%;">
      <a-col :span="24/3">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="模板编码">
          <a-input placeholder="模板编码" v-decorator.trim="[ 'code', validatorRules.code]" />
        </a-form-item>
      </a-col>
      <a-col :span="24/3">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="模板名称">
          <a-input placeholder="模板名称" v-decorator.trim="[ 'tpName', validatorRules.tpName]" />
        </a-form-item>
      </a-col>
      <a-col :span="24/3">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="计划工期">
          <a-input v-decorator.trim="[ 'duration', validatorRules.duration]" />
        </a-form-item>
      </a-col>
      <a-col :span="24/3">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属线路">
<!--          <j-dict-select-tag
            triggerChange
            v-decorator="['lineId', validatorRules.lineId]"
            dictCode="bu_mtr_line,line_name,line_id"
          />-->
          <line-select-list
            v-decorator.trim="['lineId', validatorRules.lineId]"
          >
          </line-select-list>
        </a-form-item>
      </a-col>
      <a-col :span="24/3">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="编组数量">
          <a-input v-decorator.trim="[ 'groupQuantity', validatorRules.groupQuantity]" />
        </a-form-item>
      </a-col>
      <a-col :span="24/3">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="修程类型">
          <j-dict-select-tag
            triggerChange
            v-decorator="['repairProgramId', validatorRules.repairProgramId]"
            dictCode="bu_repair_program,name,id"
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
          <a-date-picker v-decorator="[ 'baseDate', validatorRules.baseDate]" />
        </a-form-item>
      </a-col>
      <a-col :span="24">
        <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="备注">
          <a-textarea placeholder="请输入内容" v-decorator="['remark', validatorRules.remark]" />
        </a-form-item>
      </a-col>
    </a-row>
    <a-button @click="saveVuex">保存</a-button>
    {{plan}}
  </a-form>
  </a-card>
</template>

<script>
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  components:{LineSelectList},
  data() {
    return {
      status: false,
      form: this.$form.createForm(this),
      model: {},
      validatorRules: {
        code: { rules: [{ required: true, message: '请输入结构编码!' }] },
        tpName: { rules: [{ required: true, message: '请输入结构名称!' }] },
        duration: { rules: [{ required: true, message: '请输入计划工期!' }] },
        lineId: { rules: [{ required: true, message: '请选择所属线路!' }] },
        groupQuantity: { rules: [{ required: true, message: '请输入编组数量!' }] },
        repairProgramId: { rules: [{ required: true, message: '请选择修程类型!' }] },
        baseDate: { rules: [{ required: true, message: '请选择基准日期!' }] },
        remark: { rules: [] },
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
        sm: { span: 2 },
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 22 },
      },
    }
  },
  computed: {
    plan() {
      return this.$store.getters.planForm
    },
  },
  methods: {
    handleStatus(value) {
      this.status = value
    },
    saveVuex() {
      this.form.validateFields((err, values) => {
        // if (!err) {
          let formData = Object.assign(this.model, values)
          console.log(this.model)
          this.$store.dispatch('setBasicInfo', formData)
          console.log(this.$store.getters.planForm)
        // }
      })
    },
  },
}
</script>

<style>
</style>