<template>
  <a-modal
    :title="title"
    width="70%"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form" id="specialMaterialForm">
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资类型">
              <a-select
                allowClear
                v-decorator="['materialCode', {}]"
                placeholder="请选择"
                :open="false"
                style="width: 100%"
                @deselect="onSelectMaterial([])"
                @dropdownVisibleChange="openMaterial()"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
            <a-form-item hidden>
              <a-input v-decorator="['materialTypeId', {}]"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="设备编码">
              <a-input :maxLength="33" v-decorator="['assetCode', validatorRules.assetCode]" placeholder="请输入内容"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="设备名称">
              <a-input :maxLength="33" v-decorator="['name', validatorRules.name]" placeholder="请输入内容"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="状态">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['status', validatorRules.status]"
                placeholder="请选择"
                dictCode="bu_tools_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
              <line-select-list v-decorator="['lineId', validatorRules.lineId]"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属车间">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['workshopId', validatorRules.workshopId]"
                placeholder="请选择"
                dictCode="bu_mtr_workshop,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属厂商">
              <a-select
                allowClear
                ref="formSelect"
                v-decorator="['supplierName', {}]"
                placeholder="请选择"
                :open="false"
                style="width: 100%"
                @deselect="onSupplierSelect([])"
                @dropdownVisibleChange="openSupplierModal()"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
            <a-form-item hidden>
              <a-input v-decorator="['supplierId', {}]"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="出厂编码">
              <a-input :maxLength="33" v-decorator="['manufNo', validatorRules.manufNo]" placeholder="名称或编码"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="品牌">
              <a-input :maxLength="33" v-decorator="['brand',validatorRules.brand]" placeholder="请输入内容"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="规格">
              <a-input :maxLength="33" v-decorator="['model',validatorRules.model]" placeholder="请输入内容"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="出厂日期">
              <a-date-picker v-decorator="['leaveFactory', {}]" />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="投入日期">
              <a-date-picker v-decorator="['useDate', {}]" />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="责任人员">
              <a-select
                allowClear
                ref="formSelect"
                v-decorator="['dutyUserName', {}]"
                placeholder="请选择"
                :open="false"
                style="width: 100%"
                @deselect="onUserSelect([])"
                @dropdownVisibleChange="openUserModal()"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
            <a-form-item hidden>
              <a-input v-decorator="['dutyUserId', {}]"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <material-list ref="materialForm" :multiple="false" @ok="onSelectMaterial"></material-list>
      <supplier-list ref="supplierModal" :multiple="false" @ok="onSupplierSelect"></supplier-list>
      <user-list ref="UserModalForm" :multiple="false" @ok="onUserSelect"></user-list>
    </a-spin>
  </a-modal>
</template>

<script>
// import moment from 'moment'
// import 'moment/locale/zh-cn'
import { setSpecassetItem } from '@/api/tirosMaterialApi'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'
import SupplierList from '@views/tiros/common/selectModules/SupplierList'
import UserList from '../../common/selectModules/UserList'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'SpecialMaterialModal',
  components: { MaterialList, SupplierList, UserList, LineSelectList },
  data() {
    return {
      title: '新增',
      visible: false,
      materialTypeId: '',
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 },
      },
      validatorRules: {
        assetCode: { rules: [{ required: true, message: '请输入设备编码!' },{ max: 32, message: '不能超过32个字符' }] },
        name: { rules: [{ required: true, message: '设备名称!' },{ max: 32, message: '不能超过32个字符' }] },
        status: { rules: [{ required: true, message: '请选择状态!' }] },
        lineId: { rules: [{ required: true, message: '请选择线路!' }] },
        workshopId: { rules: [{ required: true, message: '请选择车间!' }] },
        manufNo: { rules: [{ max: 32, message: '不能超过32个字符' }] },
        brand: { rules: [{ max: 32, message: '不能超过32个字符' }] },
        model: { rules: [{ max: 32, message: '不能超过32个字符' }] },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      queryParam: {},
    }
  },
  created() {},
  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      this.form = this.$form.createForm(this)
      this.queryParam = {}
      if (record.id) {
        record = Object.assign(this.queryParam, record)
        console.log(this.queryParam)
      }

      this.$nextTick(() => {
        this.form.setFieldsValue({
          assetCode: record.assetCode,
          dutyUserId: record.dutyUserId,
          dutyUserName: record.dutyUserName,
          leaveFactory: record.leaveFactory ? this.$moment(record.leaveFactory) : '',
          lineId: record.lineId,
          manufNo: record.manufNo,
          materialCode: record.materialCode,
          materialTypeId: record.materialTypeId,
          name: record.name,
          brand: record.brand,
          model: record.model,
          status: record.status,
          supplierId: record.supplierId,
          supplierName: record.supplierName,
          useDate: record.useDate ? this.$moment(record.useDate) : '',
          workshopId: record.workshopId,
        })
      })
      this.visible = true
    },
    // 确定
    handleOk() {
      this.form.validateFields((err, values) => {
        if (!err) {
          console.log(values)
          values.useDate ? (values.useDate = this.$moment(values.useDate).format('YYYY-MM-DD')) : (values.useDate = '')
          values.leaveFactory
            ? (values.leaveFactory = this.$moment(values.leaveFactory).format('YYYY-MM-DD'))
            : (values.leaveFactory = '')

          Object.assign(this.queryParam, values)

          this.confirmLoading = true
          setSpecassetItem(this.queryParam)
            .then((res) => {
              if (res.success) {
                this.$message.success(res.message)
                this.$emit('ok')
              } else {
                this.$message.warning(res.message)
              }
            })
            .finally(() => {
              this.confirmLoading = false
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
    openMaterial() {
      this.$refs.materialForm.showModal()
    },
    openSupplierModal() {
      this.$refs.supplierModal.showModal()
    },
    openUserModal() {
      this.$refs.UserModalForm.showModal()
    },
    onSelectMaterial(records) {
      if (records.length) {
        let record = records[0]
        this.form.setFieldsValue({
          materialTypeId: record.id,
          materialCode: record.code,
          assetCode: record.code,
          name: record.name,
        })
      } else {
        this.form.setFieldsValue({
          materialTypeId: '',
          materialCode: '',
        })
      }
    },
    onSupplierSelect(records) {
      if (records.length) {
        let record = records[0]
        this.form.setFieldsValue({
          supplierId: record.id,
          supplierName: record.name,
        })
      } else {
        this.form.setFieldsValue({
          supplierId: '',
          supplierName: '',
        })
      }
    },
    onUserSelect(records) {
      if (records.length) {
        let record = records[0]
        this.form.setFieldsValue({
          dutyUserId: record.id,
          dutyUserName: record.realname,
        })
      } else {
        this.form.setFieldsValue({
          dutyUserId: '',
          dutyUserName: '',
        })
      }
    },
  },
}
</script>
<style lang="less" scoped>
#specialMaterialForm {
  .ant-calendar-picker {
    width: 100%;
  }
  .hidden-ant-item {
    display: none;
  }
  [hidden] {
    display: none;
  }
}
</style>