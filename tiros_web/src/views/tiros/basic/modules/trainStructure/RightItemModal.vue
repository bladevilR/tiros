<template>
  <a-drawer
    :title="title"
    :width="800"
    :maskClosable="true"
    :closable="true"
    :visible="visible"
    @close="handleCancel"
  >
    <a-form :form="form">
      <a-row style="width: 100%;">
        <a-col :span="24/2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="上级结构">
            <a-select
              placeholder="请选择上级结构"
              :open="false"
              :showArrow="true"
              v-decorator.trim="['parentName', validatorRules.parentName]"
              @focus="openParentModal"
              ref="myParentSelect"
              :disabled="true"
            >
              <a-icon slot="suffixIcon" type="ellipsis" />
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="24/2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="设备类型">
            <a-select
              placeholder="请选择设备类型"
              :open="false"
              :showArrow="true"
              v-decorator.trim="['assetTypeName', validatorRules.assetTypeName]"
              @focus="openModal"
              ref="mySelect"
            >
              <a-icon slot="suffixIcon" type="ellipsis" />
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row style="width: 100%;">
        <a-col :span="24/2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属类型">
            <j-dict-select-tag
              :triggerChange="true"
              v-decorator.trim="[ 'structType', validatorRules.structType ]"
              placeholder="请选择"
              dictCode="bu_train_asset_type"
            />
          </a-form-item>
        </a-col>
        <a-col :span="24/2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结构编码">
            <a-input :maxLength="33" placeholder="结构编码" v-decorator.trim="[ 'code', validatorRules.code]" />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row style="width: 100%;">
        <a-col :span="24/2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结构名称">
            <a-input :maxLength="33" placeholder="结构名称" v-decorator.trim="[ 'name', validatorRules.name]" />
          </a-form-item>
        </a-col>
        <a-col :span="24/2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="短名称">
            <a-input :maxLength="33" placeholder="短名称" v-decorator.trim="[ 'shortName', validatorRules.shortName ]" />
          </a-form-item>
        </a-col>
        <a-col :span="24/2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="排序">
            <a-input :maxLength="17" placeholder="排序" v-decorator.trim="[ 'sortNo', validatorRules.sortNo]" />
          </a-form-item>
        </a-col>
        <a-col :span="24/2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属位置">
            <!-- <j-dict-select-tag
               :triggerChange="true"
               v-decorator.trim="[ 'placeId', validatorRules.placeId]"
               placeholder="请选择"
               dictCode="bu_train_place,place_name,id"
             />-->
            <a-input :maxLength="33" placeholder="请输入内容" v-decorator.trim="['placeId', validatorRules.placeId]" />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row style="width: 100%;">
        <a-col :span="24">
          <a-form-item :labelCol="labelColX1" :wrapperCol="wrapperColX1" label="位置描述">
            <a-input
              :maxLength="65"
              placeholder="请输入内容"
              v-decorator.trim="[ 'placeDesc', validatorRules.placeDesc]"
            />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row style="width: 100%">
        <a-col :span="24">
          <a-col :span="24/3">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="是否虚拟">
              <a-switch v-model="switchChecked.subjunctive" @change="changeStatusS" />
            </a-form-item>
          </a-col>
          <a-col :span="24/3">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="是否周转件">
              <a-switch v-model="switchChecked.turnover" @change="changeStatusT" />
            </a-form-item>
          </a-col>
          <a-col :span="24 / 3">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="是否有效">
              <a-switch v-model="switchChecked.status" @change="changeStatus" />
            </a-form-item>
          </a-col>
        </a-col>
      </a-row>
      <a-row style="width: 100%;">
        <a-row style="width: 100%;">
          <a-col :span="24">
            <a-form-item :labelCol="labelColX1" :wrapperCol="wrapperColX1" label="备注">
              <a-textarea :maxLength="201" v-decorator="[ 'remark', validatorRules.remark]"></a-textarea>
            </a-form-item>
          </a-col>
        </a-row>

      </a-row>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃编辑？" @confirm="handleCancel" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleOk" type="primary" :loading="confirmLoading">提交</a-button>
    </div>
    <train-asset-type
      ref="selectModal"
      :multiple="false"
      :trainTypeId="(trainTypeId!==''&&trainTypeId!=null)?trainTypeId:parent.trainTypeId"
      @ok="selectAssetType"
    ></train-asset-type>
    <select-parent
      ref="selectParentModal"
      :multiple="false"
      :trainStructId="trainStructId"
      @ok="selectParent"
    ></select-parent>
  </a-drawer>
</template>

