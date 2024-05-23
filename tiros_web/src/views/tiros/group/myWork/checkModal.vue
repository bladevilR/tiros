<template>
  <!-- 作业检查弹窗，自检、互检.....  -->
  <a-modal
    :title='title'
    :width='800'
    :visible='visible'
    :confirmLoading='confirmLoading'
    centered
    @ok='handleOk'
    @cancel='handleCancel'
    cancelText='关闭'
    :destroyOnClose='true'
  >
    <a-row>
      <a-col :md='18'>
        <div>
          <a-form :form='form' ref='initForm'>
            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='登录名称'>
              <a-row :gutter='10'>
                <a-col :span='14'>
                  <a-input v-decorator.trim="['checkUserName', validatorRules.checkUserName]" />
                </a-col>
                <a-col :span='10'>
                  <a-button type='link' @click='useCurrentUser'>使用当前账号和密码</a-button>
                </a-col>
              </a-row>
            </a-form-item>
            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='登录密码'>
              <a-input-password v-decorator.trim="['checkUserPwd', validatorRules.checkUserPwd]" />
            </a-form-item>
            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='检查结果'>
              <a-checkbox v-decorator.trim="['exp', { initialValue: false }]"> 异常</a-checkbox>
              <a-checkbox v-model='isIgnore' @change='onIgnoreChange'> 忽略</a-checkbox>
            </a-form-item>
            <!-- <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否忽略">
              <a-switch v-model="isIgnore" />
            </a-form-item> -->

            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='同检人员'>
              <a-select
                v-model="togetherCheckUserNames"
                placeholder="请选择人员"
                :open="false"
                :showArrow="true"
                allowClear
                @change="clearTogetherCheckUserNames"
                @dropdownVisibleChange="openSelectUser()"
                ref="userSelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>

            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='结果描述'>
              <a-textarea
                v-decorator.trim="[
                  'resultDesc',
                  { initialValue: '', rules: [{ required: isIgnore, message: '请输入忽略原因!' }] },
                ]"
                :auto-size='{ minRows: 8, maxRows: 8 }'
              ></a-textarea>
            </a-form-item>
          </a-form>
        </div>
      </a-col>
      <a-col :md='6'>
        <a-spin :spinning='confirmLoading'>
          <div style='margin: 30px 10px 20px; text-align: center'>
            <a-spin :spinning='qrLoading'>
              <img v-if='!imageBase64Url' class='QRcode' src='~@/assets/tiros/images/code.png' alt='' />
              <img v-else class='QRcode' :src='imageBase64Url' alt='' />
            </a-spin>
          </div>
          <div style='text-align: center'>通过APP扫码确认</div>
        </a-spin>
      </a-col>
    </a-row>
    <UserList ref='userSelectModal' :multiple='true' @ok='onSelectUser'></UserList>
  </a-modal>
</template>

<script>
import { getTaskQRCode, saveCheck } from '@/api/tirosGroupApi'
import UserList from '@views/tiros/common/selectModules/UserList'
import { EventBus } from '@/utils/eventBus.js'

