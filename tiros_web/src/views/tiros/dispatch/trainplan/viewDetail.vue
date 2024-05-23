<template>
  <div>
    <a-tabs default-active-key='2'>
      <a-space slot='tabBarExtraContent'>
        <a-button @click='viewTypeTable = !viewTypeTable'> {{ viewTypeTable ? '切换甘特图' : '切换表格视图' }}</a-button>
        <a-dropdown :class="{'info-view-hidden': viewTypeTable}">
          <a-menu slot='overlay'>
            <a-menu-item key='1' @click='collapseAll'>折叠所有</a-menu-item>
            <a-menu-item key='2' @click='expandAll'>展开所有</a-menu-item>
            <a-menu-item key='3' @click='expandFirst'>展开第一层</a-menu-item>
          </a-menu>
          <a-button>折叠/展开
            <a-icon type='down' />
          </a-button>
        </a-dropdown>
      </a-space>
      <a-tab-pane key='1' tab='基本信息'>
        <a-card>
          <a-descriptions>
            <a-descriptions-item label='计划名称'>{{ planInfo.planName }}</a-descriptions-item>
            <a-descriptions-item label='所属车辆段'>{{ planInfo.depotName }}</a-descriptions-item>
            <a-descriptions-item label='所属车间'>{{ planInfo.workshopName }}</a-descriptions-item>
            <a-descriptions-item label='计划工期'>{{ planInfo.duration }}</a-descriptions-item>
            <a-descriptions-item label='所属路线'>{{ planInfo.lineName }}</a-descriptions-item>
            <a-descriptions-item label='维修车辆'>{{ planInfo.trainNo }}</a-descriptions-item>
            <a-descriptions-item label='计划模版'>{{ planInfo.planTemplateName }}</a-descriptions-item>
            <a-descriptions-item label='修程类型'>{{ planInfo.repairProgramName }}</a-descriptions-item>
            <a-descriptions-item label='关联规程'>{{ planInfo.reguName }}</a-descriptions-item>
            <a-descriptions-item label='计划开始'>{{ planInfo.StartDate }}</a-descriptions-item>
            <a-descriptions-item label='计划结束'>{{ planInfo.FinishDate }}</a-descriptions-item>
            <a-descriptions-item label='备注'>{{ planInfo.remark }}</a-descriptions-item>
            <a-descriptions-item label='财务项目'>{{ planInfo.fdProjectCode }} - {{ planInfo.fdProjectName }}
            </a-descriptions-item>
            <a-descriptions-item label='财务任务'>{{ planInfo.fdTaskCode }} - {{ planInfo.fdTaskName }}
            </a-descriptions-item>
            <!-- <a-descriptions-item label="开支编码">{{ planInfo.fdCostType }}</a-descriptions-item> -->
          </a-descriptions>
        </a-card>
      </a-tab-pane>
      <a-tab-pane key='2' tab='任务信息'>
        <a-spin :spinning='spinning'>
          <div id='viewProject' :class="{'info-view-hidden': viewTypeTable}"
               :style="{ width: '100%', height: 'calc(100vh - 120px)', paddingTop: '8px' }"></div>
          <PlanInfoItem :class="{'info-view-hidden': !viewTypeTable}" v-model='planInfoTreeList'
                        style='height: calc(100vh - 120px)'></PlanInfoItem>
        </a-spin>
      </a-tab-pane>
      <!-- <a-tab-pane key="3" tab="必换件">
        <vxe-table border :align="allAlign" :data="planMustReplaces"  :max-height="getHeight()">
          <vxe-table-column field="code" title="编码" align="left" header-align="center" ></vxe-table-column>
          <vxe-table-column field="name" title="名称" align="left" header-align="center"></vxe-table-column>
          <vxe-table-column field="amount" title="数量"  width="150" align="right" header-align="center"></vxe-table-column>
          <vxe-table-column field="unit" title="单位" width="100"></vxe-table-column>
          &lt;!&ndash; <vxe-table-column field="structType_dictText" title="类型"></vxe-table-column> &ndash;&gt;
          <vxe-table-column field="spec" title="必换件描述" align="left" header-align="center"></vxe-table-column>
        </vxe-table>
      </a-tab-pane> -->
      <a-tab-pane key='5' tab='物料'>
        <vxe-table border :align='allAlign' :data='planMaterials' :max-height='getHeight()'>
          <vxe-table-column field='code' title='编码'></vxe-table-column>
          <vxe-table-column field='name' title='物料名称' align='left' header-align='center'></vxe-table-column>
          <vxe-table-column field='category1_dictText' title='物料分类'></vxe-table-column>
          <vxe-table-column field='amount' title='所需数量' align='right' header-align='center'></vxe-table-column>
          <vxe-table-column field='unit' title='单位' width='100'></vxe-table-column>
        </vxe-table>
      </a-tab-pane>
      <a-tab-pane key='4' tab='工器具'>
        <div>
          <vxe-table border :align='allAlign' :data='planTools' :max-height='getHeight()'>
            <vxe-table-column field='code' title='编码'></vxe-table-column>
            <vxe-table-column field='name' title='工器具名称' align='left' header-align='center'></vxe-table-column>
            <vxe-table-column field='category1_dictText' title='工器具类型'></vxe-table-column>
            <vxe-table-column field='amount' title='所需数量' align='right' header-align='center'></vxe-table-column>
            <vxe-table-column field='unit' title='单位' width='100'></vxe-table-column>
          </vxe-table>
        </div>
      </a-tab-pane>
      <a-tab-pane key='6' tab='表单'>
        <vxe-table border :align='allAlign' :data='planForms' :max-height='getHeight()'>
          <vxe-table-column field='title' title='表单名称' min-width='180' align='left' header-align='center'>
            <template v-slot='{ row }'>
              <a href='javascript:;' @click.stop='jumpFormInfo(row)'>{{ row.title }}</a>
            </template>
          </vxe-table-column>
          <vxe-table-column field='formType_dictText' title='表单类型' width='120'></vxe-table-column>
          <vxe-table-column field='reguName' title='所属规程' width='120'></vxe-table-column>
          <vxe-table-column field='reguVersion' title='规程版本' width='120'></vxe-table-column>
          <vxe-table-column field='trainStructName' title='关联设备' width='160'></vxe-table-column>
          <vxe-table-column field='remark' title='备注' width='160' align='left' header-align='center'></vxe-table-column>
          <vxe-table-column field='orderName' v-if='planInfo.status !== 0' title='所属工单' width='120' align='left'
                            header-align='center'>
            <template v-slot='{ row }'>
              <a href='javascript:;' @click.stop='viewOrder(row)'>{{ row.orderName }}</a>
            </template>
          </vxe-table-column>
          <vxe-table-column v-if='planInfo.status !== 0' field='status_dictText' title='是否已填'
                            width='100'></vxe-table-column>
        </vxe-table>
      </a-tab-pane>
      <a-tab-pane key='7' tab='人员'>
        <vxe-table border :align='allAlign' :data='planPersons' :max-height='getHeight()'>
          <vxe-table-column field='amount' title='所需人数'></vxe-table-column>
          <vxe-table-column field='requirePostion' title='岗位要求' align='left' header-align='center'></vxe-table-column>
          <vxe-table-column field='requireTech' title='技能要求' align='left' header-align='center'></vxe-table-column>
          <vxe-table-column field='requireCertificate' title='证书要求' align='left'
                            header-align='center'></vxe-table-column>
          <vxe-table-column field='remark' title='备注' align='left' header-align='center'></vxe-table-column>
        </vxe-table>
      </a-tab-pane>
      <a-tab-pane key='8' tab='关联规程'>
        <vxe-table border :align='allAlign' :data='planRegus' :max-height='getHeight()'>
          <vxe-table-column field='title' title='名称' width='25%' align='left' header-align='center'></vxe-table-column>
          <vxe-table-column field='type_dictText' title='类型' width='8%'></vxe-table-column>
          <vxe-table-column field='method_dictText' title='维保手段' width='8%'></vxe-table-column>
          <vxe-table-column field='qualityLevel' title='质保等级' width='8%'></vxe-table-column>
          <vxe-table-column field='outsource_dictText' title='是否委外' width='6%'></vxe-table-column>
          <vxe-table-column field='safeNotice' title='安全提示' width='25%' align='left'
                            header-align='center'></vxe-table-column>
          <vxe-table-column field='remark' title='备注' align='left' header-align='center'></vxe-table-column>
        </vxe-table>
      </a-tab-pane>
      <!--      <a-button v-if="visible" @click="cancel" type="dashed" slot="tabBarExtraContent">
              关闭
            </a-button>-->
    </a-tabs>
    <a-modal
      centered
      :width="'100%'"
      :title="'任务查看'"
      dialogClass='fullDialog no-footer'
      :visible='taskViewVisible'
      @cancel='cancelView'
      :footer='null'
      :destroyOnClose='true'
    >
      <taskEdit
        :readOnly='true'
        :planInfo='planInfo'
        :tasks='projectData.Tasks'
        :parent-task='parentTask'
        :task-info='viewTask'
      ></taskEdit>
    </a-modal>
    <ViewWorkForm ref='viewWorkForm'></ViewWorkForm>
    <view-work-order-modal ref='viewOrderModal'></view-work-order-modal>
  </div>
