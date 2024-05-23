<template style="height: calc(100% - 5px)">
  <div style="padding-top: 10px; padding-left: 8px; padding-right: 5px; height: calc(100% - 5px); overflow: hidden">
    <a-row :gutter="24" style="height: calc(100%); overflow: hidden">
      <a-col style="height: calc(100% / 2 - 0px)" :md="12" :sm="24">
        <div class="wrapperDiv">
          <h4 class="titleBar font-primary-8 border-primary-6">预警区</h4>
          <div class="content">
            <a-row
              :gutter="24"
              :style="{
                width: '99%',
                marginLeft: '3px',
                position: 'relative',
                top: '50%',
                transform: 'translateY(-50%)',
              }"
            >
              <a-col :md="6" v-for="(item, index) in dataSourceY" :key="index">
                <div
                  class="square border-primary-2"
                  :style="`background-color:${item.itemColor}`"
                  @click="jumppage(item.itemCode)"
                >
                  <div style="margin-top: 28px">
                    <span style="font-weight: bold; font-size: 18px">{{ item.itemValue }}</span
                    ><br />
                    {{ item.itemTitle }}
                  </div>
                </div>
              </a-col>
            </a-row>
          </div>
        </div>
      </a-col>
      <a-col style="height: calc(100% / 2 - 0px)" :md="12" :sm="24">
        <div class="wrapperDiv">
          <h4 class="titleBar font-primary-8 border-primary-6">数据区</h4>
          <div class="content">
            <a-row :gutter="24" :style="{ position: 'relative', top: '50%', transform: 'translateY(-50%)' }">
              <a-col :md="8" v-for="(item, index) in dataSourceS" :key="index" style="text-align: center">
                <div class="circle" :style="`border-color:${item.itemColor}`">
                  <div style="margin-top: 28px" :style="`color:${item.itemColor}`">
                    <span style="font-weight: bold">{{ item.itemValue }}</span
                    ><br />
                    {{ item.itemTitle }}
                  </div>
                </div>
              </a-col>
            </a-row>
          </div>
        </div>
      </a-col>
      <a-col style="height: calc(100% / 2 - 0px)" :md="12" :sm="24">
        <div class="wrapperDiv">
          <h4 class="titleBar font-primary-8 border-primary-6">
            最新故障
            <a @click.stop="jumpbreakdown()" style="float: right; margin-top: -5px; font-size: 17px; color: black"
              >...</a
            >
          </h4>
          <div class="content">
            <vxe-table
              border="none"
              ref="listTable"
              :align="allAlign"
              :data="tableData"
              show-overflow="tooltip"
              :edit-config="{ trigger: 'manual', mode: 'row' }"
              max-height="100%"
            >
              <vxe-table-column title="故障标题" align="left" width="25%">
                <template v-slot="{ row }">
                  <a @click.stop="faultDetail(row)">{{ row.faultDesc }}</a>
                </template>
              </vxe-table-column>
              <vxe-table-column field="reportGroupName" title="报告班组" width="22%"></vxe-table-column>
              <vxe-table-column field="reportTime" title="报告时间" width="20%"></vxe-table-column>
              <vxe-table-column field="sysName" title="所属系统" width="18%"></vxe-table-column>
              <vxe-table-column field="status_dictText" title="处理状态" width="15%"></vxe-table-column>
            </vxe-table>
          </div>
        </div>
      </a-col>
      <a-col style="height: calc(100% / 2 - 0px)" :md="12" :sm="24">
        <div class="wrapperDiv">
          <h4 class="titleBar font-primary-8 border-primary-6">
            任务全貌
            <a @click.stop="jumpbreakdown()" style="float: right; margin-top: -5px; font-size: 17px; color: black"
              >...</a
            >
          </h4>
          <div id="viewProject" style="height: calc(100% - 32px)"></div>
        </div>
      </a-col>
    </a-row>
    <breakdown-detail-modal ref="breakdownDetail"></breakdown-detail-modal>
  </div>
</template>

