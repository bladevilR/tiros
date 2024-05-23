<template>
  <div class="bodyWrapper" style=" display: flex; flex-direction: column">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24" type="flex">
          <!-- <a-col :md="4">
            <a-form-item label="车辆段:">
              <j-dict-select-tag v-model="queryParam.depotId" dictCode="bu_mtr_depot,name,id" />
            </a-form-item>
          </a-col> -->
          <a-col :md="3">
            <a-form-item label="线路:">
              <line-select-list v-model="queryParam.lineId" @change="onLineChange"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="24">
            <a-form-item label="车辆:">
              <j-dict-select-seach-tag @focus="handleSysFocus" v-model="queryParam.trainNo" :dictCode="dictTrainStr" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="日期">
              <!-- <a-date-picker v-model="yearValue" @change="onDateChange" /> -->
              <a-range-picker v-model="dateTime" @change="onDateChange"  :defaultPickerValue="defaultDateRange" />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="统计维度">
              <a-select v-model="queryParam.type" @change="typeChange">
                <a-select-option :value="1">员工</a-select-option>
                <a-select-option :value="2">班组</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24" v-if="queryParam.type == 1">
            <a-form-item label="班组">
              <j-dict-select-tag
                v-model="queryParam.groupId"
                placeholder="请选择班组"
                :dictCode="dictGroupStr"
              />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="24">
            <a-space>
              <a-button @click="findList">查询</a-button>
              <a-button @click="downloadExcel">导出</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- <div class="table-page-body" :class="{ 'more-visible': moreVisible }">
      <div id="luckysheet" style="height: calc(100% - 0px); width: 100%"></div>
    </div> -->
    <div class="table-page-body-wrapper">
      <div class="table-body-context" style="height: calc(100vh - 220px)">
        <vxe-table border ref="listTable" 
          align="center" 
          height="auto" 
          :data="tableData">
          <vxe-table-column field="lineName" title="线路" min-width="80px" />
          <vxe-table-column field="trainNo" title="车辆" min-width="80px" />
          <vxe-table-column field="workNo" title="工号" min-width="100px" v-if="queryParam.type === 1" />
          <vxe-table-column field="userName" title="姓名" min-width="100px" v-if="queryParam.type === 1" />
          <vxe-table-column field="groupName" title="班组" min-width="140px" />
          <vxe-table-column field="repairTime" title="维修工时" width="120px" />
          <vxe-table-column field="faultTime" title="故障处理工时" width="120px" />
          <vxe-table-column field="totalTime" title="总计工时" width="120px" />
        </vxe-table>
      </div>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="page.totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>
  </div>
</template>

<script>
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import NaSelectDate from '@comp/tiros/Na-select-date'
import { getTrainCostKpiDetail, downloadTrainCostKpiDetail } from '@api/tirosReportApi'

export default {
  name: 'CostKpi',
  components: { LineSelectList, NaSelectDate },
  data() {
    return {
      dictGroupStr:
        "bu_mtr_workshop_group,group_name,id,workshop_id='" +
        this.$store.getters.userInfo.workshopId +
        "'|workshop_id",
      moreVisible: false,
      dictTrainStr: '',
      tableData: [],
      dateTime: [this.$moment(new Date()).startOf("month").format("YYYY-MM-DD"), this.$moment(new Date()).endOf('month').format("YYYY-MM-DD")],
      queryParam: {
        groupId: undefined,
        lineId: null,
        trainNo: '',
        pageNo: 1,
        pageSize: 10,
        startDate:this.$moment(new Date()).startOf("month").format("YYYY-MM-DD"),
        endDate: this.$moment(new Date()).endOf('month').format("YYYY-MM-DD"),
        type: 1,
      },
      page: {
        totalResult: 1,
      },
    }
  },
  mounted() {
    this.findList()
  },
  methods: {
    handleSysFocus() {
      if (!this.queryParam.lineId) this.$message.warn('请先选择线路!')
    },
    typeChange(){
      if(this.queryParam.type == 2){
        this.queryParam.groupId = undefined ;
      }
      this.findList()
    },
    findList() {
      getTrainCostKpiDetail(this.queryParam).then((res) => {
        if (res.success) {
          this.page.totalResult = res.result.total
          this.tableData = res.result.records
        } else {
          this.$message.error(res.message)
        }
      })
    },
    onLineChange(val, option) {
      this.dictTrainStr = "bu_train_info,train_no,train_no,status=1 and line_id='" + val + "'"
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
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
    downloadExcel() {
      downloadTrainCostKpiDetail(`人工成本明细-${this.$moment(new Date()).format('YYYYMMDDhhmmss')}.xls`, {
        lineId: this.queryParam.lineId,
        trainNo: this.queryParam.trainNo,
        startDate: this.queryParam.startDate,
        endDate: this.queryParam.endDate,
        type: this.queryParam.type,
      })
    },
  },
}
</script>

<style lang="less" scoped>
.bodyWrapper {
  .table-page-body {
    &.more-visible {
      height: calc(100% - 106px) !important;
    }
  }
}
</style>