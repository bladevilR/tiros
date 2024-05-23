<template>

  <a-drawer
    :title="title"
    :width="800"
    :maskClosable="true"
    :closable="true"
    :visible="visible"
    @close="handleCancel"
    :confirmLoading="confirmLoading"
    :destroyOnClose="true"
  >
<!--  <a-modal
    :title="title"
    :width="600"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >-->
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="记录表编码">
          <a-input v-decorator.trim="[ 'code', validatorRules.code]"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="记录表名称">
          <a-input v-decorator.trim="[ 'title', validatorRules.title]"/>
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="技术规程">
          <a-select
            allowClear
            v-decorator.trim="[ 'reguId', validatorRules.reguId]"
            @change="reguDetaiChange"
          >
            <a-select-option v-for="(item,k) in treeDataSource" :key="k" :value="item.id">
              {{item.name + (item.version ? `(${item.version})` : '') }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属线路">
          <j-dict-select-tag
            :triggerChange="true"
            v-decorator.trim="[ 'lineId', validatorRules.lineId]"
            placeholder="请选择"
            @select="onLineSelect"
            dictCode="bu_mtr_line,line_name,line_id|train_type_id"
          />
        <!-- <line-select-list   v-decorator.trim="[ 'lineId', validatorRules.lineId]" @select="onLineSelect"></line-select-list> -->
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属修程">
          <j-dict-select-tag
            ref="lineSelect"
            :triggerChange="true"
            v-decorator.trim="[ 'repairProId', validatorRules.repairProId]"
            placeholder="请选择"
            dictCode="bu_repair_program,name,id"
          />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联设备">
          <a-select :disabled="!canSelectType" ref="assetTypeName" v-decorator.trim="[ 'assetTypeName', {}]"
                    placeholder="请选择关联设备" :open="false" style="width: 100%" @focus="openAssetTypeModal()">
            <a-icon slot="suffixIcon" type="ellipsis"/>
          </a-select>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否拆装">
          <a-switch v-model="updown"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否有效">
          <a-switch @change="switchChange" v-model="switchCheck"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注描述">
          <a-input v-decorator.trim="[ 'remark', validatorRules.remark]"/>
        </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="导入明细">
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
      </a-form>
    </a-spin>
    <div class="drawer-bootom-button">
        <a-button style="margin-right: .8rem"  @click="handleCancel">取消</a-button>
      <a-button @click="handleOk" type="primary" :loading="confirmLoading">提交</a-button>
    </div>
    <train-asset-type ref="assetTypeModal" title="设备选择" :multiple="false" :trainTypeId="trainTypeId"
                      @ok="onAssetTypeSelect" @cancel="onCancelAssetTypeSelect"></train-asset-type>
  </a-drawer>
</template>

<script>
import pick from 'lodash.pick'
import { importTpPlan, importWorkRecord, saveOldWorkRecord,getReguList } from '@/api/tirosApi'
import { duplicateCheck } from '@/api/api'
import TrainAssetType from '@views/tiros/common/selectModules/TrainAssetType'
import { everythingIsEmpty } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'SheetItemModal',
  components: { TrainAssetType ,LineSelectList},
  comments: { TrainAssetType },
  data() {
    return {
      treeDataSource: [],
      value: 1,
      title: '操作',
      status: 1,
      trainTypeId: '',
      canSelectType: true,
      switchCheck: true,
      visible: false,
      model: {},
      fileList: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        code: {
          rules: [{ required: true, message: '请输入记录表编号!' }, { validator: this.validateCode }, {
            max: 16, message: '输入长度不能超过16字符!'
          }]
        },
        title: { rules: [{ required: true, message: '请输入记录表名称!' }, { max: 64, message: '输入长度不能超过64字符!' }] },
        lineId: { rules: [{ required: true, message: '请选择所属线路!' }] },
        repairProId: { rules: [{ required: true, message: '请选择所属修程!' }] },
        remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] }
      },
      assetTypeId: '',
      updown: false
    }
  },
  methods: {
    reguDetaiChange(e){
      if(e){
        let arr = this.treeDataSource.filter((item,index)=>{
          return item.id == e;
        })
        if(arr.length>0){
          arr = arr[0];
          console.log(arr)
          this.form.setFieldsValue({
            lineId:arr.lineId,
            repairProId:arr.repairProId,
          })
        }
      }
    },
    queryTreeData() {
      getReguList()
        .then((res) => {
          if (res.success) {
            this.treeDataSource = res.result
          }
        })
    },
    handleRemove (file) {
      const index = this.fileList.indexOf(file)
      const newFileList = this.fileList.slice()
      newFileList.splice(index, 1)
      this.fileList = newFileList
    },
    beforeUpload (file) {
      if (this.model.id) {
        this.$message.warn('该操作会覆盖原有的数据,请谨慎操作!')
      }
      const isLt10M = file.size / 1024 / 1024 < 10
      if (!isLt10M) {
        this.$message.error('文件不得大于10MB!')
        return
      }
      const isExcel = file.type === 'application/msword'
        || file.type === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
      if (!isExcel) {
        this.$message.error('只支持Excel文件导入!')
        return
      }
      this.$set(this.fileList, 0, file)
      return false
    },
    customRequest (params) { // 上传提交
      let { fileList } = this
      fileList.forEach(file => {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('lineId', params.lineId)
        formData.append('repairProId', params.repairProId)
        formData.append('status', params.status)
        formData.append('updown', params.updown)
        formData.append('assetTypeId', params.assetTypeId?params.assetTypeId:'')
        formData.append('code', params.code)
        formData.append('title', params.title)
        formData.append('reguId', params.reguId)
        formData.append('createGroupId', params.createGroupId)
        formData.append('remark', params.remark?params.remark:'')
        if (this.model.id) {
          formData.append('id', this.model.id)
        } else {
          formData.append('id', '')
        }
        importWorkRecord(formData).then((res) => {
          if (res.success) {
            this.$message.success(res.message)
            this.$emit('ok')
            this.close()
          } else {
            this.$message.error(res.message)
          }
        }).finally(() => {
          this.confirmLoading = false
        })
      })
    },
    validateCode(rule, value, callback) {
      let params = {
        tableName: 'bu_work_record',
        fieldName: 'code',
        fieldVal: value,
        dataId: this.model.id
      }
      if(!everythingIsEmpty(value)) {
        duplicateCheck(params).then((res) => {
          if (res.success) {
            callback()
          } else {
            callback(res.message)
          }
        })
      }else {callback()}
    },
    add() {
      this.edit({})
    },
    edit(record) {
      this.queryTreeData();
      if (record.id) {
        this.switchCheck = record.status == 1 ? true : false
        this.updown = record.updown == 1 ? true : false
      }
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.assetTypeId = this.model.assetTypeId
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'code', 'title', 'remark','reguId', 'lineId', 'repairProId', 'assetTypeName'))
      })
    },
    switchChange(checked) {
      if (checked) {
        this.status = 1
        this.switchCheck = true
      } else {
        this.status = 0
        this.switchCheck = false
      }
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values)
          formData.status = this.status
          formData.assetTypeId = this.assetTypeId
          formData.updown = this.updown ? 1 : 0
          if (!formData.createGroupId) {
            formData.createGroupId=this.$store.getters.userInfo.departId
          }
          if (that.fileList.length > 0) {
            that.customRequest(formData)
          } else {
            saveOldWorkRecord(formData).then((res) => {
                if (res.success) {
                  that.$message.success(res.message)
                  that.$emit('ok')
                  that.close()
                } else {
                  that.$message.error(res.message)
                }
              })
              .finally(() => {
                that.confirmLoading = false
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
      this.fileList=[]
    },
    // 设备选择回调
    onAssetTypeSelect(data) {
      if (data.length) {
        const item = data[0]
        if (item) {
          //item.id,   item.name
          this.$nextTick(() => {
            this.form.setFieldsValue({ 'assetTypeName': item.name })
            this.assetTypeId = item.id
          })
        } else {
          this.$nextTick(() => {
            this.form.setFieldsValue({ 'assetTypeName': '' })
            this.assetTypeId = ''
          })
        }
      } else {
        this.$nextTick(() => {
          this.form.setFieldsValue({ 'assetTypeName': '' })
          this.assetTypeId = ''
        })
      }
    },
    // 设备选择取消回调
    onCancelAssetTypeSelect() {
    },
    openAssetTypeModal() {
      this.$refs.assetTypeModal.showModal()
      this.$refs.assetTypeName.blur()
    },
    onLineSelect(v, op) {
      if (op) {
        this.trainTypeId = op.extFields[0]['train_type_id']
        this.canSelectType = true
      } else {
        this.trainTypeId = null
        this.canSelectType = false
      }
      if (v !== this.model.lineId) {
        this.form.setFieldsValue({ 'assetTypeName': '' })
        this.assetTypeId = ''
      }
    }
  }
}
</script>

<style scoped>
.drawer-bootom-button {
  position: absolute;
  bottom: 0;
  width: 100%;
  border-top: 1px solid #e8e8e8;
  padding: 10px 16px;
  text-align: right;
  left: 0;
  background: #fff;
  border-radius: 0 0 2px 2px;
}
</style>