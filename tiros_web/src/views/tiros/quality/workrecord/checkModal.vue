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
      <a-col :md="24">
        <div>
          <a-form :form="form">
<!--            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="登录名称">
              <a-input v-decorator.trim="['checkUserName', validatorRules.checkUserName]" />
            </a-form-item>
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="登录密码">
              <a-input-password v-decorator.trim="['checkUserPwd', validatorRules.checkUserPwd]" />
            </a-form-item>-->
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="检查结果">
              <a-checkbox v-decorator.trim="['exp',{initialValue: false, rules:[],valuePropName: 'checked'}]">
                是否异常
              </a-checkbox>
            </a-form-item>
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="检查情况">
              <a-textarea v-decorator.trim="['resultDesc',{initialValue:'',rules:[]}]"
                          :auto-size="{ minRows: 12, maxRows:12 }"
              ></a-textarea>
            </a-form-item>
          </a-form>
        </div>
      </a-col>
<!--      <a-col :md="6">
        <a-spin :spinning="confirmLoading">
          <div style="margin:30px 10px 20px; text-align: center;">
            <img class="QRcode" src="~@/assets/tiros/images/code.png" alt="" />
          </div>
          <div style="text-align: center;">
              通过APP扫码确认
          </div>
        </a-spin>
      </a-col>-->
    </a-row>
  </a-modal>
</template>

<script>
import {  confirmCheck } from '@/api/tirosQualityApi'

export default {
  name: 'SheetItemModal',
  props: ['title'],
  data () {
    return {
      detailIds: '',
      checkType: -1,
      value: 1,
      status: 1,
      switchCheck: true,
      visible: false,
      recordId: '',
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
        exp:{ rules: []},
        checkUserName: { rules: [{ required: true, message: '请输入登录名称!' }] },
        checkUserPwd: { rules: [{ required: true, message: '请输入密码!' }] },
        checkResult: { rules: [{ required: true, message: '选择检查结果!' }] }
      }
    }
  },
  methods: {
    showModal (recordId) {
      this.recordId = recordId
      this.form.resetFields()
      this.visible = true
    },
    // 确定
    handleOk () {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          let formData = Object.assign(that.model, values)

          let params = {
            id: this.recordId,
            checkUserId: this.$store.getters.userInfo.id,
            checkResult: formData.exp ? 0 : 1,
            checkDate: this.$moment().format('YYYY-MM-DD'),
            remark: formData.resultDesc
          }
          // console.log('params:', params)
          confirmCheck(params).then(res => {
            if (res.success) {
              this.$message.success('确认成功')
              this.$emit('ok')
              this.close()
            } else {
              this.$message.error('确认失败')
              console.error('确认失败：', res.message)
            }
          }).catch(err => {
            this.$message.error('确认异常')
            console.error('确认异常：', err)
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
.QRcode{
    width:150px;
    height:150px;
}
</style>
