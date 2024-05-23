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
    <a-form :form="form" ref="flex" labelAlign="left" style="padding: 15px 24px">
      <a-form-item :labelCol="{ span: 3 }" :wrapperCol="{ span: 21 }" label="模板选择" :required='true'>
        <a-select
          placeholder="请选择表单"
          :maxTagTextLength="10"
          v-model="queryParam.formName"
          :open="false"
          style="width: 100%"
          @change="onFormSelectChange"
          @dropdownVisibleChange="openFormSelect()"
          optionFilterProp="formName"
        >
          <a-icon slot="suffixIcon" type="ellipsis" @click="openFormSelect()" />
        </a-select>
      </a-form-item>
      <a-form-item label="设备选择" :labelCol="{ span: 3 }" :wrapperCol="{ span: 21 }" :required='true'>
        <a-select
          v-model="queryParam.assetTypeName"
          placeholder="请选择设备"
          :open="false"
          style="width: 100%"
          @dropdownVisibleChange="openAssetSelectModal()"
          @change="onDeviceSelect"
          allowClear
        >
          <a-icon slot="suffixIcon" type="ellipsis" />
        </a-select>
      </a-form-item>
      <a-form-item label="表单名称" :labelCol="{ span: 3 }" :wrapperCol="{ span: 21 }">
        <a-input v-model="queryParam.title" placeholder="请输入表单名称" allowClear></a-input>
      </a-form-item>
    </a-form>
    <FormsList ref="multipleSelect" :multiple="false" @ok="onFormSelect"></FormsList>
<!--    <CarDeviceSelect
      ref="trainAssetSelectModal"
      :train-no="trainNo"
      :multiple="false"
      :canSelectType="[1, 2, 3, 4, 5]"
      @ok="onTrainAssetSelect"
    />-->
    <CarDeviceSelectNew
      ref="trainAssetSelectModal"
      :trainNo="trainNo"
      :lineId="lineId"
      :multiple="false"
      :canSelectType="[2,3,4]"
      @ok="onTrainAssetSelect"
      @cancel="onCancelTrainAssetSelect"
    >
    </CarDeviceSelectNew>
  </a-modal>
</template>

<script>
import FormsList from '@views/tiros/common/selectModules/FormsList'
import CarDeviceSelect from '@views/tiros/common/selectModules/CarDeviceSelect'
import { addFormEntityList } from '@/api/tirosApi'
import CarDeviceSelectNew from '@views/tiros/common/selectModules/CarDeviceSelectNew'

export default {
  // props: ['trainNo'],
  props: {
    trainNo: {
      type: String,
      default: '',
    },
    lineId: {
      type: String,
      default: '',
    },
    repairId:{
      type: [Number, String],
      default: null
    },
    checkList: {
      type: Array,
      default: () => {
        return []
      },
    },
    operator:{
      type: [String,Number],
      default: ''
    },
  },
  name: 'TaskFormModel',
  components: { FormsList, CarDeviceSelect,CarDeviceSelectNew },
  data() {
    return {
      title: '添加',
      detailIds: '',
      checkType: -1,
      value: 1,
      status: 1,
      confirmLoading: false,
      switchCheck: true,
      visible: false,
      model: {},
      // selectFormList: [],
      // selectDevice: '',
      form: this.$form.createForm(this),
      // dataFormList: [],
      queryParam: {
        assetTypeId: '',
        assetTypeName: '',
        code: '',
        copies: '',
        formName: '',
        title: '',
        formType: '',
        fromBy: '',
        id: '',
        objId: '',
        planId: '',
        remark: '',
        type: '',
        workRecordType: null
      },
    }
  },
  methods: {
    showModal(planId) {
      this.queryParam.planId = planId
      this.queryParam.assetTypeId = ''
      this.queryParam.assetTypeName = ''
      this.queryParam.code = ''
      this.queryParam.copies = ''
      this.queryParam.formName = ''
      this.queryParam.title = ''
      this.queryParam.formType = ''
      this.queryParam.fromBy = ''
      this.queryParam.id = ''
      this.queryParam.objId = ''
      this.queryParam.remark = ''
      this.queryParam.type = ''
      this.visible = true
    },
    // 确定
    handleOk() {
      if (!this.queryParam.formName) {
        this.$message.warn('请选择表单')
        return
      }
      if (!this.queryParam.assetTypeId) {
        this.$message.warn('请选择设备')
        return
      }
      if (this.checkList.find((e) => e.objId === this.queryParam.objId)) {
        this.$message.warn(`${this.checkList.find((e) => e.objId === this.queryParam.objId).formName}已经添加了`)
        return
      }
      addFormEntityList(this.queryParam).then((res) => {
        if (res.success) {
          this.$message.success('执行成功')
          this.$emit('ok')
          this.visible = false
        } else {
          this.$message.error(res.message)
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
    openFormSelect() {
      console.log(this.operator)
      this.$refs.multipleSelect.showModal(this.operator === 1 && {
        lineId:this.lineId,
        repairProgramId: this.repairId,
      })
    },
    onFormSelect(data) {
      if (data.forms.length) {
        let item = data.forms[0]
        this.queryParam.code = item.code
        this.queryParam.formName = item.title
        this.queryParam.title = item.title
        this.queryParam.formType = item.formType
        this.queryParam.workRecordType = item.workRecordType
        this.queryParam.objId = item.id
        this.queryParam.fromBy = 2
      }
    },
    onFormSelectChange() {
      // this.dataFormList = this.dataFormList.filter((e) =>
      //   this.selectFormList.find((r) => r === e.formName) ? true : false
      // )
      this.queryParam.code = ''
      this.queryParam.formName = ''
      this.queryParam.formType = ''
      this.queryParam.workRecordType = null
      this.queryParam.type = ''
      this.queryParam.objId = ''
    },
   /* openDeviceSelect() {
      this.$refs.trainAssetSelectModal.showModal()
    },*/
   /* // 设备选择回调
    onTrainAssetSelect(data) {
      if (data.length) {
        const item = data[0]
        this.queryParam.assetTypeId = item.id
        this.queryParam.assetTypeName = item.assetName
      }
    },*/
    onDeviceSelect(value) {
      if (!value) {
        this.queryParam.assetTypeId = ''
        this.queryParam.assetTypeName = ''
        this.queryParam.trainStructId = ''
        this.queryParam.trainStructName = ''
        this.queryParam.assetId = ''
        this.queryParam.assetName=''
      }
    },
    // 弹出设备选择界面
    openAssetSelectModal() {
      this.$refs.trainAssetSelectModal.showModal()
    },
    // 设备选择回调
    onTrainAssetSelect(data) {
      if (data.length) {
        const item = data[0]
        this.queryParam.assetTypeId = item.id
        this.queryParam.assetTypeName = item.assetName
        this.queryParam.trainStructId = item.id
        this.queryParam.trainStructName = item.assetName
        this.queryParam.assetId = item.id
        this.queryParam.assetName= item.assetName
       /* this.trainStructId = item.id
        this.$nextTick(() => {
          this.form.setFieldsValue({
            trainStructName: item.assetName,
          })
        })*/
      }
    },
    // 设备选择取消回调
    onCancelTrainAssetSelect() {},
  },
}
</script>
<style lang="less" scoped>
.QRcode {
  width: 150px;
  height: 150px;
}
</style>
