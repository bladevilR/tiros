<template>
  <a-tabs :activeKey="activeKey" @change="changeTab" style="height: 100%">
    <a-tab-pane key="1" tab="交车信息">
      <deliver-car-info ref="infoForm" v-if="!isReadonly" :carStateData.sync="Data" :accept-mileage.sync="acceptMileage"></deliver-car-info>
      <deliver-car-View ref="infoForm" v-else :carStateData.sync="Data" :accept-mileage.sync="acceptMileage" />
    </a-tab-pane>
    <a-tab-pane key="2" tab="车辆履历">
      <train-resume-tab ref="trainResume" :train-no="Data.trainNo" :accept-mileage="acceptMileage"></train-resume-tab>
    </a-tab-pane>
    <a-tab-pane key="3" tab="开口项">
      <receive-car-open-item ref="openItem" :isReadonly="isReadonly" :trainTypeId="trainTypeId"
                             :exchangeId="exchangeId"></receive-car-open-item>
    </a-tab-pane>
    <a-tab-pane key="4" tab="整改项">
      <receive-car-rectify ref="rectify" :isReadonly="isReadonly" :exchange-id="exchangeId"></receive-car-rectify>
    </a-tab-pane>
  </a-tabs>
</template>

<script>
import DeliverCarView from '@views/tiros/dispatch/delivercar/DeliverCarView'
import DeliverCarInfo from '@views/tiros/dispatch/delivercar/DeliverCarInfo'
import ReceiveCarOpenItem from '@views/tiros/dispatch/receivecar/ReceiveCarOpenItem'
import ReceiveCarRectify from '@views/tiros/dispatch/receivecar/ReceiveCarRectify'
import TrainResumeTab from '@views/tiros/dispatch/delivercar/TrainResumeTab'
import { getExChangeDetail, getExChangeLeaveList, getRectifyList } from '@api/tirosDispatchApi'
import { getLine } from '@api/tirosApi'
import { everythingIsEmpty } from '@/utils/util'

export default {
name: "DeliverCarForm",
  components: { DeliverCarInfo,DeliverCarView, ReceiveCarOpenItem, ReceiveCarRectify, TrainResumeTab },
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
  mounted() {
    if (this.businessKey) {
      // 调用后端接口加载数据
      getExChangeDetail({ id: this.businessKey }).then((res) => {
        if (res.success) {
          this.edit(res.result)
        } else {
          console.error('获取交接车记录明细失败：', res.message)
        }
        this.$emit('loaded')
      })
    }
  },
  data () {
    return {
      activeKey: '1',
      confirmLoading: false,
      carStateData: [],
      Data: {},
      trainTypeId: '',
      acceptMileage: '',
      exchangeId: ''
    }
  },
   watch: {
    exchangeId(val){
      if(val){
        let queryParam = {
          exchangeId: val,
          pageNo: 1,
          pageSize: 999,
        }
        getExChangeLeaveList(queryParam).then((res) => {
            this.$bus.$emit('setReceiveCarOpenItemData', res.result.records)
        })

        getRectifyList(queryParam).then((res) => {
          this.$bus.$emit('setReceiveCarRectifyData', res.result.records)
        })
      }
    },
  },
  created(){
    this.$bus.$on('setExchangeId', (value) => {
      this.exchangeId = value || ''
    })
  },
  methods:{
    save(opts) {
      this.exchangeId = ''
      this.$refs.infoForm.save().then(
        () => {
          this.$emit('ok',opts)
        },
        (e) => {
          this.$emit('fail', opts)
        }
      )
    },
    changeTab(activeKey) {
      this.activeKey = activeKey
      let lineId = this.$refs.infoForm.model.lineId
      this.exchangeId = this.$refs.infoForm.model.id
      if (activeKey === '3') {
        if (lineId) {
          getLine({ lineId: lineId }).then((res) => {
            if (res.success) {
              this.trainTypeId = res.result.trainTypeId
              // this.$refs.openItem.findList()
            }
          })
        }
      }
      if (activeKey === '2') {
        if (everythingIsEmpty(this.Data.trainNo)) {
          this.activeKey = '1'
          this.$message.warn('请先选择车辆')
        }
      }
    },
    add() {
      this.edit({})
    },
    edit(record) {
      this.Data = record
      let that = this
      setTimeout(function () {
        that.$refs.infoForm.findList()
      }, 0)
    },
    /*handleCancel() {
      this.$emit('close')
    },*/
  }
}
</script>

<style scoped>

</style>