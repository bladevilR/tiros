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
          <a-col :md="10" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属修程">
              <j-dict-select-tag
                triggerChange
                v-decorator="['repairProId', validatorRules.repairProId]"
                dictCode="bu_repair_program,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="10" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属线路">
              <line-select-list
                :trigger-change="true"
                v-decorator="['lineId', validatorRules.lineId]"
                @change="handlerLine">
              </line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="10" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属车间">
              <j-dict-select-tag
                triggerChange
                v-decorator="['workshopId', validatorRules.workshopId]"
                dictCode="bu_mtr_workshop,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="14" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-upload
                :multiple="false"
                :file-list="fileList"
                accept=".xlsx,.xls"
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
import moment from 'moment'
import JUpload from '@/components/jeecg/JUpload'
import { randomUUID } from '@/utils/util'
import { uploadFile } from '@api/tirosFileApi'
import { addFile, importRegu } from '@api/tirosApi'
import { addPerformOtherResource, addResource } from '@api/tirosOutsourceApi'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'ImportModal',
  components: { JUpload, LineSelectList },
  data () {
    return {
      title: '',
      visible: false,
      confirmLoading: false,
      fileList: [],
      form: this.$form.createForm(this),
      trainTypeId: '',
      validatorRules: {
        repairProId: { rules: [{ required: true, message: '请选择所属修程!' }] },
        lineId: { rules: [{ required: true, message: '请所属线路!' }] },
        workshopId: { rules: [{ required: true, message: '请选择所属车间!' }] }
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
  methods: {
    handlerLine (e, options) {
      if (options) this.trainTypeId = options.trainTypeId
    },
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
        return
      }
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        || file.type === 'application/vnd.ms-excel application/x-excel'
      if (!isExcel) {
        this.$message.error('只支持Excel文件导入!')
        return
      }
      // this.fileList = [...this.fileList, file]
      this.$set(this.fileList, 0, file)
      return false
    },
    customRequest (params) { // 上传提交
      let { fileList } = this
      fileList.forEach(file => {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('repairProId', params.repairProId)
        formData.append('lineId', params.lineId)
        formData.append('workshopId', params.workshopId)
        formData.append('trainTypeId', this.trainTypeId)
        this.confirmLoading = true
        importRegu(formData).then((res) => {
          if (res.success) {
            this.$message.success(res.message)
            this.close()
            this.$emit('ok')
          } else {
            this.$message.error(res.message)
          }
        }).finally(() => {
          this.confirmLoading = false
        })
      })
    },

    showModal () {
      this.visible = true
    },

    handleOk () {
      this.form.validateFields((err, values) => {
        if (!err) {
          if (this.fileList.length < 1) {
            this.$message.warn('请添加文件!')
          } else {
            let params = Object.assign({}, values)
            this.params = params
            this.customRequest(params)
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
      this.fileList=[]
    }
  }
}
</script>

<style scoped>

</style>