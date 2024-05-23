<template>
  <j-modal
    :title="title"
    centered
    :width="1100"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    fullscreen
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <div style="width: 100%; overflow: hidden">
        <a-form :form="form">
          <a-row :gutter="24">
            <a-col :md="23" :sm="24">
              <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="任务名称">
                <a-input allowClear placeholder="请输入" v-decorator.trim="['title', validatorRules.title]" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col :md="8" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="编码">
                <a-input placeholder="请输入" disabled v-decorator.trim="['rectifyNo', validatorRules.rectifyNo]" />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="整改类型">
                <j-dict-select-tag
                  :triggerChange="true"
                  v-decorator.trim="['rectifyType', validatorRules.rectifyType]"
                  dictCode="bu_work_rectify_type"
                />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="下发日期" width="40">
                <a-date-picker v-decorator.trim="['sendDate', validatorRules.sendDate]" style="width: 100%" />
              </a-form-item>
            </a-col>
            <!-- </a-row>
          <a-row :gutter="24"> -->
            <a-col :md="8" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="责任班组">
                <j-dict-select-tag
                  :triggerChange="true"
                  v-decorator.trim="['groupId', validatorRules.groupId]"
                  :dictCode="dictGroupStr"
                  @change="changeGroup"
                />
              </a-form-item>
            </a-col>
            <!-- <a-col :md="8" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="责任人员">
                <a-select
                  placeholder="请选择保管人员"
                  :open="false"
                  :showArrow="true"
                  v-decorator.trim="['dutyUserName', validatorRules.dutyUserName]"
                  @focus="openuserModal"
                  ref="myuserSelect"
                >
                  <a-icon slot="suffixIcon" type="ellipsis" />
                </a-select>
              </a-form-item>
            </a-col> -->
            <a-col :md="8" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="整改状态">
                <j-dict-select-tag
                  :triggerChange="true"
                  v-decorator.trim="['status', validatorRules.status]"
                  dictCode="bu_work_rectify_status"
                />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆段">
                <j-dict-select-tag
                  :triggerChange="true"
                  v-decorator.trim="['depotId', validatorRules.depotId]"
                  dictCode="bu_mtr_depot,name,id"
                />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
                <line-select-list @change="lineIdChange" v-decorator="['lineId', validatorRules.lineId]">
                </line-select-list>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属车辆">
                <j-dict-select-seach-tag
                  triggerChange
                  :dictCode="dictTrainStr"
                  v-decorator="['trainNo', validatorRules.trainNo]"
                />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联工单">
                <!--              <j-dict-select-tag
                  :triggerChange="true"
                  v-decorator.trim="['orderId', validatorRules.orderId]"
                  dictCode="bu_work_order,order_name,id"
                  @change="changeOrder"
                />-->
                <span @click="openOrderModel">
                  <a-select
                    allow-clear
                    placeholder="请选择工单"
                    :open="false"
                    :showArrow="true"
                    v-decorator.trim="['orderName', validatorRules.orderName]"
                    @change="orderNameChange"
                    ref="myOrderSelect"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                </span>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="整改工位">
                <j-dict-select-tag
                  :triggerChange="true"
                  v-decorator.trim="['workstationId', validatorRules.workstationId]"
                  :dictCode="dictCodeStr"
                />
              </a-form-item>
            </a-col>
            <!-- </a-row>
          <a-row :gutter="24"> -->
            <a-col :md="8" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="整改工序">
                <j-dict-select-tag
                  :triggerChange="true"
                  v-decorator.trim="['orderTaskId', validatorRules.orderTaskId]"
                  :dictCode="dictCodeStrOrder"
                />
              </a-form-item>
            </a-col>
          </a-row>
          <!-- <a-row :gutter="24"> </a-row> -->
          <a-row :gutter="24">
            <a-col :md="23" :sm="24">
              <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="备注说明">
                <a-textarea placeholder="请输入" v-decorator.trim="['remark', validatorRules.remark]" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col :md="23" :sm="24">
              <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="附件">
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
              </a-form-item>
            </a-col>
          </a-row>
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
              type="seq"
              title="序号"
              width="50px"
              header-align="center"
              align="center"
            ></vxe-table-column>
            <vxe-table-column field="title" title="文件名" header-align="left" align="left"></vxe-table-column>
            <vxe-table-column title="操作" width="160px" header-align="center" align="center">
              <template v-slot="{ row, rowIndex }">
                <a style="margin-right: 12px" @click.stop="handleSeeing(row)">查看</a>
                <a @click.stop="handleDelete(row, rowIndex)">删除</a>
              </template>
            </vxe-table-column>
          </vxe-table>
          <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
        </a-form>
      </div>
    </a-spin>
    <user-list ref="UserModalForm" :multiple="true" @ok="addTarget"></user-list>
    <work-order-select :order-status="[2]" ref="orderSelect" @ok="onSelectOrder"></work-order-select>
  </j-modal>
