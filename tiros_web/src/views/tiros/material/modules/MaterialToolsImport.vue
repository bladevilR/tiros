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
        <a-form-model-item prop="type" :labelCol="labelCol" :wrapperCol="wrapperCol" label="类型">
          <j-dict-select-tag
            v-model="form.type"
            :triggerChange="true"
            placeholder="请选择"
            dictCode="bu_tools_type"
          />
        </a-form-model-item>
        <a-col>
          <a-form-model-item prop="lineId" :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属线路">
            <line-select-list @change="(e)=>form.lineId = e" :value="form.lineId"></line-select-list>
          </a-form-model-item>
        </a-col>
        <a-col>
          <a-form-model-item prop="workshopId" :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属车间">
            <j-dict-select-tag
              v-model="form.workshopId"
              :triggerChange="true" 
              placeholder="请选择"
              dictCode="bu_mtr_workshop,name,id"
            />
          </a-form-model-item>
        </a-col>
      </a-row>
    </a-form-model> 
    <template slot="footer">
      <a-button @click="handleClose">关闭</a-button>
      <a-button type="primary" @click="handleImport" :disabled="form.fileList.length === 0 || !form.lineId || !form.workshopId || !form.type" :loading="uploading">
        {{ uploading ? '导入中...' : '开始导入' }}
      </a-button>
    </template>
  </a-modal>
</template>

<script>
import { postAction } from '@/api/manage'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'MaterialToolsImport',
  components:{LineSelectList},
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
      //   form: this.$form.createForm(this),
      visible: false,
      uploading: false,
      form: {
        fileList: [],
        lineId: undefined,
        workshopId: undefined,
        type:undefined,
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
      formData.append('lineId', this.form.lineId)
      formData.append('workshopId', this.form.workshopId)
      formData.append('type', this.form.type)
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