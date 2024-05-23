<template>
  <a-spin :spinning="spinning">
    <div class="chartWarpBox">
      <!--  -->
      <div id="chart_main" style="width: 100%; height: 450px"></div>
      <ul class="legendBox">
        <li v-for="(v, k) in legend" :key="k" @click="legendChange(v)">
          <div :style="{ backgroundColor: v.color }"></div>
          <span>{{ v.name }}</span>
        </li>
      </ul>
      <!--  -->
      <div style="padding: 0 3%">
        <div class="tableTitle">{{ selected.name }}各项成本明细</div>
        <vxe-table border ref="listTable" align="center" :data="tableData">
          <vxe-table-column field="contractNo" title="合同编号" />
          <vxe-table-column width="200px" field="contractName" title="合同名称" />
          <vxe-table-column field="itemName" title="项目名称" />
          <vxe-table-column
            field="contractAmount"
            align="right"
            header-align="center"
            title="合同总价（万）"
          />
          <vxe-table-column
            field="singlePrice"
            align="right"
            header-align="center"
            title="单列价格（万）"
          />
          <vxe-table-column
            field="sectionPrice"
            align="right"
            header-align="center"
            title="单节总价（万）"
          />
          <vxe-table-column
            field="partPrice"
            align="right"
            header-align="center"
            title="单个部件价格（万）"
          />
        </vxe-table>
      </div>
      <a-spin :spinning="spinning2">
        <div style="padding-top: 20px">
          <!--  -->
          <div class="tableTitle2">
            <a-space>
              <a-select
                showSearch
                :filterOption="filterOption"
                v-model="assetId"
                @change="assetChange"
                style="width: 200px"
              >
                <a-select-option :value="v.id" v-for="(v, k) in selectData" :key="k">
                  {{ v.assetName }}
                </a-select-option>
              </a-select>
              <!--  -->
              <a-radio-group
                v-model="priceType"
                @change="priceTypeChange"
                button-style="solid"
              >
                <a-radio-button :value="1"> 总价 </a-radio-button>
                <a-radio-button :value="2"> 平均单列 </a-radio-button>
                <a-radio-button :value="3"> 平均单节 </a-radio-button>
                <a-radio-button :value="4"> 平均单部件 </a-radio-button>
              </a-radio-group>
            </a-space>
            <a-radio-group
              v-model="chartType"
              @change="chartTypeChange"
              button-style="solid"
            >
              <a-radio-button value="bar"> 柱状图 </a-radio-button>
              <a-radio-button value="line"> 折线图 </a-radio-button>
            </a-radio-group>
          </div>
          <!--  -->
          <div id="chart_main2" style="width: 100%; height: 450px"></div>
        </div>
      </a-spin>
    </div>
  </a-spin>
</template>

