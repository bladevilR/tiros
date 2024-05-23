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
        <a-row :gutter="24">
          <a-col :md="14" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-upload
                :multiple="true"
                :file-list="fileList"
                :remove="handleRemove"
                :before-upload="beforeUpload"
              >
                <a-button type="primary">添加文件</a-button>
              </a-upload>
            </a-form-item>
          </a-col>
          <a-col :md="10" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="资料类型">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['fileType',validatorRules.fileType]"
                placeholder="请选择"
                dictCode="bu_outin_other_file_type"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col>一次最多上传5个文件，且单个文件不能超过10MB</a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import moment from 'moment'
import JUpload from '@/components/jeecg/JUpload'
import { randomUUID } from '@/utils/util'
import { uploadFile } from '@api/tirosFileApi'
import { addFile } from '@api/tirosApi'
import { addPerformOtherResource, addResource } from '@api/tirosOutsourceApi'

export default {
  name: 'SupplierUploadModule',
  components: { JUpload },
  data () {
    return {
      title: '文件上传',
      visible: false,
      confirmLoading: false,
      fileList: [],
      uploadData: [],
      fileType: null,
      fileSize: 5,
      outinDetailId: '',
      form: this.$form.createForm(this),
      validatorRules: {
        fileType: { rules: [{ required: true, message: '请选择资料类型!' }] }
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 }
      }
    }
  },
  watch: {
    uploadData: {
      immediate: false,
      handler (data) {
        if (data.length === this.fileList.length && data.length > 0) {
              addPerformOtherResource(this.uploadData).then((re) => {
                if (re.success) {
                  this.$message.success(re.message)
                  this.$emit('ok')
                  this.close()
                } else {
                  this.$message.error(re.message)
                }
              })
        }
      }
    }
  },
  methods: {
    handleRemove (file) {
      const index = this.fileList.indexOf(file)
      const newFileList = this.fileList.slice()
      newFileList.splice(index, 1)
      this.fileList = newFileList
    },
    beforeUpload (file) {
      const isLt10M = file.size / 1024 / 1024 < 10
      if (!isLt10M) {
        this.$message.error('文件不得大于10MB!')
      }
      this.fileList = [...this.fileList, file]
      return false
    },
    //获取文件类型
    handleFileName (file) {
      let fileName = file.name.lastIndexOf('.')
      let fileNameLength = file.name.length
      let fileFormat = file.name.substring(fileName + 1, fileNameLength)
      return fileFormat
    },
    customRequest (fileType) { // 上传提交
      this.confirmLoading = true
      let { fileList } = this
      fileList.forEach(file => {
        const formData = new FormData()
        formData.append('file', file)
        this.saveFile(formData, file, fileType)
      })
    },
    //组装文件信息
    getUploadData (file, res, fileType) {
      let id = randomUUID()
      let fileInfo = {
        id: id,
        fileId: id,
        fileName: file.name,
        name: file.name,
        type: this.handleFileName(file),
        fileSize: Math.round(file.size / 1024),
        savepath: res.result,
        fileType: fileType,
        outinDetailId: this.outinDetailId,
        uploadDate: moment(new Date()).format('YYYY-MM-DD')
      }
      this.uploadData.push(fileInfo)
    },
    saveFile (formData, file, fileType) {
      uploadFile(formData).then((res) => {
        if (res.success) {
          this.getUploadData(file, res, fileType)
        } else {
          this.$message.error(res.message)
        }
      })
    },

    handleAdd (value) {
      this.visible = true
      this.outinDetailId = value.outinDetailId
    },

    handleOk () {
      this.form.validateFields((err, values) => {
        if (!err) {
          const { fileList, fileSize } = this
          this.fileType = values.fileType
          if (fileList.length > fileSize) {
            this.$message.error('一次最多上传5个文件!')
          } else if (this.fileList && this.fileList.length == 0) {
            this.$message.warning('请先选择文件!')
          } else {
            this.customRequest(this.fileType)
          }
        }
      })
    },

    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
      this.fileList = []
      this.uploadData = []
      this.confirmLoading = false
    }
  }
}
</script>

<style scoped>

</style>