<template>
  <a-modal
    :title="title"
    :width="700"
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
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="使用名目">
              <a-input placeholder="使用名目" v-decorator.trim="[ 'subject', validatorRules.subject]"/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="使用金额">
              <a-input-number style="width: 100%" placeholder="使用金额"
                              :formatter="value => `${value}万`"
                              :parser="value => value.replace('万', '')"
                              :min="0"
                              :max="maxAmount"
                              :step="0.1"
                              v-decorator="['amount',validatorRules.amount]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="办理人">
              <a-input placeholder="办理人" v-decorator.trim="[ 'dealMan',validatorRules.dealMan]"/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="办理日期">
              <a-date-picker style="width: 100%" placeholder="办理日期" v-decorator="['dealDate',validatorRules.dealDate]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="使用日期">
              <a-date-picker style="width: 100%" placeholder="使用日期" v-decorator="['useDate',validatorRules.useDate]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24" >
            <a-form-item  label="使用原因" :labelCol="rLabelCol" :wrapperCol="rWrapperCol">
              <a-textarea v-decorator="[ 'reason',validatorRules.reason]"/>
            </a-form-item>
          </a-col>
        </a-row >
        <a-row :gutter="24">
          <a-col :md="24" :sm="24" >
            <a-form-item  label="备注" :labelCol="rLabelCol" :wrapperCol="rWrapperCol">
              <a-textarea v-decorator="[ 'remark',validatorRules.remark]"/>
            </a-form-item>
          </a-col>
        </a-row >
      </a-form>
    </a-spin>
  </a-modal>

</template>

<script>
  import { addContractBakMoney, editContractBakMoney } from '@/api/tirosOutsourceApi'
  import moment from 'moment'

  export default {
    name: 'ContractBakItemModal',
    props:['contractId'],
    data() {
      return {
        title: '操作',
        visible: false,
        model: {},
        maxAmount:0,
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 15 }
        },
        rLabelCol: {
          xs: { span: 24 },
          sm: { span: 3}
        },
        rWrapperCol: {
          xs: { span: 24 },
          sm: { span: 21 }
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules: {
          subject: { rules: [{ required: true, message: '请输入使用名目!' },{ max: 32, message: '输入长度不能超过32字符!' }] },
          amount: { rules: [{ required: true, message: '请输入使用金额!' }] },
          dealDate: { rules: [{ required: true, message: '请输入办理日期!' }] },
          useDate: { rules: [{ required: true, message: '请输入使用日期!' }] },
          dealMan: { rules: [{ max: 32, message: '输入长度不能超过32字符!' }] },
          reason: { rules: [{ max: 64, message: '输入长度不能超过64字符!' }] },
          remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] },
        },
      }
    },
    created() {

    },
    methods: {
      add() {
        this.edit({})
      },
      setMaxAmount(value){
        this.maxAmount=value;
      },
      edit(record) {
        this.form.resetFields()
        this.model = Object.assign({}, record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue({
            subject: this.model.subject,
            amount: this.model.amount,
            dealMan: this.model.dealMan,
            reason: this.model.reason,
            dealDate:moment(this.model.dealDate || undefined),
            useDate:moment(this.model.useDate || undefined),
            remark:this.model.remark
          })
        })
      },
      // 确定
      handleOk() {
        const that = this
        that.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true
            let formData = Object.assign(that.model, values,{
              dealDate: moment(values.dealDate).format('YYYY-MM-DD'),
              useDate: moment(values.useDate).format('YYYY-MM-DD'),
              contractId:this.contractId })
            let obj
            if (!that.model.id) {
              obj = addContractBakMoney(formData)
            } else {
              obj = editContractBakMoney(formData)
            }
            obj
              .then((res) => {
                if (res.success) {
                  that.$message.success(res.message)
                  that.$emit('ok')
                  that.close()

                } else {
                  that.$message.warning(res.message)
                }
              })
              .finally(() => {
                that.confirmLoading = false
              })
          }
        })
      },
      // 关闭
      handleCancel() {
        this.close()
      },
      close() {
        this.$emit('close')
        this.visible = false
      }
    }
  }
</script>