<script>
import * as echarts from "echarts";
import {
  getOutsourceCost,
  getOutsourceDetail,
  getOutsourcePart,
  getOutsourceAsset,
} from "@/api/tirosOutsourceApi.js";
export default {
  data() {
    return {
      assetId: null,
      chart1: null,
      chart2: null,
      chartType: "bar",
      priceType: 1,
      spinning: true,
      spinning2: false,
      legend: [],
      selected: {},
      tableData: [],
      selectData: [],
    };
  },
  mounted() {
    const All = [this.allRequest(), this.allRequest2()];
    Promise.all(All).then((result) => {
      console.log(3333333);
      this.spinning = false;
    });
  },
  methods: {
    initChart1(data) {
      // console.log(data);
      var chartDom = document.getElementById("chart_main");
      this.chart1 = echarts.init(chartDom);
      var option;
      option = {
        title: {
          text: "委外成本（万元）",
          left: "center",
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        xAxis: [
          {
            type: "category",
            data: ["总成本", "单列成本", "单节成本"],
            axisTick: {
              alignWithLabel: true,
            },
          },
        ],
        yAxis: [
          {
            type: "value",
          },
        ],
        series: [
          // {
          //   name: "2号线架修",
          //   type: "bar",
          //   // barWidth: '60%',
          //   data: [30, 30, 57],
          // },
        ],
      };

      // 数据
      data.forEach((item, index, arr) => {
        option.series.push({
          name: item.lineName + item.repairProgramName,
          type: "bar",
          barMaxWidth: '120px',
          lineId: item.lineId,
          repairProgramId: item.repairProgramId,
          data: [],
        });
        item.costItemList.forEach((item2) => {
          option.series[index].data.push(item2.price);
        });
      });

      this.chart1.setOption(option);
      this.legend = [];
      const { color, series } = this.chart1.getOption();
      series.forEach((item, index, arr) => {
        this.legend.push({
          name: item.name,
          color: color[index],
          lineId: item.lineId,
          repairProgramId: item.repairProgramId,
        });
      });
      console.log(this.legend);
      this.selected = this.legend[0];
    },
    chartTypeChange(data) {
      console.log(this.option2);
      Array.from(this.option2.series, (item, index) => {
        item.type = data.target.value;
      });
      this.chart2.setOption(this.option2);
    },
    initChart2(data) {
      console.log(data);
      var chartDom = document.getElementById("chart_main2");
      this.chart2 = echarts.init(chartDom);
      this.option2 = {
        title: {
          text: "项目合同价格趋势（万元）",
          left: "center",
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
        },
        legend: {
          bottom: "bottom",
        },
        grid: {
          left: "3%",
          right: "3%",
          bottom: "10%",
          containLabel: true,
        },
        xAxis: [
          {
            type: "category",
            data: [
              // "辅助逆变器箱"
            ],
            axisTick: {
              alignWithLabel: true,
            },
          },
        ],
        yAxis: [
          {
            type: "value",
          },
        ],
        series: [
          {
            // name: "",
            type: "bar",
            barMaxWidth: '60px',
            
            // barWidth: '60%',
            data: [
              // 20
            ],
          },
        ],
      };

      data.forEach((item, index, arr) => {
        this.option2.xAxis[0].data.push(
          item.lineName + item.repairProgramName + "合同（" + item.year + "）"
        );
        // this.option2.series.push({
        //   name: item.partName,
        //   type: "bar",
        //   // barWidth: '60%',
        //   data: [],
        // });
        this.option2.series[0].data.push(item.price);
      });


      this.chart2.setOption(this.option2);
    },
    async allRequest() {
      await this.getOutsourceCost();
      await this.getOutsourceDetail();
    },
    async allRequest2() {
      await this.getOutsourceAsset();
      await this.getOutsourcePart();
    },
    async getOutsourceCost() {
      return getOutsourceCost().then((res) => {
        console.log(res, 1);
        if (res.success) {
          this.initChart1(res.result);
        }
      });
    },
    filterOption(input, option) {
      return (
        option.componentOptions.children[0].text
          .toLowerCase()
          .indexOf(input.toLowerCase()) >= 0
      );
    },
    legendChange(item) {
      if (Object.entries(this.selected).toString() != Object.entries(item).toString()) {
        this.selected = item;
        this.getOutsourceDetail();
      }
    },
    priceTypeChange() {
      this.spinning2 = true;
      this.getOutsourcePart();
    },
    assetChange() {
      this.spinning2 = true;
      this.getOutsourcePart();
    },
    async getOutsourceDetail() {
      console.log(this.selected);
      return getOutsourceDetail({
        lineId: this.selected.lineId,
        repairProgramId: this.selected.repairProgramId,
      }).then((res) => {
        console.log(res, 2);
        if (res.success) {
          this.tableData = res.result;
        }
      });
    },
    async getOutsourceAsset() {
      return getOutsourceAsset().then((res) => {
        console.log(res, 3);
        if (res.success && res.result.length) {
          this.selectData = res.result;
          this.assetId = res.result[0].id;
        }
      });
    },
    async getOutsourcePart() {
      return getOutsourcePart({ assetId: this.assetId, type: this.priceType }).then(
        (res) => {
          console.log(res, 4);
          this.spinning2 = false;
          if (res.success) {
            this.initChart2(res.result);
          }
        }
      );
    },
  },
};
</script>

<style lang="less" scoped>
.chartWarpBox {
  height: 100%;
  overflow: auto;
}
.legendBox {
  list-style: none;
  display: flex;
  flex-flow: row nowrap;
  justify-content: center;
  li {
    padding: 20px;
    display: flex;
    flex-flow: row nowrap;
    align-items: center;
    cursor: pointer;
    &:active {
      opacity: 0.8;
    }
    div {
      width: 15px;
      height: 15px;
      margin-right: 6px;
      border-radius: 2px;
    }
  }
}
.tableTitle {
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  padding-bottom: 20px;
}
.tableTitle2 {
  width: 100%;
  font-size: 18px;
  font-weight: bold;
  display: flex;
  flex-flow: row nowrap;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  padding: 20px 3%;
  // position: absolute;
  // z-index: 99;
  // top: 20px;
}
</style>
