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
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="修程类型">
              <j-dict-select-tag
                triggerChange
                v-decorator="['repairProgramId', validatorRules.repairProgramId]"
                dictCode="bu_repair_program,name,id"
                @change="handleProgram"
              />
            </a-form-item>
          </a-col>
          <a-col :md="10" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="编组数量">
              <a-input-number :min="1" :max="99" style="width: 100%"
                              v-decorator="[ 'groupQuantity', validatorRules.groupQuantity]" />
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
          <a-col  :md="10" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否有效">
              <a-switch v-model="status" @change="handleStatus" />
            </a-form-item>
          </a-col>
          <a-col  :md="10" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="基准日期">
              <a-date-picker style="width: 100%" v-decorator="[ 'baseDate', validatorRules.baseDate]" />
            </a-form-item>
          </a-col>
          <a-col  :md="10" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联规程">
              <j-dict-select-tag
                triggerChange
                v-decorator="['reguId', validatorRules.reguId]"
                :dictCode="dicStr"
                @focus="handleFocusRegu"
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
import { everythingIsEmpty, randomUUID } from '@/utils/util'
import { uploadFile } from '@api/tirosFileApi'
import { addFile, importTpPlan, importRegu } from '@api/tirosApi'
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
      dicStr: '',
      status:true,
      validatorRules: {
        lineId: { rules: [{ required: true, message: '请所属线路!' }] },
        groupQuantity: { rules: [{ required: true, message: '请输入编组数量!' }] },
        repairProgramId: { rules: [{ required: true, message: '请选择修程类型!' }] },
        reguId: { rules: [{ required: true, message: '请选择关联规程!' }] },
        baseDate: { rules: [{ required: true, message: '请选择基准日期!' }] },
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
    handleStatus (value) {
      this.status = value
    },
    handleFocusRegu () {
      let programId = this.form.getFieldValue('repairProgramId')
      if (everythingIsEmpty(programId)) this.$message.warn('请先选择修程类型')
    },
    handleProgram (data) {
      if (data) {
        this.dicStr = `bu_repair_regu_info,name,id,status=1 and repair_pro_id='${data}'`
      } else {
        this.form.setFieldsValue({ reguId: '' })
        this.dicStr = ''
      }
    },
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
        formData.append('lineId', params.lineId)
        formData.append('groupQuantity', params.groupQuantity)
        formData.append('repairProgramId', params.repairProgramId)
        formData.append('reguId', params.reguId)
        formData.append('status', params.status)
        formData.append('trainTypeId', params.trainTypeId)
        formData.append('baseDate', params.baseDate)
        this.confirmLoading = true
        importTpPlan(formData).then((res) => {
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
            let params = Object.assign({}, values,{
              trainTypeId:this.trainTypeId,
              status:this.status?1:0,
              baseDate: moment(values.baseDate).format('YYYY-MM-DD'),
            })
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