<template style="height: calc(100% - 5px)">
  <div style="height: calc(100vh - 5px); overflow: auto" @click="hiddenFilter">
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
              <!--              <j-dict-select-tag
                v-model="lineId"
                dictCode="bu_mtr_line,line_name,line_id"
              />-->
              <line-select-list v-model="lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="车辆">
              <j-dict-select-seach-tag v-model="queryParam.trainNo" :dictCode="dictTrainStr" />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="时间">
              <a-range-picker format="YYYY-MM-DD" :placeholder="['开始时间', '结束时间']" @change="onDayChange" :defaultPickerValue="defaultDateRange" />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item>
              <a-checkbox-group v-model="programTypeItems" style="width: 100%; margin-top: 6px">
                <a-row>
                  <a-col :span="9">
                    <a-checkbox value="1"> 架修 </a-checkbox>
                  </a-col>
                  <a-col :span="9">
                    <a-checkbox value="2"> 大修 </a-checkbox>
                  </a-col>
                </a-row>
              </a-checkbox-group>
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
        <a-col :md="8" :sm="24">
          <na-card-light-table title="最新故障">
            <vxe-table
              border
              ref="listTable"
              :align="allAlign"
              :data="tableDataX"
              show-overflow="tooltip"
              :edit-config="{ trigger: 'manual', mode: 'row' }"
              height="200px"
              highlight-hover-row
            >
              <vxe-table-column field="order" title="序号" align="center" width="40"></vxe-table-column>
              <vxe-table-column 
                field="faultDesc" 
                align="left"
                header-align="center" 
                title="故障名称" width="30%">
                <template v-slot="{row}">
                  <a @click="viewFault(row)">{{row.faultDesc}}</a>
                </template>
              </vxe-table-column>
              <vxe-table-column
                field="workstationName"
                title="故障工位"
                width="30%"
                align="left"
                header-align="center"
              ></vxe-table-column>
              <!-- <vxe-table-column field="reportTime" title="报告时间" width="20%"></vxe-table-column> -->
            </vxe-table>
            <div style="box-sizing:border-box;text-align: center; padding-top: 10px; padding-bottom: 10px;border: 1px #efefef solid;border-top: 0;">
              最新新增<span style="color: #d9001b; font-weight: bold">{{ this.dateX.todayAdded }}</span
              >个， 合计<span style="color: #d9001b; font-weight: bold">{{ this.dateX.total }}</span
              >个， 已解决<span style="color: #d9001b; font-weight: bold">{{ this.dateX.resolved }}</span
              >个， 未解决<span style="color: red; font-weight: bold">{{ this.dateX.unresolved }}</span
              >个
            </div>
          </na-card-light-table>
        </a-col>
        <a-col :md="8" :sm="24">
          <na-card-light-table title="最新委外故障">
            <vxe-table
              border
              ref="listTable"
              :align="allAlign"
              :data="tableDataW"
              :edit-config="{ trigger: 'manual', mode: 'row' }"
              height="200px"
              show-overflow="tooltip"
              highlight-hover-row
            >
              <vxe-table-column field="order" title="序号" align="center" width="40"></vxe-table-column>
              <vxe-table-column
                field="faultDesc"
                title="故障名称"
                width="30%"
                align="left"
                header-align="center"
              >
                <template v-slot="{row}">
                  <a @click="viewFault(row)">{{row.faultDesc}}</a>
                </template>
              </vxe-table-column>
              <vxe-table-column
                field="assetName"
                title="委外部件"
                width="30%"
                align="left"
                header-align="center"
              ></vxe-table-column>
              <!-- <vxe-table-column field="reportTime" title="报告时间" width="20%"></vxe-table-column> -->
            </vxe-table>
            <div style="box-sizing:border-box;text-align: center; padding-top: 10px; padding-bottom: 10px;border: 1px #efefef solid;border-top: 0;">
              今日新增<span style="color: #d9001b; font-weight: bold">{{ this.dateW.todayAdded }}</span
              >个， 合计<span style="color: #d9001b; font-weight: bold">{{ this.dateW.total }}</span
              >个， 已解决<span style="color: #d9001b; font-weight: bold">{{ this.dateW.resolved }}</span
              >个， 未解决<span style="color: red; font-weight: bold">{{ this.dateW.unresolved }}</span
              >个
            </div>
          </na-card-light-table>
        </a-col>
        <a-col :md="8" :sm="24">
          <na-card-light-table title="班组发现故障排名">
            <vxe-table
              border
              ref="listTable"
              :align="allAlign"
              :data="tableDataP"
              show-overflow="tooltip"
              :edit-config="{ trigger: 'manual', mode: 'row' }"
              height="241px"
            >
              <vxe-table-column field="order" align="center" title="序号" width="48"></vxe-table-column>
              <vxe-table-column
                field="groupName"
                title="工班"
                width="20%"
                align="left"
                header-align="center"
              ></vxe-table-column>
              <vxe-table-column field="latestMonth" title="上月数" width="20%"></vxe-table-column>
              <vxe-table-column field="currentMonth" title="本月数" width="20%"></vxe-table-column>
              <vxe-table-column field="increase" title="提升" width="20%"></vxe-table-column>
            </vxe-table>
          </na-card-light-table>
        </a-col>
      </a-row>
    </div>
    <div style="margin-top: 20px; padding-right: 5px; height: calc(100% - 430px)">
      <a-row :gutter="24" style="height: calc(100% - 0px)">
        <a-col :md="12" :sm="24" style="height: calc(100% - 0px)">
          <na-card-light title="故障系统分布">
            <div id="chart1" style="width: 100%; height: calc(100% - 0px)"></div>
          </na-card-light>
        </a-col>
        <a-col :md="12" :sm="24" style="height: calc(100% - 0px)">
          <na-card-light title="故障时间分布">
            <div id="chart2" style="width: 100%; height: calc(100% - 0px)"></div>
          </na-card-light>
        </a-col>
      </a-row>
    </div>
    <BreakdownDetailModal ref='breakdownDetail'></BreakdownDetailModal>
  </div>