<script>
import { getAlert, getData, getListLatest, getAllPlans } from '@api/tirosKanbanApi'
// import { getBreakdownInfo } from '@api/tirosDispatchApi'
import BreakdownDetailModal from '@views/tiros/dispatch/breakdown/BreakdownDetailModal'
export default {
  name: 'HomePage',
  components: { BreakdownDetailModal },
  data() {
    return {
      tableData: [],
      dataSourceY: [],
      dataSourceS: [],
      allAlign: 'center',
      project: null,
      timer: null,
      projectData: {
        Tasks: [],
      },
    }
  },
  created() {
    if (!this.timer) {
      this.timer = setInterval(() => {
        //console.log('定时加载首页数据...')
        this.findList()
        // this.initGantt()
      }, 5000)
    }
  },
  mounted() {
    this.findList()
    this.initGantt()
  },
  methods: {
    findList() {
      getAlert().then((res) => {
        this.dataSourceY = res.result
      })
      getData().then((res) => {
        this.dataSourceS = res.result
      })
      getListLatest().then((res) => {
        this.tableData = res.result
      })
    },
    jumpbreakdown() {
      this.$router.push({ path: `/tiros/dispatch/breakdown` })
    },
    jumpInfo(row) {
      this.$router.push({ path: `/tiros/kanban/homepage/${row.id}` })
    },
    faultDetail(row) {
      this.$refs.breakdownDetail.show(row.id)
      // getBreakdownInfo({ id: row.id }).then((res) => {
      //   let data = res.result
      //   this.$refs.breakdownDetail.show(data)
      // })
    },

    jumppage(e) {
      if (e == 'wlkcyj') {
        this.$router.push({ path: `/tiros/material/earlywarning` })
      } else if (e == 'qjsjyj') {
        this.$router.push({ path: `/tiros/material/equipment/needcheck` })
      } else if (e == 'wzyxyj') {
        this.$router.push({ path: `/tiros/material/earlywarning` })
      } else if (e == 'bjzbqyj') {
        this.$router.push({ path: `/tiros/outsource/quality` })
      } else if (e == 'clsjyj') {
        this.$router.push({ path: `/tiros/quality/earlywarningcheck` })
      } else if (e == 'wwyqyj') {
        this.$router.push({ path: `/tiros/outsource/perform` })
      } else if (e == 'yqgdyj') {
        this.$router.push({ path: `/tiros/dispatch/workorder` })
      } else if (e == 'wclgzyj') {
        this.$router.push({ path: `/tiros/dispatch/breakdown` })
      }
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
            '日</div></div>' +
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
      getAllPlans().then((res) => {
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
          try {
            this.project.loadData(this.projectData)
          } catch (e) {
            console.error('加载全貌数据异常：', e)
          }

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
  },
  // 离开路由之前执行
  beforeRouteLeave(to, from, next) {
    clearInterval(this.timer)
    next()
  },
}
</script>
<style>
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
  border-radius: 4px;
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
    border-top-left-radius: 4px;
    border-top-right-radius: 4px;
    border-bottom: 3px solid;
    margin-bottom: 0px !important;
  }
  .content {
    text-align: center;
    /*margin-bottom: 15px;*/
    height: calc(100% - 32px);
    overflow-x: hidden;
    overflow-y: auto;
    .square {
      border: 1px #efefef solid;
      height: 110px;
      margin-top: 15px;
      cursor: pointer;
    }
    .circle {
      width: 115px;
      height: 115px;
      border-style: solid;
      border-width: 5px;
      border-radius: 50%;
      -moz-border-radius: 50%;
      -webkit-border-radius: 50%;
      /*border-color: #1E90FF;*/
      margin: 0 auto;
      margin-top: 10px;
      cursor: pointer;
    }

    /*.vxe-table .vxe-table--main-wrapper .vxe-table--header-wrapper .vxe-table--header .vxe-header--row .vxe-header--column {
        padding: 0px !important;
      }*/
  }
}
.wrapperDiv th {
  padding: 0px !important;
}
</style>