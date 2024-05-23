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
            <a-input v-decorator.trim="['code', validatorRules.code]" :disabled='!isAdd' />
          </a-form-item>
        </a-col>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='物资名称'>
            <a-input v-decorator.trim="['name', validatorRules.name]" placeholder='请输入物资名称' />
          </a-form-item>
        </a-col>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='单价'>
            <a-input-number
              :min='0'
              :step='0.1'
              :max='99999999'
              style='width: 100%'
              v-decorator.trim="['price', validatorRules.price]"
              placeholder='请输入单价'
            />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter='24'>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='单位'>
            <a-input v-decorator.trim="['unit', validatorRules.unit]" placeholder='请输入单位' />
          </a-form-item>
        </a-col>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='物资种类'>
            <j-dict-select-tag
              :trigger-change='true'
              v-decorator.trim="['kind', validatorRules.kind]"
              dictCode='bu_material_kind'
              @change='handleKind'
              placeholder='请选择'
            />
          </a-form-item>
        </a-col>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='物资分类'>
            <j-dict-select-tag
              :trigger-change='true'
              v-decorator.trim="['category1', validatorRules.category1]"
              :dictCode='dicType'
              @focus='handleTypeFocus'
              placeholder='请选择'
            />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter='24'>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='物资属性'>
            <j-dict-select-tag
              :trigger-change='true'
              v-decorator.trim="['category2']"
              dictCode='bu_material_attr'
              placeholder='请选择'
            />
          </a-form-item>
        </a-col>
        <a-col :md='5' :sm='24'>
          <a-form-item :labelCol='labelCol1' :wrapperCol='wrapperCol1' label='是否有效'>
            <a-switch v-model='status' />
          </a-form-item>
        </a-col>
        <a-col :md='5' :sm='24'>
          <a-form-item :labelCol='labelCol1' :wrapperCol='wrapperCol1' label='是否固定资产'>
            <a-switch v-model='isAsset' />
          </a-form-item>
        </a-col>
        <a-col :md='5' :sm='24'>
          <a-form-item :labelCol='labelCol1' :wrapperCol='wrapperCol1' label='是否总库直发'>
            <a-switch v-model='fromHead' />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter='24'>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='费用科目'>
            <a-input v-decorator.trim="['subject', validatorRules.subject]" placeholder='费用科目，编码' />
          </a-form-item>
        </a-col>
        <a-col :md='8' :sm='24'>
          <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='列管消耗'>
            <j-dict-select-tag
              :trigger-change='true'
              v-decorator.trim="['isConsume']"
              dictCode='bu_material_consume'
              placeholder='请选择'
            />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter='24'>
        <a-col :md='18' :sm='24'>
          <a-form-item
            :labelCol='{ xs: { span: 24 }, sm: { span: 3 } }'
            :wrapperCol='{ xs: { span: 24 }, sm: { span: 21 } }'
            label='有效提前'
          >
            <a-input-number
              style='width: 100%'
              :min='0'
              :formatter='(value) => `${value}%`'
              :parser="(value) => value.replace('%', '')"
              v-decorator.trim="['lead', validatorRules.lead]"
            />
          </a-form-item>
        </a-col>
        <a-col :md='6' :sm='24'> * 设置值为有效期的百分比，如25%，默认25%，即五分之一</a-col>
      </a-row>
      <a-row :gutter='24'>
        <a-col :md='18' :sm='24'>
          <a-form-item
            :labelCol='{ xs: { span: 24 }, sm: { span: 3 } }'
            :wrapperCol='{ xs: { span: 24 }, sm: { span: 21 } }'
            label='安全库存'
          >
            <a-input-number
              style='width: 100%'
              :min='0.0'
              :step='0.01'
              v-decorator.trim="['theshold', validatorRules.theshold]"
            />
          </a-form-item>
        </a-col>
        <a-col :md='6' :sm='24'> * 3列车的需求量为预警值</a-col>
      </a-row>
      <a-row :gutter='24'>
        <a-col :md='18' :sm='24'>
          <a-form-item
            :labelCol='{ xs: { span: 24 }, sm: { span: 3 } }'
            :wrapperCol='{ xs: { span: 24 }, sm: { span: 21 } }'
            label='规格描述'
          >
            <a-textarea v-decorator.trim="['spec', validatorRules.spec]" placeholder='请输入规格描述' />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter='24'>
        <a-col :md='18' :sm='24'>
          <a-form-item
            :labelCol='{ xs: { span: 24 }, sm: { span: 3 } }'
            :wrapperCol='{ xs: { span: 24 }, sm: { span: 21 } }'
            label='可替换物资'
          >
            <a-textarea v-decorator.trim="['canReplace']" disabled />
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
import { addMaterialType, editMaterialType } from '@api/tirosMaterialApi'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'MaterialTypeModal',
  data() {
    return {
      title: '新增',
      visible: false,
      confirmLoading: false,
      disabledArray: [],
      status: true,
      isAsset: false,
      fromHead: false,
      model: {},
      isAdd: false,
      form: this.$form.createForm(this),
      dicType: '',
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
        code: {
          rules: [
            { required: true, message: '请输入物资编码!' },
            { max: 16, message: '输入长度不能超过16字符!' }
          ]
        },
        name: {
          rules: [
            { required: true, message: '请输入物资名称!' },
            { max: 100, message: '输入长度不能超过100字符!' }
          ]
        },
        price: { rules: [{ required: true, message: '请输入单价!' }] },
        unit: {
          rules: [
            { required: true, message: '请输入单位!' },
            { max: 32, message: '输入长度不能超过32字符!' }
          ]
        },
        kind: { rules: [{ required: true, message: '请选择物资种类!' }] },
        category1: { rules: [{ required: true, message: '请选择物资分类!' }] },
        lead: { rules: [{ required: false, message: '请输入有效期!' }] },
        theshold: { rules: [{ required: false, message: '请输入安全库存!' }] },
        spec: { rules: [{ max: 200, message: '输入长度不能超过200字符!' }] },
        remark: { rules: [{ max: 256, message: '输入长度不能超过256字符!' }] },
        subject: { rules: [{ max: 32, message: '输入长度不能超过32字符!' }] }
      }
    }
  },
  methods: {
    handleKind(data) {
      console.log('handleKind(data) data=', data )
      console.log('handleKind(data) data === 2?', data === '2' )
      console.log('handleKind(data) data === 1?', data === '1' )

      this.form.setFieldsValue({ category1: '' })
      if (data) {
        if (data === '2') {
          this.dicType = 'bu_tools_type'
        }
        if (data === '1') {
          this.dicType = 'bu_material_type'
        }
      } else {
        this.dicType = ''
      }
    },

    handleTypeFocus() {
      let kind = this.form.getFieldValue('kind')
      if (everythingIsEmpty(kind)) this.$message.warn('请先选择物资种类')
    },

    add() {
      this.edit({})
    },

    edit(record) {
      this.visible = true
      this.form.resetFields()
      this.model = Object.assign({}, record)
      if (this.model.id) {
        this.status = this.model.status === 1
        this.isAsset = this.model.isAsset === 1
        this.fromHead = this.model.fromHead === 1
        this.isAdd = false
      } else {
        this.isAdd = true
      }

      if (this.model.kind === 2) this.dicType = 'bu_tools_type'
      if (this.model.kind === 1) this.dicType = 'bu_material_type'
      this.$nextTick(() => {
        this.form.setFieldsValue(
          pick(this.model, [
            'code',
            'name',
            'price',
            'unit',
            'kind',
            'category1',
            'category2',
            'subject',
            'isConsume',
            'lead',
            'theshold',
            'spec',
            'canReplace',
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
          let formData = Object.assign(that.model, values, {
            status: this.status ? 1 : 0,
            isAsset: this.isAsset ? 1 : 0,
            fromHead: this.fromHead ? 1 : 0
          })
          let obj
          if (!that.model.id) {
            obj = addMaterialType(formData)
          } else {
            obj = editMaterialType(formData)
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