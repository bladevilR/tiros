<template>
  <div>
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="监修设备">
              <a-select
                v-decorator.trim="['assetTypeName', validatorRules.outinDetailId]"
                placeholder="交接明细"
                :open="false"
                :showArrow="true"
                @focus="openOutinDetailModal"
                ref="myOutinDetailSelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="合同">
              <j-dict-select-tag
                v-decorator.trim="['contractId']"
                :triggerChange="true"
                placeholder="请选择合同"
                dictCode="bu_contract_info,contract_name,id,status<>2"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆">
              <j-dict-select-seach-tag
                v-decorator.trim="['trainNo', validatorRules.trainNo]"
                :triggerChange="true"
                placeholder="请选择车辆"
                dictCode="bu_train_info,train_no,train_no"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="派遣人员">
              <!--              <j-dict-select-tag
                              v-decorator.trim="[ 'dispatchUserId',validatorRules.dispatchUserId]"
                              :triggerChange="true"
                              placeholder="请选择派遣人员"
                              dictCode="sys_user,username,id" />-->
              <a-select
                v-decorator="['dispatchUserName', validatorRules.dispatchUserId]"
                placeholder="请选择人员"
                :open="false"
                :showArrow="true"
                @focus="openUserSelectModal"
                ref="myUserSelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="人员联系方式">
              <a-input placeholder="请输入联系方式" v-decorator.trim="['userPhone']" />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="派遣班组">
              <j-dict-select-tag
                v-decorator.trim="['dispatchGroupId', validatorRules.dispatchGroupId]"
                :triggerChange="true"
                placeholder="请选择派遣班组"
                @change="selectDispatchGroup"
                :dictCode="dictGroupStr"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="派往公司">
              <a-select
                :disabled="hasSupplierId"
                v-decorator="['supplierName', validatorRules.supplierId]"
                placeholder="请选择厂商"
                :open="false"
                :showArrow="true"
                @focus="openSupplierModal"
                ref="mySupplierSelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="公司联系方式">
              <a-input placeholder="请输入联系方式" v-decorator.trim="['supplierPhone']" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="联系地址">
              <a-input placeholder="请输入联系地址" v-decorator.trim="['supplierAddress']" />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="任务名称">
              <a-input placeholder="请输入任务名称" v-decorator.trim="['taskContent', validatorRules.taskContent]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="出派时间">
              <a-date-picker
                placeholder="请输入出派时间"
                style="width: 100%"
                v-decorator.trim="['dispatchDate', validatorRules.dispatchDate]"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="返回时间">
              <a-date-picker
                style="width: 100%"
                placeholder="请输入返回时间"
                v-decorator.trim="['returnDate', validatorRules.returnDate]"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
    <outin-detail-select ref="outinDetailForm" :multiple="false" :billType="0" @ok="addTarget"></outin-detail-select>
    <supplier-list ref="supplierModal" :multiple="false" @ok="supplierSelect"></supplier-list>
    <user-list ref="userSelectModal" :dep-id="dispatchGroupId" :multiple="false" @ok="userSelect"></user-list>
  </div>
</template>

<script>
import { everythingIsEmpty } from '@/utils/util'
import {
  addSupervise,
  editSupervise,
  getOutinDetailList,
  getSupplierList,
  getSuperviseById,
} from '@api/tirosOutsourceApi'
import OutinDetailSelect from '../supervise/OutinDetailSelect'
import SupplierList from '@views/tiros/common/selectModules/SupplierList'
import moment from 'moment'
import UserList from '@views/tiros/common/selectModules/UserList'
import isMobile from 'ant-design-vue/lib/vc-menu/utils/isMobile'
import { isEmail, isPhone } from '@/utils/validate'

