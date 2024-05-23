<template>
  <div class='bodyWrapper' style='padding: 8px'>
    <div class='table-page-search-wrapper'>
      <a-form layout='inline' @keyup.enter.native='loadData'>
        <a-row :gutter='24'>
          <a-col :md='4' :sm='24'>
            <!--            <a-form-item label="日期">-->
            <!--              <a-date-picker-->
            <!--                format="YYYY"-->
            <!--                v-model="yearValue"-->
            <!--                :defaultValue="this.$moment(new Date())"-->
            <!--                mode="year"-->
            <!--                style="width: 100%"-->
            <!--                :allowClear="true"-->
            <!--                :open="yearOpen"-->
            <!--                @change='changeYear'-->
            <!--                @panelChange="changeYear"-->
            <!--                @focus="yearOpen = true"-->
            <!--              />-->
            <!--            </a-form-item>-->
            <a-form-item label='月份'>
              <a-range-picker
                :placeholder="['开始月份', '结束月份']"
                format='YYYY-MM'
                :value='monthVal'
                @panelChange='handlePanelMonthChange'
                :mode='mode'
              />
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
              <a-button @click='$refs.importModal.show()'>导入</a-button>
              <a-button @click='downloadExcel'>导出</a-button>
              <a-button @click='setRate'>设置税率</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class='table-page-body'>
      <div :class="{ 'na-dom-hidden': !hasData }" id='luckysheet' style='height: calc(100% - 0px); width: 100%'></div>
      <div :class="{ 'na-dom-hidden': hasData }" style='width: 100%; text-align: center'>暂无数据</div>
    </div>
    <a-modal title='设置税率' :visible='visible' @ok='handleOk' @cancel='handleCancel'>
      <div>
        <!-- <a-form-item label="税率："> -->
        <span>税率：</span>
        <a-input-number
          v-model='rateSetValue'
          placeholder='请输入税率'
          :formatter='(value) => `${value}%`'
          :parser="(value) => value.replace('%', '')"
          :min='0'
          :defaultValue='0'
          :step='1'
          style='width: calc(100% - 58px)'
        ></a-input-number>
        <!-- </a-form-item> -->
      </div>
    </a-modal>
    <j-import-modal :sum='1' ref='importModal' url='/import/rptBoard' @ok='init_sheet'></j-import-modal>
  </div>
</template>

<script>
import { getReportTemplate, getTrainMaterialSum, getTrainCars } from '@api/tirosReportApi'
import luckyexcelUtil from '@views/tiros/util/LuckyexcelUtil'
import { exportExcel } from '@views/tiros/util/export'
import * as echarts from 'echarts'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import JImportModal from '@/components/jeecg/JImportModal'
import { getSysConfig, setSysConfig } from '@api/api'