</template>

<script>
import { getTrainPlanDetail, getTrainPlanRelevanceInfo, listTrainPlanForm } from '@api/tirosDispatchApi'
import { getFormEntityList } from '@api/tirosApi'
import ViewWorkOrderModal from '@views/tiros/dispatch/workOrder/ViewWorkOrderModal'
import taskEdit from '@views/tiros/dispatch/trainplan/taskEdit'
import PlanInfoItem from '@views/tiros/basic/modules/planTemplate/PlanInfoItem'
import ViewWorkForm from '@views/tiros/dispatch/workOrder/ViewWorkForm'

export default {
  name: 'viewDetail',
  components: { taskEdit, PlanInfoItem, ViewWorkForm, ViewWorkOrderModal },
  props: ['businessKey', 'visible', 'header', 'fromFlow'],
  created() {
    this.getDetail()
  },
  data() {
    return {
      project: null,
      taskViewVisible: false,
      projectData: {
        Tasks: []
      },
      planMustReplaces: [],
      planTools: [],
      planMaterials: [],
      planForms: [],
      planPersons: [],
      planRegus: [],
      planInfo: {},
      planInfoTreeList: [],
      allAlign: 'center',
      spinning: false,
      parentTask: {},
      viewTask: {},
      viewTypeTable: false
    }
  },
  methods: {
    getHeight() {
      if (this.fromFlow) {
        return this.deLayout.bodyHeight - 180
      } else {
        return this.deLayout.bodyHeight - 120
      }
    },
    getDetail() {
      this.spinning = true
      // 获取计划信息及任务信息
      getTrainPlanDetail(this.businessKey).then((res) => {
        this.planInfo = res.result
        this.loadProject()
        this.loadFormList()
      })

      // 获取关联信息
      getTrainPlanRelevanceInfo(this.businessKey).then((res) => {
        if (res.success) {
          this.planMustReplaces = res.result.mustReplaces
          this.planTools = res.result.tools
          this.planMaterials = res.result.materials
          // this.planForms = res.result.forms
          this.planPersons = res.result.persons
          this.planRegus = res.result.repairPlanReguInfo
        }
      })

      this.$emit('loaded')
    },
    loadFormList() {
      if (this.planInfo.status === 0 || this.planInfo.status === 1) {
        // 未审核状态显示关联表单
        listTrainPlanForm({ planId: this.planInfo.id })
          .then((res) => {
            if (res.success) {
              this.planForms = res.result
            } else {
              this.$message.error('获取计划表单失败')
              console.error('获取计划表单失败:', res.message)
            }
          })
          .catch((err) => {
            this.$message.error('获取计划表单异常')
            console.error('获取模板表单异常:', err)
          })
      } else {
        // 审核状态显示实例表单
        getFormEntityList({ planId: this.planInfo.id }).then((res) => {
          if (res.success) {
            this.planForms = res.result
            // console.log(res.result[0]);
          } else {
            this.$message.error('获取计划表单失败')
            console.error('获取计划表单失败:', res.message)
          }
        })
      }
    },
    loadProject() {
      mini.parse()
      this.planInfoTreeList = mini.arrayToTree(this.planInfo.Tasks, 'children', 'UID', 'ParentId')
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
          name: 'workGroup',
          header: '班组<br/>Multi',
          field: 'workGroupName',
          width: 120
        },
        {
          name: 'dayIndex',
          header: '第几天？<br/>String',
          field: 'dayIndex',
          width: 60
        },
        {
          name: 'finishStatus',
          header: '完成状态<br/>String',
          field: 'status',
          width: 60
        },
        {
          name: 'creatStatus',
          header: '已生成?<br/>String',
          field: 'hasGen',
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
      this.project.allowOrderProject = false

      //设置时间线
      this.project.setTimeLines([
        { date: new Date(), text: '', position: 'bottom', style: 'width:2px;background:red;' }
      ])

      //设置刻度
      this.project.setTopTimeScale('month')
      this.project.setBottomTimeScale('day')

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

      this.customGanttRender()

      // 加载数据
      this.projectData.StartDate = new Date(this.planInfo.StartDate)
      this.projectData.FinishDate = new Date(this.planInfo.FinishDate)
      this.projectData.Tasks = mini.arrayToTree(this.planInfo.Tasks, 'children', 'UID', 'ParentId')
      this.project.loadData(this.projectData)
      this.spinning = false
    },
    cancel() {
      this.$emit('update:visible', false)
    },
    customGanttRender() {
      // 自定义单元格显示样式
      this.project.on('drawcell', (e) => {
        let task = e.record,
          column = e.column,
          field = e.field

        //单元格样式
        if (column.name === 'Name') {
          e.cellCls = 'name-col-cls'
        }

        if (column.name === 'finishStatus') {
          if (task.status === 1) {
            e.cellHtml = '未开始'
          }
          if (task.status === 2) {
            e.cellHtml = '已开始'
          }
          if (task.status === 3) {
            e.cellHtml = '已暂停'
          }
        }
        if (column.name === 'creatStatus') {
          if (task.hasGen === 0) {
            e.cellHtml = '未生成工单'
          }
          if (task.hasGen === 1) {
            e.cellHtml = '已生成工单'
          }
        }


        //行样式
        if (task.Summary === 1) {
          e.rowCls = 'summary-row-cls'
        }

        if (task.Summary === 1 && column.name !== 'Name') {
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
      })
      const thiz = this

      //1)自定义条形图外观显示
      this.project.on('drawitem', (e) => {
        let item = e.item
        let left = e.itemBox.left,
          top = e.itemBox.top,
          width = e.itemBox.width,
          height = e.itemBox.height

        if (!item.Summary && !item.Milestone) {
          if (e.baseline) {
            //区分比较基准
          } else {
            // 判断当前时间是否超出实际完成时间，然后再判断任务的状态
            let planFinish = thiz.$moment(item.Finish).format('YYYY-MM-DD')
            let planStart = thiz.$moment(item.Start).format('YYYY-MM-DD')
            let actFinish = item.ActualFinish ? thiz.$moment(item.ActualFinish).format('YYYY-MM-DD') : ''
            let actStart = item.ActualStart ? thiz.$moment(item.ActualStart).format('YYYY-MM-DD') : ''
            let now = thiz.$moment().format('YYYY-MM-DD')

            let theClass = 'base-item'
            if (actFinish) {
              // 已完成，判断是正常完成、提前完成、延后完成
              if (actFinish > planFinish) {
                // 延期完成
                theClass = 'delay-finish-item bg-red'
              } else if (actFinish === planFinish) {
                // 正常完成
                theClass = 'normal-finish-item bg-blue'
              } else if (actFinish < planFinish) {
                // 提前完成
                theClass = 'before-finish-item bg-green'
              }
            } else {
              // 没完成， 判断是否开始，判断当前已否已超期
              if (actStart) {
                // 已经开始了
                if (now > planFinish) {
                  theClass = 'delay-finish-item bg-red'
                }
              } else {
                // 还没开始
                if (now > planStart) {
                  theClass = 'delay-finish-item bg-red'
                } else {
                  theClass = 'no-start-item'
                }
              }
            }

            let percentWidth = width * (item.PercentComplete / 100)
            e.itemHtml =
              '<div id="' +
              item._id +
              '" class="' +
              theClass +
              '" style="left:' +
              left +
              'px;top:' +
              top +
              'px;width:' +
              width +
              'px;height:' +
              height +
              'px;">'
            e.itemHtml += '<div style="width:' + percentWidth + 'px;" class="finish-progress-item"></div>'
            e.itemHtml += '</div>'

            //右侧的文本
            //e.itemHtml += '<div style="left:' + (left + width + 4) + 'px;top:' + (top - 3) + 'px;height:' + (height) + 'px;position:absolute;z-index:100;">' + item.Name + '</div>';
          }
        } else {
          // 如果是summary
          if (item.Summary) {
            // let html='<div id="2" class="mini-gantt-item  mini-gantt-summary" style="left:83px;top:4px;width:76px;"><div class="mini-gantt-summary-left"></div><div class="mini-gantt-summary-right"></div></div>'
          }
        }
      })
      //2)自定义条形图提示信息
      this.project.on('itemtooltipneeded', (e) => {
        let task = e.task

        if (e.baseline) {
          //区分比较基准
        } else {
          e.tooltip =
            '<div>计划名称：' +
            task.Name +
            '</div>' +
            '<div ><div style=\'float:left;\'>进度：<b>' +
            task.PercentComplete +
            '%</b></div>' +
            '<div style=\'float:right;\'>工期：' +
            task.Duration +
            '日</div></div>' +
            '<div style=\'clear:both;\'>计划开始：' +
            mini.formatDate(task.Start, 'yyyy-MM-dd') +
            '</div>' +
            '<div>计划完成：' +
            mini.formatDate(task.Finish, 'yyyy-MM-dd') +
            '</div>' +
            '<div>实际开始：' +
            mini.formatDate(task.ActStart, 'yyyy-MM-dd') +
            '</div>' +
            '<div>实际完成：' +
            mini.formatDate(task.ActFinish, 'yyyy-MM-dd') +
            '</div>'
        }
      })

      //3)自定义条形图label内容
      this.project.on('drawitem', (e) => {
        let item = e.item
        e.label = item.Name + ' ' + e.task['PercentComplete'] + '%'
        e.labelAlign = 'right'
      })
    },
    /**
     * 查看任务
     */
    onViewTask() {
      this.viewTask = this.project.getSelected()
      // console.log(this.viewTask);
      if (this.viewTask) {
        this.parentTask = null
        if (this.viewTask.ParentTaskUID) {
          let pTask = this.project.getTask(this.viewTask.ParentTaskUID)
          if (pTask) {
            this.parentTask = pTask
          }
        }
        this.taskViewVisible = true
      } else {
        this.$message.error('请选择要修改的任务')
      }
    },
    cancelView() {
      this.taskViewVisible = false
    },
    collapseAll() {
      this.project.collapseAll()
    },
    expandAll() {
      this.project.expandAll()
    },
    expandFirst() {
      this.project.collapseAll()
      this.project.expandLevel(0)
    },
    jumpFormInfo(row) {
      if (this.planInfo.status === 0 || this.planInfo.status === 1) {
        // 未审核状态 查看关联表单明细
        let obj = {
          planFormId: row.id,
          instType: row.formType,
          formObjId: row.objId,
          formInstId: null,
          workRecordType: row.workRecordType,
          title: row.title,
          formName: row.title
        }
        this.$refs.viewWorkForm.showModalNoData(obj, this.planInfo.trainNo)
      } else {
        // 未审核状态 查看实例表单明细
        // if (row.formType === 1) {
          let obj = {
            planFormId: row.planFormId,
            instType: row.formType,
            formObjId: row.formObjId,
            formInstId: row.id,
            workRecordType: row.workRecordType,
            title: row.title,
            formName: row.title
          }
          this.$refs.viewWorkForm.showModal(obj, row.trainNo)
        // } else {
        //   let obj = {
        //     id: row.task2InstId,
        //     instType: row.formType,
        //     formInstId: row.formObjId,
        //     title: row.title
        //   }
        //   this.$refs.viewWorkForm.showModal(obj, row.trainNo)
        // }
      }
    },
    viewOrder(row) {
      this.$refs.viewOrderModal.showModal(row.orderId)
    }
  }
}
</script>

<style lang='less' scoped>
.info-view-hidden {
  display: none;
}
</style>