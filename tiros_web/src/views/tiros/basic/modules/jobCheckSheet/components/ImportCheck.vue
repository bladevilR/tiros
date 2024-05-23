<template>
  <a-modal
    :title="title"
    :width="450"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :span="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
              <line-select-list v-decorator.trim="['lineId', validatorRules.lineId]"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="修程类型">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['repairProgramId', validatorRules.repairProgramId]"
                placeholder="请选择修程"
                dictCode="bu_repair_program,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="文件上传" required>
              <a-upload
                :multiple="false"
                :file-list="fileList"
                accept=".docx,.doc"
                :remove="handleRemove"
                :before-upload="beforeUpload"
              >
                <a-button type="primary">添加文件</a-button>
              </a-upload>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import DocUpload from '@views/tiros/common/doc/DocUpload'
import { importWorkCheck } from '@api/tirosApi'

export default {
  name: 'ImportCheck',
  components: { LineSelectList, DocUpload },
  data() {
    return {
      title: '导入',
      visible: false,
      model: {},
      fileList: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        lineId: { rules: [{ required: true, message: '请选择线路!' }] },
        repairProgramId: { rules: [{ required: true, message: '请选择修程!' }] },
      },
    }
  },
  created() {},
  methods: {
    show() {
      this.form.resetFields()
      this.visible = true
    },
    handleRemove(file) {
      const index = this.fileList.indexOf(file)
      const newFileList = this.fileList.slice()
      newFileList.splice(index, 1)
      this.fileList = newFileList
    },
    beforeUpload(file) {
      const isLt10M = file.size / 1024 / 1024 < 10
      if (!isLt10M) {
        this.$message.error('文件不得大于10MB!')
        return
      }
      const isExcel =
        file.type === 'application/msword' ||
        file.type === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
      if (!isExcel) {
        this.$message.error('只支持Word或Excel文件导入!')
        return
      }
      this.$set(this.fileList, 0, file)
      return false
    },
    customRequest(params) {
      this.confirmLoading=true
      // 上传提交
      let { fileList } = this
      fileList.forEach((file) => {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('lineId', params.lineId)
        formData.append('repairProId', params.repairProgramId)
        // console.log(formData)
        importWorkCheck(formData)
          .then((res) => {
            if (res.success) {
              this.$message.success(res.message)
              this.$emit('ok')
              this.close()
            } else {
              this.$message.error(res.message)
            }
          })
          .finally(() => {
            this.confirmLoading = false
          })
      })
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          let formData = Object.assign(that.model, values)
          if (that.fileList.length > 0) {
            that.customRequest(formData)
          } else {
            this.$message.error('请您先添加上传文件！')
          }
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
      this.fileList = []
    },
  },
}
</script>