<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    centered
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="预想标题">
              <a-input placeholder="请输入" v-decorator.trim="[ 'title', validatorRules.title]"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="预想日期">
              <a-date-picker v-decorator.trim="[ 'readDate', validatorRules.readDate]"/>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="预想内容">
              <a-textarea placeholder="请输入" :auto-size="{ minRows: 10, maxRows: 15 }"
                          v-decorator.trim="[ 'content', validatorRules.content]"/>
            </a-form-item>
          </a-col>

        </a-row>
      </a-form>
    </a-spin>
  </a-modal>

</template>

<script>
import moment from 'moment'
import { addSafeassume, editSafeassume } from '../../../../api/tirosGroupApi'
import Vue from 'vue'
import { USER_INFO } from '@/store/mutation-types'

export default {
  name: 'SafeAnticipateModal',
  data() {
    return {
      value: 1,
      title: '操作',
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 18 }
      },

      isClose: false,
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        title: { rules: [{ required: true, message: '请输入预想标题!' }, { max: 32, message: '输入长度不能超过32字符!' }] },
        readDate: { rules: [{ required: true, message: '请选择日期!' }] },
        content: { rules: [{ required: true, message: '请输入预想内容!' }, { max: 500, message: '输入长度不能超过500字符!' }] }
      }
    }
  },

  created() {
  },
  methods: {

    changeClose(checked) {
      this.isClose = checked
    },
    add() {
      this.edit({})
    },
    edit(record) {
      if (record.id) {
        this.isClose = record['close'] && record['close'] == 1 ? true : false
      } else {
        this.isClose = false
      }
      // this.$form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          title: this.model.title,
          readDate: moment(this.model.readDate || new Date(), 'YYYY-MM-DD'),
          content: this.model.content
        })
      })
    },

    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values, {
            readDate: moment(values.readDate).format('YYYY-MM-DD')
          })

          console.log(formData)
          const userInfo = Vue.ls.get(USER_INFO)
          formData.groupId = userInfo.departId
          console.log(formData)

          let obj
          if (!that.model.id) {
            obj = addSafeassume(formData)
          } else {
            obj = editSafeassume(formData)
          }
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