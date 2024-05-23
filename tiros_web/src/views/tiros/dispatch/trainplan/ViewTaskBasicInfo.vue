<template>
  <a-descriptions>
    <a-descriptions-item label="任务名称">{{ taskInfo.taskName }}</a-descriptions-item>
    <a-descriptions-item label="作业手段">{{ taskInfo.method_dictText }}</a-descriptions-item>
    <a-descriptions-item label="作业工时">{{ taskInfo.workTime }}</a-descriptions-item>
    <a-descriptions-item label="作业系统">{{ taskInfo.systemName }}</a-descriptions-item>
    <a-descriptions-item label="作业设备">{{ taskInfo.assetTypeName }}</a-descriptions-item>
    <a-descriptions-item label="开始日期">{{ taskInfo.startTime }}</a-descriptions-item>
    <a-descriptions-item label="结束日期">{{ taskInfo.finishTime }}</a-descriptions-item>
    <a-descriptions-item label="作业工班">{{ taskInfo.workGroupName }}</a-descriptions-item>
    <a-descriptions-item label="作业工位">{{ taskInfo.workStations }}</a-descriptions-item>
    <a-descriptions-item label="备注">{{ taskInfo.remark }}</a-descriptions-item>

  </a-descriptions>
</template>

<script>
export default {
  name: 'ViewTaskBasicInfo',
  props: ['taskInfo']
}
</script>

<style scoped>

</style>