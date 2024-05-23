<template>
  <div>
    <a-tabs v-model='curTabKey'>
      <a-tab-pane key='1' tab='基本信息'>
        <TaskBasicInfo
          v-if='!readOnly'
          ref='baseInfo'
          :planInfo='planInfo'
          :trainTypeId='planInfo.trainTypeId'
          :tasks='tasks'
          :parentTask='parentTask'
          :baseDate='planInfo.baseDate'
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
          :plan-id='planInfo.id'
          :task-regulations.sync='curTaskInfo.repairPlanReguInfo'
          @change='reguChange'
          @addMaterials='addMaterials'
          @addTools='addTools'
          @addPersons='addPersons'
          @addBookDetails='addBookDetails'
          @addMustReplaces='addMustReplaces'
          @removeMaterials='removeMaterials'
          @removePersons='removePersons'
          @removeTools='removeTools'
          @removeMustReplaces='removeMustReplaces'
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
        <TargetDevice :planInfo='planInfo' :lineId='lineId' :taskInfo='taskInfo'
                      :equipments.sync='curTaskInfo.equipments' :readOnly='readOnly' />
      </a-tab-pane>
      <a-tab-pane key='5' tab='任务工器具'>
        <TaskTools :readOnly='readOnly' :task-tools.sync='curTaskInfo.tools'></TaskTools>
      </a-tab-pane>
      <a-tab-pane key='6' tab='任务表单'>
        <TaskForms :plan-type='0' :readOnly='readOnly' :task-forms.sync='curTaskInfo.forms' :plan-info='planInfo'
                   :trainTypeId='planInfo.trainTypeId'></TaskForms>
      </a-tab-pane>
      <a-tab-pane key='9' tab='设备需求'>
        <TaskSpecAssets :readOnly='readOnly' :taskSpecAssets='curTaskInfo.specAssets'></TaskSpecAssets>
      </a-tab-pane>
      <a-tab-pane key='7' tab='人员需求'>
        <TaskPersons :readOnly='readOnly' :task-persons.sync='curTaskInfo.persons'></TaskPersons>
      </a-tab-pane>
      <a-button
        v-if='!readOnly'
        @click='save'
        class='margin-right8'
        type='primary'
        slot='tabBarExtraContent'
        :loading='confirmLoading'
      >
        保存
      </a-button>
      <!--      <a-button @click="cancel" class="margin-right8" type="dashed" slot="tabBarExtraContent">
              取消
            </a-button>-->
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
import {
  addPlanTask,
  getTaskDetail,
  getTrainAssetType,
  updateTaskNoAndWbs
} from '@api/tirosApi'
import {
  getMaterialToolType,
  getMaterialType
} from '@api/tirosMaterialApi'
import { randomUUID } from '@/utils/util'

