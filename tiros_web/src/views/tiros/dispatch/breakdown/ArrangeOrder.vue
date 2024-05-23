<template>
  <EditWorkOrderComponent  ref="editForm" :fromFlow="fromFlow" :isReadonly="isReadonly"  @ok="ok" @fail="fail" @close="close" @loaded="onLoaded"></EditWorkOrderComponent>
</template>

<script>
import { getBreakdownInfo } from '@api/tirosDispatchApi'
import { randomString } from '@comp/_util/RandomString'
import EditWorkOrderComponent from '@views/tiros/dispatch/workOrder/EditWorkOrderComponent'

export default {
  name: "ArrangeOrder",
  components: {
    EditWorkOrderComponent
  },
  props: {
    businessKey: {
      type: String,
      default: null,
    },
    isReadonly: {
      type: Boolean,
      default: false,
    },
    fromFlow: {
      type: Boolean,
      default: false,
    },
  },
  data () {
    return {
      orderId: null
    }
  },
  mounted () {
    if (this.businessKey) {
      this.loadFaultInfo(this.businessKey)
    }
  },
  methods: {
    onLoaded () {
      this.$emit('loaded')
    },
    loadFaultInfo (id) {
      getBreakdownInfo({ id: id }).then((res) => {
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
    save (onlySave) {
      // this.$refs.editForm.save()
      if (onlySave) {
        this.$refs.editForm.saveAndStart(false)
      } else {
        this.$refs.editForm.saveAndStart(true)
      }
    },
    ok (data) {
      this.$emit('ok', data)
    },
    fail (data) {
      this.$emit('fail', data)
    },
    close () {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>

</style>