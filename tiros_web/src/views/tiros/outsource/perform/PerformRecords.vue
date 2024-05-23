<template>
  <a-modal
    :width="'85%'"
    title=" 厂商逾期返厂记录"
    :visible="visible"
    dialogClass="fullDialog no-title no-footer"
    @cancel="handleCancel"
    :closable="true"
    :destroyOnClose="true"
  >
    <div class="bodyWrapper" style="padding: 8px; display: flex; flex-direction: column">
      <!-- <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="findList">
          <a-row :gutter="24" type="flex">
            <a-col :md="4">
              <a-form-item label="线路:">
                <line-select-list v-model="queryParam.lineId" @change="onLineChange"></line-select-list>
              </a-form-item>
            </a-col>
            <a-col :md="4" :sm="24">
              <a-form-item label="车辆:">
                <j-dict-select-seach-tag v-model="queryParam.trainNo" :dictCode="dictTrainStr" />
              </a-form-item>
            </a-col>
            <a-col :md="4" :sm="24">
              <a-space>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </a-col>
          </a-row>
        </a-form>
      </div> -->

      <div class="table-page-body-wrapper" style="height: calc(100vh - 160px)">
        <div class="table-body-context">
          <vxe-table border ref="listTable" align="center" height="auto" :data="tableData">
            <vxe-table-column field="lineName" title="线路" width="100"></vxe-table-column>
            <vxe-table-column field="trainNo" title="车号" width="100"></vxe-table-column>
            <vxe-table-column field="assetName" title="委外部件" min-width='120'></vxe-table-column>
            <vxe-table-column field="supplierName" title="委外厂商" min-width='120'></vxe-table-column>
            <vxe-table-column field="contractName" title="合同" min-width='120'></vxe-table-column>
            <vxe-table-column field="sendGroupName" title="送检班组" width="100"></vxe-table-column>
            <vxe-table-column field="transferDate" title="送检日期" width="100"></vxe-table-column>
            <vxe-table-column field="returnTime" title="计划返厂" width="100"></vxe-table-column>
            <vxe-table-column field="actReturnTime" title="实际返厂" width="100"></vxe-table-column>
            <vxe-table-column field="supervisor" title="监造人员" width="100"></vxe-table-column>
            <vxe-table-column field="status_dictText" title="状态" width="80"></vxe-table-column>
            <vxe-table-column field="delayDays" title="逾期天数" width="80" header-align='center'
                              align='right'></vxe-table-column>
            <vxe-table-column field="delayReason" title="逾期原因" min-width='120' header-align='center'
                              align='left'></vxe-table-column>
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
  </a-modal>
</template>

<script>
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

import { getPerformList } from '@api/tirosOutsourceApi'

export default {
  name: 'PerformRecords',
  components: { LineSelectList },
  data() {
    return {
      visible: false,
      dictTrainStr: '',
      tableData: [],
      queryParam: {
        lineId: null,
        trainNo: null,
        supplierId: '',
        pageNo: 1,
        pageSize: 10,
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
    showModal(supplierId){
      this.queryParam.supplierId = supplierId
      this.findList()
      this.visible = true
    },
    findList() {
      getPerformList(this.queryParam).then((res) => {
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
    handleCancel(){
      this.visible = false
    }
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