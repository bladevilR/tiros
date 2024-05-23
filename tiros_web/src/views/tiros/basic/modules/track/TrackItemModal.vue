<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属车辆段">
          <j-dict-select-tag
            v-decorator.trim="[ 'depotId', validatorRules.depotId]"
            placeholder="请选择"
            :triggerChange="true"
            dictCode="bu_mtr_depot,name,id"
            @change="handleDep"
          />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="编号">
          <a-input placeholder="编号" v-decorator.trim="[ 'code', validatorRules.code]"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="状态">
          <!-- <a-input placeholder="状态" v-decorator.trim="[ 'status', validatorRules.status]" /> -->
          <j-dict-select-tag
            v-decorator.trim="[ 'status', validatorRules.status]"
            :triggerChange="true"
            placeholder="请选择股道状态"
            dictCode="bu_valid_status"
          />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
          <a-textarea v-decorator.trim="[ 'remark', validatorRules.remark]"/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { addTrack, editTrack } from '@/api/tirosApi'
import { duplicateCheck } from '@/api/api'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'TrackModal',
  data() {
    return {
      value: 1,
      title: '操作',
      visible: false,
      depId:'',
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
          rules: [{ required: true, message: '请输入编号!' }, { validator: this.validateCode }, {
            max: 32,
            message: '输入长度不能超过32字符!'
          }]
        },
        status: { rules: [{ required: true, message: '请选择状态!' }] },
        depotId: { rules: [{ required: true, message: '请选择所属车辆段!' }] },
        remark: { rules: [{ max: 256, message: '输入长度不能超过255字符!' }] }
      }
    }
  },
  created() {
  },
  methods: {
    handleDep(value){
      this.depId=value;
    },
    validateCode(rule, value, callback) {
      let params = {
        tableName: 'bu_mtr_track',
        fieldName: 'code',
        fieldVal: value,
        dataId: this.model.id,
        filterFields: [{ name: 'depot_id', val: this.model.depotId?this.model.depotId:this.depId}]
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
        this.visiblekey = true
      } else {
        this.visiblekey = false
      }
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          code: this.model.code,
          depotId: this.model.depotId,
          status: this.model.status,
          remark: this.model.remark
        })
      })
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values)
          let obj
          if (!that.model.id) {
            obj = addTrack(formData)
          } else {
            obj = editTrack(formData)
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