<template>
  <a-modal
    :title="title"
    :width="1100"
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
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="设备名称">
              <a-input placeholder="请输入"  v-decorator.trim="[ 'name', validatorRules.name]"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资类型">
              <a-select
                v-decorator.trim="[ 'materialName', validatorRules.materialName]"
                placeholder="请选择物资类型"
                :open="false"
                :showArrow="true"
                @focus="openmaterModal"
                ref="mymaterSelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资编码">
              <a-input placeholder="请输入" disabled v-decorator.trim="[ 'code', validatorRules.code]"/>
            </a-form-item>
          </a-col>
        <!-- </a-row>
        <a-row :gutter="24"> -->
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
<!--              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="[ 'lineId', validatorRules.lineId ]"
                dictCode="bu_mtr_line,line_name,line_id"
              />-->
              <line-select-list
                v-decorator="['lineId', validatorRules.lineId]">
              </line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车间">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="[ 'workshopId', validatorRules.workshopId ]"
                dictCode="bu_mtr_workshop,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="状态">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="[ 'status', validatorRules.status ]"
                dictCode="bu_tools_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="资产编码">
              <a-input placeholder="请输入"  v-decorator.trim="[ 'assetCode', validatorRules.assetCode]"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="类别">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="[ 'toolType', validatorRules.toolType ]"
                dictCode="bu_tools_type"
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="生产厂家">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['supplierId', validatorRules.supplierId ]"
                dictCode="bu_base_supplier,name,id"
              />
            </a-form-item>
          </a-col>
        <!-- </a-row>
        <a-row :gutter="24"> -->
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="入场使用日期">
              <a-date-picker style="width:100%" v-decorator.trim="[ 'entraceDate', validatorRules.entraceDate]" />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="使用寿命">
              <a-input placeholder="请输入"  v-decorator.trim="[ 'lifetime', validatorRules.lifetime]"/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="入库日期">
              <a-date-picker style="width:100%" v-decorator.trim="[ 'entryDate', validatorRules.entryDate]" />
            </a-form-item>
          </a-col>
        <!-- </a-row>
        <a-row :gutter="24"> -->
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属工班">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="[ 'groupId', validatorRules.groupId ]"
                :dictCode="dictGroupStr"
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属仓库">
              <!-- 很卡 -->
              <!-- <j-dict-select-tag
                v-decorator.trim="[ 'warehouseId', validatorRules.warehouseId ]"
                dictCode="bu_mtr_warehouse,name,id"
              /> -->
              <j-tree-select
                dict="bu_mtr_warehouse,name,id"
                pidField="parent_id"
                v-decorator.trim="[ 'warehouseId', validatorRules.warehouseId ]"
              >
              </j-tree-select>
            </a-form-item>
          </a-col>
        <!-- </a-row>
        <a-row :gutter="24"> -->
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="下次送检日期">
              <a-date-picker style="width:100%" v-decorator.trim="[ 'nextCheckTime', validatorRules.nextCheckTime]" />
            </a-form-item>
          </a-col>
        <!-- </a-row>
        <a-row :gutter="24"> -->
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="保养周期">
              <a-input-number style="width:100%" placeholder="请输入" :max="999999" v-decorator.trim="[ 'serviceInterval', validatorRules.serviceInterval]" />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item 
              :labelCol="labelCol" 
              :wrapperCol="wrapperCol" 
              label="规格">
              <a-input placeholder="请输入" v-decorator.trim="[ 'model', validatorRules.model]" />
            </a-form-item>
          </a-col>

        </a-row>
      </a-form>
    </a-spin>
<!--    <train-asset-type ref="selectForm" :multiple="false" @ok="selectTrainSys"></train-asset-type>-->
<!--    <user-list ref="UserModalForm" :multiple="true" @ok="addTarget"></user-list>-->
    <material-list ref="materModalForm" :multiple="false" @ok="selectmater"></material-list>
  </a-modal>

</template>

