<template>
  <div>
    <a-tabs v-model='curTabKey' style='height: 100%'>
      <a-tab-pane key='1' tab='基本信息'>
        <TaskBasicInfo
          v-if='!readOnly'
          ref='baseInfo'
          :trainTypeId='planInfo.trainTypeId'
          :tasks='tasks'
          :baseDate='planInfo.startDate'
          :task='curTaskInfo'
        ></TaskBasicInfo>
        <TaskBasicInfoView
          v-else
          ref='baseInfo'
          :trainTypeId='planInfo.trainTypeId'
          :tasks='tasks'
          :baseDate='planInfo.baseDate'
          :task='curTaskInfo'
        ></TaskBasicInfoView>
      </a-tab-pane>
      <a-tab-pane key='2' tab='关联规程'>
        <TaskRegulations
          :readOnly='readOnly'
          :regu-id='planInfo.reguId'
          :task-regulations.sync='curTaskInfo.repairPlanReguInfo'
          @addMaterials='addMaterials'
          @addTools='addTools'
          @addPersons='addPersons'
          @removeMaterials='removeMaterials'
          @addBookDetails='addBookDetails'
          @removePersons='removePersons'
          @removeTools='removeTools'
        ></TaskRegulations>
      </a-tab-pane>
      <a-tab-pane key='8' tab='关联作业指导书'>
        <TaskBookSteps
          :readOnly='readOnly'
          :taskBookSteps='curTaskInfo.bookSteps'
          :taskId='curTaskInfo.taskId'
        ></TaskBookSteps>
      </a-tab-pane>
      <!-- <a-tab-pane key="3" tab="任务必换件">
        <TaskMustReplaces
          :readOnly="readOnly"
          :lineId="lineId"
          :repairProId="repairProId"
          :must-replaces.sync="curTaskInfo.mustReplaces"
        ></TaskMustReplaces>
      </a-tab-pane> -->
      <a-tab-pane key='4' tab='任务物料'>
        <TaskMaterials ref='taskMaterials' :readOnly='readOnly'
                       :task-materials.sync='curTaskInfo.materials'></TaskMaterials>
      </a-tab-pane>
      <a-tab-pane key='10' tab='目标设备'>
        <TargetDevice :planInfo='planInfo' :taskInfo='taskInfo' :equipments.sync='curTaskInfo.equipments'
                      :readOnly='readOnly' />
      </a-tab-pane>
      <a-tab-pane key='5' tab='任务工器具'>
        <TaskTools :readOnly='readOnly' :task-tools.sync='curTaskInfo.tools'></TaskTools>
      </a-tab-pane>
      <a-tab-pane key='6' tab='任务表单'>
        <TaskForms :plan-type='1' :readOnly='readOnly' :task-forms.sync='curTaskInfo.forms' :plan-info='planInfo'
                   :trainTypeId='planInfo.trainTypeId'></TaskForms>
      </a-tab-pane>
      <a-tab-pane key='9' tab='设备需求'>
        <TaskSpecAssets :readOnly='readOnly' :taskSpecAssets='curTaskInfo.specAssets'></TaskSpecAssets>
      </a-tab-pane>
      <a-tab-pane key='7' tab='人员需求'>
        <TaskPersons :readOnly='readOnly' :task-persons.sync='curTaskInfo.persons'></TaskPersons>
      </a-tab-pane>
      <a-space slot='tabBarExtraContent'>
        <a-button v-if='!readOnly' @click='save' type='primary' :loading='loading'> 保存</a-button>
        <!--      <a-button @click="cancel" type="dashed" >
        取消
      </a-button>-->
      </a-space>
    </a-tabs>
  </div>
</template>

<script>
import TaskBasicInfo from '@views/tiros/basic/modules/planTemplate/TaskBasicInfo'
import TaskBasicInfoView from '@views/tiros/basic/modules/planTemplate/TaskBasicInfoView'
import TaskRegulations from '@views/tiros/basic/modules/planTemplate/TaskRegulations'
import TaskMustReplaces from '@views/tiros/basic/modules/planTemplate/TaskMustReplaces'
import TaskTools from '@views/tiros/basic/modules/planTemplate/TaskTools'
import TaskMaterials from '@views/tiros/basic/modules/planTemplate/TaskMaterials'
import TaskBookSteps from '@views/tiros/basic/modules/planTemplate/TaskBookSteps'
import TaskForms from '@views/tiros/basic/modules/planTemplate/TaskForms'
import TaskPersons from '@views/tiros/basic/modules/planTemplate/TaskPersons'
import TaskSpecAssets from '@views/tiros/basic/modules/planTemplate/TaskSpecAssets'
import TargetDevice from '@views/tiros/basic/modules/planTemplate/TargetDevice'

import { addOrUpdateTrainPlanTask, getTrainPlanTaskDetail } from '@api/tirosDispatchApi'

