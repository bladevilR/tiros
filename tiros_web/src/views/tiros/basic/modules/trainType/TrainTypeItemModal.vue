<template>
  <a-modal
    :title="title"
    :width="600"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车型编码">
          <a-input v-decorator.trim="[ 'code', validatorRules.code]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车型名称">
          <a-input v-decorator.trim="[ 'name', validatorRules.name]" />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否启用">
          <a-switch @change="onChose" v-model="visibleCheck" />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注描述">
          <a-input v-decorator="[ 'remark',validatorRules.remark]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import pick from 'lodash.pick'
import { addTrainType, editTrainType } from '@/api/tirosApi'
import { duplicateCheck } from '@api/api'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'TrainTypeModal',
  data() {
    return {
      value: 1,
      title: '操作',
      status: 1,
      visibleCheck: true,
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        code: { rules: [{ required: true, message: '请输入车型编码!' },{validator: this.validateCode},{max:32,message: '输入长度不能超过32字符!'}] },
        name: { rules: [{ required: true, message: '请输入车型名称!' },{max:32,message: '输入长度不能超过32字符!'}] },
        remark: {rules:[{max:255,message: '输入长度不能超过255字符!'}]},
      }
    }
  },
  methods: {
    validateCode(rule, value, callback){
      let params = {
        tableName: "bu_train_type",
        fieldName: "code",
        fieldVal: value,
        dataId: this.model.id
      }
      if(!everythingIsEmpty(value)) {
        duplicateCheck(params).then((res) => {
          if (res.success) {
            callback()
          } else {
            callback(res.message)
          }
        })
      }else {
        callback()
      }
    },
    add() {
      this.edit({})
    },
    edit(record) {
      if (record.id) {
        this.visibleCheck = record.status == 1 ? true : false
      } else {
        this.visibleCheck = true
      }
      this.form.resetFields()
      this.model = Object.assign({}, record)
      // this.model.status = this.status
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'code', 'name', 'remark'))
      })
    },
    onChose(checked) {
      if (checked) {
        this.status = 1
        this.visibleCheck = true
      } else {
        this.status = 0
        this.visibleCheck = false
      }
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          // values.remark = (values.remark || '').trim()
          let formData = Object.assign(that.model, values)
          formData.status = this.status
          let obj
          if (!that.model.id) {
            obj = addTrainType(formData)
          } else {
            obj = editTrainType(formData)
          }
          obj
            .then(res => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
                that.close()
              } else {
                that.$message.warning(res.message)
              }
            })
            .finally(() => {
              that.confirmLoading = false
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
    }
  }
}
</script>