export default {
  name: 'SuperviseItemForm',
  props: {
    assetId: {
      type: String,
      default: null,
    },
    businessKey: {
      type: String,
      default: null,
    },
    isReadonly: {
      type: Boolean,
      default: true,
    },
    fromFlow: {
      type: Boolean,
      default: false,
    },
  },
  components: { OutinDetailSelect, SupplierList, UserList },
  data() {
    return {
      confirmLoading: false,
      dictGroupStr:
        "bu_mtr_workshop_group,group_name,id,workshop_id='" + this.$store.getters.userInfo.workshopId + "'|workshop_id",
      treeData: [],
      title: '操作',
      visible: false,
      dispatchGroupId: '',
      model: {},
      dispatchUserId: '',
      outinDetailId: '',
      outinNo: '',
      assetTypeId: '',
      supplierName: '',
      supplierId: '',
      hasSupplierId:false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 },
      },
      queryParam: {
        category: null,
        name: '',
        objTypeId: '',
        status: null,
        id: '',
        pageNo: 1,
        pageSize: 10,
      },
      form: this.$form.createForm(this),
      validatorRules: {
        outinDetailId: { rules: [{ required: true, message: '请选择交接明细!' }] },
        assetTypeId: { rules: [{ required: true, message: '请选择部件!' }] },
        trainNo: { rules: [{ required: true, message: '请选择车辆!' }] },
        dispatchUserId: { rules: [{ required: true, message: '请选择派遣人员!' }] },
        dispatchGroupId: { rules: [{ required: true, message: '请选择派遣班组!' }] },
        supplierId: { rules: [{ required: true, message: '请选择派往公司!' }] },
        taskContent: {
          rules: [
            { required: true, message: '请输入任务内容!' },
            { max: 64, message: '输入长度不能超过64字符!' },
          ],
        },
        dispatchDate: { rules: [{ required: true, message: '请输入出派时间!' }] },
        returnDate: { rules: [{ required: true, message: '请输入返回时间!' }] },
      },
    }
  },
  mounted() {
    if (this.businessKey) {
      this.loadById(this.businessKey)
    }
  },
  methods: {
    loadById(id) {
      getSuperviseById(id).then((res) => {
        if (res.success) {
          let record = Object.assign({}, res.result)
          this.edit(record)
        } else {
          console.error('获取指定ID监修记录失败：', res)
          this.$message.warn('查询监修记录失败')
        }
        this.$emit('loaded')
      })
    },
    openUserSelectModal() {
      this.$refs.userSelectModal.showModal()
      this.$refs.myUserSelect.blur()
    },
    userSelect(data) {
      if (!everythingIsEmpty(data)) {
        this.dispatchUserId = data[0].id
        this.form.setFieldsValue({
          dispatchUserName: data[0].realname,
          userPhone: data[0].phone,
          dispatchGroupId: data[0].departId,
        })
      }
    },
    selectDispatchGroup(v, option) {
      // 选择派遣班组
      if (option) {
        this.dispatchGroupId = option.value
        this.form.setFieldsValue({
          dispatchUserName: '',
          userPhone: '',
        })
      }
    },
    checkPhone(rule, value, callback) {
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
    checkEmail(rule, value, callback) {
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
    handleCategory(value) {
      this.category = value
    },
    openModal() {
      switch (this.category) {
        case '1':
          break
        case '2':
          this.$refs.selectModal.showModal()
          break
        case '3':
          this.$refs.MaterialModalForm.showModal()
          break
        default:
          break
      }
      this.$refs.mySelect.blur()
    },
    addTarget(data) {
      if (!everythingIsEmpty(data)) {
        this.outinDetailId = data[0].id
        this.assetTypeId = data[0].assetTypeId
        this.supplierId=data[0].supplierId
        this.hasSupplierId=!everythingIsEmpty(this.supplierId)
        this.form.setFieldsValue({
          assetTypeName: data[0].assetName,
          trainNo: data[0].trainNo,
          contractId: data[0].contractId,
          supplierName:data[0].supplierName,
          supplierPhone: data[0].supplierPhone,
          supplierAddress: data[0].supplierAddress,
        })
      }
    },
    changeStatus(checked) {
      this.status = checked
    },
    openSupplierModal() {
      this.$refs.supplierModal.showModal()
      this.$refs.mySupplierSelect.blur()
    },
    supplierSelect(data) {
      if (!everythingIsEmpty(data)) {
        this.$nextTick(() => {
          this.form.setFieldsValue({
            supplierName: data[0].name,
            supplierPhone: data[0].contactPhone,
            supplierAddress: data[0].address,
          })
        })
        this.supplierId = data[0].id
      }
    },
    openOutinDetailModal() {
      this.$refs.outinDetailForm.showModal()
      this.$refs.myOutinDetailSelect.blur()
    },
    async findList(value) {
      this.queryParam.id = value
      await getOutinDetailList(this.queryParam).then((res) => {
        let result = res.result.records
        if (result) {
          this.outinNo = result[0].outinNo
        }
      })
    },
    onSupplierChange(value) {
      this.queryParam.id = value
      getSupplierList(this.queryParam).then((res) => {
        let supplier = res.result.records
        if (supplier) {
          this.form.setFieldsValue({
            supplierPhone: supplier[0].contactPhone,
            supplierAddress: supplier[0].address,
          })
        }
      })
    },
    add() {
      this.edit({})
    },
    async edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      if (record.id) {
        this.outinDetailId = record.outinDetailId
        this.supplierId = record.supplierId
        this.hasSupplierId=!everythingIsEmpty(this.supplierId)
        await this.findList(this.outinDetailId)
        this.assetTypeId = record.assetTypeId
        this.dispatchUserId = record.dispatchUserId
      }

      this.$nextTick(() => {
        this.form.setFieldsValue({
          assetTypeName: this.model.assetTypeName,
          trainNo: this.model.trainNo,
          contractId: this.model.contractId,
          supplierPhone: this.model.supplierPhone,
          supplierAddress: this.model.supplierAddress,
          // dispatchUserId: this.model.dispatchUserId,
          dispatchUserName: this.model.dispatchUserName,
          userPhone: this.model.userPhone,
          dispatchGroupId: this.model.dispatchGroupId,
          supplierName: this.model.supplierName,
          taskContent: this.model.taskContent,
          dispatchDate: moment(this.model.dispatchDate || undefined),
          returnDate: moment(this.model.returnDate || undefined),
        })
      })
    },
    validate() {
      return new Promise((resolve, reject) => {
        this.form.validateFields((err, values) => {
          if (!err) {
            resolve()
          }
        })
      })
    },
    // 确定
    save(opts) {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values, {
            dispatchDate: moment(values.dispatchDate).format('YYYY-MM-DD'),
            returnDate: moment(values.returnDate).format('YYYY-MM-DD'),
            outinDetailId: this.outinDetailId,
            assetTypeId: this.assetTypeId,
            supplierId: this.supplierId,
            dispatchUserId: this.dispatchUserId,
          })
          let obj
          if (!that.model.id) {
            obj = addSupervise(formData)
          } else {
            obj = editSupervise(formData)
          }
          obj
            .then((res) => {
              if (res.success) {
                if(this.fromFlow) {
                  that.$emit('ok', opts)
                } else {
                  that.$emit('ok')
                }
              } else {
                console.error('保存监造申请失败：', res.message)
                if (this.fromFlow) {
                  opts.res = res
                  that.$emit('fail', opts)
                } else {
                  that.$emit('fail')
                }
              }
            })
            .finally(() => {
              that.confirmLoading = false
            })
        }
      })
    },
  },
}
</script>

<style scoped>
</style>