<template>
  <a-modal
    :title="`${title}`"
    width="100%"
    dialogClass="fullDialog"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="save"
    @cancel="handleCancel"
  >
    <a-spin class="full-srceen" :spinning="confirmLoading">
      <!-- <div class="table-page-search-wrapper"> -->
      <NaCardContent
        class="table-page-search-wrapper"
        title="基本信息"
        style="padding-bottom: 0; background: transparent"
      >
        <a-form :form="form" layout="inline">
          <a-row :gutter="24">
            <a-col :md="8" :sm="24">
              <a-form-item label="借用类型" :labelCol="labelCol1" :wrapperCol="wrapperCol1">
                <j-dict-select-tag
                  v-decorator.trim="['borrowType', validatorRules.borrowType]"
                  dictCode="bu_borrow_type"
                  @input="onBorrowTypeChange"
                />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="借用组织" :labelCol="labelCol1" :wrapperCol="wrapperCol1">
                <j-select-depart
                  v-if="queryParam.borrowType == 1"
                  v-decorator="['borrowDep', validatorRules.borrowDep]"
                  :rootOpened="false"
                />
                <a-input
                  v-else
                  v-decorator="['borrowDep', validatorRules.borrowDep]"
                  placeholder="借用部门或厂家"
                ></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="借用人员" :labelCol="labelCol1" :wrapperCol="wrapperCol1">
                <!-- <a-input v-model="queryParam.warehouseId" placeholder="借用人"></a-input> -->
<!--                <a-select
                  v-if="queryParam.borrowType == 1"
                  ref="userSelect"
                  v-decorator="['borrowUserName', validatorRules.borrowUserName]"
                  placeholder="借用人员"
                  :open="false"
                  style="width: 100%"
                  @focus="openUserSelectModal()"
                >
                  <a-icon slot="suffixIcon" type="ellipsis" />
                </a-select>-->
                <a-input
                  v-decorator="['borrowUser', validatorRules.borrowUser]"
                  placeholder="借用人员"
                ></a-input>
              </a-form-item>
            </a-col>

            <a-col :md="8" :sm="24">
              <a-form-item label="归还类型" :labelCol="labelCol1" :wrapperCol="wrapperCol1">
                <j-dict-select-tag
                  v-decorator="['returnType', validatorRules.returnType]"
                  dictCode="bu_material_return_type"
                  @input="onReturnTypeChange"
                />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="借用日期" :labelCol="labelCol1" :wrapperCol="wrapperCol1">
                <a-date-picker
                  v-decorator="['pickDate', validatorRules.pickDate]"
                  format="YYYY-MM-DD"
                  @change="dateChange"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24" :labelCol="labelCol1" :wrapperCol="wrapperCol1">
              <a-form-item label="二级库">
                <!-- <a-input v-model="queryParam.exeTime" placeholder="请输入内容"></a-input> -->
                <!-- <a-input v-model="queryParam.warehouseName" placeholder="二级库"></a-input> -->
                <j-tree-select
                  placeholder="请选择"
                  dict="bu_mtr_warehouse,name,id"
                  pidField="parent_id"
                  v-decorator="['warehouseId', validatorRules.warehouseId]"
                  @change="handleWarehouseChange"
                />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24" :labelCol="labelCol1" :wrapperCol="wrapperCol1">
              <a-form-item label="归还状态">
                <j-dict-select-tag
                  v-decorator="['returnStatus', validatorRules.returnStatus]"
                  :dictCode="dictReturnStatusStr"
                  @input="onReturnStatusChange"
                />
              </a-form-item>
            </a-col>
            <a-col :md="16" :sm="24" :labelCol="labelCol1" :wrapperCol="wrapperCol1">
              <a-form-item label="备注说明">
                <!-- queryParam.remark -->
                <a-input :maxLength="201" v-decorator="['remark', validatorRules.remark]" placeholder="请输入内容"></a-input>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
        <!-- </div> -->
      </NaCardContent>
      <BorrowDetail
        ref="borrowDetailRef"
        :tableData="queryParam.borrowDetailList"
        :borrowInfo="queryParam"
      ></BorrowDetail>
      <!-- <LocationList ref="locationModal" @ok="onSelectLocation"></LocationList> -->
      <user-list ref="userSelectModal" :multiple="false" @ok="onSelectUser"></user-list>
    </a-spin>
  </a-modal>
</template>

<script>
import NaCardContent from '@comp/tiros/NaCardContent'
import UserList from '@views/tiros/common/selectModules/UserList'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { addBorrow, editBorrow, getBorrowDetail } from '@/api/tirosMaterialApi'
import { everythingIsEmpty, randomUUID } from '@/utils/util'
import BorrowDetail from '@views/tiros/material/borrow/BorrowDetail'
import JTreeSelect from '@/components/jeecg/JTreeSelect'
import { getUserByDepId } from '@api/api'
import JSelectDepart from '@/components/jeecgbiz/JSelectDepart'
// import LocationList from '@views/tiros/common/selectModules/LocationList'

