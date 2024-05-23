<template>
  <a-modal
    :title="title"
    centered
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
        <a-row :gutter="24">
          <!--          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="编码">
              <a-input placeholder="请输入" v-decorator.trim="[ 'recordCode', validatorRules.recordCode ]"/>
            </a-form-item>
          </a-col>-->
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开口项名称">
              <a-input placeholder="请输入" v-decorator.trim="['recordName', validatorRules.recordName]" />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
              <!--              <j-dict-select-tag
                :triggerChange="true"
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
                @change="changeLine"
              />-->
              <line-select-list v-decorator="['lineId', validatorRules.lineId]" @change="changeLine">
              </line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆">
              <j-dict-select-seach-tag
                :triggerChange="true"
                v-decorator="['trainNo', validatorRules.trainNo]"
                :dictCode="dictCodeStr"
              />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item field="orderId" :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属工单">
              <!-- <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="[ 'orderId', validatorRules.orderId ]"
                dictCode="bu_work_order,order_name,id"
                @change="changeOrder"
              /> -->
              <a-select
                allow-clear
                placeholder="请选择工单"
                v-decorator.trim="['orderName', validatorRules.orderId]"
                :open="false"
                :showArrow="true"
                ref="myOrderSelect"
                @change="clearVal"
                @dropdownVisibleChange="chooseWorkOrder"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工单任务">
              <j-dict-select-tag
                :triggerChange="true"
                placeholder="请选择工单任务"
                v-decorator.trim="[ 'orderTaskId', validatorRules.orderTaskId ]"
                :dictCode="dictCodeStrOrder"
              />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否处理">
              <a-switch v-model="isStatus" @change="handleChange" />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注说明">
              <a-textarea placeholder="请输入" v-decorator.trim="['recordDesc', validatorRules.recordDesc]" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <work-order-select ref="workOrderSelect" @ok="onSelectOrder"></work-order-select>
    </a-spin>
  </a-modal>
</template>

<script>
import moment from 'moment'
import { addOpenItem, editOpenItem } from '@api/tirosQualityApi'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import WorkOrderSelect from '@views/tiros/common/selectModules/WorkOrderSelect'

export default {
  name: 'OpenItemModal',
  components: { LineSelectList, WorkOrderSelect },
  data() {
    return {
      isStatus: false,
      status: '',
      dictCodeStr: 'bu_train_info,train_no,train_no',
      dictCodeStrOrder: '',
      title: '操作',
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 13 },
      },
      isClose: false,
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        /*  recordCode: { rules: [{ required: true, message: '请输入编码!' }] },*/
        recordName: {
          rules: [
            { required: true, message: '请输入开口项名称!' },
            { max: 32, message: '输入长度不能超过32字符!' },
          ],
        },
        orderId: { rules: [{ required: true, message: '请选择所属工单!' }] },
        orderName: { rules: [] },
        lineId: { rules: [{ required: true, message: '请选择线路!' }] },
        trainNo: { rules: [{ required: true, message: '请选择车辆!' }] },
        orderTaskId: { rules: [{ required: true, message: '请选择工单任务!' }] },
        recordDesc: { rules: [{ max: 500, message: '输入长度不能超过500字符!' }] },
        status: { rules: [{ required: true, message: '请选择处理状态!' }] },
      },
    }
  },
  created() {},
  methods: {
    changeOrder(data) {
      if (data) {
        this.dictCodeStrOrder = "bu_work_order_task,task_name,id,order_id='" + data + "'"
      } else {
        this.dictCodeStrOrder = 'bu_work_order_task,task_name,id'
      }
    },
    changeLine(data) {
      if (data) {
        this.dictCodeStr = 'bu_train_info,train_no,train_no,line_id=' + data
      } else {
        this.dictCodeStr = 'bu_train_info,train_no,train_no'
      }
    },
    handleChange(value) {
      let val = value ? 1 : 0
      this.status = val
    },
    changeClose(checked) {
      this.isClose = checked
    },
    add(orderId, taskId, remark) {
      if (orderId) {
        this.changeOrder(orderId)
      }
      this.edit({ orderId: orderId, orderTaskId: taskId, recordDesc: remark })
    },
    edit(record) {
      if (record.id) {
        this.isClose = record['close'] && record['close'] == 1 ? true : false
      } else {
        this.isClose = false
      }
      //  this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      let val = this.model.status ? true : false
      this.model.status = val
      this.isStatus = this.model.status
      this.changeOrder(this.model.orderId)
      this.$nextTick(() => {
        this.form.getFieldDecorator('orderName', {rules: this.validatorRules.orderName})
        this.form.setFieldsValue({
          /* recordCode:this.model.recordCode,*/
          recordName: this.model.recordName,
          orderId: this.model.orderId,
          orderName: this.model.orderName,
          groupId: this.model.groupId,
          trainNo: this.model.trainNo,
          orderTaskId: this.model.orderTaskId,
          lineId: this.model.lineId,
          recordDesc: this.model.recordDesc,
        })
        console.log(this.form.getFieldsValue());
      })
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          if (!that.model.orderId) {
            that.model.orderId = ''
          }
          if (!values.orderTaskId) {
            values.orderTaskId = ''
          }
          let formData = Object.assign(that.model, values)
          formData['status'] = that.isStatus ? 1 : 0
          let obj
          if (!that.model.id) {
            obj = addOpenItem(formData)
          } else {
            obj = editOpenItem(formData)
          }
          obj
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
                that.close()
              } else {
                that.$message.error('保存失败')
                console.error('保存开口项失败：', res.message)
              }
            })
            .catch((err) => {
              that.$message.error('保存异常')
              console.error('保存开口项异常：', err)
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
    clearVal(val){
      if (!val) {
        this.model.orderId = null
        this.model.orderTaskId = null
        this.form.setFieldsValue({
          orderName: null,
          orderTaskId: null
        })
      }
    },
    chooseWorkOrder() {
      this.$refs.workOrderSelect.showModal()
    },
    onSelectOrder(data) {
        if (data.length> 0) {
          this.form.setFieldsValue({
            orderName: data[0].orderName,
            orderTaskId: null
          })
          this.model.orderId = data[0].id
          this.changeOrder(data[0].id)
        }
    },
  },
}
</script>

<style scoped>
</style>