<template>
  <a-modal
    :title="title"
    centered
    :width="800"
    :bodyStyle="{height:'500px'}"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="序号">
          <a-input-number
            :max="2147483647"
            :min="0"
            id="inputNumber"
            v-decorator.trim="['itemNo', validatorRules.itemNo ]"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作业内容">
          <a-textarea :auto-size="{ minRows: 4,maxRows: 6}"  v-decorator.trim="[ 'workContent', validatorRules.workContent]"  allow-clear/>
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="控制点">
          <j-dict-select-tag
            v-decorator.trim="[ 'checkLevel', validatorRules.checkLevel]"
            placeholder="请选择"
            :triggerChange="true"
            dictCode="bu_check_level"
          />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="技术要求">
        <a-textarea  :auto-size="{ minRows: 6 ,maxRows: 8 }" v-decorator.trim="[ 'techRequire', validatorRules.techRequire]" allow-clear />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import pick from 'lodash.pick'
import { saveOldWorkRecordDetail } from '@/api/tirosApi'
export default {
  name: 'RecordItemModal',
  props: ['workRecId', 'categoryId'],
  data() {
    return {
      value: 1,
      title: '操作',
      status: 1,
      switchCheck: true,
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        itemNo: { rules: [{ required: true, message: '请输入序号!' }] },
        workContent: { rules: [{ required: true, message: '请输入作业内容!' },{ max:255, message: '输入长度不能超过255字符!'}] },
        checkLevel: { rules: [{ required: true, message: '请选择!' }] },
        techRequire: { rules: [{ required: true, message: '请输入技术要求!' },{ max:20000, message: '输入长度不能超过20000字符!'}], initialValue:'' },
      },
    }
  },
  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      if (record.id) {
        this.switchCheck = record.status == 1 ? true : false
      }
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'recIndex', 'workContent', 'checkLevel', 'techRequire','itemNo'))
      })
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let p = {
            workRecId: this.workRecId,
            categoryId: this.categoryId,
          }
          let formData = Object.assign(that.model, values, p)
          saveOldWorkRecordDetail(formData)
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
              } else {
                that.$message.error(res.message)
              }
            })
            .finally(() => {
              that.confirmLoading = false
              that.close()
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
  },
}
</script>