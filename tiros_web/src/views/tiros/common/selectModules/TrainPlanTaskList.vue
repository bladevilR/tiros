<template>
  <!-- 列计划任务选择 -->
  <a-modal
    width="90%"
    title="列计划任务选择"
    centered
    :visible="visible"
    :bodyStyle="{ height: '80vh' }"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-row>
      <a-col :md="4" :sm="24" style="border-right: 1px dotted #D3E1F1; height: calc(80vh - 20px);margin-right: 8px;padding: 0px;">
        <div style="height: calc(80vh - 40px); overflow: auto">
          <a-menu mode="inline" @click="handleMenuClick" :default-selected-keys="defaultKey">
            <a-menu-item v-for="menu in planList" :key="menu.id">
              <a-icon type="pie-chart" />
              <span>{{ menu.planName }}</span>
            </a-menu-item>
          </a-menu>
        </div>
      </a-col>
      <a-col :md="19" :sm="24">
        <a-spin :spinning="spinning">
          <div id="viewProject" style="width:100%;height:calc(80vh - 20px); padding-top: 8px;"></div>
        </a-spin>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script>
import { getSerialList } from '@/api/tirosGroupApi'
import { getTrainPlanDetail } from '@api/tirosDispatchApi'
export default {
  name: 'TrainPlanTaskList',
  data () {
    return {
      spinning: false,
      defaultKey: [],
      planList: [],
      curPlan: null,
      visible: false,
      projectData: {},
      queryParam: {
        searchText: '',
        lineId:'',
        // 0 草稿 1 审批中 2 已审批
        status:'2',
        pageNo: 1,
        pageSize: 10000,
      },
    }
  },
  methods: {
    showModal(lineId) {
      if (lineId) {
        this.queryParam.lineId = lineId
      }
      this.visible = true
      this.loadPlanList()
    },
    loadPlanList () {
      getSerialList(this.queryParam).then((res) => {
        if (res.success) {
          this.planList = res.result.records
          if (this.planList && this.planList.length > 0) {
            this.defaultKey.push(this.planList[0].id)
            this.initGantt()
            this.loadPlanTask(this.planList[0].id)
          }
        } else {
          this.$message.error('加载列计划失败')
          console.error('加载列计划失败', res.message)
        }
      }).catch(err => {
        this.$message.error('加载列计划异常')
        console.error('加载列计划异常', err)
      })
    },
    handleMenuClick (e) {
      // console.log('click menu:', e)
      this.loadPlanTask(e.key)
    },
    loadPlanTask (planId) {
      this.spinning = true
      getTrainPlanDetail(planId).then((res) => {
        if (res.success) {
          this.projectData = res.result
          // 数据加载到甘特图
          this.projectData.StartDate = new Date(this.projectData.StartDate)
          this.projectData.FinishDate = new Date(this.projectData.FinishDate)
          this.projectData.Tasks = mini.arrayToTree(this.projectData.Tasks, 'children', 'UID', 'ParentId')
          this.project.loadData(this.projectData)
        } else {
          console.error('加载计划明细失败')
          this.$message.error('加载计划明细失败', res.message)
        }
      }).catch(err => {
        console.error('加载计划明细异常')
        this.$message.error('加载计划明细异常', err)
      }).finally(() => {
        this.spinning = false
      })
    },
    handleOk() {
      const task = this.project.getSelected();
      this.$emit('ok', task)
      this.close()
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    initGantt () {
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
        new PlusProject.WorkColumn()
        // new PlusProject.DepartmentColumn(),
        // new PlusProject.PrincipalColumn(),
        // new PlusProject.AssignmentsColumn(),
      ]
      columns.push({
        name: "dayIndex",
        header: "第几天？<br/>String",
        field: "dayIndex",
        width: 150
      });

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
        { date: new Date(), text: '当前时间', position: 'bottom', style: 'width:2px;background:red;' },
      ])

      //设置刻度
      this.project.setTopTimeScale('month')
      this.project.setBottomTimeScale('day')

      // 设置行高
      this.project.setRowHeight(28)

      this.project.setGanttViewExpanded(false)
      // 加载数据
      // this.project.loadData({})
      // this.getTemplateInfo()
    },
  }
}
</script>

<style scoped>

</style>