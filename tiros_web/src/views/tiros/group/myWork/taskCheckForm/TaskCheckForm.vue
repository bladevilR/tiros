<template>
  <div na-flex-height-full>
    <a-space v-if='!isSelectDetaile' style='padding: 10px 0'>
      <a-button @click='setCheckRecordTime'>作业完成</a-button>
      <a-button @click='openSetCheck' v-has="'order:write:check:add'">检查登记</a-button>
      <a-button @click='openRectifyModal' v-has="'order:write:rectify:add'">添加整改</a-button>
    </a-space>
    <div na-flex-height-full>
      <vxe-table
        border
        auto-resize
        max-height='auto'
        row-id='id'
        ref='listTable'
        align='center'
        :data='tableData'
        show-overflow='tooltip'
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
      >
        <vxe-table-column type='checkbox' width='40'></vxe-table-column>
        <vxe-table-column field='sortNo' title='序号' align='center' width='60'></vxe-table-column>
        <vxe-table-column field='title' title='项点' header-align='center' align='left' width='160'></vxe-table-column>
        <vxe-table-column field='content' title='检查内容' header-align='center' align='left'></vxe-table-column>
        <vxe-table-column field='checkLevel' title='等级' align='center' width='80'></vxe-table-column>
        <vxe-table-column v-if='!isSelectDetaile' field='checkDesc' title='检查情况' align='center'
                          width='80'></vxe-table-column>
        <vxe-table-column v-if='!isSelectDetaile' field='checkResult_dictText' title='结果' align='center'
                          width='80'></vxe-table-column>
        <vxe-table-column v-if='!isSelectDetaile' field='workTime' title='作业时间' align='center'
                          width='115'></vxe-table-column>
        <vxe-table-column v-if='!isSelectDetaile' field='checkMethod_dictText' title='检查方式' align='center'
                          width='80'></vxe-table-column>
        <vxe-table-column v-if='!isSelectDetaile' field='checkUserName' title='检查人' align='center'
                          width='80'></vxe-table-column>
        <vxe-table-column v-if='!isSelectDetaile' field='checkTime' title='检查时间' align='center'
                          width='115'></vxe-table-column>
      </vxe-table>
    </div>
    <a-modal
      title='检查'
      :width='800'
      :visible='visible'
      :confirmLoading='confirmLoading'
      centered
      @ok='handleOk'
      @cancel='handleCancel'
      cancelText='关闭'
      :destroyOnClose='true'
    >
      <a-form :form='form' ref='initForm'>
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='登录名称'>
          <a-row :gutter='10'>
            <a-col :span='14'>
              <a-input v-decorator.trim="['checkUserName', validatorRules.checkUserName]" />
            </a-col>
            <a-col :span='10'>
              <a-button type='link' @click='useCurrentUser'>使用当前账号和密码</a-button>
            </a-col>
          </a-row>
        </a-form-item>
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='登录密码'>
          <a-input-password v-decorator.trim="['checkUserPwd', validatorRules.checkUserPwd]" />
        </a-form-item>
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='检查方式'>
          <j-dict-select-tag
            triggerChange
            v-decorator="['checkMethod', validatorRules.checkMethod]"
            placeholder='请选择检查方式'
            dictCode='bu_regu_method'
          />
        </a-form-item>
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='检查结果'>
          <a-checkbox v-model='checkResult'> 合格 ?</a-checkbox>
        </a-form-item>
        <!-- <a-row>
          <a-col :span="12">

          </a-col>
          <a-col :span="12">

          </a-col>
        </a-row> -->
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='检查时间'>
          <a-date-picker
            format='YYYY-MM-DD HH:mm:ss'
            :show-time="{ format: 'HH:mm:ss', hideDisabledOptions: true }"
            v-model='checkQueryParam.checkTime'
            style='width: 100%'
          />
        </a-form-item>
        <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='结果描述'>
          <a-textarea v-model='checkQueryParam.checkDesc' :auto-size='{ minRows: 8, maxRows: 8 }'></a-textarea>
        </a-form-item>
      </a-form>
    </a-modal>
    <rectify-modal ref='modalForm'></rectify-modal>
    <WorkTimeModal ref="workTimeModal" @ok="findList()"></WorkTimeModal>
  </div>
</template>

<script>
import RectifyModal from '@views/tiros/quality/modules/RectifyModal'
import WorkTimeModal from '@views/tiros/group/myWork/taskCheckForm/WorkTimeModal'
import { getCheckRecordDetail, getTrainPlanRecordFormById, saveCheckRecord, saveCheckTime } from '@api/tirosGroupApi'
import { getSerialNumber, getWorkcheck } from '@/api/tirosApi'
import moment from 'moment'

