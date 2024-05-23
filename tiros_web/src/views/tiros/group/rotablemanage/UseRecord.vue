<template>
  <div class="bodyWrapper">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="更换日期">
              <a-date-picker v-model="date" ></a-date-picker>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="车号">
              <j-dict-select-seach-tag
                v-model="queryParam.trainNo"
                placeholder="请选择"
                dictCode="bu_train_info,train_no,train_no"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
              <a-button @click.stop="findList">查询</a-button>
                </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 180px)">
      <vxe-table
        border
        max-height="90%"
        style="height: calc(100vh - 230px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="assetNo" title="物资编码" min-width="120"></vxe-table-column>
        <vxe-table-column field="assetName" title="部件名称" min-width="120"></vxe-table-column>
        <vxe-table-column field="assetCode" title="资产编号" min-width="120"></vxe-table-column>
        <vxe-table-column field="manufNo" title="出厂编号" min-width="120"></vxe-table-column>
        <vxe-table-column field="exchangeTime" title="更换日期" width="100"></vxe-table-column>
        <vxe-table-column field="type_dictText" title="操作类型" width="100"></vxe-table-column>
        <vxe-table-column field="trainName" title="操作车号" width="100"></vxe-table-column>
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
  </div>
</template>

<script>
import { getRotableManageUseRecord } from '@api/tirosGroupApi'
import moment from 'moment'

export default {
  name: 'UseRecord',
  props: ['assetId'],
  data () {
    return {
      date:null,
      queryParam: {
        assetId: this.assetId,
        trainNo: '',
        changeDate: null,
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: []
    }
  },
  created() {
    this.findList()
  },
  methods: {
    findList () {
      if(this.date){
        this.queryParam.changeDate = moment(this.date).format('YYYY-MM-DD')
      }else {
        this.queryParam.changeDate = ''
      }
      getRotableManageUseRecord(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.tableData = res.result.records
        }
      })
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    }
  }
}
</script>

<style scoped>

</style>