<template>
  <a-modal
    :title="title"
    :width="490"
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
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="状态">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="[ 'status', validatorRules.status]"
                placeholder="请选择"
                dictCode="bu_tools_status"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>

</template>

<script>

  import { setToolEquipment } from '../../../../api/tirosGroupApi'

  export default {
    name: 'EquipmentManageModal',
    data() {
      return {
        title: '操作',
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 9 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 10 }
        },
        isClose: false,
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules: {
          status: { rules: [{ required: true, message: '请选择!' }] },
        }
      }
    },

    created() {
    },
    methods: {

      changeClose(checked) {
        this.isClose=checked
      },
      set(record) {
        if (record.id) {
          this.isClose=record['close']&&record['close']==1?true:false;
        } else {
          this.isClose=false
        }
        // this.$form.resetFields()
        this.model = Object.assign({}, record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue({
            status:this.model.status,
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
            console.log(formData)
            let obj
            obj = setToolEquipment(formData)
            obj
              .then((res) => {
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

<style scoped>

</style>