<template>
  <a-drawer :title="title" :width="800" :maskClosable="true" :closable="true" :visible="visible" @close="handleCancel">
    <a-form :form="form">
      <a-row style="width: 100%">
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="上级结构">
            <a-select
              placeholder="请选择上级结构"
              :open="false"
              :showArrow="true"
              v-decorator.trim="['parentName', validatorRules.parentName]"
              @focus="openParentModal"
              ref="myParentSelect"
            >
              <a-icon slot="suffixIcon" type="ellipsis"/>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="设备类型">
            <a-select
              placeholder="请选择设备类型"
              :open="false"
              :showArrow="true"
              v-decorator.trim="['assetTypeName', validatorRules.assetTypeName]"
              @focus="openModal"
              ref="mySelect"
            >
              <a-icon slot="suffixIcon" type="ellipsis"/>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row style="width: 100%">
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
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结构编码">
            <a-input placeholder="结构编码" v-decorator.trim="['assetNo']" disabled/>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结构名称">
            <a-input placeholder="结构名称" v-decorator.trim="['assetName', validatorRules.assetName]"/>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="短名称">
            <a-input placeholder="短名称" v-decorator.trim="['shortName',validatorRules.shortName]"/>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="排序">
            <a-input placeholder="排序" v-decorator.trim="['sortNo', validatorRules.sortNo]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row style="width: 100%">
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属位置">
            <a-input placeholder="请输入内容" v-decorator.trim="['placeId', validatorRules.placeId]"/>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="位置描述">
            <a-input placeholder="请输入内容" v-decorator.trim="['placeDesc', validatorRules.placeDesc]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row style="width: 100%">
        <a-col :span="24 / 3">
          <a-form-item :labelCol="labelColX2" :wrapperCol="wrapperColX2" label="是否虚拟">
            <a-switch v-model="switchChecked.subjunctive" @change="changeSubjunctive"/>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 3">
          <a-form-item :labelCol="labelColX2" :wrapperCol="wrapperColX2" label="是否周转件">
            <a-switch v-model="switchChecked.turnover" @change="changeTurnover"/>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 3">
          <a-form-item :labelCol="labelColX2" :wrapperCol="wrapperColX2" label="是否有效">
            <a-switch v-model="switchChecked.status" @change="changeStatus"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row style="width: 100%">
        <a-col :span="24">
          <a-form-item :labelCol="labelColX1" :wrapperCol="wrapperColX1" label="备注">
            <a-textarea v-decorator="['remark',validatorRules.remark]"></a-textarea>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃编辑？" @confirm="handleCancel" okText="确定" cancelText="取消">
        <a-button style="margin-right: 0.8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleOk" type="primary">提交</a-button>
    </div>
    <train-asset-type
      ref="selectModal"
      :multiple="false"
      :trainTypeId="trainTypeId"
      @ok="selectAssetType"
    ></train-asset-type>
    <select-parent ref="selectParentModal" :multiple="false" :trainId="trainId" @ok="selectParent"></select-parent>
  </a-drawer>
</template>

<script>
import TrainAssetType from '../../../common/selectModules/TrainAssetType'
import SelectParent from './SelectParent'
import { addTrainAsset, editTrainAsset } from '@/api/tirosApi'

export default {
  name: 'TrainSysItemModal',
  props: ['trainTypeId', 'trainId', 'parent'],
  components: { TrainAssetType, SelectParent },
  data() {
    return {
      value: 1,
      title: '操作',
      visible: false,
      model: {},
      structType_dictText: '',
      switchChecked: {
        subjunctive: false,
        turnover: false,
        status: false
      },
      // selectTargetName: {
      //   assetTypeName: '',
      //   parentName: '',
      // },
      parentId: '',
      parentName: '',
      assetTypeId: '',

      // selectTargetId: {
      //   assetTypeId: '',
      //   parentId: '',
      // },
      parentTreeNode: [],
      typeTreeNode: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
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
        assetNo: { rules: [{ required: true, message: '请输入结构编码!' }, { max: 64, message: '输入长度不能超过64字符!' }] },
        assetName: { rules: [{ required: true, message: '请输入结构名称!' }, { max: 64, message: '输入长度不能超过64字符!' }] },
        sortNo: { rules: [{ max: 16, message: '输入长度不能超过16字符!' }] },
        parentName: { rules: [] },
        structType: { rules: [{ required: true, message: '请选择所属类型!' }] },
        assetTypeName: { rules: [{ required: true, message: '请选择设备类型!' }] },
        placeId: { rules: [{ max: 32, message: '输入长度不能超过32字符!' }] },
        placeDesc: { rules: [{ max: 64, message: '输入长度不能超过64字符!' }] },
        remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] },
        shortName: { rules: [{ required: true, message: '请输入短名称!' },{ max: 32, message: '输入长度不能超过32字符!' }] },
      }
    }
  },

  methods: {

    openModal() {
      this.$refs.selectModal.showModal()
      this.$refs.mySelect.blur()
    },
    selectAssetType(data) {
      console.log(data)
      this.form.setFieldsValue({
        assetTypeName: data[0].name,
        structType: data[0].structType
      })
      this.assetTypeId = data[0].id
      this.structType_dictText = data[0].structType_dictText
    },
    openParentModal() {
      this.$refs.selectParentModal.showModal()
      this.$refs.myParentSelect.blur()
    },
    selectParent(data) {
      console.log(data)
      // this.parentName = data[0].assetName
      this.form.setFieldsValue({
        parentName: data[0].assetName
      })
      this.parentId = data[0].id
    },
    changeSubjunctive(checked) {
      this.switchChecked.subjunctive = checked
    },
    changeTurnover(checked) {
      this.switchChecked.turnover = checked
    },
    changeStatus(checked) {
      this.switchChecked.status = checked
    },
    add() {
      this.edit({})
    },
    edit(record) {
      if (record.id) {
        // this.selectTargetId.assetTypeId = record.assetTypeId
        // this.selectTargetName.assetTypeName = record.assetType
        // this.selectTargetId.parentId = record.parentId
        // this.selectTargetName.parentName = record.parentName
        this.parentId = record.parentId
        this.parentName = record.parentName
        this.assetTypeId = record.assetTypeId
        for (let key in this.switchChecked) {
          this.switchChecked[key] = !!record[key]
        }

      } else {
        this.parentId = this.parent.id
        this.parentName = this.parent.name
        this.assetTypeId = ''
        this.switchChecked['status']=true
      }
      this.form.resetFields()
      this.visible = true
      this.model = Object.assign({}, record)
      this.$nextTick(() => {
        this.form.setFieldsValue({
          assetNo: this.model.assetNo,
          assetName: this.model.assetName,
          shortName: this.model.shortName,
          parentName: this.parentName,
          assetTypeName: this.model.assetTypeName,
          placeId: this.model.placeId,
          placeDesc: this.model.placeDesc,
          remark: this.model.remark,
          structType: this.model.structType,
          sortNo: this.model.sortNo
        })
      })
    },

    // 确定
    handleOk() {
      const that = this
      // 触发表单验证
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let transCheck = {}
          for (var key in that.switchChecked) {
            that.$set(transCheck, key, that.switchChecked[key] ? 1 : 0)
          }
          let formData = Object.assign(that.model, values, transCheck, {
            trainId: this.trainId,
            assetTypeId: this.assetTypeId,
            parentId: this.parentId,
            structType_dictText: this.structType_dictText
          })
          let obj
          if (!that.model.id) {
            obj = addTrainAsset(formData)
          } else {
            obj = editTrainAsset(formData)
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