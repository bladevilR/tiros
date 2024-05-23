<template>
  <a-card :body-style="cardStyle">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="车号">
              <a-input placeholder="请输入车号" v-model="queryParam.trainNo"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button @click="findList">查询</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 250px)">
      <vxe-table
        border
        auto-resize
        ref="listTable"
        max-height="100%"
        style="height: calc(100vh - 250px)"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{trigger: 'row', highlight: true, range: true}"
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column title="工单号"  field="orderCode" min-width="130"></vxe-table-column>
        <vxe-table-column field="orderName" title="工单名称" min-width="130"></vxe-table-column>
        <vxe-table-column field="orderTaskName" title="工单任务" min-width="130"></vxe-table-column>
        <vxe-table-column field="lineName" title="线路" min-width="100"></vxe-table-column>
        <vxe-table-column field="trainNo" title="车号" width="100"></vxe-table-column>
        <vxe-table-column field="sysName" title="设备系统" width="100"></vxe-table-column>
        <vxe-table-column field="userDate" title="使用时间" width="100"></vxe-table-column>
        <vxe-table-column field="groupName" title="使用班组" width="100"></vxe-table-column>
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
  </a-card>

</template>

<script>
import { equipmentUseRecord } from '@api/tirosGroupApi'

export default {
name: "EquipmentUseRecord",
  props:['id'],
  data() {
    return {
      queryParam: {
        trainNo: '',
        id: '',
        pageNo: 1,
        pageSize: 10,
      },
      cardStyle: {
        'padding': '8px',
        'height': 'calc(100vh - 125px)',
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
    }
  },
  created() {
   // this.findList()
  },
  methods: {
    findList() {
      this.queryParam.id=this.id
      equipmentUseRecord(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.tableData = res.result.records
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
  }
}
</script>

<style scoped>

</style>