<template>
  <a-modal
    :title="title"
    :width="500"
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
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" :label="labelText">
              <a-switch v-decorator="[ 'isSelfCheck', validatorRules.isSelfCheck]" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>

  </a-modal>
</template>

<script>
import { setSelfCheck } from '@/api/tirosMaterialApi'

export default {
  name: 'SelfCheckModal',
  data () {
    return {
      value: 1,
      title: '操作',
      labelText: '是否自检',
      visible: false,
      model: {},
      selfId: '',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 }
      },

      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        isSelfCheck: {
          rules: [{ required: true, message: '请选择是否自检!' }],
          initialValue: false,
          valuePropName: 'checked'
        }
      }
    }
  },

  created () {
  },
  methods: {
    selfCheck (record) {
      this.form.resetFields()
      let idsStr = record
        .map(function (obj, index) {
          return obj.id
        })
        .join(',')
      this.selfId = idsStr
      this.visible = true
      if (record.length == 1) {
        this.model = Object.assign(record[0])
        this.$nextTick(() => {
          this.form.setFieldsValue({
            isSelfCheck: this.model.isSelfCheck == 1
          })
        })
      }
    },

    // 确定
    handleOk () {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let selfCheck=values.isSelfCheck ? 1 : 0
          setSelfCheck('ids=' + that.selfId + '&' + 'selfCheck=' +selfCheck ).then((res) => {
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
      this.selfId=''
    }
  }
}
</script>

<style scoped>

</style>