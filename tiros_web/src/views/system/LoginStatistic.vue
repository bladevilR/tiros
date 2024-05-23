<template>
  <div id='MonitorContent'>
    <div class='table-page-search-wrapper'>
      <a-form layout='inline' @keyup.enter.native='findList'>
        <a-row :gutter='24' type='flex'>
          <a-col :md='4' :sm='24'>
            <a-form-item label='日期范围:'>
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
            <span class='table-page-search-submitButtons'>
              <a-space>
                <a-button @click='findList'>查询</a-button>
                <a-button :loading="exportLoading" @click="exportLoginStatisticEXFile">导出</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div class='info-wrapper info-top-wrapper' style='margin-top: 18px'>
      <h4>系统访问统计</h4>
      <vxe-table
        border
        resizable
        auto-resize
        ref='listTable'
        :align='allAlign'
        :data='tableData'
        show-overflow='tooltip'
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
      >
        <vxe-table-column field='year' title='年' width='100' />
        <vxe-table-column field='month' title='月' width='100' />
        <vxe-table-column field='weekOfMonth' title='周' width='100' />
        <vxe-table-column field='dateRange' title='日期范围' min-width='300' align='left' header-align='center' />
        <vxe-table-column field='userCount' title='用户数' width='100' />
        <vxe-table-column field='loginCount' title='登录次数' width='100' />
        <vxe-table-column field='useTimeCount' title='使用次数' width='100' />
      </vxe-table>
    </div>
  </div>
</template>

<script>
import { listLoginStatistic, exportLoginStatisticExcel } from '@api/api'

export default {
  name: 'LoginStatistic',
  components: {},
  data() {
    return {
      mode: ['month', 'month'],
      monthVal: [this.$moment(new Date()).format('YYYY-MM'), this.$moment(new Date()).format('YYYY-MM')],
      queryParam: {
        startMonth: '',
        endMonth: ''
      },
      tableData: [],
      allAlign: 'center',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 19 }
      },
      exportLoading: false,
    }
  },
  created() {
    this.findList()
  },
  watch: {},
  methods: {
    handlePanelMonthChange(value, mode) {
      this.monthVal = value
      this.queryParam.startMonth = this.$moment(this.monthVal[0]).format('YYYY-MM')
      this.queryParam.endMonth = this.$moment(this.monthVal[1]).format('YYYY-MM')
      console.log('this.queryParam.startMonth ' + this.queryParam.startMonth)
      console.log('this.queryParam.startMonth ' + this.queryParam.endMonth)
    },
    findList() {
      if (!this.queryParam.startMonth) {
        this.queryParam.startMonth = this.$moment(new Date()).format('YYYY-MM')
      }
      if (!this.queryParam.endMonth) {
        this.queryParam.endMonth = this.$moment(new Date()).format('YYYY-MM')
      }
      listLoginStatistic(this.queryParam).then((res) => {
        if (res.success) {
          this.loading = false
          this.tableData = res.result
        }
      })
    },
    exportLoginStatisticEXFile() {
      if (!this.queryParam.startMonth) {
        this.queryParam.startMonth = this.$moment(new Date()).format('YYYY-MM')
      }
      if (!this.queryParam.endMonth) {
        this.queryParam.endMonth = this.$moment(new Date()).format('YYYY-MM')
      }

      // 导入
      this.exportLoading = true;
      exportLoginStatisticExcel(this.queryParam).then((data) => {
        if (!data) {
          this.$message.warning('文件下载失败')
          return
        }
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(new Blob([data], { type: 'application/vnd.ms-excel' }), fileName + '.xls')
        } else {
          let url = window.URL.createObjectURL(new Blob([data], { type: 'application/vnd.ms-excel' }))
          let link = document.createElement('a')
          link.style.display = 'none'
          link.href = url

          link.setAttribute('download', '架大修系统访问统计.xls')
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link) //下载完成移除元素
          window.URL.revokeObjectURL(url) //释放掉blob对象
        }
        this.exportLoading = false;
      }).catch((err) => {
        this.exportLoading = false;
      });
    },
  },
  computed: {}
}
</script>

<style scoped>
.info-wrapper {
  border: 1px solid #eee;
  position: relative;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 10px;
}

.info-wrapper h4 {
  position: absolute;
  top: -14px;
  padding: 1px 8px;
  margin-left: 16px;
  color: #777;
  border-radius: 2px 2px 0 0;
  background: #fff;
  font-size: 16px;
  width: auto;
}
</style>