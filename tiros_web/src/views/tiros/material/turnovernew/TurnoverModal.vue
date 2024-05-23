<template>
  <a-modal
    :title='title'
    :width="'70%'"
    :visible='visible'
    :confirmLoading='confirmLoading'
    @ok='handleOk'
    @cancel='handleCancel'
    cancelText='关闭'
    :destroyOnClose='true'
  >
    <a-form :form='form'>
      <a-row :gutter='24'>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='物资编码'>
            <a-input v-decorator.trim="['materialTypeId']" disabled />
          </a-form-item>
        </a-col>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='描述'>
            <a-input v-decorator.trim="['name']" disabled />
          </a-form-item>
        </a-col>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='单位'>
            <a-input v-decorator.trim="['unit', validatorRules.unit]" placeholder='请输入单位' />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter='24'>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='价格'>
            <a-input-number
              :min='0'
              :step='0.00000001'
              :max='99999999'
              style='width: 100%'
              v-decorator.trim="['price', validatorRules.price]"
              placeholder='请输入价格'
            />
          </a-form-item>
        </a-col>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='税率'>
            <a-input-number
              :min='0'
              :step='0.01'
              :max='99999999'
              style='width: 100%'
              v-decorator.trim="['taxRate', validatorRules.taxRate]"
              placeholder='请输入税率'
              disabled='true'
            />
          </a-form-item>
        </a-col>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='含税价格'>
            <a-input-number
              :min='0'
              :step='0.00000001'
              :max='99999999'
              style='width: 100%'
              v-decorator.trim="['taxPrice', validatorRules.taxPrice]"
              placeholder='请输入含税价格'
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter='24'>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='消耗量'>
            <a-input-number
              :min='0'
              :step='0.1'
              :max='99999999'
              style='width: 100%'
              v-decorator.trim="['useAmount', validatorRules.useAmount]"
              placeholder='请输入消耗量'
            />
          </a-form-item>
        </a-col>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='总额'>
            <a-input-number
              :min='0'
              :step='0.00000001'
              :max='99999999'
              style='width: 100%'
              v-decorator.trim="['useAmountPrice', validatorRules.useAmountPrice]"
              placeholder='请输入总额'
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter='24'>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='工单号'>
            <a-input v-decorator.trim="['orderCode', validatorRules.orderCode]" placeholder='请输入工单号' />
          </a-form-item>
        </a-col>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='出库单号'>
            <a-input v-decorator.trim="['outOrderCode', validatorRules.outOrderCode]" placeholder='请输入出库单号' />
          </a-form-item>
        </a-col>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='子库'>
            <a-input v-decorator.trim="['warehouseCode', validatorRules.warehouseCode]" placeholder='请输入子库' />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter='24'>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='消耗时间'>
            <a-date-picker v-decorator.trim="['useTime', validatorRules.useTime]" />
          </a-form-item>
        </a-col>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='所属系统'>
            <j-dict-select-tag
              v-model='model.systemId'
              placeholder='请选择'
              dictCode='bu_train_asset_type,name,id,struct_type=1 and parent_id is null'
            />
          </a-form-item>
        </a-col>
        <!--        <a-col :span='8'>-->
        <!--          <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='系统' prop='systemId'>-->
        <!--            <j-dict-select-tag-->
        <!--              :triggerChange='true'-->
        <!--              v-model='form.systemId'-->
        <!--              :dict-code='dictCodeSystem'-->
        <!--            />-->
        <!--          </a-form-model-item>-->
        <!--        </a-col>-->
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='首次投入使用'>
            <j-dict-select-tag
              v-model='model.firstUsePlanId'
              placeholder='请选择'
              dictCode='bu_repair_plan,plan_name,id'
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter='24'>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='使用年限'>
            <a-input-number
              :min='0'
              :step='0.1'
              :max='99999999'
              style='width: 100%'
              v-decorator.trim="['serviceYear', validatorRules.serviceYear]"
              placeholder='请输入消耗量'
            />
          </a-form-item>
        </a-col>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='使用年限备注'>
            <a-input v-decorator.trim="['serviceYearRemark', validatorRules.serviceYearRemark]"
                     placeholder='请输入使用年限备注' />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter='24'>
        <a-col :md='18' :sm='24'>
          <a-form-item
            :labelCol='{ xs: { span: 24 }, sm: { span: 3 } }'
            :wrapperCol='{ xs: { span: 24 }, sm: { span: 21 } }'
            label='备注'
          >
            <a-textarea v-decorator.trim="['remark', validatorRules.remark]" placeholder='请输入备注' />
          </a-form-item>
        </a-col>
      </a-row>

    </a-form>
  </a-modal>
</template>

<script>
import { pick } from 'xe-utils/methods'
import { saveTurnover } from '@api/tirosMaterialApi'

export default {
  name: 'TurnoverModal',
  data() {
    return {
      title: '编辑',
      visible: false,
      confirmLoading: false,
      disabledArray: [],
      model: {},
      form: this.$form.createForm(this),
      dictCodeSystem: 'bu_train_asset_type,name,id,struct_type=1 and parent_id is null',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 }
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 12 }
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 12 }
      },
      validatorRules: {
        unit: {
          rules: [
            { required: true, message: '请输入单位!' },
            { max: 10, message: '输入长度不能超过10字符!' }
          ]
        },

        price: { rules: [{ required: true, message: '请输入价格!' }] },
        taxRate: { rules: [{ required: true, message: '请输入税率!' }] },
        taxPrice: { rules: [{ required: true, message: '请输入含税价格!' }] },
        useAmount: { rules: [{ required: true, message: '请输入消耗量!' }] },
        useAmountPrice: { rules: [{ required: true, message: '请输入总额!' }] },

        orderCode: { rules: [{ max: 50, message: '输入长度不能超过50字符!' }] },
        outOrderCode: { rules: [{ max: 50, message: '输入长度不能超过50字符!' }] },
        warehouseCode: { rules: [{ max: 50, message: '输入长度不能超过50字符!' }] },

        useTime: { rules: [{ required: true, message: '请选择消耗时间!' }] },

        serviceYear: { rules: [{ required: true, message: '请输入使用年限!' }] },
        serviceYearRemark: { rules: [{ max: 50, message: '输入长度不能超过50字符!' }] },

        remark: { rules: [{ max: 1000, message: '输入长度不能超过1000字符!' }] }
      }
    }
  },
  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      this.visible = true
      this.form.resetFields()
      this.model = Object.assign({}, record)

      this.$nextTick(() => {
        this.form.setFieldsValue(
          pick(this.model, [
            'materialTypeId',
            'name',
            'unit',
            'price',
            'taxRate',
            'taxPrice',
            'useAmount',
            'useAmountPrice',
            'orderCode',
            'outOrderCode',
            'warehouseCode',
            'warehouseId',
            'useTime',
            'systemId',
            'systemName',
            'systemShortName',
            'firstUsePlanId',
            'firstUsePlanName',
            'programId',
            'programName',
            'serviceYear',
            'serviceYearRemark',
            'remark'
          ])
        )
      })
    },
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values)
          let obj
          if (!that.model.id) {
            obj = saveTurnover(formData)
          } else {
            obj = saveTurnover(formData)
          }
          obj
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.close()
                that.$emit('ok')
              } else {
                that.$message.error(res.message)
              }
            })
            .finally(() => {
              that.confirmLoading = false
            })
        }
      })
    }
  }
}
</script>

<style scoped>
</style>