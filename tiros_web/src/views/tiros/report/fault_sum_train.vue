<template>
  <div class='bodyWrapper' style='padding: 8px'>
    <div class='table-page-search-wrapper'>
      <a-form layout='inline' @keyup.enter.native='init_sheet'>
        <a-row :gutter='24'>
          <a-col :md='12' :xl='8'>
            <!-- <a-form-item label="日期">
              <a-date-picker
                format="YYYY"
                v-model="yearValue"
                :defaultValue="this.$moment(new Date())"
                mode="year"
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
            <a-form-item label='日期'>
              <!-- <a-date-picker v-model="yearValue" @change="onDateChange" /> -->
              <a-range-picker v-model='dateTime' @change='onDateChange' :defaultPickerValue='defaultDateRange' />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='线路'>
              <!--              <j-dict-select-tag
                v-model="queryParam.lineId"
                :triggerChange="true"
                @change="onLineChange"
                dictCode="bu_mtr_line,line_name,line_id"
              />-->
              <line-select-list v-model='queryParam.lineId' @change='onLineChange'></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='车辆'>
              <j-dict-select-seach-tag v-model='queryParam.trainNo' @focus='handleSysFocus' :dictCode='dictTrainStr' />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-space>
              <a-button @click='init_sheet'>查询</a-button>
              <a-button @click='downloadExcel'>导出</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class='table-page-body'>
      <div id='luckysheet' style='height: calc(100% - 0px); width: 100%'></div>
    </div>
  </div>
</template>

