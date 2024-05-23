<template>
  <div class="bodyWrapper" style="padding: 8px">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="loadData">
        <a-row :gutter="24">
          <a-col :md="12" :xl="8">
            <!-- <a-form-item label="日期">
              <a-date-picker
                mode="year"
                format="YYYY"
                v-model="yearValue"
                :defaultValue="this.$moment(new Date())"
                style="width: 100%"
                :allowClear="false"
                :open="yearOpen"
                @panelChange="changeYear"
                @focus="yearOpen = true"
              />

            </a-form-item> -->
            <!-- <NaSelectDate
              ref="naDate1"
              :hiddenArray="[4]"
              :defaultType="1"
              @change="dateChange1"
            >
            </NaSelectDate> -->
<!--            <a-form-item label="日期">-->
<!--              <a-range-picker-->
<!--                :placeholder="['开始月份', '结束月份']"-->
<!--                format="YYYY-MM"-->
<!--                :value="monthVal"-->
<!--                @panelChange="handlePanelChange2"-->
<!--                :mode="mode"-->
<!--              />-->
<!--            </a-form-item>-->
            <a-form-item label="日期">
              <!-- <a-date-picker v-model="yearValue" @change="onDateChange" /> -->
              <a-range-picker v-model="dateTime" @change="onDateChange"  :defaultPickerValue="defaultDateRange" />
            </a-form-item>
          </a-col>
          <!--          <a-col :md="4" :sm="24">
            <a-form-item label="车辆">
              <j-dict-select-seach-tag
                v-model="queryParam.trainNo"
                :dictCode="dictTrainStr"
              />
            </a-form-item>
          </a-col>-->
          <a-col :md="4" :sm="24">
            <a-space>
              <a-button @click="init_sheet">查询</a-button>
              <a-button @click="downloadExcel">导出</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="table-page-body">
      <div id="luckysheet" style="height: calc(100% - 0px); width: 100%"></div>
    </div>
  </div>
</template>

<script>
import NaSelectDate from '@comp/tiros/Na-select-date'
import { getReportTemplate, getTrainFaultSum, getSystemFaultSum } from '@api/tirosReportApi'
import luckyexcelUtil from '../util/LuckyexcelUtil'
import { exportExcel } from '../util/export'
import * as echarts from 'echarts'

