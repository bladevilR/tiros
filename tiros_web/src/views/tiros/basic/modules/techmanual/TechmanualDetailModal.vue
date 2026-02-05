<template>
  <a-modal
    v-model="visible"
    title="工艺电子手册"
    width="900px"
    @ok="handleOk"
    @cancel="handleCancel"
    :confirmLoading="loading"
    :maskClosable="false"
  >
    <a-form :form="form" layout="vertical">
      <a-row :gutter="16">
        <a-col :md="12">
          <a-form-item label="手册名称" :required="true">
            <a-input
              placeholder="请输入手册名称"
              v-decorator="['name', {rules: [{required: true, message: '请输入手册名称'}]}]"
            />
          </a-form-item>
        </a-col>
        <a-col :md="12">
          <a-form-item label="车型">
            <a-select
              placeholder="请选择车型"
              v-decorator="['trainType', {rules: [{required: false}]}]"
            >
              <a-select-option value="">全部</a-select-option>
              <a-select-option value="电客车">电客车</a-select-option>
              <a-select-option value="电动车组">电动车组</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="24">
          <a-form-item label="备注">
            <a-textarea
              placeholder="请输入备注信息"
              :rows="3"
              v-decorator="['remark', {rules: [{required: false}]}]"
            />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script>
import { saveSopRecord } from '@/api/tirosApi'

export default {
  data () {
    return {
      visible: false,
      loading: false,
      form: this.$form.createForm(this),
      record: null,
      isEdit: false
    }
  },
  methods: {
    add () {
      this.isEdit = false
      this.record = null
      this.form.resetFields()
      this.visible = true
    },
    edit (record) {
      this.isEdit = true
      this.record = record
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          name: record.name,
          trainType: record.trainType,
          remark: record.remark
        })
      })
    },
    handleOk () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          const params = {
            ...values,
            id: this.isEdit ? this.record.id : undefined
          }
          saveSopRecord(params).then((res) => {
            if (res.success) {
              this.$message.success(this.isEdit ? '修改成功' : '新增成功')
              this.handleCancel()
              this.$emit('ok')
            } else {
              this.$message.error(res.message || '操作失败')
            }
          }).finally(() => {
            this.loading = false
          })
        }
      })
    },
    handleCancel () {
      this.visible = false
      this.form.resetFields()
      this.record = null
    }
  }
}
</script>
