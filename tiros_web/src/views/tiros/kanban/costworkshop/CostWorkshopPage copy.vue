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
              <line-select-list v-model="lineId"></line-select-list>
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
            <a-form-item label="时间">
              <a-range-picker
                format="YYYY-MM-DD"
                :placeholder="['开始时间', '结束时间']"
                @change="onDayChange"/>
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
    <div style="margin-top: 20px; padding-right: 5px;">
      <a-row :gutter="24">
        <a-col :md="6" :sm="24" v-for="item in dataSourceM" :key="item.name">
          <na-card-light :title="item.name">
            <a-row >
              <a-col :span="11" style="text-align: center;padding-top:10px;">
                <a-icon style="font-size: 60px" type="pay-circle"></a-icon>
                <div style="margin-top: 10px"><span>{{ item.cost }}</span>元</div>
              </a-col>
              <a-col :span="1" >
                <a-divider style="height: 110px" type="vertical"/>
              </a-col>
              <a-col :span="11" style="text-align: center;padding-top:10px;">
                <a-icon style="font-size: 60px;" type="gold"></a-icon>
                <div style="margin-top: 10px"><span>{{ item.quantity }}</span>个</div>
              </a-col>
            </a-row>
          </na-card-light>
        </a-col>
      </a-row>
    </div>
    <div style="margin-top: 20px; padding-right: 5px; height: calc(100% - 200px)">
      <a-row :gutter="24" style="height: calc(100% - 0px)">
        <a-col :md="12" :sm="24" style="height: calc(100% - 10px)">
          <na-card-light title="物料消耗占比">
            <div id="chart1" style="width:100%; height: calc(100% - 0px)"></div>
          </na-card-light>
        </a-col>
        <a-col :md="12" :sm="24"  style="height: calc(100% - 10px)">
          <na-card-light title="安全库存预警">
            <div style="height: calc(100% - 47px)">
              <vxe-table
                border="none"
                ref="listTable"
                :align="allAlign"
                :data="tableData"
                show-overflow="tooltip"
                :edit-config="{trigger: 'manual', mode: 'row'}"
                max-height="100%"
                style="width: 100%"
              >
                <vxe-table-column field="order" title="序号" width="60"></vxe-table-column>
                <vxe-table-column field="materialCode" title="物资编码" width="120"></vxe-table-column>
                <vxe-table-column field="materialName" title="物资名称"></vxe-table-column>
                <vxe-table-column field="alertStock" title="安全库存" width="80"></vxe-table-column>
                <vxe-table-column field="currentStock" title="当前库存" width="80"></vxe-table-column>
                <vxe-table-column field="diffStock" title="偏差" width="80"></vxe-table-column>
              </vxe-table>
            </div>
          </na-card-light>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script>
import { getCostItem, getCostProportion, getThresholdAlert } from '@api/tirosKanbanApi'
import * as echarts from 'echarts'
import NaCardLight from '@comp/tiros/Na-card-light'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
export default {
  name: 'CostWorkshopPage',
  components: { NaCardLight,LineSelectList },
  data() {
    return {
      dictTrainStr: 'bu_train_info,train_no,train_no',
      tableData: [],
      dataSourceB: [],
      dataSourceM: [],
      queryParam: {
        depotId: '',
        lineId: '',
        trainNo: '',
        endDate: '',
        startDate: '',
        programType: ''
      },
      allAlign: 'center',
      isList: false,
      lineId: '',
      programTypeItems: []
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
    onDayChange: function(value, dateString) {
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
      getCostItem(this.queryParam).then((res) => {
        this.dataSourceM = res.result
      })
      getCostProportion(this.queryParam).then((res) => {
        this.dataSourceB = res.result
        if (this.dataSourceB && this.dataSourceB.length > 0) {
          this.chart1(this.dataSourceB)
        } else {
          this.chart1([])
        }
      })
      getThresholdAlert(this.queryParam).then((res) => {
        this.tableData = res.result
      })
    },
    chart1(data) {
      // 基于准备好的dom，初始化echarts实例
      let myChart = echarts.init(document.getElementById('chart1'));

      // 指定图表的配置项和数据
      let option = {
        toolbox: {
            show: false,
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        series: [{
          name: '成本',
          type: 'pie',
          radius: '55%',
          center: ['50%', '50%'],
          data: data.map(d=>{
            return {name: d.item, value: d.count}
          }),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      };

      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option);
    }
  }
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
  border-radius: 5px
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