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
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结构编码">
          <a-input v-decorator.trim="[ 'code', validatorRules.code]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结构名称">
          <a-input v-decorator.trim="[ 'name', validatorRules.name]" />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属线路">
<!--          <j-dict-select-tag
            triggerChange
            v-decorator="['lineId', validatorRules.lineId]"
            dictCode="bu_mtr_line,line_name,line_id"
          />-->
          <line-select-list
            v-decorator="['lineId', validatorRules.lineId]">
          </line-select-list>
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
import { addTrainStruct, editTrainStruct, delTrainStruct } from '@/api/tirosApi'
import { duplicateCheck } from '@api/api'
import { everythingIsEmpty } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'LeftItemModal',
  components: {LineSelectList},
  data() {
    return {
      value: 1,
      title: '操作',
      visibleCheck: true,
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
        code: { rules: [{ required: true, message: '请输入结构编码!' },{validator: this.validateCode},{max:32,message: '输入长度不能超过32字符!'}] },
        name: { rules: [{ required: true, message: '请输入结构名称!' },{max:32,message: '输入长度不能超过32字符!'}] },
        lineId: { rules: [{ required: true, message: '请选择所属线路!' }] },
        remark: { rules: [{max:255,message: '输入长度不能超过255字符!'}] },
      },
    }
  },
  created() {},
  methods: {
    validateCode(rule, value, callback){
      let params = {
        tableName: "bu_train_structure",
        fieldName: "code",
        fieldVal: value,
        dataId: this.model.id
      }
      if(!everythingIsEmpty(value)){
        duplicateCheck(params).then((res)=>{
          if(res.success){
            callback()
          }else{
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
        this.visiblekey = true
      } else {
        this.visiblekey = false
      }
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'code', 'name', 'lineId', 'remark'))
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
          console.log(formData)
          //   formData.status = this.status
          let obj
          if (!that.model.id) {
            obj = addTrainStruct(formData)
          } else {
            obj = editTrainStruct(formData)
          }
          obj
            .then((res) => {
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
    },
  },
}
</script>