export default {
  name: 'TaskCheckForm',
  components: { WorkTimeModal, RectifyModal },
  props: {
    taskFormsList: {
      type: Array,
      default: () => {
        return []
      }
    },
    tasks: {
      type: Array,
      default: () => {
        return []
      }
    },
    readOnly: {
      type: Boolean,
      default: false
    },
    selectTask: {
      type: Object,
      default: null
    },
    workOrderInfo: {
      type: Object,
      default: () => {
      }
    },
    forms: {
      type: Array,
      default: () => {
        return []
      }
    },
    // 0: 检查表单 1：明细选择
    modalType: {
      type: Number,
      default: 0
    }
  },
  computed: {
    isSelectDetaile() {
      return this.modalType === 1
    }
  },
  mounted() {
    // console.log(this.selectTask)
  },
  data() {
    return {
      visible: false,
      confirmLoading: false,
      form: this.$form.createForm(this),
      labelCol: {
        xs: { span: 24 },
        sm: { span: 3 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 20 }
      },
      labelCol2: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol2: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      validatorRules: {
        checkUserName: { rules: [{ required: true, message: '请输入登录名称!' }] },
        checkUserPwd: { rules: [{ required: true, message: '请输入密码!' }] },
        checkMethod: { rules: [{ required: true, message: '请选择!' }] }
      },
      tableData: [],
      queryParam: {
        formInstId: '',
        task2InstId: ''
      },
      checkQueryParam: {
        checkUserName: '',
        checkUserPwd: '',
        itemIds: '',
        checkResult: 1,
        checkDesc: '',
        checkMethod: null,
        checkTime: undefined
      },
      checkResult: true
    }
  },
  methods: {
    async findList(task2InstId, formInstId) {
      if (formInstId) {
        this.queryParam.formInstId = formInstId
      }
      if (task2InstId) {
        this.queryParam.task2InstId = task2InstId
      }
      return getCheckRecordDetail(this.queryParam).then((res) => {
        if (res.success) {
          return this.tableData = res.result.sort((a, b) => a.sortNo - b.sortNo)
        } else {
          this.$message.error(res.message)
          return []
        }
      })
    },
    findModelDetaile(row) {
      getWorkcheck({
        id: row.formObjId
      }).then((res) => {
        if (res.success) {
          this.tableData = res.result.itemList.sort((a, b) => a.sortNo - b.sortNo)
        } else {
          this.$message.error(res.message)
        }
      })
    },

    // 保存检查结果
    setCheckRecordTime() {
      let ids = this.$refs.listTable.getCheckboxRecords().map((e) => e.id)
      if (ids.length) {
        // this.$confirm({
        //   content: `是否执行选中${ids.length}条数据？`,
        //   okText: '确定',
        //   cancelText: '取消',
        //   onOk: () => {
        //     let formData = new FormData()
        //     formData.append('itemIds', ids)
        //     saveCheckTime(formData).then((res) => {
        //       if (res.success) {
        //         this.$message.success('设置成功')
        //         this.findList()
        //         this.$emit('checkEnd')
        //       } else {
        //         this.$message.error(res.message)
        //       }
        //     })
        //   }
        // })
        this.$refs.workTimeModal.show(ids);
      } else {
        this.$message.warn('请选择作业项目')
      }
    },
    async openSetCheck() {
      let isOk = await this.checkTaskCanSubmit(1)
      if (!isOk.success) {
        this.$message.warn(`${isOk.formsName}未完成，请检查！`)
        this.tabFormKey = this.taskFormsList.findIndex((e) => e == isOk.checkFomrs[0])
        this.$emit('taskFormNoWork', this.tabFormKey)
        return
      }
      let ids = this.$refs.listTable.getCheckboxRecords().map((e) => e.id)
      if (ids.length) {
        this.visible = true
        this.checkQueryParam.checkUserName = ''
        this.checkQueryParam.checkUserPwd = ''
        this.checkQueryParam.itemIds = ids.join(',')
        this.checkQueryParam.checkResult = 1
        this.checkQueryParam.checkDesc = ''
        this.checkQueryParam.type = null
        this.isIgnore = false
        this.resultCheck = true
        this.$nextTick(() => {
          this.form.getFieldDecorator('resultDesc')
          this.form.setFieldsValue({
            checkUserName: this.$store.getters.userInfo.username,
            checkUserPwd: '~**&&##@@$$%%^^~',
            checkDesc: '',
            type: null
          })
        })
      } else {
        this.$message.warn('请选择作业项目')
      }
    },
    // 作业检查保存
    handleOk() {
      this.form.validateFields((err, values) => {
        if (!err) {
          Object.assign(this.checkQueryParam, values)
          this.checkQueryParam.checkResult = this.checkResult ? 1 : 0
          if (this.checkQueryParam.checkTime) {
            this.checkQueryParam.checkTime = moment(this.checkQueryParam.checkTime).format('YYYY-MM-DD HH:mm:ss')
          } else {
            this.checkQueryParam.checkTime = null
          }
          saveCheckRecord(this.checkQueryParam).then((res) => {
            if (res.success) {
              this.$message.success('保存成功')
              this.visible = false
              this.findList()
              this.$emit('checkEnd')
            } else {
              this.$message.error(res.message)
            }
          })
        }
      })
    },
    handleCancel() {
      this.visible = false
    },
    useCurrentUser() {
      this.$nextTick(() => {
        this.form.setFieldsValue({
          checkUserName: this.$store.getters.userInfo.username,
          checkUserPwd: '~**&&##@@$$%%^^~' // 为固定值，后端通过判断该值来确定是自检，且没有修改用户名和密码
        })
      })
    },
    openRectifyModal() {
      let contents = this.$refs.listTable.getCheckboxRecords().map((e) => e.content)
      if (contents.length) {
        getSerialNumber({ moduleCode: 'JDXZG' }).then((res) => {
          if (res.success) {
            // this.selectTask.orderId, this.selectTask.id, contents.join(','), res.result
            this.$refs.modalForm.addByRow({
              orderId: this.selectTask.orderId,
              orderName: this.workOrderInfo.orderName,
              depotId: this.workOrderInfo.depotId,
              orderTaskId: this.selectTask.id,
              remark: contents.join(','),
              rectifyNo: res.result,
              groupId: this.workOrderInfo.groupId,
              lineId: this.workOrderInfo.lineId,
              lineName: this.workOrderInfo.lineName,
              trainNo: this.workOrderInfo.trainNo,
              groupName: this.workOrderInfo.groupName,
              status: '0'
            })
            this.$refs.modalForm.title = '新增'
          } else {
            this.$message.error(res.message)
          }
        })
      } else {
        this.$message.warn('请选择作业项目')
      }
    },
    onIgnoreChange() {
      this.$nextTick(() => {
        this.form.resetFields('resultDesc')
      })
    },
    // 保存前检查作业表单是否完成
    async checkTaskCanSubmit(checkType = 1) {
      // checkType 1：作业记录表  2：作业检查表
      let isOk = true
      let result = {
        success: true,
        forms: [],
        records: [],
        checkFomrs: []
      }
      let list = []
      if (
        !(
          this.selectTask.outTask === 0 &&
          (this.selectTask.taskType === 1 ||
            this.selectTask.taskType === 3 ||
            this.selectTask.taskType === 4 ||
            this.selectTask.taskType === 5)
        )
      ) {
        return result
      }
      this.taskFormsList.forEach((element) => {
        if (element.instType === 3) {
          if (checkType === 1) {
            list.push(
              getTrainPlanRecordFormById({
                task2InstId: element.id,
                formInstId: element.formInstId,
                needCategory: false,
                needChecks: true,
                orderTaskId: this.selectTask.id
              }).then((res) => {
                if (res.success && res.result) {
                  result.forms.push(res.result)
                  return res.result.detailList
                } else {
                  this.$message.error('获取表单作业明细失败')
                  return []
                }
              })
            )
          }
        }
        if (element.instType === 4) {
          if (checkType === 2) {
            list.push(
              getCheckRecordDetail({
                task2InstId: element.id,
                formInstId: element.formInstId
              }).then((res) => {
                result.forms.push(res.element)
                if (res.success && res.result) {
                  return res.result
                } else {
                  this.$message.error('获取表单作业明细失败')
                  return []
                }
              })
            )
          }
        }
      })
      return Promise.all(list).then((detailList) => {
        detailList.forEach((detailInfos) => {
          if (checkType === 1) {
            for (let index = 0; index < detailInfos.length; index++) {
              const element = detailInfos[index]
              if (!element.isIgnore || element.isIgnore !== 1) {
                if (!element.selfCheck || !element.monitorCheck || !element.guarderCheck) {
                  // 检查不通过
                  result.success = false
                  isOk = false
                  result.records.push(element)
                }
              }
            }
            if (result.forms.length > 0) {
              let checkFomrs = []
              result.records.forEach((element) => {
                if (!checkFomrs.find((e) => e.formInstId === element.workRecordId)) {
                  checkFomrs.push(this.taskFormsList.find((f) => f.formInstId === element.workRecordId))
                }
              })
              result.checkFomrs = checkFomrs
              result.formsName = checkFomrs.map((e) => `《${e.formName}》`).toString()
            }
          }
          if (checkType === 2) {
            for (let index = 0; index < detailInfos.length; index++) {
              const element = detailInfos[index]
              if (!element.checkUserId || !element.workTime) {
                // 检查不通过
                result.success = false
                isOk = false
                result.records.push(element)
              }
            }
            if (result.records.length > 0) {
              let checkFomrs = []
              result.records.forEach((element) => {
                if (!checkFomrs.find((e) => e.formInstId === element.checkId)) {
                  checkFomrs.push(this.taskFormsList.find((e) => e.formInstId === element.checkId))
                }
              })
              result.checkFomrs = checkFomrs
              result.formsName = checkFomrs.map((e) => `《${e.formName}》`).toString()
            }
          }
        })
        return result
      })
    },
    getIDRecords() {
      return this.$refs.listTable.getCheckboxRecords().map(e => e.id).toString()
    }
  }
}
</script>

<style>
</style>