<template>
  <a-modal
    :title="title"
    :width="535"
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
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工艺装备">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="[ 'toolsId', validatorRules.toolsId ]"
                dictCode="bu_material_tools,name,id,tool_type = 2 and group_id is null|code,model,remark"
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
  import { addTools } from '../../../../api/tirosGroupApi'

  export default {
    name: 'AddEquipInfoModal',
    props:['datagroup'],
    data() {
    return {
      title: '操作',
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 10 }
      },
      isClose: false,
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        toolsId: { rules: [{ required: true, message: '请选择工艺装备!' }] }
      }
    }
  },

  created() {

  },
  methods: {

    changeClose(checked) {
      this.isClose=checked
    },

    add() {
      this.edit({})
    },
    edit(record) {
      if (record.id) {
        this.isClose=record['close']&&record['close']==1?true:false;
      } else {
        this.isClose=false
      }
      // this.$form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          toolsId:this.model.toolsId,
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
          formData['groupId'] = this.datagroup
          console.log(formData)
          let obj
          obj = addTools(formData)
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