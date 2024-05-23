<template>
  <a-modal
    :title="title"
    :width="'32%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :bodyStyle="{ minHeight: '50px' }"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="选择年份">
              <a-date-picker
                style="width: 100%"
                mode="year"
                placeholder="请选择年份"
                format="YYYY"
                v-decorator.trim="['sbYear', validatorRules.sbYear]"
                :open="yearPickShow"
                @panelChange="handlePanelChange"
                @openChange="handleOpenChange"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资类型选择">
              <a-input
                allowClear
                placeholder="请选择"
                :open="false"
                :showArrow="true"
                v-decorator.trim="['materialTypeName', validatorRules.materialTypeName]"
                @focus="openModal"
                ref="mySelect"
              >
                <a-icon slot="suffix" type="ellipsis" />
              </a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物料类别">
              <j-dict-select-tag
                triggerChange
                v-decorator.trim="['sbType', validatorRules.sbType]"
                placeholder="请选择"
                dictCode="bu_material_type"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
              <line-select-list v-decorator.trim="['lineId', validatorRules.lineId]"></line-select-list>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="修程">
              <j-dict-select-tag
                triggerChange
                v-decorator.trim="['repairProgramId', validatorRules.repairProgramId]"
                placeholder="请选择"
                dictCode="bu_repair_program,name,id"
                @change="sumRepairPlanYearDetailAmount"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所需数量">
              <a-input-number
                style="width: 100%"
                :min="1"
                placeholder="请输入所需数量"
                v-decorator.trim="['sbAmount', validatorRules.sbAmount]"
              />
              所需数量= 单列所需量 * 年度维修车辆数
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
    <material-list ref="MaterialModalForm" :multiple="false" @ok="addTarget"></material-list>
  </a-modal>
</template>

<script>
import moment from 'moment'
import 'moment/locale/zh-cn'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { addMaterialPlanItem, editMaterialPlanItem, sumRepairPlanYearDetailAmount } from '@/api/tirosMaterialApi'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'
export default {
  name: 'MaterialPlanItemModal.vue',
  components: { LineSelectList, MaterialList },
  data() {
    return {
      title: '操作',
      visible: false,
      yearPickShow: false,
      model: {},
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
      rowNum: 0,
      trainTotal: 0,
      validatorRules: {
        sbYear: { rules: [{ required: true, message: '请选择年份!' }] },
        materialTypeName: { rules: [{ required: true, message: '请选择物资类型!' }] },
        sbAmount: { rules: [{ required: true, message: '请输入所需数量!' }] },
        sbType: { rules: [{ required: true, message: '请选择物料类别!' }] },
        lineId: { rules: [{ required: true, message: '请选择线路!' }] },
        repairProgramId: { rules: [{ required: true, message: '请选择修程!' }] },
      },
    }
  },
  watch: {
    rowNum(val) {
      console.log(val)
      if (val && this.trainTotal) {
        this.form.setFieldsValue({
          sbAmount: val * this.trainTotal,
        })
      }
    },
    trainTotal(val) {
      console.log(val)
      if (val && this.rowNum) {
        this.form.setFieldsValue({
          sbAmount: val * this.rowNum,
        })
      }
    },
  },
  created() {},
  methods: {
    // 弹出日历和关闭日历的回调
    handleOpenChange(status) {
      this.yearPickShow = status
    },
    // 得到年份选择器的值
    handlePanelChange(value) {
      this.form.setFieldsValue({
        sbYear: moment(value).format('YYYY'),
      })
      this.sumRepairPlanYearDetailAmount()
      this.yearPickShow = false
    },
    openModal() {
      this.$refs.MaterialModalForm.showModal()
      this.$refs.mySelect.blur()
    },
    addTarget(data) {
      if (data && data.length === 1 && data[0] != null) {
        this.form.setFieldsValue({
          materialTypeName: data[0].name,
          // sbAmount: data[0].num,
        })
        this.rowNum = data[0].num
        this.materialTypeId = data[0].id
      }
    },
    sumRepairPlanYearDetailAmount(v) {
      if (v) {
        this.form.setFieldsValue({
          repairProgramId: v,
        })
        this.$forceUpdate()
      }
      if (this.form.getFieldsValue().sbYear && this.form.getFieldsValue().repairProgramId) {
        sumRepairPlanYearDetailAmount({
          year: this.form.getFieldsValue().sbYear,
          programId: this.form.getFieldsValue().repairProgramId,
        }).then((res) => {
          if (res.success) {
              this.trainTotal = res.result
          }
        })
      }
    },
    add() {
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      this.visible = true
      this.model = Object.assign({}, record)
      if (record.id) {
        this.$nextTick(() => {
          this.form.setFieldsValue({
            sbYear: this.model.sbYear.toString(),
            materialTypeName: this.model.name,
            sbAmount: this.model.sbAmount,
          })
        })
      } else {
        this.$nextTick(() => {
          this.form.setFieldsValue({
            sbYear: moment(new Date()).format('YYYY'),
          })
        })
      }
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values, {
            sbYear: moment(values.sbYear).format('YYYY'),
          })
          formData.materialTypeId = this.materialTypeId
          let obj
          if (!that.model.id) {
            obj = addMaterialPlanItem(formData)
          } else {
            obj = editMaterialPlanItem(formData)
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
  },
}
</script>