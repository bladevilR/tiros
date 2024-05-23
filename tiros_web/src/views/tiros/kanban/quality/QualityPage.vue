<template style="height: calc(100% - 5px)">
  <div style="height: calc(100% - 5px); overflow: hidden;" @click="hiddenFilter">
    <div :style="{  position: 'absolute', top: 1, right: 0, zIndex: 999, cursor: 'pointer' }" @click="onShowFilter">
      <img src="~@/assets/tiros/images/magic.svg"/>
    </div>
    <!-- 查询区域 -->
    <div ref="filterBox" class="filterBox" @click.stop>
      <a-form layout="horizontal" :label-col="{ span: 6 }" :wrapperCol="{ span: 18 }">
        <a-row :gutter="16">
          <a-col :md="24" :sm="24">
            <a-form-item label="车辆段">
              <j-dict-select-tag
                v-model="queryParam.depotId"
                placeholder="请选择"
                dictCode="bu_mtr_depot,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="线路">
<!--              <j-dict-select-tag
                v-model="lineId"
                dictCode="bu_mtr_line,line_name,line_id"
              />-->
              <line-select-list v-model="queryParam.lineId" ></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="车辆">
              <j-dict-select-seach-tag
                v-model="queryParam.trainNo"
                :dictCode="dictTrainStr"
              />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item>
              <a-checkbox-group
                v-model="programTypeItems"
                style="width: 100%;margin-top: 6px"
              >
                <a-row>
                  <a-col :span="9">
                    <a-checkbox value="1">
                      架修
                    </a-checkbox>
                  </a-col>
                  <a-col :span="9">
                    <a-checkbox value="2">
                      大修
                    </a-checkbox>
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
    <vxe-table
      border
      ref="listTable"
      :align="allAlign"
      :data="tableData"
      show-overflow="ellipsis"
      :edit-config="{trigger: 'manual', mode: 'row'}"
      :span-method="objectSpanMethod"
    >
      <vxe-table-column field="depotName" title="车辆段" width="15%"></vxe-table-column>
      <vxe-table-column field="programName" title="修程" width="15%"></vxe-table-column>
      <vxe-table-column field="sum1" title="架大修故障" width="10%"></vxe-table-column>
      <vxe-table-column field="sum3" title="质保期故障" width="10%"></vxe-table-column>
      <vxe-table-column field="sum4" title="质保期正线故障" width="12%"></vxe-table-column>
      <vxe-table-column field="sum5" title="质保期A/B故障" width="12%"></vxe-table-column>
      <vxe-table-column field="sum6" title="质保期有责故障" width="12%"></vxe-table-column>
      <vxe-table-column field="sum7" title="出质保期故障" width="12%"></vxe-table-column>
    </vxe-table>
    <div style="margin-top: 40px;height: calc(100% - 170px)">
      <a-row :gutter="24" style="height: calc(100% - 10px)">
        <a-col :md="12" :sm="24" style="height: calc(100% - 30px)">
          <na-card-light title="质保期系统故障分布">
            <div id="chart1" style="width:100%; height: calc(100% - 30px)"></div>
          </na-card-light>
        </a-col>
        <a-col :md="12" :sm="24" style="height: calc(100% - 30px)">
          <na-card-light title="质保期故障趋势">
          <div id="chart2" style="width:100%; height: calc(100% - 30px)"></div>
          </na-card-light>
        </a-col>
      </a-row>
    </div>
  </div>

</template>

<script>
import { getDepotFault, getFaultTrend, getSysFault } from '@api/tirosKanbanApi'
import * as echarts from 'echarts'
import NaCardLight from '@comp/tiros/Na-card-light'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'QualityPage',
  components: { NaCardLight ,LineSelectList},
  data() {
    return {
      dictTrainStr: 'bu_train_info,train_no,train_no',
      tableData: [],
      dataSourceB: [],
      dataSourceL: [],
      queryParam: {
        depotId: '',
        lineId: '',
        trainNo: '',
        programType: ''
      },
      date: null,
      allAlign: 'center',
      count: '',
      programTypeItems: [],
      lineId: ''
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
    }
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
      getSysFault(this.queryParam).then((res) => {
        this.dataSourceB = res.result
        if (this.dataSourceB && this.dataSourceB.length > 0) {
          this.chart1(this.dataSourceB)
        } else {
          this.chart1([])
        }
      })
      getFaultTrend(this.queryParam).then((res) => {
        this.dataSourceL = res.result
        if (this.dataSourceL && this.dataSourceL.length > 0) {
          this.chart2(this.dataSourceL)
        } else {
          this.chart2([])
        }
      })
      getDepotFault(this.queryParam).then((res) => {
        this.tableData = res.result
      })
    },

    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0) {    //用于设置要合并的列
        if (rowIndex % 3 === 0) {   //用于设置合并开始的行号
          return {
            rowspan: 3,　　　　　//合并的行数
            colspan: 1          //合并的列数，设为０则直接不显示
          }
        } else {
          return {
            rowspan: 0,
            colspan: 0
          }
        }
      }
    },
    // 质保期系统故障分布
    chart1(data) {
      // 基于准备好的dom，初始化echarts实例
      let myChart = echarts.init(document.getElementById('chart1'));
      // 指定图表的配置项和数据
      let option = {
        /*title: {
          text: '质保期系统故障分布',
        },*/
        tooltip: {},
         toolbox: {
             show: false
         },
        xAxis: {
          type: 'category',
          axisLabel: {
            interval: 0,
            rotate: -50,
            fontSize:10,
            formatter: function (value, index) {
              if (value.length > 4) {
                return value.substring(0, 4) + "...";
              } else {
                return value ;
              }
            }
          },
          data: data.map(d=>{
            return d.x})
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          name: '故障数',
          type: 'bar',
          data: data.map(d=>{return d.y})
        }]
      };

      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option);
    },
    chart2(data) {
      // 基于准备好的dom，初始化echarts实例
      let myChart = echarts.init(document.getElementById('chart2'));

      // 指定图表的配置项和数据
      let option = {
       /* title: {
          text: '质保期故障趋势',
        },*/
        legend: {
          data: ['平均故障数', '实际故障数']
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
            name: '平均故障数',
            data: data.map(d=>{return d.jeecg}),
            type: 'line'
          },
          {
            symbol:'none', //这句就是去掉点的
            smooth:true, //这句就是让曲线变平滑的
            name: '实际故障数',
            data: data.map(d=>{return d.jeebt}),
            type: 'line'
          },
        ]
      };


      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option);
    }
  }
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