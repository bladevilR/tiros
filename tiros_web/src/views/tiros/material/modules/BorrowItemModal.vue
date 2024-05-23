<template>

  <a-modal
    :title="title"
    :width="'90%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :bodyStyle="{ minHeight: '450px'}"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属仓库">
              <!--              <j-dict-select-tag
                              @focus="handleWarehouseFocus"
                              placeholder="请选择"
                              :dictCode="`bu_mtr_warehouse,name,id,id in (select warehouse_id from bu_material_stock where material_type_id ='${materialTypeId}')`"
                              :triggerChange="true"
                              v-decorator.trim="[ 'warehouseId',validatorRules.warehouseId]"
                              :allowClear="true">
                            </j-dict-select-tag>-->
              <j-tree-select
                placeholder="请选择"
                dict="bu_mtr_warehouse,name,id"
                pidField="parent_id"
                v-decorator.trim="[ 'warehouseId',validatorRules.warehouseId]"
                @change="handleWarehouseChange"

              >
              </j-tree-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="借用物料">
              <a-select
                placeholder="请选择"
                :open="false"
                :showArrow="true"
                v-decorator.trim="['materialTypeName', validatorRules.materialTypeName]"
                @focus="openModal"
                ref="mySelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资编码">
              <a-input placeholder="请输入物资编码" v-decorator.trim="[ 'materialTypeCode']" disabled />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="借用数量">
              <a-input-number :min="1" :max="99999999" v-decorator.trim="[ 'amount', validatorRules.amount]"
                              style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="归还类型">
              <j-dict-select-tag
                v-decorator.trim="[ 'returnType', validatorRules.returnType]"
                :triggerChange="true"
                placeholder="请选择"
                dictCode="bu_material_return_type"
                :allowClear="true"
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="归还状态">
              <j-dict-select-tag
                v-decorator.trim="[ 'returnStatus', validatorRules.returnStatus]"
                :triggerChange="true"
                placeholder="请选择"
                dictCode="bu_material_return_status"
                :allowClear="true"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="借用组织">
              <!--<j-dict-select-tag
                v-decorator.trim="[ 'deptId']"
                :triggerChange="true"
                placeholder="请选择"
                dictCode="sys_depart,depart_name,id"
                :allowClear="true"
              />-->
              <j-select-depart v-decorator.trim="[ 'deptId',validatorRules.deptId]" :rootOpened="false"
                               @change="handleDept" />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24" v-if="deptId!==''&&deptId!=null">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="借用人员">
              <!-- <j-dict-select-tag
                 v-decorator.trim="[ 'borrowUserId', validatorRules.borrowUserId]"
                 :triggerChange="true"
                 placeholder="请选择"
                 :dictCode="`sys_user,username,id,id in (select user_id from sys_user_depart where dep_id='${deptId}' )`"
                 :allowClear="true"
               />-->
              <a-select
                v-decorator.trim="[ 'borrowUserId', validatorRules.borrowUserId]"
                :allowClear="true"
                placeholder="请选择人员">
                <a-select-option v-for=" u in userList" :key="u.id" :value="u.id">{{ u.realname }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="借用日期">
              <a-date-picker style="width: 100%" v-decorator.trim="[ 'borrowDate',validatorRules.borrowDate]" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item label="借用描述">
              <a-textarea v-decorator="[ 'remark',validatorRules.remark]" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
    <material-list ref="MaterialModalForm" :multiple="false" @ok="addTarget" :disabled="true"
                   :default-checked-keys="defaultChecked"></material-list>
  </a-modal>

</template>

<script>
import { addBorrow, editBorrow } from '@/api/tirosMaterialApi'
import JTreeSelect from '@/components/jeecg/JTreeSelect'
import moment from 'moment'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'
import JSelectDepart from '@/components/jeecgbiz/JSelectDepart'
import { getUserByDepId } from '@api/api'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'BorrowItemModal',
  components: { JTreeSelect, MaterialList, JSelectDepart },
  data () {
    return {
      title: '操作',
      visible: false,
      materialTypeId: '',
      materialTypeName: '',
      defaultChecked: '',
      model: {},
      userList: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      deptId: '',
      validatorRules: {
        warehouseId: { rules: [{ required: true, message: '请选择所属仓库!' }] },
        materialTypeName: { rules: [{ required: true, message: '请选择借用物资!' }] },
        amount: { rules: [{ required: true, message: '请输入借用数量!' }], initialValue: 1 },
        returnType: { rules: [{ required: true, message: '请输选择归还类型!' }] },
        returnStatus: { rules: [{ required: true, message: '请选择归还状态!' }] },
        borrowUserId: { rules: [{ required: true, message: '请选择借用人员!' }] },
        borrowDate: { rules: [{ required: true, message: '请选择借用日期!' }] },
        deptId: { rules: [{ required: true, message: '请选择借用组织!' }] },
        remark: { rules: [{ max: 200, message: '输入长度不能超过200字符!' }] }
      }
    }
  },
  created () {
  },
  methods: {
    handleWarehouseChange (data) {
      this.materialTypeId=''
      this.form.setFieldsValue({
        materialTypeName: '',
        materialTypeCode: ''
      })
      this.defaultChecked = data
    },
    handleDept (value) {
      this.deptId = value
      let param = { id: value }
      getUserByDepId(param).then((res) => {
        if (res.success) {
          this.userList = res.result
        }
      })
    },
    openModal () {
      if (everythingIsEmpty(this.defaultChecked)) {
        this.$message.warn('请先选择仓库！')
      } else {
        this.$refs.MaterialModalForm.showModal()
        this.$refs.mySelect.blur()
      }
    },
    addTarget (data) {
      if (data && data.length === 1 && data[0] != null) {
        this.materialTypeId = data[0].id
        this.form.setFieldsValue({
          materialTypeName: data[0].name,
          materialTypeCode: data[0].code,
          amount: data[0].num
        })
      }
    },
    add () {
      this.edit({})
    },
    edit (record) {
      this.form.resetFields()
      this.visible = true
      this.model = Object.assign({}, record, { borrowDate: moment(record.borrowDate || undefined) })
      if (record.id) {
        this.materialTypeId = record.materialTypeId
        this.defaultChecked = record.warehouseId
        this.deptId=record.deptId
        this.$nextTick(() => {
          this.form.setFieldsValue({
            warehouseId: this.model.warehouseId,
            materialTypeName: this.model.materialTypeName,
            amount: this.model.amount,
            materialTypeCode: this.model.materialTypeCode,
            returnType: this.model.returnType,
            returnStatus: this.model.returnStatus,
            borrowUserId: this.model.borrowUserId,
            borrowDate: this.model.borrowDate,
            remark: this.model.remark,
            deptId: this.model.deptId
          })
        })
      }
    },
    // 确定
    handleOk () {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values, {
            borrowDate: moment(values.borrowDate).format('YYYY-MM-DD'),
            materialTypeId: this.materialTypeId
          })
          let obj
          if (!that.model.id) {
            obj = addBorrow(formData)
          } else {
            obj = editBorrow(formData)
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
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
      this.defaultChecked = ''
    }
  }
}
</script>