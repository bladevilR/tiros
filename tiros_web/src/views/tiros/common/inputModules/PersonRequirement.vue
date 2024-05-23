<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-form :form="form">
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="岗位要求">
        <a-input v-decorator.trim="[ 'requirePostion', validatorRules.requirePostion]" />
      </a-form-item>
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="证书要求">
        <a-textarea v-decorator="[ 'requireCertificate', validatorRules.requireCertificate]" />
      </a-form-item>
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="技能要求">
        <a-textarea v-decorator="[ 'requireTech', validatorRules.requireTech]" />
      </a-form-item>
      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所需人数">
        <a-input-number :min="0" :max="99999" v-decorator="[ 'amount', validatorRules.amount]" style="width: 100%" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import pick from 'lodash.pick'
import { addTrack, editTrack } from '@/api/tirosApi'

export default {
  name: 'PersonRequirement',
  data() {
    return {
      value: 1,
      title: '操作',
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
      form: this.$form.createForm(this),
      validatorRules: {
        requirePostion: { rules: [{ required: true, message: '请输入岗位要求!' },{ max: 450, message: '输入长度不能超过500字符!' }] },
        requireCertificate: { rules: [{ required: true, message: '请输入证书要求!' },{ max: 450, message: '输入长度不能超过500字符!' }] },
        requireTech: { rules: [{ required: true, message: '请输入技能要求!' },{ max: 450, message: '输入长度不能超过500字符!' }] },
        amount: { rules: [{ required: true, message: '请输入所需人数!' }] },
      },
    }
  },
  created() {},
  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      if (record.id) {
        this.visiblekey = true
      } else {
        this.visiblekey = false
      }
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          requirePostion: this.model.requirePostion,
          requireCertificate: this.model.requireCertificate,
          requireTech: this.model.requireTech,
          amount: this.model.amount,
        })
      })
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          let formData = Object.assign(that.model, values)
          if(!that.model.id){
            formData['id']=Date.parse(new Date())
          }
          console.log(formData)
          that.$emit('ok',formData)
          that.close()
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