<template>
  <a-modal
    :title="title"
    :width="450"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :bodyStyle="{ minHeight: '300px' }"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :span="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="序号">
              <a-input-number
                placeholder="输入序号"
                v-decorator.trim="['sortNo', validatorRules.sortNo]"
                :min="0"
                :max="999999999"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="项点名称">
              <a-input
                :maxLength="65"
                placeholder="输入项点名称"
                v-decorator.trim="['title', validatorRules.title]"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="检查等级">
              <a-rate v-decorator.trim="['checkLevel', validatorRules.checkLevel]" :allowClear="false" count="3"  />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="检查内容">
              <a-textarea
                :maxLength="201"
                placeholder="输入检查内容"
                v-decorator.trim="['content', validatorRules.content]"
                :auto-size="{ minRows: 3, maxRows: 5 }"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
export default {
  name: 'CheckItemModel',
  components: {},
  data() {
    return {
      title: '操作',
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      deptId: '',
      validatorRules: {
        sortNo: { rules: [{ required: true, message: '请输入序号!' }] },
        title: { rules: [{ required: true, message: '请输入项点名称!' },{ max: 64, message: '不能超过64个字符' }] },
        checkLevel: { rules: [{ required: true, message: '请选择等级!' }] },
        content: { rules: [{ required: true, message: '请输入检查内容!' },{ max: 200, message: '不能超过200个字符' }] },
      },
    }
  },
  created() {},
  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      this.visible = true
      this.model = Object.assign({}, record)
      if (record._XID) {
        this.$nextTick(() => {
          this.form.setFieldsValue({
            sortNo: this.model.sortNo,
            title: this.model.title,
            checkLevel: this.model.checkLevel,
            content: this.model.content,
          })
        })
      }
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values)
          that.$emit('ok', formData)
          that.close()
          that.confirmLoading = false
          //   that.confirmLoading = true
          //   let formData = Object.assign(that.model, values, {})
          //   let obj
          //   if (!that.model.id) {
          //     obj = addBorrow(formData)
          //   } else {
          //     obj = editBorrow(formData)
          //   }
          //   obj
          //     .then((res) => {
          //       if (res.success) {
          //         that.$message.success(res.message)
          //         that.$emit('ok')
          //         that.close()
          //       } else {
          //         that.$message.warning(res.message)
          //       }
          //     })
          //     .finally(() => {
          //       that.confirmLoading = false
          //     })
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
  },
}
</script>