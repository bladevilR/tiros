<template>
  <a-modal
    :width="'85%'"
    title=" 厂商故障记录"
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
            <vxe-table-column type='checkbox' width='40'></vxe-table-column>
              <vxe-table-column field='faultSn' title='故障编号' width='140'></vxe-table-column>
              <vxe-table-column field='fromType_dictText' title='来源' width='120'></vxe-table-column>
              <vxe-table-column field='lineName' title='线路' width='100'></vxe-table-column>
              <vxe-table-column field='trainNo' title='车号' width='100'></vxe-table-column>
              <vxe-table-column field='sysName' title='故障系统' min-width='120'></vxe-table-column>
              <vxe-table-column field='faultAssetName' title='故障部件' min-width='120'></vxe-table-column>
              <vxe-table-column field='faultDesc' title='故障描述' min-width='140' align='left'
                                header-align='center'></vxe-table-column>
              <vxe-table-column field='happenTime' title='发现时间' width='100'></vxe-table-column>
              <vxe-table-column field='reportUserName' title='提报人员' width='100'></vxe-table-column>
              <vxe-table-column field='reportTime' title='提报日期' width='100'></vxe-table-column>
              <vxe-table-column field='status_dictText' title='状态' width='80'></vxe-table-column>
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

import { getFaultPage } from '@api/tirosOutsourceApi'

export default {
  name: 'PerformFaultRecords',
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
      getFaultPage(this.queryParam).then((res) => {
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