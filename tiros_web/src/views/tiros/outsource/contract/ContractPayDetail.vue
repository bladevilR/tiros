<template>
    <div class="bodyWrapper">
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="24">
            <a-col :md="6" :sm="24">
              <a-form-item label="支付标题">
                <a-input placeholder="请输入名称" v-model="queryParam.name"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="支付日期">
                <a-range-picker
                  v-model="payDate"
                  :ranges="dateRanges"
                  @change="onDateChange"
                />
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
               <a-button @click="findList">查询</a-button>
               <a-button type="primary" @click="handleAdd" >新增</a-button>
              <a-button   @click="deleteRecord">删除</a-button>
              </a-space>
            </span>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <div>
        <vxe-table
          class="table-footer"
          border
          max-height="78%"
          style="height: calc(100vh - 240px)"
          ref="listTable"
          :align="allAlign"
          :data="tableData"
          show-overflow="tooltip"
          :edit-config="{trigger: 'manual', mode: 'row'}"
          show-footer
          :footer-span-method="footerColspanMethod"
          :footer-cell-class-name="footerCellClassName"
          :footer-method="sumMethod"
        >
          <vxe-table-column type="checkbox" width="40" align="center"></vxe-table-column>
          <vxe-table-column field="payNo" title="序号" width="8%"></vxe-table-column>
          <vxe-table-column field="payDesc" title="支付标题" width="15%" header-align="center"
                            align="left"></vxe-table-column>
          <vxe-table-column field="amount" title="支付金额" width="10%" header-align="center" align="right">
            <template v-slot="{ row }">
              <span>{{ row.amount + '万' }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="dealMan" title="办理人" width="10%"></vxe-table-column>
          <vxe-table-column field="payDate" title="支付日期" width="10%"></vxe-table-column>
          <vxe-table-column field="leftover" title="剩余支付" width="10%" header-align="center" align="right">
            <template v-slot="{ row }">
              <span>{{ row.leftover + '万' }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="remark" title="备注" width="20%" header-align="center" align="left"></vxe-table-column>
          <vxe-table-column title="操作" >
            <template v-slot="{ row }">
              <a @click="handleEdit(row)">编辑</a>
            </template>
          </vxe-table-column>
        </vxe-table>
      </div>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
      <contract-pay-item-modal ref="modalForm" :contractId="queryParam.contractId"
                               @ok="loadData()"></contract-pay-item-modal>
    </div>
</template>


<script>
import moment from 'moment'
import { delContractPay, getContractPayList } from '../../../../api/tirosOutsourceApi'
import ContractPayItemModal from '../modules/ContractPayItemModule'
import XEUtils, { arrayMin } from 'xe-utils'
import { everythingIsEmpty } from '@/utils/util'

export default {
  components: { ContractPayItemModal },
  name: 'ContractPay',
  props:['detail'],
  data () {
    return {
      tableData: [],
      allAlign: 'center',
      totalResult: 0,
      amount: 0,
      payDate: null,
      back: true,
      dateRanges:
        {
          最近一周: [moment().startOf('day').subtract(1, 'weeks'), moment()],
          最近一个月: [moment().startOf('day').subtract(30, 'day'), moment()],
          最近三个月: [moment().startOf('day').subtract(90, 'day'), moment()]
        },
      queryParam: {
        contractId: '',
        name: '',
        startTime: null,
        endTime: null,
        pageNo: 1,
        pageSize: 10
      },
    }
  },
  created () {
    this.queryParam.contractId=this.detail.id
    this.amount=this.detail.amount
    this.findList();
  },
  methods: {
    loadData () {
      this.findList()
    },
    findList () {
      this.confirmLoading = true
      getContractPayList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.confirmLoading = false
        this.tableData = res.result.records
      })
    },
    onDateChange (value, dateString) {
      if (everythingIsEmpty(dateString[0]) && everythingIsEmpty(dateString[1])) {
        this.queryParam.startTime = ''
        this.queryParam.endTime = ''
      } else {
        this.queryParam.startTime = moment(value[0]).format('YYYY-MM-DD')
        this.queryParam.endTime = moment(value[1]).format('YYYY-MM-DD')
      }
    },

    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleEdit (record) {
      let leftover = record.leftover + record.amount
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.setMaxAmount(leftover)
      this.$refs.modalForm.title = '编辑'
    },
    handleAdd () {
      this.$refs.modalForm.add()
      let leftover
      if (this.tableData.length > 0) {
        leftover = this.tableData[this.tableData.length - 1].leftover
      } else {
        leftover = this.amount
      }
      this.$refs.modalForm.setMaxAmount(leftover)
      this.$refs.modalForm.title = '新增'
    },
    deleteRecord () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delContractPay('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    sumMethod ({ columns, data }) {
      const footerData = [
        columns.map((column, _columnIndex) => {
          if (_columnIndex === 0) {
            return '合计'
          }
          if (['payNo'].includes(column.property)) {
            let over = '剩余支付：'
            if (data.length === 1) {
              over = over + data[0].leftover + '万元'
            } else if (data.length > 1) {
              // over = over + Math.min.apply(Math, data.map(function (o) {
              //   return o.leftover
              // })) + '万元'
              
              over = over + data[data.length - 1].leftover + '万元'
            } else {
              over += this.amount + '万元'
            }
            return '已支付：' + XEUtils.sum(data, 'amount') + '万元' + '；' + over
          }
          return null
        })]
      return footerData
    },
    footerColspanMethod ({ $rowIndex, _columnIndex }) {
      if ($rowIndex === 0) {
        if (_columnIndex === 1) {
          return {
            rowspan: 1,
            colspan: 8
          }
        } else if (_columnIndex !== 0) {
          return {
            rowspan: 0,
            colspan: 0
          }
        }
      }
    },
    footerCellClassName ({ $rowIndex, column, columnIndex }) {
      if (columnIndex === 0) {
        if ($rowIndex === 0) {
          return 'col-red'
        }
      }
    },
  }
}

</script>
<style lang="less">
.table-footer .col-red {
  background-color: #2db7f5;
  color: #fff;
}
</style>