export default {
  name: 'SheetItemModal',
  components: { UserList },
  props: ['title', 'formInstId', 'workRecordType'],
  data() {
    return {
      detailIds: '',
      checkType: -1,
      colIndex: 0,
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
        // resultDesc: { rules: [{ required: true, message: '选择结果描述!' }] },
      },
      isIgnore: false,
      qrLoading: false,
      imageBase64Url: '',
      togetherCheckUserIds: '',
      togetherCheckUserNames: ''
    }
  },
  mounted() {
    EventBus.$on('topic', (data) => {
      this.$message.success(data.msgTxt)
      let formData = Object.assign(this.model)
      // 结构 1 表示正常  0 表示异常
      formData.detailIds = this.workRecordType === 1 ? this.detailIds : this.detailIds.join(',')
      formData.result = formData.exp ? 0 : 1
      formData.isIgnore = this.isIgnore ? 1 : 0
      formData.type = this.checkType
      formData.workRecordId = this.formInstId
      formData.colIndex = this.colIndex
      formData.workRecordType = this.workRecordType
      formData.detailIds = this.detailIds
      this.$emit('scan', { saveSuccess: true, data: formData })
      this.close()
    })
  },
  methods: {
    add() {
      this.edit({})
    },
    getTaskQRCode(curForm) {
      this.qrLoading = true
      // return
      getTaskQRCode({
        formDetails: typeof this.detailIds === 'string' ? this.detailIds : this.detailIds.join(','),
        checkType: this.checkType,
        colIndex: this.colIndex,
        formInstId: curForm.formInstId,
        formType: curForm.instType,
        fromBy: 'PC',
        orderId: curForm.workOrderId,
        targetType: 'TASK',
        taskId: curForm.workOrderTaskId,
        workRecordType: curForm.workRecordType
      }).then((res) => {
        this.qrLoading = false
        if (res.success) {
          this.imageBase64Url = res.result.base64
        }
      })
    },
    showModal(ids, checkType, colIndex = 0, curForm) {
      this.imageBase64Url = ''
      this.detailIds = ids
      this.checkType = checkType
      this.colIndex = colIndex
      this.getTaskQRCode(curForm)
      //   if (record.id) {
      //     this.switchCheck = record.status == 1 ? true : false
      //   }
      this.form.resetFields()
      //   this.model = Object.assign({}, record)
      // this.model.status = this.status
      this.isIgnore = false
      this.visible = true
      if (this.checkType === 1) {
        this.$nextTick(() => {
          this.form.setFieldsValue({
            checkUserName: this.$store.getters.userInfo.username,
            checkUserPwd: '~**&&##@@$$%%^^~' // 为固定值，后端通过判断该值来确定是自检，且没有修改用户名和密码
          })
        })
      }
      this.$nextTick(() => {
        this.form.setFieldsValue({
          exp: '',
          resultDesc: ''
        })
      })
      //   this.$nextTick(() => {
      //     this.form.setFieldsValue(pick(this.model, 'code', 'title', 'remark'))
      //   })
    },
    useCurrentUser() {
      this.$nextTick(() => {
        this.form.setFieldsValue({
          checkUserName: this.$store.getters.userInfo.username,
          checkUserPwd: '~**&&##@@$$%%^^~' // 为固定值，后端通过判断该值来确定是自检，且没有修改用户名和密码
        })
      })
    },
    clearTogetherCheckUserNames(text){
      if (!text){
        this.togetherCheckUserIds = ''
        this.togetherCheckUserNames = ''
      }
    },
    openSelectUser() {
      this.$refs.userSelectModal.showModal()
      this.$refs.userSelect.blur()
    },
    onSelectUser(data) {
      if (data.length) {
        let name = '', ids = ''
        data.forEach((item, index, arr) => {
          name += item.realname
          ids += item.id

          if (index < arr.length - 1) {
            name += ','
            ids += ','
          }
        })

        this.togetherCheckUserIds = ids
        this.togetherCheckUserNames = name
      }
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          let formData = Object.assign(that.model, values)
          // 结构 1 表示正常  0 表示异常
          formData.detailIds = this.workRecordType === 1 ? this.detailIds : this.detailIds.join(',')
          formData.result = formData.exp ? 0 : 1
          formData.isIgnore = this.isIgnore ? 1 : 0
          formData.type = this.checkType
          formData.workRecordId = this.formInstId
          formData.colIndex = this.colIndex
          formData.workRecordType = this.workRecordType
          formData.togetherCheckUserIds = this.togetherCheckUserIds
          formData.togetherCheckUserNames = this.togetherCheckUserNames

          saveCheck(formData)
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                formData.detailIds = this.detailIds
                // 设置返回的检查用户信息
                if (res.result) {
                  formData.checkUserInfo = res.result
                }
                that.$emit('ok', { saveSuccess: true, data: formData })
                that.close()
              } else {
                that.$message.error(res.message)
                // console.error('提交检查信息错误：', res.message)
              }
            })
            .finally(() => {
            })
        }
      })
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    onIgnoreChange() {
      this.$nextTick(() => {
        this.form.resetFields('resultDesc')
      })
    },
    close() {
      this.$emit('close')
      this.visible = false
    }
  }
}
</script>
<style lang='less' scoped>
.QRcode {
  width: 150px;
  height: 150px;
}
</style>
