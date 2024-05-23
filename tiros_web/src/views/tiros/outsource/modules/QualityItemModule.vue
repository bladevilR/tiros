<template>
  <a-modal
    title="设置质保日期"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="质保开始日期">
              <a-date-picker
                style="width: 100%"
                v-decorator.trim="[ 'startDate',validatorRules.startDate]"
                placeholder="请选择"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="质保天数">
              <a-input-number
                :min="0"
                :max="999999999"
                :step="1"
                :precision="0"
                @change="dayChange"
                style="width: 100%"
                v-decorator.trim="[ 'dayCount',validatorRules.qualityDay]"
                placeholder="请选择"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="质保结束日期">
              <a-date-picker
                style="width: 100%"
                @change="endChange"
                v-decorator.trim="[ 'endDate',validatorRules.endDate]"
                placeholder="请选择"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="功能验收日期">
              <a-date-picker
                style="width: 100%"
                v-decorator.trim="[ 'checkDate',validatorRules.checkDate]"
                placeholder="请选择"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol=" {xs: { span: 24 }, sm: { span: 4 }}" :wrapperCol="{xs: { span: 24 }, sm: { span: 20 }}" label="备注">
              <a-textarea
                v-decorator.trim="[ 'remark',validatorRules.remark]"
                placeholder="请选择"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { editQuality } from '@api/tirosOutsourceApi'
import { pick } from 'xe-utils/methods'
import moment from 'moment'

export default {
  name: 'QualityItemModule',
  data () {
    return {
      visible: false,
      confirmLoading: false,
      model: {},
      form: this.$form.createForm(this),
      validatorRules: {
        startDate: { rules: [{ required: true, message: '质保开始日期不能为空!' }] },
        endDate: { rules: [{ required: true, message: '质保结束日期不能为空!' }] },
        checkDate: { rules: [{ required: true, message: '功能验收日期不能为空!' }] },
        qualityDay: { rules: [], initialValue: 0 },
        remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] }
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8}
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 }
      }

    }
  },
  methods: {
    dayChange(data){
      console.log(data)
      const startDate = this.form.getFieldValue('startDate');
      if(startDate){
        const endDate = moment(startDate).add('day',data)
        console.log(endDate)
        this.form.setFieldsValue({
          endDate: endDate,
        })
      }
    },
    endChange(data){
      console.log(data)
      const startDate = this.form.getFieldValue('startDate');
      if(startDate){
        const dayCount = data.diff(startDate,'days')
        console.log(dayCount)
        this.form.setFieldsValue({
          dayCount: dayCount,
        })
      }
    },
    edit (data) {
      this.visible = true
      this.form.resetFields()
      this.model = Object.assign({}, data)
      this.$nextTick(() => {
        this.form.setFieldsValue({
          startDate: this.model.startDate,
          dayCount: this.model.dayCount,
          endDate: this.model.endDate,
          checkDate: this.model.checkDate,
          remark: this.model.remark,
        })
      })
    },
    handleOk () {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values,{
            checkDate:values.checkDate? moment(values.checkDate).format('YYYY-MM-DD'):'',
            startDate:values.startDate? moment(values.startDate).format('YYYY-MM-DD'):'',
            endDate:values.endDate? moment(values.endDate).format('YYYY-MM-DD'):'',
          })
          editQuality(formData).then((res) => {
            if (res.success) {
              that.$message.success(res.message)
              that.$emit('ok')
            } else {
              that.$message.warning(res.message)
            }
          })
            .finally(() => {
              that.confirmLoading = false
              that.close()
            })
          this.visible = false
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
    }
  }
}
</script>

<style scoped>

</style>