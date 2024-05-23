<template>
  <a-modal
    :title="title"
    :width="535"
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
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开始时间">
              <a-date-picker v-decorator.trim="[ 'startTime', validatorRules.startTime]"
                              placeholder="开始时间"
                              format="YYYY-MM-DD HH:mm:ss"
                              :show-time="{ format: 'HH:mm:ss' }"
                              :disabled-date="disabledStartDate"
                              @openChange="handleStartOpenChange"
                              @change="handleStartChange"
                               />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结束时间">
              <a-date-picker v-decorator.trim="[ 'endTime', validatorRules.endTime]"
                              :show-time="{ format: 'HH:mm:ss' }"
                              format="YYYY-MM-DD HH:mm:ss"
                              placeholder="结束时间"
                              :disabled-date="disabledEndDate"
                              :open="endOpen"
                              @openChange="handleEndOpenChange"
                              @change="handleEndChange"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="工艺装备">
              <a-select
                v-decorator="['toolName',validatorRules.toolId]"
                placeholder="请选择"
                :open="false"
                :showArrow="true"
                @focus="openToolsSelectModal"
                ref="myToolsSelect">
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <tools-list  ref="toolsSelectModal" :multiple="false" @ok="handleToolsSelect" :canSelectType="[5]"></tools-list>
    </a-spin>
  </a-modal>
</template>

<script>
import moment from 'moment'
import { addToolplan, editToolplan } from '../../../../api/tirosDispatchApi'
import ToolsList from '@views/tiros/common/selectModules/ToolsList'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'ToUseModal',
  components:{ToolsList},
  data() {
    return {
      value: 1,
      title: '操作',
      visible: false,
      model: {},
      toolId:'',
      startValue: null,
      endValue: null,
      endOpen: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 8 }
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 9 }
      },
      planType: 1,
      isEdit: false,
      isClose: false,
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        startTime: { rules: [{ required: true, message: '请选择开始时间!' }] },
        endTime: { rules: [{ required: true, message: '请选择结束时间!' }] },
        toolId: { rules: [{ required: true, message: '请选择工艺装备!' }] }
      }
    }
  },

  created() {
  },
  methods: {
    handleStartChange(data) {
      this.startValue=data
    },
    handleEndChange(data) {
      this.endValue=data
    },
    disabledStartDate(startValue) {
      const endValue = this.endValue;

      if (!startValue || !endValue) {
        return false;
      }
      return startValue.valueOf() > endValue.valueOf();
    },
    disabledEndDate(endValue) {
      const startValue = this.startValue;
      if (!endValue || !startValue) {
        return false;
      }
      return startValue.valueOf() >= endValue.valueOf();
    },
    handleStartOpenChange(open) {
      if (!open) {
        this.endOpen = true;
      }
    },
    handleEndOpenChange(open) {
      this.endOpen = open;
    },

    openToolsSelectModal(){
      this.$refs.toolsSelectModal.showModal()
      this.$refs.myToolsSelect.blur()
    },
    handleToolsSelect(data){
      if(!everythingIsEmpty(data)){
        this.toolId=data[0].id
        this.form.setFieldsValue({toolName:data[0].name})
      }
    },

    changeClose(checked) {
      this.isClose = checked
    },

    add() {
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      if (record.id) {
        this.isClose = record['close'] && record['close'] === 1
        this.isEdit = true
        this.toolId = record.toolId
      } else {
        this.isClose = false
        this.isEdit = false
      }
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        if (this.isEdit) {
          this.form.setFieldsValue({
             startTime: moment(this.model.startTime || undefined, 'YYYY-MM-DD HH:mm:ss'),
             endTime: moment(this.model.endTime || undefined, 'YYYY-MM-DD HH:mm:ss'),
            toolName: this.model.name
          })
        }
      })
    },

    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true

          let formData = Object.assign(that.model, values, {
            startTime: moment(values.startTime).format('YYYY-MM-DD HH:mm:ss'),
            endTime: moment(values.endTime).format('YYYY-MM-DD HH:mm:ss'),
            toolId:this.toolId
          })
          formData['planType'] = that.planType
          let obj
          if (!that.model.id) {
            obj = addToolplan(formData)
          } else {
            obj = editToolplan(formData)
          }
          obj
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
              } else {
                that.$message.warning(res.message)
              }
            })
            .finally(() => {
              that.confirmLoading = false
              that.close()
            })
          this.visible = false
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
    }
  }
}
</script>

<style scoped>

</style>