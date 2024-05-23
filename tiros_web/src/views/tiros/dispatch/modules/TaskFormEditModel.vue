<template>
  <a-modal
    :title='title'
    :visible='visible'
    :confirmLoading='confirmLoading'
    @ok='handleOk'
    @cancel='handleCancel'
    cancelText='关闭'
    :destroyOnClose='true'
    centered
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="标题">
              <a-input v-decorator.trim="[ 'title', validatorRules.title]" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆设备">
              <div @click="openAssetSelectModal">
                <a-select
                  ref="trainAssetSelect"
                  placeholder="请选择作业目标设备"
                  v-decorator.trim="['trainStructName', validatorRules.trainStructName]"
                  :open="false"
                  style="width: 100%"
                >
                  <a-icon slot="suffixIcon" type="ellipsis" />
                </a-select>
              </div>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <CarDeviceSelectNew
        ref="trainAssetSelectModal"
        :trainNo="trainNo"
        :lineId="lineId"
        :multiple="false"
        :canSelectType="[2,3,4]"
        @ok="onTrainAssetSelect"
        @cancel="onCancelTrainAssetSelect"
      >
      </CarDeviceSelectNew>
    </a-spin>
  </a-modal>
</template>
<script>
import CarDeviceSelectNew from '@views/tiros/common/selectModules/CarDeviceSelectNew'
import { updateFormTitleOrAsset } from '@api/tirosDispatchApi'

export default {
  name: 'TaskFormEditModel',
  components:{CarDeviceSelectNew},
  props:['lineId', 'trainNo'],
  data() {
    return {
      title: '编辑',
      visible: false,
      trainStructId:'',
      model: {},
      confirmLoading:false,
      form: this.$form.createForm(this),
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      validatorRules: {
        title: { rules: [{ required: true, message: '请输入标题!'},{max:64 ,message: '输入长度不能超过64字符!'}] },
        trainStructName: { rules: [{ required: true, message: '请选择车辆设备!' }] }
      }
    }
  },
  methods:{
    edit(record){
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          title: this.model.title,
          trainStructName: this.model.structDetailName,
        })
        this.trainStructId=this.model.trainStructId
      })

    },
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
           that.confirmLoading = true
          let formData = Object.assign(that.model, values,{trainStructId:that.trainStructId,formType:that.model.formType})
          updateFormTitleOrAsset(formData).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
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
    // 弹出设备选择界面
    openAssetSelectModal() {
      this.$refs.trainAssetSelectModal.showModal()
    },
    // 设备选择回调
    onTrainAssetSelect(data) {
      if (data.length) {
        const item = data[0]
        this.trainStructId = item.id
        this.$nextTick(() => {
          this.form.setFieldsValue({
            trainStructName: item.assetName,
          })
        })
      }
    },
    // 设备选择取消回调
    onCancelTrainAssetSelect() {},
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