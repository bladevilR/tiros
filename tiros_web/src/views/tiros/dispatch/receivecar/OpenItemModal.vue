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
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="暂缓项目内容">
              <a-textarea
                v-decorator.trim="['leaveContent', validatorRules.leaveContent]"
                placeholder="请输入暂缓项目内容"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="20" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="专业系统">
              <a-select
                v-decorator.trim="['assetTypeName', validatorRules.assetTypeName]"
                placeholder="请选择"
                :open="false"
                :showArrow="true"
                @focus="openAssetTypeModal"
                ref="myAssetTypeSelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="20" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工序名称">
              <a-input
                style="width: 100%"
                v-decorator.trim="['operation', validatorRules.operation]"
                placeholder="请输入工序名称"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="20" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="暂缓原因">
              <a-textarea v-decorator.trim="['reason', validatorRules.reason]" placeholder="请输入暂缓原因" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="20" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="计划处理措施">
              <a-textarea
                v-decorator.trim="['planHandle', validatorRules.planHandle]"
                placeholder="请输入计划处理措施"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <train-asset-type
        :multiple="false"
        ref="assetTypeForm"
        :train-type-id="trainTypeId"
        @ok="addTarget"
      ></train-asset-type>
    </a-spin>
  </a-modal>
</template>
<script>
import { addExChangeLeave, editExChangeLeave } from '@api/tirosDispatchApi'
import TrainAssetType from '@views/tiros/common/selectModules/TrainAssetType'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'OpenItemModal',
  components: { TrainAssetType },
  props: ['trainTypeId', 'exchangeId'],
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
      assetTypeId: '',
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        leaveContent: {
          rules: [
            { required: true, message: '请输入暂缓项目内容!' },
            { max: 255, message: '输入长度不能超过255字符!' },
          ],
        },
        assetTypeName: { rules: [{ required: true, message: '请选择专业系统!' }] },
        operation: {
          rules: [
            { required: true, message: '请输入工序名称!' },
            { max: 64, message: '输入长度不能超过64字符!' },
          ],
        },
        reason: {
          rules: [
            { required: true, message: '请输入暂缓原因!' },
            { max: 255, message: '输入长度不能超过255字符!' },
          ],
        },
        planHandle: { rules: [{ max: 500, message: '输入长度不能超过500字符!' }] },
      },
    }
  },

  created() {},
  methods: {
    openAssetTypeModal() {
      this.$refs.assetTypeForm.showModal()
      this.$refs.myAssetTypeSelect.blur()
    },
    addTarget(data) {
      if (!everythingIsEmpty(data)) {
        this.assetTypeId = data[0].id
        this.form.setFieldsValue({ assetTypeName: data[0].name })
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
        this.assetTypeId = record.assetTypeId
      }
      this.$nextTick(() => {
        this.form.setFieldsValue({
          leaveContent: this.model.leaveContent,
          assetTypeName: this.model.assetTypeName,
          operation: this.model.operation,
          reason: this.model.reason,
          planHandle: this.model.planHandle,
        })
      })
    },

    // 确定
    handleOk() {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.confirmLoading = true
          let formData = Object.assign(this.model, values, { assetTypeId: this.assetTypeId })
          this.$emit('addItem', formData, this.activeIndex)
          this.$message.success('添加成功！')
          this.$emit('ok')
          this.close()
          this.activeIndex = null
          this.confirmLoading = false
          //   let obj
          //   if (!that.model.id) {
          //     formData['exchangeId']=this.exchangeId
          //     obj = addExChangeLeave(formData)

          //   } else {
          //     obj = editExChangeLeave(formData)
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