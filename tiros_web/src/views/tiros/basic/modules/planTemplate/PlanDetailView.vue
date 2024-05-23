<template style="height: 100%;">
  <div style="height: 100%" class="planTabs">
    <a-tabs default-active-key="2" style="height: calc(100vh - 24px)">
      <a-space slot="tabBarExtraContent" style="margin-right: 50px">
        <a-button @click="viewTypeTable = !viewTypeTable"> {{viewTypeTable ? '切换甘特图' : '切换表格视图'}}</a-button>
        <a-dropdown :class="{'info-view-hidden': viewTypeTable}">
          <a-menu slot="overlay">
            <a-menu-item key="1" @click="collapseAll">折叠所有</a-menu-item>
            <a-menu-item key="2" @click="expandAll">展开所有</a-menu-item>
            <a-menu-item key="3" @click="expandFirst">展开第一层</a-menu-item>
          </a-menu>
          <a-button>折叠/展开<a-icon type="down" /> </a-button>
        </a-dropdown>
      </a-space>
      <a-tab-pane key="1" tab="基本信息">
        <a-card>
          <a-descriptions>
            <a-descriptions-item label="模板编码">{{ planInfo.code }}</a-descriptions-item>
            <a-descriptions-item label="模板名称">{{ planInfo.tpName }}</a-descriptions-item>
            <a-descriptions-item label="计划工期">{{ planInfo.duration }}</a-descriptions-item>
            <a-descriptions-item label="所属路线">{{ planInfo.lineName }}</a-descriptions-item>
            <a-descriptions-item label="编组数量">{{ planInfo.groupQuantity }}</a-descriptions-item>
            <a-descriptions-item label="修程类型">{{ planInfo.repairProgramName }}</a-descriptions-item>
            <a-descriptions-item label="状态">{{ planInfo.status_dictText }}</a-descriptions-item>
            <a-descriptions-item label="基准日期">{{ planInfo.baseDate }}</a-descriptions-item>
            <a-descriptions-item label="关联规程">{{ planInfo.reguName }}</a-descriptions-item>
            <a-descriptions-item label="财务项目">{{planInfo.fdProjectCode}} - {{ planInfo.fdProjectName }}</a-descriptions-item>
            <a-descriptions-item label="财务任务">{{ planInfo.fdTaskCode }} - {{ planInfo.fdTaskName }}</a-descriptions-item>
            <!-- <a-descriptions-item label="开支编码">{{ planInfo.fdCostType }}</a-descriptions-item> -->
            <a-descriptions-item label="备注">{{ planInfo.remark }}</a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-tab-pane>
      <a-tab-pane key="2" tab="任务信息">
        <div id="viewProject" :class="{'info-view-hidden': viewTypeTable}" style="width: 100%; height: calc(100vh - 76px); padding-top: 8px"></div>
        <PlanInfoItem :class="{'info-view-hidden': !viewTypeTable}" v-model="planInfoTreeList" style="height: calc(100vh - 76px)"></PlanInfoItem>
      </a-tab-pane>
      <a-tab-pane key="8" tab="关联规程">
        <div style="height: calc(100% - 55px)">
          <vxe-table border :align="allAlign" :data="planRegus" max-height="100%">
            <vxe-table-column field="workGroupName" title="班组" width="8%"></vxe-table-column>
            <vxe-table-column
              field="title"
              title="名称"
              align="left"
              header-align="center"
              width="25%"
              tree-node
            ></vxe-table-column>
            <vxe-table-column field="type_dictText" title="类型" width="8%"></vxe-table-column>
            <vxe-table-column field="method_dictText" title="维保手段" width="8%"></vxe-table-column>
            <vxe-table-column field="qualityLevel" title="质保等级" width="8%"></vxe-table-column>
            <vxe-table-column field="outsource_dictText" title="是否委外" width="6%"></vxe-table-column>
            <vxe-table-column
              field="safeNotice"
              title="安全提示"
              align="left"
              header-align="center"
              width="25%"
            ></vxe-table-column>
            <vxe-table-column field="remark" title="备注" align="left" header-align="center"></vxe-table-column>
          </vxe-table>
        </div>
      </a-tab-pane>
      <!-- <a-tab-pane key="3" tab="必换件">
        <div style="height: calc(100% - 55px)">
          <vxe-table border :align="allAlign" :data="planMustReplaces" max-height="100%">
            <vxe-table-column
              field="code"
              title="编码"
              width="150"
              align="left"
              header-align="center"
            ></vxe-table-column>
            <vxe-table-column field="name" title="名称" align="left" header-align="center"></vxe-table-column>
            <vxe-table-column field="amount" title="数量" width="150"></vxe-table-column>
            <vxe-table-column field="unit" title="单位"></vxe-table-column>
            <vxe-table-column field="spec" title="必换件描述" align="left" header-align="center"></vxe-table-column>
          </vxe-table>
        </div>
      </a-tab-pane> -->
      <a-tab-pane key="5" tab="物料">
        <div style="height: calc(100% - 55px)">
          <vxe-table border :align="allAlign" :data="planMaterials" max-height="100%">
            <vxe-table-column field="workGroupName" title="班组" width="8%"></vxe-table-column>
            <vxe-table-column field="code" title="编码"></vxe-table-column>
            <vxe-table-column field="name" title="物料名称"></vxe-table-column>
            <vxe-table-column field="useCategory_dictText" title="物料分类"></vxe-table-column>
            <vxe-table-column field="amount" title="所需数量"></vxe-table-column>
            <vxe-table-column field="unit" title="单位"></vxe-table-column>
          </vxe-table>
        </div>
      </a-tab-pane>
      <a-tab-pane key="4" tab="工器具">
        <div style="height: calc(100% - 55px)">
          <vxe-table border :align="allAlign" :data="planTools" max-height="100%">
            <vxe-table-column field="workGroupName" title="班组" width="8%"></vxe-table-column>
            <vxe-table-column field="code" title="编码"></vxe-table-column>
            <vxe-table-column field="name" title="工器具名称"></vxe-table-column>
            <vxe-table-column field="category1_dictText" title="工器具类型"></vxe-table-column>
            <vxe-table-column field="amount" title="所需数量"></vxe-table-column>
            <vxe-table-column field="unit" title="单位"></vxe-table-column>
          </vxe-table>
        </div>
      </a-tab-pane>

      <!-- <a-tab-pane key="6" tab="表单">
        <div style="height: calc(100% - 55px)">
          <vxe-table border :align="allAlign" :data="planForms" max-height="100%">
            <vxe-table-column field="formName" title="表单名称"></vxe-table-column>
            <vxe-table-column field="formType_dictText" title="表单类型"></vxe-table-column>
            <vxe-table-column field="copies" title="填写数量"></vxe-table-column>
            <vxe-table-column field="assetTypeName" title="关联设备"></vxe-table-column>
            <vxe-table-column field="remark" title="备注"></vxe-table-column>
          </vxe-table>
        </div>
      </a-tab-pane> -->
      <a-tab-pane key="7" tab="人员">
        <div style="height: calc(100% - 55px)">
          <vxe-table border :align="allAlign" :data="planPersons" max-height="100%">
            <vxe-table-column field="workGroupName" title="班组" width="8%"></vxe-table-column>
            <vxe-table-column field="amount" title="所需人数"></vxe-table-column>
            <vxe-table-column field="requirePostion" title="岗位要求"></vxe-table-column>
            <vxe-table-column field="requireTech" title="技能要求"></vxe-table-column>
            <vxe-table-column field="requireCertificate" title="证书要求"></vxe-table-column>
            <vxe-table-column field="remark" title="备注"></vxe-table-column>
          </vxe-table>
        </div>
      </a-tab-pane>
      <!--      <a-button @click="cancel" type="dashed" slot="tabBarExtraContent">
              关闭
            </a-button>-->
    </a-tabs>
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
        :planInfo="planInfo"
        :tasks="projectData.Tasks"
        :parent-task="parentTask"
        :task-info="viewTask"
      ></TaskEditModal>
    </a-modal>
  </div>
