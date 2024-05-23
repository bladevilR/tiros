<template>
  <a-drawer :title="title" :width="800" :maskClosable="false" :closable="true" :visible="visible" @close="handleCancel">
    <a-form :form="form">
      <a-row style="width: 100%">
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结构编码">
            <a-input :maxLength="33" placeholder="结构编码" v-decorator.trim="['code', validatorRules.code]"/>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结构名称">
            <a-input :maxLength="33" placeholder="结构名称" v-decorator.trim="['name', validatorRules.name]"/>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="短名称">
            <a-input :maxLength="33" placeholder="结构名称" v-decorator.trim="['shortName',validatorRules.shortName]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row style="width: 100%">
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="上级结构">
            <a-select
              placeholder="请选择"
              :open="false"
              :showArrow="true"
              v-decorator.trim="['parentName', validatorRules.parentName]"
              @focus="openModal"
              ref="mySelect"
            >
              <a-icon slot="suffixIcon" type="ellipsis"/>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="排序">
            <a-input placeholder="排序" :maxLength="17" v-decorator.trim="['sortNum', validatorRules.sortNum]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row style="width: 100%">
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="初始数量">
            <a-input-number
              :max="9999"
              id="inputNumber"
              v-decorator.trim="['initNum', validatorRules.initNum ]"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单位">
            <a-input :maxLength="17" placeholder="请输入内容" v-decorator.trim="['unit', validatorRules.unit]"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row style="width: 100%">
        <a-col :span="24 / 4">
          <a-form-item :labelCol="labelColX2" :wrapperCol="wrapperColX2" label="是否虚拟">
            <a-switch v-model="switchChecked.subjunctive" @change="changeSubjunctive"/>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 4">
          <a-form-item :labelCol="labelColX2" :wrapperCol="wrapperColX2" label="是否BOM">
            <a-switch v-model="switchChecked.bom" @change="changeBom"/>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 4">
          <a-form-item :labelCol="labelColX2" :wrapperCol="wrapperColX2" label="是否周转件">
            <a-switch v-model="switchChecked.turnover" @change="changeTurnover"/>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 4">
          <a-form-item :labelCol="labelColX2" :wrapperCol="wrapperColX2" label="是否启用">
            <a-switch v-model="switchChecked.status" @change="changeStatus"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row style="width: 100%">
        <a-col :span="24">
          <a-form-item :labelCol="labelColX1" :wrapperCol="wrapperColX1" label="备注">
            <a-textarea :maxLength="201" v-decorator="['remark',validatorRules.remark]"></a-textarea>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <div class="drawer-bootom-button">
      <!--      <a-popconfirm title="确定放弃编辑？" @confirm="handleCancel" okText="确定" cancelText="取消">
              <a-button style="margin-right: 0.8rem">取消</a-button>
            </a-popconfirm>-->
      <a-space>
        <a-button @click="handleCancel">取消</a-button>
        <a-button @click="handleOk" type="primary" :loading="confirmLoading">提交</a-button>
      </a-space>
    </div>
    <train-asset-type
      ref="selectModal"
      :multiple="false"
      :trainTypeId="trainTypeId"
      @ok="selectParent"
    ></train-asset-type>
  </a-drawer>
</template>

<script>
import TrainAssetType from '../../../common/selectModules/TrainAssetType'
import { addTrainTypeSys, editTrainTypeSys } from '@/api/tirosApi'
import { duplicateCheck } from '@/api/api'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'TrainTypeSysModal',
  props: ['parent', 'trainTypeId'],
  components: { TrainAssetType },
  data() {
    return {
      value: 1,
      title: '操作',
      visible: false,
      sortNum: '',
      parentName: '',
      parentId: '',
      model: {},
      switchChecked: {
        subjunctive: false,
        bom: false,
        turnover: false,
        status: true
      },
      selectedName: '',
      selectTreeNode: [],
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
        code: {
          rules: [{ required: true, message: '请输入结构编码!' }, { validator: this.validateCode }, {
            max: 32,
            message: '输入长度不能超过32字符!'
          }]
        },
        name: { rules: [{ required: true, message: '请输入结构名称!' }, { max: 32, message: '输入长度不能超过32字符!' }] },
        shortName: { rules: [{ required: true, message: '请输入短名称!' }, { max: 32, message: '输入长度不能超过32字符!' }] },
        parentName: { rules: [{ required: true, message: '请选择上级结构!' }] },
        sortNum: { rules: [{ max: 16, message: '输入长度不能超过16字符!' }] },
        initNum: {
          rules: [{ required: true, message: '请输入初始数量!' }], initialValue: 1
        },
        unit: { rules: [{ required: false, message: '请选输入单位!' }, { max: 16, message: '输入长度不能超过16字符!' }] },
        remark: { rules: [{ max: 200, message: '输入长度不能超过200字符!' }] }
      }
    }
  },
  methods: {
    changeSubjunctive(checked) {
      this.switchChecked.subjunctive = checked
    },
    changeBom(checked) {
      this.switchChecked.bom = checked
    },
    changeTurnover(checked) {
      this.switchChecked.turnover = checked
    },
    changeStatus(checked) {
      this.switchChecked.status = checked
    },
    openModal() {
      this.$refs.selectModal.showModal()
      this.$refs.mySelect.blur()
    },
    validateCode(rule, value, callback) {
      let params = {
        tableName: 'bu_train_asset_type',
        fieldName: 'code',
        fieldVal: value,
        dataId: this.model.id,
        filterFields: [{ name: 'train_type_id', val: this.trainTypeId }]
      }
      if(!everythingIsEmpty(value)) {
        duplicateCheck(params).then((res) => {
          if (res.success) {
            callback()
          } else {
            callback(res.message)
          }
        })
      }else {
        callback()
      }
    },
    selectParent(data) {
      if(!everythingIsEmpty(data)){
        this.form.setFieldsValue({
          parentName: data[0].name
        })
        this.parentId = data[0].id
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
        this.parentId = record.parentId
        this.parentName = record.parentName
        for (var key in this.switchChecked) {
          this.switchChecked[key] = record[key] ? true : false
        }
        this.$nextTick(() => {
          this.form.setFieldsValue({
            code: this.model.code,
            name: this.model.name,
            shortName: this.model.shortName,
            initNum: this.model.initNum,
            unit: this.model.unit,
            parentName: this.parentName,
            remark: this.model.remark,
            sortNum: this.model.sortNum
          })
        })
      } else {
        this.parentId = this.parent.sysId
        this.parentName = this.parent.sysName
        for (var key in this.switchChecked) {
          if (key == 'bom' || key == 'status') {
            this.switchChecked[key] = true
          } else {
            this.switchChecked[key] = false
          }
        }
        this.$nextTick(() => {
          this.form.setFieldsValue({
            parentName: this.parentName
          })
        })
      }
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
            trainTypeId: this.trainTypeId,
            parentId: this.parentId
          })

          let obj
          if (!that.model.id) {
            obj = addTrainTypeSys(formData)
          } else {
            obj = editTrainTypeSys(formData)
          }
          obj
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
                that.close()
                that.visible = false
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