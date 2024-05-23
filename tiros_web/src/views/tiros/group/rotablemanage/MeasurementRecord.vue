<template>
  <div class="bodyWrapper">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="测点名称">
              <a-input placeholder="" v-model="queryParam.measureName" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
              <a-button @click="findList">查询</a-button>
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
        <vxe-table-column field="trainName" title="车辆" width="100"></vxe-table-column>
        <vxe-table-column field="trainAssetName" title="设备" width="120"></vxe-table-column>
        <vxe-table-column field="measureName" title="测量项名称" min-width="100"></vxe-table-column>
        <vxe-table-column field="values" title="测量值" width="100" align="right"
                          header-align="center"></vxe-table-column>
        <vxe-table-column field="workUser" title="测量人" width="100"></vxe-table-column>
        <vxe-table-column field="workTime" title="测量日期" min-width="80"></vxe-table-column>
        <vxe-table-column field="alertMessage" title="预警信息" min-width="100" header-align="center"
                          align="left"></vxe-table-column>
        <vxe-table-column field="alertReason" title="预警原因" min-width="100" header-align="center"
                          align="left"></vxe-table-column>
        <vxe-table-column field="fixValues" title="修正值" width="100" align="right"
                          header-align="center"></vxe-table-column>
        <vxe-table-column field="tools" title="测量器具" width="100"></vxe-table-column>
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
import { getMeasureRecord } from '@api/tirosProductionApi'

export default {
  name: 'MeasurementRecord',
  props: ['assetId'],
  data () {
    return {
      queryParam: {
        assetId: this.assetId,
        measureName: '',
        queryType:2,
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: []
    }
  },
  created () {
    this.findList()
  },
  methods: {
    findList () {
      getMeasureRecord(this.queryParam).then((res) => {
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