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
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="逾期原因">
              <a-textarea placeholder="请输入逾期原因" v-decorator.trim="[ 'delayReason',validatorRules.delayReason]"/>
            </a-form-item>
          </a-col>
      </a-row >
      </a-form>
    </a-spin>
  </a-modal>

</template>

<script>
  import { settingDelayReason } from '@api/tirosOutsourceApi'

  export default {
    name: 'PerformItemModal',
    data() {
      return {
        title: '操作',
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 15 }
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules: {
          delayReason: { rules: [{ max: 64, message: '输入长度不能超过64字符!' }] },
        }
      }
    },
    created() {
    },
    methods: {
      add() {
        this.edit({})
      },
      edit(record) {
        this.form.resetFields()
        this.model = Object.assign({}, record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue({
            delayReason: this.model.delayReason
          })
        })
      },
      // 确定
      handleOk() {
        const that = this
        that.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true
            let formData = Object.assign(that.model, values)
            settingDelayReason(formData).then((res) => {
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