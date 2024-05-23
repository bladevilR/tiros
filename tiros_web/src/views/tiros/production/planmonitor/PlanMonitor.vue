<template>
  <div ref="rootWrapper" class="planmonitor-container">
    <div v-if="leftPx !== 0" :style="{ position: 'absolute', top: 1, left: leftPx, zIndex: 999, cursor: 'pointer' }">
      <a-dropdown :trigger="['click']">
        <!--                <a-icon class="switch_btn" type="setting" @click="(e) => e.preventDefault()" />-->
        <img src="~@/assets/tiros/images/magic.svg" @click="(e) => e.preventDefault()" />
        <a-menu slot="overlay" style="min-width: 110px">
          <a-sub-menu v-for="(lineItem, index) in plansList" :key="index" :title="lineItem.lineName">
            <a-menu-item v-for="(planItem, j) in lineItem.plans" :key="j" @click="selectPlanInfo(planItem)"
              >{{ planItem.planName }}
            </a-menu-item>
          </a-sub-menu>
        </a-menu>
      </a-dropdown>
    </div>
    <div class="header">
      <table class="tg">
        <thead>
          <tr>
            <th class="tg-yf16 switch bg-primary-1" rowspan="2" style="width: 160px">
              当前状态：
              <span v-if="planInfo.trainNo">
                <span v-if="planInfo.progressStatus == 0" class="status unstarted">未开始</span>
                <span v-else-if="planInfo.progressStatus == 1" class="status nor">正常(作业中)</span>
                <span v-else-if="planInfo.progressStatus == 2" class="status overdue">逾期(作业中)</span>
                <span v-else-if="planInfo.progressStatus == 3" class="status nor">正常(完工)</span>
                <span v-else class="status overdue">逾期(完工)</span>
              </span>
              <span v-else>无数据</span>
            </th>
            <th class="tg-yf16" colspan="3">
              <span class="title" v-if="planInfo.trainNo"> {{ planInfo.trainNo + '' + planInfo.planName }} </span>
              <span v-else>暂时无数据</span>
            </th>
            <th class="tg-yf16" colspan="2">
              <div class="type">
                <a-radio-group v-model="typeValue" @change="onChange">
                  <a-radio :value="1"> 列计划方式 </a-radio>
                  <a-radio :value="2"> 规程结构方式 </a-radio>
                  <a-radio :value="3"> 车辆结构方式 </a-radio>
                </a-radio-group>
              </div>
            </th>
          </tr>
          <tr>
            <td class="tg-co5t" style="width: 200px">
              <div class="progress_box">
                <span class="label">当前架修进度：{{ planInfo.progress || 0 }}%</span>
                <p class="progress_in" :style="{ width: planInfo.progress || 0 + '%' }"></p>
              </div>
            </td>
            <td class="tg-co5t">当前工期第： {{ planInfo.currentDay || '暂无数据' }} 天</td>
            <td class="tg-co5t">计划完工： {{ planInfo.finishDate || '暂无数据' }}</td>
            <td class="tg-co5t" colspan="2">
              <div class="tips_list">
                <p class="item">
                  <span class="block bg-red"> </span>
                  <span class="label">滞后</span>
                </p>
                <p class="item">
                  <span class="block bg-green"> </span>
                  <span class="label">超前</span>
                </p>
                <p class="item">
                  <span class="block bg-blue"> </span>
                  <span class="label">正常</span>
                </p>
              </div>
            </td>
          </tr>
        </thead>
      </table>
    </div>
    <div class="main">
      <a-spin :spinning="loading">
      <div v-show="typeValue == 1" class="plan_cont">
        <div id="viewProject" style="width: 100%; height: calc(100vh - 389px); padding-top: 8px"></div>
        <div class="chart_cont" v-if="chartCountItems.length > 0">
          <div class="chart" v-for="(item, index) in chartCountItems" :key="index">
            <p class="title">{{ item.title }}</p>
            <div id="chart1" class="chartIn">
              <v-chart :forceFit="true" :height="127" :data="item.chartData" :padding="[20, 40, 20, 60]">
                <v-coord type="rect" direction="LB" />
                <v-tooltip />
                <v-axis dataKey="value" label="" />
                <v-bar position="label*value" label="value" />
              </v-chart>
            </div>
          </div>
        </div>
      </div>
      <!-- 规程结构方式 -->
      <div v-show="typeValue == 2" class="discipline_cont">
        <!-- <div class="title">{{ planInfo.planName }}</div> -->
        <discipline v-if="planReguList.length" :data="planReguList"></discipline>
      </div>
      <!-- 车辆结构方式 -->
      <div v-show="typeValue == 3" class="vehicle_structure_cont">
        <div class="table_cont">
          <vehicle-structure-table
            v-if="planTrainStructList.length"
            :data="planTrainStructList"
          ></vehicle-structure-table>
        </div>
      </div>
      </a-spin>
    </div>

    <a-modal
      centered
      title="任务查看"
      :width="'100%'"
      dialogClass="fullDialog no-footer"
      :visible="taskViewVisible"
      @cancel="taskViewVisible = false"
      :footer="null"
      :destroyOnClose="true"
    >
      <ViewTaskComponent :task-id="taskId"></ViewTaskComponent>
    </a-modal>
  </div>
