<template>
  <a-tabs :activeKey="activeKey" @change="changeTab">
    <a-tab-pane key="1" tab="基本信息">
     <train-resume-basic-info  :train-no="trainNo" :accept-mileage="acceptMileage"></train-resume-basic-info>
    </a-tab-pane>
    <a-tab-pane key="2" tab="部件信息">
      <train-asset-info :train-no="trainNo" ></train-asset-info>
    </a-tab-pane>
    <a-tab-pane key="3" tab="测量信息">
      <measurement-record :train-no="trainNo"></measurement-record>
    </a-tab-pane>
    <a-tab-pane key="4" tab="故障信息">
      <fault-record :train-no="trainNo"></fault-record>
    </a-tab-pane>
    <a-tab-pane key="5" tab="部件更换">
      <replace-record :train-no="trainNo"></replace-record>
    </a-tab-pane>
    <a-tab-pane key="6" tab="作业表单">
      <work-record :train-no="trainNo"></work-record>
    </a-tab-pane>
  </a-tabs>
</template>

<script>

import TrainResumeBasicInfo from '@views/tiros/dispatch/delivercar/TrainResumeBasicInfo'
import MeasurementRecord from '@views/tiros/dispatch/delivercar/MeasurementRecord'
import FaultRecord from '@views/tiros/dispatch/delivercar/FaultRecord'
import ReplaceRecord from '@views/tiros/dispatch/delivercar/ReplaceRecord'
import WorkRecord from '@views/tiros/dispatch/delivercar/WorkRecord'
import TrainAssetInfo from '@views/tiros/dispatch/delivercar/TrainAssetInfo'

export default {
  name: 'TrainResumeTab',
  components:{TrainResumeBasicInfo,MeasurementRecord,FaultRecord,ReplaceRecord,WorkRecord,TrainAssetInfo},
  props:['trainNo','acceptMileage'],
  data (){
    return{
      activeKey: '1',
    }

  },
  methods:{
    changeTab(activeKey) {
      this.activeKey = activeKey
    },
  },
}
</script>

<style scoped>

</style>