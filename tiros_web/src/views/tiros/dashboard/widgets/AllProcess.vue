<template>
  <div :id="viewProjectId" style="height: calc(100% - 5px)">
  </div>
</template>

<script>
import { getAllPlans } from '@api/tirosKanbanApi'
import { randomString } from '@/utils/util'

export default {
  name: 'AllProcess',
  data () {
    return {
      viewProjectId: randomString(),
      project: null,
      projectData:{},
      timer: null
    }
  },
  mounted () {
    this.initGantt()
  },
  methods: {
    initGantt () {
      mini.parse()
      this.project = new PlusProject()
      this.project.setStyle('width:100%;height:100%')
      this.project.setBorderStyle('border:1px dotted #cdcdcd')
      const columns = [
        new PlusProject.IDColumn(),
        new PlusProject.NameColumn(),
        new PlusProject.StartColumn(),
        new PlusProject.FinishColumn(),
        new PlusProject.DurationColumn()
      ]

      this.project.setColumns(columns)
      this.project.render(document.getElementById(this.viewProjectId))

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

      // 设置表格去折叠
      this.project.setTableViewExpanded( false )

      this.customGanttRender()

      this.loadProjects()
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
            + "<div>实际开始：" + mini.formatDate(task.ActualStart, 'yyyy-MM-dd') + "</div>"
            + "<div>实际完成：" + mini.formatDate(task.ActualFinish, 'yyyy-MM-dd') + "</div>";
        }
      });

      //3)自定义条形图label内容
      this.project.on("drawitem", (e) => {
        let item = e.item;
        e.label =item.Name+" "+ e.task["PercentComplete"] + "%";
        e.labelAlign = "left";
      });
    },
    loadProjects () {
      getAllPlans().then(res => {
        if(res.success){
          let max = this.$moment().toDate(), min = this.$moment().toDate()
          if (!res.result || res.result.length == 0) {
            return;
          }
        let  tasks=[]
          res.result.forEach((item) => {
            let start = this.$moment(item.Start).toDate()
            let finish = this.$moment(item.Finish).toDate()
             item.Duration=item.duration

            if (start < min || min == null) {
              min = start
            }
            if (finish > max) {
              max = finish
            }
            tasks.push(item)
          })
            this.$set(this.projectData,'Tasks',tasks)
          if (!this.projectData || !this.projectData.Tasks) {
            return;
          }
          this.projectData.Tasks.forEach(t => {
            if (t.ActualStart) {
              t.ActualStart = new Date(t.ActualStart)
            }
            if (t.ActualFinish) {
              t.ActualFinish = new Date(t.ActualFinish)
            }
          })
          this.projectData.Tasks = mini.arrayToTree(res.result, 'children', 'UID', 'ParentId')
          this.projectData.StartDate = this.$moment(min).add(-5, 'days').toDate()
          this.projectData.FinishDate = this.$moment(max).add(5, 'days').toDate()
          try {
            this.project.loadData(this.projectData)
          } catch (e){
            console.error('加载全貌数据异常：', e
            )
          }


          setTimeout(()=>{
            // project.scrollIntoView(project.getTaskByID(92));
            let num=this.project.getXByDate(new Date());
            if (num > 200) {
              num = num - 200;
            }
            this. project.ganttView.setScrollLeft(num);
            // this.project.scrollToDate(this.$moment().add(5).toDate())
          },100)
        }
      })
    }
  },
  // 离开路由之前执行
  beforeRouteLeave(to, from, next) {
    clearInterval(this.timer)
    next()
  }
}
</script>