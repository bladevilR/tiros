<template>
  <a-spin :spinning="spinning" style="height: calc(100%)" id="mySpin">
    <div style="height: calc(100%)">
      <a-form layout="inline" style="z-index: 99">
        <a-row>
          <a-col :span="4">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="名称">
              <a-input placeholder="请输入任务编码和名称" v-model="queryParam.name" />
            </a-form-item>
          </a-col>
          <a-col :span="3">
            <a-form-item style="width: 100%" :labelCol="labelCol" :wrapperCol="wrapperCol" label="工班">
              <j-dict-select-tag
                v-model="queryParam.groupId"
                placeholder="请选择"
                dictCode="sys_depart,depart_name,id,org_category=4"
                :trigger-change="true"
                @change="changeWorkGroup"
              />
            </a-form-item>
          </a-col>
          <!--          <a-col :span="3">
            <a-form-item style="width: 100%" :labelCol="labelCol" :wrapperCol="wrapperCol" label="工位">
              <j-multi-select-tag v-if="isAllWorkStation"
                                  placeholder="请选择"
                                  :trigger-change="true"
                                  v-model="queryParam.workStationId"
                                  dictCode="bu_mtr_workstation,name,id"
              />
              <a-select v-else
                        mode="multiple"
                        style="width: 100%"
                        labelInValue
                        v-model="queryParam.workStationId"
              >
                <a-select-option v-for="i in workStationList" :title="i.name" :key="i.id">{{ i.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>-->
          <a-col :span="16" style="padding-left: 15px">
            <a-form-item class="space-btn">
              <a-button @click="onSearch">搜索</a-button>
              <a-button @click="onReset">重置</a-button>
              <!--              <a-button @click="onAddTask(1,false)" type="primary">新建根任务</a-button>-->
              <a-dropdown>
                <a-menu slot="overlay" @click="handleMenuClick">
                  <a-menu-item key="1"> 新建根任务 </a-menu-item>
                  <a-menu-item key="2"> 新建子任务 </a-menu-item>
                </a-menu>
                <a-button> 新建任务 <a-icon type="down" /> </a-button>
              </a-dropdown>
              <a-button @click="onEditTask">修改任务</a-button>
              <a-button @click="onDeleteTask" v-has="'plantemplate:task:delete'">删除任务</a-button>
              <!--              <a-button @click="cancel">关闭</a-button>-->
              <a-dropdown>
                <a-menu slot="overlay">
                  <a-menu-item key="1" @click="collapseAll">折叠所有</a-menu-item>
                  <a-menu-item key="2" @click="expandAll">展开所有</a-menu-item>
                  <a-menu-item key="3" @click="expandFirst">展开第一层</a-menu-item>
                </a-menu>
                <a-button>折叠/展开<a-icon type="down" /> </a-button>
              </a-dropdown>
            </a-form-item>
            <a-form-item style="color: chocolate">
              还有<span style="font-weight: bold"
                ><a @click.stop="handlerUnlink">{{ unlinkCount }}</a></span
              >条规程作业项没有关联到具体任务
            </a-form-item>
          </a-col>
          <!-- <a-col :span="5">

          </a-col> -->
        </a-row>
      </a-form>
      <div id="viewProject" style="width: 100%; height: calc(100% - 40px); padding-top: 8px"></div>
      <!-- {{task}} -->
      <!--<gan-te v-if="taskLength"></gan-te>-->
      <!-- <task-item-modal ref="modalForm" @ok="addTask"></task-item-modal> -->
    </div>
    <a-modal
      centered
      :width="'100%'"
      :title="'任务编辑'"
      dialogClass="fullDialog no-footer"
      :visible="taskEditVisible"
      @cancel="cancelSave"
      :footer="null"
      :destroyOnClose="true"
    >
      <TaskEditModal
        :lineId="lineId"
        :repairProId="repairProId"
        :planInfo="planInfo"
        :visible.sync="taskEditVisible"
        :tasks="projectData.Tasks"
        :parent-task="parentTask"
        :task-info="editTask"
        @save_success="saveTaskSuccess"
        @cancel="cancelSave"
        @save_taskNo="saveTaskNo"
      ></TaskEditModal>
    </a-modal>
    <a-modal
      centered
      :width="'100%'"
      :title="'任务查看'"
      dialogClass="fullDialog no-footer"
      :visible="taskViewVisible"
      @cancel="cancelView"
      :footer="null"
      :destroyOnClose="true"
    >
      <TaskEditModal
        :readOnly="true"
        :lineId="lineId"
        :repairProId="repairProId"
        :planInfo="planInfo"
        :visible.sync="taskEditVisible"
        :tasks="projectData.Tasks"
        :parent-task="parentTask"
        :task-info="editTask"
      ></TaskEditModal>
    </a-modal>
    <unlink-regulation ref="unlinkRegulation"></unlink-regulation>
  </a-spin>
</template>

<script>
import TaskEditModal from '@views/tiros/basic/modules/planTemplate/TaskEditModal'
import ProjectMenu from '@views/tiros/basic/modules/planTemplate/ProjectMenu'
import {
  delPlanTask,
  getPlanDetail,
  getPlanNoRelevanceCount,
  getStationByGroupId,
  updateTaskNoAndWbs,
} from '@/api/tirosApi'
import { everythingIsEmpty, UUID_V1 } from '@/utils/util'
import UnlinkRegulation from '@views/tiros/basic/modules/planTemplate/UnlinkRegulation'
import JMultiSelectTag from '@comp/dict/JMultiSelectTag'

export default {
  name: 'TaskManageModal',
  props: ['planInfo', 'visible'],
  components: { TaskEditModal, UnlinkRegulation, JMultiSelectTag },
  data() {
    return {
      spinning: false,
      unlinkCount: 0,
      taskEditVisible: false,
      taskViewVisible: false,
      queryParam: {
        name: '',
        groupId: '',
        workStationId: '',
      },
      project: null,
      lineId: '',
      repairProId: '',
      projectData: {
        Tasks: [],
      },
      parentTask: null,
      editTask: null,
      curAction: 0, // 0 新增  1 修改
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 },
      },
      workStationList: [],
      isAllWorkStation: false,
    }
  },
  watch: {
    'queryParam.groupId': {
      immediate: true,
      handler(id) {
        if (everythingIsEmpty(id)) {
          this.isAllWorkStation = true
          this.queryParam.workStationId = ''
        } else {
          this.isAllWorkStation = false
          this.queryParam.workStationId = []
        }
      },
    },
  },
  computed: {
    // task() {
    //   return this.$store.getters.taskList
    // },
  },
  mounted() {
    if (this.planInfo) {
      this.loadProject()
      this.getUnlinkCount()
    }
  },
  methods: {
    handleMenuClick(e) {
      // console.log(e)
      if (e.key === '1') {
        this.onAddTask(1, false)
      }
      if (e.key === '2') {
        this.onAddTask(1, true)
      }
    },
    onReset() {
      this.project.clearFilter()
      this.queryParam = {
        name: '',
        groupId: '',
        workStationId: '',
      }
    },
    onSearch() {
      /*groupId: "zdgb"
      name: ""
      workStationId: Array(0)*/
      this.project.filter((task) => {
        let flag = true
        if (this.queryParam.name) {
          flag = flag && task.Name.indexOf(this.queryParam.name) >= 0
        }
        if (this.queryParam.groupId) {
          flag = flag && task.workGroupId === this.queryParam.groupId
        }
        if (this.queryParam.workStationId && this.queryParam.workStationId.length > 0) {
          flag = flag && this.checkExistAnyOne(this.queryParam.workStationId, task.workstations)
        }
        /*if (task.Duration > 3) return true;
        else return false;*/
        return flag
      })
    },
    checkExistAnyOne(arr1, arr2) {
      if (!arr1 && arr1.length === 0) {
        return false
      }
      if (!arr2 && arr2.length === 0) {
        return false
      }
      let flag = false
      for (let i = 0; i < arr1.length; i++) {
        if (arr2.indexOf(arr1[i]) > -1) {
          flag = true
          return flag
        }
      }
      return flag
    },
    // 工班改变
    changeWorkGroup(value) {
      getStationByGroupId(value).then((res) => {
        this.workStationList = res.result
      })
    },
    handlerUnlink() {
      this.$refs.unlinkRegulation.show({ planId: this.planInfo.id, reguId: this.planInfo.reguId })
    },
    /**
     * 加载甘特图信息
     */
    loadProject() {
      mini.parse()
      this.project = new PlusProject()
      this.project.setStyle('width:100%;height:100%')
      this.project.setBorderStyle('border:1px dotted #cdcdcd')
      const columns = [
        new PlusProject.IDColumn(),
        // new PlusProject.StatusColumn(),
        new PlusProject.NameColumn(),
        {
          header: '前置任务',
          field: '',
          width: 150,
        },
        // new PlusProject.PredecessorLinkColumn(),
        // new PlusProject.PercentCompleteColumn(),
        new PlusProject.DurationColumn(),
        new PlusProject.StartColumn(),
        new PlusProject.FinishColumn(),
        new PlusProject.WorkColumn(),
        {
          header: '班组<br/>Multi',
          field: 'workGroupName',
          width: 150,
        },
        {
          name: 'dayIndex',
          header: '第几天？<br/>String',
          field: 'dayIndex',
          width: 150,
        },
        // new PlusProject.DepartmentColumn(),
        // new PlusProject.PrincipalColumn(),
        // new PlusProject.AssignmentsColumn(),
      ]

      this.project.setColumns(columns)
      this.project.render(document.getElementById('viewProject'))

      //启用手动模式
      this.project.enableManualSchedule = true
      // project.setShowGanttView(false);
      // 只读
      this.project.setReadOnly(true)
      // 显示修改痕迹
      this.project.setShowDirty(false)
      //禁止任务排程算法
      this.project.allowOrderProject = false

      //设置时间线
      /*this.project.setTimeLines([
        { date: new Date(), text: '当前时间', position: 'bottom', style: 'width:2px;background:red;' },
      ])*/

      //设置刻度
      this.project.setTopTimeScale('month')
      this.project.setBottomTimeScale('day')

      //设置表格右键菜单 表格Body
      const menu = new ProjectMenu()
      this.project.setTableBodyMenu(menu)
      menu.editTask.on('click', (e) => {
        this.onEditTask()
      })
      menu.deleteTask.on('click', (e) => {
        this.onDeleteTask()
      })
      menu.beforeAddTask.on('click', (e) => {
        // console.log('click:', e)
        this.onAddTask(0, false)
      })
      menu.afterAddTask.on('click', (e) => {
        this.onAddTask(1, false)
      })
      menu.addChildren.on('click', (e) => {
        this.onAddTask(0, true)
      })
      menu.on('beforeopen', function (e) {
        const el = $(e.htmlEvent.target).closest('.mini-supergrid-row')
        if (el.length === 0) {
          e.cancel = true
        }
      })

      // 设置行高
      this.project.setRowHeight(28)

      // 单击任务
      this.project.on('taskclick', ({ field, task }) => {
        if (field === 'Name' && !(task.children && task.children.length)) {
          this.onViewTask()
        }
      })
      // 设置名称样式
      this.project.on('drawcell', (row) => {
        // {field, cellStyle, task}
        if (row.field === 'Name' && !(row.task.children && row.task.children.length)) {
          row.cellStyle = 'color: #40a9ff;cursor: pointer;'
        }
      })

      // 双击任务
      this.project.on('taskdblclick', ({ task }) => {
        this.onEditTask()
      })

      // 加载数据
      // this.project.loadData({})
      this.getTemplateInfo()
    },
    /**
     * 加载计划模版详情
     * @param plan
     */
    getTemplateInfo() {
      this.parentTask = null
      this.editTask = null
      this.spinning = true
      getPlanDetail(this.planInfo.id)
        .then((res) => {
          // console.log(res)
          if (res.success) {
            this.lineId = res.result.lineId
            this.repairProId = res.result.repairProgramId
            this.projectData = res.result
            // 数据加载到甘特图
            this.projectData.StartDate = new Date(this.projectData.StartDate)
            this.projectData.FinishDate = new Date(this.projectData.FinishDate)
            this.projectData.Tasks = mini.arrayToTree(this.projectData.Tasks, 'children', 'UID', 'ParentId')
            this.project.loadData(this.projectData)
            this.onSearch()
          } else {
            console.error('加载计划模版明细数据失败：', this.planInfo.id)
            this.$message.error('加载计划模版明细数据失败')
          }
          this.spinning = false
        })
        .catch((err) => {
          console.error('加载计划模版明细数据异常：', err, this.planInfo.id)
          this.$message.error('加载计划模版明细数据异常')
        })
    },
    /**
     * 获取未关联的规程数量
     */
    getUnlinkCount() {
      if (!this.planInfo.id || !this.planInfo.reguId) {
        this.unlinkCount = 0
        return
      }
      getPlanNoRelevanceCount({ planId: this.planInfo.id, reguId: this.planInfo.reguId })
        .then((res) => {
          if (res.success) {
            this.unlinkCount = res.result
          } else {
            console.error('获取未关联的规程数量错误：', res.message)
          }
        })
        .catch((err) => {
          console.error('获取未关联的规程数量错误：', err)
        })
    },
    /**
     * 添加任务
     */
    onAddTask(location, add_child) {
      this.curAction = 0
      const task = this.project.getSelected()
      if (add_child) {
        if (!task) {
          this.$message.error('请选择一条上级任务')
          return
        }
        this.parentTask = task
      } else {
        // console.log(task)
        // 原代码逻辑  新创建的模板-新建根任务报错
        // let pTask=this.project.getTask(task.ParentTaskUID)
        // this.parentTask = pTask
        // 以下为修改后的代码
        if(task){
          let pTask=this.project.getTask(task.ParentTaskUID)
          this.parentTask = pTask
        }else{
          this.parentTask = null
        }
      }

      const newTask = this.project.newTask()
      newTask.UID = UUID_V1()
      newTask.Name = '<新增任务>' //初始化任务属性

      const selectedTask = this.project.getSelected()
      if (selectedTask) {
        if (add_child) {
          this.project.addTask(newTask, 'add', selectedTask) //加入到选中任务之内
        } else {
          if (location === 0) {
            this.project.addTask(newTask, 'before', selectedTask) //插入到到选中任务之前
          } else {
            this.project.addTask(newTask, 'after', selectedTask) //插入到到选中任务之前
          }
        }
      } else {
        this.project.addTask(newTask)
      }

      newTask.taskName = newTask.Name
      newTask.taskId = newTask.UID
      newTask.taskNo = newTask.ID
      this.editTask = newTask
      this.taskEditVisible = true
    },
    /**
     * 查看任务
     */
    onViewTask() {
      this.curAction = 0
      this.editTask = this.project.getSelected()
      if (this.editTask) {
        this.parentTask = null
        if (this.editTask.ParentTaskUID) {
          let pTask = this.project.getTask(this.editTask.ParentTaskUID)
          if (pTask) {
            this.parentTask = pTask
          }
        }
        this.taskViewVisible = true
      } else {
        this.$message.error('请选择要修改的任务')
      }
    },
    /**
     * 修改任务
     */
    onEditTask() {
      this.curAction = 1
      this.editTask = this.project.getSelected()
      if (this.editTask) {
        this.parentTask = null
        if (this.editTask.ParentTaskUID) {
          let pTask = this.project.getTask(this.editTask.ParentTaskUID)
          if (pTask) {
            this.parentTask = pTask
          }
        }
        this.taskEditVisible = true
      } else {
        this.$message.error('请选择要修改的任务')
      }
    },
    /**
     * 删除任务
     */
    onDeleteTask() {
      const task = this.project.getSelected()
      if (!task) {
        this.$message.error('请选择要删除的任务')
      } else {
        const thiz = this
        this.$confirm({
          title: '删除确认',
          content: '确定要删除当前选中任务及其子任务',
          okText: '确认',
          cancelText: '取消',
          onOk() {
            const params = { ids: task.UID }

            delPlanTask(params)
              .then((res) => {
                if (res.success) {
                  thiz.getTemplateInfo()
                  this.getUnlinkCount()
                  thiz.$message.success('删除任务成功')
                } else {
                  thiz.$message.error(res.message)
                }
              })
              .catch((e) => {
                thiz.$message.error(e)
              })
          },
        })
      }
    },
    cancel() {
      this.$emit('update:visible', false)
    },
    saveTaskSuccess() {
      // 重新加载模版信息
      // this.getTemplateInfo()
    },
    cancelSave() {
      this.taskEditVisible = false
      if (this.curAction === 0) {
        this.project.removeTask(this.editTask)
      }
    },
    cancelView() {
      this.taskViewVisible = false
      // if (this.curAction === 0) {
      //   this.project.removeTask(this.editTask)
      // }
    },
    // 保存任务序号
    saveTaskNo() {
      const taskNos = this.getTaskNos()
      this.$message.success('保存任务成功')
      updateTaskNoAndWbs(taskNos)
        .then((res) => {
          if (!res.success) {
            console.error('保存任务序号失败：', res.message)
          } else {
            // 重新加载模版信息
            this.getTemplateInfo()
            this.getUnlinkCount()
          }
        })
        .catch((err) => {
          console.error('保存任务序号异常：', err)
        })
    },
    getTaskNos() {
      const tasks = this.project.getTaskList()
      // console.log('tasks:', tasks)
      const taskNos = []
      tasks.map((item) => {
        taskNos.push({
          planId: this.planInfo.id,
          UID: item.UID,
          ID: item.ID,
        })
      })
      return taskNos
    },
    collapseAll(){
      this.project.collapseAll()
    },
    expandAll(){
      this.project.expandAll()
    },
    expandFirst(){
      this.project.collapseAll()
      this.project.expandLevel(0)
    }
  },
}
</script>

<style>
#mySpin .ant-spin-container {
  height: 100%;
}

/* 设置甘特图垂直居中 */
.mini-supertree-ec-icon {
  margin-top: 4px;
}

.mini-supertree-nodetext {
  margin-top: 4px;
}

.mini-supergrid-cell-inner {
  margin-top: 4px;
}

/* 去掉列选中的背景色 */
.mini-supergrid-cellselected {
  background-color: transparent !important;
}
</style>