<template>
    <a-spin :spinning="spinning" style="height: calc(100%)" id="mySpin">
      <div style="text-align: right;">
        <a-space>
          <a-button @click="zoomIn">放大</a-button>
          <a-button @click="zoomOut">缩小</a-button>
        </a-space>
      </div>
      <div style="height: calc(100% - 35px)">
        <div id="viewProject" style="width:100%;height:calc(100% - 5px); padding-top: 5px;"></div>
      </div>
    </a-spin>
</template>

<script>
import { getYearPlanDetailGantt, saveYearPlanDetailGantt } from '@/api/tirosDispatchApi'

export default {
  name: 'TaskManageModal',
  props: ['planId'],
  data() {
    return {
      visible:false,
      spinning: false,
      project: null,
      projectData: {
        Tasks: []
      }
    }
  },
  mounted () {
    this.loadProject()
  },
  methods: {
    zoomIn () {
      this.project.zoomIn();
    },
    zoomOut () {
      this.project.zoomOut();
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
        // new PlusProject.NameColumn(),
        // new PlusProject.PredecessorLinkColumn(),
        // new PlusProject.PercentCompleteColumn(),
        {
          name: "Name",
          header: "任务名称<br/>String",
          field: "Name",
          width: 150
        },
        new PlusProject.StartColumn(),
        new PlusProject.FinishColumn(),
        new PlusProject.DurationColumn(),
        {
          name: "programName",
          header: "修程<br/>String",
          field: "programName",
          width: 100
        },
      ]


      this.project.setColumns(columns)
      this.project.render(document.getElementById('viewProject'))

      //启用手动模式
      this.project.enableManualSchedule = true
      // project.setShowGanttView(false);
      // 只读
      // this.project.setReadOnly(true)
      // 显示修改痕迹
      this.project.setShowDirty(false)
      //禁止任务排程算法
     this.project.allowOrderProject = false;

      this.project.setEditOnDblClick(false)

      this.project.setTableViewWidth(550)

      this.project.on("cellbeginedit",function(e){
        e.cancel=true;
      })
      //设置时间线
      /*this.project.setTimeLines([
        { date: new Date(), text: '当前时间', position: 'bottom', style: 'width:2px;background:red;' },
      ])*/

      //设置刻度
      this.project.setTopTimeScale('month')
      this.project.setBottomTimeScale('day')
      // 设置行高
      this.project.setRowHeight(28)

      // 设置任务拖拽
      this.project.setAllowDragDrop(true)
      // 不显示箭头连线
      this.project.setShowLinkLines(false)

      // // 拖拽开始事件
      // this.project.on("itemdragstart",(e) =>{
      //   if (e.action === 'finish') {
      //     console.log(e);
      //   // console.log(e.action === 'percentcomplete');
      //   }
      // })
      
      // 拖拽释放事件
      this.project.on("itemdragcomplete",({action, item}) =>{
        //fix 拖动后结束日期变更bug
        if (action === 'move') {
          let start = this.$moment(item.startDate)
          let finish = this.$moment(item.finishDate)
          item.Finish = new Date(this.$moment(item.Start).add(finish.diff(start, 'days'), 'days').format('YYYY-MM-DD'))
        }
      })
      
      // // 任务点击
      // this.project.on("taskclick",({task}) =>{
      // })
      
      // 加载数据
      /*let tasks = []

      for (let i = 1; i <= 5; i++) {
        tasks.push({
          UID: i,
          ID: i,
          Name: '车辆' + i,
          Start: new Date('2021-02-20'),
          Finish: new Date('2021-03-10'),
          Duration: 19
        })
      }
      let pro = {
        UID: 0,
        Name: 'ProjectName',
        StartDate: new Date('2021-02-20'),
        FinishDate: new Date('2021-03-10'),
        Tasks: tasks
      }
      this.project.loadData(pro)*/
      this.getTemplateInfo()
    },
    /**
     * 加载计划模版详情
     * @param plan
     */
    save () {
      // 保存前转换日期时间
      let tasks = []
      this.projectData.Tasks.forEach(t => {
        let task = Object.assign({}, t)
        task.startDate = this.$moment(t.startDate).format('YYYY-MM-DD hh:mm:ss')
        task.finishDate = this.$moment(t.Finish).format('YYYY-MM-DD hh:mm:ss')
        task.Start = this.$moment(t.Start).format('YYYY-MM-DD hh:mm:ss')
        task.Finish = this.$moment(t.Finish).format('YYYY-MM-DD hh:mm:ss')
        tasks.push(task)
      })
      //console.log('save tasks:', tasks)
      saveYearPlanDetailGantt(tasks).then(res => {
        if (res.success) {
          this.$message.success('保存成功')
          this.$emit('ok')
        } else {
          this.$message.error('保存失败')
          console.error('保存年计划明细失败：', res.message)
        }
      })
    },
    getTemplateInfo() {
      this.spinning=true;
      getYearPlanDetailGantt({planYearId: this.planId}).then((res) => {
        if (res.success) {
          this.projectData = res.result
          // 数据加载到甘特图
          this.projectData.StartDate = new Date(this.projectData.StartDate)
          this.projectData.FinishDate = new Date(this.projectData.FinishDate)
          this.projectData.Tasks = mini.arrayToTree(this.projectData.Tasks, 'children', 'UID', 'ParentId')
          this.project.loadData(this.projectData)
        } else {
          console.error('加载年计划明细数据失败：', this.planId)
          this.$message.error('加载年计划明细数据失败')
        }
        this.spinning = false
      }).catch(err => {
        console.error('加载年计划明细数据异常：', err, this.planId)
        this.$message.error('加载年计划明细数据异常')
      })
    },
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
    }
  },
}
</script>

<style>
#mySpin .ant-spin-container{
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