export default {
  name: 'fault_sum',
  components:{ NaSelectDate },
  data() {
    return {
      maxRow: 5,
      trainFaults: [],
      systeFaults: [],
      // mode: ['month', 'month'],
      // monthVal: [this.$moment(new Date()).format('YYYY-MM'),this.$moment(new Date()).format('YYYY-MM')],
      dateTime: [this.$moment(new Date()).startOf("month").format("YYYY-MM-DD"), this.$moment(new Date()).endOf('month').format("YYYY-MM-DD")],
      yearOpen: false,
      yearValue: this.$moment(new Date()),
      queryParam: {
        // year: this.$moment(new Date()).format('YYYY'),
        // startMonth: this.$moment(new Date()).format('YYYY-MM'),
        // endMonth: this.$moment(new Date()).format('YYYY-MM'),
        // depotId: this.$store.getters.userInfo.depotId,
        // lineId: this.$store.getters.userInfo.lineId,
        // workshopId: this.$store.getters.userInfo.workshopId,
        startDate:this.$moment(new Date()).startOf("month").format("YYYY-MM-DD"),
        endDate: this.$moment(new Date()).endOf('month').format("YYYY-MM-DD"),
      },
      sheetOption: {},
      imageCount: 1,
    }
  },
  mounted() {
    this.init_sheet()
  },
  beforeDestroy() {
    luckysheet.destroy()
  },
  methods: {
    // handlePanelChange2(value, mode) {
    //   console.log(value)
    //   this.monthVal = value;
    // },
    onDateChange() {
      if (this.dateTime[0]) {
        this.queryParam.startDate = this.dateTime[0].format('YYYY-MM-DD')
      }else {
        this.queryParam.startDate = ''
      }
      if (this.dateTime[1]) {
        this.queryParam.endDate = this.dateTime[1].format('YYYY-MM-DD')
      }else {
        this.queryParam.endDate = ''
      }
    },
    // dateChange1(value) {
    //   console.log(value)
    //   this.monthVal = value;
    //   return;
    //   if (value.dateType) {
    //     this.queryParam.dateType = value.dateType
    //     this.queryParam.year = value.year
    //     this.queryParam.quarter = value.quarter
    //     this.queryParam.month = value.month
    //     this.queryParam.startDate = value.dateRef[0]
    //     this.queryParam.endDate = value.dateRef[1]
    //   } else {
    //     this.queryParam.dateType = null
    //     this.queryParam.year = null
    //     this.queryParam.quarter = null
    //     this.queryParam.month = null
    //     this.queryParam.startDate = null
    //     this.queryParam.endDate = null
    //   }
    // },
    // changeYear(value, mode) {
    //   this.yearValue = value
    //   this.queryParam.year = value.format('YYYY')
    //   this.yearOpen = false
    // },
    check() {
      // if (!this.queryParam.year) {
      //   this.$message.warn('请选择年份')
      //   return false
      // }
      // if (!this.queryParam.workshopId) {
      //   this.$message.warn('当前人员没有车间')
      //   return false
      // }
      return true
    },
    init_sheet() {
      // if (!this.check()) {
      //   return
      // }
      getReportTemplate('fault_sum').then((res) => {
        if (res.success) {
          if (res.result && !!res.result.tempContent) {
            const sheet = JSON.parse(res.result.tempContent)
            const op = Object.assign({}, luckyexcelUtil.getDefaultOptions())
            op.hook['workbookCreateAfter'] = () => {
              this.init_train_fault_sum(this.trainFaults)
              this.init_system_fault_sum(this.systeFaults)
            }
            this.imageCount = 1
            sheet.images = {}
            sheet.data = []
            op.allowEdit = true
            op.row=150
            op.data = [sheet]

            this.loadData().then(() => {
              // op.data[0].row = this.maxRow + 176
              this.sheetOption = op
              this.init_charts()
              luckysheet.destroy()
              luckysheet.create(this.sheetOption)
            })
          } else {
            this.$message.error('报表模板内容为空，请定义模板')
          }
        } else {
          this.$message.error('报表模板查询失败：', res.message)
          this.dailyInfo = null
        }
      })
    },
    async loadData() {
      this.maxRow = 0
      return getTrainFaultSum({
        ...this.queryParam,
        // startMonth: this.$moment(this.monthVal[0]).format('YYYY-MM'),
        // endMonth: this.$moment(this.monthVal[1]).format('YYYY-MM'),
      })
        .then((res) => {
          if (res.success) {
            this.trainFaults = res.result
            if (this.maxRow < this.trainFaults.length) {
              this.maxRow = this.trainFaults.length
            }
          } else {
            this.$message.error('查询车辆故障统计错误：', res.message)
            this.dailyInfo = null
          }
          return
        })
        .then(() => {
          return getSystemFaultSum({
            ...this.queryParam,
            // startMonth: this.$moment(this.monthVal[0]).format('YYYY-MM'),
            // endMonth: this.$moment(this.monthVal[1]).format('YYYY-MM')
          }).then((res) => {
            if (res.success) {
              this.systeFaults = res.result
              if (this.maxRow < this.systeFaults.length) {
                this.maxRow = this.systeFaults.length
              }
            } else {
              this.$message.error('查询车辆故障统计错误：', res.message)
              this.dailyInfo = null
            }
          })
        })
    },

    init_train_fault_sum(faults) {
      // console.log('车辆故障统计：', faults)
      // 设置故障总数
      let rowIndex = 0
      if (faults && faults.length > 0) {
        let total = faults[0]
        // 架修期
        luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('B'), parseInt(total['repair']))
        // 质保期
        luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('C'), parseInt(total['warranty']))
        // 质保期正线
        luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('D'), parseInt(total['warrantyOnline']))
        // 质保期B类以上
        luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('E'), parseInt(total['warrantyAboveB']))
        // 质保期有责故障
        luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('F'), parseInt(total['warrantyHasDuty']))
        // 出保期故障
        luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('G'), parseInt(total['expireWarranty']))
      }
      rowIndex = 2
      if (faults && faults.length > 1) {
        for (let i = 1; i < faults.length; i++) {
          /*if (i > 1) {
            // 超出一个车辆，要插入一行(在下面）
            luckysheet.insertRow(rowIndex)
            rowIndex = rowIndex + 1
          }*/

          let train = faults[i]
          // 车号
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('A'), {
            v: train['trainNo'],
            ht: 0,
            vt: 0,
            ff: '宋体',
            fs: 11,
          })
          // 架修期
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('B'), train['repair'])
          // 质保期
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('C'), train['warranty'])
          // 质保期正线
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('D'), train['warrantyOnline'])
          // 质保期B类以上
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('E'), train['warrantyAboveB'])
          // 质保期有责故障
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('F'), train['warrantyHasDuty'])
          // 出保期故障
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('G'), train['expireWarranty'])

          rowIndex++
        }
        luckyexcelUtil.setBorder(0, 'A', rowIndex - 1, 'G')
      }

      // if (this.maxRow < rowIndex) {
      //   this.maxRow = rowIndex
      // }
    },
    init_system_fault_sum(faults) {
      // console.log('系统故障统计:', faults)
      let rowIndex = 2
      if (faults && faults.length > 0) {
        for (let i = 0; i < faults.length; i++) {
          let sys = faults[i]
          // 系统
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('I'), {
            v: sys['sys'],
            ht: 0,
            vt: 0,
            ff: '宋体',
            fs: 11,
          })
          // 架修期
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('J'), sys['repair'])
          // 质保期
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('K'), sys['warranty'])

          rowIndex++
        }

        luckyexcelUtil.setBorder(1, 'I', rowIndex - 1, 'K')

        // if (this.maxRow < rowIndex) {
        //   this.maxRow = rowIndex
        // }
        this.systeFaults = faults
        // 插入图表
        // console.log("images:",luckysheet.getLuckysheetfile()[0].images)
        // luckysheet.insertImage(imgUrl,{rowIndex: 15, colIndex: 1})
        // 基于准备好的dom，初始化echarts实例

        luckysheet.setRangeShow('A1', { show: false })
      }
    },
    init_charts() {
      // 绘制图表--各系统故障分布
      let dataInfo = {
        title: '各系统故障分布',
        dataCategory: ['架修期', '质保期'],
        dataNames: [],
        dataSeries: [
          {
            name: '架修期',
            type: 'bar',
            data: [],
          },
          {
            name: '质保期',
            type: 'bar',
            data: [],
          },
        ],
      }
      this.systeFaults.forEach((fault) => {
        dataInfo.dataNames.push(fault.sys)
        dataInfo.dataSeries[0].data.push(fault.repair)
        dataInfo.dataSeries[1].data.push(fault.warranty)
      })
      this.addHBar(this.maxRow, luckyexcelUtil.char2Index('A'), dataInfo)

      // 绘制图表--各车故障分布
      let dataInfo2 = {
        title: '各车辆故障分布',
        dataCategory: [],
        dataNames: [],
        dataSeries: [],
      }
      let dataFields = [
        { field: 'repair', name: '架修期' },
        { field: 'warranty', name: '质保期' },
        { field: 'warrantyOnline', name: '质保期正线' },
        { field: 'warrantyAboveB', name: '质保期B类及以上' },
        { field: 'warrantyHasDuty', name: '质保期有责故障' },
        { field: 'expireWarranty', name: '出质保故障数' },
      ]
      dataFields.forEach((f) => {
        let dataItem = {
          name: f.name,
          type: 'bar',
          data: [],
        }
        for (let i = 1; i < this.trainFaults.length; i++) {
          let fault = this.trainFaults[i]
          dataItem.data.push(fault[f.field])
        }
        dataInfo2.dataSeries.push(dataItem)
        dataInfo2.dataCategory.push(f.name)
      })
      for (let i = 1; i < this.trainFaults.length; i++) {
        let fault = this.trainFaults[i]
        dataInfo2.dataNames.push(fault.trainNo)
      }
      this.addHBar(this.maxRow, luckyexcelUtil.char2Index('J'), dataInfo2)

      this.maxRow = this.maxRow + 22

      // 绘制图表--架修期各系统故障占比
      let pieOption = {
        title: '架修期各系统故障占比',
        data: [],
      }
      this.systeFaults.forEach((fault) => {
        if (fault.repair) {
          pieOption.data.push({
            name: fault.sys,
            value: fault.repair,
          })
        }
      })
      if (pieOption.data.length === 0) {
        pieOption.data.push({
          name: '暂无数据',
          value: 0,
        })
      }
      this.addPie(this.maxRow, luckyexcelUtil.char2Index('A'), pieOption)
      // 绘制图表--架修期各车故障占比
      let pieOption2 = {
        title: '架修期各车故障占比',
        data: [],
      }
      for (let i = 1; i < this.trainFaults.length; i++) {
        let fault = this.trainFaults[i]
        if (fault.repair) {
          pieOption2.data.push({
            name: fault.trainNo,
            value: fault.repair,
          })
        }
      }
      if (pieOption2.data.length === 0) {
        pieOption2.data.push({
          name: '暂无数据',
          value: 0,
        })
      }
      this.addPie(this.maxRow, luckyexcelUtil.char2Index('J'), pieOption2)

      this.maxRow = this.maxRow + 22

      // 绘制图表--质保期各系统故障占比
      let pieOption3 = {
        title: '质保期各系统故障占比',
        data: [],
      }
      this.systeFaults.forEach((fault) => {
        if (fault.warranty) {
          pieOption3.data.push({
            name: fault.sys,
            value: fault.warranty,
          })
        }
      })
      if (pieOption3.data.length === 0) {
        pieOption3.data.push({
          name: '暂无数据',
          value: 0,
        })
      }
      this.addPie(this.maxRow, luckyexcelUtil.char2Index('A'), pieOption3)

      // 绘制图表--质保期各车故障占比
      let pieOption4 = {
        title: '质保期各车故障占比',
        data: [],
      }
      for (let i = 1; i < this.trainFaults.length; i++) {
        let fault = this.trainFaults[i]
        if (fault.warranty) {
          pieOption4.data.push({
            name: fault.trainNo,
            value: fault.warranty,
          })
        }
      }
      if (pieOption4.data.length === 0) {
        pieOption4.data.push({
          name: '暂无数据',
          value: 0,
        })
      }
      this.addPie(this.maxRow, luckyexcelUtil.char2Index('J'), pieOption4)
    },
    /*  添加饼图 */
    addPie(row, col, dataInfo) {
      let option = {
        backgroundColor: '#f6f6f6',
        title: {
          text: dataInfo.title,
          top: 10,
          left: 'center',
        },
        tooltip: {
          trigger: 'item',
        },
        legend: {
          show: false,
          orient: 'vertical',
          left: 'right',
        },
        series: [
          {
            name: '故障数',
            type: 'pie',
            radius: '50%',
            data: dataInfo.data,
            label: {
              show: true,
              formatter: '{b} : {c} ({d}%)',
            },
          },
        ],
      }
      this.insertChart(row, col, option)
    },
    /* 添加水平的柱状图 */
    addHBar(row, col, dataInfo) {
      let option = {
        backgroundColor: '#f6f6f6',
        title: {
          text: dataInfo.title,
          top: 10,
          left: 'center',
        },
        legend: {
          data: dataInfo.dataCategory,
          orient: 'vertical',
          left: 'right',
          top: 'middle',
        },
        grid: {
          top: '35',
          left: '15',
          right: '45',
          bottom: '15',
          containLabel: true,
        },
        xAxis: {
          type: 'value',
          boundaryGap: [0, 0.01],
        },
        yAxis: {
          type: 'category',
          data: dataInfo.dataNames,
        },
        series: dataInfo.dataSeries,
      }

      this.insertChart(row, col, option)
    },

    /*  插入入Echart 图片 */
    insertChart(row, col, options) {
      options['textStyle'] = {
        fontSize: 13,
      }

      let canvas = document.createElement('canvas')
      canvas.id = 'CursorLayer'
      let myChart = echarts.init(canvas)
      myChart.clear()
      myChart.setOption(options)
      myChart.resize({ width: 675, height: 450, silent: true })
      let dataUrl = myChart.getDataURL()
      // Yzc 2021-05-11 使用先配置好图片插入数据在渲染插入图片功能
      this.sheetOption.data[0].images[`image_${row}_${col}`] = {
        type: '3',
        src: dataUrl,
        originWidth: 675,
        originHeight: 450,
        default: {
          width: 583,
          height: 387,
          top: this.maxRow * 18.5 + 100,
          left: this.imageCount % 2 ? 0 : 664,
        },
        crop: {
          width: 583,
          height: 387,
          offsetLeft: 0,
          offsetTop: 0,
        },
        isFixedPos: false,
        fixedLeft: 77,
        fixedTop: 286,
        border: {
          width: 0,
          radius: 0,
          style: 'solid',
          color: '#000',
        },
      }
      this.imageCount++
    },
    downloadExcel() {
      exportExcel(luckysheet.getAllSheets(), this.queryParam.year + '年' + '故障汇总统计')
    },
  },
}
</script>

<style scoped>
</style>