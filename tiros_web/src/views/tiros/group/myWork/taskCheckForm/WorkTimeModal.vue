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
            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='作业时间'>
              <a-date-picker
                format='YYYY-MM-DD HH:mm:ss'
                :show-time="{ format: 'HH:mm:ss', hideDisabledOptions: true }"
                v-model='workTime'
                style='width: 100%'
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>

  </a-modal>
</template>

<script>
  import moment from 'moment'
  import { saveCheckTime } from '@api/tirosGroupApi'

  export default {
    name: 'WorkTimeModal',
    data() {
      return {
        value: 1,
        title: '作业完成',
        labelText:'作业时间',
        visible: false,
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
        ids: '',
        workTime: undefined,
      }
    },

    created() {
    },
    methods: {
      show(ids) {
        this.form.resetFields()
        this.ids = ids
        this.visible = true
      },

      // 确定
      handleOk() {
        const that = this
        that.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true

            let formData = new FormData()
            formData.append('itemIds', that.ids)
            if (that.workTime) {
              that.workTime = moment(that.workTime).format('YYYY-MM-DD HH:mm:ss')
              formData.append('workTime', that.workTime)
            }

            saveCheckTime(formData).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
              } else {
                that.$message.warning(res.message)
              }
            }).finally(() => {
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