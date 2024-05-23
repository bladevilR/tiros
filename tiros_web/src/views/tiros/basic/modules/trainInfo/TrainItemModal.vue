<template>
  <a-modal
    :title="title"
    width="1200px"
    :visible="visible"
    centered
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
  >
    <a-form :form="form">
      <div class="info-wrapper info-top-wrapper">
        <h4>基本信息</h4>
        <a-row>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属线路">
<!--              <j-dict-select-tag
                :triggerChange="true"
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />-->
              <line-select-list
                v-decorator="['lineId', validatorRules.lineId]">
              </line-select-list>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆编码">
              <a-input :maxLength="17" placeholder="请输入车辆编码" allow-clear v-decorator="['trainNo', validatorRules.trainNo]"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="停放股道">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator="['trackId', validatorRules.trackId]"
                dictCode="bu_mtr_track,name,id,status=1"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="编组数量">
              <a-input-number :min="0" :max="127" :precision="0" style="width: 100%"
              v-decorator="['groupNum', validatorRules.groupNum]"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="走行里程">
              <a-input-number style="width: 78%" :min="0" :max="999999999999999" :precision="2"
              v-decorator="['mileage', validatorRules.mileage]"/>&nbsp;
              万公里
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="投入状态">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator="['status', validatorRules.status]"
                dictCode="bu_train_status"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </div>
      <div class="info-wrapper info-top-wrapper">
        <h4>其他信息</h4>
        <a-row>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属厂商">
<!--              <j-dict-select-tag
                :triggerChange="true"
                v-decorator="['supplierId', validatorRules.supplierId]"
                dictCode="bu_base_supplier,name,id"
              />-->
              <a-select
                v-decorator="['supplierName', validatorRules.supplierId]"
                placeholder="请选择厂商"
                :open="false"
                :showArrow="true"
                @focus="openSupplierModal"
                ref="mySupplierSelect">
                <div slot="suffixIcon">
                  <a-icon
                    v-if="supplierId"
                    @click.stop="clearValue"
                    type="close-circle"
                    style="padding-right: 3px"
                  />
                  <a-icon v-else type="ellipsis" />
                </div>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="合同编号">
              <a-input
                placeholder="请输入合同编码"
                allow-clear
                :maxLength="33"
                v-decorator="['contractNo', validatorRules.contractNo]"
              ></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="合同名称">
              <a-input
                placeholder="请输入合同名称"
                allow-clear
                :maxLength="33"
                v-decorator="['contractName', validatorRules.contractName]"
              ></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="制造日期">
              <a-date-picker style="width:100%;" v-decorator="['madeDate', validatorRules.madeDate]"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="购入日期">
              <a-date-picker style="width:100%;" v-decorator="['buyDate', validatorRules.buyDate]"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="投入日期">
              <a-date-picker style="width:100%;" v-decorator="['useDate', validatorRules.useDate]"/>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="质保日期">
              <a-date-picker style="width:100%;" v-decorator="['warrantyDate', validatorRules.warrantyDate]"/>
            </a-form-item>
          </a-col>
        </a-row>
      </div>
      <supplier-list ref="supplierModal" :multiple="false" @ok="supplierSelect"></supplier-list>
    </a-form>
  </a-modal>
</template>

<script>
import moment from 'moment'
import 'moment/locale/zh-cn'
import { addTrainInfo, editTrainInfo } from '@/api/tirosApi'
import { duplicateCheck } from '@api/api'
import { everythingIsEmpty } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import SupplierList from '@views/tiros/common/selectModules/SupplierList'

