<template>
  <div>
  <div style="text-align: center;">
    故障处理方式：
  <a-radio-group v-model="handleAction" @change="onHandleChange">
    <a-radio :value="0">
      直接关闭故障
    </a-radio>
    <a-radio :value="1">
      创建工单处理
    </a-radio>
    <a-radio :value="2">
      取消故障记录
    </a-radio>
  </a-radio-group>
  </div>
  <EditWorkOrderComponent v-if="handleAction===1" ref="editForm" :fromFlow="fromFlow" :isReadonly="isReadonly" @ok="ok" @fail="fail" @close="close" @loaded="onLoaded"></EditWorkOrderComponent>
  <BreakdowmDetailItem v-if="handleAction!==1" ref="BreakdowmDetailItem" height="calc(100vh - 61px)" @loaded="onLoaded" />
  </div>
</template>

<script>
import BreakdowmDetailItem from "./BreakdowmDetailItem";
import EditWorkOrderComponent from '@views/tiros/dispatch/workOrder/EditWorkOrderComponent'
import { cancelBreakdown, getBreakdownInfo } from '@api/tirosDispatchApi'
import { randomString } from '@comp/_util/RandomString'
export default {
  name: 'BreakdownHandle',
  components:{ BreakdowmDetailItem, EditWorkOrderComponent},
  data () {
    return {
      handleAction: -1,
      flwOpts: { vars: {} }
    }
  },
  props: {
    businessKey: {
      type: String,
      default: null
    },
    isReadonly: {
      type: Boolean,
      default: false
    },
    fromFlow: {
      type: Boolean,
      default: false
    },
    attributes: {
      type: Object,
      default: function () {
        return {}
      }
    },
    processInstanceId: {
      type: String,
      default: ''
    },
    wfTask: {
      type: Object,
      default: ()=>{
        return null
      }
    }
  },
  mounted () {
    this.handleAction = -1
    if (this.businessKey && this.handleAction !== 1) {
      this.$nextTick(() => {
        this.$refs.BreakdowmDetailItem.show(this.businessKey)
      })
    }
  },
  methods: {
    onLoaded (data) {
      this.$emit('loaded')
    },
    onHandleChange (e) {
      // e.target.value
      if (this.handleAction === 1) {
        this.$nextTick(()=>{
          this.crateOrderByFaultInfo()
        })
      } else {
        this.$nextTick(()=>{
          this.$refs.BreakdowmDetailItem.show(this.businessKey);
        })
      }
    },
    save (opts) {
      this.flwOpts = opts
      if (this.handleAction === -1) {
        this.$message.warn('请选择一种故障处理方式')
        this.$emit("cancel");
        return
      }
      // 取消故障
      if (this.handleAction === 2) {
        this.$confirm({
          content: `确定取消该故障记录？`,
          onOk: () => {
            cancelBreakdown('ids=' + this.businessKey).then((res) => {
              if (res.success) {
                // 取消流程
                this.$workflowApi.stopProcess(this.processInstanceId, this.wfTask.taskId).then(res => {
                  if (res.success) {
                    this.$message.success('操作成功')
                    this.$emit('cancel', { close: true })
                  } else {
                    this.$message.error('操作失败')
                    console.error('取消流程失败：', res.message)
                    this.$emit('cancel')
                  }
                })
              } else {
                this.$message.error('取消故障记录失败:' + res.message)
                this.$emit('cancel')
              }
            })
          },
          onCancel: () => {
            this.$emit('cancel')
          }
        })
        return
      }

      let vars = { isGenWork: this.handleAction === 1 ? 1 : 0 }
      if (this.handleAction === 1) {
        this.$refs.editForm.saveAndStart(true)
      } else {
        this.flwOpts.vars = vars
        this.$emit("ok",this.flwOpts);
      }
    },
    crateOrderByFaultInfo () {
      getBreakdownInfo({ id: this.businessKey }).then((res) => {
        let faultInfo = res.result;
        if(faultInfo.handleOrderId) {
          this.orderId = faultInfo.handleOrderId
          this.$refs.editForm.edit(this.orderId)
        } else {
          let orderInfo = {
            orderName: faultInfo.faultSn + '故障处理',
            orderType: 2,
            lineId: faultInfo.lineId,
            trainNo: faultInfo.trainNo,
            planId: faultInfo.planId,
            planName: faultInfo.planName,
            groupId: '',
            workshopId: this.$store.getters.userInfo.workshopId,
            startTime: this.$moment().format("YYYY-MM-DD"),
            finishTime: this.$moment().format("YYYY-MM-DD")
          }

          let tasks = []
          let task = {
            id: randomString(),
            taskType: 2,
            taskType_dictText: '故障任务',
            taskNo: 1,
            taskName: faultInfo.faultSn + '[故障处理]',
            taskObjId: faultInfo.id,
            method: 3,
            assetTypeId: faultInfo.subSysId,
            structDetailId: faultInfo.trainStructureId,
            trainAssetId: faultInfo.faultAssetId,
            taskContent: '处理：' + faultInfo.faultDesc
          }
          tasks.push(task)

          this.$refs.editForm.createFaultOrder(orderInfo, tasks)
          // this.annexList = data.annexList;
          /* if (!everythingIsEmpty(data.handleRecordList)) {
          this.handleModel = data.handleRecordList[0];
        }*/
        }
      });
    },
    ok (data) {
      let vars = { isGenWork: this.handleAction }
      this.flwOpts.vars=vars
      this.$emit("ok",this.flwOpts);
    },
    fail (data) {
      this.$emit('cancel')
    },
    close () {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>

</style>