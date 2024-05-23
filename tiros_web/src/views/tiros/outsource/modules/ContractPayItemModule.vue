<template>
  <a-modal
    :title="title"
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
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="支付标题:">
              <a-input placeholder="支付标题" v-decorator.trim="['payDesc', validatorRules.payDesc]" />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="执行税率:">
              <a-input-number
                style="width: 100%"
                placeholder="执行税率"
                :formatter="(value) => `${value}%`"
                :parser="(value) => value.replace('%', '')"
                :min="0"
                :max="100"
                :step="0.1"
                v-decorator="['executeTaxRate', validatorRules.executeTaxRate]"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开票金额:">
              <a-input-number
                style="width: 100%"
                placeholder="开票金额"
                :formatter="(value) => `${value}万`"
                :parser="(value) => value.replace('万', '')"
                :min="0"
                :max="maxAmount"
                :step="0.1"
                v-decorator="['ticketAmount', validatorRules.ticketAmount]"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="支付金额:">
              <a-input-number
                style="width: 100%"
                placeholder="支付金额"
                :formatter="(value) => `${value}万`"
                :parser="(value) => value.replace('万', '')"
                :min="0"
                :max="maxAmount"
                :step="0.1"
                @change="onAmountChange"
                v-decorator="['amount', validatorRules.amount]"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="质保金比率:">
              <a-input-number
                style="width: 100%"
                placeholder="质保金比率"
                :formatter="(value) => `${value}%`"
                :parser="(value) => value.replace('%', '')"
                :min="0"
                :max="100"
                :step="0.1"
                @change="onQaRateChange"
                v-decorator="['qaRate', validatorRules.qaRate]"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="质保金:">
              <a-input-number
                style="width: 100%"
                placeholder="质保金"
                :formatter="(value) => `${value}万`"
                :parser="(value) => value.replace('万', '')"
                :min="0"
                :max="maxAmount"
                :step="0.1"
                :disabled="true"
                v-decorator="['qaMoney', validatorRules.qaMoney]"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">

          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="办理日期">
              <a-date-picker
                style="width: 100%"
                placeholder="办理日期"
                v-decorator="['dealDate', validatorRules.dealDate]"
              />
            </a-form-item>
          </a-col><a-col :md="12" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="办理人:">
            <a-input placeholder="办理人" v-decorator.trim="['dealMan', validatorRules.dealMan]" />
          </a-form-item>
          </a-col>

        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="支付日期:">
              <a-date-picker
                style="width: 100%"
                placeholder="支付日期"
                v-decorator="['payDate', validatorRules.payDate]"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item label="备注" :labelCol="rLabelCol" :wrapperCol="rWrapperCol">
              <a-textarea v-decorator="['remark', validatorRules.remark]" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { addContractPay, addSupplier, editContractPay, editSupplier } from '../../../../api/tirosOutsourceApi'
import moment from 'moment'
import { jsAdd, jsCut, jsMul, jsDiv } from '@/utils/util'

export default {
  name: 'ContractPayItemModal',
  props: ['contractId'],
  data() {
    return {
      title: '操作',
      visible: false,
      model: {},
      maxAmount: 0,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 },
      },
      rLabelCol: {
        xs: { span: 24 },
        sm: { span: 3 },
      },
      rWrapperCol: {
        xs: { span: 24 },
        sm: { span: 21 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        executeTaxRate: { rules: [{ required: true, message: '请输入执行税率!' }] },
        ticketAmount: { rules: [{ required: true, message: '请输入开票金额!' }] },
        amount: { rules: [{ required: true, message: '请输入支付金额!' }] },
        qaRate: { rules: [{ required: true, message: '请输入质保金比率!' }] },
        // qaMoney: { rules: [{ required: true, message: '请输入质保金!' }] },
        payDesc: {
          rules: [
            { required: true, message: '请输入支付标题!' },
            { max: 64, message: '输入长度不能超过64字符!' },
          ],
        },
        dealDate: { rules: [{ required: true, message: '请输入办理日期!' }] },
        payDate: { rules: [{ required: true, message: '请输入支付日期!' }] },
        remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] },
        dealMan: {
          rules: [
            { required: true, message: '请输入办理人!' },
            { max: 32, message: '输入长度不能超过32字符!' },
          ],
        },
      },
      oldExecuteTaxRate: 0
    }
  },
  created() {},
  methods: {
    add(record) {
      this.edit(record)
    },
    setMaxAmount(value) {
      this.maxAmount = value
    },
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.oldExecuteTaxRate = record.executeTaxRate
      this.visible = true
      this.$nextTick(() => {
        this.form.getFieldDecorator('qaMoney')
        this.form.setFieldsValue({
          payDesc: this.model.payDesc,
          ticketAmount: this.model.ticketAmount,
          amount: this.model.amount,
          qaMoney: this.model.qaMoney,
          qaRate: this.model.qaRate,
          dealMan: this.model.dealMan,
          dealDate: moment(this.model.dealDate || undefined),
          payDate: moment(this.model.payDate || undefined),
          executeTaxRate: this.model.executeTaxRate,
          remark: this.model.remark,
        })
      })
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values, {
            dealDate: moment(values.dealDate).format('YYYY-MM-DD'),
            payDate: moment(values.payDate).format('YYYY-MM-DD'),
            contractId: this.contractId,
          })

        

          //如果税率变化 计算算剩余本金，公式为 剩余本金= 剩余金额/(1+上次税率),  剩余支付额= 剩余本金*(1+新的税率）
          // let leftover = (this.maxAmount / (1 + (Number(this.oldExecuteTaxRate) / 100))) * (1 + (Number(formData.executeTaxRate) / 100)) - formData.amount

          //本金 (this.maxAmount / (1 + (Number(this.oldExecuteTaxRate) / 100)))
          let capitalTemp = jsDiv(this.maxAmount, jsAdd(1, jsDiv(Number(this.oldExecuteTaxRate), 100)))
          // 剩余金额 (1 + (Number(formData.executeTaxRate) / 100))
          let leftOverTemp = jsAdd(1, jsDiv(Number(formData.executeTaxRate), 100))
          
          formData['leftover'] = jsCut(jsMul(capitalTemp, leftOverTemp), formData.amount)

          let obj
          if (!that.model.id) {
            obj = addContractPay(formData)
          } else {
            obj = editContractPay(formData)
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
    },
    onAmountChange(value){
      let qaRate = this.form.getFieldValue('qaRate')
      if (qaRate !== undefined ) {
        //质保金公式 合同总价 * (比率 / 100)
        try {
          console.log(this.model);
          this.form.setFieldsValue({
            qaMoney: jsMul( Number(value) , jsDiv(Number(qaRate), 100))
          })
        } catch (error) {
          console.log(error);
        }
      }
    },
    onQaRateChange(value){
      let amount = this.form.getFieldValue('amount')
      if (amount !== undefined ) {
        //质保金公式 合同总价 * (比率 / 100)
        try {
          console.log(this.model);
          this.form.setFieldsValue({
            qaMoney: jsMul( Number(amount) , jsDiv(Number(value), 100))
          })
        } catch (error) {
          console.log(error);
        }
      }


    },
  },
}
</script>