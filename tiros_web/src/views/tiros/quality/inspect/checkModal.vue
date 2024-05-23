<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    centered
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-row>
      <a-col :md="18">
        <div>
          <a-form :form="form">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="登录名称">
              <a-input v-decorator.trim="['checkUserName', validatorRules.checkUserName]" />
            </a-form-item>
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="登录密码">
              <a-input-password v-decorator.trim="['checkUserPwd', validatorRules.checkUserPwd]" />
            </a-form-item>
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结果">
              <a-checkbox v-decorator.trim="['exp',{initialValue: false}]">
                是否异常
              </a-checkbox>
            </a-form-item>
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作业情况">
              <a-textarea v-decorator.trim="['resultDesc',{initialValue:'',rules:[]}]"
                          :auto-size="{ minRows: 8, maxRows: 8 }"
              ></a-textarea>
            </a-form-item>
          </a-form>
        </div>
      </a-col>
      <a-col :md="6">
        <a-spin :spinning="confirmLoading">
          <div style="margin:30px 10px 20px; text-align: center;">
           <a-spin :spinning="qrLoading">
              <img v-if="!imageBase64Url" class="QRcode" src="~@/assets/tiros/images/code.png" alt="" />
              <img v-else class="QRcode" :src="imageBase64Url" alt="" />
            </a-spin>
          </div>
          <div style="text-align: center;">
            通过APP扫码确认
          </div>
        </a-spin>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script>
import { saveCheck,getTaskQRCode } from '@api/tirosGroupApi'
import { EventBus } from '@/utils/eventBus.js'

export default {
  name: 'SheetItemModal',
  props: ['title'],
  data () {
    return {
      qrLoading: false,
      imageBase64Url: '',
      detailIds: '',
      checkType: -1,
      value: 1,
      status: 1,
      switchCheck: true,
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        checkUserName: { rules: [{ required: true, message: '请输入登录名称!' }] },
        checkUserPwd: { rules: [{ required: true, message: '请输入密码!' }] },
        checkResult: { rules: [{ required: true, message: '选择检查结果!' }] }
      }
    }
  },
  mounted(){
    EventBus.$on('topic',(data)=>{
      this.$message.success(data.msgTxt);
      let formData = Object.assign(this.model)
      // 结构 1 表示正常  0 表示异常
      formData.detailIds = this.workRecordType === 1 ? this.detailIds : this.detailIds.join(',')
      formData.result = formData.exp ? 0 : 1
      formData.isIgnore = this.isIgnore ? 1 : 0
      formData.type = this.checkType
      formData.workRecordId= this.formInstId
      formData.colIndex=this.colIndex
      formData.workRecordType = this.workRecordType
      formData.detailIds = this.detailIds
      this.$emit('scan', { saveSuccess: true, data: formData })
      this.close()
    })
  },
  methods: {
    add () {
      this.edit({})
    },
    getTaskQRCode(curForm){
      console.log(curForm)
      this.qrLoading = true;
      // return
      getTaskQRCode({
        formDetails: typeof this.detailIds === 'string' ? this.detailIds : this.detailIds.join(','),
        checkType: this.checkType,
        colIndex: this.colIndex,
        formInstId: curForm.formInstId,
        formType: curForm.instType,
        fromBy: "PC",
        orderId: curForm.workOrderId,
        targetType: "TASK",
        taskId: curForm.workOrderTaskId,
        workRecordType: curForm.workRecordType,
      }).then((res)=>{
        console.log(res)
        this.qrLoading = false;
        if(res.success){
          this.imageBase64Url = res.result.base64;
        }
      })
    },
    showModal (ids, checkType) {
      console.log(ids)
      this.detailIds = ids;
      this.checkType = checkType
      //   if (record.id) {
      //     this.switchCheck = record.status == 1 ? true : false
      //   }
      this.getTaskQRCode(curForm);
      this.form.resetFields()
      //   this.model = Object.assign({}, record)
      // this.model.status = this.status
      this.visible = true
      //   this.$nextTick(() => {
      //     this.form.setFieldsValue(pick(this.model, 'code', 'title', 'remark'))
      //   })
    },
    // 确定
    handleOk () {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          let formData = Object.assign(that.model, values)
          // 结构 1 表示正常  0 表示异常
          formData.detailIds = this.detailIds
          formData.result = formData.exp ? 0 : 1
          formData.type = this.checkType
          saveCheck(formData).then(res => {
            if (res.success) {
              that.$message.success(res.message)
              that.$emit('ok',{ saveSuccess: true})
              that.close()
            } else {
              that.$message.error(res.message)
              // console.error('提交检查信息错误：', res.message)
            }
          }).finally(() => {

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
    }
  }
}
</script>
<style lang="less" scoped>
.QRcode {
  width: 100px;
  height: 100px;
}
</style>
