<template style="height: calc(100% - 5px)">
  <div style="height: calc(100% - 5px)" @click="hiddenFilter">
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
      <a-row>
        <a-col :md="24" :sm="8">
          <na-card-light title="年度架大修成本分布">
            <div id="chart3" style="width: 100%; height: 300px"></div>
          </na-card-light>
        </a-col>
      </a-row>
    </div>
    <div style="padding: 5px" v-if="tableData && tableData.length > 0">
      <vxe-table
        border
        highlight-current-row
        highlight-hover-row
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="ellipsis"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        :span-method="objectSpanMethod"
      >
        <vxe-table-column field="depotName" title="车辆段" width="120"></vxe-table-column>
        <vxe-table-column field="programName" title="修程" width="100"></vxe-table-column>
        <vxe-table-column field="totalCost" title="总成本" width="10%"></vxe-table-column>
        <vxe-table-column field="mustCost" title="必换件" width="10%"></vxe-table-column>
        <vxe-table-column field="randomCost" title="偶换件" width="10%"></vxe-table-column>
        <vxe-table-column field="consumeCost" title="耗材" width="10%"></vxe-table-column>
        <vxe-table-column field="selfRepairCost" title="自主修" width="12%"></vxe-table-column>
        <vxe-table-column field="outsourceRepairCost" title="委外修" width="12%"></vxe-table-column>
        <vxe-table-column field="singleTrainAverageCost" title="单列车平均总成本" width="15%"></vxe-table-column>
      </vxe-table>
    </div>
    <div style="margin-top: 20px; height: 400px">
      <a-row :gutter="24" style="height: calc(100% - 10px)">
        <a-col :md="12" :sm="24" style="height: calc(100% - 0px)">
          <na-card-light title="物资主要消耗">
            <div id="chart1" style="width: 100%; height: calc(100% - 0px)"></div>
          </na-card-light>
        </a-col>
        <a-col :md="12" :sm="24" style="height: calc(100% - 0px)">
          <na-card-light title="车辆架大修成本趋势">
            <div id="chart2" style="width: 100%; height: calc(100% - 0px)"></div>
          </na-card-light>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script>
import {
  getAreaChart,
  getDepotCost,
  getLastTenTrainCost,
  getLastTenTrainNo,
  getMaterialCostTopTen,
} from '@api/tirosKanbanApi'
import * as echarts from 'echarts'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'CostPage',
  components: { LineSelectList },
  data() {
    return {
      dictTrainStr: 'bu_train_info,train_no,train_no',
      tableData: [],
      dataSourceB: [],
      dataSourceZ: [],
      dataSourceM: [],
      typetrain: [],
      queryParam: {
        depotId: '',
        lineId: '',
        trainNo: '',
        programType: '',
      },
      allAlign: 'center',
      isList: false,
      programTypeItems: [],
      lineId: '',
    }
  },
  created() {
    this.findList()
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

    changelist() {
      this.isList = true
    },
    offList() {
      this.isList = false
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
      getLastTenTrainNo(this.queryParam).then((res) => {
        // console.log('last train:', res)
        this.typetrain = res.result
        getLastTenTrainCost(this.queryParam).then((res) => {
          if (this.typetrain && this.typetrain.length > 0) {
            this.dataSourceZ = res.result
          } else {
            this.dataSourceZ = []
          }

          if (this.dataSourceZ && this.dataSourceZ.length > 0) {
            this.chart2(this.typetrain, this.dataSourceZ)
          } else {
            this.chart2([], [])
          }
        })
      })

      getMaterialCostTopTen(this.queryParam).then((res) => {
        this.dataSourceB = res.result
        if (this.dataSourceB && this.dataSourceB.length > 0) {
          this.chart1(this.dataSourceB)
        } else {
          this.chart1([])
        }
      })

      getAreaChart(this.queryParam).then((res) => {
        this.dataSourceM = res.result
        if (this.dataSourceM && this.dataSourceM.length > 0) {
          this.chart3(this.dataSourceM)
        } else {
          this.chart3([])
        }
      })

      getDepotCost(this.queryParam).then((res) => {
        this.tableData = res.result
      })
    },
    changeLine(data) {
      if (data) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + data
      } else {
        this.dictTrainStr = 'bu_train_info,train_no,train_no'
      }
    },
    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0) {
        //用于设置要合并的列
        if (rowIndex % 3 === 0) {
          //用于设置合并开始的行号
          return {
            rowspan: 3, //合并的行数
            colspan: 1, //合并的列数，设为０则直接不显示
          }
        } else {
          return {
            rowspan: 0,
            colspan: 0,
          }
        }
      }
    },
    chart1(data) {
      if (!data || data.length < 1) {
        return
      }
      // 基于准备好的dom，初始化echarts实例
      let myChart = echarts.init(document.getElementById('chart1'))

      // 指定图表的配置项和数据
      let option = {
        /* title: {
          text: '物资主要消耗占比',
        },*/
        grid: {
          left: '0%',
          right: '0%',
          top: '0%',
          bottom: '0%',
        },
        toolbox: {
          show: false,
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
        },
        series: [
          {
            name: '成本消耗',
            type: 'pie',
            radius: '55%',
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
    chart2(dataMouth, datas) {
      if (!dataMouth || dataMouth.length < 1) {
        return
      }
      // 基于准备好的dom，初始化echarts实例
      let myChart = echarts.init(document.getElementById('chart2'))
      let _series = []
      let _series_names = []

      datas.forEach((d) => {
        let _name = d.type
        _series_names.push(_name)

        let data = []
        dataMouth.forEach((m) => {
          data.push(d[m])
        })
        _series.push({
          name: _name,
          type: 'bar',
          stack: 'sum',
          barWidth: 30,
          itemStyle: {
            normal: {
              label: {
                show: true, //是否展示
              },
            },
          },
          data: data,
        })
      })
      let option = {
        /* title: {
          text: '车辆架大修成本趋势'
        },*/
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
          },
        },
        legend: {
          x: 'center',
          y: 'top',
          data: _series_names,
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true,
        },
        yAxis: [{}],
        xAxis: [
          {
            type: 'category',
            data: dataMouth,
          },
        ],
        series: _series,
      }

      myChart.setOption(option)
    },
    chart3(data) {
      if (!data || data.length < 1) {
        return
      }
      let myChart = echarts.init(document.getElementById('chart3'))
      let option = {
        color: ['#80FFA5', '#00DDFF', '#37A2FF', '#FF0087', '#FFBF00'],
        /* title: {
          text: '年度架大修成本分布'
        },*/
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985',
            },
          },
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true,
        },
        xAxis: [
          {
            type: 'category',
            boundaryGap: false,
            data: data.map((d) => {
              return d.x
            }),
          },
        ],
        yAxis: [
          {
            type: 'value',
          },
        ],
        series: [
          {
            name: '消耗金额',
            type: 'line',
            stack: '消耗金额',
            smooth: true,
            lineStyle: {
              width: 0,
            },
            showSymbol: false,
            areaStyle: {
              opacity: 0.8,
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(128, 255, 165)',
                },
                {
                  offset: 1,
                  color: 'rgba(1, 191, 236)',
                },
              ]),
            },
            emphasis: {
              focus: 'series',
            },
            data: data.map((d) => {
              return d.y
            }),
          },
        ],
      }
      myChart.setOption(option)
    },
  },
}
</script>

<style scoped>
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