<template>
  <a-modal
    :title="title"
    width="90%"
    :visible="visible"
    :confirmLoading="confirmLoading"
    centered
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
    :bodyStyle="{ height: '80vh' }"
  >
    <div v-if="formObj.instType === 4" layout-flex style="height: 100%">
      <TaskCheckForm
        :taskFormsList="taskFormItemList"
        @taskFormNoWork="onTaskFormNoWork"
        :modalType="1"
        ref="checkForm"
        flex-full
      ></TaskCheckForm>
    </div>
    <div v-if="formObj.instType === 3" layout-flex style="height: 100%">
      <div flex-full>
        <vxe-table
          :stripe="true"
          border
          auto-resize
          max-height="100%"
          row-id="id"
          ref="recordListTable"
          align="center"
          :data="taskFormItemList"
          show-overflow="tooltip"
          :span-method="mergeRowMethod"
          :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        >
          <vxe-table-column type="checkbox" width="40"></vxe-table-column>
          <vxe-table-column type="index" title="序号" align="center" width="60"></vxe-table-column>
          <!--                    <vxe-table-column field="itemNo" title="序号" align="center" width="70"></vxe-table-column>-->
          <vxe-table-column field="reguTitle" title="作业项目" align="left" header-align="center" width="240"></vxe-table-column>
          <vxe-table-column field="workContent" title="检修内容" align="left"></vxe-table-column>
          <!-- <vxe-table-column field="result_dictText" title="结果" align="center" width="80">
            <template v-slot="{ row }">
              <span v-if="row.result === 1" style="color: red">异常</span>
              <span
                v-if="row.result === 0 && (row.selfCheck || row.guarderCheck || row.monitorCheck)"
                style="color: green"
                >正常</span
              >
            </template>
          </vxe-table-column>
          <vxe-table-column field="selfCheck" title="自检" align="center" width="80"></vxe-table-column>
          <vxe-table-column field="guarderCheck" title="互检" align="center" width="80"></vxe-table-column>
          <vxe-table-column field="monitorCheck" title="专检" align="center" width="80"></vxe-table-column>
          <vxe-table-column field="workInfo" title="作业情况" align="left" header-align="center">
            <template v-slot="{ row }">
              <span v-if="row.isIgnore === 1">{{ row.ignoreDesc }}</span>
              <span v-else>{{ row.workInfo }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="isIgnore_dictText" title="忽略？" align="center" width="80"></vxe-table-column> -->
        </vxe-table>
      </div>
    </div>
    <check-modal ref="checkModalForm" :title="checkModalTitle" @ok="onCheckRecord"></check-modal>
    <BreakdownModal ref="breakdownModal" :showWorkStation="true"></BreakdownModal>
  </a-modal>
</template>

<script>
import { getOldWorkRecord } from '@/api/tirosApi'
import TaskCheckForm from '@views/tiros/group/myWork/taskCheckForm/TaskCheckForm'
import checkModal from '@views/tiros/group/myWork/checkModal'
import { getTrainPlanRecordFormById } from '@/api/tirosGroupApi'
import { getWorkOrderDetail,deleteCheckRecord } from '@api/tirosDispatchApi'
import BreakdownModal from '@views/tiros/dispatch/breakdown/BreakdownModal'

export default {
  name: 'TaskFormDetaileModal',
  components: {
    TaskCheckForm,
    checkModal,
    BreakdownModal
  },
  props: {
    taskForms: {
      type: Array,
      default: () => {
        return []
      },
    },
    workOrderInfo: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  data() {
    return {
      title: '填写明细',
      createFaultLoading: false,
      switchCheck: true,
      visible: false,
      confirmLoading: false,
      model: {},
      taskFormItemList: [],
      formObj: {},
      checkModalTitle: '自检',
    }
  },
  methods: {
    add() {
      this.edit({})
    },
    showModal(record) {
      this.formObj = record
      this.taskFormItemList = []
      this.visible = true
      this.$nextTick(() => {
        if (record.instType === 3) {
          this.loadRecordFormDetail()
        } else if (record.instType === 4) {
          this.$refs.checkForm.findModelDetaile(record)
        }
      })
    },
    // 确定
    handleOk() {
      let result = ''
      if (this.formObj.instType === 3) {
        result = this.$refs.recordListTable.getCheckboxRecords().map(e => e.id).toString();
      } else if(this.formObj.instType === 4){
        result = this.$refs.checkForm.getIDRecords();
      }
      this.$emit('ok', result)
      this.close()
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    onTaskFormNoWork(data) {
      console.log('....');
    },
    // 获取作业记录明细
    loadRecordFormDetail() {
      // 获取作业记录表明细
      const params = {
        formInstId: this.formObj.formInstId,
        task2InstId: this.formObj.id,
        needChecks: true,
        needCategory: false,
      }
      // 8a8a483c7910f4f001791141fe6702dd
      getOldWorkRecord({
        id: this.formObj.formObjId
      }).then(res => {
        let detailes = []
        res.result.categoryList.forEach(element => {
          detailes = [...detailes, ...(element.detailList.map(e => {
            e.reguTitle = element.reguTitle
            e.recIndex = element.recIndex
            e.workRecId = element.workRecId
            return e
          }))]
        });
        this.taskFormItemList = detailes
        this.$nextTick(()=>{
          if (this.formObj.recordIds && this.formObj.recordIds.length) {
            let idList = this.formObj.recordIds.split(',')
            this.$refs.recordListTable.setCheckboxRow(this.taskFormItemList.filter(e => idList.find(r => r === e.id)), true)
          }
        })
      })
      // getTrainPlanRecordFormById(params)
      //   .then((res) => {
      //     if (res.success) {
      //       console.log('嘿嘿=2');
      //       console.log(res.result);
      //       // this.taskFormItemList = res.result.detailList
      //       // this.$nextTick(()=>{
      //       //   if (this.formObj.recordIds && this.formObj.recordIds.length) {
      //       //     let idList = this.formObj.recordIds.split(',')
      //       //     this.$refs.recordListTable.setCheckboxRow(this.taskFormItemList.filter(e => idList.find(r => r === e.id)), true)
      //       //   }
      //       // })
      //     } else {
      //       this.$message.error('获取作业记录表内容错误')
      //       console.error('获取作业记录表内容错误', res.message)
      //     }
      //   })
      //   .catch((err) => {
      //     this.$message.error('获取作业记录表内容异常')
      //     console.error('获取作业记录表内容异常', err)
      //   })
    },
    // 通用行合并函数（将相同多列数据合并为一行）
    mergeRowMethod({ row, _rowIndex, column, visibleData }) {
      const fields = ['reguTitle']
      const cellValue = row[column.property]
      if (cellValue && fields.includes(column.property)) {
        const prevRow = visibleData[_rowIndex - 1]
        let nextRow = visibleData[_rowIndex + 1]
        if (prevRow && prevRow[column.property] === cellValue) {
          return { rowspan: 0, colspan: 0 }
        } else {
          let countRowspan = 1
          while (nextRow && nextRow[column.property] === cellValue) {
            nextRow = visibleData[++countRowspan + _rowIndex]
          }
          if (countRowspan > 1) {
            return { rowspan: countRowspan, colspan: 1 }
          }
        }
      }
    },
    // 检查
    handleCheck(type, title) {
      let selectRecords = this.$refs.recordListTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let idsStr = selectRecords
          .map((obj, index) => {
            return obj.id
          })
          .join(',')
        this.checkModalTitle = title
        this.$refs.checkModalForm.showModal(idsStr, type)
      } else {
        this.$message.error('请选择要检查的作业记录明细!')
      }
    },
    // 检查回调
    onCheckRecord(result) {
      if (result.saveSuccess) {
        this.loadRecordFormDetail()
      }
    },
    handleMenuClick({key}){
      let selectRecords = this.$refs.recordListTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let idsStr = selectRecords
          .map((obj, index) => {
            return obj.id
          })
          .join(',')
        deleteCheckRecord(`ids=${idsStr}&checkType=${key}`).then((res)=>{
          if(res.success){
            this.$message.success('清除成功')
            this.loadRecordFormDetail()
          }else{
            this.$message.warning(res.message)
          }
        })
      } else {
        this.$message.error('请选择要检查的作业记录明细!')
      }
    },
    createFault() {
      (async () => {
        this.$refs.breakdownModal.setWorkStations(this.workOrderInfo.staffArranges)
        let _data = {}
        if (this.workOrderInfo.planId) {
          _data = {
            planName: this.workOrderInfo.planName,
            planId: this.workOrderInfo.planId,
            lineId: this.workOrderInfo.lineId,
            trainNo: this.workOrderInfo.trainNo,
          }
        }
        this.$refs.breakdownModal.edit({
          workOrderId: this.workOrderInfo.orderId,
          formType: 2,
          createType: 2, //  1 表示来自调度， 2 表示来自工班
          happenTime: this.$moment().format('YYYY-MM-DD'),
          faultDesc: '',
          phase: 1,
          status: 1,
          ..._data,
        })
      })()
    },
  },
}
</script>
<style lang="less" scoped>
.buttonDiv {
  padding: 10px 0;
}

[layout-flex]{
  display: flex;
  flex-direction: column;
}
[flex-full]{
  flex: 1;
  height: 100%;
}
</style>
