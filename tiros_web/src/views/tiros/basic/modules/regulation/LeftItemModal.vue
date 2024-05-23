<template>
  <a-modal
    :title="title"
    width="80%"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row style="width: 100%">
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="规程编码">
              <a-input v-decorator.trim="['code', validatorRules.code]" />
            </a-form-item>
          </a-col>
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="规程名称">
              <a-input v-decorator.trim="['name', validatorRules.name]" />
            </a-form-item>
          </a-col>

          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属修程">
              <j-dict-select-tag
                triggerChange
                v-decorator="['repairProId', validatorRules.repairProId]"
                dictCode="bu_repair_program,name,id"
                @change="handlerRepairPro"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属车间">
              <j-dict-select-tag
                triggerChange
                v-decorator="['workshopId', validatorRules.workshopId]"
                dictCode="bu_mtr_workshop,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属线路">
              <line-select-list
                :trigger-change="true"
                v-decorator="['lineId', validatorRules.lineId]"
                @change="handlerLine"
              >
              </line-select-list>
            </a-form-item>
          </a-col>
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="版本号">
              <a-input v-decorator="['version', validatorRules.version]" />
            </a-form-item>
          </a-col>
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注描述">
              <a-input v-decorator="['remark', validatorRules.remark]" />
            </a-form-item>
          </a-col>
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="导入规程">
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
import pick from 'lodash.pick'
import { saveRegu, copyRegu, delRegu, importRegu } from '@/api/tirosApi'
import { duplicateCheck } from '@/api/api'
import { everythingIsEmpty } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'LeftItemModal',
  components: { LineSelectList },
  data() {
    return {
      value: 1,
      title: '操作',
      visibleCheck: true,
      visible: false,
      model: {},
      fileList: [],
      trainTypeId: '',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        code: {
          rules: [
            { required: true, message: '请输入规程编码!' },
            { validator: this.validateCode },
            {
              max: 32,
              message: '输入长度不能超过32字符!',
            },
          ],
        },
        name: {
          rules: [
            { required: true, message: '请输入规程名称!' },
            { max: 32, message: '输入长度不能超过32字符!' },
          ],
        },
        repairProId: { rules: [{ required: true, message: '请选择所属修程!' }] },
        lineId: { rules: [{ required: true, message: '请选择所属线路!' }] },
        workshopId: { rules: [{ required: true, message: '请选择所属车间!' }] },
        remark: { rules: [{ max: 200, message: '输入长度不能超过200字符!' }] },
        version: { rules: [{max: 16, message: '输入长度不能超过16字符!'}]}
      },
    }
  },
  created() {},
  methods: {
    handlerRepairPro(e, options) {
      console.log(options)
      if (options) this.model.repairProName = options.title
    },
    handlerLine(e, options) {
      if (options) this.trainTypeId = options.trainTypeId
    },
    handleRemove(file) {
      const index = this.fileList.indexOf(file)
      const newFileList = this.fileList.slice()
      newFileList.splice(index, 1)
      this.fileList = newFileList
    },
    beforeUpload(file) {
      if (this.model.id) {
        this.$message.warn('该操作会覆盖原有的规程项,请谨慎使用!')
      }
      const isLt10M = file.size / 1024 / 1024 < 10
      if (!isLt10M) {
        this.$message.error('文件不得大于10MB!')
        return
      }
      const isExcel =
        file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
        file.type === 'application/vnd.ms-excel'
      if (!isExcel) {
        this.$message.error('只支持Excel文件导入!')
        return
      }
      this.$set(this.fileList, 0, file)
      return false
    },
    customRequest(params) {
      // 上传提交
      let { fileList } = this
      fileList.forEach((file) => {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('name', params.name)
        formData.append('code', params.code)
        formData.append('repairProId', params.repairProId)
        formData.append('lineId', params.lineId)
        formData.append('workshopId', params.workshopId)
        formData.append('trainTypeId', this.trainTypeId)
        formData.append('version', params.version)
        formData.append('remark', params.remark)
        if (this.model.id) {
          formData.append('id', this.model.id)
        } else {
          formData.append('id', '')
        }
        importRegu(formData)
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
    validateCode(rule, value, callback) {
      let params = {
        tableName: 'bu_repair_regu_info',
        fieldName: 'code',
        fieldVal: value,
        dataId: this.model.id,
      }
      if (!everythingIsEmpty(value)) {
        duplicateCheck(params).then((res) => {
          if (res.success) {
            callback()
          } else {
            callback(res.message)
          }
        })
      } else {
        callback()
      }
    },
    add() {
      this.edit({})
    },
    edit(record) {
      if (record.id) {
        this.visiblekey = true
      } else {
        this.visiblekey = false
      }
      this.form.resetFields()
      // console.log(record)
      // console.log(this.form)
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'code', 'name', 'lineId', 'workshopId', 'repairProId','version', 'remark'))
      })
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values)
          if (that.fileList.length > 0) {
            that.customRequest(formData)
          } else {
            saveRegu(formData).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok', true)
                that.confirmLoading = false
                that.close()
              } else {
                that.$message.warning(res.message)
              }
            })
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