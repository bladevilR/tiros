<template>
  <a-modal
    :title="title"
    :width="700"
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
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="仓库编码">
              <a-input placeholder="请输入仓库编码" v-decorator.trim="[ 'code', validatorRules.code]"/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="仓库名称">
              <a-input placeholder="请输入仓库名称" v-decorator.trim="[ 'name', validatorRules.name]"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="上级仓库">
              <!-- <a-input placeholder="请输入上级仓库" v-decorator.trim="[ 'parentId', validatorRules.parentId]" />-->
              <j-tree-select
                placeholder="请选择"
                dict="bu_mtr_warehouse,name,id"
                pidField="parent_id"
                has-child-field="isLeaf"
                v-decorator.trim="[ 'parentId',validatorRules.parentId]"
                @change="selectLevel"
              ></j-tree-select>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="仓库级别">
              <a-input placeholder="请输入仓库名称" v-model="whLevel" disabled/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="仓库类别">
              <j-dict-select-tag v-decorator.trim="[ 'type', validatorRules.type]" :triggerChange="true"
                                 placeholder="请选择仓库类别" dictCode="bu_warehouse_type"/>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆段">
              <j-dict-select-tag v-decorator.trim="[ 'depotId', validatorRules.depotId]" :triggerChange="true"
                                 placeholder="请选择车辆段" dictCode="bu_mtr_depot,name,id"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
<!--              <j-dict-select-tag v-decorator.trim="[ 'lineId', validatorRules.lineId]" :triggerChange="true"
                                 placeholder="请选择线路" dictCode="bu_mtr_line,line_name,line_id"
              />-->
              <line-select-list
                v-decorator="['lineId', validatorRules.lineId]">
              </line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否关账">
              <a-switch v-model="isClose" @change="changeClose"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车间">
              <j-dict-select-tag v-decorator.trim="[ 'workshopId', validatorRules.workshopId]" :triggerChange="true"
                                 placeholder="请选择车间" dictCode="bu_mtr_workshop,name,id"/>
            </a-form-item>

          </a-col>
        </a-row>
        <a-form-item label="位置描述">
          <a-textarea v-decorator.trim="[ 'location', validatorRules.location]"/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>

</template>

<script>
import { addWarehouse, editWarehouse, getWarehouseOne } from '../../../../api/tirosMaterialApi'
import JTreeSelect from '@/components/jeecg/JTreeSelect'
import { duplicateCheck } from '@api/api'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'


export default {
  name: 'WarehouseItemModal',
  props: ['parentId'],
  components: { JTreeSelect ,LineSelectList},
  data() {
    return {
      queryParam: {
        title: '',
        parentId: null
      },
      selectTreeNode: [],
      defaultKey: [],
      whLevel: '',
      value: 1,
      title: '操作',
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 }
      },
      isClose: false,
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        name: { rules: [{ required: true, message: '请输入仓库名称!' }, { max: 32, message: '输入长度不能超过32字符!' }] },
        code: {
          rules: [{ required: true, message: '请输入仓库编码!' }, { validator: this.validateCode }, {
            max: 32,
            message: '输入长度不能超过32字符!'
          }]
        },
        whLevel: { rules: [{ required: true, message: '请输选择仓库级别!' }] },
        parentId: { rules: [{ required: true, message: '请输选择上级级别!' }] },
        type: { rules: [{ required: true, message: '请选择仓库类别!' }] },
        depotId: { rules: [{ required: true, message: '请选择车辆段!' }] },
        lineId: { rules: [{ required: true, message: '请选择车辆段!' }] },
        workshopId: { rules: [{ required: true, message: '请选择车间!' }] },
        location: { rules: [{ max: 64, message: '输入长度不能超过64字符!' }] }
      }
    }
  },
  created() {
  },
  methods: {
    validateCode(rule, value, callback) {
      let params = {
        tableName: 'bu_mtr_warehouse',
        fieldName: 'code',
        fieldVal: value,
        dataId: this.model.id,
        filterFields: [{ name: 'parent_id', val: this.model.parentId }]
      }
      duplicateCheck(params).then((res) => {
        if (res.success) {
          callback()
        } else {
          callback(res.message)
        }
      })
    },
    selectLevel(value) {
      let param = { id: value }
      getWarehouseOne(param).then((res) => {
        if (res.success) {
          this.whLevel = res.result.whLevel + 1
        }
      })
    },
    changeClose(checked) {
      this.isClose = checked
    },
    add() {
      this.edit({})
    },
    edit(record) {
      if (record.id) {
        this.isClose = record['close'] && record['close'] == 1 ? true : false
        this.whLevel = record['whLevel']
      } else {
        this.isClose = false
        this.whLevel = ''

      }
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      if (!this.model.parentId) {
        this.model.parentId = this.parentId
        this.selectLevel(this.parentId)
      }
      this.$nextTick(() => {
        this.form.setFieldsValue({
          code: this.model.code,
          name: this.model.name,
          parentId: this.model.parentId,
          type: this.model.type,
          depotId: this.model.depotId,
          lineId: this.model.lineId,
          location: this.model.location,
          workshopId: this.model.workshopId
        })
      })
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values, {
            whLevel: that.whLevel, close: that.isClose ? 1 : 0
          })
          let obj
          if (!that.model.id) {
            obj = addWarehouse(formData)
          } else {
            obj = editWarehouse(formData)
          }
          obj
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