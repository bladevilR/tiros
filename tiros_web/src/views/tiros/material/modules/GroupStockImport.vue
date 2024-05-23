<template>
  <a-modal title="导入EXCEL" :width="600" :visible="visible" :confirmLoading="uploading" @cancel="handleClose">
    <a-form-model :form="form">
      <a-row>
        <a-col>
          <a-form-model-item prop="fileList" :labelCol="labelCol" :wrapperCol="wrapperCol" label="请选择文件">
            <a-upload
              name="file"
              :multiple="false"
              accept=".xls,.xlsx"
              :fileList="form.fileList"
              :remove="handleRemove"
              :beforeUpload="beforeUpload"
            >
              <a-button :disabled="form.fileList.length >= sum">
                <a-icon type="upload" />
                选择导入文件
              </a-button>
            </a-upload>
          </a-form-model-item>
        </a-col>
        <a-col>
          <a-form-model-item prop="" :labelCol="labelCol" :wrapperCol="wrapperCol" label="清除已有数据">
            <a-switch v-model="form.clear" />
          </a-form-model-item>
        </a-col>
      </a-row>
    </a-form-model>
    <template slot="footer">
      <a-button @click="handleClose">关闭</a-button>
      <a-button type="primary" @click="handleImport" :disabled="form.fileList.length === 0" :loading="uploading">
        {{ uploading ? '导入中...' : '开始导入' }}
      </a-button>
    </template>
  </a-modal>
</template>

<script>
import { postAction } from '@/api/manage'
export default {
  name: 'GroupStockImport',
  props: {
    url: {
      type: String,
      default: '',
      required: false,
    },
    biz: {
      type: String,
      default: '',
      required: false,
    },
    // 上传文件数量
    sum: {
      type: Number,
      default: 1,
    },
  },
  data() {
    return {
      visible: false,
      uploading: false,
      form: {
        fileList: [],
        clear:true,
      },
      uploadAction: '',
      foreignKeys: '',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 },
      },
    }
  },
  watch: {
    url(val) {
      if (val) {
        this.uploadAction = val
      }
    },
  },
  created() {
    this.uploadAction = this.url
  },
  mounted() {
    console.log(this.form.fileList)
  },
  methods: {
    handleClose() {
      this.visible = false
    },
    show(arg) {
      Object.assign(this.form, this.$options.data().form);
      this.uploading = false
      this.visible = true
      this.foreignKeys = arg
    },
    handleRemove(file) {
      const index = this.form.fileList.indexOf(file)
      let newFileList = this.form.fileList
      newFileList.splice(index, 1)
      this.fileList = JSON.parse(JSON.stringify(newFileList))
    },
    beforeUpload(file) {
      this.form.fileList = [...this.form.fileList, file]
      return false
    },
    handleImport() {
      const fileList = this.form.fileList
      const formData = new FormData()
      if (this.biz) {
        formData.append('isSingleTableImport', this.biz)
      }
      if (this.foreignKeys && this.foreignKeys.length > 0) {
        formData.append('foreignKeys', this.foreignKeys)
      }
      fileList.forEach((file) => {
        formData.append('file', file)
      })
      formData.append('cleanup', this.form.clear)
      this.uploading = true
      postAction(this.uploadAction, formData).then((res) => {
        this.uploading = false
        if (res.success) {
          this.$message.success(res.message)
          this.visible = false
          this.$emit('ok')
          Object.assign(this.form, this.$options.data().form);
        } else {
          this.$message.warning(res.message)
        }
      })
    },
  },
}
</script>

<style scoped>
</style>