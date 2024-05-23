<template>
  <a-modal
    :title="title"
    :width="900"
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
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="厂商名称">
              <a-input placeholder="请输入厂商名称" v-decorator.trim="[ 'name', validatorRules.name]" />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="厂商简称">
              <a-input placeholder="请输入厂商简称" v-decorator.trim="[ 'shortName', validatorRules.shortName]" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="厂商类别">
<!--              <j-dict-select-tag v-decorator.trim="[ 'category', validatorRules.category]" :triggerChange="true"
                                 placeholder="请选择厂商类别" dictCode="bu_vendor_category"
                                  @change="handleCategory"/>-->
              <a-input value="部件"  disabled></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="厂商类型">
              <j-dict-select-tag v-decorator.trim="[ 'bizType', validatorRules.bizType]" :triggerChange="true"
                                 placeholder="请选择厂商类型" dictCode="bu_bizType" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属修程">
              <j-dict-select-tag
                v-decorator.trim="[ 'repairProgramId', validatorRules.repairProgramId]" :triggerChange="true"
                placeholder="请选择"
                dictCode="bu_repair_program,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属线路">
              <line-select-list v-decorator.trim="[ 'lineId', validatorRules.lineId]"></line-select-list>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="联系人员">
              <a-input placeholder="请输入联系人员" v-decorator.trim="[ 'contactName',validatorRules.contactName]" />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="联系电话">
              <a-input placeholder="请输入联系电话" v-decorator.trim="[ 'contactPhone',validatorRules.contactPhone]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="邮箱地址">
              <a-input placeholder="请输入邮箱地址" v-decorator.trim="[ 'email',validatorRules.email]" />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="地址">
              <a-input placeholder="请输入地址" v-decorator.trim="[ 'address',validatorRules.address]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="评分">
              <a-input-number style="width: 100%" :min="0" :max="100" v-decorator.trim="[ 'appraise']" />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
<!--            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联车辆" v-if="category=='1'">
              <j-dict-select-tag v-model="objTypeId" :triggerChange="true"
                                 placeholder="请选择" dictCode="bu_train_info,train_no,id" />
            </a-form-item>-->
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联设备">
              <a-select
                placeholder="请选择"
                :open="false"
                :showArrow="true"
                v-decorator.trim="['objTypeName']"
                @focus="openModal"
                ref="mySelect"
              >
                <div slot="suffixIcon">
                  <a-icon
                    v-if="objTypeName"
                    @click.stop="clearValue"
                    type="close-circle"
                    style="padding-right: 3px"
                  />
                  <a-icon v-else type="ellipsis" />
                </div>
              </a-select>
            </a-form-item>

<!--            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联物资" v-if="category=='3'">
              <a-select
                placeholder="请选择"
                :open="false"
                :showArrow="true"
                v-decorator.trim="['objTypeName']"
                @focus="openModal"
                ref="mySelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>-->
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否有效">
              <a-switch v-model="status" @change="changeStatus" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item label="备注" :labelCol="rLabelCol" :wrapperCol="rWrapperCol">
              <a-textarea v-decorator="[ 'remarks',validatorRules.remarks]" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
    <train-asset-type
      ref="selectModal"
      :multiple="false"
      :trainTypeId="''"
      @ok="addTarget"
    ></train-asset-type>
  </a-modal>

</template>

<script>
import JTreeSelect from '@/components/jeecg/JTreeSelect'
import { addSupplier, editSupplier } from '@/api/tirosOutsourceApi'
import store from '@/store'
import { isEmail, isMobile, isPhone } from '@/utils/validate'
import { everythingIsEmpty } from '@/utils/util'
import TrainAssetType from '@views/tiros/common/selectModules/TrainAssetTypeByTrainType'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'


