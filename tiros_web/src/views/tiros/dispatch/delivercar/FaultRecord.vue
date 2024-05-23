<template>
  <div class='bodyWrapper'>
    <div class='table-page-search-wrapper'>
      <a-form layout='inline'>
        <a-row :gutter='24'>
          <a-col :md='6' :sm='24'>
            <a-form-item label='故障'>
              <a-input placeholder='故障编号或描述' v-model='queryParam.searchText' allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md='6' :sm='24'>
            <a-form-item label='发现日期'>
              <a-date-picker v-model='date'></a-date-picker>
            </a-form-item>
          </a-col>
          <a-col :md='6' :sm='8'>
            <span style='float: left;overflow: hidden;' class='table-page-search-submitButtons'>
              <a-space>
              <a-button @click='findList'>查询</a-button>
                </a-space>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>
    <a-spin :spinning='loading'>
      <div style='height: calc(100vh - 290px)'>
        <vxe-table
          border
          max-height='80%'
          style='height: calc(100vh - 340px)'
          ref='listTable'
          :align='allAlign'
          :data='tableData'
          show-overflow='tooltip'
          :edit-config="{trigger: 'manual', mode: 'row'}"
        >
          <vxe-table-column type='checkbox' width='40'></vxe-table-column>
          <vxe-table-column field='faultSn' title='故障编号' width='140'></vxe-table-column>
          <vxe-table-column field='fromType_dictText' title='来源' width='120'></vxe-table-column>
          <vxe-table-column field='sysName' title='故障系统' width='120'></vxe-table-column>
          <vxe-table-column field='faultAssetName' title='故障部件' width='120'></vxe-table-column>
          <vxe-table-column field='faultDesc' title='故障描述' min-width='120' header-align='center'
                            align='left'></vxe-table-column>
          <vxe-table-column field='handleTime' title='处理日期' min-width='80'></vxe-table-column>
          <vxe-table-column field='reportUserId' title='提报人员' width='120'></vxe-table-column>
          <vxe-table-column field='reportTime' title='提报日期' min-width='80'></vxe-table-column>
          <vxe-table-column field='status_dictText' title='状态' width='80'></vxe-table-column>
        </vxe-table>
        <vxe-pager
          perfect
          :current-page.sync='queryParam.pageNo'
          :page-size.sync='queryParam.pageSize'
          :total='totalResult'
          :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
          @page-change='handlePageChange'
        ></vxe-pager>
      </div>
    </a-spin>
  </div>
</template>

<script>
import { getFaultRecord } from '@api/tirosProductionApi'
import moment from 'moment'

export default {
  name: 'FaultRecord',
  props: ['trainNo'],
  data() {
    return {
      date: null,
      queryParam: {
        trainNo: this.trainNo,
        searchText: '',
        happenTime: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      loading: false
    }
  },
  created() {
    this.findList()
  },
  methods: {
    findList() {
      this.loading = true
      if (this.date) {
        this.queryParam.happenTime = moment(this.date).format('YYYY-MM-DD')
      } else {
        this.queryParam.happenTime = ''
      }
      getFaultRecord(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.tableData = res.result.records
        }
      }).finally(() => this.loading = false)
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    }
  }
}
</script>

<style scoped>

</style>