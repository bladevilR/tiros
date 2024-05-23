<template style="height: calc(100% - 5px)">
  <div style="height: calc(100% - 5px); overflow: hidden;" @click="hiddenFilter">
    <div :style="{ position: 'absolute', top: 1, right: 0, zIndex: 999, cursor: 'pointer' }" @click="onShowFilter">
      <img src="~@/assets/tiros/images/magic.svg" />
    </div>
    <!-- 查询区域 -->
    <div ref="filterBox" class="filterBox" @click.stop>
      <a-form layout="horizontal" :label-col="{ span: 6 }" :wrapperCol="{ span: 18 }">
        <a-row :gutter="16">
          <a-col :md="24" :sm="24">
            <a-form-item label="车辆段">
              <j-dict-select-tag v-model="queryParam.depotId" placeholder="请选择" dictCode="bu_mtr_depot,name,id" />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="线路">
<!--              <j-dict-select-tag v-model="lineId" dictCode="bu_mtr_line,line_name,line_id" />-->
              <line-select-list v-model="lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="车辆">
              <j-dict-select-seach-tag v-model="queryParam.trainNo" :dictCode="dictCodeStr" />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="时间">
              <a-range-picker format="YYYY-MM-DD" :placeholder="['开始时间', '结束时间']" @change="onDayChange" :defaultPickerValue="defaultDateRange" />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="8">
            <a-button type="primary" @click="findList" block>切换条件</a-button>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div>
      <a-row :gutter="24">
        <a-col :md="24" :sm="8">
          <na-card-light title="当月任务完成情况">
            <div id="chart1" style="width:100%; height: 260px;"></div>
          </na-card-light>
        </a-col>
      </a-row>
    </div>
    <div id="myWorkContent" style="padding-right: 5px; height: calc(100% - 316px);">
      <a-row :gutter="24" style="height: calc(100%); overflow: hidden">
        <a-col :md="10" :sm="24" style="height: calc(100%); overflow: hidden">
          <na-card-light title="班组任务进度">
            <div style="height: 100%;overflow-y: auto;">
            <div v-for="(item, index) in dataSourceP" @click.stop="workOrder(item.groupId)" :key="index" class="groupItem">
              <span style="font-weight: bold" >{{ item.groupName }}</span> [工班长：<span class="font-primary-7">{{ item.groupMonitorName }}</span
            >，作业人数：<span class="font-primary-8">{{ item.workerQuantity }}</span>人]
              <!--                <mini-progress :target="item.progress" :percentage="item.progress" :height="20"></mini-progress>-->
              <a-progress :percent="item.progress" :strokeWidth="20" status="active" style="width: 98%" />
            </div>
            </div>
          </na-card-light>
        </a-col>
        <a-col :md="14" :sm="24" style="height: calc(100%); overflow: hidden">
          <na-card-light title="委外任务进度">
            <div id="viewProject" style="width: 100%; height: calc(100% - 32px); padding-top: 8px"></div>
          </na-card-light>
        </a-col>
      </a-row>
    </div>
    <ProgressWorkShopInfo ref="workShopInfo"></ProgressWorkShopInfo>
  </div>
</template>

