<template>
  <div id="layout1" class="mini-layout" style="width:100%;height:500px;">
    <div class="header" region="north" height="40" showSplit="false" showHeader="false">
      <div style="float: left; width: 40%;line-height: 35px;">
        <span style="font-weight: bold; font-size: 15px;" id="projectName"></span>
      </div>

      <div style="float: right; width: 60%;line-height: 25px; vertical-align: middle;text-align: right;padding-right: 15px;">
        <input type="button" value="展开所有" @click="expandAll()" />
        <input type="button" value="收缩所有" @click="collapseAll()" />
      </div>
    </div>
    <div title="south" region="south" showSplit="false" showHeader="false" height="30" >
      <div style="line-height:28px;text-align:center;cursor:default">Copyright © 东莞市诺丽电子科技有限公司版权所有 </div>
    </div>
    <div title="center" region="center" bodyStyle="overflow:hidden;">
      <div id="viewProject" style="width:100%;height:100%;"></div>
    </div>
  </div>
</template>

<script>
    export default {
      name: 'project',
      data () {
        return {
          project: null,
          projectId: '',
          password: ''
        }
      },
      mounted () {
        this.loadProject();
      },
      methods: {
        loadProject () {
          mini.parse();
          /* 创建项目甘特图对象------------------------------------------------------------------*/
          this.project = new PlusProject();
          this.project.setStyle("width:100%;height:100%");
          this.project.setBorderStyle("border:0");
          this.project.setColumns([
            new PlusProject.IDColumn(),
            //new PlusProject.StatusColumn(),
            new PlusProject.NameColumn(),
            new PlusProject.PredecessorLinkColumn(),
            new PlusProject.PercentCompleteColumn(),
            new PlusProject.DurationColumn(),
            new PlusProject.StartColumn(),
            new PlusProject.FinishColumn(),
            new PlusProject.WorkColumn(),
            new PlusProject.DepartmentColumn(),
            new PlusProject.PrincipalColumn(),
            new PlusProject.AssignmentsColumn()
          ]);
          // this.project.setTreeColumn("name");
          this.project.render(document.getElementById("viewProject"));

          LoadJSONProject('/miniui/data/project.txt', this.project);
          /*LoadJSONProject2("/project/" + projectId + "?password=" + window.atob(password), this.project, function (result) {
            if(result.success){
              $("#projectName").text(result.data.Name);
            }

            /!*project.orderProject();
            window.setTimeout(function () {
                $(closeList).each(function (item) {
                    project.collapse(item);
                })
            }, 2000);
            *!/
          });*/

          /* 业务代码
          -----------------------------------------------------------------------------*/
          //project.allowOrderProject = false;      //禁止任务排程算法
          this.project.enableManualSchedule = true;        //启用手动模式
          // project.setShowGanttView(false);
          // 只读
          this.project.setReadOnly(true);
          // 显示修改痕迹
          this.project.setShowDirty(false);

          //设置时间线
          this.project.setTimeLines([
            { date: new Date(), text: "当前时间", position: "bottom",  style: "width:2px;background:red;"},
          ]);

          //初始进来：年/月
          this.project.setTopTimeScale("month");
          this.project.setBottomTimeScale("day");

          let closeList = new Array();
          //自定义表格内容
          this.project.on("drawcell", function (e) {
            let task = e.record, column = e.column, field = e.field;

            //行样式
            if (task.Summary == 1) {
              e.rowCls = "summaryRow";
            }

            if (column.name == "PercentComplete") {
              e.cellHtml = task.PercentComplete + "%";

              if (task.PercentComplete == 100) {
                e.cellCls = 'finishTash';
                closeList.push(task);
              }

            }

            // 自定义单元格Html。如果是工期列, 并且工期大与5天, 显示红色
            if (field == "Name" && task.Summary == 1) {
              e.cellHtml = '<b style="color:#2c7fff;">' + task.Name + '</b>';
            }
          });

          //1)自定义条形图外观显示
          /*this.project.on("drawitem", function (e) {
            var item = e.item;
            var left = e.itemBox.left,
              top = e.itemBox.top,
              width = e.itemBox.width,
              height = e.itemBox.height;

            if (!item.Summary && !item.Milestone) {
              let itemClass = "noStartItem";
              if(item.PercentComplete>0) {
                itemClass = "startItem";
              }
              let percentWidth = width * (item.PercentComplete / 100);
              e.itemHtml = '<div id="' + item._id + '" class="'+itemClass+'" style="left:' + left + 'px;top:' + top + 'px;width:' + width + 'px;height:' + (height) + 'px;">';
              e.itemHtml += '<div style="width:' + (percentWidth) + 'px;" class="percentcomplete"></div>';

//        //根据你自己逻辑，把任务分成几块，注意坐标和宽度
//        e.itemHtml += '<div style="position:absolute;left:0px;top:0;height:100%;width:20px;background:red;"></div>';

              e.itemHtml += '</div>';

              //e.ItemHtml = '<a href="http://www.baidu.com" style="left:'+left+'px;top:'+top+'px;width:'+width+'px;height:'+(height-2)+'px;" class="myitem">111</a>';
            }
          });*/
        },
        expandAll () {
          this.project.expandAll();
        },
        collapseAll () {
          this.project.collapseAll();
        }
      }
    }
</script>

<style scoped>
  .summaryRow {
    font-weight: bold;
  }
  .finishTash {
    background: #9adaaf;
  }

  .startItem {
    background: #8dd687;
    border:solid 1px #74b86f;
    position:absolute;overflow:hidden;display:block;
    z-index:100;
  }
  .noStartItem {
    background: #9d9d9d;
    border:solid 1px #818181;
    position:absolute;overflow:hidden;display:block;
    z-index:100;
  }

  .percentcomplete
  {
    margin-top:3px;
    height:6px;overflow:hidden;background: #39ff1d;
  }
</style>