export default {
  name: 'material_sum_train',
  components: { LineSelectList, JImportModal },
  data() {
    return {
      hasData: false,
      visible: false,
      yearOpen: false,
      yearValue: this.$moment(new Date()),
      dictTrainStr: '',
      maxRow: 0,
      chartWidth: 1300,
      chartHeight: 550,
      chartData: [],
      rateSetValue: 0,
      trainCars: 0,
      queryParam: {
        // year: this.$moment(new Date()).format('YYYY'),
        trainNo: '',
        depotId: this.$store.getters.userInfo.depotId,
        lineId: '',
        workshopId: this.$store.getters.userInfo.workshopId
      },
      mode: ['month', 'month'],
      monthVal: [this.$moment(new Date()).format('YYYY-MM'), this.$moment(new Date()).format('YYYY-MM')]
    }
  },
  computed: {
    rate() {
      return (Number(this.rateSetValue) + 100) / 100
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
    handlePanelMonthChange(value, mode) {
      this.monthVal = value
    },
    handleSysFocus() {
      if (!this.queryParam.lineId) this.$message.warn('请先选择线路!')
    },
    onLineChange(val, option) {
      this.dictTrainStr = 'bu_train_info,train_no,train_no,status=1 and line_id=\'' + val + '\''
    },
    changeYear(value, mode) {
      this.yearValue = value
      this.queryParam.year = value ? value.format('YYYY') : ''
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
      /*if (!this.queryParam.depotId) {
        this.$message.warn('当前人员没有所属车辆段')
        return false
      }*/
      return true
    },
    init_sheet() {
      this.maxRow = 0
      if (!this.check()) {
        return
      }
      getReportTemplate('material_sum_train').then((res) => {
        if (res.success) {
          if (res.result && !!res.result.tempContent) {
            const sheet = JSON.parse(res.result.tempContent)
            const op = Object.assign({}, luckyexcelUtil.getDefaultOptions())
            sheet.data = []
            op.allowEdit = true
            op.data = [sheet]
            this.hasData = true
            op.hook = {
              workbookCreateAfter: () => {
                // 初始化Sheet后再设置数据
                this.loadData()
              }
            }
            luckysheet.destroy()
            luckysheet.create(op)
          } else {
            this.$message.error('报表模板内容为空，请定义模板')
          }
        } else {
          this.$message.error('报表模板查询失败：', res.message)
          this.dailyInfo = null
        }
      })
    },
    loadData() {
      let param = {
        ...this.queryParam,
        startMonth: this.$moment(this.monthVal[0]).format('YYYY-MM'),
        endMonth: this.$moment(this.monthVal[1]).format('YYYY-MM')
      }
      getTrainMaterialSum(param).then((res) => {
        if (res.success) {
          // res.result = [
          //   {
          //     consumeCost: 0,
          //     mustCost: 0,
          //     mustRandomCost: 0,
          //     randomCost: 1,
          //     sysName: '测试1',
          //   },
          //   {
          //     consumeCost: 2,
          //     mustCost: 2,
          //     mustRandomCost: 2,
          //     randomCost: 2,
          //     sysName: '测试2',
          //   },
          //   {
          //     consumeCost: 3,
          //     mustCost: 3,
          //     mustRandomCost: 3,
          //     randomCost: 3,
          //     sysName: '测试3',
          //   }
          // ]
          this.hasData = res.result.length > 0
          this.init_material_sum(res.result)
        } else {
          this.hasData = false
          this.$message.error('查询物资汇总统计错误：', res.message)
          this.dailyInfo = null
        }
      })
    },
    changeTwoDecimal_f(x) {
      let f_x = parseFloat(x)
      if (isNaN(f_x)) {
        return 0
      }
      f_x = Math.round(x * 100) / 100
      let s_x = f_x.toString()
      let pos_decimal = s_x.indexOf('.')
      if (pos_decimal < 0) {
        pos_decimal = s_x.length
        s_x += '.'
      }
      while (s_x.length <= pos_decimal + 2) {
        s_x += '0'
      }
      return s_x
    },
    init_material_sum(materials) {
      // console.log('materials:', materials)

      // 清空原有的公式
      luckysheet.getSheet(0).calcChain = []
      // 设置标题
      luckyexcelUtil.setCellValue(0, luckyexcelUtil.char2Index('A'), this.queryParam.trainNo + '累计消耗金额')
      luckyexcelUtil.setCellValue(0, luckyexcelUtil.char2Index('F'), this.queryParam.trainNo + '累计消耗金额(含税)')
      if (materials.length < 1) {
        return
      }
      let rowIndex = 2
      for (let i = 0; i < materials.length; i++) {
        if (i > 0) {
          rowIndex = rowIndex + 1
          luckysheet.insertRow(rowIndex)
        }

        let m = materials[i]
        luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('A'), {
          v: m['sysName'],
          bl: 1,
          fs: 12,
          ff: '等线'
        })
        luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('B'), {
          v: m['mustCost'],
          ct: { fa: '#,##0.00_', t: 'n' },
          fs: 12,
          ff: '等线'
        })
        luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('C'), {
          v: m['randomCost'],
          ct: { fa: '#,##0.00_', t: 'n' },
          fs: 12,
          ff: '等线'
        })
        luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('D'), {
          v: m['mustRandomCost'],
          ct: { fa: '#,##0.00_', t: 'n' },
          fs: 12,
          ff: '等线'
        })
        luckyexcelUtil.setCellValue(rowIndex, luckyexcelUtil.char2Index('E'), {
          v: m['consumeCost'],
          ct: { fa: '#,##0.00_', t: 'n' },
          fs: 12,
          ff: '等线'
        })
      }
      // 设置边框
      luckyexcelUtil.setBorder(1, 'A', rowIndex, 'I')

      // 设置公式
      let sources = []
      let start = 'B'
      for (let i = 0; i <= 7; i++) {
        sources.push({
          row: rowIndex + 1,
          col: String.fromCharCode(start.charCodeAt() + i)
        })
      }

      let template = (materials.length > 1) ? '=SUM(${C}3:${C}' + (rowIndex + 1) + ')' : '=SUM(${C}3)'
      luckyexcelUtil.setCellValueByTemplate(sources, template, '${C}', 2)

      if (this.maxRow < rowIndex) {
        this.maxRow = rowIndex
      }
      setTimeout(() => {
        this.init_byRates()
      }, 200)

    },
    async init_byRates() {
      // let rate = 1.13
      this.rateSetValue = await getSysConfig('costRate').then((res) => {
        if (res.success) {
          return res.result.configValue ? Number(res.result.configValue) : 0
        }
        console.warn(res.message)
        return 0
      })

      this.trainCars = await getTrainCars({
        lineId: this.queryParam.lineId,
        trainNo: this.queryParam.trainNo
      }).then(res => {
        if (res.success) {
          return res.result
        } else {
          this.$message.error('获取列车车厢数量错误')
          console.error(res.message)
          return 0
        }
      })

      let start = 'F'
      for (let i = 0; i <= 3; i++) {
        let sources = []
        let curCol = String.fromCharCode(start.charCodeAt() + i)
        let srcCol = String.fromCharCode(curCol.charCodeAt() - 4)
        for (let j = 2; j <= this.maxRow + 1; j++) {
          sources.push({
            row: j,
            col: curCol,
            value: {
              f: '=' + srcCol + '${R}*' + this.rate.toString(),
              ct: { fa: '#,##0.00_', t: 'n' },
              fs: 12,
              ff: '等线'
            }
          })
        }
        luckyexcelUtil.setCellValueByObjectValue(sources, 'f', '${R}', 1)
      }


      // 设置水平对齐
      // 数字居右
      luckyexcelUtil.setRangeFormat('ht', 2, 2, 'B', this.maxRow + 1, 'I')
      // 标题居中
      luckyexcelUtil.setRangeFormat('ht', 0, 2, 'A', this.maxRow + 1, 'A')

      // 加载最下面的统计
      setTimeout(() => {
        this.init_total()
      }, 200)
    },
    init_total() {
      // 成本合计不含税 =SUM(D4:E4)
      let total_row = this.maxRow + 1 // 合计行
      let beginRow = this.maxRow + 2 // 成本合计行, 从0开始算的
      let value = (total_row === 3) ? '=SUM(D3:E3)' : '=SUM(D4:E4)'.replaceAll('4', total_row + 1 + '') // 公式中的行号从1开始算
      luckyexcelUtil.setCellValue(beginRow, luckyexcelUtil.char2Index('D'), value)

      // 每节成本
      value = (total_row === 3) ? `=SUM(D3:E3)/${this.trainCars}` : ('=D' + (beginRow + 1) + `/${this.trainCars}`)
      luckyexcelUtil.setCellValue(beginRow + 1, luckyexcelUtil.char2Index('D'), value)

      // 含税合计
      let sources = [
        { row: beginRow + 2, col: 'B' },
        { row: beginRow + 2, col: 'C' },
        { row: beginRow + 2, col: 'D' },
        { row: beginRow + 2, col: 'E' }
      ]
      let template = (total_row === 3) ? ('=${C}3*' + this.rate.toString()) : ('=${C}4*' + this.rate.toString()).replaceAll('4', total_row + 1 + '')
      luckyexcelUtil.setCellValueByTemplate(sources, template, '${C}', 2)

      // 成本合计（含税)
      value = (total_row === 3) ? '=D7+E7' : `=SUM(D16:E16)`.replaceAll('16', beginRow + 3 + '')
      luckyexcelUtil.setCellValue(beginRow + 3, luckyexcelUtil.char2Index('D'), value)

      // 每节成本(含税)
      value = (total_row === 3) ? `=(D7+E7)/${this.trainCars}` : `=D17/${this.trainCars}`.replaceAll('17', beginRow + 4 + '')
      luckyexcelUtil.setCellValue(beginRow + 4, luckyexcelUtil.char2Index('D'), value)

      // 小数位
      /*excelUtil.setRangeFormat('ct', '0.00', beginRow, 'D', beginRow + 4, 'E')
      excelUtil.setRangeFormat('ct', '0.00', beginRow + 2, 'B', beginRow + 2, 'C')
     */
      // 设置水平对齐
      luckyexcelUtil.setRangeFormat('ht', 2, beginRow, 'D', beginRow + 4, 'E')
      luckyexcelUtil.setRangeFormat('ht', 2, beginRow + 2, 'B', beginRow + 2, 'C')

      // 设置选中状态
      luckysheet.setRangeShow('A1', { show: false })
      setTimeout(() => {
        this.init_chart()
      }, 500)
    },
    init_chart() {
      // 获取数据
      this.chartData = []
      for (let i = 2; i <= this.maxRow; i++) {
        this.chartData.push({
          name: luckysheet.getCellValue(i, luckyexcelUtil.char2Index('A'), { type: 'm' }),
          bj: luckysheet.getCellValue(i, luckyexcelUtil.char2Index('H'), { type: 'v' }),
          hc: luckysheet.getCellValue(i, luckyexcelUtil.char2Index('I'), { type: 'v' })
        })
      }
      // console.log('the datas:', datas)
      let source = []
      source.push(['product', '备品备件', '耗材'])

      for (let i = 0; i < this.chartData.length; i++) {
        let d = this.chartData[i]
        let data = []
        data.push(d.name)
        data.push(d.bj)
        data.push(d.hc)
        source.push(data)
      }

      let option = {
        backgroundColor: '#f6f6f6',
        title: {
          text: this.queryParam.trainNo + '车各系统消耗金额(含税)',
          top: 10,
          left: 'center'
        },
        grid: {
          left: 100,
          right: 30
        },
        legend: {
          show: true,
          orient: 'vertical',
          left: 'right'
        },
        tooltip: {},
        xAxis: {
          type: 'category',
          boundaryGap: true,
          splitNumber: 16,
          axisTick: {
            length: 64 // 竖线的长度
          },
          axisLabel: {
            interval: 0
            // rotate:40
          }
        },
        yAxis: {
          type: 'value'
        },
        dataset: {
          source: source
        },
        series: [
          { type: 'bar', barWidth: 25 },
          { type: 'bar', barWidth: 25 }
        ],
        graphic: this.buildTableGraphic()
      }

      this.insertChart(this.maxRow + 4 + 5, luckyexcelUtil.char2Index('A'), option)
    },
    buildTableGraphic() {
      let draws = []
      draws.push({
        type: 'line',
        id: 'line1',
        left: 5,
        bottom: 45,
        style: {
          fill: '#fff',
          stroke: '#686767'
        },
        shape: {
          x1: 0,
          y1: 0,
          x2: this.chartWidth - 35,
          y2: 2
        }
      })
      draws.push({
        type: 'line',
        id: 'line2',
        left: 5,
        bottom: 25,
        style: {
          fill: '#fff',
          stroke: '#686767'
        },
        shape: {
          x1: 0,
          y1: 0,
          x2: this.chartWidth - 35,
          y2: 2
        }
      })
      draws.push({
        type: 'line',
        id: 'line3',
        left: 5,
        bottom: 5,
        style: {
          fill: '#fff',
          stroke: '#686767'
        },
        shape: {
          x1: 0,
          y1: 0,
          x2: this.chartWidth - 35,
          y2: 2
        }
      })
      draws.push({
        type: 'line',
        id: 'line4',
        left: 5,
        bottom: 7,
        style: {
          fill: '#fff',
          stroke: '#686767'
        },
        shape: {
          x1: 0,
          y1: 0,
          x2: 0,
          y2: 40
        }
      })

      draws.push({
        type: 'text',
        id: 'text1',
        left: 10,
        bottom: 30,
        style: { text: '备品备件(元)' }
      })
      draws.push({
        type: 'text',
        id: 'text2',
        left: 10,
        bottom: 10,
        style: { text: '耗材(元)' }
      })

      //
      let grid = this.chartWidth - 100 - 30
      let dit = grid / this.chartData.length
      let startLeft = 108
      for (let i = 0; i < this.chartData.length; i++) {
        let d = this.chartData[i]
        draws.push({
          type: 'text',
          id: 'textvaluebj' + i,
          left: startLeft + i * dit,
          bottom: 30,
          style: { text: this.formatMoney(d.bj, 2, '') }
        })
        draws.push({
          type: 'text',
          id: 'textvaluehc' + i,
          left: startLeft + i * dit,
          bottom: 10,
          style: { text: this.formatMoney(d.hc, 2, '') }
        })
      }

      return draws
    },
    /*  插入入Echart 图片 */
    insertChart(row, col, options) {
      options['textStyle'] = {
        fontSize: 13
      }

      let canvas = document.createElement('canvas')
      canvas.id = 'CursorLayer'
      /*     canvas.width = this.chartWidth;
      canvas.height = 600;*/
      let myChart = echarts.init(canvas)
      myChart.clear()
      myChart.setOption(options)
      myChart.resize({ width: this.chartWidth, height: this.chartHeight, silent: true })
      let img = myChart.getDataURL()

      luckysheet.insertImage(img, {
        rowIndex: row, colIndex: col, width: this.chartWidth * 0.8,
        height: this.chartHeight * 0.8, force: true
      })
    },
    formatMoney(number, places, symbol, thousand, decimal) {
      number = number || 0
      places = !isNaN((places = Math.abs(places))) ? places : 2
      symbol = symbol !== undefined ? symbol : '$'
      thousand = thousand || ','
      decimal = decimal || '.'
      let negative = number < 0 ? '-' : '',
        i = parseInt((number = Math.abs(+number || 0).toFixed(places)), 10) + '',
        j = (j = i.length) > 3 ? j % 3 : 0
      return (
        symbol +
        negative +
        (j ? i.substr(0, j) + thousand : '') +
        i.substr(j).replace(/(\d{3})(?=\d)/g, '$1' + thousand) +
        (places
          ? decimal +
          Math.abs(number - i)
            .toFixed(places)
            .slice(2)
          : '')
      )
    },
    setRate() {
      this.visible = true
      getSysConfig('costRate').then((res) => {
        if (res.success) {
          this.rateSetValue = res.result.configValue ? Number(res.result.configValue) : 0
        } else {
          this.$message.error(res.message)
        }
      })
    },
    handleOk() {
      if (!this.rateSetValue) {
        this.$message.warn('请输入税率')
        return
      }
      setSysConfig({
        configCode: 'costRate',
        configName: '物料成本税率',
        configRemark: '',
        configValue: this.rateSetValue
      }).then((res) => {
        if (res.success) {
          this.init_sheet()
          this.$message.success('设置成功')
        } else {
          this.$message.error(res.message)
        }
      })
      this.visible = false
    },
    handleCancel() {
      this.visible = false
    },
    downloadExcel() {
      if (!this.check()) {
        return
      }
      exportExcel(luckysheet.getAllSheets(), this.queryParam.year + '年' + this.queryParam.trainNo + '车物料成本统计')
    }
  }
}
</script>

<style scoped>
.na-dom-hidden {
  display: none;
}
</style>