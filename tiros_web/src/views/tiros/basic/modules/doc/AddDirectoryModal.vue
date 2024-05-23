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
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="名称">
              <a-input placeholder="名称" v-decorator.trim="[ 'name', validatorRules.name]"/>
            </a-form-item>
          </a-col>

        </a-row >
        <a-row :gutter="24">
          <a-col :md="24" :sm="24" >
            <a-form-item  label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-textarea v-decorator="[ 'remark',validatorRules.remark]"/>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>

</template>

<script>


  import { addDirectory } from '@api/tirosApi'

  export default {
    name: 'AddDirectoryModal',
    props:['directoryId'],
    data() {
      return {
        title: '操作',
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 15 }
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules: {
          name: { rules: [{ required: true, message: '请输入目录名称!' },{max:50,message: '输入长度不能超过50字符!'}] },
          remark: { rules: [{max:255,message: '输入长度不能超过255字符!'}] },
        },
      }
    },
    created() {

    },
    methods: {
      add() {
        this.edit({})
      },
      edit(record) {
        this.form.resetFields()
        this.model = Object.assign({}, record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue({
            name: this.model.name,
            remark:this.model.remark
          })
        })
      },
      // 确定
      handleOk() {
        const that = this
        that.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true
            let formData = Object.assign(that.model, values,{
              parentId:this.directoryId })

           addDirectory(formData).then((res) => {
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