</template>

<script>
import BarHorizontal from '../../../../components/chart/BarHorizontal'
import Discipline from './components/Discipline'
import VehicleStructureTable from './components/VehicleStructureTable'
import ViewTaskComponent from '@views/tiros/dispatch/trainplan/ViewTaskComponent'

import {
  getPlanList,
  getPlanTaskList,
  getPlanDetail,
  getTrainPlanDetail,
  getTrainTaskDetail,
  getPlanReguList,
  getPlanTrainStructList,
} from '@api/tirosProductionApi'

export default {
  components: { BarHorizontal, Discipline, VehicleStructureTable, ViewTaskComponent },
  data() {
    return {
      loading: false,
      leftPx: 0,
      typeValue: 1,
      lineList: [],
      plansList: [],
      project: null,
      projectData: {
        Tasks: []
      },
      planInfo: {},
      planTaskInfo: {},
      parentTask: null,
      editTask: null,
      taskViewVisible: false,
      dataSourceB: [
        { label: '总数', value: 66 },
        { label: '已完成', value: 20 }
      ],
      chartCountItems: [],
      planReguList: [],
      planTrainStructList: [],
      taskId: ''
    }
  },
  watch: {
    typeValue: function (val, oldVal) {
      if (val == 1) {
        this.getPlanInfo(this.planInfo.id)
        // 不同查看方式就不需要重新加载任务明细了吧 this.getTemplateInfo()
      }
      if (val == 2) {
        this.getPlanInfoByRegu(this.planInfo.id)
      }
      if (val == 3) {
        this.getPlanInfoByTrainStruct(this.planInfo.id)
      }
    },
  },
  mounted() {
    this.getAllPlanList()
  },
  methods: {
    getAllPlanList() {
      // 查询列计划列表
      getPlanList()
        .then((res) => {
          if (res.success) {
            this.leftPx = this.$refs.rootWrapper.offsetLeft
            this.plansList = res.result
            if (this.plansList && this.plansList.length > 0) {
              this.planInfo = this.plansList[0].plans[0]
              this.getPlanInfo(this.planInfo.id)
              this.loadProject()
            }
          }
        })
        .catch((err) => {})
    },

    // 改方法没有用
    getPlanTaskList(id) {
      // 查询列计划任务列表
      getPlanTaskList(id)
        .then((res) => {
          if (res.success) {
            this.taskList = res.result
            this.taskViewVisible = true
          }
        })
        .catch((err) => {})
    },
    getPlanInfo(id) {
      // 查询指定列计划详情
      this.loading=true
      getPlanDetail(id).then((res) => {
        if (res.success) {
          this.planInfo.progress = res.result.progress
          this.planInfo.finishDate = res.result.finishDate
          this.planInfo.currentDay = res.result.currentDay
          this.chartCountItems = this.changeCountItemsFormat(res.result.countItems)
        }
        this.loading=false
      })
    },
    /**
     * 规程方式查看
     * @param id
     */
    getPlanInfoByRegu(id) {
      // 规程结构方式-查询规程进度列表
      this.loading = true
      getPlanReguList(id).then((res) => {
        if (res.success) {
          this.planReguList = res.result
        }
        this.loading = false
      })
    },
    /**
     * 车辆结构方式查看
     * @param id
     */
    getPlanInfoByTrainStruct(id) {
      // 车辆结构方式-查询车辆结构进度列表
      this.loading = true
      getPlanTrainStructList(id).then((res) => {
        if (res.success) {
          this.planTrainStructList = res.result
        }
        this.loading = false
      })
    },
    onChange(e) {
      // console.log('radio checked', e.target.value)
    },
    // 设置统计数据
    changeCountItemsFormat(dataSource) {
      let data = []
      if (dataSource.length > 0) {
        for (let i = 0; i < dataSource.length; i++) {
          const item = dataSource[i]
          data.push({
            title: item.title,
            chartData: [
              {
                label: '总数',
                value: item.count,
              },
              {
                label: '已完成',
                value: item.finish,
              },
            ],
          })
        }
        return data
      } else {
        return []
      }
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
        // new PlusProject.PredecessorLinkColumn(),
        // new PlusProject.PercentCompleteColumn(),
        new PlusProject.DurationColumn(),
        new PlusProject.StartColumn(),
        new PlusProject.FinishColumn(),
        new PlusProject.WorkColumn(),
        new PlusProject.ActualStartColumn(),
        new PlusProject.ActualFinishColumn(),
        {
          header: '班组<br/>Multi',
          field: 'workGroupName',
          width: 120,
        },
        {
          name: 'dayIndex',
          header: '第几天？<br/>String',
          field: 'dayIndex',
          width: 60,
        },
        {
          name: 'finishStatus',
          header: '完成状态<br/>String',
          field: 'status',
          width: 60,
        },
        {
          name: 'creatStatus',
          header: '已生成?<br/>String',
          field: 'hasGen',
          width: 80,
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
      this.project.setTimeLines([
        { date: new Date(), text: '', position: 'bottom', style: 'width:2px;background:red;' },
      ])

      //设置刻度
      this.project.setTopTimeScale('month')
      this.project.setBottomTimeScale('day')

      // 设置行高
      this.project.setRowHeight(28)

      // 双击任务
      this.project.on('taskdblclick', ({ task }) => {
        this.getTrainTaskDetail(task.UID)
        // this.onEditTask()
      })

      this.customGanttRender()

      // 加载数据
      // this.project.loadData({})
      this.getTemplateInfo()
    },
    /**
     * 加载计划模版详情
     * @param plan
     */
    selectPlanInfo(value) {
      this.planInfo = value
      this.typeValue = 1
      // jakgong 注释， 解决停留在该界面时间长了后，导致浏览器死掉的问题
      //this.getPlanReguList(this.planInfo.id)
      //this.getPlanTrainStructList(this.planInfo.id)
      this.getPlanInfo(this.planInfo.id)
      this.getTemplateInfo()
    },
    /**
     * 获取列计划任务明细
     */
    getTemplateInfo() {
      this.parentTask = null
      this.editTask = null
      getTrainPlanDetail(this.planInfo.id).then((res) => {
        if (res.success) {
          this.projectData = res.result
          // 数据加载到甘特图
          this.projectData.StartDate = new Date(this.projectData.StartDate)
          this.projectData.FinishDate = new Date(this.projectData.FinishDate)
          this.projectData.Tasks.forEach((t) => {
            if (t.ActualStart) {
              t.ActualStart = new Date(t.ActualStart)
            }
            if (t.ActualFinish) {
              t.ActualFinish = new Date(t.ActualFinish)
            }
          })
          this.projectData.Tasks = mini.arrayToTree(this.projectData.Tasks, 'children', 'UID', 'ParentId')

          this.project.loadData(this.projectData)
        }
      })
    },
    customGanttRender() {
      // 自定义单元格显示样式
      this.project.on('drawcell', (e) => {
        let task = e.record,
          column = e.column,
          field = e.field
        //单元格样式
        if (column.name == 'Name') {
          e.cellCls = 'name-col-cls'
        }

        //行样式
        if (task.Summary == 1) {
          e.rowCls = 'summary-row-cls'
        }

        if (column.name == 'finishStatus') {
          if (task.status == 1) {
            e.cellHtml = '未开始'
          }
          if (task.status == 2) {
            e.cellHtml = '已开始'
          }
          if (task.status == 3) {
            e.cellHtml = '已暂停'
          }
        }
        if (column.name == 'creatStatus') {
          if (task.hasGen == 0) {
            e.cellHtml = '未生成工单'
          }
          if (task.hasGen == 1) {
            e.cellHtml = '已生成工单'
          }
        }

        if (task.Summary == 1 && column.name != 'Name') {
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
            "<div ><div style='float:left;'>进度：<b>" +
            task.PercentComplete +
            '%</b></div>' +
            "<div style='float:right;'>工期：" +
            task.Duration +
            '日</div></div>' +
            "<div style='clear:both;'>计划开始：" +
            mini.formatDate(task.Start, 'yyyy-MM-dd') +
            '</div>' +
            '<div>计划完成：' +
            mini.formatDate(task.Finish, 'yyyy-MM-dd') +
            '</div>' +
            '<div>实际开始：' +
            mini.formatDate(task.ActualStart, 'yyyy-MM-dd') +
            '</div>' +
            '<div>实际完成：' +
            mini.formatDate(task.ActualFinish, 'yyyy-MM-dd') +
            '</div>'
        }
      })

      //3)自定义条形图label内容
      this.project.on('drawitem', (e) => {
        let item = e.item
        e.label = item.Name + ' ' + e.task['PercentComplete'] + '%'
        e.labelAlign = 'left'
      })
    },
    // 查看任务明细
    getTrainTaskDetail(id) {
      this.taskId = id
      this.taskViewVisible = true
    },
  },
}
</script>
<style scoped lang="less">
.planmonitor-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 0 10px 0px 0px;
  .header {
    height: auto;
    background: none !important;
    .switch {
      position: relative;
      .switch_btn {
        position: absolute;
        left: 6px;
        top: 6px;
        font-size: 22px;
        color: #1268e8;
        cursor: pointer;
      }
    }
    .status {
      font-weight: 900;
      color: #333;
      &.nor {
        color: #0ad436;
      }
      &.unstarted {
        color: #adadad;
      }
      &.overdue {
        color: #f11433;
      }
    }
    .title {
      font-weight: 900;
    }
    .progress_box {
      overflow: hidden;
      position: relative;
      top: 0;
      left: 0;
      width: 200px;
      height: 40px;
      .label {
        position: absolute;
        width: 100%;
        height: 100%;
        left: 0;
        top: 0;
        z-index: 9;
        line-height: 40px;
      }
      .progress_in {
        position: absolute;
        height: 100%;
        background: #14f12f;
        left: 0;
        top: 0;
      }
    }
    .tips_list {
      overflow: hidden;
      display: flex;
      justify-content: center;
      .item {
        margin: 0 24px;
        float: left;
        line-height: 20px;
        overflow: hidden;
        .block {
          width: 20px;
          height: 20px;
          background: #ccc;
          display: inline-block;
          float: left;
          margin-right: 6px;
        }
        .label {
          display: inline-block;
          float: left;
        }
      }
    }
  }
  .main {
    // padding-top: 10px;
    height: calc(100% - 88px);
    border: 1px solid #d7d7d7;
    border-top: none;

    .chart_cont {
      height: 180px;
      display: flex;
      .chart {
        width: 25%;
        justify-content: center;
        padding: 10px;
        box-sizing: border-box;
        overflow: hidden;
        .title {
          height: 36px;
          line-height: 36px;
          background: #f5f5f5;
          padding: 0 10px;
          margin: 0;
          font-size: 14px;
        }
        .chartIn {
          width: 100%;
          overflow: hidden;
          border: 1px solid #f5f5f5;
          box-sizing: border-box;
        }
      }
    }
    .discipline_cont {
      padding: 12px;
      .title {
        font-size: 24px;
        // padding-left: 38px;
        font-weight: 900;
        margin: 12px;
      }
    }
  }
}
// header table
.header .tg {
  border-collapse: collapse;
  border-spacing: 0;
  width: 100%;
}
.header .tg td {
  border-color: black;
  border-style: solid;
  border-width: 1px;
  font-family: Arial, sans-serif;
  font-size: 12px;
  overflow: hidden;
  padding: 10px 5px;
  word-break: normal;
}
.header .tg th {
  border-color: black;
  border-style: solid;
  border-width: 1px;
  font-family: Arial, sans-serif;
  font-size: 12px;
  font-weight: normal;
  overflow: hidden;
  padding: 10px 5px;
  word-break: normal;
}
.header .tg .tg-5wng {
  background-color: #f3f5f7;
  border-color: #d7d7d7;
  text-align: left;
  vertical-align: middle;
}
.header .tg .tg-yf16 {
  background-color: #f3f5f7;
  border-color: #d7d7d7;
  text-align: center;
  vertical-align: middle;
}
.header .tg .tg-co5t {
  border-color: #d7d7d7;
  text-align: center;
  vertical-align: middle;
  padding: 2px;
}
</style>