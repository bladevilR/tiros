<template >
  <div class="monitor-main">
    <div class="main-content">
      <div class="main_top">
        <!-- 上方头部区域 -->
        <div class="main_top_left">
          <!-- 预警信息 -->
          <div class="header_info_box">
            <div class="m_title"><span>预警信息</span></div>
            <div class="m_content">
              <a
                class="card_color_info alert"
                v-for="item in alertList"
                :key="item.itemCode"
                @click="jumpAlertPage(item.itemCode)"
              >
                <div>{{ item.itemValue }}</div>
                <div class="small_text">{{ item.itemDesc }}</div>
              </a>
            </div>
          </div>
          <!-- 故障排名 -->
          <div class="header_info_box">
            <div class="m_title"><span>班组故障排名</span></div>
            <div class="m_content group_fault">
              <div class="fault_item" v-for="item in groupFaultList" :key="item.groupName">
                <div class="flex_1">{{ item.groupName }}</div>
                <div class="fault_count">{{ item.currentMonth }}</div>
              </div>
            </div>
          </div>
        </div>
        <!-- 上方中部面板 -->
        <div class="main_top_midden">
          <div class="midden_info m_title">
            <img src="~@assets/tiros/images/monitor/title_bg.png" alt="" />
            <div class="text">苏州车辆架大修信息监控大屏</div>
          </div>
          <div class="midden_info day">
            <div class="box">
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
              <div class="label_text">安全生产天数</div>
            </div>
          </div>
          <div class="midden_info chart">
            <div class="chart_box">
              <div class="m_title"><span>物料消耗占比</span></div>
              <div class="echart_container" id="materialEchart"></div>
            </div>
            <div class="chart_box">
              <div class="m_title"><span>故障系统占比</span></div>
              <div class="echart_container" id="faultEchart"></div>
            </div>
          </div>
        </div>
        <div class="main_top_right">
          <!-- 数据区 -->
          <div class="header_info_box area_data">
            <div class="m_title"><span>数据区</span></div>
            <div class="m_content">
              <div class="card_color_info data" v-for="item in dataList" :key="item.itemCode">
                <div>{{ item.itemValue }}</div>
                <div class="small_text">{{ item.itemDesc }}</div>
              </div>
            </div>
          </div>
          <!-- 物料消耗占比 -->
          <div class="header_info_box">
            <div class="m_title"><span>物料消耗分类占比</span></div>
            <div class="m_content">
              <div class="echart_container" id="materialTypeEchart"></div>
            </div>
          </div>
        </div>
      </div>
      <!-- 列计划进度显示区 -->
      <div class="main_box cost">
        <!-- 2021-07-23 调整显示列计划进度 -->
        <!-- <div class="card">
          <div class="label">人工成本</div>
          <div class="text">{{ costList.userNum }}人</div>
        </div>
        <div class="card">
          <div class="label">物料成本</div>
          <div class="text">{{ costList.materialCost }}元</div>
        </div>
        <div class="card">
          <div class="label">委外成本</div>
          <div class="text">{{ costList.outsourceCost }}万元</div>
        </div>
        <div class="card">
          <div class="label">设备成本</div>
          <div class="text">{{ costList.toolNum }}台</div>
        </div>
        <div class="card">
          <div class="label">管理成本</div>
          <div class="text">{{ 0 }}</div>
        </div> -->
        <div class="plan-card">
          <div class="card_title">列计划进度</div>
          <div class="m_content">
            <div class="scroll_content" :class="{'animation': planAnimation}">
              <div class="m_item item_progress" v-for="item in planProgressList" :key="item.id">
                <div class="m_text ellipsis">
                  <span>{{`${item.trainNo}车辆-${item.repairProgramName}计划`}}</span>
                </div>
                <div class="progress">
                  <div class="prog" :style="{ width: `${item.progress}%` }"></div>
                  <div class="prog_text">{{item.progress + '%'}}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="main_box task">
        <!-- 最新故障 -->
        <div class="card">
          <div class="m_title"><span>最新故障</span></div>
          <div class="m_content">
            <div class="scroll_content" :class="{'animation': nfAnimation}">
              <div class="m_item" v-for="item in newFaultList" :key="item.id">
                <div class="flex_1 ellipsis">{{ item.faultDesc }}</div>
                <div>{{ item.reportTime }}</div>
              </div>
            </div>
          </div>
        </div>
        <!-- 委外任务进度 -->
        <div class="card out_source">
          <div class="m_title"><span>委外任务进度</span></div>
          <div class="m_content">
            <div class="scroll_content" :class="{'animation': osAnimation}">
              <div class="m_item item_progress" v-for="item in outSourceList" :key="item.id">
                <div class="m_text ellipsis">
                  <span>{{ item.Name }}</span>
                </div>
                <div class="progress">
                  <div class="prog" :style="{ width: `${item.PercentComplete}%` }"></div>
                  <div class="prog_text">{{item.PercentComplete + '%'}}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 工单作业 -->
        <div class="card">
          <div class="m_title"><span>工单作业</span></div>
          <div class="m_content">
            <div class="scroll_content" :class="{'animation': taskAnimation}">
              <div class="m_item" v-for="item in taskList" :key="item.id" >
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
      </div>
      <div class="main_box compare">
        <!-- 成本雷达区 -->
        <div class="card lack">
          <div class="m_title"><span>当前缺料</span></div>
          <div class="m_content">
            <div class="scroll_content" :class="{'animation': lackAnimation}">
              <div class="m_item" v-for="item in lackList" :key="item.id">
                <div class="m_text_code ellipsis">{{ item.code }}</div>
                <div class="m_text_name ellipsis">{{ item.name }}</div>
                <div class="m_text_lack ellipsis">{{ item.lack }}</div>
              </div>
            </div>
          </div>
        </div>
        <!-- 物料成本趋势 -->
        <div class="card flex_2">
          <div class="m_title"><span>物料成本趋势</span></div>
          <div class="m_content">
            <div class="echart_container" id="materialCostEchart"></div>
          </div>
        </div>
        <!-- 故障趋势 -->
        <div class="card flex_2">
          <div class="m_title"><span>故障趋势</span></div>
          <div class="m_content">
            <div class="echart_container" id="falutTrendEchart"></div>
          </div>
        </div>
        <!-- 工班任务进度 -->
        <div class="card group_task">
          <div class="m_title"><span>工班任务进度</span></div>
          <div class="m_content">
            <div class="scroll_content" :class="{'animation': wgAnimation}">
              <div class="m_item item_progress" v-for="item in workGroupTaskList" :key="item.groupName">
                <div class="m_text ellipsis">
                  <span>{{ item.groupName }}</span>
                </div>
                <div class="progress">
                  <div class="prog" :style="{ width: `${item.progress}%` }"></div>
                  <div class="prog_text">{{item.progress + '%'}}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
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
} from '@api/tirosKanbanApi'
import { getWorkOrderList, getTrainPlanList } from '@/api/tirosDispatchApi'
import { getFaultRanking } from '@api/tirosKanbanApi'
import { getCostStatistics, getFaultStatistics, getLackMaterial } from '@api/tirosProductionApi'
import { getSysConfig } from '@api/api'
import { getTrainFaultSum } from '@api/tirosReportApi'

