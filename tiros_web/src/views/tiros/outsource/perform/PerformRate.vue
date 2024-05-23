<template>
  <a-modal
    title="评分管理"
    :width="'85%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    :closable="true"
    :footer="null"
    :destroyOnClose="true"
  >
  <div class="">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="评分项">
              <j-dict-select-tag
                v-model="queryParam.rateingItem"
                placeholder="请选择"
                dictCode="bu_rateing_item"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="12">
            <a-form-item label="评分日期">
              <a-range-picker
                v-model="rateDate"
                :ranges="dateRanges"
                @change="onDateChange"
              />
            </a-form-item>
          </a-col>
          <a-col :md="10" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
              <a-button @click="findList">查询</a-button>
              <a-button @click="add">新建</a-button>
              <a-button :disabled="records.length != 1" @click="handleEdit(records[0])">编辑</a-button>
              <a-button :disabled="records.length < 1" @click="deleteRecord">删除</a-button>
              <a-button @click="showPerformRecords">逾期记录</a-button>
              <a-button @click="showPerformFaultRecords">故障记录</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div >
      <vxe-table
        border
        ref="listTable"
        style="height: calc(100vh - 330px)"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{trigger: 'row', highlight: true, range: true}"
        :edit-config="{trigger: 'manual', mode: 'row'}"
        show-footer
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :footer-span-method="footerColspanMethod"
        :footer-method="sumMethod"
      >
        <vxe-table-column type="checkbox" width="8%" ></vxe-table-column>
        <vxe-table-column type="seq" title="序号" width="8%"></vxe-table-column>
        <vxe-table-column field="rateingItem_dictText" title="评分项" width="20%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="score" title="分值" width="10%"
                          align="right" header-align="center"></vxe-table-column>
        <vxe-table-column field="rateDate" title="评分日期" width="10%"></vxe-table-column>
        <vxe-table-column field="rateDesc" title="评分说明" width="34%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column  title="附件" width="10%">
          <template v-slot="{ row }">
            <a @click.stop="handleDoc(row)" v-if="row&&row.annexes.length>0">查看</a>
          </template>
        </vxe-table-column>
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>
    <perform-rate-item-module ref="modalForm" @ok="loadData()"
                              :contractId="queryParam.contractId"></perform-rate-item-module>
    <rate-doc-modal ref="docModal" ></rate-doc-modal>
    <PerformRecords ref="performRecordsModal"></PerformRecords>
    <PerformFaultRecords ref="performFaultRecordsModal"></PerformFaultRecords>
  </div>
  </a-modal>
</template>

<script>
import { delContractInfo, delRate, getPerformList, getRateList, getResource } from '../../../../api/tirosOutsourceApi'
import moment from 'moment'
import XEUtils from 'xe-utils'
import PerformRateItemModule from '../modules/PerformRateItemModule'
import { everythingIsEmpty } from '@/utils/util'
import RateDocModal from '@views/tiros/outsource/modules/RateDocModal'
import PerformRecords from '@views/tiros/outsource/perform/PerformRecords'
import PerformFaultRecords from '@views/tiros/outsource/perform/PerformFaultRecords'

export default {
  name: 'PerformRate',
  components: { PerformRateItemModule,RateDocModal, PerformRecords, PerformFaultRecords },
  data () {
    return {
      records:[],
      confirmLoading:false,
      visible: false,
      rateDate: null,
      queryParam: {
        startTime: null,
        endTime: null,
        contractId: '',
        rateingItem: null,
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      dateRanges:
        {
          最近一周: [moment().startOf('day').subtract(1, 'weeks'), moment()],
          最近一个月: [moment().startOf('day').subtract(30, 'day'), moment()],
          最近三个月: [moment().startOf('day').subtract(90, 'day'), moment()]
        }
    }
  },
  methods: {
    checkboxChange(e){
      this.records = e.records;
    },
    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
    },
    show(contractId,supplierId){
       this.visible = true;
      this.queryParam.contractId = contractId
      this.queryParam.supplierId = supplierId
      this.findList()
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
    findList () {
      getRateList(this.queryParam).then((res) => {
        this.loading = false
        if(res.success) {
          this.totalResult = res.result.total
          this.tableData = res.result.records
          this.records = [];
        }
      })
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData () {
      this.findList()
      this.$emit('load')
    },
    sumMethod ({ columns, data }) {
      const footerData = [
        columns.map((column, _columnIndex) => {
          if (_columnIndex === 0) {
            return '合计'
          }
          return '平均得分：' + XEUtils.mean(data, 'score').toFixed(2) + '分'
          return null
        })]
      return footerData
    },
    footerColspanMethod ({ $rowIndex, _columnIndex }) {
      if ($rowIndex === 0) {
        if (_columnIndex === 1) {
          return {
            rowspan: 1,
            colspan: 7
          }
        } else if (_columnIndex !== 0) {
          return {
            rowspan: 0,
            colspan: 0
          }
        }
      }
    },
    add () {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleEdit (record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
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
            delRate('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handleDoc(row){
        this.$refs.docModal.show(row)
    },
    showPerformRecords(){
      this.$refs.performRecordsModal.showModal(this.queryParam.supplierId)
    },
    showPerformFaultRecords(){
      this.$refs.performFaultRecordsModal.showModal(this.queryParam.supplierId)
    }
  }
}
</script>

<style scoped lang="less">

</style>