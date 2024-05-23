<template>
  <a-modal
    :title="title"
    :width="600"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="年份">
          <a-date-picker
            mode="year"
            placeholder="请选择年份"
            format="YYYY"
            v-decorator="[ 'year', validatorRules.year]"
            :open="yearPickShow"
            @panelChange="handlePanelChange"
            @openChange="handleOpenChange"
          />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开始日期">
          <a-date-picker
            v-decorator.trim="['startTime',validatorRules.startTime]"
            format="MM-DD"
            :disabled-date="disabledStartDate"
            placeholder="开始日期"
            @openChange="handleStartOpenChange"
            @change="handleStartChange"
          />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结束日期">
          <a-date-picker
            v-decorator.trim="['end',validatorRules.end]"
            :disabled-date="disabledEndDate"
            format="MM-DD"
            placeholder="结束日期"
            :open="endOpen"
            @openChange="handleEndOpenChange"
            @change="handleEndChange"
          />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="节日名称">
          <a-input placeholder="请输入节日名称" v-decorator.trim="[ 'name', validatorRules.name]" />
        </a-form-item>

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="类型">
          <j-dict-select-tag v-decorator.trim="[ 'work', validatorRules.work]" :triggerChange="true" placeholder="请选择节假日类型" dictCode="bu_day_type" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="描述">
          <a-textarea v-decorator.trim="[ 'remark', validatorRules.remark]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import moment from 'moment'
import 'moment/locale/zh-cn'
import pick from 'lodash.pick'
import { addHoliday, editHoliday } from '@/api/tirosApi'

export default {
  name: 'HolidayModal',
  data() {
    return {
      yearPick: null, //年选择器的值
      yearPickShow: false, //年选择器的显示隐藏
      dateFormat: 'YYYY',
      value: 1,
      title: '操作',
      visible: false,
      model: {},
      startValue: null,
      endValue: null,
      endOpen: false,
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
        name: { rules: [{ required: true, message: '请输入节日名称!' },{ max:20, message: '输入长度不能超过20个字符!'}] },
        year: { rules: [{ required: true, message: '请输入节日年份!' }] },
        startTime: { rules: [{ required: true, message: '请输入开始时间!' }] },
        end: { rules: [{ required: true, message: '请输入结束时间!' }] },
        work:{rules: [{ required: true, message: '请选择节假日类型!' }]},
        remark: { rules: [{ max:200, message: '输入长度不能超过200个字符!'}] },
      },

    }
  },
  created() {},
  methods: {
    moment,
    add() {
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      if (record.id) {
        this.visiblekey = true
        this.$nextTick(() => {
          this.form.setFieldsValue({
            year: moment(this.model.year || new Date(), 'YYYY'),
            startTime: moment(this.model.startTime || undefined),
            end: moment(this.model.end || undefined),
            name: this.model.name,
            remark: this.model.remark,
            work:this.model.work
          })
        })
      } else {
        this.visiblekey = false
      }
      this.$nextTick(() => {
        this.form.setFieldsValue({
          year: moment(this.model.year || new Date(), 'YYYY'),
        })
      })

    },
    handleStartChange(data,dateString) {
      this.startValue=data

    },
    handleEndChange(data,dateString) {
      this.endValue=data
    },
    disabledStartDate(startValue) {
      const endValue = this.endValue;
      if (!startValue || !endValue) {
        return false;
      }
      return startValue.valueOf() >endValue.valueOf()
    },
    disabledEndDate(endValue) {
      const startValue = this.startValue;
      if (!endValue || !startValue) {
        return false;
      }
      let e=moment(endValue).format("YYYY")
      let s=moment(this.startValue).format("YYYY")
      return startValue.valueOf() >= endValue.valueOf()||s!==e
    },
    handleStartOpenChange(open) {
      console.log(open)
      if (!open) {
        this.endOpen = true
      }
    },
    handleEndOpenChange(open) {
      this.endOpen = open
    },

    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values, {
            year: moment(values.year).format('YYYY'),
            startTime: moment(values.startTime).format('YYYY-MM-DD'),
            end: moment(values.end).format('YYYY-MM-DD'),
            work: values.work
          })
          let obj
          if (!that.model.id) {
            obj = addHoliday(formData)
          } else {
            obj = editHoliday(formData)
          }
          obj
            .then(res => {
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
      })
    },
    // 弹出日历和关闭日历的回调
    handleOpenChange(status) {
      this.yearPickShow = status
    },
    // 得到年份选择器的值
    handlePanelChange(value) {
      this.form.setFieldsValue({
        year: value
      })
      this.yearPickShow = false
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
      this.startValue=null
      this.endValue=null
    }
  }
}
</script>