export default {
  name: 'MonitorScreen',
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
      safeDays: '00000',
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
    }
  },
  computed: {
    permissionList() {
      return this.$store.getters.permissionList || []
    },
  },
  mounted() {
    this.initAll()
    this.initRunDay()
  },
  methods: {
    initAll() {
      this.materialUseEchart()
      this.faultEchart()
      this.martrialCostEchart()
      this.initAlert()
      this.initData()
      this.initFaultOfGroup()
      this.initNewFault()
      this.initTask()
      this.initOutSource()
      this.initWorkGroupTask()
      // this.initCostList()
      this.initLackList()
      this.initFaultTrend()
      this.initPlanProgress()
    },
    initRunDay() {
      getSysConfig('SafeServiceDays').then((res) => {
        if (res.success) {
          this.safeDays = (res.result.configValue ? Number(res.result.configValue) : 0).toString().padStart(5, '0')
        } else {
          this.safeDays = '00000'
          this.$message.error(res.message)
        }
      })
    },
    async materialUseEchart() {
      let data = await getCostStatistics({}).then((res) => {
        let template = {
          consumeDataList: [],
          categoryDataList: [],
        }
        if (res.success) {
          if (res.result && res.result.consumeDataList && res.result.consumeDataList.length) {
            template.consumeDataList = res.result.consumeDataList
              .filter((e, index) => index < 6)
              .map((e) => {
                return { value: e.count, name: e.item }
              })
          }
          if (res.result && res.result.categoryDataList && res.result.categoryDataList.length) {
            template.categoryDataList = res.result.categoryDataList
              .filter((e, index) => index < 6)
              .map((e) => {
                return { value: e.count, name: e.item }
              })
          }
        } else {
          this.$message.error(res.message)
        }
        return template
      })
      this.createEchart('materialEchart', '成本', data.consumeDataList)
      this.createEchart2('materialTypeEchart', '物料消耗数量', data.categoryDataList)
    },
    async faultEchart() {
      let list = await getFaultStatistics({}).then((res) => {
        if (res.success) {
          // return []
          if (res.result && res.result.systemItemList && res.result.systemItemList.length) {
            return res.result.systemItemList
              .sort((a, b) => b.faultCount - a.faultCount)
              .filter((e, index) => index < 6)
              .map((e) => {
                return { value: e.faultCount, name: e.systemName }
              })
          }
        } else {
          this.$message.error(res.message)
        }
        return []
      })
      this.createEchart('faultEchart', '故障数', list)
    },
    async martrialCostEchart() {
      let tirosList = await getLastTenTrainNo({}).then((res) => {
        if (res.success) {
          return res.result
        } else {
          this.$message.error(res.message)
        }
        return []
      })
      let list = await getLastTenTrainCost({}).then((res) => {
        console.log(res)
        if (res.success) {
          if (tirosList && tirosList.length > 0) {
            if (res.result && res.result.length) {
              return res.result
            }
          }
        } else {
          this.$message.error(res.message)
        }
        return []
      })
      if (!tirosList.length || !list.length) {
        tirosList = []
        list = []
      }
      this.createBarEchart('materialCostEchart', tirosList, list)
      // <div class="echart_container" id="faultEchart"></div>
    },
    initAlert() {
      getAlert().then((res) => {
        if (res.success) {
          this.alertList = res.result
        }
      })
    },
    initData() {
      getData().then((res) => {
        if (res.success) {
          this.dataList = res.result
        }
      })
    },
    initFaultOfGroup() {
      getFaultRanking({}).then((res) => {
        if (res.success) {
          res.result.sort((a, b) => b.currentMonth - a.currentMonth)
          this.groupFaultList = res.result
        }
      })
    },
    initNewFault() {
      getListLatest({}).then((res) => {
        if (res.success) {
          this.newFaultList = res.result
          this.run(this.newFaultList, 'nfAnimation', 4)
        }
      })
    },
    initTask() {
      let list = [
        getWorkOrderList({
          status: 1,
          pageNo: 1,
          pageSize: 100,
        }).then((res) => {
          if (res.success) {
            return res.result.records
          }
          return []
        }),
        getWorkOrderList({
          status: 2,
          pageNo: 1,
          pageSize: 100,
        }).then((res) => {
          if (res.success) {
            return res.result.records
          }
          return []
        }),
      ]
      Promise.all(list).then((records) => {
        let recordList = [...records[0], ...records[1]]
        if (recordList.length < 100) {
          getWorkOrderList({
            status: 8,
            pageNo: 1,
            pageSize: 100 - recordList.length,
          }).then((res) => {
            if (res.success) {
              recordList = [...recordList, ...res.result.records]
            }
            recordList.sort((a, b) => {
              return new Date(b.startTime).getTime() - new Date(a.startTime).getTime()
            })
            this.taskList = JSON.parse(JSON.stringify(recordList))
            setTimeout(() => {
              this.run(this.taskList, 'taskAnimation', 4)
            }, 1000);
          })
        }else {
          recordList.sort((a, b) => {
            return new Date(b.startTime).getTime() - new Date(a.startTime).getTime()
          })
          this.taskList = JSON.parse(JSON.stringify(recordList))
          setTimeout(() => {
            this.run(this.taskList, 'taskAnimation', 4)
          }, 1000);
        }
      })
    },
    initOutSource() {
      getListOutsourceTaskProgress({}).then((res) => {
        if (res.success) {
          this.outSourceList = res.result
          setTimeout(() => {
            this.run(this.outSourceList, 'osAnimation', 4)
          }, 500);
        }
      })
    },
    initWorkGroupTask() {
      getListWorkGroupTaskProgress({}).then((res) => {
        if (res.success) {
          this.workGroupTaskList = res.result
          setTimeout(() => {
            this.run(this.workGroupTaskList, 'wgAnimation', 5)
          }, 2000);
        }
      })
    },
    // 列计划进度
    initPlanProgress() {
      this.planProgressList = []
      Promise.all([
        getTrainPlanList({
          progressStatus: 1,
          pageNo: 1,
          pageSize: 1000
        }).then((res) => {
          if (res.success) {
            return this.planProgressList.push(...res.result.records)
          } else {
            return []
          }
        }),
        getTrainPlanList({
          progressStatus: 2,
          pageNo: 1,
          pageSize: 1000
        }).then((res) => {
          if (res.success) {
            return this.planProgressList.push(...res.result.records)
          } else {
            return []
          }
        }).then(res =>{
          setTimeout(() => {
            this.run(this.planProgressList, 'planAnimation', 1)
          }, 1500);
        })
      ])
      
    },
    initCostList() {
      getCostItem2({}).then((res) => {
        if (res.success) {
          Object.assign(this.costList, res.result)
          this.createRadar('costRadar', '', [
            this.costList.userNum,
            this.costList.materialCost,
            this.costList.outsourceCost,
            this.costList.toolNum,
            this.costList.system,
          ])
        }
      })
    },
    initLackList(){
      this.lackList = []
      getLackMaterial({}).then(res => {
        console.log(res)
        if (res.success) {
          this.lackList = res.result
          setTimeout(() => {
            this.run(this.lackList, 'lackAnimation', 6)
          }, 1500);
        }
      })
    },
    async initFaultTrend() {
      getTrainFaultSum({
        year: this.$moment(new Date()).format('YYYY'),
      }).then((res) => {
        if (res.success) {
          this.faultTrendList = res.result
            .filter((e, index) => index > 0)
            .map((e) => {
              return { trainNo: e.trainNo, repair: e.repair, warranty: e.warranty, outsource: 0 }
            })
          this.createFalutAxis('falutTrendEchart', this.faultTrendList)
        } else {
          this.$message.error(res.message)
        }
      })
    },

    createEchart(chartId, tipName, data) {
      let chart = echarts.init(document.getElementById(chartId))
      let option = {
        legend: {
          top: '7%',
          right: '5%',
          // top: '55%',
          orient: 'vertical',
          textStyle: {
            color: '#fff',
            fontSize: 12
          },
        },
        tooltip: {
          // show: false,
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
        },
        series: [
          {
            name: tipName,
            type: 'pie',
            radius: '65%',
            center: ['50%', '50%'],
            avoidLabelOverlap: false,
            // left: '15%',
            label: {
              show: false,
              formatter: '{b}',
              color: '#fff',
              fontSize: 12
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)',
              },
            },
            data: data,
          },
        ],
      }
      chart.setOption(option)
    },
    createEchart2(chartId, tipName, data) {
      let chart = echarts.init(document.getElementById(chartId))
      let option = {
        legend: {
          top: '7%',
          right: '5%',
          // top: '55%',
          orient: 'vertical',
          textStyle: {
            color: '#fff',
            fontSize: 12
          },
        },
        tooltip: {
          // show: false,
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
        },
        series: [
          {
            name: tipName,
            type: 'pie',
            radius: '70%',
            center: ['45%', '50%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              formatter: '{b}',
              color: '#fff',
              fontSize: 12
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)',
              },
            },
            data: data,
          },
        ],
      }
      chart.setOption(option)
    },
    createBarEchart(chartId, tirosList, dataList) {
      let series_names = []
      let series = []
      dataList.forEach((d) => {
        let _name = d.type
        series_names.push(_name)
        let data = []
        tirosList.forEach((m) => {
          data.push(d[m])
        })
        series.push({
          name: _name,
          type: 'bar',
          stack: 'sum',
          barWidth: 30,
          itemStyle: {
            normal: {
              label: {
                show: false, //是否展示
              },
            },
          },
          data: data,
        })
      })
      let chart = echarts.init(document.getElementById(chartId))
      let option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
          },
        },
        legend: {
          x: 'right',
          y: 'top',
          textStyle: {
            color: '#fff',
          },
          data: series_names,
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true,
        },
        textStyle: {
          color: '#fff',
        },
        yAxis: [{}],
        xAxis: [
          {
            type: 'category',
            data: tirosList,
          },
        ],
        series: series,
      }
      chart.setOption(option)
    },
    createRadar(chartId, tipName, data) {
      let templist = JSON.parse(JSON.stringify(data))
      templist.sort((a, b) => b - a)
      let chart = echarts.init(document.getElementById(chartId))
      let option = {
        radar: {
          center: ['50%', '55%'],
          radius: '70%',
          indicator: [
            { name: '人工成本', color: '#fff', max: templist[0] },
            { name: '物料成本', color: '#fff', max: templist[0] },
            { name: '委外成本', color: '#fff', max: templist[0] },
            { name: '设备成本', color: '#fff', max: templist[0] },
            { name: '管理成本', color: '#fff', max: templist[0] },
          ],
        },
        textStyle: {
          fontSize: 12
        },
        series: [
          {
            name: tipName,
            type: 'radar',
            data: [
              {
                value: data,
                areaStyle: {
                  color: '#0efcff',
                },
                symbol: 'none',
              },
            ],
          },
        ],
      }
      chart.setOption(option)
    },
    createFalutAxis(chartId, data) {
      let categoryList = ['架修故障', '质保故障']
      let nameList = data.map((e) => e.trainNo)
      let seriesData = categoryList.map((e, index) => {
        let itemData = data.map((record) => {
          switch (index) {
            case 0:
              return record.repair
            case 1:
              return record.warranty
            // case 2:
            //   return record.outsource
          }
        })
        return {
          name: e,
          type: 'bar',
          stack: 'sum',
          barWidth: 30,
          itemStyle: {
            normal: {
              label: {
                show: false, //是否展示
              },
            },
          },
          data: itemData,
        }
      })
      let chart = echarts.init(document.getElementById(chartId))
      let option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
          },
        },
        legend: {
          x: 'right',
          y: 'top',
          textStyle: {
            color: '#fff',
          },
          data: categoryList,
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true,
        },
        textStyle: {
          color: '#fff',
        },
        yAxis: [{}],
        xAxis: [
          {
            type: 'category',
            data: nameList,
          },
        ],
        series: seriesData,
      }
      chart.setOption(option)
    },
    jumpAlertPage() {
      let routePath = ''
      switch (e) {
        case 'wlkcyj':
          routePath = `/tiros/material/earlywarning`
          break
        case 'qjsjyj':
          routePath = `/tiros/material/equipment/needcheck`
          break
        case 'wzyxyj':
          routePath = `/tiros/material/earlywarning`
          break
        case 'bjzbqyj':
          routePath = `/tiros/outsource/quality`
          break
        case 'clsjyj':
          routePath = `/tiros/group/warningoff`
          break
        case 'wwyqyj':
          routePath = `/tiros/outsource/perform`
          break
        case 'yqgdyj':
          routePath = `/tiros/dispatch/workorder`
          break
        default:
          // wclgzyj
          routePath = `/tiros/dispatch/breakdown`
          break
      }
      // 增加路由权限判断
      if (this.permissionList.length) {
        for (let i = 0; i < this.permissionList.length; i++) {
          const permissionItem = this.permissionList[i]
          if (permissionItem.children) {
            for (let j = 0; j < permissionItem.children.length; j++) {
              const childrenItem = permissionItem.children[j]
              if (childrenItem.path == routePath) {
                if (e == 'wclgzyj') {
                  sessionStorage.setItem('DEFAULT', 'true')
                } else if (e == 'wlkcyj') {
                  sessionStorage.setItem('DEFAULT', '1')
                } else if (e == 'wzyxyj') {
                  sessionStorage.setItem('DEFAULT', '2')
                }
                this.$router.push({ path: routePath })
                return false
              }
            }
          }
        }
        this.$message.warning('没有查看明细的权限!')
      }
    },
    run(list, animationParam, length){
      if (list.length <= length) {
        return
      }
      setInterval(() => {
        this[animationParam] = true
        setTimeout(() => {
          list.push(list.shift())
          this.$nextTick(()=>{
            this[animationParam] = false
          })
        }, 500);
      }, 5000);
    }
  },
}
</script>
<style lang="less" scoped>
.font_color {
  color: #0efcff;
}