export default {
  name: 'taskEdit',
  components: {
    TaskBasicInfo,
    TaskRegulations,
    TaskBasicInfoView,
    TaskMustReplaces,
    TaskMaterials,
    TaskTools,
    TaskForms,
    TaskPersons,
    TaskBookSteps,
    TaskSpecAssets,
    TargetDevice
  },
  props: {
    repairProId: {
      default: ''
    },
    lineId: {
      default: ''
    },
    visible: {
      type: Boolean,
      default: false
    },
    planInfo: {
      type: Object,
      default() {
        return {}
      }
    },
    tasks: {
      type: Array,
      default: []
    },
    taskInfo: {
      type: Object,
      default: null
    },
    parentTask: {
      type: Object,
      default: null
    },
    readOnly: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      curTabKey: '1',
      curTaskInfo: {
        repairPlanReguInfo: [],
        materials: [],
        mustReplaces: [],
        forms: [],
        persons: [],
        tools: [],
        bookSteps: [],
        specAssets: [],
        equipments: []
      }
    }
  },
  beforeMount() {
    this.curTaskInfo = {
      repairPlanReguInfo: [],
      materials: [],
      mustReplaces: [],
      forms: [],
      persons: [],
      tools: [],
      bookSteps: [],
      specAssets: [],
      equipments: []
    }
    // 有设置任务信息，则表示为编辑任务
    Object.assign(this.curTaskInfo, this.taskInfo)
    // 有设置上级节点
    if (this.parentTask) {
      this.curTaskInfo.ParentId = this.parentTask.UID
      this.curTaskInfo.ParentName = this.parentTask.Name
    }

    this.curTaskInfo.planId = this.planInfo.id
    // 加载任务其他数据,如果是修改
    if (this.curTaskInfo.startTime && this.curTaskInfo.finishTime) {
      getTrainPlanTaskDetail(this.curTaskInfo.UID).then((res) => {
        console.log(res)
        if (res.success) {
          const task = res.result
          this.curTaskInfo.repairPlanReguInfo = task.repairPlanReguInfo ? task.repairPlanReguInfo : []
          this.curTaskInfo.materials = task.materials ? task.materials : []
          this.curTaskInfo.mustReplaces = task.mustReplaces ? task.mustReplaces : []
          this.curTaskInfo.forms = task.forms ? task.forms : []
          this.curTaskInfo.persons = task.persons ? task.persons : []
          this.curTaskInfo.tools = task.tools ? task.tools : []
          this.curTaskInfo.bookSteps = task.bookSteps ? task.bookSteps : []
          this.curTaskInfo.equipments = task.equipments ? task.equipments : []

          // this.$set(this.curTaskInfo, 'bookSteps', task.bookSteps)
          this.$set(this.curTaskInfo, 'specAssets', task.specAssets)
          if (this.$refs.baseInfo) {
            this.$refs.baseInfo.taskInfo.method_dictText = task.method_dictText || ''
          }
        }
      })
    }
  },
  mounted() {
  },
  methods: {
    save() {
      this.$refs.baseInfo
        .validate()
        .then((re) => {
          // 验证添加物资
          (async () => {
            const result = await this.$refs.taskMaterials && await this.$refs.taskMaterials.validAllEvent()
            console.log(result)
            // 检查通过
            if (result == false) {
              this.$message.warning('任务物料输入验证失败，请检查')
              this.curTabKey = '4'
              return
            }
            // 检查通过

            // 任务基本信息
            const task = this.$refs.baseInfo.getData()

            // 获取其他数据
            task.repairPlanReguInfo = this.curTaskInfo.repairPlanReguInfo
            task.materials = this.curTaskInfo.materials
            task.mustReplaces = this.curTaskInfo.mustReplaces
            task.forms = this.curTaskInfo.forms
            task.persons = this.curTaskInfo.persons
            task.tools = this.curTaskInfo.tools
            task.bookSteps = this.curTaskInfo.bookSteps
            task.specAssets = this.curTaskInfo.specAssets
            task.equipments = this.curTaskInfo.equipments

            // 保存到服务器
            this.loading = true
            addOrUpdateTrainPlanTask(task)
              .then((res) => {
                if (res.success) {
                  // 保存任务序号, 改成发送事件，由父组件保存
                  // this.saveTaskNo()

                  //this.$message.success('保存任务成功')
                  this.$emit('save_taskNo')

                  this.$emit('save_success')
                  this.$emit('update:visible', false)
                } else {
                  this.$message.error(res.message)
                }
              })
              .catch((err) => {
                this.$message.error(err)
              })
              .finally(() => {
                this.loading = false
              })
          })()
        })
        .catch((err) => {
          console.error(err)
          this.$message.error('任务基本信息输入验证失败，请检查')
          this.curTabKey = '1'
        })
    },
    cancel() {
      this.$emit('cancel')
      this.$emit('update:visible', false)
    },
    // 保存任务序号
    saveTaskNo() {
      /* const taskNos = this.$parent.$parent.getTaskNos()
      updateTrainPlanTaskNoAndWbs(taskNos).then(res => {
        console.log('save TaskNo:', res)

        if (!res.success) {
          console.error('保存任务序号失败：', res.message)
        }
      }).catch(err => {
        console.error('保存任务序号异常：', err)
      })*/
    },
    //=============================规程改变，对任务物料、工器具、人员的改变============================
    addMaterials(materials) {
      console.log('addMaterials:', materials)
      // this.curTaskInfo.materials.push({})
    },
    addTools(tools) {
      console.log('addTools:', tools)
    },
    addPersons(persons) {
      console.log('addPersons:', persons)
    },
    removeMaterials(materials) {
      console.log('removeMaterials:', materials)
    },
    removeTools(tools) {
      console.log('removeTools:', tools)
    },
    removePersons(persons) {
      console.log('removePersons:', persons)
    },
    addBookDetails(details) {
      details.forEach((item) => {
        if (this.curTaskInfo.bookSteps.find((e) => e.bookDetailId === item.bookDetailId)) {
          return
        }
        this.curTaskInfo.bookSteps.push(JSON.parse(JSON.stringify(item)))
      })
    }
  }
}
</script>

<style scoped>
</style>