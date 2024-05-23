<template>
  <j-modal
    :title="title"
    :width="900"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    fullscreen
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col style="border-right: 1px solid #f4f4f4" :md="12" :sm="24">
            <a-row :gutter="24">
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="合同">
                  <j-dict-select-tag
                    :trigger-change="true"
                    v-decorator.trim="['contractId']"
                    dictCode="bu_contract_info,contract_name,id,status<>2"
                    placeholder="选择合同"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="培训厂商">
                  <a-input :maxLength="33" placeholder="输入培训厂商" v-decorator.trim="['supplierName', validatorRules.supplierName]" />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="标题">
                  <a-input :maxLength="33" placeholder="输入标题" v-decorator.trim="['title', validatorRules.title]" />
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="培训天数" required>
                  <a-input-number style="width: 100%" placeholder="输入培训天数" v-model="days" :min="0" :max="9999"/>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开始日期" required>
                  <a-date-picker style="width: 100%" placeholder="选择开始日期" v-model="startDate" />
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结束日期" required>
                  <a-input v-model="finishDate" disabled placeholder="自动计算" />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="培训人次">
                  <a-input-number
                    style="width: 80%"
                    :max="999999999"
                    placeholder="输入培训人次"
                    v-decorator.trim="['manTimes', validatorRules.manTimes]"
                  />
                  人次
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="24" :sm="24">
                <a-form-item :labelCol="rLabelCol" :wrapperCol="wrapperCol" label="备注说明">
                  <a-textarea
                    :maxLength="201"
                    v-decorator.trim="['remark',validatorRules.remark]"
                    :auto-size="{ minRows: 3, maxRows: 5 }"
                    placeholder="输入备注说明"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-divider orientation="left" style="font-size: 14px">上传附件</a-divider>
            <a-row :gutter="24">
              <a-col :md="18" :sm="24">
                <DocUpload
                  style="padding: 0 20px"
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
              </a-col>
            </a-row>
            <a-divider orientation="left" style="font-size: 14px">已上传列表</a-divider>
            <a-col :md="24" :sm="24">
              <vxe-table
                style="margin-top: 12px"
                border
                :data="annexList"
                show-overflow="tooltip"
                :edit-config="{ trigger: 'manual', mode: 'row' }"
              >
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
              <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
            </a-col>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-row :gutter="24">
              <a-col :md="24" :sm="24">
                <a-form-item :labelCol="rLabelCol" :wrapperCol="wrapperCol" label="人员列表">
                  <a-button style="margin-right: 6px" type="primary" @click="openSelectUser">新增</a-button>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="24" :sm="24">
                <vxe-table
                  border
                  :data="usersList"
                  show-overflow="tooltip"
                  :edit-config="{ trigger: 'manual', mode: 'row' }"
                >
                  <vxe-table-column field="workNo" title="工号" header-align="center" align="center">
                    <!-- <template #default="{ row }">
                      <a-input
                        ref="userSelect"
                        v-model="row.workNo"
                        placeholder="请选择人员"
                        style="width: 100%"
                        @click="openSelectUser(row)"
                      >
                        <a-icon slot="suffix" type="ellipsis" />
                      </a-input>
                    </template> -->
                  </vxe-table-column>
                  <vxe-table-column field="realName" title="名称" header-align="center" align="center">
                    <!-- <template #default="{ row }">
                      <a-input
                        ref="userSelect"
                        v-model="row.realName"
                        placeholder="请选择人员"
                        style="width: 100%"
                        @click="openSelectUser(row)"
                      >
                        <a-icon slot="suffix" type="ellipsis" />
                      </a-input>
                    </template> -->
                  </vxe-table-column>
                  <vxe-table-column field="groupName" title="班组" header-align="center" align="center">
                    <!-- <template #default="{ row }">
                      <a-input style="width: 100%" v-model="row.groupName" placeholder="选择人员" disabled></a-input>
                    </template> -->
                  </vxe-table-column>
                  <vxe-table-column title="操作" width="100px" header-align="center" align="center">
                    <template v-slot="{ rowIndex }">
                      <a @click="delUserItem(rowIndex)">删除</a>
                    </template>
                  </vxe-table-column>
                </vxe-table>
                <user-list ref="userSelectModal" :multiple="true" @ok="onSelectUser"></user-list>
              </a-col>
            </a-row>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>
import moment from 'moment'
import { isPrivilege } from '@/api/tirosApi'
import { addTrainingRecordItem, editTrainingRecordItem } from '@/api/tirosOutsourceApi'
import DocUpload from '@views/tiros/common/doc/DocUpload'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import { everythingIsEmpty } from '@/utils/util'
// import { addFile } from '@api/tirosApi'
import { randomUUID } from '@/utils/util'
import UserList from '@views/tiros/common/selectModules/UserList'

