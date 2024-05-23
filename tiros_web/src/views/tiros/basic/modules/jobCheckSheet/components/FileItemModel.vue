<template>
  <a-modal
    :title="title"
    :width="450"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :span="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作业指导书">
              <a-select
                placeholder="请选择关联设备"
                :open="false"
                :showArrow="true"
                v-decorator.trim="['teckBookName', validatorRules.teckBookName]"
                @focus="openModal"
                ref="mySelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <TechBookList ref="selectForm" :multiple="false" @ok="selectBook"></TechBookList>
    </a-spin>
  </a-modal>
</template>

<script>
import TechBookList from '@views/tiros/common/selectModules/TechBookList'
export default {
  name: 'FileItemModel',
  components: { TechBookList },
  data() {
    return {
      title: '操作',
      visible: false,
      teckBookId: '',
      teckBookName: '',
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
        teckBookName: { rules: [{ required: true, message: '请选择作业指导书!' }] },
      },
    }
  },
  created() {},
  methods: {
    openModal() {
      this.$refs.selectForm.showModal()
      this.$refs.mySelect.blur()
    },
    selectBook(data) {
      if (data.length) {
        this.teckBookId = data[0].id
        this.teckBookName = data[0].fileName
      }
      this.form.setFieldsValue({
        teckBookName: this.teckBookName,
      })
    },
    add() {
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      this.visible = true
      this.model = Object.assign({}, record)
      if (record._XID) {
        this.$nextTick(() => {
          this.form.setFieldsValue({})
        })
      }
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values, {
            teckBookId: this.teckBookId,
          })
          that.$emit('ok', formData)
          that.close()
          that.confirmLoading = false
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