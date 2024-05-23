<template>
  <div class="bodyWrapper" style="padding: 8px">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="loadData">
        <a-row :gutter="24">
          <a-col :md="4" :sm="24">
            <a-form-item label="日期">
              <a-date-picker
                format="YYYY-MM-DD"
                valueFormat="YYYY-MM-DD"
                style="width: 100%"
                v-model="queryParam.day"
                :allowClear="false"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="线路">
              <!--                <j-dict-select-tag
                  v-model="queryParam.lineId"
                  :triggerChange="true"
                  @change="onLineChange"
                  dictCode="bu_mtr_line,line_name,line_id"
                />-->
              <line-select-list v-model="queryParam.lineId" @change="onLineChange"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="车辆">
              <j-dict-select-seach-tag @focus="handleSysFocus" v-model="queryParam.trainNo" :dictCode="dictTrainStr" />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-space>
              <a-button @click="loadData" :loading="loading">查询</a-button>
              <a-button @click="createDailyReport" :loading="loading">重新生成</a-button>
              <a-button @click="saveDayReport" :loading="loading">保存日报</a-button>
              <a-button @click="downloadExcel" :loading="loading">导出</a-button>
              <!--              <a-button @click="setSelectValue">插入数据</a-button>-->
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
import { getDailyReport, createDailyReport, getReportTemplate, saveDailyReport } from '@api/tirosReportApi'
import clone from 'clone'
import { exportExcel } from '@views/tiros/util/export'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
export default {
  name: 'report_day',
  components: { LineSelectList },
  data() {
    return {
      loading: false,
      queryParam: {
        day: this.$moment(new Date()).format('YYYY-MM-DD'),
        trainNo: '',
        depotId: this.$store.getters.userInfo.depotId,
        lineId: '',
        workshopId: this.$store.getters.userInfo.workshopId,
      },
      dictTrainStr: '',
      dailyInfo: null,
      defaultOptions: {
        container: 'luckysheet', //luckysheet is the container id
        title: 'sheet',
        column: 26, // 列数
        row: 50, // 行数
        lang: 'zh', // 设定表格语言
        allowEdit: true, // 允许编辑
        showinfobar: false, // 名称栏
        showsheetbar: false, // 底部sheet页
        showstatisticBar: true, // 底部计数栏
        enableAddRow: false, // 允许添加行
        enableAddCol: false, // 允许添加列
        showtoolbar: true, // 是否显示工具栏
        hook: {
          workbookCreateAfter: (options) => {
            if (this.dailyInfo && !this.dailyInfo.reportContent) {
              this.loadHeaderInfo(this.dailyInfo)
              this.loadCurDayTask(this.dailyInfo.workPlanList)
              this.loadNexDayTask(this.dailyInfo.nextWorkList)
              this.loadOutInfo(this.dailyInfo.outinList)
              this.loadDailyFault(this.dailyInfo.faultList)
            }
          }
        }
      },
    }
  },
  watch: {
    'queryParam.lineId': {
      handler(val) {
        this.onLineChange(val)
      },
      deep: true,
    },
  },
  created() {
    this.$set(this.queryParam, 'lineId', this.$store.getters.userInfo.lineId)
  },
  beforeDestroy() {
    // 离开销毁Luckysheet
    luckysheet.destroy()
  },
  methods: {
    handleSysFocus() {
      if (!this.queryParam.lineId) this.$message.warn('请先选择线路!')
    },
    onLineChange(val, option) {
      this.dictTrainStr = "bu_train_info,train_no,train_no,status=1 and line_id='" + val + "'"
    },
    check() {
      if (!this.queryParam.day) {
        this.$message.warn('请选择日报日期')
        return false
      }
      if (!this.queryParam.trainNo) {
        this.$message.warn('请选择日报所属车辆')
        return false
      }
      if (!this.queryParam.workshopId) {
        this.$message.warn('当前人员没有车间')
        return false
      }
      return true
    },
    createDailyReport() {
      if (!this.check()) {
        return
      }
      this.loading = true
      createDailyReport(this.queryParam).then((res) => {
        if (res.success) {
          let dayReport = res.result
          this.inti_dayReport(dayReport)
        } else {
          this.$message.error('生成日报错误：', res.message)
        }
      })
    },
    loadData() {
      if (!this.check()) {
        return
      }
      this.dailyInfo = null
      this.loading = true
      getDailyReport(this.queryParam).then((res) => {
        if (res.success) {
          let dayReport = res.result
          this.inti_dayReport(dayReport)
        } else {
          this.loading = false
          this.$message.error('查询日报错误：', res.message)
          this.dailyInfo = null
        }
      })
    },
    inti_dayReport(dayReport) {
      this.dailyInfo = dayReport
      // console.log('result:', this.dailyInfo)
      // 有保存过内容呢
      if (
        this.dailyInfo.reportContent != null &&
        this.dailyInfo.reportContent != undefined &&
        this.dailyInfo.reportContent != ''
      ) {
        const sheet = JSON.parse(this.dailyInfo.reportContent)
        this.init_sheet(sheet)
      } else {
        // 要先加载模板啊
        getReportTemplate('day_report').then((res) => {
          if (res.success) {
            if (res.result && !!res.result.tempContent) {
              const sheet = JSON.parse(res.result.tempContent)
              this.init_sheet(sheet)
            } else {
              this.$message.error('报表模板内容为空，请定义模板')
            }
          } else {
            this.$message.error('报表模板查询失败：', res.message)
            this.dailyInfo = null
          }
        })
      }
    },
    init_sheet(sheet) {
      const op = Object.assign({}, this.defaultOptions)
      op.data = [sheet]
      luckysheet.destroy()
      luckysheet.create(op)
      this.loading = false
      luckysheet.setRangeShow('A1', { show: false })
    },
    // 保存日报
    saveDayReport() {
      if (!!this.dailyInfo) {
        let saveInfo = Object.assign({}, this.dailyInfo)
        if (!saveInfo.userName) {
          saveInfo.userName = this.$store.getters.userInfo.realname
        }
        // console.log('saveInfo:', saveInfo)
        saveInfo.reportContent = JSON.stringify(luckysheet.getAllSheets()[0])
        saveDailyReport(saveInfo).then((res) => {
          if (res.success) {
            this.$message.success('保存成功')
          } else {
            this.$message.error('保存失败')
            console.error('保存生产日报：', res.message)
          }
        })
      } else {
        this.$message.warn('当前没有要保存的日报信息')
      }
    },
    // 将表格字母列转成序号，必须是大写（最多支持两个字母)
    getCharIndex(c) {
      let index = 0
      if (c.length < 2) {
        index = c.charCodeAt() - 65
      }
      if (c.length === 2) {
        let first = c[0]
        let sec = c[1]
        let r = first.charCodeAt() - 65
        r = (r + 1) * 25
        index = r + sec.charCodeAt() - 64
      }
      return index
    },
    // 加载表格头部内容, 其他单个内容
    loadHeaderInfo(record) {
      // 标题
      this.setCellValue(0, this.getCharIndex('A'), record.title)

      let rowIndex = 3
      // 填写周期
      this.setCellValue(rowIndex, this.getCharIndex('C'), record.period)
      // 日期
      this.setCellValue(rowIndex, this.getCharIndex('F'), '日期：' + record.reportDate)
      // 作业天数
      this.setCellValue(rowIndex, this.getCharIndex('G'), '作业天数：' + record.dayIndex)
      // 车号
      this.setCellValue(rowIndex, this.getCharIndex('K'), record.trainNo)
      // 运行公里数
      this.setCellValue(rowIndex, this.getCharIndex('O'), record.mileage + 'km')
      // 填报车间
      this.setCellValue(rowIndex, this.getCharIndex('U'), record.workshop)
      // 填报人
      this.setCellValue(rowIndex, this.getCharIndex('AA'), record.userName)

      //工作故障情况
      this.setCellValue(32, this.getCharIndex('B'), record.toolFault)
      // 设备故障情况
      this.setCellValue(32, this.getCharIndex('R'), record.assetFault)
    },
    // 当日任务
    loadCurDayTask(tasks) {
      if (!tasks) {
        return
      }
      let startRow = 5,
        maxGroup = 9,
        taskCnt = tasks.length
      for (let i = 0; i < maxGroup; i++) {
        let task = {
          actContent: '',
          finished: false,
          finished_dictText: '',
          group: '',
          id: '',
          peoples: 0,
          planContent: '',
          remark: '',
          reportId: '',
          workTime: 0,
        }

        if (i < taskCnt) {
          task = tasks[i]
        }

        this.setCellValue(startRow, this.getCharIndex('C'), task.groupName)
        this.setCellValue(startRow, this.getCharIndex('F'), task.planContent || '无')
        this.setCellValue(startRow, this.getCharIndex('I'), task.actContent || '无')
        this.setCellValue(startRow, this.getCharIndex('P'), task.finished_dictText)
        this.setCellValue(
          startRow,
          this.getCharIndex('T'),
          task.peoples > 0 ? task.peoples + '人/' + task.workTime + '小时' : ''
        )
        this.setCellValue(startRow, this.getCharIndex('X'), task.remark || '-')

        startRow = startRow + 1
      }
    },
    setCellValue(row, col, value) {
      luckysheet.setCellValue(row, col, value, {
        success: () => {
          // console.log('成功在：%s 行 %s  列 插入: %s', row, col,value)
        },
      })
    },
    // 次日任务
    loadNexDayTask(tasks) {
      let startRow = 15,
        maxGroup = 9,
        taskCnt = tasks.length
      for (let i = 0; i < maxGroup; i++) {
        let task = {
          group: '',
          id: '',
          remark: '',
          reportId: '',
          tempWork: '',
          trackContent: '',
          workContent: '',
        }
        if (i < taskCnt) {
          task = tasks[i]
        }

        this.setCellValue(startRow, this.getCharIndex('C'), task.groupName)
        this.setCellValue(startRow, this.getCharIndex('F'), task.workContent)
        this.setCellValue(startRow, this.getCharIndex('M'), task.trackContent + '\n' + task.tempWork)
        // this.setCellValue(startRow, this.getCharIndex('S'), task.tempWork)
        this.setCellValue(startRow, this.getCharIndex('X'), task.remark)
        startRow = startRow + 1
      }
    },
    // 委外松下情况
    loadOutInfo(outs) {
      let startRow = 25,
        maxGroup = 8,
        taskCnt = outs.length
      for (let i = 0; i < maxGroup; i++) {
        let task = {
          assetTypeId: '',
          assetTypeName: '',
          id: '',
          receiveDesc: '',
          reportId: '',
          sendDesc: '',
        }

        if (i < taskCnt) {
          task = outs[i]
        }

        if (i == 4) {
          startRow = 25
        }

        if (i < 4) {
          this.setCellValue(startRow, this.getCharIndex('C'), task.assetTypeName)
          this.setCellValue(startRow, this.getCharIndex('F'), task.sendDesc)
          this.setCellValue(startRow, this.getCharIndex('K'), task.receiveDesc)
        } else {
          this.setCellValue(startRow, this.getCharIndex('P'), task.assetTypeName)
          this.setCellValue(startRow, this.getCharIndex('U'), task.sendDesc)
          this.setCellValue(startRow, this.getCharIndex('Z'), task.receiveDesc)
        }

        startRow = startRow + 1
      }
    },
    // 当日故障信息
    loadDailyFault(faults) {
      let startRow = 30,
        max = 4,
        faultCnt = faults.length - 1
      for (let i = 0; i < max; i++) {
        let fault = {
          dutyEngineer: '',
          faultDesc: '',
          faultTime: '',
          handleDesc: '',
          id: '',
          isHandle: 0,
          remark: '',
          reportId: '',
          reportUser: '',
        }
        if (i <= faultCnt) {
          fault = faults[i]
        }
        this.setCellValue(startRow, this.getCharIndex('B'), i + 1)
        this.setCellValue(startRow, this.getCharIndex('C'), fault.faultTime)
        this.setCellValue(startRow, this.getCharIndex('E'), fault.reportUser)
        this.setCellValue(startRow, this.getCharIndex('G'), fault.faultDesc)
        if (fault.reportUser) {
          this.setCellValue(startRow, this.getCharIndex('P'), fault.isHandle == 0 ? '否' : '是')
        }else {
           this.setCellValue(startRow, this.getCharIndex('P'), '')
        }

        this.setCellValue(startRow, this.getCharIndex('R'), fault.dutyEngineer)
        this.setCellValue(startRow, this.getCharIndex('T'), fault.handleDesc)
        this.setCellValue(startRow, this.getCharIndex('Z'), fault.remark)
        startRow = startRow + 1
      }
    },
    downloadExcel() {
      exportExcel(luckysheet.getAllSheets(), this.queryParam.day + '  ' + this.queryParam.trainNo + ' 生产日报')
    },
    setSelectValue() {
      let select = luckysheet.getRange()
      if (select && select.length > 0) {
        let row = select[0].row[0]
        let col = select[0].column[0]

        luckysheet.setCellValue(5, 5, '插入', {
          success: () => {
            console.log('成功在行：%s 列: %s 插入', row, col)
          },
        })
      }
    },
  },
}
</script>

<style scoped>
</style>