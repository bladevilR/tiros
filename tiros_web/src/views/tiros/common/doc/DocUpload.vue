<template>
  <div>
    <a-row :gutter="24" style="padding-bottom: 10px;">
      <a-col :md="24" :sm="24">
        <a-upload
          :multiple="true"
          :file-list="fileList"
          :remove="handleRemove"
          :before-upload="beforeUpload"
          :default-file-list="defaultFileList"
        >
          <a-button class="border-primary-4">添加文件</a-button>
        </a-upload>
      </a-col>
    </a-row>
    <a-row>
      <a-col :span="5" v-if="showUpload">
        <a-button type="primary" :loading="isLoading" @click="beginUpload">上传至服务器</a-button>
      </a-col>
      <a-col :span="19" style="color: #cccccc">一次最多上传{{ fileSize }}个文件，且单个文件不能超过10MB</a-col>
    </a-row>
  </div>
</template>

<script>
import { uploadFile } from '@api/tirosFileApi'

export default {
  name: 'DocUpload',
  props: {
    path: {
      type: String,
      default: '',
    },
    fileSize: {
      type: Number,
      default: 5,
    },
    defaultFileList: {
      type: Array,
      default: [],
    },
    // 是否显示上传按钮，如果不显示，则需要手动调用该组件的beginUpload
    showUpload: {
      type: Boolean,
      default: true,
    },
    isFileEmpty: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    uploadFiles: {
      immediate: false,
      async handler(data) {
        if (data.length === this.fileList.length) {
          await this.$emit('uploaded', this.uploadFiles)
        }
      },
    },
  },
  data() {
    return {
      fileList: [],
      uploadFiles: [],
      isLoading: false,
    }
  },
  methods: {
    beforeUpload(file) {
      const isLt10M = file.size / 1024 / 1024 < 10
      if (!isLt10M) {
        this.$message.error('文件不得大于10MB!')
        return false
      }
      this.fileList.push(file)
      this.$emit('fileNum',this.fileList.length)
      this.$emit('setBforeUploadStatus', false)
      return false
    },
    handleRemove(file) {
      const index = this.fileList.indexOf(file)
      const newFileList = this.fileList.slice()
      newFileList.splice(index, 1)
      this.fileList = newFileList;
      this.$emit('fileNum',this.fileList.length)
      this.$emit('removed', file)
    },
    beginUpload() {
      return new Promise((resolve, reject) => { // resolve(res) 抛出成功  reject(err)抛出失败
        const { fileList, fileSize } = this
        if (!this.isFileEmpty && fileList.length === 0) {
          this.$emit('upLoadingEnd')
          this.$message.warning('请选择要上传的文件')
          reject()
          return
        }

        if (fileList.length > fileSize) {
          this.$message.error('一次最多上传' + fileSize + '个文件!')
          reject()
        } else {
          this.customRequest().then(()=>{
            resolve()
          }).catch((err) => {
            reject()
          });
        }
      });
    },
    // 上传提交
    customRequest() {
      let { fileList } = this
      // 如果文件列表为空, 直接触发上传成功
      if (fileList.length < 1) {
        this.$emit('uploaded', [])
        this.$emit('upLoadingEnd')
        return Promise.resolve()
      }
      this.isLoading = true
      let promiseList = []
      fileList.forEach((file) => {
        const formData = new FormData()
        formData.append('file', file)
        promiseList.push(this.saveFile(formData, file))
      })
      return Promise.all(promiseList).finally(() => {
        this.isLoading = false
        this.$emit('upLoadingEnd')
      })
    },
    async saveFile(formData, file) {
      return uploadFile(formData, this.path).then((res) => {
        if (res.success) {
          this.getUploadData(file, res)
          this.$emit('setUpLoadingEndStatus', true)
        } else {
          this.$message.error(res.message)
          this.$emit('uploadFail', file)
        }
      })
    },
    //组装文件信息
    getUploadData(file, res) {
      let { uploadFiles } = this
      let fileInfo = {
        name: file.name,
        type: this.handleFileName(file),
        fileSize: Math.round(file.size / 1024),
        savepath: res.result,
      }
      uploadFiles.push(fileInfo)
    },
    //获取文件类型
    handleFileName(file) {
      let fileName = file.name.lastIndexOf('.')
      let fileNameLength = file.name.length
      let fileFormat = file.name.substring(fileName + 1, fileNameLength)
      return fileFormat
    },
  },
}
</script>

<style scoped lang="less">
#fileTag {
  margin-top: 10px;
  margin-left: 8px;

  .ant-tag {
    height: 30px;
    padding-top: 5px;
  }
}
</style>