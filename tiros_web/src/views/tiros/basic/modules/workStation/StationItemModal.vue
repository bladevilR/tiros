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
        <a-row :gutter="{ xs: 8, sm: 16, md: 24, lg: 32 }">
          <a-col :span="8">
            <a-form-item :labelCol="labelCol3" :wrapperCol="wrapperCol3" label="运营公司">
              <a-input v-model="model.companyName" disabled />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol3" :wrapperCol="wrapperCol3" label="中心">
              <a-input v-model="model.depotName" disabled />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol3" :wrapperCol="wrapperCol3" label="车间">
              <a-input v-model="model.workshopName" disabled />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="{ xs: 8, sm: 16, md: 24, lg: 32 }">
          <!-- <a-col :span="12">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车间">
              <a-input v-model="workShopDetail.curTitle" disabled />
            </a-form-item>
          </a-col>-->
          <a-col :span="12">
            <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="工位号">
              <a-input v-decorator.trim="[ 'stationNo', validatorRules.stationNo]" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="工位名称">
              <a-input v-decorator.trim="[ 'name', validatorRules.name]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="{ xs: 8, sm: 16, md: 24, lg: 32 }">
          <a-col :span="12">
            <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="位置">
              <a-input v-decorator.trim="[ 'location', validatorRules.location]" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="工位状态">
              <j-dict-select-tag
                v-decorator.trim="[ 'status', validatorRules.status]"
                :triggerChange="true"
                dictCode="bu_valid_status"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="{ xs: 8, sm: 16, md: 24, lg: 32 }">
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol1"
              :wrapperCol="wrapperCol1"
              label="作业内容"
            >
              <a-textarea v-decorator.trim="[ 'content', validatorRules.content]" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row class="form-row" :gutter="{ xs: 8, sm: 16, md: 24, lg: 32 }">
          <a-col :span="24">
            <a-form-item
              :labelCol="labelCol1"
              :wrapperCol="wrapperCol1"
              label="备注"
            >
              <a-textarea v-decorator.trim="[ 'remark', validatorRules.remark]" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import pick from 'lodash.pick'
import { addStation, editStation } from '@/api/tirosApi'

export default {
  name: 'StationItemModal',
  props: ['workShopDetail'],
  data () {
    return {
      value: 1,
      title: '操作',
      visible: false,
      model: {},
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 3 }
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 21 }
      },
      labelCol2: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol2: {
        xs: { span: 24 },
        sm: { span: 18 }
      },
      labelCol3: {
        xs: { span: 24 },
        sm: { span: 9 }
      },
      wrapperCol3: {
        xs: { span: 24 },
        sm: { span: 15 }
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        stationNo: { rules: [{ required: true, message: '请输入工位号!' }, { max: 32, message: '输入长度不能超过32字符!' }] },
        name: { rules: [{ required: true, message: '请输入工位名称!' }, { max: 20, message: '输入长度不能超过20字符!' }] },
        location: { rules: [{ required: true, message: '请输入位置!' }, { max: 200, message: '输入长度不能超过200字符!' }] },
        content: { rules: [{ required: true, message: '请输入作业内容!' }, { max: 1000, message: '输入长度不能超过1000字符!' }] },
        status: { rules: [{ required: true, message: '请选择状态!' }] },
        remark: { rules: [{ max: 500, message: '输入长度不能超过500字符!' }] }
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
      console.log(this.workShopDetail, record)
      if (record.id) {
      } else {
        record = Object.assign({}, this.workShopDetail)
      }
      this.form.resetFields()
      this.model = Object.assign({}, record)
      console.log(record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          stationNo: this.model.stationNo,
          name: this.model.name,
          location: this.model.location,
          content: this.model.content,
          status: this.model.status,
          remark: this.model.remark
        })
      })
    },
    // 确定
    handleOk () {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData
          if (this.workShopDetail.workshopId) {
            formData = Object.assign(that.model, values, { workshopId: this.workShopDetail.workshopId })
          } else {
            formData = Object.assign(that.model, values)
          }

          console.log(that.model)
          let obj
          if (!that.model.id) {
            obj = addStation(formData)
          } else {
            obj = editStation(formData)
          }
          obj
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
                that.close()
              } else {
                that.$message.warning(res.message)
                that.$emit('ok')
              }
            })
            .finally(() => {
              that.confirmLoading = false
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