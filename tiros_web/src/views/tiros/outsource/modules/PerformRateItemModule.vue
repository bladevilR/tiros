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
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="评分项">
              <j-dict-select-tag
                v-decorator.trim="[ 'rateingItem',validatorRules.rateingItem]"
                :triggerChange="true"
                placeholder="请选择"
                dictCode="bu_rateing_item"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="评分分值">
              <a-input-number :min="0" :max="100" style="width: 100%" placeholder="请输入评分值"
                              :precision="0"
                              v-decorator.trim="[ 'score',validatorRules.score]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="评分日期">
              <a-date-picker
                style="width: 100%"
                v-decorator.trim="[ 'rateDate',validatorRules.rateDate]"
                placeholder="请选择"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24" align="left">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="评分说明">
              <a-textarea
                v-decorator.trim="[ 'rateDesc',validatorRules.rateDesc]"
                placeholder="请输入评分说明"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="22" :sm="24">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1">
              <DocUpload
                  ref="upload"
                  :defaultFileList="defaultFileList"
                  @fileNum="fileNumChange"
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
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>

</template>

<script>
import {
  addRate, editRate
} from '@api/tirosOutsourceApi'
import moment from 'moment'
import { randomUUID } from '@/utils/util'
import DocUpload from '@views/tiros/common/doc/DocUpload'

export default {
  name: 'PerformRateItemModal',
  props: ['contractId'],
  components:{DocUpload},
  data () {
    return {
      title: '操作',
      visible: false,
      model: {},
      fileList: [],
      defaultFileList:[],
      fileType: null,
      saveFlag:true,
      fileNum:0,
      rateId: '',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 }
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 3 }
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 15 }
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        score: { rules: [{ required: true, message: '请输入评分分值!' }] },
        rateingItem: { rules: [{ required: true, message: '请选择评分项!' }] },
        rateDesc: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] },
        rateDate: { rules: [{ required: true, message: '请输入评分日期!' }] }
      }
    }
  },
  created () {
  },
  methods: {
    add () {
      this.edit({})
    },
    edit (record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.model.annexes=[]
      this.$nextTick(() => {
        this.form.setFieldsValue({
          rateingItem: this.model.rateingItem,
          score: this.model.score,
          rateDate: moment(this.model.rateDate || undefined),
          rateDesc: this.model.rateDesc
        })
      })
    },
    fileNumChange(e){
      console.log(e);
      this.fileNum = e;
    },
    // 确定
    handleOk () {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let submitFun = ()=>{
            let formData = Object.assign(that.model, values, {
                rateDate: moment(values.rateDate).format('YYYY-MM-DD'),
                contractId: that.contractId,
                annexes:that.fileList
              })
              let obj
              if (!that.model.id) {
                obj = addRate(formData)
              } else {
                obj = editRate(formData)
              }
              obj.then((re) => {
                if (re.success) {
                  that.$message.success(re.message)
                  that.$emit('ok')
                  that.close()
                } else {
                  that.$message.error(re.message)
                }
              }).finally(() => {
                that.confirmLoading = false
              })
          }
          // 不上传文件
          if (that.fileNum>0) {
            // 上传文件
            that.$refs.upload.beginUpload().then(()=>{
              if (that.saveFlag) {
                submitFun()
              }
            }).catch((err) => {
              that.confirmLoading = false
              that.$message.error('上传失败!')
            });
          }else{
            submitFun()
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
      this.fileNum = 0;
      this.visible = false
      this.fileList = []
      this.saveFlag=false
    },

    setBforeUploadStatus (val) {
      console.log(val)
      this.saveFlag = val
    },
    setUpLoadingEndStatus (val) {
      console.log(val)
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
        })
        this.fileList.push(item)
      })
    },
    uploadFail (file) {
      this.confirmLoading = false
    },
    onRemoveFile (file) {
    },
  }
}
</script>