// 颜色列表
@colorArr: #37d2d4, #19ca88, #858ff8, #fd9133, #d3ac1a, #2e8cff, #f6580e, #26aa5c;
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

.monitor-main:extend(.font_color) {
  position: relative;
  width: 100%;
  height: calc(100%);
  background-image: url('~@assets/tiros/images/monitor/background.jpg');
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;

  img {
    width: 100%;
    position: relative;
  }

  .main-content {
    width: 97%;
    height: 95%;
    display: flex;
    flex-direction: column;
  }

  .main_top {
    width: 100%;
    height: 37%;
    display: flex;

    //上方左右模板
    .main_top_left,
    .main_top_right {
      width: 17.6%;
      height: 100%;
      display: flex;
      flex-direction: column;
    }
    //上方中间模板
    .main_top_midden {
      width: 64.8%;
      height: 100%;
      display: flex;
      flex-direction: column;

      .midden_info {
        // 标题
        &.m_title:extend([flex-center-all]) {
          width: 100%;
          height: 20%;
          transform: translateY(-11%);

          img {
            width: 86%;
            height: 100%;
            position: relative;
          }

          .text {
            position: absolute;
            font-size: 1.7vw;
            font-weight: bold;
          }
        }
        &.day:extend([flex-center-all]) {
          height: 23%;

          .box:extend([flex-center-all]) {
            width: 38%;
            height: 100%;
            justify-content: space-around !important;
            position: relative;

            .box_num:extend([flex-center-all]) {
              font-size: 2vw;
              font-weight: bold;
              width: 18%;
              height: 100%;

              background-image: url('~@assets/tiros/images/monitor/center_num.png');
              background-size: 100% 100%;
              background-repeat: no-repeat;

              .text {
                position: absolute;
              }
            }

            .label_text {
              position: absolute;
              right: 106%;
              font-size: 1.5vw;
              width: 50%;
              text-align: right;
            }
          }
        }
        &.chart {
          height: 100%;
          flex: 1;
          display: flex;
          padding-top: 1%;
          padding-left: 1%;
          padding-right: 1%;
          justify-content: space-around;

          .chart_box {
            position: relative;
            width: 49%;
            height: 100%;

            background-image: url('~@assets/tiros/images/monitor/main_top_bottom.png');
            background-size: 100% 100%;
            background-repeat: no-repeat;

            .m_title {
              position: absolute;
              top: 0;
              left: 0;
              width: 100%;
              height: 20%;
              padding-left: 3%;
              display: flex;
              align-items: center;
              font-size: 0.8vw;
            }
          }
        }
      }
    }
    .main_top_right {
      width: 17.6%;
      height: 100%;
    }
  }

  .main_box {
    width: 100%;
    display: flex;
    justify-content: space-between;

    .card {
      flex: 1;
      background-size: 100% 100%;
      background-repeat: no-repeat;
      &:not(:last-child) {
        margin-right: 1%;
      }

      .m_content {
        flex: 1;
        padding: 2%;
        max-height: 72%;

        .scroll_content {
          width: 100%;
          height: 100%;
          overflow: hidden;

          &::-webkit-scrollbar-thumb {
            background: #2475ce;
          }

          &.animation{
            .m_item{
              &:nth-child(1),&:nth-child(2),&:nth-child(3),&:nth-child(4),&:nth-child(5),&:nth-child(6){
                animation: .5s rowup linear 1 normal;
                // animation-fill-mode: forwards;
              }
            }
          }
        }

        .m_item {
          height: 25%;
          display: flex;
          align-items: center;
          color: #fff;
          font-size: 0.7vw;
          transform: translateY(0);

          div {
            margin: 0 2%;
          }
        }
      }
    }

    //成本信息
    &.cost {
      height: 7.1%;
      margin-top: 1%;

      .card {
        // width: 19%;
        box-shadow: 1px 2px 10px 1px rgba(14, 252, 255, 0.53), inset 5px 4px 100px 1px rgba(14, 252, 255, 0.34);
        position: relative;

        &::before {
          content: '';
          width: 100%;
          height: 100%;
          position: absolute;
          top: 0;
          left: 0;
          background-image: url('~@assets/tiros/images/monitor/main_middle.png');
          background-size: 100% 100%;
          background-repeat: no-repeat;
          z-index: 2;
        }

        .label {
          font-size: 0.75vw;
          text-indent: 0.8vw;
          transform: translateY(30%);
        }

        .text {
          font-size: 1.1vw;
          font-weight: bold;
          color: white;
          text-align: center;
          // transform: translateY(-15%);
        }
      }

      //列计划进度样式
      .plan-card{
        border: 2px solid;
        width: 100%;
        height: 100%;
        color: rgba(9, 180, 205, 80%);;
        box-shadow: 0px 0px 3px 1px #014a5a;
        
        display: flex;
        align-items: center;

        .card_title{
          width: 9%;
          margin-right: 1%;
          font-size: 1.2vw;
          line-height: 3vw;
          text-align: center;
          color: #0efcff;
          height: 100%;
          border-right: 1px solid rgba(9, 180, 205, 0.8);
          background: rgba(9, 180, 205, 0.05);
        }
        
        .m_content {
            flex: 1;
            // max-height: 88%;
            height: 80%;
            padding-right: 2%;

            .m_item {
                height: 100%;
                font-size: 1vw;
                color: #fff;
                display: flex;
                align-items: center;
                line-height: 2vw;

              .m_text{
                width: auto;
                margin-right: 1.5%;
              }

              .progress {
                width: 80%;
                height: 60%;

                .prog_text{
                  font-size: .8vw;
                  line-height: 1.6vw;
                }
              }
            }
          }
      }
    }

    //故障，工单任务，委外
    &.task {
      height: 21%;
      margin-top: 1%;
      color: white;

      .card {
        display: flex;
        flex-direction: column;
        background-image: url('~@assets/tiros/images/monitor/main_bottopm_top1.png');
        &:not(:last-child) {
          margin-right: 2%;
        }

        .m_title {
          margin-top: 4%;
          padding-left: 8.5%;
          height: 18%;
          display: flex;
          align-items: center;
          font-size: 0.8vw;
        }
      }
    }

    &.compare {
      // flex: 1;
      margin-top: 1%;
      height: 29.5%;

      .card {
        width: 20%;
        flex: none;
        background-image: url('~@assets/tiros/images/monitor/main_bottom_bottom.png');
        &.flex_2 {
          background-image: url('~@assets/tiros/images/monitor/main_bootm_middle.png');
        }

        display: flex;
        flex-direction: column;

        .m_title:extend([flex-center-all]) {
          width: 100%;
          height: 12%;
          text-align: center;
          font-size: 0.75vw;
        }
        .m_content {
          flex: 1;
          padding: 1.5% 3.5% 2.5% 2.2%;
          max-height: 88%;
        }

        //工班任务样式
        &.group_task {
          .m_content {
            .m_item {
              height: 20%;
              font-size: 0.6vw;

              //复写进度条样式
              // .m_text {
              //   max-width: 50%;
              // }
              .progress {
                // min-width: 50%;
                width: 45%;
                height: 30%;
                margin-top: 1%;
              }
            }
          }
        }

        //缺料区样式
        &.lack{
          .m_content {
            .m_item {
              height: 20%;
              font-size: 0.6vw;

              .m_text_cost{
                width: 40%;
              }
              .m_text_name{
                width: 45%;
              }
              .m_text_lack{
                width: 15%;
                text-align: center;
              }
            }
          }
        }
      }

      .flex_2 {
        flex: 2;
      }
    }
  }

  .header_info_box {
    width: 100%;
    height: 46%;
    background-image: url('~@assets/tiros/images/monitor/main_top_left.png');
    background-size: 100% 100%;
    background-repeat: no-repeat;
    display: flex;
    flex-direction: column;

    &:last-child {
      margin-top: auto;
    }

    .m_title:extend([flex-center-all]) {
      height: 18%;
      text-align: center;
      color: #0efcff;
      font-size: 0.75vw;
    }

    .m_content {
      flex: 1;
      padding: 1.5% 3.5% 2.5% 2.2%;
      display: flex;
      flex-wrap: wrap;
      justify-content: center;
      align-items: center;

      // 班组故障
      &.group_fault {
        flex-direction: column;
        justify-content: flex-start;

        .fault_item {
          width: 100%;
          height: 20%;
          display: flex;
          align-items: center;
          color: #fff;

          .fault_count {
            padding-right: 5%;
          }

          div {
            margin: 0 2%;
          }
        }
      }

      .card_color_info:extend([flex-center-all]) {
        width: 24%;
        height: 47%;
        font-size: 0.7vw;
        flex-direction: column;
        color: #fff !important;
        text-align: center;
        margin: 0.5%;

        & {
          .small_text {
            font-size: 0.5vw;
          }
        }

        .Loop(1);
      }
    }

    // 数据区宽度复写
    &.area_data .m_content .card_color_info {
      width: 32%;
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
    position: relative;
    height: 50%;
    background-color: rgba(150, 150, 150, 60%);
    border-radius: 11px;
    overflow: hidden;
    .prog_text{
      width: 100%;
      text-align: center;
      position: relative;
      z-index: 10;
      height: 100%;
      font-size: 0.4vw;
      line-height: .7vw;
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

.animation{
  .m_item{
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
  &.animation{
    .m_item{
      &:nth-child(1),&:nth-child(2),&:nth-child(3),&:nth-child(4),&:nth-child(5),&:nth-child(6){
        animation: .5s rowup linear 1 normal;
        // animation-fill-mode: forwards;
      }
    }
  }
}
</style>