</template>

<script>
import UserList from '../../common/selectModules/UserList'
import moment from 'moment'
import { addRectify, editRectify, getRectifyCode } from '@api/tirosQualityApi'
import { addFile } from '@api/tirosApi'
import { everythingIsEmpty, randomUUID } from '@/utils/util'
import DocUpload from '@views/tiros/common/doc/DocUpload'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { isPrivilege } from '@/api/tirosApi'
import WorkOrderSelect from '@views/tiros/common/selectModules/WorkOrderSelect'
export default {
  name: 'RectifyModal',
  // props:['defaultFileList'],
  props: {
    defaultFileList: {
      type: Array,
      default: () => [],
    },
  },
  components: { UserList, DocUpload, DocPreviewModal, LineSelectList, WorkOrderSelect },
  data() {
    return {
      dictTrainStr: '',
      dictGroupStr:
        "bu_mtr_workshop_group,group_name,id,workshop_id='" + this.$store.getters.userInfo.workshopId + "'|workshop_id",
      fileList: [],
      annexList: [],
      rectifyId: '',
      filePath: '',
      fileName: '',
      status: false,
      uploading: false,
      dictCodeStr: 'bu_mtr_workstation,name,id',
      dictCodeStrOrder: '',
      title: '整改项信息',
      visible: false,
      model: {},
      saveFlag: true,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 13 },
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 2 },
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 22 },
      },
      labelCol2: {
        xs: { span: 24 },
        sm: { span: 2 },
      },
      wrapperCol2: {
        xs: { span: 24 },
        sm: { span: 19 },
      },
      dutyUsername: '',
      dutyUserId: '',
      isClose: false,
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        rectifyNo: {
          rules: [
            { required: true, message: '请输入编号!' },
            { max: 32, message: '输入长度不能超过32字符!' },
          ],
        },
        rectifyType: { rules: [{ required: true, message: '请选择整改类型!' }] },
        orderName: { rules: [] },
        groupId: { rules: [{ required: true, message: '请选择责任班组!' }] },
        // dutyUserName: { rules: [{ required: true, message: '请选择责任人员!' }] },
        workstationId: { rules: [] },
        sendDate: { rules: [{ required: true, message: '请选择下发日期!' }] },
        status: { rules: [{ required: true, message: '请选择整改状态!' }] },
        lineId: { rules: [{ required: true, message: '请选择线路!' }] },
        trainNo: { rules: [{ required: true, message: '请选择车辆!' }] },
        depotId: { rules: [] },
        title: {
          rules: [
            { required: true, message: '请输入整改标题!' },
            { max: 32, message: '输入长度不能超过32字符!' },
          ],
        },
        remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] },
        orderTaskId: { rules: [] },
      },
      workOrderId: '',
      rectifyNo:'',
    }
  },
  created() {},
  methods: {
    orderNameChange(value){
      if(!value){
        this.workOrderId = '';
        this.form.setFieldsValue({ orderTaskId: '' });
        this.changeOrder();
      }
    },
    lineIdChange(newVal) {
      this.form.setFieldsValue({
        trainNo: '',
      })
      if (newVal) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + newVal
      } else {
        this.dictTrainStr = ''
      }
    },
    openOrderModel() {
      this.$refs.orderSelect.showModal()
    },
    onSelectOrder(data) {
      if (!everythingIsEmpty(data)) {
        this.form.setFieldsValue({ orderName: data[0].orderName })
        this.workOrderId = data[0].id
        this.changeOrder(data[0].id)
      }
    },
    changeGroup(data) {
      console.log(data)
      if (data) {
        this.dictCodeStr =
          "bu_mtr_workstation,name,id,id in (select workstation_id from bu_group_workstation where group_id = '" +
          data +
          "')"
      } else {
        this.dictCodeStr = 'bu_mtr_workstation,name,id'
      }
    },
    changeOrder(data) {
      if (data) {
        this.dictCodeStrOrder = "bu_work_order_task,task_name,id,order_id='" + data + "'"
      } else {
        this.dictCodeStrOrder = ''
      }
    },
    changeClose(checked) {
      this.isClose = checked
    },
    async  add(orderId, taskId, remark, rectifyNo) {
     await this.getRectifyNo()
      if (orderId) {
        this.changeOrder(orderId)
      }
      this.lineIdChange()
      this.edit({
        orderId: orderId,
        orderTaskId: taskId,
        remark: remark,
        rectifyNo: this.rectifyNo,
      })
    },
   async  getRectifyNo() {
     await getRectifyCode().then((res)=>{
        this.rectifyNo = res.result
      })
    },
    // 来自作业检查表整顿功能调用
    addByRow(row) {
      if (row.orderId) {
        this.workOrderId = row.orderId
        this.changeOrder(row.orderId)
      }
      this.edit(row)
    },
    edit(record) {
      this.confirmLoading = false
      if (record.id) {
        this.isClose = record['close'] && record['close'] == 1 ? true : false
        this.workOrderId = record.orderId
        this.changeOrder(this.workOrderId)
        if (record.annexList != null && record.annexList.length > 0) {
          this.annexList = record.annexList
        }
      } else {
        this.isClose = false
      }
      this.dutyUserId = record.dutyUserId || ''
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          rectifyNo: this.model.rectifyNo,
          rectifyType: this.model.rectifyType,
          groupId: this.model.groupId,
          workstationId: this.model.workstationId,
          lineId: this.model.lineId,
          status: this.model.status,
          sendDate: moment(this.model.sendDate || new Date(), 'YYYY-MM-DD'),
          depotId: this.model.depotId,
          title: this.model.title,
          remark: this.model.remark,
          orderTaskId: this.model.orderTaskId,
          orderName: this.model.orderName,
        })
        this.lineIdChange(this.model.lineId)
        this.form.setFieldsValue({
          trainNo: this.model.trainNo,
        })
      })
    },

    openuserModal() {
      this.$refs.UserModalForm.showModal()
      this.$refs.myuserSelect.blur()
    },

    addTarget(data) {
      if (data.length) {
        this.dutyUsername = data[0].realname
        this.dutyUserId = data[0].id
      }
      this.form.setFieldsValue({
        dutyUserName: this.dutyUsername,
      })
    },
    successUploadFile(fileInfos) {
      if (!fileInfos || fileInfos.length < 1) {
        return
      }
      fileInfos.map((item) => {
        Object.assign(item, { id: randomUUID() })
      })
      addFile(fileInfos).then((res) => {
        if (res.success) {
          fileInfos.map((item) => {
            this.annexList.push({ address: item.savepath, id: item.id, title: item.name, rectifyId: this.rectifyId })
          })
        }
      })
    },
    uploadFail(file) {
      this.confirmLoading = false
    },
    onRemoveFile(file) {},
    beginUpload() {
      this.confirmLoading = true
      this.$refs.upload.beginUpload()
    },
    setBforeUploadStatus(val) {
      this.saveFlag = val
    },
    setUpLoadingEndStatus(val) {
      this.saveFlag = val
    },
    async handlePrivilege(id, operation) {
      let param = { id: id, operation: operation }
      await isPrivilege(param).then((res) => {
        this.status = res.result
      })
    },
    async handleSeeing(data) {
      await this.handlePrivilege(data.id, 2)
      if (!this.status) {
        this.$message.error('您没有权限!')
      } else {
        this.fileName = data.title
        this.$refs.docPreview.handleFilePath(data.address)
      }
    },
    async handleDelete(data, index) {
      // 删除文件
      let that = this
      that.$confirm({
        title: '提示',
        content: '确定删除该条附件吗？',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk() {
          that.annexList.splice(index, 1)
        },
        onCancel() {},
      })
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values, {
            sendDate: moment(values.sendDate).format('YYYY-MM-DD HH:mm:ss'),
            orderId: this.workOrderId,
          })
          if (!that.model.id) {
            that.rectifyId = null
          } else {
            that.rectifyId = that.model.id
          }
          this.beginUpload()
          let timer = null
          clearInterval(timer)
          timer = setInterval(() => {
            if (that.saveFlag) {
              formData['annexList'] = that.annexList
              formData['id'] = that.rectifyId
              formData['dutyUserId'] = that.dutyUserId
              let obj
              if (!that.model.id) {
                obj = addRectify(formData)
              } else {
                obj = editRectify(formData)
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
                  clearInterval(timer)
                  that.confirmLoading = false
                  that.close()
                })
            }
          }, 1000)
        }
      })
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.fileList = []
      this.annexList = []
      this.visible = false
    },
  },
}
</script>

<style scoped>
</style>