export default {
  name: 'TaskEditModal',
  components: {
    TaskBasicInfo,
    TaskBasicInfoView,
    TaskRegulations,
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
    visible: {
      type: Boolean,
      default: false
    },
    lineId: {
      default: ''
    },
    repairProId: {
      default: ''
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
      confirmLoading: false,
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
      getTaskDetail(this.curTaskInfo.UID).then((res) => {
        // console.log(res)
        if (res.success) {
          const task = res.result
          this.curTaskInfo.repairPlanReguInfo = task.repairPlanReguInfo ? task.repairPlanReguInfo : []
          this.curTaskInfo.materials = task.materials ? task.materials : []
          this.curTaskInfo.mustReplaces = task.mustReplaces ? task.mustReplaces : []
          this.curTaskInfo.forms = task.forms ? task.forms : []
          this.curTaskInfo.persons = task.persons ? task.persons : []
          this.curTaskInfo.tools = task.tools ? task.tools : []
          this.curTaskInfo.equipments = task.equipments ? task.equipments : []

          this.$set(this.curTaskInfo, 'bookSteps', task.bookSteps)
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
      this.$refs.baseInfo.validate().then(() => {
        // 验证添加物资
        (async () => {
          const result = await this.$refs.taskMaterials && await this.$refs.taskMaterials.validAllEvent()
          // console.log(result === false)
          // 检查通过
          if (result === false) {
            this.$message.warning('任务物料输入验证失败，请检查')
            this.curTabKey = '4'
            return
          }
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
          this.confirmLoading = true
          addPlanTask(task)
            .then((res) => {
              if (res.success) {
                // 保存任务序号  改成发送事件，由父组件保存
                // this.saveTaskNo()

                // this.$message.success('保存任务成功')
                this.$emit('save_taskNo')
                this.$emit('save_success')
                this.$emit('update:visible', false)
              } else {
                this.$message.error(res.message)
              }
            })
            .catch((err) => {
              this.$message.error('保存任务信息异常')
              console.error('保存任务信息异常：', err)
            })
            .finally(() => {
              this.confirmLoading = false
            })

        })()
      }).catch((err) => {
        console.error(err)
        this.$message.error('任务基本信息输入验证失败，请检查')
        this.curTabKey = '1'
      })
    },
    cancel() {
      this.$emit('cancel')
      this.$emit('update:visible', false)
    },
    findParent(parent) {
      if (parent.getTaskNos) {
        return parent
      } else {
        if (parent.$parent) {
          return this.findParent(parent.$parent)
        } else {
          return null
        }
      }
    },
    // 保存任务序号
    saveTaskNo() {
      // const taskNos = this.$parent.$parent.$parent.$parent.$parent.getTaskNos()
      let taskNos = []
      let p = this.findParent(this.$parent)
      if (p) {
        taskNos = p.getTaskNos()
      }

      updateTaskNoAndWbs(taskNos)
        .then((res) => {
          if (!res.success) {
            console.error('保存任务序号失败：', res.message)
          }
        })
        .catch((err) => {
          console.error('保存任务序号异常：', err)
        })
    },
    //=============================规程改变，对任务物料、工器具、人员的改变============================
    reguChange(rg) {
      // 循环所有规程记录，判断是否有委外或者关键任务
      this.curTaskInfo.repairPlanReguInfo.forEach((regu) => {
        if (regu.outsource === 1) {
          this.$refs.baseInfo.setOutsource(true)
        }
        if (regu.important === 1) {
          this.$refs.baseInfo.setImportant(true)
        }
        this.$forceUpdate()
      })
    },
    addMaterials(materials) {
      materials.forEach((mater) => {
        if (this.findMaterial(mater.materialTypeId)) {
          // 存在了不添加
          return
        }
        getMaterialType({ id: mater.materialTypeId }).then((res) => {
          if (res.success) {
            let m = {
              id: randomUUID(),
              code: mater.code,
              materialTypeId: mater.materialTypeId,
              name: mater.name,
              kind: mater.kind,
              kind_dictText: mater.kind_dictText,
              unit: mater.unit,
              category1_dictText: res.result.category1_dictText,
              amount: mater.amount ? mater.amount : 0,
              useCategory: mater.useCategory == -1 ? '' : mater.useCategory,
              useCategory_dictText: mater.useCategory == -1 ? '' : mater.useCategory_dictText
            }
            this.curTaskInfo.materials.push(m)
          }
        })
      })
    },
    addTools(tools) {
      tools.forEach((tool) => {
        if (this.findTool(tool.toolTypeId)) {
          return
        }
        getMaterialToolType({ id: tool.toolTypeId }).then((res) => {
          if (res.success) {
            let m = {
              id: randomUUID(),
              code: tool.code,
              toolTypeId: tool.toolTypeId,
              name: tool.name,
              kind: tool.kind,
              kind_dictText: tool.kind_dictText,
              unit: tool.unit,
              category1: res.result.category1,
              category1_dictText: res.result.category1_dictText,
              amount: tool.amount ? tool.amount : 0
            }
            this.curTaskInfo.tools.push(m)
          }
        })
      })
    },
    addPersons(persons) {
      persons.forEach((p) => {
        if (this.findPerson(p.id)) {
          return
        }
        let person = {
          id: p.id,
          amount: p.amount,
          requireCertificate: p.requireCertificate,
          requirePostion: p.requirePostion,
          requireTech: p.requireTech
        }
        this.curTaskInfo.persons.push(person)
      })
    },
    addBookDetails(details) {
      details.forEach((item) => {
        if (this.curTaskInfo.bookSteps.find((e) => e.bookDetailId === item.bookDetailId)) {
          return
        }
        this.curTaskInfo.bookSteps.push(JSON.parse(JSON.stringify(item)))
      })
    },
    addMustReplaces(regu) {
      if (regu.assetTypeId) {
        if (this.findReplace(regu.assetTypeId)) {
          // 存在了就不添加了
          return
        }
        getTrainAssetType({ id: regu.assetTypeId }).then((res) => {
          if (res.success) {
            let asset = res.result
            let mustReplace = {
              assetTypeId: regu.assetTypeId,
              code: asset.code,
              id: randomUUID(),
              initNum: asset.initNum,
              name: asset.name,
              placeCode: asset.placeCode,
              placeDesc: asset.placeDesc,
              placeId: asset.placeId,
              price: 0,
              remark: asset.remark,
              structType: asset.structType,
              structType_dictText: asset.structType_dictText,
              sumPrice: 0,
              taskId: '',
              unit: asset.unit
            }
            this.curTaskInfo.mustReplaces.push(mustReplace)
          }
        })
      }
    },
    findReplace(assetTypeId) {
      let flag = false
      this.curTaskInfo.mustReplaces.forEach((rep) => {
        if (rep.assetTypeId === assetTypeId) {
          flag = true
          return false
        }
      })
      return flag
    },
    findMaterial(materialId) {
      let flag = false
      this.curTaskInfo.materials.forEach((m) => {
        if (m.materialTypeId === materialId) {
          flag = true
          return false
        }
      })
      return flag
    },
    findTool(toolTypeId) {
      let flag = false
      this.curTaskInfo.tools.forEach((t) => {
        if (t.toolTypeId === toolTypeId) {
          flag = true
          return false
        }
      })
      return flag
    },
    findPerson(personId) {
      let flag = false
      this.curTaskInfo.persons.forEach((p) => {
        if (p.id === personId) {
          flag = true
          return false
        }
      })
      return flag
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
    removeMustReplaces(regu) {
      console.log('removeMustReplaces:', regu)
    }
  }
}
</script>

<style>
</style>