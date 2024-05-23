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
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="系统编码">
          <a-input v-decorator.trim="[ 'code', validatorRules.code]"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="系统名称">
          <a-input v-decorator.trim="[ 'name', validatorRules.name]"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="短名称">
          <a-input v-decorator.trim="[ 'shortName' ]"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="排序">
          <a-input   v-decorator="[ 'sortNum',validatorRules.sortNum]"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注描述">
          <a-textarea v-decorator="[ 'remark',validatorRules.remark]"/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import pick from 'lodash.pick'
import { addTrainTypeSys, editTrainTypeSys } from '@/api/tirosApi'
import { duplicateCheck } from '@/api/api'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'TrainTypeSysItemModal',
  props: ['trainTypeId'],
  data() {
    return {
      value: 1,
      title: '操作',

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
        code: {
          rules: [{ required: true, message: '请输入内容!' }, { validator: this.validateCode }, {
            max: 32,
            message: '输入长度不能超过32字符!'
          }]
        },
        name: { rules: [{ required: true, message: '请输入内容!' }, { max: 32, message: '输入长度不能超过32字符!' }] },
        remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] },
        sortNum: { rules: [{ max: 16, message: '输入长度不能超过16字符!' }] }
      }
    }
  },
  created() {
  },
  methods: {
    validateCode(rule, value, callback) {
      let params = {
        tableName: 'bu_train_asset_type',
        fieldName: 'code',
        fieldVal: value,
        dataId: this.model.id,
       /* filterFields: [{ name: 'train_type_id', val: this.model.trainTypeId }]*/
      }
      if(!everythingIsEmpty(value)){
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
      this.form.resetFields()
      this.model = Object.assign({}, record, { 'trainTypeId': this.trainTypeId })
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'code', 'name','shortName', 'remark','sortNum'))
      })
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          // values.remark = (values.remark || '').trim()
          let formData = Object.assign(that.model, values)
          let obj
          if (!that.model.id) {
            obj = addTrainTypeSys(formData)
          } else {
            obj = editTrainTypeSys(formData)
          }
          obj
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok', that.model.id ? that.model : undefined)
                that.close()
              } else {
                that.$message.error(res.message)
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