<template style="height: calc(100% - 5px)">
  <div style="padding: 8px">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="loadData">
        <a-row :gutter="24" type="flex">
          <!--   系统不需要，点击下面表格进行过滤       <a-col :md="4">
                      <a-form-item label="系统:">
                        <line-select-list></line-select-list>
                      </a-form-item>
                    </a-col>-->
          <a-col :md="4">
            <a-form-item label="线路:">
              <line-select-list v-model="queryParam.lineId" @change="onLineChange"
                                :default-first="true"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="车辆:">
              <j-dict-select-seach-tag 
                v-model="queryParam.trainNo" 
                :dictCode="dictTrainStr" 
                placeholder="车辆" 
                @focus="handleSysFocus" 
                :default-first="true"
              />
            </a-form-item>
          </a-col>
          <a-col flex="180px">
            <a-form-item label="统计周期:">
              <a-select
                v-model="queryParam.periodType"
              >
                <!--                <a-select-option :value="undefined">请选择</a-select-option>-->
                <a-select-option :key="1" :value="1">年</a-select-option>
                <a-select-option :key="2" :value="2">月</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-space>
              <a-button @click="loadData">查询</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div v-if="this.resData.length>0" style="margin-top: 10px; padding-right: 5px; width: 100%">
      <a-row>
        <a-col :md="24" :sm="8">
          <na-card-light :title="'架大修实效分析-'+curDataTitle">
            <div id="chart1" style="width: 100%; height: 350px"></div>
          </na-card-light>
        </a-col>
      </a-row>
    </div>
    <div v-if="this.resData.length>0" style="margin-top: 10px; padding-right: 5px; width: 100%">
      <vxe-table
        border
        :max-height="'90%'"
        :style="'height: calc(100vh - 350px)'"
        ref="listTable"
        align="center"
        :data="tableData"
        show-overflow="tooltip"
        :radio-config="{trigger: 'row', highlight: true}"
        @radio-change="radioChangeEvent"
      >
        <vxe-table-column field="sysName" title="系统" width="100"></vxe-table-column>
        <vxe-table-column :title="item.title.toString()" :field="item.title.toString()" v-for="item in resData[0].dataList" :key="item.title">
        </vxe-table-column>
      </vxe-table>
    </div>
  </div>
</template>

