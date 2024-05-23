<template>
  <a-modal
    :title='title'
    centered
    :width='500'
    :visible='visible'
    :confirmLoading='confirmLoading'
    @ok='handleOk'
    @cancel='handleCancel'
    cancelText='关闭'
    :destroyOnClose='true'
  >
    <a-spin :spinning='confirmLoading'>
      <a-form :form='form'>
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='检查日期'>
          <a-date-picker
            format='YYYY-MM-DD'
            style='width: 100%'
            placeholder='选择检查日期'
            v-decorator="['checkDate', validatorRules.checkDate]"
            @change='changeCheckDate'
          />
        </a-form-item>
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='检查结果'>
          <j-dict-select-tag
            :triggerChange='true'
            placeholder='请选择检查结果'
            v-decorator="['checkResult', validatorRules.checkResult]"
            dictCode='bu_work_order_record_result'
          />
        </a-form-item>
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='质量负责人'>
          <a-input
            ref='userSelect'
            v-model='checkUserName'
            placeholder='请选择质量负责人'
            :open='false'
            style='width: 100%'
            @click='openSelectUser()'
          >
            <a-icon slot='suffix' type='ellipsis' />
          </a-input>
        </a-form-item>
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='备注'>
          <a-textarea placeholder='请输入备注' v-decorator.trim="['remark']" />
        </a-form-item>
      </a-form>
    </a-spin>
    <user-list ref='userSelectModal' :multiple='false' @ok='onSelectUser'></user-list>
  </a-modal>
</template>

<script>
import UserList from '@views/tiros/common/selectModules/UserList'
import { confirmForm } from '@/api/tirosQualityApi'
import moment from 'moment'

export default {
  name: 'ConfirmForm',
  components: { UserList },
  data() {
    return {
      title: '检查确认',
      visible: false,
      recordId: '',
      checkUserName: '',
      checkUserId: '',
      model: {},
      confirmLoading: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      form: this.$form.createForm(this),
      validatorRules: {
        checkDate: {
          rules: [{ required: true, message: '请选择检查日期!' }]
        },
        checkResult: {
          rules: [{ required: true, message: '请选择检查结果!' }]
        }
      }
    }
  },
  mounted() {
  },
  methods: {
    confirm(id) {
      this.form.resetFields()
      this.visible = true
      this.confirmLoading = false
      this.recordId = id
      this.model = Object.assign(
        {},
        {
          checkDate: moment(new Date()).format('YYYY-MM-DD')
        }
      )
      this.$nextTick(() => {
        this.form.setFieldsValue({
          checkDate: this.model.checkDate
        })
      })
      this.checkUserName = this.$store.getters.userInfo.realname
      this.checkUserId = this.$store.getters.userInfo.id
    },
    // 检查日期改变
    changeCheckDate(value) {
      if (value) {
        this.model.checkDate = value
      }
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values, {
            checkDate: moment(that.model.checkDate).format('YYYY-MM-DD'),
            id: that.recordId,
            checkUserName: that.checkUserName,
            checkUserId: that.checkUserId
          })

          confirmForm(formData)
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok', formData)
                that.close()
              } else {
                that.$message.warning(res.message)
              }
            })
            .finally(() => {
              that.confirmLoading = false
            })
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
    },
    openSelectUser() {
      this.$refs.userSelectModal.showModal()
      this.$refs.userSelect.blur()
    },
    onSelectUser(data) {
      if (data.length) {
        this.checkUserName = data[0].realname
        this.checkUserId = data[0].id
        this.$forceUpdate()
      }
    }
  }
}
</script>