</template>

<script>
import {
  getDayFaultTrend,
  getFaultRanking,
  getFaultSystemCount,
  getLatestFault,
  getLatestOutsourceFault,
} from '@api/tirosKanbanApi'
import BreakdownDetailModal from '@views/tiros/dispatch/breakdown/BreakdownDetailModal'
import * as echarts from 'echarts'
import NaCardLight from '@comp/tiros/Na-card-light'
import NaCardLightTable from '@comp/tiros/Na-card-light-table'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'QualityWorkshopPage',
  components: { NaCardLight, NaCardLightTable, BreakdownDetailModal, LineSelectList },
  data() {
    return {
      dictTrainStr: 'bu_train_info,train_no,train_no',
      detail: {},
      tableDataX: [],
      tableDataW: [],
      tableDataP: [],
      dateX: [],
      dateW: [],
      dateP: [],

      dataSourceB: [],
      dataSourceZ: [],
      queryParam: {
        depotId: '',
        lineId: '',
        trainNo: '',
        endDate: '',
        startDate: '',
        programType: '',
      },
      lineId: '',
      programTypeItems: [],
      allAlign: 'center',
      isList: false,
    }
  },
  created() {
    this.findList()
    // this.loadProject()
  },
  watch: {
    lineId(newVal, oldVal) {
      if (newVal) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + newVal
      } else {
        this.dictTrainStr = 'bu_train_info,train_no,train_no'
      }
    },
  },
  methods: {
    viewFault(row){
      this.$refs.breakdownDetail.show(row.id)
    },
    hiddenFilter() {
      this.$refs.filterBox.style.display = 'none'
    },
    onShowFilter(e) {
      if (this.$refs.filterBox.style.display === 'block') {
        this.$refs.filterBox.style.display = 'none'
      } else {
        this.$refs.filterBox.style.top = e.pageY + 'px'
        this.$refs.filterBox.style.left = e.pageX - 270 + 'px'
        this.$refs.filterBox.style.display = 'block'
      }
      e.stopPropagation()
    },
    onDayChange: function (value, dateString) {
      this.queryParam.startDate = dateString[0]
      this.queryParam.endDate = dateString[1]
    },
    findList() {
      if (this.programTypeItems.length > 0) {
        let containJIAXIU = this.programTypeItems.indexOf('1') > -1
        let containDAXIU = this.programTypeItems.indexOf('2') > -1

        if (containJIAXIU && containDAXIU) {
          this.queryParam.programType = 0
        } else {
          if (containJIAXIU && !containDAXIU) {
            this.queryParam.programType = 1
          } else if (!containJIAXIU && containDAXIU) {
            this.queryParam.programType = 2
          }
        }
      } else {
        this.queryParam.programType = 0
      }

      this.queryParam.lineId = this.lineId
      getDayFaultTrend(this.queryParam).then((res) => {
        this.dataSourceZ = res.result
        if (this.dataSourceZ && this.dataSourceZ.length > 0) {
          this.chart2(this.dataSourceZ)
        } else {
          this.chart2([])
        }
      })
      getFaultSystemCount(this.queryParam).then((res) => {
        this.dataSourceB = res.result
        if (this.dataSourceB && this.dataSourceB.length > 0) {
          this.chart1(this.dataSourceB)
        } else {
          this.chart1([])
        }
      })
      getLatestFault(this.queryParam).then((res) => {
        this.dateX = res.result
        console.log(res.result)
        this.tableDataX = res.result.latestFaults
      })
      getLatestOutsourceFault(this.queryParam).then((res) => {
        this.dateW = res.result
        console.log(res.result)
        this.tableDataW = res.result.latestFaults
      })
      getFaultRanking(this.queryParam).then((res) => {
        this.tableDataP = res.result
      })
    },
    chart1(data) {
      // 基于准备好的dom，初始化echarts实例
      let myChart = echarts.init(document.getElementById('chart1'))

      // 指定图表的配置项和数据
      let option = {
        toolbox: {
          show: false,
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
        },
        series: [
          {
            name: '故障数量',
            type: 'pie',
            radius: '75%',
            center: ['50%', '50%'],
            data: data.map((d) => {
              return { name: d.item, value: d.count }
            }),
            label: {
              formatter: '{b}: {c} ({d}%)',
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)',
              },
            },
          },
        ],
      }

      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option)
    },
    chart2(data) {
      // 基于准备好的dom，初始化echarts实例
      let myChart = echarts.init(document.getElementById('chart2'))

      // 指定图表的配置项和数据
      let option = {
        tooltip: {},
        toolbox: {
          show: false,
        },
        xAxis: {
          type: 'category',
          axisLabel: {
            interval: 0,
            rotate: -50,
            fontSize: 10,
            formatter: function (value, index) {
              /*if (value.length > 4) {
                  return value.substring(0, 4) + "...";
              } else {
                  return value ;
              }*/
              return value
            },
          },
          data: data.map((d) => {
            return d.x
          }),
        },
        yAxis: {
          type: 'value',
        },
        series: [
          {
            name: '故障数',
            type: 'bar',
            data: data.map((d) => {
              return d.y
            }),
          },
        ],
      }

      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option)
    },
  },
}
</script>

<style scoped>
.titleBar {
  width: 100%;
  font-size: 14px;
  padding: 8px;
  background: #eee;
  margin-top: -1px;
  border-top-left-radius: 5px;
  border-top-right-radius: 5px;
}

.bor {
  border: 1px #e2e2e2 solid !important;
  border-radius: 5px;
}

.ant-table td {
  white-space: nowrap;
}

.filterBox {
  top: 100px;
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