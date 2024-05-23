<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="12">
            <a-form-item label="合同">
              <a-input placeholder="输入编码或者名称" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="12">
            <a-form-item label="线路">
              <line-select-list :trigger-change="true" v-model="queryParam.lineId"> </line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="12">
            <a-form-item label="厂商">
              <j-dict-select-tag
                :triggerChange="true"
                v-model="queryParam.supplierId"
                dictCode="bu_base_supplier,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="12">
            <a-form-item label="时间">
              <a-range-picker format="YYYY-MM-DD" :placeholder="['开始时间', '结束时间']" @change="onDateChange"  :defaultPickerValue="defaultDateRange" />
            </a-form-item>
          </a-col>
          <a-col :md="2" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div :style="tableHeight.top">
      <vxe-grid
        border
        :align="allAlign"
        ref="listTable"
        :max-height="tableHeight.size"
        :style="tableHeight.bottom"
        :data="tableData"
        show-overflow="tooltip"
        show-footer
        :footer-method="footerMethod"
        :columns="tableColumn"
      >
        <!-- <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column field="lineName" title="所属线路" width="120">
        </vxe-table-column>
        <vxe-table-column field="contractNo" title="合同编号" width="140"></vxe-table-column>
        <vxe-table-column field="contractName" title="合同名称" width="180"></vxe-table-column>
        <vxe-table-column field="supplier" title="厂商" width="140"></vxe-table-column>
        <vxe-table-column field="ticketAmount" title="开票金额" width="100"></vxe-table-column>
        <vxe-table-column field="amount" title="实付金额" width="120"></vxe-table-column>
        <vxe-table-column field="executeTaxRate" title="支付税率" width="120"></vxe-table-column>
        <vxe-table-column field="leftover" title="剩余额度" width="120"></vxe-table-column>
        <vxe-table-column field="qaMoney" title="质保额" width="120"></vxe-table-column>
        <vxe-table-column field="qaRate" title="质保比率" width="120"></vxe-table-column>
        <vxe-table-column field="leftQaMoney" title="剩余质保金" width="120"></vxe-table-column>
        <vxe-table-column field="payDate" title="支付日期" width="120"></vxe-table-column>
        <vxe-table-column field="dealDate" title="办理日期" width="120"></vxe-table-column> -->
        <!-- <vxe-table-column title="操作" width="80" fixed="right">
          <template v-slot="{ row }">
            <a @click.stop="handleEdit(row)">编辑</a>
          </template>
        </vxe-table-column> -->
      </vxe-grid>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>
  </div>
</template>


<script>
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { getPayRecordList } from '@api/tirosOutsourceApi'

export default {
  name: 'List',
  components: { LineSelectList },
  data() {
    return {
      queryParam: {
        endTime: '',
        startTime: '',
        searchText: '',
        lineId: '',
        supplierId: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      tableHeight: {
        top: 'height: calc(100vh - 280px)',
        bottom: 'height: calc(100vh - 225px)',
        size: '100%',
      },
      tableColumn: [
        { field: 'lineName', title: '所属线路', width: 120, fixed: 'left' },
        { field: 'contractNo', title: '合同编号', width: 140  ,align:"left", headerAlign:"center"},
        { field: 'contractName', title: '合同名称', width: 180, align:"left", headerAlign:"center"},
        { field: 'supplier', title: '厂商', width: 140 , align:"left", headerAlign:"center"},
        { field: 'ticketAmount', title: '开票金额', width: 100, align: 'right', formatter: this.setFormatter },
        { field: 'amount', title: '实付金额', width: 120, align: 'right', formatter: this.setFormatter },
        { field: 'executeTaxRate', title: '支付税率', width: 120, align: 'right', formatter: this.setFormatter2 },
        { field: 'leftover', title: '剩余额度', width: 120, align: 'right', formatter: this.setFormatter },
        { field: 'qaMoney', title: '质保额', width: 120, align: 'right', formatter: this.setFormatter },
        { field: 'qaRate', title: '质保比率', width: 120, align: 'right', formatter: this.setFormatter2 },
        { field: 'leftQaMoney', title: '剩余质保金', width: 120, align: 'right', formatter: this.setFormatter },
        { field: 'payDate', title: '支付日期', width: 120 },
        { field: 'dealDate', title: '办理日期', width: 120 },
      ],
    }
  },

  created() {
    this.findList()
  },
  methods: {
    setFormatter({ cellValue }) {
      if (!cellValue) {
        return '0万'
      }
      return `${cellValue}万`
    },
    setFormatter2({ cellValue }) {
      if (!cellValue) {
        return '0%'
      }
      return `${cellValue}%`
    },
    footerMethod({ columns, data }) {
      // console.log(columns)
      return [
        columns.map((column, columnIndex) => {
          // console.log(column)
          if (columnIndex === 0) {
            return '总额'
          }
          if (['ticketAmount', 'amount', 'qaMoney', 'leftQaMoney'].includes(column.property)) {
            return this.handleSum(data, column.property)
          }
          if (['leftover'].includes(column.property)) {
            return this.handleLeftover(data, column.property)
          }
          return '-'
        }),
      ]
    },
    handleSum(list, field) {
      var total = 0
      for (var index = 0; index < list.length; index++) {
        total += Number(list[index][field] || 0)
      }
      return `${total.toFixed(2)}万`
    },
    handleLeftover(list, field) {
      let total = 0
      let payRecords = {}
      list.forEach(element => {
        if (payRecords[element.contractId] === undefined) {
          payRecords[element.contractId] = element.leftover
        } else {
          if (element.leftover < payRecords[element.contractId]) {
            payRecords[element.contractId] = element.leftover
          }
        }
      });
      for (const key in payRecords) {
        if (Object.hasOwnProperty.call(payRecords, key)) {
          total += payRecords[key];
        }
      }
      return total + '万'
    },
    onDateChange(value, dateString) {
      this.queryParam.startTime = dateString[0]
      this.queryParam.endTime = dateString[1]
    },
    findList() {
      getPayRecordList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData() {
      this.findList()
      this.$emit('load')
    },
  },
}
</script>
<style  scoped>
.fontBlod {
  font-weight: 900;
}
</style>