export default {
  name: 'TrainItemModal',
  components:{LineSelectList,SupplierList},
  data() {
    return {
      value: 1,
      title: '操作',
      visibleCheck: true,
      visible: false,
      model: {},
      supplierName: '',
      supplierId: '',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        lineId: { rules: [{ required: true, message: '请选择所属线路!' }] },
        trainNo: {
          rules: [{ required: true, message: '请输入车辆编码!' }, { validator: this.validateTrainNo }, {
            max: 16,
            message: '输入长度不能超过16字符!'
          }]
        },
        trackId: { rules: [{ required: false, message: '请选择停放股道!' }] },
        groupNum: { rules: [{ required: true, message: '请输入编组数量!' }] },
        mileage: { rules: [{ required: true, message: '请输入走行里程!' }] },
        status: { rules: [{ required: true, message: '请选择投入状态!' }] },
        supplierId: { rules: [] },
        contractNo: { rules: [{ max: 32, message: '输入长度不能超过32字符!' }] },
        contractName: { rules: [{ max: 32, message: '输入长度不能超过32字符!' }] },
        madeDate: { rules: [] },
        buyDate: { rules: [] },
        useDate: { rules: [] },
        warrantyDate: { rules: [] }
      }
    }
  },
  created() {
  },
  methods: {
    openSupplierModal () {
      this.$refs.supplierModal.showModal()
      this.$refs.mySupplierSelect.blur()
    },
    supplierSelect (data) {
      if (!everythingIsEmpty(data)) {
        this.$nextTick(() => {
          this.form.setFieldsValue({ supplierName: data[0].name })
        })
        this.supplierId = data[0].id
      }
    },
    clearValue() {
      this.supplierId = ''
      this.form.setFieldsValue({ supplierName: ''})
    },
    validateTrainNo(rule, value, callback) {
      let params = {
        tableName: 'bu_train_info',
        fieldName: 'train_no',
        fieldVal: value,
        dataId: this.model.id
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
    moment,
    add() {
      this.edit({})
    },
    edit(record) {
      if (record.id) {
        this.visiblekey = true
        this.supplierId=record.supplierId
      } else {
        this.visiblekey = false
      }
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          lineId: this.model.lineId,
          trainNo: this.model.trainNo,
          trackId: this.model.trackId,
          groupNum: this.model.groupNum,
          mileage: this.model.mileage,
          status: this.model.status,
          supplierName: this.model.supplier,
          contractNo: this.model.contractNo,
          contractName: this.model.contractName,
          madeDate: this.model.madeDate ? moment(this.model.madeDate, 'YYYY-MM-DD') : '',
          buyDate: this.model.buyDate ?  moment(this.model.buyDate, 'YYYY-MM-DD') : '',
          useDate: this.model.useDate ?  moment(this.model.useDate, 'YYYY-MM-DD') : '',
          warrantyDate: this.model.warrantyDate ?  moment(this.model.warrantyDate, 'YYYY-MM-DD') : ''
        })
      })
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values, {
            madeDate: values.madeDate ? moment(values.madeDate).format('YYYY-MM-DD') : '',
            buyDate: values.buyDate ? moment(values.buyDate).format('YYYY-MM-DD') : '',
            useDate: values.useDate ? moment(values.useDate).format('YYYY-MM-DD') : '',
            warrantyDate: values.warrantyDate ? moment(values.warrantyDate).format('YYYY-MM-DD') : '',
            supplierId:this.supplierId
          })
          let obj
          if (!that.model.id) {
            obj = addTrainInfo(formData)
          } else {
            obj = editTrainInfo(formData)
          }
          obj
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
                that.close()
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
    onChange(date) {
    },
    // checkRepeat(rule, value, callback) {
    //   if(value.length){
    //     callback('11')
    //   }else(
    //     callback()
    //   )
    // },
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

<style lang="less" scoped>
.info-wrapper {
  border: 1px solid #eee;
  position: relative;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 20px;
}

.info-wrapper h4 {
  position: absolute;
  top: -14px;
  padding: 1px 8px;
  margin-left: 16px;
  color: #777;
  border-radius: 2px 2px 0 0;
  background: #fff;
  font-size: 14px;
  width: auto;
}
</style>