<script>
  import moment from 'moment'
  import { addEquipment, editEquipment } from '@api/tirosMaterialApi'
  import MaterialList from '../../common/selectModules/MaterialList'
  import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
  import JTreeSelect from '@comp/jeecg/JTreeSelect'
  import { duplicateCheck } from '@/api/api'
  import { everythingIsEmpty } from '@/utils/util'

  export default {
    name: 'EquipmentItemModal',
    components: { MaterialList,LineSelectList,JTreeSelect },
    data() {
      return {
        dictGroupStr: 'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
        value: 1,
        title: '操作',
        visible: false,
        materName:'',
        materId:'',
        matercode:'',
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 8 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 14 }
        },

        isClose: false,
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules: {
          name: { rules: [{ required: true, message: '请选择物资名称!' }] },
          code: { rules: [{ required: true, message: '请输入物资编码!' }] },
          assetCode: { rules: [{ required: true, message: '请填写资产编码!' },{ validator: this.validateCode }] },
          supplierId: { rules: [] },
          groupId: { rules: [] },
          entraceDate: { rules: [] },
          lifetime: { rules: [] },
          entryDate: { rules: [] },
          warehouseId: { rules: [] },
          nextCheckTime: { rules: [] },
          toolType: { rules: [{ required: true, message: '请选择类别!' }] },
          status: { rules: [{ required: true, message: '请选择状态!' }] },
          serviceInterval: { rules: [] },
          model: { rules: [{ max:200, message: '输入长度不能超过200个字符!'}] },
          materialName: { rules: [{ required: true, message: '请选择物资类型!' }] },
          lineId: { rules: [{ required: true, message: '请选择线路!' }] },
          workshopId: { rules: [{ required: true, message: '请选择车间!' }] },
        }
      }
    },

    created() {
    },
    methods: {
      validateCode(rule, value, callback) {
        // console.log(rule, value, callback)
        let params = {
          tableName: 'bu_material_tools',
          fieldName: 'asset_code',
          fieldVal: value,
          dataId: this.model.id,
          // filterFields: [{ name: 'train_type_id', val: this.trainTypeId }]
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
      changeClose(checked) {
        this.isClose=checked
      },
      add() {
        this.edit({})
      },
      edit(record) {
        console.log(record)
        if (record.id) {
          this.isClose=record['close']&&record['close']==1?true:false;
        } else {
          this.isClose=false
        }
        // this.$form.resetFields()
        this.model = Object.assign({}, record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue({
            name:this.model.name,
            materialName:this.model.materialName,
            code:this.model.code,
            lineId:this.model.lineId,
            workshopId:this.model.workshopId,
            assetCode:this.model.assetCode,
            supplierId:this.model.supplierId,
            groupId:this.model.groupId,
            entraceDate:moment(this.model.entraceDate || new Date(), 'YYYY-MM-DD'),
            lifetime:this.model.lifetime,
            entryDate:moment(this.model.entryDate || new Date(), 'YYYY-MM-DD'),
            warehouseId:this.model.warehouseId,
            nextCheckTime:moment(this.model.nextCheckTime || new Date(), 'YYYY-MM-DD'),
            toolType:this.model.toolType,
            serviceInterval:this.model.serviceInterval,
            status:this.model.status,
            model:this.model.model,
          })
        })
      },
      openmaterModal() {
        this.$refs.materModalForm.showModal()
        this.$refs.mymaterSelect.blur()
      },
      selectmater(data) {
        console.log(data)
        if (data.length) {
          this.materName = data[0].name
          this.materId = data[0].id
          this.matercode = data[0].code
          
        }
        this.form.setFieldsValue({
          materialName:this.materName,
          code:this.matercode,
          materialTypeId:this.materId,
          model: data[0]? data[0].spec : ''
        })
      },

      // 确定
      handleOk() {
        const that = this
        that.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true

            let formData = Object.assign(that.model, values,{
              entraceDate: moment(values.entraceDate).format('YYYY-MM-DD'),
              entryDate: moment(values.entryDate).format('YYYY-MM-DD'),
              nextCheckTime: moment(values.nextCheckTime).format('YYYY-MM-DD'),
            })


            console.log(formData)
            let obj
            if (!that.model.id) {
              // formData['assetTypeId'] = that.assetTypeId
              // formData['dutyUserId'] = that.dutyUserid
              formData['materialTypeId'] = that.materId
              // formData['materialCode'] = that.materId
              obj = addEquipment(formData)
            } else {
              obj = editEquipment(formData)
            }
            obj
              .then((res) => {
                if (res.success) {
                  that.$message.success(res.message)
                  that.$emit('ok')
                } else {
                  that.$message.warning(res.message)
                }
              })
              .finally(() => {
                that.confirmLoading = false
                that.close()
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
      }
    }
  }
</script>

<style scoped>

</style>