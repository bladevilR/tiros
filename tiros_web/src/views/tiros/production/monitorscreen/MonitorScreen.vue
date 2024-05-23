<template>
  <div class="monitor_main">
    <div class="main_content">
      <div>
        <!--  -->
        <div class="titleBox">
          <div class="title_text">苏州车辆架大修信息监控大屏</div>
          <div class="label_Box">
            <div class="label_text">安全生产天数</div>
            <div class="box_num">
              <div class="text">{{ safeDays.slice(0, 1) }}</div>
            </div>
            <div class="box_num">
              <div class="text">{{ safeDays.slice(1, 2) }}</div>
            </div>
            <div class="box_num">
              <div class="text">{{ safeDays.slice(2, 3) }}</div>
            </div>
            <div class="box_num">
              <div class="text">{{ safeDays.slice(3, 4) }}</div>
            </div>
            <div class="box_num">
              <div class="text">{{ safeDays.slice(4, 5) }}</div>
            </div>
          </div>
        </div>
        <!--  -->
        <div class="plan_card">
          <div class="card_title">列计划进度</div>
          <div class="m_content">
            <div>
              <div
                class="m_item item_progress"
                v-for="item in planProgressList"
                :key="item.id"
              >
                <div class="m_text ellipsis">
                  <span>{{ `${item.trainNo}车辆-${item.repairProgramName}计划` }}</span>
                </div>
                <div class="progress">
                  <div class="prog" :style="{ width: `${item.progress}%` }"></div>
                  <div class="prog_text">{{ item.progress + "%" }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="bottomBox">
        <!--  -->
        <div class="forstBox">
          <!-- 预警信息 -->
          <div class="header_info_box">
            <div class="m_title"><span>预警信息</span></div>
            <div class="m_content">
              <a
                :style="`width:calc(100% / ${alertList.length / 2} - .3vw)`"
                class="card_color_info alert"
                v-for="item in alertList"
                :key="item.itemCode"
                @click="jumpAlertPage(item.itemCode)"
              >
                <div class="numberText">{{ item.itemValue }}</div>
                <div class="small_text">{{ item.itemDesc }}</div>
              </a>
            </div>
          </div>
          <!-- 数据区 -->
          <div class="header_info_box">
            <div class="m_title"><span>数据区</span></div>
            <div class="m_content">
              <div
                class="card_color_info data"
                :style="`width:calc(100% / ${dataList.length / 2} - .3vw)`"
                v-for="item in dataList"
                :key="item.itemCode"
              >
                <div class="numberText">{{ item.itemValue }}</div>
                <div class="small_text">{{ item.itemDesc }}</div>
              </div>
            </div>
          </div>
          <!--  -->
        </div>
        <!--  -->
        <div class="twoBox">
          <!--  -->
          <div class="topBox">
            <!-- 工班任务进度 -->
            <div class="card group_task">
              <div class="m_title">
                <span>工班任务进度</span>
              </div>
              <div class="m_content">
                <div class="scroll_content" :class="{ animation: wgAnimation }">
                  <div
                    class="m_item item_progress"
                    v-for="item in workGroupTaskList"
                    :key="item.groupName"
                  >
                    <div class="m_text ellipsis">
                      <span>{{ item.groupName }}</span>
                    </div>
                    <div class="progress">
                      <div class="prog" :style="{ width: `${item.progress}%` }"></div>
                      <div class="prog_text">{{ item.progress + "%" }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 委外任务进度 -->
            <div class="card out_source">
              <div class="m_title">
                <span>委外任务进度</span>
              </div>
              <div class="m_content">
                <div class="scroll_content" :class="{ animation: osAnimation }">
                  <div
                    class="m_item item_progress"
                    v-for="item in outSourceList"
                    :key="item.id"
                  >
                    <div class="m_text ellipsis">
                      <span>{{ item.Name }}</span>
                    </div>
                    <div class="progress">
                      <div
                        class="prog"
                        :style="{ width: `${item.PercentComplete}%` }"
                      ></div>
                      <div class="prog_text">{{ item.PercentComplete + "%" }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 当前缺料 -->
            <div class="card lack">
              <div class="m_title">
                <span>当前缺料</span>
              </div>
              <div class="m_content">
                <div class="scroll_content" :class="{ animation: lackAnimation }">
                  <div
                    class="m_item item_progress"
                    v-for="item in lackList"
                    :key="item.id"
                  >
                    <div class="m_text_code ellipsis">{{ item.code }}</div>
                    <div class="m_text_name ellipsis">{{ item.name }}</div>
                    <div class="m_text_lack ellipsis">{{ item.lack }}</div>
                  </div>
                </div>
              </div>
            </div>
            <!--  -->
          </div>
          <!--  -->
          <div class="botBox">
            <!-- 最新故障 -->
            <div class="card">
              <div class="m_title">
                <span>最新故障</span>
              </div>
              <div class="m_content">
                <div class="scroll_content" :class="{ animation: nfAnimation }">
                  <div
                    class="m_item item_progress"
                    v-for="item in newFaultList"
                    :key="item.groupName"
                  >
                    <div class="flex_1 ellipsis">{{ item.faultDesc }}</div>
                    <div>{{ item.reportTime }}</div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 作业工单 -->
            <div class="card">
              <div class="m_title"><span>作业工单</span></div>
              <div class="m_content">
                <div class="scroll_content" :class="{ animation: taskAnimation }">
                  <div class="m_item" v-for="item in taskList" :key="item.id">
                    <div class="flex_1 ellipsis">
                      <a-tooltip trigger="none">
                        <template slot="title">
                          {{ item.orderName }}
                        </template>
                        {{ item.orderName }}
                      </a-tooltip>
                    </div>
                    <div>{{ item.orderStatus_dictText }}</div>
                    <div>{{ item.startTime }}</div>
                  </div>
                </div>
              </div>
            </div>
            <!--  -->
          </div>
        </div>
        <!--  -->
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from "echarts";
import {
  getAlert,
  getData,
  getListLatest,
  getListOutsourceTaskProgress,
  getListWorkGroupTaskProgress,
  getCostItem2,
  getCostProportion,
  getLastTenTrainCost,
  getLastTenTrainNo,
} from "@api/tirosKanbanApi";
import { getWorkOrderList, getTrainPlanList } from "@/api/tirosDispatchApi";
import { getFaultRanking } from "@api/tirosKanbanApi";
import {
  getCostStatistics,
  getFaultStatistics,
  getLackMaterial,
} from "@api/tirosProductionApi";
import { getSysConfig } from "@api/api";
import { getTrainFaultSum } from "@api/tirosReportApi";

export default {
  name: "MonitorScreen",
  components: {},
  data() {
    return {
      alertList: [],
      dataList: [],
      groupFaultList: [],
      newFaultList: [],
      taskList: [],
      outSourceList: [],
      workGroupTaskList: [],
      planProgressList: [],
      faultTrendList: [],
      lackList: [],
      safeDays: "00000",
      nfAnimation: false,
      taskAnimation: false,
      osAnimation: false,
      wgAnimation: false,
      lackAnimation: false,
      planAnimation: false,
      costList: {
        userNum: 0,
        materialCost: 0,
        outsourceCost: 0,
        toolNum: 0,
        system: 0,
      },
    };
  },
  computed: {
    permissionList() {
      return this.$store.getters.permissionList || [];
    },
  },
  mounted() {
    this.initAll();
    this.initRunDay();
  },
  methods: {
    initAll() {
      this.initPlanProgress().then(()=>{
        this.materialUseEchart();
        this.faultEchart();
        this.martrialCostEchart();
        this.initAlert();
        this.initData();
        this.initFaultOfGroup();
        this.initNewFault();
        this.initTask();
        this.initOutSource();
        this.initWorkGroupTask();
        // this.initCostList()
        this.initLackList();
        this.initFaultTrend();
      })
    },
    initRunDay() {
      getSysConfig("SafeServiceDays").then((res) => {
        if (res.success) {
          this.safeDays = (res.result.configValue ? Number(res.result.configValue) : 0)
            .toString()
            .padStart(5, "0");
        } else {
          this.safeDays = "00000";
          this.$message.error(res.message);
        }
      });
    },
    async materialUseEchart() {
      // let data = await getCostStatistics({}).then((res) => {
      //   let template = {
      //     consumeDataList: [],
      //     categoryDataList: [],
      //   };
      //   if (res.success) {
      //     if (
      //       res.result &&
      //       res.result.consumeDataList &&
      //       res.result.consumeDataList.length
      //     ) {
      //       template.consumeDataList = res.result.consumeDataList
      //         .filter((e, index) => index < 6)
      //         .map((e) => {
      //           return { value: e.count, name: e.item };
      //         });
      //     }
      //     if (
      //       res.result &&
      //       res.result.categoryDataList &&
      //       res.result.categoryDataList.length
      //     ) {
      //       template.categoryDataList = res.result.categoryDataList
      //         .filter((e, index) => index < 6)
      //         .map((e) => {
      //           return { value: e.count, name: e.item };
      //         });
      //     }
      //   } else {
      //     this.$message.error(res.message);
      //   }
      //   return template;
      // });
      // this.createEchart("materialEchart", "成本", data.consumeDataList);
      // this.createEchart2("materialTypeEchart", "物料消耗数量", data.categoryDataList);
    },
    async faultEchart() {
      // let list = await getFaultStatistics({}).then((res) => {
      //   if (res.success) {
      //     // return []
      //     if (
      //       res.result &&
      //       res.result.systemItemList &&
      //       res.result.systemItemList.length
      //     ) {
      //       return res.result.systemItemList
      //         .sort((a, b) => b.faultCount - a.faultCount)
      //         .filter((e, index) => index < 6)
      //         .map((e) => {
      //           return { value: e.faultCount, name: e.systemName };
      //         });
      //     }
      //   } else {
      //     this.$message.error(res.message);
      //   }
      //   return [];
      // });
      // this.createEchart("faultEchart", "故障数", list);
    },
    async martrialCostEchart() {
      // let tirosList = await getLastTenTrainNo({}).then((res) => {
      //   if (res.success) {
      //     return res.result;
      //   } else {
      //     this.$message.error(res.message);
      //   }
      //   return [];
      // });
      // let list = await getLastTenTrainCost({}).then((res) => {
      //   if (res.success) {
      //     if (tirosList && tirosList.length > 0) {
      //       if (res.result && res.result.length) {
      //         return res.result;
      //       }
      //     }
      //   } else {
      //     this.$message.error(res.message);
      //   }
      //   return [];
      // });
      // if (!tirosList.length || !list.length) {
      //   tirosList = [];
      //   list = [];
      // }
      // this.createBarEchart("materialCostEchart", tirosList, list);
      // <div class="echart_container" id="faultEchart"></div>
    },
    initAlert() {
      getAlert().then((res) => {
        if (res.success) {
          this.alertList = res.result;
        }
      });
    },
    initData() {
      getData().then((res) => {
        if (res.success) {
          this.dataList = res.result;
        }
      });
    },
    initFaultOfGroup() {
      getFaultRanking({}).then((res) => {
        if (res.success) {
          res.result.sort((a, b) => b.currentMonth - a.currentMonth);
          this.groupFaultList = res.result;
        }
      });
    },
    initNewFault() {
      getListLatest({}).then((res) => {
        if (res.success) {
          this.newFaultList = res.result;
          this.run(this.newFaultList, "nfAnimation", 6);
        }
      });
    },
    initTask() {
      let planIdList = [];
      this.planProgressList.map((item) => {
        if (item.id) {
          planIdList.push(item.id)
        }
      });

      let list = [
        getWorkOrderList({
          isForMonitorScreenControl: 1,
          planIdList: planIdList,
          status: 1,
          pageNo: 1,
          pageSize: 100,
        }).then((res) => {
          if (res.success) {
            return res.result.records;
          }
          return [];
        }),
        getWorkOrderList({
          isForMonitorScreenControl: 1,
          planIdList: planIdList,
          status: 2,
          pageNo: 1,
          pageSize: 100,
        }).then((res) => {
          if (res.success) {
            return res.result.records;
          }
          return [];
        }),
      ];
      Promise.all(list).then((records) => {
        let recordList = [...records[0], ...records[1]];
        if (recordList.length < 100) {
          getWorkOrderList({
            isForMonitorScreenControl: 1,
            planIdList: planIdList,
            status: 8,
            pageNo: 1,
            pageSize: 100 - recordList.length,
          }).then((res) => {
            if (res.success) {
              recordList = [...recordList, ...res.result.records];
            }
            recordList.sort((a, b) => {
              return new Date(b.startTime).getTime() - new Date(a.startTime).getTime();
            });
            this.taskList = JSON.parse(JSON.stringify(recordList));
            setTimeout(() => {
              this.run(this.taskList, "taskAnimation", 6);
            }, 1000);
          });
        } else {
          recordList.sort((a, b) => {
            return new Date(b.startTime).getTime() - new Date(a.startTime).getTime();
          });
          this.taskList = JSON.parse(JSON.stringify(recordList));
          setTimeout(() => {
            this.run(this.taskList, "taskAnimation", 6);
          }, 1000);
        }
      });
    },
    initOutSource() {
      getListOutsourceTaskProgress({}).then((res) => {
        if (res.success) {
          this.outSourceList = res.result;
          setTimeout(() => {
            this.run(this.outSourceList, "osAnimation", 4);
          }, 500);
        }
      });
    },
    initWorkGroupTask() {
      getListWorkGroupTaskProgress({}).then((res) => {
        if (res.success) {
          this.workGroupTaskList = res.result;
          setTimeout(() => {
            this.run(this.workGroupTaskList, "wgAnimation", 4);
          }, 2000);
        }
      });
    },
    // 列计划进度
    initPlanProgress() {
      this.planProgressList = [];
      return Promise.all([
        getTrainPlanList({
          progressStatus: 1,
          pageNo: 1,
          pageSize: 3,
        }).then((res) => {
          if (res.success) {
            return this.planProgressList.push(...res.result.records);
          } else {
            return [];
          }
        }),
        getTrainPlanList({
          progressStatus: 2,
          pageNo: 1,
          pageSize: 1000,
        }).then((res) => {
          if (res.success) {
            return this.planProgressList.push(...res.result.records);
          } else {
            return [];
          }
        }),
        // .then(res =>{
        //   setTimeout(() => {
        //     this.run(this.planProgressList, 'planAnimation', 1)
        //   }, 1500);
        // })
      ]);
    },
    initCostList() {
      getCostItem2({}).then((res) => {
        if (res.success) {
          Object.assign(this.costList, res.result);
          this.createRadar("costRadar", "", [
            this.costList.userNum,
            this.costList.materialCost,
            this.costList.outsourceCost,
            this.costList.toolNum,
            this.costList.system,
          ]);
        }
      });
    },
    initLackList() {
      this.lackList = [];
      getLackMaterial({}).then((res) => {
        if (res.success) {
          this.lackList = res.result;
          setTimeout(() => {
            this.run(this.lackList, "lackAnimation", 4);
          }, 1500);
        }
      });
    },
    async initFaultTrend() {
      // getTrainFaultSum({
      //   year: this.$moment(new Date()).format("YYYY"),
      // }).then((res) => {
      //   if (res.success) {
      //     this.faultTrendList = res.result
      //       .filter((e, index) => index > 0)
      //       .map((e) => {
      //         return {
      //           trainNo: e.trainNo,
      //           repair: e.repair,
      //           warranty: e.warranty,
      //           outsource: 0,
      //         };
      //       });
      //     // this.createFalutAxis("falutTrendEchart", this.faultTrendList);
      //   } else {
      //     this.$message.error(res.message);
      //   }
      // });
    },

    createEchart(chartId, tipName, data) {
      let chart = echarts.init(document.getElementById(chartId));
      let option = {
        legend: {
          top: "7%",
          right: "5%",
          // top: '55%',
          orient: "vertical",
          textStyle: {
            color: "#fff",
            fontSize: 12,
          },
        },
        tooltip: {
          // show: false,
          trigger: "item",
          formatter: "{a} <br/>{b} : {c} ({d}%)",
        },
        series: [
          {
            name: tipName,
            type: "pie",
            radius: "65%",
            center: ["50%", "50%"],
            avoidLabelOverlap: false,
            // left: '15%',
            label: {
              show: false,
              formatter: "{b}",
              color: "#fff",
              fontSize: 12,
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
            },
            data: data,
          },
        ],
      };
      chart.setOption(option);
    },
    createEchart2(chartId, tipName, data) {
      let chart = echarts.init(document.getElementById(chartId));
      let option = {
        legend: {
          top: "7%",
          right: "5%",
          // top: '55%',
          orient: "vertical",
          textStyle: {
            color: "#fff",
            fontSize: 12,
          },
        },
        tooltip: {
          // show: false,
          trigger: "item",
          formatter: "{a} <br/>{b} : {c} ({d}%)",
        },
        series: [
          {
            name: tipName,
            type: "pie",
            radius: "70%",
            center: ["45%", "50%"],
            avoidLabelOverlap: false,
            label: {
              show: false,
              formatter: "{b}",
              color: "#fff",
              fontSize: 12,
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
            },
            data: data,
          },
        ],
      };
      chart.setOption(option);
    },
    createBarEchart(chartId, tirosList, dataList) {
      let series_names = [];
      let series = [];
      dataList.forEach((d) => {
        let _name = d.type;
        series_names.push(_name);
        let data = [];
        tirosList.forEach((m) => {
          data.push(d[m]);
        });
        series.push({
          name: _name,
          type: "bar",
          stack: "sum",
          barWidth: 30,
          itemStyle: {
            normal: {
              label: {
                show: false, //是否展示
              },
            },
          },
          data: data,
        });
      });
      let chart = echarts.init(document.getElementById(chartId));
      let option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
        },
        legend: {
          x: "right",
          y: "top",
          textStyle: {
            color: "#fff",
          },
          data: series_names,
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        textStyle: {
          color: "#fff",
        },
        yAxis: [{}],
        xAxis: [
          {
            type: "category",
            data: tirosList,
          },
        ],
        series: series,
      };
      chart.setOption(option);
    },
    createRadar(chartId, tipName, data) {
      let templist = JSON.parse(JSON.stringify(data));
      templist.sort((a, b) => b - a);
      let chart = echarts.init(document.getElementById(chartId));
      let option = {
        radar: {
          center: ["50%", "55%"],
          radius: "70%",
          indicator: [
            { name: "人工成本", color: "#fff", max: templist[0] },
            { name: "物料成本", color: "#fff", max: templist[0] },
            { name: "委外成本", color: "#fff", max: templist[0] },
            { name: "设备成本", color: "#fff", max: templist[0] },
            { name: "管理成本", color: "#fff", max: templist[0] },
          ],
        },
        textStyle: {
          fontSize: 12,
        },
        series: [
          {
            name: tipName,
            type: "radar",
            data: [
              {
                value: data,
                areaStyle: {
                  color: "#0efcff",
                },
                symbol: "none",
              },
            ],
          },
        ],
      };
      chart.setOption(option);
    },
    createFalutAxis(chartId, data) {
      let categoryList = ["架修故障", "质保故障"];
      let nameList = data.map((e) => e.trainNo);
      let seriesData = categoryList.map((e, index) => {
        let itemData = data.map((record) => {
          switch (index) {
            case 0:
              return record.repair;
            case 1:
              return record.warranty;
            // case 2:
            //   return record.outsource
          }
        });
        return {
          name: e,
          type: "bar",
          stack: "sum",
          barWidth: 30,
          itemStyle: {
            normal: {
              label: {
                show: false, //是否展示
              },
            },
          },
          data: itemData,
        };
      });
      let chart = echarts.init(document.getElementById(chartId));
      let option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
        },
        legend: {
          x: "right",
          y: "top",
          textStyle: {
            color: "#fff",
          },
          data: categoryList,
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        textStyle: {
          color: "#fff",
        },
        yAxis: [{}],
        xAxis: [
          {
            type: "category",
            data: nameList,
          },
        ],
        series: seriesData,
      };
      chart.setOption(option);
    },
    jumpAlertPage() {
      let routePath = "";
      switch (e) {
        case "wlkcyj":
          routePath = `/tiros/material/earlywarning`;
          break;
        case "qjsjyj":
          routePath = `/tiros/material/equipment/needcheck`;
          break;
        case "wzyxyj":
          routePath = `/tiros/material/earlywarning`;
          break;
        case "bjzbqyj":
          routePath = `/tiros/outsource/quality`;
          break;
        case "clsjyj":
          routePath = `/tiros/group/warningoff`;
          break;
        case "wwyqyj":
          routePath = `/tiros/outsource/perform`;
          break;
        case "yqgdyj":
          routePath = `/tiros/dispatch/workorder`;
          break;
        default:
          // wclgzyj
          routePath = `/tiros/dispatch/breakdown`;
          break;
      }
      // 增加路由权限判断
      if (this.permissionList.length) {
        for (let i = 0; i < this.permissionList.length; i++) {
          const permissionItem = this.permissionList[i];
          if (permissionItem.children) {
            for (let j = 0; j < permissionItem.children.length; j++) {
              const childrenItem = permissionItem.children[j];
              if (childrenItem.path == routePath) {
                if (e == "wclgzyj") {
                  sessionStorage.setItem("DEFAULT", "true");
                } else if (e == "wlkcyj") {
                  sessionStorage.setItem("DEFAULT", "1");
                } else if (e == "wzyxyj") {
                  sessionStorage.setItem("DEFAULT", "2");
                }
                this.$router.push({ path: routePath });
                return false;
              }
            }
          }
        }
        this.$message.warning("没有查看明细的权限!");
      }
    },
    run(list, animationParam, length) {
      if (list.length <= length) {
        return;
      }
      setInterval(() => {
        this[animationParam] = true;
        setTimeout(() => {
          list.push(list.shift());
          this.$nextTick(() => {
            this[animationParam] = false;
          });
        }, 500);
      }, 5000);
    },
  },
};
</script>
<style lang="less" scoped>
.font_color {
  color: #0efcff;
}

// 颜色列表
@colorArr: #c43d0c, #11865b, #545cb6, #ca7226, #9400D3, #216cc9, #c74e16, #1a914c;
@len: length(@colorArr);

.Loop(@index) when(@index<=@len) {
  // 执行内容
  // 类名参数要加大括号@{index}
  // 根据index获取对应的某个值 extract(数组名, 对应的序号)
  &:nth-child(@{index}) {
    background: extract(@colorArr, @index);
  }

  //递归调用 达到循环目的
  .Loop(@index+1);
}

.monitor_main:extend(.font_color) {
  position: relative;
  width: 100%;
  height: calc(100%);
  background-image: url("~@assets/tiros/images/monitor/background.jpg");
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
  .main_content {
    width: 97%;
    height: 100%;
    display: flex;
    flex-direction: column;
    .titleBox {
      display: flex;
      flex-flow: row nowrap;
      justify-content: space-between;
      align-items: center;
      font-size: 1.7vw;
      padding-top: 2vw;
      .title_text {
        padding: 0.2vw 1vw;
        // background: rgba(73, 160, 193, 0.3);
        background-image: url("~@assets/tiros/images/monitor/center_num2.png");
        background-size: 100% 100%;
        margin-right: 3vw;
      }
      .label_Box {
        display: flex;
        flex-flow: row nowrap;
        align-items: center;
        .label_text {
        }
        .box_num {
          margin-left: 2vw;
          text-align: center;
          line-height: 3vw;
          width: 3vw;
          height: 3vw;
          background-image: url("~@assets/tiros/images/monitor/center_num.png");
          background-size: 100% 100%;
        }
      }
    }

    //列计划进度样式
    .plan_card {
      width: 100%;
      margin: 0 auto;
      margin-top: 1vw;
      border: 2px solid;
      color: rgba(9, 180, 205, 80%);
      box-shadow: 0px 0px 3px 1px #014a5a;
      display: flex;
      .card_title {
        width: 9%;
        margin-right: 1%;
        font-size: 1.2vw;
        display: flex;
        flex-flow: row nowrap;
        justify-content: center;
        align-items: center;
        color: #0efcff;
        border-right: 1px solid rgba(9, 180, 205, 0.8);
        background: rgba(9, 180, 205, 0.05);
      }

      .m_content {
        flex: 1;
        overflow: hidden;
        // max-height: 88%;
        // height: 80%;
        padding: 0.3vw 0;
        padding-right: 2%;
        .m_item {
          height: 100%;
          font-size: 1.1vw;
          color: #fff;
          display: flex;
          align-items: center;
          line-height: 2vw;

          .m_text {
            width: 18%;
            margin-right: 1.5%;
          }

          .progress {
            width: 60%;
            height: 60%;

            .prog_text {
              font-size: 1vw;
              line-height: 1.6vw;
            }
          }
        }
      }
    }

    // 进度条样式
    .item_progress {
      .m_text {
        width: 60%;
      }

      .progress {
        flex: 1;
        overflow: hidden;
        position: relative;
        height: 50%;
        background-color: rgba(150, 150, 150, 60%);
        border-radius: 11px;
        overflow: hidden;
        .prog_text {
          width: 100%;
          position: relative;
          z-index: 10;
          height: 100%;
          font-size: 1.1vw;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .prog {
          height: 100%;
          position: absolute;
          top: 0;
          left: 0;
          background-image: linear-gradient(#6eb54c, #56903b, #6eb54c);
          margin: 0 !important;
          border-radius: 11px;
        }
      }
    }
    .bottomBox {
      flex: 1;
      overflow: hidden;
      padding: 1vw 0;
      display: flex;
      flex-flow: column nowrap;
      .forstBox {
        display: flex;
        flex-flow: row nowrap;
        justify-content: space-between;
        .header_info_box {
          border: 2px solid rgba(9, 180, 205, 0.8);
          box-shadow: 0px 0px 3px 1px #014a5a;
          width: 49%;
          .m_title {
            padding: 0.5vw 0;
            text-align: center;
            border-bottom: 2px solid rgba(9, 180, 205, 0.8);
            background: rgba(9, 180, 205, 0.05);
          }
          .m_content {
            display: flex;
            flex-flow: row wrap;
            padding: 0.3vw 0 0 0.3vw;
            .card_color_info:extend([flex-center-all]) {
              font-size: 1vw;
              flex-direction: column;
              color: #fff !important;
              text-align: center;
              padding: 0.3vw 0;
              margin: 0 0.3vw 0.3vw 0;
              .numberText {
                font-size: 1.2vw;
              }

              .Loop(1);
            }
          }
        }
      }
      .twoBox {
        flex: 1;
        overflow: hidden;
        padding-top: 1vw;
        display: flex;
        flex-flow: column nowrap;
        .topBox,
        .botBox {
          height: 40%;
          overflow: hidden;
          display: flex;
          justify-content: space-between;
          .card {
            height: 100%;
            overflow: hidden;
            width: calc((100% - 2vw) / 3);
            border: 2px solid rgba(9, 180, 205, 0.8);
            box-shadow: 0px 0px 3px 1px #014a5a;
            display: flex;
            flex-direction: column;
            .m_title {
              padding: 0.5vw 0;
              text-align: center;
              border-bottom: 2px solid rgba(9, 180, 205, 0.8);
              background: rgba(9, 180, 205, 0.05);
            }
            &.lack {
              .m_content {
                .m_item {
                  .m_text_code {
                    width: 40%;
                  }
                  .m_text_name {
                    width: 45%;
                  }
                  .m_text_lack {
                    width: 15%;
                    text-align: right;
                  }
                }
              }
            }
            .m_content {
              flex: 1;
              // padding: 0.5vw;
              // max-height: 88%;
              overflow: hidden;
              .m_item {
                height: calc(100% / 4);
                display: flex;
                align-items: center;
                color: #fff;
                font-size: 1.1vw;
                transform: translateY(0);
                //复写进度条样式
                // .m_text {
                //   max-width: 50%;
                // }
                .progress {
                  // min-width: 50%;
                  width: 45%;
                  height: 50%;
                  // margin-top: 1%;
                }
                div {
                  margin: 0 2%;
                }
              }
              .scroll_content {
                width: 100%;
                height: 100%;
                overflow: hidden;
                &::-webkit-scrollbar-thumb {
                  background: #2475ce;
                }
                &.animation {
                  .m_item {
                    &:nth-child(1),
                    &:nth-child(2),
                    &:nth-child(3),
                    &:nth-child(4),
                    &:nth-child(5),
                    &:nth-child(6) {
                      animation: 0.5s rowup linear 1 normal;
                      animation-fill-mode: forwards;
                    }
                  }
                }
              }
            }
          }
        }
        .botBox {
          margin-top: 1vw;
          height: calc(60% - 1vw);
          .card {
            height: 100%;
            overflow: hidden;
            width: calc((100% - 1vw) / 2);
            .m_item {
              height: calc(100% / 6) !important;
            }
          }
        }
      }
    }
  }
  .echart_container {
    width: 100%;
    height: 100%;
  }

  .ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .flex_1 {
    flex: 1;
  }

  [flex-center-all] {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .animation {
    .m_item {
      animation-fill-mode: forwards !important;
    }
  }

  //滚动动画
  @keyframes rowup {
    0% {
      transform: translateY(0);
    }
    100% {
      transform: translateY(-100%);
    }
  }

  .scroll_content {
    width: 100%;
    height: 100%;
    overflow: hidden;
  }

  // 滚动动画样式
  .scroll_content {
    &.animation {
      .m_item {
        &:nth-child(1),
        &:nth-child(2),
        &:nth-child(3),
        &:nth-child(4),
        &:nth-child(5),
        &:nth-child(6) {
          animation: 0.5s rowup linear 1 normal;
          // animation-fill-mode: forwards;
        }
      }
    }
  }
}
</style>
