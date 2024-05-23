<template>
  <a-spin :spinning="spinning" style="height: calc(100%)" id="mySpin">
    <div style="height: calc(100%)">
      <a-form layout="inline" style="z-index: 99;">
        <a-row>
          <a-col :span="4">
            <a-form-item style="width: 100%" :labelCol="labelCol" :wrapperCol="wrapperCol" label="名称">
              <a-input placeholder="请输入任务编码和名称" v-model="queryParam.name"  allowClear />
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
          <a-col :span="16" style="padding-left: 15px;">
            <a-form-item class="space-btn">
              <a-button @click="onSearch">搜索</a-button>
              <a-button @click="onReset">重置</a-button>
<!--              <a-button @click="onAddTask(1,false)" type="primary">新建任务</a-button>-->
              <a-dropdown>
                <a-menu slot="overlay" @click="handleMenuClick">
                  <a-menu-item key="1">
                    新建根任务
                  </a-menu-item>
                  <a-menu-item key="2">
                    新建子任务
                  </a-menu-item>
                </a-menu>
                <a-button> 新建任务 <a-icon type="down" /> </a-button>
              </a-dropdown>
              <a-button @click="onEditTask">修改任务</a-button>
              <a-button @click="onDeleteTask">删除任务</a-button>
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
          </a-col>
          <!--<a-col :span="6">
            <a-form-item style="color: chocolate">
              还有<span style="font-weight: bold">{{ unlinkCount }}</span>条规程作业项没有关联到具体任务
            </a-form-item>
          </a-col>-->
        </a-row>
      </a-form>
      <div id="viewProject" style="width:100%;height:calc(100vh - 110px); padding-top: 8px;"></div>
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
      <taskEdit :lineId="lineId" :repairProId="repairProgramId" :planInfo="planInfo" :visible.sync="taskEditVisible" :tasks="projectData.Tasks"
                :parent-task="parentTask" :task-info="editTask" @save_success="saveTaskSuccess"
                @cancel="cancelSave" @save_taskNo="saveTaskNo"></taskEdit>
    </a-modal>
<!--    <vxe-modal v-if="taskEditVisible" v-model="taskEditVisible" :zIndex="100" title="任务编辑" min-width="800"
               min-height="600" :showHeader="false" fullscreen resize show-overflow>
      <template v-slot>
        <taskEdit :planInfo="planInfo" :visible.sync="taskEditVisible" :tasks="projectData.Tasks"
                       :parent-task="parentTask" :task-info="editTask" @save_success="saveTaskSuccess"
                       @cancel="cancelSave"></taskEdit>
      </template>
    </vxe-modal>-->
  </a-spin>
</template>

<script>
import taskEdit from '@views/tiros/dispatch/trainplan/taskEdit'
import ProjectMenu from '@views/tiros/basic/modules/planTemplate/ProjectMenu'
import { getTrainPlanDetail, deleteTrainPlanTask, updateTrainPlanTaskNoAndWbs } from '@api/tirosDispatchApi'
import { everythingIsEmpty, UUID_V1 } from '@/utils/util'
import { getStationByGroupId } from '@api/tirosApi'
import JMultiSelectTag from '@comp/dict/JMultiSelectTag'