<script>
import { addTrainStructDetail, editTrainStructDetail } from '@/api/tirosApi'
import TrainAssetType from '@views/tiros/common/selectModules/TrainAssetType'
import SelectParent from '@views/tiros/basic/modules/trainStructure/SelectParent'
import { duplicateCheck } from '@api/api'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'RightItemModal',
  props: ['trainStructId', 'trainTypeId', 'parent'],
  components: { TrainAssetType, SelectParent },
  data () {
    return {
      value: 1,
      title: '操作',
      visible: false,
      trainType: '',
      trainStruct: '',
      structType_dictText: '',
      model: {},
      switchChecked: {
        turnover: false,
        subjunctive: false,
        status: false
      },
      sortNo: '',
      parentName: '',
      parentId: '',
      assetTypeId: '',
      hasLabel: false,
      typeTreeNode: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 12 }
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 12 }
      },
      labelColX1: {
        xs: { span: 24 },
        sm: { span: 3 }
      },
      wrapperColX1: {
        xs: { span: 24 },
        sm: { span: 21 }
      },
      labelColX2: {
        xs: { span: 24 },
        sm: { span: 12 }
      },
      wrapperColX2: {
        xs: { span: 24 },
        sm: { span: 12 }
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        code: {
          rules: [{ required: true, message: '请输入结构编码!' }, { validator: this.validateCode }, {
            max: 32,
            message: '输入长度不能超过32字符!'
          }]
        },
        name: { rules: [{ required: true, message: '请输入结构名称!' }, { max: 32, message: '输入长度不能超过32字符!' }] },
        shortName: { rules: [{ required: true, message: '请输入短名称!' }, { max: 32, message: '输入长度不能超过32字符!' }] },
        sortNo: { rules: [{ max: 16, message: '输入长度不能超过16字符!' }] },
        structType: { rules: [{ required: true, message: '请选择所属类型!' }] },
        assetTypeName: { rules: [{ required: true, message: '请选择设备类型!' }] },
        placeId: { rules: [{ max: 32, message: '输入长度不能超过32字符!' }] },
        placeDesc: { rules: [{ max: 64, message: '输入长度不能超过64字符!' }] },
        remark: { rules: [{ max: 200, message: '输入长度不能超过200字符!' }] },
        parentName: { rules: [] }
      }
    }
  },
  watch: {
    trainStructId: {
      immediate: true,
      handler (id) {
        this.trainStruct = id
      }
    }
  },
  methods: {
    validateCode (rule, value, callback) {
      let params = {
        tableName: 'bu_train_structure_detail',
        fieldName: 'code',
        fieldVal: value,
        dataId: this.model.id,
        filterFields: [{ name: 'train_struct_id', val: this.trainStruct }]
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

    openModal () {
      this.$refs.selectModal.showModal()
      this.$refs.mySelect.blur()
    },
    selectAssetType (data) {

      this.$nextTick(() => {
        this.form.setFieldsValue({
          assetTypeName: data[0].name,
          structType: data[0].structType
        })
      })
      this.structType_dictText = data[0].structType_dictText
      this.structType = data[0].structType
      this.assetTypeId = data[0].id
    },
    openParentModal () {
      this.$refs.selectParentModal.showModal()
      this.$refs.myParentSelect.blur()
    },
    selectParent (data) {
      this.form.setFieldsValue({
        parentName: data[0].name
      })
      this.parentId = data[0].id
    },
    changeStatusS (checked) {
      this.switchChecked.subjunctive = checked
    },
    changeStatusT (checked) {
      this.switchChecked.turnover = checked
    },
    changeStatus (checked) {
      this.switchChecked.status = checked
    },

    add () {
      this.switchChecked.turnover = false
      this.switchChecked.subjunctive = false
      this.switchChecked.status = true
      this.edit({})
    },
    edit (record) {
      if (record.id) {
        this.hasLabel = true
        this.assetTypeId = record.assetTypeId
        this.parentId = record.parentId
        this.parentName = record.parentName
        this.trainStruct =record.trainStructId
        for (let key in this.switchChecked) {
          this.switchChecked[key] = !!record[key]
        }
      } else {
        this.assetTypeId = ''
        this.parentId = this.parent.id
        this.parentName = this.parent.name
        if (!everythingIsEmpty(this.parent)) {
          this.trainStruct = this.parent.trainStructId
        } else {
          this.trainStruct = this.trainStructId
        }
      }
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          code: this.model.code,
          name: this.model.name,
          shortName: this.model.shortName,
          placeId: this.model.placeId,
          placeDesc: this.model.placeDesc,
          structType: this.model.structType,
          assetTypeName: this.model.assetTypeName,
          initNum: this.model.initNum,
          // unit: this.model.unit,
          parentName: this.parentName,
          remark: this.model.remark,
          sortNo: this.model.sortNo
        })
      })
    },
    // 确定
    handleOk () {
      const that = this
      // 触发表单验证
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let transCheck = {}
          for (let key in that.switchChecked) {
            that.$set(transCheck, key, that.switchChecked[key] ? 1 : 0)
          }
          let formData = Object.assign(
            that.model,
            values,
            transCheck,
            {
              trainStructId: this.trainStruct,
              assetTypeId: this.assetTypeId,
              parentId: this.parentId,
              structType_dictText: this.structType_dictText
            },
            that.selectTargetId
          )
          let obj
          if (!that.model.id) {
            obj = addTrainStructDetail(formData)
          } else {
            obj = editTrainStructDetail(formData)
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
    },
    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
    }
  }
}
</script>
<style scoped>
.drawer-bootom-button {
  position: absolute;
  bottom: 0;
  width: 100%;
  border-top: 1px solid #e8e8e8;
  padding: 10px 16px;
  text-align: right;
  left: 0;
  background: #fff;
  border-radius: 0 0 2px 2px;
}
</style>