<script>
import {
  getTaskTrend,
  getListWorkGroupTaskProgress,
  getListOutsourceOrderProgress, getListOutsourceTaskProgress
} from '@api/tirosKanbanApi'
import ProgressWorkShopInfo from '@views/tiros/kanban/progressworkshop/ProgressWorkShopInfo'
import * as echarts from 'echarts'
import NaCardLight from '@comp/tiros/Na-card-light'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
export default {
  components: { NaCardLight, ProgressWorkShopInfo ,LineSelectList},
  name: 'ProgressWorkshopPage',
  data() {
    return {

      dictCodeStr: 'bu_train_info,train_no,train_no',
      tableData: [],
      dataSourceP: [],
      dataSourceL: [],
      queryParam: {
        depotId: '',
        lineId: '',
        trainNo: '',
        endDate: '',
        startDate: '',
      },
      allAlign: 'center',
      count: '',
      isList: false,
      lineId: '',
      project: null,
      projectData: {
        Tasks: [],
      },
    }
  },
  watch: {
    lineId(newVal, oldVal) {
      // console.log('newVal:', newVal, '========', 'oldVal:', oldVal)
      if (newVal) {
        this.dictCodeStr = 'bu_train_info,train_no,train_no,line_id=' + newVal
      } else {
        this.dictCodeStr = 'bu_train_info,train_no,train_no'
      }
    },
  },
  mounted() {
    this.findList()

    setTimeout(() => {
      this.initGantt()
    }, 200)
  },
  methods: {
    onDayChange: function (value, dateString) {
      this.queryParam.startDate = dateString[0]
      this.queryParam.endDate = dateString[1]
    },
    workOrder(e) {
      // this.$router.push({ path: `/tiros/kanban/progressworkshop/${e}` })
      this.$refs.workShopInfo.show(e)
    },
    findList() {
      this.queryParam.lineId = this.lineId
      getListWorkGroupTaskProgress(this.queryParam).then((res) => {
        this.dataSourceP = res.result
      })
      getTaskTrend(this.queryParam).then((res) => {
        this.dataSourceL = res.result

        if (this.dataSourceL && this.dataSourceL.length > 0) {
          this.chart1(this.dataSourceL)
        } else {
          this.chart1([])
        }
      })
      this.loadProjects()
    },
    onShowFilter(e) {
      // console.log('display:', this.$refs.filterBox.style.display)
      if (this.$refs.filterBox.style.display === 'block') {
        this.$refs.filterBox.style.display = 'none'
      } else {
        this.$refs.filterBox.style.top = e.pageY + 'px'
        this.$refs.filterBox.style.left = e.pageX - 270 + 'px'
        this.$refs.filterBox.style.display = 'block'
      }
      e.stopPropagation()
    },
    hiddenFilter() {
      this.$refs.filterBox.style.display = 'none'
    },
    initGantt() {
      mini.parse()
      this.project = new PlusProject()
      this.project.setStyle('width:100%;height:100%')
      this.project.setBorderStyle('border:1px dotted #cdcdcd')
      const columns = [
        new PlusProject.IDColumn(),
        new PlusProject.NameColumn(),
        new PlusProject.StartColumn(),
        new PlusProject.FinishColumn(),
        new PlusProject.DurationColumn(),
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
        { date: new Date(), text: '当前时间', position: 'bottom', style: 'width:2px;background:red;' },
      ])

      //设置刻度
      this.project.setTopTimeScale('month')
      this.project.setBottomTimeScale('day')
      // 设置行高
      this.project.setRowHeight(28)

      // 设置表格去折叠
      this.project.setTableViewExpanded(false)

      this.customGanttRender()

      this.loadProjects()
    },
    customGanttRender() {
      // 自定义单元格显示样式
      this.project.on('drawcell', (e) => {
        var task = e.record,
          column = e.column,
          field = e.field
        //单元格样式
        if (column.name == 'Name') {
          e.cellCls = 'mycellcls'
        }

        //行样式
        if (task.Summary == 1) {
          e.rowCls = 'myrowcls'
        }

        ////自定义单元格Html。如果是工期列, 并且工期大与5天, 显示红色
        /*if (field == "Name" && task.Duration > 5) {
            e.cellHtml = '<b style="color:red;">' + task.Name + '</b>';
          }*/

        /* if (field == "Name" && task.Duration <= 2) {
             e.cellHtml = '<span style="color:blue;">' + task.Name + '</span>';
           }*/

        if (task.Duration == 0) {
          e.rowCls = 'deletetask'
        }
      })
      const thiz = this
      //1)自定义条形图外观显示
      this.project.on('drawitem', (e) => {
        var item = e.item
        var left = e.itemBox.left,
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

            let theClass = 'myitem'
            if (actFinish) {
              // 已完成，判断是正常完成、提前完成、延后完成
              if (actFinish > planFinish) {
                // 延期完成
                theClass = 'delay'
              } else if (actFinish === planFinish) {
                // 正常完成
                theClass = 'normal'
              } else if (actFinish < planFinish) {
                // 提前完成
                theClass = 'ahead'
              }
            } else {
              // 没完成， 判断是否开始，判断当前已否已超期
              if (actStart) {
                // 已经开始了
                if (now > planFinish) {
                  theClass = 'delay'
                }
              } else {
                // 还没开始
                if (now > planStart) {
                  theClass = 'delay'
                } else {
                  theClass = 'unstart'
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
            e.itemHtml += '<div style="width:' + percentWidth + 'px;" class="percentcomplete"></div>'
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
            '时</div></div>' +
            "<div style='clear:both;'>计划开始：" +
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
        e.labelAlign = 'left'
      })
    },
    loadProjects() {
      getListOutsourceTaskProgress(this.queryParam).then((res) => {
        if (res.success) {
          let max = this.$moment().toDate(),
            min = this.$moment().toDate()
          res.result.forEach((item) => {
            let start = this.$moment(item.Start).toDate()
            let finish = this.$moment(item.Finish).toDate()

            if (start < min || min == null) {
              min = start
            }
            if (finish > max) {
              max = finish
            }
          })

          this.projectData.Tasks = mini.arrayToTree(res.result, 'children', 'UID', 'ParentId')
          this.projectData.StartDate = this.$moment(min).add(-5, 'days').toDate()
          this.projectData.FinishDate = this.$moment(max).add(5, 'days').toDate()

          this.project.loadData(this.projectData)

          setTimeout(() => {
            // project.scrollIntoView(project.getTaskByID(92));
            let num = this.project.getXByDate(new Date())
            if (num > 200) {
              num = num - 200
            }
            this.project.ganttView.setScrollLeft(num)
            // this.project.scrollToDate(this.$moment().add(5).toDate())
          }, 100)
        }
      })
    },
    chart1(data) {
      // 基于准备好的dom，初始化echarts实例
      let myChart = echarts.init(document.getElementById('chart1'));

      // 指定图表的配置项和数据
      let option = {
        title: {
          text: '当月任务完成情况一览',
        },
        legend: {
          data: ['计划任务数', '实际完成数']
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
          }
        },
        grid: {
          x: '5', y: '25', width: '100%', height: '75%'
        },
        xAxis: {
          type: 'category',
          data: data.map(d=>{return d.type})
        },
        yAxis: {
          type: 'value',
          boundaryGap: ['0%', '20%']
        },
        series: [
          {
            symbol:'none', //这句就是去掉点的
            smooth:true, //这句就是让曲线变平滑的
            name: '计划任务数',
            data: data.map(d=>{return d.jeecg}),
            type: 'line'
          },
          {
            symbol:'none', //这句就是去掉点的
            smooth:true, //这句就是让曲线变平滑的
            name: '实际完成数',
            data: data.map(d=>{return d.jeebt}),
            type: 'line'
          },
        ]
      };


      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option);
    }
  },
}
</script>
<style lang="less">
.myitem {
  background: #9ccc5e;
  border: solid 1px #6e845c;
  position: absolute;
  overflow: hidden;
  display: block;
  z-index: 100;
}
.normal {
  background: #150bff;
  border: solid 1px #cdcdcd;
  position: absolute;
  overflow: hidden;
  display: block;
  z-index: 100;
}

