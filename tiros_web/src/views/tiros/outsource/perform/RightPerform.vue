<template>
  <a-card id="performCard">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="部件名称">
              <a-input placeholder="部件名称" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="线路">
              <!--              <j-dict-select-tag
                              v-model="queryParam.lineId"
                              placeholder="请选择"
                              dictCode="bu_mtr_line,line_name,line_id"
                            />-->
              <line-select-list v-model="queryParam.lineId"  @change="changeLine"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="车号">
              <j-dict-select-seach-tag
                v-model="queryParam.trainNo"
                placeholder="请选择"
                :dictCode="dictTrainStr"
                @focus="handleSysFocus"
              />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag v-model="queryParam.status" placeholder="请选择" dictCode="bu_perform_status" />
            </a-form-item>
          </a-col>
          <a-col :md="2" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
          </a-col>
          <a-col :md="24" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handlePerformDetail" :disabled="!btnStatus.handlePerformDetail"
                  >执行管理</a-button
                >
                <a-button @click="handleDelayReason" :disabled="!btnStatus.handleDelayReason">设置逾期原因</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 282px)">
      <vxe-table
        border
        ref="listTable"
        max-height="100%"
        style="height: calc(100vh - 282px)"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        @checkbox-change="rowSelectChange"
        @checkbox-all="btnStatus.update()"
      >
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column field="lineName" title="线路" width="100"></vxe-table-column>
        <vxe-table-column field="trainNo" title="车号" width="100"></vxe-table-column>
        <vxe-table-column field="assetName" title="委外部件" min-width="120"></vxe-table-column>
        <vxe-table-column
          field="supplierName"
          align="left"
          header-align="center"
          title="委外厂商"
          min-width="120"
        ></vxe-table-column>
        <vxe-table-column
          field="contractName"
          align="left"
          header-align="center"
          title="合同"
          min-width="120"
        ></vxe-table-column>
        <vxe-table-column
          field="sendGroupName"
          align="left"
          header-align="center"
          title="送检班组"
          width="100"
        ></vxe-table-column>
        <vxe-table-column field="transferDate" title="送检日期" width="100"></vxe-table-column>
        <vxe-table-column field="returnTime" title="计划返厂" width="100"></vxe-table-column>
        <vxe-table-column field="actReturnTime" title="实际返厂" width="100"></vxe-table-column>
        <vxe-table-column
          field="supervisor"
          align="left"
          header-align="center"
          title="监造人员"
          width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="trainIndex"
          align="left"
          header-align="center"
          title="第几列车"
          width="12%"
        ></vxe-table-column>
        <vxe-table-column field="curTrain" title="执行车号" width="12%"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="80"></vxe-table-column>
        <vxe-table-column
          field="delayDays"
          title="逾期天数"
          width="80"
          header-align="center"
          align="right"
        ></vxe-table-column>
        <vxe-table-column
          field="delayReason"
          title="逾期原因"
          min-width="120"
          header-align="center"
          align="left"
        ></vxe-table-column>
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
    <perform-item-module ref="modalForm" @ok="loadData()"></perform-item-module>
    <perform-detail ref="performDetail" @ok="loadData()"></perform-detail>
  </a-card>
</template>

<script>
import { getPerformList } from '@api/tirosOutsourceApi'
import PerformItemModule from '../modules/PerformItemModule'
import PerformDetail from '@views/tiros/outsource/perform/PerformDetail'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'RightPerform',
  props: ['id', 'contractIds'],
  components: { PerformItemModule, PerformDetail, LineSelectList },
  data() {
    return {
      btnStatus: new TableBtnUtil(this, 'listTable', {
        attrs: [
          {
            key: 'handlePerformDetail',
          },
          {
            key: 'handleDelayReason',
          },
        ],
      }),
      dictTrainStr:'',
      queryParam: {
        lineId: '',
        searchText: '',
        trainNo: '',
        contactId: '',
        status: null,
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
    }
  },
  watch: {
    id: {
      immediate: true,
      handler(id) {
        this.queryParam.id = id
        this.findList()
      },
    },
    contractIds: {
      immediate: true,
      handler(contractIds) {
        this.findList()
      },
    },
  },
  created() {
    this.findList()
  },
  methods: {
    handleSysFocus() {
      if (!this.queryParam.lineId) this.$message.warn('请先选择线路!')
    },
    changeLine(data, edit) {
      this.dictTrainStr = ''
      if (data) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + data + '|train_struct_id'
      }
    },
    rowSelectChange() {
      this.btnStatus.update()
    },
    findList() {
      this.queryParam.id = null
      this.queryParam.contractIds = this.contractIds
      getPerformList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
        this.btnStatus.update()
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
    handleDelayReason() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
        let temp=selectRecords[0]
          if (temp.delayDays==null
            ||temp.delayDays === 0) {
            this.$message.warn('该委外设备没有逾期，无法设置逾期原因!')
          } else {
            let params = {
              id: temp.id,
              delayReason:temp.delayReason,
            }
            this.$refs.modalForm.edit(params)
            this.$refs.modalForm.title = '编辑'
          }
    },
    handlePerformDetail() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length === 1) {
        this.$refs.performDetail.show(selectRecords[0])
      } else if (selectRecords.length > 1) {
        this.$message.error('只能选中一条数据!')
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
  },
}
</script>

<style lang="less">
#performCard {
  .ant-card-body {
    padding: 10px;
    height: calc(100vh - 120px);
    overflow-y: auto;
  }

  .tableHeight {
    min-height: calc(100vh - 270px);
    // overflow-y: auto;
  }
}
</style>