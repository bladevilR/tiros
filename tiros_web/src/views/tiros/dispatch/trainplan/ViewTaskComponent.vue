<template>
  <a-tabs na-flex-height-full>
    <a-tab-pane key="1" tab="基本信息">
      <ViewTaskBasicInfo :taskInfo="taskInfo"></ViewTaskBasicInfo>
    </a-tab-pane>
    <a-tab-pane key="2" tab="关联规程">
      <TaskRegulations :task-regulations="taskInfo.repairPlanReguInfo" :read-only="true"></TaskRegulations>
    </a-tab-pane>
    <a-tab-pane key="3" tab="任务必换件">
      <TaskMustReplaces :must-replaces="taskInfo.mustReplaces"  :read-only="true"></TaskMustReplaces>
    </a-tab-pane>
    <a-tab-pane key="4" tab="任务物料">
      <TaskMaterials :task-materials="taskInfo.materials"  :read-only="true"></TaskMaterials>
    </a-tab-pane>
    <a-tab-pane key="5" tab="任务工器具">
      <TaskTools :task-tools="taskInfo.tools"  :read-only="true"></TaskTools>
    </a-tab-pane>
    <a-tab-pane key="6" tab="人员需求">
      <TaskPersons :task-persons="taskInfo.persons"  :read-only="true"></TaskPersons>
    </a-tab-pane>
  </a-tabs>
</template>

<script>
import ViewTaskBasicInfo from '@views/tiros/dispatch/trainplan/ViewTaskBasicInfo'
import TaskRegulations from '@views/tiros/basic/modules/planTemplate/TaskRegulations'
import TaskMustReplaces from '@views/tiros/basic/modules/planTemplate/TaskMustReplaces'
import TaskTools from '@views/tiros/basic/modules/planTemplate/TaskTools'
import TaskMaterials from '@views/tiros/basic/modules/planTemplate/TaskMaterials'
import TaskPersons from '@views/tiros/basic/modules/planTemplate/TaskPersons'
import { getTrainPlanTaskDetail } from '@api/tirosDispatchApi'

export default {
  name: 'ViewTaskComponent',
  components: {ViewTaskBasicInfo,TaskRegulations, TaskMustReplaces, TaskMaterials, TaskTools, TaskPersons },
  props: ['taskId'],
  data () {
    return {
      taskInfo: {}
    }
  },
  mounted () {
    this.loadTaskInfo()
  },
  methods: {
    loadTaskInfo () {
      getTrainPlanTaskDetail(this.taskId).then(res => {
        if(res.success){
          this.taskInfo = res.result
        }
      })
    }
  }
}
</script>

<style scoped>

</style>