export default {
  name: 'BorrowModal',
  components: { LineSelectList, NaCardContent, BorrowDetail, JTreeSelect, JSelectDepart, UserList },
  data() {
    return {
      value: 1,
      title: '借用',
      visible: false,
      dictReturnStatusStr: 'bu_material_borrow_return_status',
      model: {},
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        borrowDep: { rules: [{ required: true, message: '请选择组织!' },{max: 32, message: '输入长度不能超过32字符!'}] },
       /* borrowUserName: { rules: [{ required: true, message: '请选择借用人员!' }] },*/
        borrowUser: { rules: [{ required: true, message: '请选择借用人员!' },{max: 32, message: '输入长度不能超过32字符!'}] },
        borrowType: { rules: [{ required: true, message: '请选择借用类型!' }] },
        returnType: { rules: [{ required: true, message: '请选择归还类型!' }] },
        pickDate: { rules: [{ required: true, message: '请选择日期!' }] },
        returnStatus: { rules: [{ required: true, message: '请选择归还状态!' }] },
        warehouseId: { rules: [{ required: true, message: '请选择仓库!' }] },
        remark:{ rules: [{ required: false, max: 255, message: '输入长度不能超过255字符!' }] },
      },
      userList: [],
      pickDate: this.$moment(new Date()),
      queryParam: {
        borrowDate: this.$moment(new Date()).format('YYYY-MM-DD'),
        borrowDep: null,
        borrowType: null,
        returnStatus: null,
        returnType: null,
        billCode: null,
        borrowDetailList: [],
        borrowUser: null,
        borrowUserName: null,
        deptName: null,
        id: null,
        remark: null,
        warehouseName: null,
        warehouseId: null,
      },
    }
  },
  created() {},
  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      this.visible = true
      this.$nextTick(() => {
        this.$refs.borrowDetailRef.initStatus()
      })
      if (record.id) {
        record = Object.assign(this.queryParam, record)
        this.pickDate = this.$moment(record.borrowDate)
        if (record.returnType == 1) {
          this.dictReturnStatusStr = 'bu_material_borrow_return_status'
        } else if (record.returnType == 2) {
          this.dictReturnStatusStr = 'bu_material_transfers_status'
        }
      } else {
        this.pickDate = this.$moment(new Date())
        this.queryParam.borrowDep = null
        this.queryParam.borrowType = null
        this.queryParam.returnStatus = null
        this.queryParam.returnType = null
        this.queryParam.warehouseId = null
        this.queryParam.billCode = null
        this.queryParam.borrowDetailList = []
        this.queryParam.borrowUser = null
        this.queryParam.borrowUserName = null
        this.queryParam.deptName = null
        this.queryParam.id = null
        this.queryParam.remark = null
        this.queryParam.warehouseName = null
        this.queryParam.warehouseId = null
        record = Object.assign({}, this.queryParam)
      }
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          pickDate: this.pickDate,
          borrowDep: record.borrowDep,
          borrowUser: record.borrowUser,
          borrowType: record.borrowType,
          returnStatus: record.returnStatus,
          returnType: record.returnType,
          borrowDate: record.borrowDate,
          warehouseId: record.warehouseId,
          remark:record.remark,
        })
      })
      this.updateDetail()
    },
    // 确定
    handleOk() {
      this.save()
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    dateChange(value) {
      this.pickDate = value
      this.queryParam.borrowDate = this.$moment(this.pickDate).format('YYYY-MM-DD')
    },
    // 保存数据
    save() {
      if (this.$refs.borrowDetailRef.isSaveData) {
        this.$message.warn('请先保存明细内容')
        return
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          if (this.queryParam.borrowDetailList.length === 0) {
            this.$message.warn('至少需要一条借用明细')
            return
          }
          Object.assign(this.queryParam, values)
          this.queryParam.borrowDate = this.pickDate.format('YYYY-MM-DD')
          if (!this.queryParam.id) {
            this.queryParam.id = randomUUID()
            console.log(this.queryParam)
            addBorrow(this.queryParam).then((res) => {
              if (res.success) {
                this.$emit('ok')
                this.visible = false
              } else {
                this.$message.error(res.message)
              }
            })
          } else {
            console.log(this.queryParam)
            editBorrow(this.queryParam).then((res) => {
              if (res.success) {
                this.$emit('ok')
                this.visible = false
              } else {
                this.$message.error(res.message)
              }
            })
          }
        }
      })
    },
    updateDetail() {
      getBorrowDetail(this.queryParam.id).then((res) => {
        if (res.success) {
          this.queryParam.borrowDetailList = res.result
        } else {
          this.$message.error(res.message)
          this.visible = false
        }
      })
      // this.$nextTick(() => {
      //   if (this.queryParam.id) {
      //     this.$refs.borrowDetailRef.updateDetail(this.queryParam.id)
      //   }
      // })
    },
    openUserSelectModal() {
      this.$refs.userSelectModal.showModal()
      this.$refs.userSelect.blur()
    },
    // 仓位选择
    handleWarehouseChange(value) {
      this.form.setFieldsValue({
        warehouseId: value,
      })
    },
    onBorrowTypeChange(value) {
      this.form.setFieldsValue({
        borrowType: value,
      })
      this.queryParam.borrowType = value
      this.queryParam.borrowUser = ''
      this.form.setFieldsValue({
        borrowDep: '',
        borrowUser: '',
      })
    },
    onReturnTypeChange(value) {
      this.form.setFieldsValue({
        returnType: value,
      })
      if (value) {
        if (value == 1) {
          this.dictReturnStatusStr = 'bu_material_borrow_return_status'
        } else if (value == 2) {
          this.dictReturnStatusStr = 'bu_material_transfers_status'
        }
      } else {
        this.dictReturnStatusStr = ''
      }
      this.$refs.borrowDetailRef.updateDict(value)
    },
    onReturnStatusChange(value) {
      this.form.setFieldsValue({
        returnStatus: value,
      })
    },
    //选择员工
    onSelectUser(data) {
      if (data && data.length > 0) {
        this.queryParam.borrowUser = data[0].id
        this.queryParam.borrowUserName = data[0].realname
        this.form.setFieldsValue({
          borrowUserName: data[0].realname,
        })
      }
    },
  },
}
</script>