<template>
  <a-modal
    :title="title"
    :width="'85%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    dialogClass="fullDialog"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <div class="content">
          <div class="info-wrapper info-top-wrapper" style="margin-bottom: 20px">
            <h4>基本信息</h4>
            <a-row :gutter="24">
              <a-col :md="6" :sm="24" :offset="3">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="合同编号">
                  <a-input placeholder="合同编号" v-decorator="['contractNo', validatorRules.contractNo]" />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="合同名称">
                  <a-input placeholder="合同名称" v-decorator="['contractName', validatorRules.contractName]" />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="合同厂商">
                  <!--                  <j-dict-select-tag
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
                    ref="mySupplierSelect"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="6" :sm="24" :offset="3">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="合同金额">
                  <a-input-number
                    style="width: 100%"
                    placeholder="合同金额"
                    :formatter="(value) => `${value}万`"
                    :parser="(value) => value.replace('万', '')"
                    :min="0"
                    :step="0.1"
                    :max="99999999"
                    v-decorator="['amount', validatorRules.amount]"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="合同税率">
                  <a-input-number
                    style="width: 100%"
                    :formatter="(value) => `${value}%`"
                    :parser="(value) => value.replace('%', '')"
                    :min="0"
                    :step="0.1"
                    :max="100"
                    placeholder="合同税率"
                    v-decorator="['taxRate', validatorRules.taxRate]"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="执行税率">
                  <a-input-number
                    style="width: 100%"
                    :formatter="(value) => `${value}%`"
                    :parser="(value) => value.replace('%', '')"
                    :min="0"
                    :step="0.1"
                    :max="100"
                    placeholder="执行税率"
                    v-decorator="['executeTaxRate', validatorRules.executeTaxRate]"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="6" :sm="24" :offset="3">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="合同状态">
                  <j-dict-select-tag
                    placeholder="选择合同状态"
                    :triggerChange="true"
                    v-decorator="['status', validatorRules.status]"
                    dictCode="bu_contract_status"
                  />
                </a-form-item>
              </a-col>
              <!-- <a-col :md="6" :sm="24" :offset="3">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="暂列金">
                  <a-input-number
                    style="width: 100%"
                    placeholder="暂列金"
                    :formatter="(value) => `${value}万`"
                    :parser="(value) => value.replace('万', '')"
                    :min="0"
                    :step="0.1"
                    :max="99999999"
                    v-decorator="['behindMoney', { initialValue: 0 }]"
                  />
                </a-form-item>
              </a-col> -->
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="保证金类型">
                  <j-dict-select-tag
                    :triggerChange="true"
                    placeholder="选择保证金类型"
                    v-decorator="['depositType', validatorRules.depositType]"
                    dictCode="bu_contract_deposit_type"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="履约保证金">
                  <a-input-number
                    style="width: 100%"
                    placeholder="履约保证金"
                    :formatter="(value) => `${value}万`"
                    :parser="(value) => value.replace('万', '')"
                    :min="0"
                    :step="0.1"
                    :max="99999999"
                    v-decorator="['deposit', { initialValue: 0 }]"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="6" :sm="24" :offset="3">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="所属线路">
                  <line-select-list
                    v-decorator="['lineId', validatorRules.lineId]"
                    :trigger-change="true"
                    @change="handleLine"
                  >
                  </line-select-list>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="开始时间">
                  <a-date-picker
                    style="width: 100%"
                    placeholder="开始时间"
                    v-decorator="['startDate', validatorRules.startDate]"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="终止日期">
                  <a-date-picker
                    style="width: 100%"
                    placeholder="终止日期"
                    v-decorator="['finishDate', validatorRules.finishDate]"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="6" :sm="24" :offset="3">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="质保期">
                  <a-input-number
                    style="width: 80%"
                    placeholder="质保期"
                    :min="0"
                    :max="99999999"
                    :precision="0"
                    v-decorator="['expiration', validatorRules.expiration]"
                  />
                  个月
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="质保金类型">
                  <j-dict-select-tag
                    :triggerChange="true"
                    placeholder="选择质保金类型"
                    v-decorator="['qualityDepositType']"
                    dictCode="bu_contract_deposit_type"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="质保金比例">
                  <a-input-number
                    style="width: 100%"
                    :formatter="(value) => `${value}%`"
                    :parser="(value) => value.replace('%', '')"
                    :min="0"
                    :step="0.1"
                    :max="100"
                    placeholder="质保金比例"
                    v-decorator="['qualityDepositPercent', validatorRules.qualityDepositPercent]"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="6" :sm="24" :offset="3">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="预付款">
                  <!--                  <a-input placeholder="输入预付款" v-decorator="['advancePayment', validatorRules.advancePayment]" />-->
                  <a-input-number
                    style="width: 100%"
                    placeholder="输入预付款"
                    :formatter="(value) => `${value}万`"
                    :parser="(value) => value.replace('万', '')"
                    :min="0"
                    :step="0.1"
                    :max="99999999"
                    v-decorator="['advancePayment', { initialValue: 0 }]"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="合同部件" required="true">
                  <a-select
                    placeholder="请选择"
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
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="合同部件别名">
                  <a-input placeholder="合同部件别名" v-decorator="['assetTypeAlias',validatorRules.assetTypeAlias]" />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24" :offset="3">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="每列部件数量">
                  <a-input placeholder="每列部件数量" v-decorator="['assetAmount', validatorRules.assetAmount]" />
                </a-form-item>
              </a-col>

              <a-col :md="6" :sm="24" >
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="车辆数">
                  <a-input-number :min="1" style="width: 100%" placeholder="输入车辆数"
                                  v-decorator="['trainAmount', validatorRules.trainAmount]" />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="已完成数">
                  <a-input-number :min="0" style="width: 100%" placeholder="输入已完成数"
                                  v-decorator="['finishAmount', validatorRules.finishAmount]" />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24" :offset="3">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="修程类型">
                  <j-dict-select-tag
                    :triggerChange="true"
                    placeholder="请选择修程类型"
                    v-decorator="['repairProgramId', validatorRules.repairProgramId]"
                    dictCode="bu_repair_program,name,id"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="计数开始">
                  <a-input-number :min="1" style="width: 100%" placeholder="从第几列开始计算" v-decorator="['payBegin']" />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="支付间隔数">
                  <a-input-number :min="1" style="width: 100%" placeholder="每隔多少列支付一次" v-decorator="['payInterval']" />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="6" :sm="24"  :offset="3">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="维修周期">
                  <a-input-number
                    style="width: 100%"
                    placeholder="自然日"
                    :min="0"
                    :max="999999999"
                    :precision="0"
                    v-decorator="['needDay', validatorRules.needDay]"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="签订日期">
                  <a-date-picker style="width: 100%" placeholder="签订日期" v-decorator="['signDate']" />
                </a-form-item>
              </a-col>
              <a-col :md="18" :sm="24" :offset="3">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="支付条款">
                  <a-textarea
                    style="width: 100%"
                    placeholder="支付条款"
                    v-decorator="['attention', validatorRules.attention]"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="18" :sm="24" :offset="3">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="监修信息">
                  <a-textarea
                    style="width: 100%"
                    placeholder="输入监修信息"
                    v-decorator="['superInfo', validatorRules.superInfo]"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="18" :sm="24" :offset="3">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="培训信息">
                  <a-textarea
                    style="width: 100%"
                    placeholder="输入培训信息"
                    v-decorator="['trainInfo', validatorRules.trainInfo]"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="18" :sm="24" :offset="3">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
                  <a-textarea style="width: 100%" placeholder="备注" v-decorator="['remark', validatorRules.remark]" />
                </a-form-item>
              </a-col>
            </a-row>
          </div>
        </div>
        <div class="content">
          <div class="info-wrapper info-top-wrapper">
            <h4>合同附件</h4>
            <a-row :gutter="24">
              <a-col :md="18" :sm="24" :offset="3">
                <DocUpload
                  ref="upload"
                  :default-file-list="defaultFileList"
                  @setBforeUploadStatus="setBforeUploadStatus"
                  @uploaded="successUploadFile"
                  @removed="onRemoveFile"
                  @uploadFail="uploadFail"
                  @setUpLoadingEndStatus="setUpLoadingEndStatus"
                  :show-upload="false"
                  :isFileEmpty="true"
                ></DocUpload>
                <a-divider orientation="left" style="font-size: 14px">已上传列表</a-divider>
                <vxe-table
                  style="margin-top: 12px"
                  border
                  :data="annexList"
                  show-overflow="tooltip"
                  :edit-config="{ trigger: 'manual', mode: 'row' }"
                >
                  <div class=""></div>
                  <vxe-table-column
                    type="index"
                    title="序号"
                    width="50px"
                    header-align="center"
                    align="center"
                  ></vxe-table-column>
                  <vxe-table-column field="name" title="文件名" header-align="center" align="left"></vxe-table-column>
                  <vxe-table-column
                    field="type"
                    title="文件类型"
                    width="120px"
                    header-align="center"
                    align="center"
                  ></vxe-table-column>
                  <vxe-table-column
                    field="fileSize"
                    title="文件大小"
                    width="180px"
                    header-align="center"
                    align="center"
                  ></vxe-table-column>
                  <vxe-table-column title="操作" width="160px" header-align="center" align="center">
                    <template v-slot="{ row, rowIndex }">
                      <a style="margin-right: 12px" @click.stop="handleSeeing(row)">查看</a>
                      <a @click.stop="handleDelete(row, rowIndex)">删除</a>
                    </template>
                  </vxe-table-column>
                </vxe-table>
              </a-col>
            </a-row>
          </div>
        </div>
        <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
        <train-asset-type
          ref="selectModal"
          :multiple="false"
          :trainTypeId="trainTypeId"
          @ok="addTarget"
        ></train-asset-type>
        <supplier-list ref="supplierModal" :multiple="false" @ok="supplierSelect"></supplier-list>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import JTreeSelect from '@/components/jeecg/JTreeSelect'
