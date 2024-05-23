<template>
  <a-modal
    :title="title"
    :width="'60%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :bodyStyle="{ minHeight: '450px' }"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :md="12" :sm="24" v-if="!isEdit">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资类型选择">
              <a-input
                allowClear
                placeholder="请选择"
                :open="false"
                :showArrow="true"
                v-decorator.trim="['materialTypeName']"
                @focus="openModal"
                ref="mySelect"
                @change="clearSelectInfo"
                :disabled="isEdit"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单列所需数量">
              <a-input-number
                style="width: 100%"
                :min="1"
                :max="999999999"
                placeholder="请输入物资单列所需数量"
                v-decorator.trim="['needAmount', validatorRules.needAmount]"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资编码">
              <a-input
                placeholder="请输入物资编码"
                v-decorator.trim="['code', validatorRules.code]"
                :disabled="isSelectData"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属线路">
              <line-select-list v-decorator.trim="['lineId', validatorRules.lineId]"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资名称">
              <a-input
                placeholder="请输入物资名称"
                v-decorator.trim="['name', validatorRules.name]"
                :disabled="isSelectData"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属修程">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['repairProgramId', validatorRules.repairProgramId]"
                placeholder="请选择"
                dictCode="bu_repair_program,name,id"
                @change="selectRepairProgram"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资单位">
              <a-input
                placeholder="请输入物资单位"
                v-decorator.trim="['unit', validatorRules.unit]"
                :disabled="isSelectData"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属工位">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['workstationId', validatorRules.workstationId]"
                placeholder="请选择"
                dictCode="bu_mtr_workstation,name,id"
                @change="selectWorkstation"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资属性">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['materialTypeCategory', validatorRules.materialTypeCategory]"
                placeholder="请选择"
                dictCode="bu_material_attr"
                :disabled="isSelectData"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="安装部位">
              <a-input
                ref="inputAssetSelect"
                v-decorator.trim="['locationName', validatorRules.location]"
                placeholder="安装部位"
                style="width: 100%"
                @focus="showAssetModal"
              >
                <a-icon slot="suffix" type="ellipsis" />
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资描述">
              <a-textarea v-decorator="['spec', validatorRules.spec]" :disabled="isSelectData" />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
              <a-textarea allowClear :maxLength="201" v-decorator="['remark', validatorRules.remark]">
              </a-textarea>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属班组">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['groupId']"
                placeholder="请选择班组"
                :dictCode="dictGroupStr"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
    <material-list
      ref="MaterialModalForm"
      :multiple="false"
      @ok="addTarget"
      :disabled="true"
      :default-checked-keys="defaultChecked"
    ></material-list>
    <train-structure-list ref="assetSelect" :multiple="false" @ok="addAsset"></train-structure-list>
  </a-modal>
</template>

<script>
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { saveWillChangeItem, editWillChangeItem } from '@/api/tirosMaterialApi'
import JTreeSelect from '@/components/jeecg/JTreeSelect'
import moment from 'moment'
import { everythingIsEmpty } from '@/utils/util'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'
import JSelectDepart from '@/components/jeecgbiz/JSelectDepart'
import TrainStructureList from '@views/tiros/common/selectModules/TrainStructureList'

