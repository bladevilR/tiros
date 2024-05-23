<template>
  <a-modal
    :title="'任务提交'"
    :width="600"
    :visible="visible"
    centered
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-form :form="form">
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作业人数">
        <a-input-number style="width: 100%" :min='0' :max='9999' v-decorator.trim="['renshu', validatorRules.renshu]" @change="renshuChange" />
      </a-form-item>
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="人均工时">
        <a-input-number style="width: 100%" :min="0"  :max='9999' v-decorator.trim="['workTime', validatorRules.worktime]" @change="worktimeChange" />
      </a-form-item>
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="总计工时">
        <a-input-number style="width: 100%"  :min="0" :max='99999999999' v-decorator.trim="['workTimeSum', validatorRules.sumtime]" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { submitTask } from '@api/tirosGroupApi'

export default {
  name: 'taskSubmitModal',
  data () {
    return {
      visible: false,
      taskId: null,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
      },
      form: this.$form.createForm(this),
      validatorRules: {
        renshu: {initialValue:0, rules: [{ required: true, message: '请输入人数!' }] },
        worktime: {initialValue:0, rules: [{ required: true, message: '请输入人均工时!' }] },
        sumtime: {initialValue:0, rules: [{ required: true, message: '请输入总计工时' }] }
      }
    }
  },
  methods: {
    showModal (taskId) {
      this.taskId = taskId
      this.form.resetFields()
      this.visible = true
    },
    renshuChange (value) {
      const workTime = this.form.getFieldValue('workTime')
      this.form.setFieldsValue({
        workTimeSum: value * workTime
      });
    },
    worktimeChange (value) {
      const ren = this.form.getFieldValue('renshu')
      this.form.setFieldsValue({
        workTimeSum: ren * value
      });
    },
    // 确定
    handleOk () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.$confirm({
            content:'你确定提交该任务？',
            onOk: () => {
              let formData = Object.assign({}, values)
              formData.taskId = this.taskId
              submitTask(formData).then(res => {
                if (res.success) {
                  this.$message.success(res.message)
                  this.$emit('ok')
                  this.close()
                } else {
                  this.$message.error('保存错误')
                  console.error('提交检查信息错误：', res.message)
                }
              })
            }
          })
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
    }
  }
}
</script>

<style scoped>

</style>