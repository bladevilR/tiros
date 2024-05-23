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
          <a-col :md="20" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="整改项目">
              <a-input v-decorator.trim="['changeName', validatorRules.changeName]" placeholder="请输入整改项目" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="20" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="整改内容描述">
              <a-textarea
                v-decorator.trim="['changeDesc', validatorRules.changeDesc]"
                placeholder="请输入整改内容描述"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="20" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="责任部门">
              <j-dict-select-tag
                placeholder="请选择责任部门"
                :triggerChange="true"
                v-decorator.trim="['dept', validatorRules.dept]"
                dictCode="sys_depart,depart_name,id"
                @select="deptChange"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="20" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="责任人">
              <a-select
                placeholder="请选择责任人"
                v-decorator.trim="['dutyManName', validatorRules.dutyManName]"
                :open="false"
                @focus="openUserSelectModal"
                ref="userSelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="20" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="整改日期">
              <a-date-picker
                v-decorator.trim="['changeDate', validatorRules.changeDate]"
                placeholder="请选择整改日期"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="20" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="检查日期">
              <a-date-picker v-decorator.trim="['checkDate']" placeholder="请选择检查日期" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <user-list ref="userModalForm" :multiple="false" @ok="addTarget"></user-list>
    </a-spin>
  </a-modal>
</template>
<script>
import { addRectify, editRectify } from '@api/tirosDispatchApi'
import { everythingIsEmpty } from '@/utils/util'
import UserList from '@views/tiros/common/selectModules/UserList'
import moment from 'moment'

export default {
  name: 'RectifyModal',
  components: { UserList },
  props: ['exchangeId'],
  data() {
    return {
      title: '操作',
      visible: false,
      activeIndex: null,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      dutyMan: '',
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        changeName: {
          rules: [
            { required: true, message: '请输入整改项目名称!' },
            { max: 64, message: '输入长度不能超过64字符!' },
          ],
        },
        changeDesc: {
          rules: [
            { required: true, message: '请输入整改内容描述!' },
            { max: 500, message: '输入长度不能超过500字符!' },
          ],
        },
        dept: { rules: [{ required: true, message: '请选择责任部门!' }] },
        dutyManName: { rules: [{ required: true, message: '请选择责任人!' }] },
        changeDate: { rules: [{ required: true, message: '请选择整改日期!' }] },
      },
    }
  },

  created() {},
  methods: {
    deptChange(v, data) {
      if (v) {
        this.model.deptName = data.text
      }
    },
    openUserSelectModal() {
      this.$refs.userModalForm.showModal()
      this.$refs.userSelect.blur()
    },
    addTarget(data) {
      if (!everythingIsEmpty(data)) {
        this.dutyMan = data[0].id
        this.form.setFieldsValue({ dutyManName: data[0].realname })
      }
    },
    add() {
      this.edit({})
    },
    edit(record, index) {
      this.activeIndex = index
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      if (record.id) {
        this.dutyMan = record.dutyMan
      }
      this.$nextTick(() => {
        this.form.setFieldsValue({
          changeName: this.model.changeName,
          changeDesc: this.model.changeDesc,
          dept: this.model.dept,
          deptName: this.model.deptName,
          dutyManName: this.model.dutyManName,
          changeDate: moment(this.model.changeDate || undefined),
          checkDate: moment(this.model.checkDate || undefined),
        })
      })
    },

    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          this.confirmLoading = true

          let formData = Object.assign(that.model, values, {
            dutyMan: this.dutyMan,
            changeDate: moment(values.changeDate).format('YYYY-MM-DD'),
            checkDate: moment(values.checkDate).format('YYYY-MM-DD'),
          })
          this.$emit('addItem', formData, this.activeIndex)
          this.$message.success('添加成功！')
          this.$emit('ok')
          this.close()
          this.activeIndex = null
          this.confirmLoading = false
          //   that.confirmLoading = true
          //   let formData = Object.assign(that.model, values, {
          //     dutyMan: this.dutyMan,
          //     changeDate: moment(values.changeDate).format('YYYY-MM-DD'),
          //     checkDate: moment(values.checkDate).format('YYYY-MM-DD')
          //   })
          //   let obj
          //   if (!that.model.id) {
          //     formData['exchangeId'] = this.exchangeId
          //     obj = addRectify(formData)
          //   } else {
          //     obj = editRectify(formData)
          //   }
          //   obj
          //     .then((res) => {
          //       if (res.success) {
          //         that.$message.success(res.message)
          //         that.$emit('ok')
          //         that.close()
          //       } else {
          //         that.$message.warning(res.message)
          //       }
          //     })
          //     .finally(() => {
          //       that.confirmLoading = false
          //     })
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
  },
}
</script>

<style scoped>
</style>