</template>

<script>
// import PlanBasicInfo from './PlanBasicInfo'

import { getPlanDetail, getPlanRelation } from '@/api/tirosApi'
import TaskEditModal from '@views/tiros/basic/modules/planTemplate/TaskEditModal'
import PlanInfoItem from '@views/tiros/basic/modules/planTemplate/PlanInfoItem'

export default {
  name: 'PlanDetailView',
  components: { TaskEditModal, PlanInfoItem },
  props: ['planInfoId', 'visible'],
  created() {
    this.getDetail()
  },
  mounted() {
    // console.log('planInfo:', this.planInfo)
  },
  data() {
    return {
      project: null,
      taskViewVisible: false,
      projectData: {
        Tasks: [],
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
      parentTask: {},
      viewTask: {},
      viewTypeTable: false
    }
  },
  methods: {
    getDetail() {
      // 获取计划信息及任务信息
      getPlanDetail(this.planInfoId).then((res) => {
        this.planInfo = res.result
        this.loadProject()
      })

      // 获取关联信息
      getPlanRelation(this.planInfoId).then((res) => {
        if (res.success) {
          this.planMustReplaces = res.result.mustReplaces
          this.planTools = res.result.tools
          this.planMaterials = res.result.materials
          this.planForms = res.result.forms
          this.planPersons = res.result.persons
          this.planRegus = res.result.repairPlanReguInfo
        }
      })
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
        { date: new Date(), text: '当前时间', position: 'bottom', style: 'width:2px;background:red;' }
      ])*/

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

      // 加载数据
      this.projectData.StartDate = new Date(this.planInfo.StartDate)
      this.projectData.FinishDate = new Date(this.planInfo.FinishDate)
      this.projectData.Tasks = mini.arrayToTree(this.planInfo.Tasks, 'children', 'UID', 'ParentId')
      this.project.loadData(this.projectData)
    },
    cancel() {
      this.$emit('update:visible', false)
    },
    /**
     * 查看任务
     */
    onViewTask() {
      this.viewTask = this.project.getSelected()
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
  },
}
</script>

<style>
.planTabs > .ant-tabs > .ant-tabs-content {
  height: 100% !important;
}
</style>
<style lang="less" scoped>
.info-view-hidden{
  display: none;
}
</style>