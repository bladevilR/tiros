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
    <div>
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :md="15" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol">
              <DocUpload
                ref="upload"
                :defaultFileList="defaultFileList"
                @setBforeUploadStatus="setBforeUploadStatus"
                @uploaded="successUploadFile"
                @removed="onRemoveFile"
                @uploadFail="uploadFail"
                @setUpLoadingEndStatus="setUpLoadingEndStatus"
                :show-upload="false"
                :isFileEmpty="true"
              ></DocUpload>
            </a-form-item>
          </a-col>
          <a-col :md="9" :sm="24" v-if="other">
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
      </a-form>
    </div>
  </a-modal>
</template>

<script>
import moment from 'moment'
import JUpload from '@/components/jeecg/JUpload'
import { addFile } from '@api/tirosApi'
import { uploadFile } from '@api/tirosFileApi'
import { randomUUID } from '@/utils/util'
import { addPerformOtherResource, addResource } from '@api/tirosOutsourceApi'
import DocUpload from '@views/tiros/common/doc/DocUpload'

export default {
  name: 'ResourceUploadModule',
  components: { JUpload, DocUpload },
  props: ['contractId', 'outinDetailId', 'other'],
  data () {
    return {
      title: '文件上传',
      visible: false,
      confirmLoading: false,
      fileList: [],
      defaultFileList: [],
      fileType: null,
      fileSize: 5,
      saveFlag: true,
      form: this.$form.createForm(this),
      validatorRules: {
        fileType: { rules: [{ required: true, message: '请选择资料类型!' }] }
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      }
    }
  },
  methods: {
    setBforeUploadStatus (val) {
      this.saveFlag = val
    },
    setUpLoadingEndStatus (val) {
      this.saveFlag = val
    },
    successUploadFile (fileInfos) {
      if (!fileInfos || fileInfos.length < 1) {
        return
      }
      fileInfos.map((item) => {
          Object.assign(item, {
            id: randomUUID(),
            uploadDate: moment(new Date()).format('YYYY-MM-DD'),
            outinDetailId: this.outinDetailId,
            contractId: this.contractId
          })
        this.fileList.push(item)
      })
    },
    uploadFail (file) {
      this.confirmLoading = false
    },
    onRemoveFile (file) {
    },
    async beginUpload () {
      // console.log(this.$refs.upload.beginUpload())
      return this.$refs.upload.beginUpload()
    },
    handleAdd (fileType) {
      this.visible = true
      this.fileType = fileType
    },

    handleOk () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.confirmLoading = true
          this.beginUpload().then((res)=>{
            if(this.fileList&&this.fileList.length==0){
              this.$message.warning('请先选择文件!')
              this.confirmLoading = false
            }else{
              if (this.other) {
                this.fileType = values.fileType
              }
              this.fileList.map((item) => {
                item['fileType'] = this.fileType
              })
              let obj
              if (this.other) {
                obj = addPerformOtherResource(this.fileList)
              } else {
                obj = addResource(this.fileList)
              }
              obj.then((re) => {
                if (re.success) {
                  this.$message.success(re.message)
                  this.$emit('ok')
                  this.close()
                } else {
                  this.$message.error(re.message)
                }
              }).finally(() => {
                this.confirmLoading = false
              })
            }
          }).catch((err)=>{
            this.$message.error('上传失败!')
            this.confirmLoading = false
          })
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
      this.saveFlag=false
      this.fileList = []
    }
  }
}
</script>

<style scoped>

</style>