export default {
  name: 'SupplierItemModal',
  components: { JTreeSelect,TrainAssetType,LineSelectList},
  props: ['assetId'],
  data () {
    return {
      condition: {
        train_type_Id: store.getters.userLines ? store.getters.userLines[0].trainTypeId : ''
      },
      treeData: [],
      title: '操作',
      visible: false,
      model: {},
      objTypeId:'',
      category:2,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 }
      },
      rLabelCol: {
        xs: { span: 24 },
        sm: { span: 3 }
      },
      rWrapperCol: {
        xs: { span: 24 },
        sm: { span: 21 }
      },
      status: true,
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        name: { rules: [{ required: true, message: '请输入厂商名称!' }, { max: 32, message: '输入长度不能超过32字符!' }] },
        shortName: { rules: [{ max: 32, message: '输入长度不能超过32字符!' }] },
        repairProgramId:{ rules: [{ required: true, message: '请选择所属修程!' }] },
        lineId:{ rules: [{ required: true, message: '请选择所属线路!' }]},
        bizType: { rules: [{ required: true, message: '请输选择厂商类型!'}] },
        contactName: { rules: [{ max: 32, message: '输入长度不能超过32字符!' }] },
        contactPhone: { rules: [{ max: 16, message: '输入长度不能超过16字符!' }, { validator: this.checkPhone }] },
        email: { rules: [{ max: 32, message: '输入长度不能超过32字符!' }, { validator: this.checkEmail }] },
        address: { rules: [{ max: 64, message: '输入长度不能超过64字符!' }] },
        remarks: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] }
      },
      objTypeName:''

    }
  },
  created () {
  },
  methods: {
    checkPhone (rule, value, callback) {
      if (!everythingIsEmpty(value)) {
        if (!(isMobile(value) || isPhone(value))) {
          callback('请输入正确的号码！')
        } else {
          callback()
        }
      } else {
        callback()
      }

    },
    checkEmail (rule, value, callback) {
      if (!everythingIsEmpty(value)) {
        if (!isEmail(value)) {
          callback('请输入正确的邮箱！')
        } else {
          callback()
        }
      } else {
        callback()
      }
    },
    handleCategory(value){
      this.category=value
    },
    clearValue() {
      this.form.setFieldsValue({
        objTypeName:''
      })
      this.objTypeId =''
      this.objTypeName =''
    },
    openModal () {
      this.$refs.selectModal.showModal()
      this.$refs.mySelect.blur()
    },
    addTarget (data) {
      if (!everythingIsEmpty(data)) {
        this.objTypeId = data[0].id
        this.objTypeName = data[0].name
        this.form.setFieldsValue({
          objTypeName: data[0].name
        })
      }
    },
    changeStatus (checked) {
      this.status = checked
    },
    add () {
      this.edit({})
    },
    edit (record) {
      this.form.resetFields()
      if (record.id) {
        this.status = !!(record['status'] && record['status'] === 1)
        this.objTypeId=record.objTypeId
        this.category=record.category+''
        this.objTypeName=record.objTypeName
      }else{
        this.objTypeId=''
        this.category=''
      }

      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          shortName: this.model.shortName,
          name: this.model.name,
          contactName: this.model.contactName,
          contactPhone: this.model.contactPhone,
          email: this.model.email,
          address: this.model.address,
          remarks: this.model.remarks,
          bizType: this.model.bizType,
          appraise: this.model.appraise,
          objTypeName:this.model.objTypeName,
          lineId:this.model.lineId,
          repairProgramId:this.model.repairProgramId
        })
      })
      if(this.category=='1') {
        this.$nextTick(() => {
          this.form.setFieldsValue({ objTypeId: this.model.objTypeId ? this.model.objTypeId : this.assetId })
        })
      }
    },
    // 确定
    handleOk () {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values, { status: that.status ? 1 : 0 ,objTypeId:this.objTypeId,category:2})
          let obj
          if (!that.model.id) {
            obj = addSupplier(formData)
          } else {
            obj = editSupplier(formData)
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
    }
  }
}
</script>