export default {
  name: 'taskManage',
  props: ['planInfo','visible'],
  components: {taskEdit, JMultiSelectTag},
  data() {
    return {
      spinning: false,
      isAllWorkStation: false,
      workStationList: [],
      unlinkCount: 0,
      taskEditVisible: false,
      queryParam: {
        name: '',
        groupId: '',
        workStationId:''
      },
      project: null,
      lineId:'',
      repairProgramId:'',
      projectData: {
        Tasks: []
      },
      parentTask: null,
      editTask: null,
      curAction: 0,  // 0 新增  1 修改
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
      }
    }
  },
  watch: {
    'queryParam.groupId': {
      immediate: true,
      handler (id) {
        if (everythingIsEmpty(id)) {
          this.isAllWorkStation = true
          this.queryParam.workStationId=""
        }else{this.isAllWorkStation=false
          this.queryParam.workStationId=[]}

      }
    }
  },
  computed: {
    // task() {
    //   return this.$store.getters.taskList
    // },
  },
  mounted () {
    this.loadProject()
    // this.getUnlinkCount()
  },
  methods: {
    handleMenuClick (e) {
      if (e.key === '1') {
        this.onAddTask(1,false)
      }
      if (e.key === '2') {
        this.onAddTask(1, true)
      }
    },
    onReset () {
      this.project.clearFilter();
      this.queryParam = {
        name: '',
        groupId: '',
        workStationId:''
      }
    },
    onSearch(){
      /*groupId: "zdgb"
      name: ""
      workStationId: Array(0)*/
      // debugger
      // console.log(this.project)
      this.project.filter((task) => {
        let flag = true
        if (this.queryParam.name) {
          flag = flag && (task.Name.indexOf(this.queryParam.name) >= 0 ||  task.ID== this.queryParam.name)
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
      });
    },
    checkExistAnyOne (arr1, arr2) {
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
    changeWorkGroup (value) {
      getStationByGroupId(value).then((res) => {
        this.workStationList = res.result
      })
    },
    /**
     * 加载甘特图信息
     */
    loadProject () {
      mini.parse()
      this.project = new PlusProject()
      this.project.setStyle('width:100%;height:100%')
      this.project.setBorderStyle('border:1px dotted #cdcdcd')
      const columns = [
        new PlusProject.IDColumn(),
        // new PlusProject.StatusColumn(),
        new PlusProject.NameColumn(),
        // new PlusProject.PredecessorLinkColumn(),
        // new PlusProject.PercentCompleteColumn(),
        new PlusProject.DurationColumn(),
        new PlusProject.StartColumn(),
        new PlusProject.FinishColumn(),
        new PlusProject.WorkColumn(),
        {
          header: "班组<br/>Multi",
          field: "workGroupName",
          width: 150
        },
        {
          name: "dayIndex",
          header: "第几天？<br/>String",
          field: "dayIndex",
          width: 80
        },
        {
          name: 'finishStatus',
          header: "完成状态<br/>String",
          field: "status_dictText",
          width: 80
        }
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
      this.project.allowOrderProject = false;

      //设置时间线
      this.project.setTimeLines([
        { date: new Date(), text: '', position: 'bottom', style: 'width:2px;background:red;' },
      ])

      //设置刻度
      this.project.setTopTimeScale('month')
      this.project.setBottomTimeScale('day')

      //设置表格右键菜单 表格Body
      const menu = new ProjectMenu();
      this.project.setTableBodyMenu(menu);
      menu.editTask.on('click', (e) => {
        this.onEditTask()
      })
      menu.deleteTask.on("click", (e) => {
        this.onDeleteTask()
      });
      menu.beforeAddTask.on("click", (e) => {
        this.onAddTask(0,false)
      });
      menu.afterAddTask.on("click", (e) => {
        this.onAddTask(1,false)
      });
      menu.addChildren.on("click", (e) => {
        this.onAddTask(0, true)
      });
      menu.on("beforeopen", function (e) {
        const el = $(e.htmlEvent.target).closest(".mini-supergrid-row");
        if (el.length === 0) {
          e.cancel = true;
        }
      });

      // 设置行高
      this.project.setRowHeight(28)

      // 双击任务
      this.project.on('taskdblclick', ({ task }) =>
      {
        this.onEditTask()
      });

      this.customGanttRender()
      // 加载数据
      // this.project.loadData({})
      this.getTemplateInfo()
    },
    /**
     * 加载计划模版详情
     * @param plan
     */
    getTemplateInfo() {
      return new Promise((resolve, reject) => { 
        this.parentTask = null
        this.editTask = null
        this.spinning = true
        getTrainPlanDetail(this.planInfo.id).then((res) => {
          if (res.success) {
            this.repairProgramId = res.result.repairProgramId
            this.lineId = res.result.lineId
            this.projectData = res.result
            // 数据加载到甘特图
            this.projectData.StartDate = new Date(this.projectData.StartDate)
            this.projectData.FinishDate = new Date(this.projectData.FinishDate)
            // console.log('time', this.$moment(new Date()).format("hh:mm:ss"))
            this.projectData.Tasks = mini.arrayToTree(this.projectData.Tasks, 'children', 'UID', 'ParentId')
            this.project.loadData(this.projectData)
            // console.log('time', this.$moment(new Date()).format("hh:mm:ss"))
            setTimeout(()=>{
              // project.scrollIntoView(project.getTaskByID(92));
              let num=this.project.getXByDate(new Date());
              if (num > 200) {
                num = num - 200;
              }
              // console.log(this.project.ganttView.setScrollLeft)
              this.project.ganttView.setScrollLeft(num);
              // this.project.scrollToDate(this.$moment().add(5).toDate())
              
            },500)
            resolve();
          } else {
            console.error('加载计划明细失败')
            this.$message.error('加载计划明细失败', res.message)
            reject();
          }
          this.spinning = false
        }).catch(err=>{
          console.error('加载计划明细异常')
          this.$message.error('加载计划明细异常', err)
          reject();
        })
      });
    },
    /**
     * 获取未关联的规程数量
     */
    getUnlinkCount () {
      getPlanNoRelevanceCount(this.planInfo.id).then(res => {
        if (res.success) {
          this.unlinkCount = res.result
        } else {
          console.error('获取未关联的规程数量错误：', res.message)
        }
      }).catch(err => {
        console.error('获取未关联的规程数量错误：', err)
      })
    },
    /**
     * 添加任务
     */
    onAddTask(location,add_child) {
      this.curAction = 0
      // this.$router.push(`/tiros/basic/plantemplate/task/${this.planId}/${null}`)
      if (add_child) {
        const task = this.project.getSelected()
        if (!task) {
          this.$message.error('请选择一条上级任务')
          return
        }
        this.parentTask = task
      } else {
        this.parentTask = null
      }

      const newTask = this.project.newTask();
      newTask.UID = UUID_V1()
      newTask.Name = '<新增任务>';    //初始化任务属性

      const selectedTask = this.project.getSelected();
      if (selectedTask) {
        if(add_child) {
          this.project.addTask(newTask, "add", selectedTask);       //加入到选中任务之内
        } else {
          if(location===0) {
            this.project.addTask(newTask, "before", selectedTask);   //插入到到选中任务之前
          } else {
            this.project.addTask(newTask, "after", selectedTask);   //插入到到选中任务之前
          }
        }
      } else {
        this.project.addTask(newTask);
      }

      newTask.taskName=newTask.Name;
      newTask.taskId = newTask.UID;
      newTask.taskNo = newTask.ID;
      this.editTask = newTask
      this.taskEditVisible = true
    },
    /**
     * 修改任务
     */
    onEditTask () {
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
    onDeleteTask () {
      const task = this.project.getSelected();
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
            deleteTrainPlanTask(params).then(res => {
              if (res.success) {
                thiz.getTemplateInfo().then(()=>{
                  thiz.onSearch();
                })
                thiz.$message.success('删除任务成功')
              } else {
                thiz.$message.error(res.message)
              }
            }).catch(e => {
              thiz.$message.error(e)
            })
          }
        });
      }
    },
    cancel () {
      this.$emit('update:visible', false)
    },
    saveTaskSuccess () {
      /*// 重新加载模版信息
      this.getTemplateInfo()*/
    },
    cancelSave () {
      this.taskEditVisible=false
      if (this.curAction === 0) {
        this.project.removeTask(this.editTask);
      }
    },
    // 保存任务序号
    saveTaskNo () {
      const taskNos = this.getTaskNos()
      this.$message.success('保存任务成功')
       updateTrainPlanTaskNoAndWbs(taskNos).then(res => {
         if (!res.success) {
           console.error('保存任务序号失败：', res.message)
         } else {
           // 重新加载模版信息
           this.getTemplateInfo().then(()=>{
             this.onSearch();
           })
         }
       }).catch(err => {
         console.error('保存任务序号异常：', err)
       })
    },
    getTaskNos () {
      const tasks = this.project.getTaskList()
      const taskNos = []
      tasks.map(item => {
        taskNos.push({
          planId: this.planInfo.id,
          UID: item.UID,
          ID: item.ID
        })
      })
      return taskNos
    },
    customGanttRender () {
      // 自定义单元格显示样式
      this.project.on("drawcell", (e) => {
        let task = e.record, column = e.column, field = e.field;
        //单元格样式
        if (column.name == "Name") {
          e.cellCls = "name-col-cls";
        }

        //行样式
        if (task.Summary == 1) {
          e.rowCls = "summary-row-cls";
        }

        if (task.Summary == 1 && (column.name !='Name' && column.name !='ID' && column.name !='Start' && column.name !='Finish' )) {
          e.cellHtml = ''
        }

        ////自定义单元格Html。如果是工期列, 并且工期大与5天, 显示红色
        /*if (field == "Name" && task.Duration > 5) {
          e.cellHtml = '<b style="color:red;">' + task.Name + '</b>';
        }*/

        /* if (field == "Name" && task.Duration <= 2) {
           e.cellHtml = '<span style="color:blue;">' + task.Name + '</span>';
         }*/

        /* if (task.Duration == 0) {
           e.rowCls = "deletetask";
         }*/
      });
      const thiz = this

      //1)自定义条形图外观显示
      this.project.on("drawitem", (e) => {
        let item = e.item;
        let left = e.itemBox.left,
          top = e.itemBox.top,
          width = e.itemBox.width,
          height = e.itemBox.height;

        if (!item.Summary && !item.Milestone) {
          if (e.baseline) {    //区分比较基准

          } else {
            // 判断当前时间是否超出实际完成时间，然后再判断任务的状态
            let planFinish = thiz.$moment(item.Finish).format('YYYY-MM-DD');
            let planStart = thiz.$moment(item.Start).format('YYYY-MM-DD');
            let actFinish = item.ActualFinish ? thiz.$moment(item.ActualFinish).format('YYYY-MM-DD') : "";
            let actStart = item.ActualStart ? thiz.$moment(item.ActualStart).format('YYYY-MM-DD') : "";
            let now = thiz.$moment().format('YYYY-MM-DD');

            let theClass = "base-item";
            if (actFinish) {
              // 已完成，判断是正常完成、提前完成、延后完成
              if (actFinish > planFinish) {
                // 延期完成
                theClass = "delay-finish-item bg-red";
              } else if (actFinish === planFinish) {
                // 正常完成
                theClass = "normal-finish-item bg-blue";
              } else if (actFinish < planFinish) {
                // 提前完成
                theClass = "before-finish-item bg-green";
              }
            } else {
              // 没完成， 判断是否开始，判断当前已否已超期
              if (actStart) {
                // 已经开始了
                if (now > planFinish) {
                  theClass = "delay-finish-item bg-red";
                }
              } else {
                // 还没开始
                if (now > planStart) {
                  theClass = "delay-finish-item bg-red";
                } else {
                  theClass = "no-start-item";
                }
              }
            }

            let percentWidth = width * (item.PercentComplete / 100);
            e.itemHtml = '<div id="' + item._id + '" class="' + theClass + '" style="left:' + left + 'px;top:' + top + 'px;width:' + width + 'px;height:' + (height) + 'px;">';
            e.itemHtml += '<div style="width:' + (percentWidth) + 'px;" class="finish-progress-item"></div>';
            e.itemHtml += '</div>';

            //右侧的文本
            //e.itemHtml += '<div style="left:' + (left + width + 4) + 'px;top:' + (top - 3) + 'px;height:' + (height) + 'px;position:absolute;z-index:100;">' + item.Name + '</div>';

          }
        } else {
          // 如果是summary
          if (item.Summary) {
            // let html='<div id="2" class="mini-gantt-item  mini-gantt-summary" style="left:83px;top:4px;width:76px;"><div class="mini-gantt-summary-left"></div><div class="mini-gantt-summary-right"></div></div>'
          }
        }
      });
      //2)自定义条形图提示信息
      this.project.on('itemtooltipneeded', (e) => {
        let task = e.task;

        if (e.baseline) {    //区分比较基准
        } else {
          e.tooltip = "<div>计划名称：" + task.Name + "</div>"
            +   "<div ><div style='float:left;'>进度：<b>"+task.PercentComplete + "%</b></div>"
            +   "<div style='float:right;'>工期："+task.Duration + "日</div></div>"
            + "<div style='clear:both;'>计划开始：" + mini.formatDate(task.Start, 'yyyy-MM-dd') + "</div>"
            + "<div>计划完成：" + mini.formatDate(task.Finish, 'yyyy-MM-dd') + "</div>"
            + "<div>实际开始：" + mini.formatDate(task.ActStart, 'yyyy-MM-dd') + "</div>"
            + "<div>实际完成：" + mini.formatDate(task.ActFinish, 'yyyy-MM-dd') + "</div>";
        }
      });

      //3)自定义条形图label内容
      this.project.on("drawitem", (e) => {
        let item = e.item;
        e.label =item.Name+" "+ e.task["PercentComplete"] + "%";
        e.labelAlign = "left";
      });
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
  }
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

.mini-supertree-nodetext{
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