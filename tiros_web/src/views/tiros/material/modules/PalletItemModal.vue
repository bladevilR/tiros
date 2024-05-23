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
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="托盘编码">
              <a-input placeholder="请输入托盘编码" v-decorator.trim="['code', validatorRules.code]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="托盘名称">
              <a-input placeholder="请输入托盘名称" v-decorator.trim="['name', validatorRules.name]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资类型">
              <a-select
                v-model="materNameList"
                placeholder="请选择物资类型"
                :open="false"
                :showArrow="true"
                @focus="openmaterModal"
                ref="mymaterSelect"
                mode="multiple"
                @deselect="onDeselect"
                option-label-prop="label"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="尺寸大小">
              <a-input placeholder="请输入尺寸大小" v-decorator.trim="['palletSize', validatorRules.palletSize]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="材质描述">
              <a-input placeholder="请输入材质描述" v-decorator.trim="['texture', validatorRules.texture]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="10" :sm="24" :offset="4">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="使用状态">
              <a-switch v-model="useStatus" @change="changeUserStatus" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="10" :sm="24" :offset="4">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否可用">
              <a-switch v-model="state" @change="changeStatus" />
            </a-form-item>
          </a-col>
          <a-col :md="10" :sm="24" :hidden="showBatch">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-checkbox v-model="isBatchAdd">批量新增</a-checkbox>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24" :hidden="!isBatchAdd">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="编码序号">
              <a-input-number v-model="batchAddStartNum" :min="1" :max="batchAddEndNum" />
              ~
              <a-input-number v-model="batchAddEndNum" :min="batchAddStartNum + 1" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24" :hidden="!isBatchAdd">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="提示">
              <span style="color: #f00">*批量新增时，编码和名称只要输入前缀</span>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
              <a-textarea v-decorator="['remark', validatorRules.remark]" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
    <material-list ref="materModalForm" :multiple="true" @ok="selectmater"></material-list>
  </a-modal>
</template>

<script>
import { addPallet, editPallet } from '@/api/tirosMaterialApi'
import { duplicateCheck } from '@api/api'
import { everythingIsEmpty } from '@/utils/util'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'

export default {
  name: 'PalletItemModal',
  components: { MaterialList },
  data() {
    return {
      showBatch: false,
      isBatchAdd: false,
      state: true,
      useStatus: false,
      batchStateOptions: [
        {
          label: '批量新增',
          value: true,
        },
      ],
      batchAddStartNum: 1,
      batchAddEndNum: 10,
      title: '操作',
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        name: {
          rules: [
            { required: true, message: '请输入托盘名称!' },
            { max: 32, message: '输入长度不能超过32字符!' },
          ],
        },
        code: {
          rules: [
            { required: true, message: '请输入托盘编码!' },
            { validator: this.validateCode },
            { max: 32, message: '输入长度不能超过32字符!' },
          ],
        },
        texture: { rules: [{ max: 16, message: '输入长度不能超过16字符!' }] },
        palletSize: { rules: [{ max: 16, message: '输入长度不能超过16字符!' }] },
        remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] },
      },
      materNameList: [],
      materTypeList: [],
    }
  },
  created() {},
  methods: {
    validateCode(rule, value, callback) {
      let params = {
        tableName: 'bu_material_pallet',
        fieldName: 'code',
        fieldVal: value,
        dataId: this.model.id,
      }
      if (!everythingIsEmpty(value)) {
        duplicateCheck(params).then((res) => {
          if (res.success) {
            callback()
          } else {
            callback(res.message)
          }
        })
      } else {
        callback()
      }
    },
    add() {
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      this.isBatchAdd = false
      if (record.id) {
        this.state = !!(record['status'] && record['status'] === 1)
        this.useStatus = !!(record['useStatus'] && record['useStatus'] === 1)
        // 当有值时进行数组操作 避免无值时报错
        this.materTypeList = record.materialTypes ? record.materialTypes.split(',') : [];
        this.materNameList = record.materialTypeNames ? record.materialTypeNames.split(',') : [];
      } else {
        this.state = true
        this.useStatus = false
        this.materTypeList = []
        this.materNameList = []
      }
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          code: this.model.code,
          name: this.model.name,
          palletSize: this.model.palletSize,
          texture: this.model.texture,
          remark: this.model.remark,
        })
      })
    },
    changeStatus(checked) {
      this.state = checked
    },
    changeUserStatus(checked) {
      this.useStatus = checked
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          // that.confirmLoading = true
          let formData = Object.assign(that.model, values)
          formData['materialTypes'] = this.materTypeList.join(',')
          formData['materialTypeNames'] = this.materNameList.join(',')
          formData['status'] = that.state ? 1 : 0
          formData['useStatus'] = that.useStatus ? 1 : 0
          formData['isBatchAdd'] = that.isBatchAdd
          if (that.isBatchAdd) {
            formData['batchAddStartNum'] = that.batchAddStartNum
            formData['batchAddEndNum'] = that.batchAddEndNum
          }
          let obj
          if (!that.model.id) {
            obj = addPallet(formData)
          } else {
            obj = editPallet(formData)
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
    },
    openmaterModal() {
      this.$refs.materModalForm.showModal()
      this.$refs.mymaterSelect.blur()
    },
    selectmater(data) {
      data.forEach((element) => {
        this.materNameList.push(element.name)
        this.materTypeList.push(element.id)
      })
    },
    onDeselect(data) {
      this.materTypeList.splice(this.materNameList.indexOf(data), 1)
    },
  },
}
</script>