<script>
import { getReportTemplate, getOneTrainFaultSum } from '@api/tirosReportApi'
import NaSelectDate from '@comp/tiros/Na-select-date'
import luckyexcelUtil from '@views/tiros/util/LuckyexcelUtil'
import * as echarts from 'echarts'
import { exportExcel } from '@views/tiros/util/export'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'fault_sum_train',
  components: { LineSelectList, NaSelectDate },
  data() {
    return {
      yearOpen: false,
      yearValue: this.$moment(new Date()),
      dictTrainStr: '',
      queryParam: {
        // year: this.$moment(new Date()).format('YYYY'),
        trainNo: '',
        // depotId: this.$store.getters.userInfo.depotId,
        lineId: '',
        // workshopId: this.$store.getters.userInfo.workshopId,
        startDate: this.$moment(new Date()).startOf('month').format('YYYY-MM-DD'),
        endDate: this.$moment(new Date()).endOf('month').format('YYYY-MM-DD')
      },
      // mode: ['month', 'month'],
      // monthVal: [this.$moment(new Date()).format('YYYY-MM'),this.$moment(new Date()).format('YYYY-MM')],
      dateTime: [this.$moment(new Date()).startOf('month').format('YYYY-MM-DD'), this.$moment(new Date()).endOf('month').format('YYYY-MM-DD')],
      faultSum: {
        C: 0,
        D: 0,
        H: 0,
        I: 0,
        J: 0,
        K: 0
      },
      maxRow: 0,
      trainFaults: [],
      sheetOption: {},
      imageCount: 1
    }
  },
  watch: {
    'queryParam.lineId': {
      handler(val) {
        this.onLineChange(val)
      },
      deep: true
    }
  },
  created() {
    if (this.$store.getters.userInfo.lineId.indexOf(',') > -1) {
      this.$set(this.queryParam, 'lineId', this.$store.getters.userInfo.lineId.split(',')[0])
    } else {
      this.$set(this.queryParam, 'lineId', this.$store.getters.userInfo.lineId)
    }
  },
  beforeDestroy() {
    // 离开销毁Luckysheet
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
      } else {
        this.queryParam.startDate = ''
      }
      if (this.dateTime[1]) {
        this.queryParam.endDate = this.dateTime[1].format('YYYY-MM-DD')
      } else {
        this.queryParam.endDate = ''
      }
    },
    // dateChange1(value) {
    //   console.log(value)
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
    handleSysFocus() {
      if (!this.queryParam.lineId) this.$message.warn('请先选择线路!')
    },
    onLineChange(val, option) {
      this.dictTrainStr = 'bu_train_info,train_no,train_no,status=1 and line_id=\'' + val + '\''
    },
    changeYear(value, mode) {
      this.yearValue = value
      this.queryParam.year = value.format('YYYY')
      this.yearOpen = false
    },
    check() {
      // if (!this.queryParam.year) {
      //   this.$message.warn('请选择年份')
      //   return false
      // }
      if (!this.queryParam.trainNo) {
        this.$message.warn('请选择车辆')
        return false
      }
      // if (!this.queryParam.workshopId) {
      //   this.$message.warn('当前人员没有车间')
      //   return false
      // }
      return true
    },
    init_sheet() {
      this.maxRow = 0
      this.faultSum = {
        C: 0,
        D: 0,
        H: 0,
        I: 0,
        J: 0,
        K: 0
      }
      if (!this.check()) {
        return
      }
      getReportTemplate('fault_sum_train').then((res) => {
        if (res.success) {
          if (res.result && !!res.result.tempContent) {
            const sheet = JSON.parse(res.result.tempContent)
            const op = Object.assign({}, luckyexcelUtil.getDefaultOptions())
            this.imageCount = 1
            sheet.images = {}
            sheet.data = []
            op.allowEdit = false
            op.data = [sheet]
            op.hook = {
              workbookCreateAfter: () => {
                // 初始化Sheet后再设置数据
                this.init_train_fault_sum(this.trainFaults)
              }
            }
            this.loadData().then(() => {
              this.sheetOption = op
              // 插图使用插入option.image 来实现调整图片大小的目的
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
      return getOneTrainFaultSum({
        ...this.queryParam
        // startMonth: this.$moment(this.monthVal[0]).format('YYYY-MM'),
        // endMonth: this.$moment(this.monthVal[1]).format('YYYY-MM'),
      }).then((res) => {
        if (res.success) {
          let faults = res.result
          this.trainFaults = faults
          this.maxRow = this.trainFaults.length
        } else {
          this.$message.error('查询车辆故障统计错误：', res.message)
          this.dailyInfo = null
        }
      })
    },
    init_train_fault_sum(faults) {
      // 设置标题
      luckyexcelUtil.setCellValue(1, luckyexcelUtil.char2Index('B'), this.queryParam.trainNo + '架修期故障总数')
      luckyexcelUtil.setCellValue(1, luckyexcelUtil.char2Index('G'), this.queryParam.trainNo + '质保期故障总数')
      // 设置故障总数
      let rowIndex = 3
      if (faults && faults.length > 1) {
        for (let i = 0; i < faults.length; i++) {
          let fault = faults[i]
          // 系统
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('B'), {
            v: fault['sys'],
            ht: 0,
            vt: 0,
            ff: '宋体',
            fs: 11
          })
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('G'), {
            v: fault['sys'],
            ht: 0,
            vt: 0,
            ff: '宋体',
            fs: 11
          })

          // 架修期故障
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('C'), {
            v: fault['repair'],
            ht: 0,
            vt: 0,
            ff: '宋体',
            fs: 11
          })
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('D'), {
            v: fault['repairAB'],
            ht: 0,
            vt: 0,
            ff: '宋体',
            fs: 11
          })
          this.faultSum['C'] = this.faultSum['C'] + fault['repair']
          this.faultSum['D'] = this.faultSum['D'] + fault['repairAB']
          // 质保期故障
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('H'), {
            v: fault['warranty'],
            ht: 0,
            vt: 0,
            ff: '宋体',
            fs: 11
          })
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('I'), {
            v: fault['warrantyAB'],
            ht: 0,
            vt: 0,
            ff: '宋体',
            fs: 11
          })
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('J'), {
            v: fault['warrantyOnline'],
            ht: 0,
            vt: 0,
            ff: '宋体',
            fs: 11
          })
          luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('K'), {
            v: fault['warrantyHasDuty'],
            ht: 0,
            vt: 0,
            ff: '宋体',
            fs: 11
          })

          this.faultSum['H'] = this.faultSum['H'] + fault['warranty']
          this.faultSum['I'] = this.faultSum['I'] + fault['warrantyAB']
          this.faultSum['J'] = this.faultSum['J'] + fault['warrantyOnline']
          this.faultSum['K'] = this.faultSum['K'] + fault['warrantyHasDuty']

          rowIndex++
        }
        // 设置总数
        for (const faultSumKey in this.faultSum) {
          luckyexcelUtil.setCellValue(1, luckyexcelUtil.char2Index(faultSumKey), this.faultSum[faultSumKey])
        }
        // 设置边框
        luckyexcelUtil.setBorder(1, 'B', rowIndex - 1, 'D')
        luckyexcelUtil.setBorder(1, 'G', rowIndex - 1, 'K')

        // if (this.maxRow < rowIndex) {
        //   this.maxRow = rowIndex
        // }

        luckysheet.setRangeShow('A1', { show: false })
      }
    },
    /* 加载所有图形 */
    init_charts() {
      // 绘制图表--架修期内各系统故障分布
      let dataInfo = {
        title: this.queryParam.trainNo + '架修期各系统故障分布',
        dataCategory: ['架修期故障数', 'A/B类故障数'],
        dataNames: [],
        dataSeries: [
          {
            name: '架修期故障数',
            type: 'bar',
            data: []
          },
          {
            name: 'A/B类故障数',
            type: 'bar',
            data: []
          }
        ]
      }
      this.trainFaults.forEach((fault) => {
        dataInfo.dataNames.push(fault.sys)
        dataInfo.dataSeries[0].data.push(fault.repair)
        dataInfo.dataSeries[1].data.push(fault.repairAB)
      })
      this.addHBar(this.maxRow, luckyexcelUtil.char2Index('B'), dataInfo)

      // 绘制图表--质保期各系统故障分布
      let dataInfo2 = {
        title: this.queryParam.trainNo + '车质保期各系统故障分布',
        dataCategory: [],
        dataNames: [],
        dataSeries: []
      }
      let dataFields = [
        { field: 'warranty', name: '质保期故障' },
        { field: 'warrantyAB', name: '质保期A/B故障' },
        { field: 'warrantyOnline', name: '质保期正线故障' },
        { field: 'warrantyHasDuty', name: '质保期有责故障' }
      ]
      dataFields.forEach((f) => {
        let dataItem = {
          name: f.name,
          type: 'bar',
          data: []
        }
        for (let i = 0; i < this.trainFaults.length; i++) {
          let fault = this.trainFaults[i]
          dataItem.data.push(fault[f.field])
        }
        dataInfo2.dataSeries.push(dataItem)
        dataInfo2.dataCategory.push(f.name)
      })
      for (let i = 0; i < this.trainFaults.length; i++) {
        let fault = this.trainFaults[i]
        dataInfo2.dataNames.push(fault.sys)
      }
      this.addHBar(this.maxRow, luckyexcelUtil.char2Index('H'), dataInfo2)

      this.maxRow = this.maxRow + 21

      // 绘制图表--架修期各系统故障占比
      let pieOption = {
        title: this.queryParam.trainNo + '架修期各系统故障占比',
        data: []
      }
      this.trainFaults.forEach((fault) => {
        if (fault.repair) {
          pieOption.data.push({
            name: fault.sys,
            value: fault.repair
          })
        }
      })
      if (pieOption.data.length === 0) {
        pieOption.data.push({
          name: '暂无数据',
          value: 0
        })
      }
      this.addPie(this.maxRow, luckyexcelUtil.char2Index('B'), pieOption)

      // 绘制图表--质保期各系统故障占比
      let pieOption2 = {
        title: this.queryParam.trainNo + '质保期各系统故障占比',
        data: []
      }
      this.trainFaults.forEach((fault) => {
        if (fault.warranty) {
          pieOption2.data.push({
            name: fault.sys,
            value: fault.warranty
          })
        }
      })
      if (pieOption2.data.length === 0) {
        pieOption2.data.push({
          name: '暂无数据',
          value: 0
        })
      }
      this.addPie(this.maxRow, luckyexcelUtil.char2Index('H'), pieOption2)

      this.maxRow = this.maxRow + 21

      // 绘制图表--质保期正线故障占比
      let pieOption3 = {
        title: this.queryParam.trainNo + '质保期正线故障占比',
        data: []
      }
      if (this.faultSum['K']) {
        pieOption3.data.push({
          name: '质保期正线故障',
          value: this.faultSum['K']
        })
      }
      if ((this.faultSum['I'] - this.faultSum['K']) !== 0) {
        pieOption3.data.push({
          name: '非正线故障',
          value: this.faultSum['I'] - this.faultSum['K']
        })
      }
      if (pieOption3.data.length === 0) {
        pieOption3.data.push({
          name: '暂无数据',
          value: 0
        })
      }
      this.addPie(this.maxRow, luckyexcelUtil.char2Index('B'), pieOption3)

      // 绘制图表--质保期各车故障占比
      let pieOption4 = {
        title: this.queryParam.trainNo + '质保期正线故障各系统占比',
        data: []
      }
      for (let i = 0; i < this.trainFaults.length; i++) {
        let fault = this.trainFaults[i]
        if (fault.warrantyOnline) {
          pieOption4.data.push({
            name: fault.sys,
            value: fault.warrantyOnline
          })
        }
      }
      if (pieOption4.data.length === 0) {
        pieOption4.data.push({
          name: '暂无数据',
          value: 0
        })
      }
      this.addPie(this.maxRow, luckyexcelUtil.char2Index('H'), pieOption4)
    },
    /*  添加饼图 */
    addPie(row, col, dataInfo) {
      let option = {
        backgroundColor: '#f6f6f6',
        title: {
          text: dataInfo.title,
          top: 10,
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          show: false,
          orient: 'vertical',
          left: 'right'
        },
        series: [
          {
            name: '故障数',
            type: 'pie',
            radius: '50%',
            data: dataInfo.data,

            label: {
              show: true,
              formatter: '{b} : {c} ({d}%)'
            }
          }
        ]
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
          left: 'center'
        },
        legend: {
          data: dataInfo.dataCategory,
          orient: 'vertical',
          left: 'right',
          top: 'middle'
        },
        grid: {
          top: '35',
          left: '15',
          right: '45',
          bottom: '15',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          boundaryGap: [0, 0.01]
        },
        yAxis: {
          type: 'category',
          data: dataInfo.dataNames
        },
        series: dataInfo.dataSeries
      }

      this.insertChart(row, col, option)
    },

    /*  插入入Echart 图片 */
    insertChart(row, col, options) {
      options['textStyle'] = {
        fontSize: 13
      }
      let canvas = document.createElement('canvas')
      canvas.id = 'CursorLayer'
      let myChart = echarts.init(canvas)
      myChart.clear()
      myChart.setOption(options)
      myChart.resize({ width: 600, height: 400, silent: true })
      let dataUrl = myChart.getDataURL()
      // Yzc 2021-05-11 使用先配置好图片插入数据在渲染插入图片功能
      this.sheetOption.data[0].images[`image_${row}_${col}`] = {
        type: '3',
        src: dataUrl,
        originWidth: 600,
        originHeight: 400,
        default: {
          width: 583,
          height: 387,
          top: this.maxRow * 20 + 100,
          left: this.imageCount % 2 ? 31 : 695
        },
        crop: {
          width: 583,
          height: 387,
          offsetLeft: 0,
          offsetTop: 0
        },
        isFixedPos: false,
        fixedLeft: 77,
        fixedTop: 286,
        border: {
          width: 0,
          radius: 0,
          style: 'solid',
          color: '#000'
        }
      }
      this.imageCount++
    },

    downloadExcel() {
      if (!this.check()) {
        return
      }
      exportExcel(luckysheet.getAllSheets(), this.queryParam.year + '年' + this.queryParam.trainNo + '车故障汇总统计')
    }
  }
}
</script>

<style scoped>
</style>