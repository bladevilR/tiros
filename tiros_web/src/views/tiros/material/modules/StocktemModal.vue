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
        <!--        <a-row :gutter="24">
                  <a-col :md="12" :sm="24">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资属性">
                      <j-dict-select-tag
                        :triggerChange="true"
                        placeholder="请选择"
                        dictCode="bu_material_attr"
                        v-decorator.trim="[ 'materialAttr',validatorRules.materialAttr]"
                      />
                    </a-form-item>
                  </a-col>
                </a-row>-->
        <!-- <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="有效提前">
              <a-input-number
                style="width: 100%"
                :min="0"
                :formatter="value => `${value}%`"
                :parser="value => value.replace('%', '')"
                v-decorator.trim="[ 'lead',validatorRules.lead]" />
            </a-form-item>
          </a-col>
          <a-col>
            * 设置值为有效期的百分比，如25%，默认25%，即五分之一
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="安全库存">
              <a-input-number style="width: 100%" :min="0.00" :step="0.01"
                              v-decorator.trim="[ 'theshold',validatorRules.theshold]" />
            </a-form-item>
          </a-col>
          <a-col>
            * 3列车的需求量为预警值
          </a-col>
        </a-row> -->
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="一列需求量">
              <a-input-number
                style="width: 100%"
                :min="0.0"
                :step="0.01"
                v-decorator.trim="['oneRequire', validatorRules.oneRequire]"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="3列需求量">
              <a-input-number
                style="width: 100%"
                :min="0.0"
                :step="0.01"
                v-decorator.trim="['theshold', validatorRules.twoRequire]"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="15列需求量">
              <a-input-number
                style="width: 100%"
                :min="0.0"
                :step="0.01"
                v-decorator.trim="['threeRequire', validatorRules.threeRequire]"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import moment from 'moment'
import 'moment/locale/zh-cn'
import { editStockAttr, getStockAttr } from '@api/tirosMaterialApi'
import JTreeSelect from '@/components/jeecg/JTreeSelect'

export default {
  name: 'StockItemModal',
  components: { JTreeSelect },
  data() {
    return {
      title: '操作',
      visible: false,
      materialTypeId: '',
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 },
      },
      validatorRules: {
        // materialAttr: { rules: [{ required: true, message: '请选择物资属性!' }] },
        // lead: { rules: [{ required: false, message: '请输入有效期!' }] },
        // theshold: { rules: [{ required: false, message: '请输入安全库存!' }] },
        oneRequire: { rules: [{ required: false, message: '请输入一列车需求量!' }] },
        twoRequire: { rules: [{ required: false, message: '请输入3列车需求量!' }] },
        threeRequire: { rules: [{ required: false, message: '请输入15列车需求量!' }] },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
    }
  },
  created() {},
  methods: {
    moment,
    edit(materialTypeId) {
      this.form.resetFields()
      this.visible = true
      this.materialTypeId = materialTypeId
      let param = { materialTypeId: materialTypeId }
      console.log('param' + JSON.stringify(param))
      getStockAttr(param).then((res) => {
        if (res.success && res.result) {
          this.form.setFieldsValue({
            // materialAttr: res.result.materialAttr,
            lead: res.result.lead,
            theshold: res.result.theshold,
            oneRequire: res.result.oneRequire,
            threeRequire: res.result.threeRequire,
          })
        }
      })
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          let formData = Object.assign(that.model, values, {
            materialTypeId: this.materialTypeId,
            twoRequire: values.theshold,
          })
          editStockAttr(formData)
            .then((res) => {
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
    },
  },
}
</script>