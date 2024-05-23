<template>
  <a-modal
    title='转入周转件'
    :visible='visible'
    :confirmLoading='confirmLoading'
    @ok='handleOk'
    @cancel='handleCancel'
    :destroyOnClose='true'
    cancelText='关闭'
  >
    <a-form :form='form'>
      <a-row :gutter='24'>
        <a-col>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='转入数量'>
            <a-input-number
              :min='0'
              :step='0.1'
              :max='99999999'
              style='width: 100%'
              v-decorator.trim="['transAmount', validatorRules.transAmount]"
              placeholder='请输入转入数量'
            />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script>
import { transGroupStockToTurnover } from '@api/tirosMaterialApi'

export default {
  name: 'TransTurnoverModal',
  components: {},
  props: ['groupStockId'],
  data() {
    return {
      visible: false,
      confirmLoading: false,
      form: this.$form.createForm(this),
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
      },
      validatorRules: {
        transAmount: { rules: [{ required: true, message: '请输入转入数量!' }] }
      },
      transAmount: 0
    }
  },
  methods: {
    show() {
      this.visible = true
      this.form.resetFields()
      this.model = Object.assign({}, {})
    },
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          // let formData = Object.assign({}, values, {
          //   groupStockId: that.groupStockId,
          //   transAmount: that.transAmount
          // })
          let formData = Object.assign(that.model, values)
          console.log("formData")
          console.log(JSON.stringify(formData))
          that.confirmLoading = true
          transGroupStockToTurnover('groupStockId=' + that.groupStockId + '&transAmount=' + formData.transAmount).then((res) => {
            if (res.success) {
              that.$message.success(res.result)
              that.$emit('ok')
              that.close()
            } else {
              that.$message.error(res.message)
            }
          }).finally(() => {
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

<style scoped>

</style>