export default {
  name: 'WillChangeItemModal',
  components: { JTreeSelect, MaterialList, JSelectDepart, LineSelectList, TrainStructureList },
  data() {
    return {
      dictGroupStr:
        "bu_mtr_workshop_group,group_name,id,workshop_id='" + this.$store.getters.userInfo.workshopId + "'|workshop_id",
      title: '操作',
      visible: false,
      materialTypeId: '',
      materialTypeName: '',
      defaultChecked: '',
      isSelectData: false,
      isEdit: false,
      model: {},
      userList: [],
      trainTypeId: '',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      deptId: '',
      validatorRules: {
        needAmount: { rules: [{ required: true, message: '请输入单列所需数量!' }], initialValue: 1 },
        code: { rules: [{ required: true, message: '请输入物资编码!' }] },
        lineId: { rules: [{ required: true, message: '请选择线路!' }] },
        name: { rules: [{ required: true, message: '请输入物资名称!' }] },
        repairProgramId: { rules: [{ required: true, message: '请选择所属修程!' }] },
        unit: { rules: [{ required: true, message: '请输入单位!' }] },
        workstationId: { rules: [{ required: true, message: '请选择所属工位!' }] },
        materialTypeCategory: { rules: [{ required: true, message: '请选择物资属性!' }] },
        location: { rules: [{ required: true, message: '请选择安装部位!' }] },
        spec: { rules: [{ required: true, max: 200, message: '请输入物资描述，不能超过200字符!' }] },
        remark:{ rules: [{ required: false, max: 255, message: '输入长度不能超过255个字符!' }] },
      },
    }
  },
  created() {},
  methods: {
    openModal() {
      this.$refs.MaterialModalForm.showModal()
      this.$refs.mySelect.blur()
    },
    // 弹出车辆结构界面
    showAssetModal() {
      // if (!this.model.trainNo) {
      //   this.$message.warn('请先选择车辆!')
      // } else {
      //   this.$refs.assetSelect.showModal(this.trainStructId)
      // }
      this.$refs.assetSelect.showModal()
      this.$refs.inputAssetSelect.blur()
    },
    addAsset(data) {
      // console.log(data)
      if (!everythingIsEmpty(data)) {
        this.form.setFieldsValue({
          locationName: data[0].name,
        })
        this.model.location = data[0].id
      }
    },
    addTarget(data) {
      if (data && data.length === 1 && data[0] != null) {
        this.isSelectData = true
        this.materialTypeId = data[0].id
        this.form.setFieldsValue({
          materialTypeName: data[0].name,
          amount: data[0].num,
          code: data[0].code,
          name: data[0].name,
          needAmount: data[0].num,
          unit: data[0].unit,
          materialTypeCategory: data[0].category2,
          spec: data[0].spec,
          remark: data[0].remark,
        })
      }
    },
    // 设备选择回调
    onAssetTypeSelect(data) {
      // console.log(data)
      if (data.length) {
        const item = data[0]
        // if (item) {
        // } else {
        //   console.log(item)
        // }
      } else {
      }
    },
    // 设备选择取消回调
    onCancelAssetTypeSelect() {},
    clearSelectInfo(e) {
      if (!e.currentTarget.value) {
        this.isSelectData = false
        this.form.setFieldsValue({
          materialTypeName: '',
          amount: '',
          code: '',
          name: '',
          unit: '',
          materialTypeCategory: '',
          spec: '',
          needAmount: '',
          remark: '',
        })
      }
    },
    selectRepairProgram(v, option) {
      // 选择修程
      if (option)
        this.form.setFieldsValue({
          repairProgramName: option.title,
        })
    },
    selectWorkstation(v, option) {
      // 选择工位
      if (option)
        this.form.setFieldsValue({
          workstationName: option.title,
        })
    },
    selectLocationName(v, option) {
      // 选择安装部位
      if (option)
        this.form.setFieldsValue({
          locationName: option.title,
        })
    },
    add() {
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      this.visible = true
      this.model = Object.assign({}, record)
      if (record.id) {
        this.isEdit = true
        this.isSelectData = true
        this.$nextTick(() => {
          this.materialTypeId = this.model.materialTypeId
          this.form.setFieldsValue({
            needAmount: this.model.needAmount,
            code: this.model.code,
            lineId: this.model.lineId,
            name: this.model.name,
            repairProgramId: this.model.repairProgramId,
            unit: this.model.unit,
            workstationId: this.model.workstationId,
            groupId: this.model.groupId,
            materialTypeCategory: this.model.materialTypeCategory,
            locationName: this.model.locationName,
            spec: this.model.spec,
            remark: this.model.remark,
          })
        })
      }
    },
    // 确定
    handleOk() {
      const that = this

      that.form.validateFields((err, values) => {
        // console.log(values)
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values, {
            materialTypeId: this.materialTypeId,
            location: this.model.location,
          })
          let obj
          if (!that.model.id) {
            obj = saveWillChangeItem(formData)
          } else {
            obj = editWillChangeItem(formData)
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
      this.isSelectData = false
      this.isEdit = false
      this.visible = false
      this.defaultChecked = ''
    },
  },
}
</script>