<script>
import { getEffectivenessData } from '@api/tirosProductionApi'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import * as echarts from 'echarts'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'EffectivenessPage',
  components: { LineSelectList },
  data () {
    return {
      activeIndex: 0,
      dictTrainStr: '',
      myChart: undefined,
      resData: [
        {
          sysName: '故障总数',
          dataList: [
            {
              title: '-5',
              value: 0
            },
            {
              title: '-4',
              value: 0
            },
            {
              title: '-3',
              value: 0
            },
            {
              title: '-2',
              value: 0
            },
            {
              title: '-1',
              value: 0
            },
            {
              title: '架修点',
              value: 0
            },
            {
              title: '1',
              value: 0
            },
            {
              title: '2',
              value: 0
            },
            {
              title: '3',
              value: 0
            },
            {
              title: '4',
              value: 0
            },
            {
              title: '5',
              value: 0
            }
          ]
        }
      ],
      tableData: [],
      curDataTitle: '故障总数',
      queryParam: {
        lineId: null,
        trainNo: undefined,
        depotId: undefined,
        periodType: 2
      }
    }
  },
  mounted () {
    this.resData = []
    this.tableData = []
  },
  methods: {
    handleSysFocus() {
      if (!this.queryParam.lineId) this.$message.warn('请先选择线路!')
    },
    onLineChange (val, option) {
      this.dictTrainStr = 'bu_train_info,train_no,train_no,status=1 and line_id=\'' + val + '\''
    },
    initTableData () {
      this.resData.forEach((element) => {
        const item = {}
        item['sysName'] = element.sysName
        element.dataList.forEach((itemElement) => {
          item[itemElement.title] = itemElement.value
        })
        this.tableData.push(item)
      })
      setTimeout(() => {
        this.$refs.listTable.setRadioRow(this.tableData[0])
        this.chart()
      }, 200)
    },
    check () {
    },
    loadData () {
      if (everythingIsEmpty(this.queryParam.lineId)) {
        this.$message.warn('请先选择线路!')
        return
      }
      if (everythingIsEmpty(this.queryParam.trainNo)) {
        this.$message.warn('请先选择车辆!')
        return
      }
      getEffectivenessData(this.queryParam).then(res => {
        if (res.success && res.result) {
          this.resData = []
          this.tableData = []
          res.result.forEach(row => {
            let item = {
              sysName: row.name,
              dataList: []
            }
            // 架修前
            let len = row.before.length
            row.before.forEach((d, index) => {
              let title = '-' + (len - index)
              item.dataList.push({
                title: title,
                value: d
              })
            })
            // 架修点
            item.dataList.push({
              title: '架修点', //row.jdxPeriodName,
              value: row.jdx,
              date: row.jdxPeriodName,
            })
            // 架修后
            row.after.forEach((d, index) => {
              let title = (1 + index)
              item.dataList.push({
                title: title,
                value: d
              })
            })

            this.resData.push(item)
          })
          this.initTableData()
        } else {
          this.resData = []
          this.tableData = []
          this.$message.warn(res.message)
        }
      })
    },
    radioChangeEvent ({ rowIndex }) {
      this.activeIndex = rowIndex
      this.chart()
    },
    chart () {
      // 修改 this.activeIndex 可切换折线图的数据
      // 初始化折线图 series 数据
      const seriesData = []
      console.log(this.resData[this.activeIndex])
      const repairPointIndex = this.resData[this.activeIndex].dataList.findIndex((e) => e.title.toString().indexOf('架修点') > -1 )
      const date = this.resData[this.activeIndex].dataList[repairPointIndex].date;
      this.curDataTitle = this.resData[this.activeIndex].sysName
      // 架修点前
      seriesData.push({
        // name: this.resData[this.activeIndex].sysName,
        name: '架大修前',
        type: 'line',
        stack: this.resData[this.activeIndex].sysName,
        lineStyle: {
          color: '#B11F1F',
          width: 3
        },
        emphasis: {
          focus: 'series'
        },
        symbolSize: 10,
        color: '#B11F1F',
        symbol: 'circle',
        data: [...this.resData[this.activeIndex].dataList.slice(0, repairPointIndex), ...new Array(this.resData[this.activeIndex].dataList.length - repairPointIndex)]
      })
      console.log(repairPointIndex)
      // 架修点
      seriesData.push({
        name: '架修点',
        type: 'line',
        stack: '架修点',
        symbolSize: 10,
        emphasis: {
          focus: 'series'
        },
        color: '#B11F1F',
        symbol: 'circle',
        data: [...new Array(repairPointIndex), this.resData[this.activeIndex].dataList[repairPointIndex], ...new Array(repairPointIndex)]
      })
      // 架修点后
      seriesData.push({
        // name: this.resData[this.activeIndex].sysName,
        name: '架大修后 ',
        type: 'line',
        stack: this.resData[this.activeIndex].sysName,
        lineStyle: {
          color: '#B11F1F',
          width: 3
        },
        emphasis: {
          focus: 'series'
        },
        symbolSize: 10,
        color: '#B11F1F',
        symbol: 'circle',
        data: [...new Array(repairPointIndex + 1), ...this.resData[this.activeIndex].dataList.slice(repairPointIndex + 1, this.resData[this.activeIndex].dataList.length)]
      })
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          },
          formatter: (params) => {
            // console.log('params:', params)

            let period = this.queryParam.periodType == 2 ? '个月' : '年'

            if (params[0].value != undefined) {
              let name = Math.abs(parseInt(params[0].name)) + ''
              name = '第' + name + '' + period
              return params[0].seriesName + '  ' + name + '  故障数量：' + params[0].value
            }
            if (params[1].value != undefined) {
              return params[1].seriesName + '  故障数量：' + params[1].value
            }
            if (params[2].value != undefined) {
              let name = Math.abs(parseInt(params[2].name)) + ''
              name = '第' + name + '' + period
              return params[2].seriesName + '  ' + name + '  故障数量：' + params[2].value
            }
          }
        },
        xAxis: [
          {
            type: 'category',
            boundaryGap: false,
            data: this.resData[this.activeIndex].dataList.map((d) => {
              if(d.title.toString().indexOf('架修点') > -1){
                console.log(d.title)
                return d.title + ' ' + date;
              }
              return d.title
            }),
            splitLine: {
              show: true,
              interval: (index, value) => index === repairPointIndex,
              lineStyle: {
                type: 'dashed',
                width: 2,
                color: '#43B69D'
              }
            }
          }
        ],
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '5%',
          containLabel: true
        },
        yAxis: [
          {
            type: 'value'
          }
        ],
        series: seriesData
      }
      if (!this.myChart) {
        this.myChart = echarts.init(document.getElementById('chart1'))
      } else {
        // this.myChart.clear();
      }
      this.myChart.setOption(option)
    }
  }
}
</script>