import moment from 'moment'
import { isPrivilege } from '@/api/tirosApi'
import {
  addContractInfo,
  editContractInfo,
  getContractFiles,
  saveContractFiles,
  deleteContractFiles
} from '@/api/tirosOutsourceApi'

import TrainAssetType from '@views/tiros/common/selectModules/TrainAssetType'
import { everythingIsEmpty } from '@/utils/util'
import { addFile } from '@api/tirosApi'
import { randomUUID } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import SupplierList from '@views/tiros/common/selectModules/SupplierList'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import DocUpload from '@views/tiros/common/doc/DocUpload'
import { duplicateCheck } from '@api/api'

export default {
  name: 'ContractInfoAdd',
  components: { JTreeSelect, TrainAssetType, LineSelectList, SupplierList, DocUpload, DocPreviewModal },
  props: ['contractDetail'],
  data () {
    return {
      title: '新增',
      trainTypeId: '',
      assetTypeId: '',
      back: true,
      detail: {},
      lineId: '',
      form: this.$form.createForm(this),
      confirmLoading: false,
      visible: false,
      supplierName: '',
      supplierId: '',
      defaultFileList: [],
      annexList: [],
      filePath: '',
      fileName: '',
      status: false,
      saveFlag: true,
      uploading: false,
      validatorRules: {
        contractName: {
          rules: [
            { required: true, message: '请输入合同名称!' },
            { max: 200, message: '输入长度不能超过200字符!' }
          ]
        },
        contractNo: {
          rules: [
            { required: true, message: '请输入合同编号!' },
            { max: 32, message: '输入长度不能超过32字符!' },
            { validator: this.validateCode }
          ]
        },
        attention: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] },
        remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] },
        supplierId: { rules: [{ required: true, message: '请选择厂商!' }] },
        amount: { rules: [{ required: true, message: '请输入合同金额!' }] },
        taxRate: { rules: [{ required: true, message: '请输入合同税率!' }] },
        status: { rules: [{ required: true, message: '请选择合同状态!' }] },
        lineId: { rules: [{ required: true, message: '请选择所属线路!' }] },
        startDate: { rules: [{ required: true, message: '请输入开始时间!' }] },
        finishDate: { rules: [{ required: true, message: '请输入终止时间!' }] },
        signDate: { rules: [{ required: true, message: '请输入签订时间!' }] },
        expiration: { rules: [{ required: true, message: '请输入质保期!' }] },
        needDay: { rules: [{ required: true, message: '请输入维修周期!' }] },
        qualityDepositPercent: { rules: [{ required: true, message: '请输入质保金比例!' }] },
        depositType: { rules: [{ required: true, message: '请选择保证金类型!' }] },
        advancePayment: { rules: [{ required: true, message: '请输入预付款!' }] },
        assetAmount: { rules: [{ required: true, message: '请输入每列部件数量!' }] },
        executeTaxRate: { rules: [{ required: true, message: '请输入执行税率!' }] },
        assetTypeName: { rules: [{ required: true, message: '请选择合同设备!' }] },
        trainAmount: { rules: [{ required: true, message: '输入车辆数!' }] },
        finishAmount: { rules: [{ required: true, message: '输入已完成数!' }] },
        repairProgramId: { rules: [{ required: true, message: '请选择修程类型!' }] },
        assetTypeAlias: { rules: [{ max: 150, message: '输入长度不能超过150字符!' }] },
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 9 }
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 15 }
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 3 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 21 }
      }
    }
  },
  /* created () {
     if (this.contractDetail) {
       this.detail = this.contractDetail
       this.back = false
       this.edit(this.detail)
     } else {
       if (this.$route.params.contractDetail) {
         this.detail = this.$route.params.contractDetail
         this.edit(this.detail)
       } else {
         this.detail = {}
         this.add()
       }
     }
   },*/
  methods: {
    validateCode (rule, value, callback) {
      let params = {
        tableName: 'bu_contract_info',
        fieldName: 'contract_no',
        fieldVal: value,
        dataId: this.model.id
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
    handleLine (e, optionData) {
      if (optionData) this.trainTypeId = optionData.trainTypeId
    },
    openModal () {
      if (everythingIsEmpty(this.form.getFieldValue('lineId'))) {
        this.$message.warn('请先选择线路!')
        return
      }
      this.$refs.selectModal.showModal()
      this.$refs.mySelect.blur()
    },
    addTarget (data) {
      if (!everythingIsEmpty(data)) {
        this.assetTypeId = data[0].id
        this.form.setFieldsValue({
          assetTypeName: data[0].name
        })
      }
    },
    setBforeUploadStatus (val) {
      this.saveFlag = val
    },
    setUpLoadingEndStatus (val) {
      this.saveFlag = val
    },
    successUploadFile (fileInfos) {
      if (!fileInfos || fileInfos.length < 1) {
        return
      }
      fileInfos.map((item) => {
        Object.assign(item, { id: randomUUID() })
        this.annexList.push(item)
      })

      this.confirmLoading = false
      // addFile(fileInfos).then((res) => {
      //   // if (res.success) {
      //   //   fileInfos.map((item) => {
      //   //     this.annexList.push(item)
      //   //   })
      //   // }
      //   this.confirmLoading = false
      // })
    },
    uploadFail (file) {
      this.confirmLoading = false
    },
    onRemoveFile (file) {
    },
    beginUpload () {
      this.confirmLoading = true
      this.$refs.upload.beginUpload()
    },
    async handlePrivilege (id, operation) {
      let param = { id: id, operation: operation }
      await isPrivilege(param).then((res) => {
        this.status = res.result
      })
    },
    async handleSeeing (data) {
      await this.handlePrivilege(data.id, 2)
      if (!this.status) {
        this.$message.error('您没有权限!')
      } else {
        this.fileName = data.fileName
        // this.filePath = window._CONFIG['onlinePreviewDomainURL'] + '?url=' + encodeURIComponent(data.savepath)
        this.$refs.docPreview.handleFilePath(data.savepath)
      }
    },
    async handleDelete (data, index) {
      // 删除文件
      let that = this
      that.$confirm({
        title: '提示',
        content: '确定删除该条附件吗？',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk () {
          that.annexList.splice(index, 1)
          // deleteContractFiles({
          //   ids: data.id,
          // }).then((res) => {
          //   if (res.success) {
          //     that.annexList.splice(index, 1)
          //     that.$message.success(res.message)
          //   } else {
          //     that.$message.error(res.message)
          //   }
          // })
        },
        onCancel () {
        }
      })
    },

    handleOk () {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.beginUpload()
          let p = Object.assign({}, this.model, values, {
            startDate: moment(values.startDate).format('YYYY-MM-DD'),
            finishDate: moment(values.finishDate).format('YYYY-MM-DD'),
            signDate: values.signDate ? moment(values.signDate).format('YYYY-MM-DD') : '',
            assetTypeId: this.assetTypeId,
            supplierId: this.supplierId,
            extInfo: {
              superInfo: values.superInfo,
              trainInfo: values.trainInfo
            }
          })
          let timer = null
          clearInterval(timer)
          timer = setInterval(() => {
            if (this.saveFlag) {
              p.annexList = this.annexList
              let obj
              this.confirmLoading = true
              if (everythingIsEmpty(this.model)) {
                obj = addContractInfo(p)
              } else {
                obj = editContractInfo(p)
              }
              obj
                .then((res) => {
                  if (res.success) {
                    this.annexList = []
                    this.$message.success(res.message)
                    //this.$router.back(-1)
                    this.$emit('ok')
                    this.close()
                  } else {
                    this.$message.error(res.message)
                  }
                })
                .finally(() => {
                  clearInterval(timer)
                  this.confirmLoading = false
                })
            }
          }, 1000)
        }
      })
    },
    add () {
      this.edit({})
      // 设置默认值
      this.$nextTick(() => {
        this.form.setFieldsValue({
          trainAmount: 1,
          finishAmount: 0
        })
      })
    },
    edit (record) {
      this.visible = true
      this.form.resetFields()
      this.model = Object.assign({}, record)
      if (!everythingIsEmpty(record)) {
        this.assetTypeId = this.model.assetTypeId
        this.supplierId = this.model.supplierId
        this.handleLine(this.model.lineId)
      } else {
        this.assetTypeId = ''
      }
      if (!everythingIsEmpty(this.model.extInfo)) {
        this.$nextTick(() => {
          this.form.setFieldsValue({
            superInfo: this.model.extInfo.superInfo,
            trainInfo: this.model.extInfo.trainInfo
          })
        })
      }
      if (this.model.id) {
        getContractFiles({
          contractId: this.model.id
        }).then((res) => {
          if (res.success && res.result.length) {
            this.annexList = res.result || []
          }
        })
        this.$nextTick(() => {
          this.form.setFieldsValue({
            amount: this.model.amount,
            contractName: this.model.contractName,
            contractNo: this.model.contractNo,
            finishDate: this.model.finishDate ? moment(this.model.finishDate) : '',
            signDate: moment(this.model.signDate || undefined),
            startDate: this.model.startDate ? moment(this.model.startDate) : '',
            status: this.model.status,
            taxRate: this.model.taxRate,
            assetTypeName: this.model.assetTypeName,
            attention: this.model.attention,
            deposit: this.model.deposit,
            expiration: this.model.expiration,
            lineId: this.model.lineId,
            supplierName: this.model.supplierName,
            remark: this.model.remark,
            needDay: this.model.needDay,
            qualityDepositPercent: this.model.qualityDepositPercent,
            depositType: this.model.depositType,
            advancePayment: this.model.advancePayment,
            assetAmount: this.model.assetAmount,
            executeTaxRate: this.model.executeTaxRate,
            trainAmount: this.model.trainAmount,
            finishAmount: this.model.finishAmount,
            repairProgramId: this.model.repairProgramId,
            qualityDepositType: this.model.qualityDepositType,
            payInterval: this.model.payInterval,
            payBegin: this.model.payBegin,
            assetTypeAlias:this.model.assetTypeAlias,
          })
        })
      }
    },
    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
      this.annexList = []
    }
  }
}
</script>

<style scoped lang="less">
.content {
  .info-wrapper {
    border: 1px solid #eee;
    position: relative;
    border-radius: 8px;
    padding: 10px;
    margin-top: 10px;
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

  .ant-form-item {
    margin: 0;
  }
}
</style>