export default {
  name: 'TrainingRecordItemModel',
  components: { DocUpload, DocPreviewModal, UserList },
  data() {
    return {
      title: '操作',
      visible: false,
      model: {},
      startDate: '',
      days: 0,
      defaultFileList: [],
      annexList: [],
      usersList: [],
      curRow: {},
      filePath: '',
      fileName: '',
      saveFlag: true,
      uploading: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 },
      },
      rLabelCol: {
        xs: { span: 24 },
        sm: { span: 3 },
      },
      rWrapperCol: {
        xs: { span: 24 },
        sm: { span: 21 },
      },
      status: true,
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        supplierName: { rules: [{ max: 32, message: '输入长度不能超过32字符!' }] },
        remark: { rules: [{ max: 200, message: '输入长度不能超过200字符!' }] },
        title: {
          rules: [{ required: true, message: '请输入厂商名称!' },{ max: 32, message: '输入长度不能超过32字符!' }],
        },
        days: {
          rules: [{ required: true, message: '请输入培训天数!' }],
        },
        startDate: {
          rules: [{ required: true, message: '请选择开始日期!' }],
        },
        finishDate: {
          rules: [{ required: true, message: '请选择开始日期和培训天数!' }],
        },
        manTimes: {
          rules: [{ required: true, message: '请输入培训人次!' }],
        },
      },
    }
  },
  computed: {
    finishDate: {
      get() {
        if (this.startDate) {
          let date = new Date(this.startDate).getTime() + 1000 * 60 * 60 * 24 * (this.days-1>=0?this.days-1:this.days)
          return moment(date).format('YYYY-MM-DD')
        } else {
          return ''
        }
      },
      set(val) {},
    },
  },
  created() {},
  mounted() {},
  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      if (record.id) {
        this.$nextTick(() => {
          this.form.setFieldsValue({
            contractId: this.model.contractId,
            supplierName: this.model.supplierName,
            title: this.model.title,
            manTimes: this.model.manTimes,
            remark: this.model.remark,
          })
          this.days = this.model.days
          this.startDate = this.model.startDate
          this.finishDate = this.model.finishDate
          this.usersList = this.model.trainingUsersList || []
          this.annexList = this.model.trainingAnnexList || []
        })
      }
    },
    setBforeUploadStatus(val) {
      this.saveFlag = val
    },
    setUpLoadingEndStatus(val) {
      this.saveFlag = val
    },
    successUploadFile(fileInfos) {
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
    uploadFail(file) {
      this.confirmLoading = false
    },
    onRemoveFile(file) {},
    beginUpload() {
      this.confirmLoading = true
      this.$refs.upload.beginUpload()
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
        this.fileName = data.fileName
        this.$refs.docPreview.handleFilePath(data.savepath)
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
        onCancel() {},
      })
    },
    // addUser() {
    //   this.trainingUsersList.push({
    //     id: '',
    //   })
    // },
    openSelectUser() {
      this.$refs.userSelectModal.showModal()
    },
    onSelectUser(data) {
      let that = this
      if (data.length) {
        data.forEach((item) => {
          if (that.usersList) {
            for (let i = 0; i < that.usersList.length; i++) {
              const userItem = that.usersList[i]
              if (userItem.userId == item.id) {
                that.$message.warning(`${item.realname}已存在列表中！`)
                return false
              }
            }
            that.usersList.push({
              userId: item.id,
              realName: item.realname,
              groupName: item.orgCode,
              workNo: item.workNo,
            })
          } else {
            that.usersList.push({
              userId: item.id,
              realName: item.realname,
              groupName: item.orgCode,
              workNo: item.workNo,
            })
          }
        })
        // this.curRow.userId = data[0].id
        // this.curRow.userName = data[0].realname
        // this.curRow.groupName = data[0].orgCode
        // this.curRow.workNo = data[0].workNo
        this.$forceUpdate()
      }
    },
    delUserItem(index) {
      this.usersList.splice(index, 1)
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {

          if (this.days && this.startDate && this.finishDate && this.usersList.length > 0) {
            that.confirmLoading = true
            let formData = Object.assign(that.model, values, {
              days: that.days,
              startDate: that.startDate,
              finishDate: that.finishDate,
              trainingUsersList: that.usersList,
            })
            this.beginUpload()
            let timer = null
            clearInterval(timer)
            timer = setInterval(() => {
              if (that.saveFlag) {
                formData['trainingAnnexList'] = that.annexList
                let obj
                if (!that.model.id) {
                  obj = addTrainingRecordItem(formData)
                } else {
                  obj = editTrainingRecordItem(formData)
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
          } else if(this.days==0){
            that.$message.warning('培训时间不能为0！')
          } else {
            that.$message.warning('请完善基础信息或人员列表！')
          }
        }
      })
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.usersList = []
      this.annexList = []
      this.visible = false
    },
  },
}
</script>