.unstart {
  background: #9a9c9b;
  border: solid 1px #cdcdcd;
  position: absolute;
  overflow: hidden;
  display: block;
  z-index: 100;
}
.delay {
  background: #eaa5a5;
  border: solid 1px #cdcdcd;
  position: absolute;
  overflow: hidden;
  display: block;
  z-index: 100;
}
.ahead {
  background: #00ff00;
  border: solid 1px #cdcdcd;
  position: absolute;
  overflow: hidden;
  display: block;
  z-index: 100;
}

.percentcomplete {
  margin-top: 5px;
  height: 9px;
  overflow: hidden;
  background: #03c103;
}

.groupItem{
  cursor: pointer;
  padding: 5px;
  text-align: left;
  background: #ffffff;
  margin: 5px;
}
.groupItem:hover {
  background: #e6f3e1;
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
<style scoped lang="less">
.wrapperDiv {
  border: 1px #efefef solid !important;
  border-radius:4px;
  margin-bottom: 15px;
  font-size: 14px;
  height: calc(100% - 16px);
  overflow: hidden;

  .titleBar {
    width: 100%;
    font-size: 14px;
    font-weight: bold;
    padding: 5px;
    background: #f1f1f1;
    /*text-align: center;*/
    padding-left: 10px;
    border: 0;
    margin-top: -1px;
    border-top-left-radius:4px;
    border-top-right-radius:4px;
    border-bottom: 3px solid;
    margin-bottom: 0px !important;
  }
  .content {
    text-align: center;
    /*margin-bottom: 15px;*/
    height: calc(100% - 32px);
    overflow-x: hidden;
    overflow-y: auto;
  }
}

.filterBox{
  top: 50px;
  width: 270px;
  position: absolute;
  z-index: 999;
  padding: 10px;
  padding-right: 15px;
  background-color: #eeeeee;
  box-shadow: -3px 3px 3px 3px #cbcaca;
  display: none;
}

</style>