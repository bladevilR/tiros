<template style="height: calc(100% - 5px)">
  <div style="height: calc(100% - 5px); overflow: hidden" @click="hiddenFilter">
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
                v-model="queryParam.lineId"
                placeholder="请选择"
                dictCode="bu_mtr_line,line_name,line_id"
              />-->
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="8">
            <a-button type="primary" @click="findList" block>切换条件</a-button>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <vxe-table
      border
      ref="listTable"
      :align="allAlign"
      :data="tableData"
      show-overflow="ellipsis"
      :edit-config="{ trigger: 'manual', mode: 'row' }"
      :span-method="objectSpanMethod"
    >
      <vxe-table-column field="depotName" title="车辆段" width="15%"></vxe-table-column>
      <vxe-table-column field="programName" title="修程" width="15%"></vxe-table-column>
      <vxe-table-column field="amount" title="年计划" width="12%"></vxe-table-column>
      <vxe-table-column field="finish" title="已完成" width="12%"></vxe-table-column>
      <vxe-table-column field="finishProgress" title="完成比例" width="15%"></vxe-table-column>
      <vxe-table-column field="repairing" title="正在检修" width="15%"></vxe-table-column>
      <vxe-table-column field="overallProgress" title="整体进度" width="15%"></vxe-table-column>
    </vxe-table>
    <div style="margin-top: 40px; height: calc(100% - 170px)">
      <a-row :gutter="24" style="height: calc(100% - 10px)">
        <a-col :md="12" :sm="24" style="height: calc(100% - 30px)">
          <na-card-light title="架大修完成质量比例">
            <div id="chart1" style="width: 100%; height: calc(100% - 30px)"></div>
          </na-card-light>
        </a-col>
        <a-col :md="12" :sm="24" style="height: calc(100% - 30px)">
          <na-card-light title="架大修年计划进度">
            <div id="chart2" style="width: 100%; height: calc(100% - 30px)"></div>
          </na-card-light>
        </a-col>
      </a-row>
    </div>
  </div>
</template>


<script>
import { getBurnDownChartData, getFinishQuality, getListProgress } from '../../../../api/tirosKanbanApi'
import * as echarts from 'echarts'
import NaCardLight from '@comp/tiros/Na-card-light'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
export default {
  name: 'ProgressPage',
  components: { NaCardLight, LineSelectList },
  data() {
    return {
      detail: {},
      tableData: [],
      dataSourceB: [],
      dataSourceL: [],
      queryParam: {
        depotId: '',
        lineId: '',
      },
      date: null,
      allAlign: 'center',
      count: '',
      isList: false,
    }
  },
  created() {
    this.findList()
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

    findList() {
      getFinishQuality(this.queryParam).then((res) => {
        this.dataSourceB = res.result
        if (this.dataSourceB && this.dataSourceB.length > 0) {
          this.chart1(this.dataSourceB)
        } else {
          this.chart1([])
        }
      })
      getBurnDownChartData(this.queryParam).then((res) => {
        this.dataSourceL = res.result
        if (this.dataSourceL && this.dataSourceL.length > 0) {
          this.chart2(this.dataSourceL)
        } else {
          this.chart2([])
        }
      })
      getListProgress(this.queryParam).then((res) => {
        this.tableData = res.result
      })
    },
    changelist() {
      this.isList = true
    },
    offList() {
      this.isList = false
    },

    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0) {
        //用于设置要合并的列
        if (rowIndex % 2 === 0) {
          //用于设置合并开始的行号
          return {
            rowspan: 2, //合并的行数
            colspan: 1, //合并的列数，设为０则直接不显示
          }
        } else {
          return {
            rowspan: 0,
            colspan: 0,
          }
        }
      }
      if (columnIndex === 6) {
        //用于设置要合并的列
        if (rowIndex % 2 === 0) {
          //用于设置合并开始的行号
          return {
            rowspan: 2, //合并的行数
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
      // 基于准备好的dom，初始化echarts实例
      let myChart = echarts.init(document.getElementById('chart1'))

      // 指定图表的配置项和数据
      let option = {
        /*  title: {
          text: '架大修完成质量比例',
        },*/
        grid: {
          left: '0%',
          right: '0%',
          top: '0%',
          bottom: '0%',
        },
        toolbox: {
          show: false,
          feature: {
            dataView: { readOnly: false },
            restore: {},
            saveAsImage: {},
          },
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
        },
        series: [
          {
            name: '架大修',
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
    chart2(data) {
      // 基于准备好的dom，初始化echarts实例
      let myChart = echarts.init(document.getElementById('chart2'))

      // 指定图表的配置项和数据
      let option = {
        /*   title: {
          text: '架大修年计划燃尽图',
        },*/
        legend: {
          data: ['计划剩余架修', '实际剩余架修'],
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985',
            },
          },
        },
        xAxis: {
          type: 'category',
          data: data.map((d) => {
            return d.type
          }),
        },
        yAxis: {
          type: 'value',
        },
        series: [
          {
            name: '计划剩余架修',
            data: data.map((d) => {
              return d.jeecg
            }),
            type: 'line',
          },
          {
            name: '实际剩余架修',
            data: data.map((d) => {
              return d.jeebt
            }),
            type: 'line',
          },
        ],
      }

      myChart.setOption(option)
    },
  },
}
</script>

<style scoped lang="less">
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