<template>
  <div class="bodyWrapper">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="开口项">
              <a-input placeholder="请输入名称或编码" v-model="queryParam.searchText"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="线路">
              <line-select-list v-model="queryParam.lineId" @change="changeLine"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag :triggerChange="true" v-model="queryParam.status" dictCode="bu_repair_leave_status" />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-spin :spinning="loading">
      <div style="height: calc(100vh - 250px)">
        <vxe-table
          border
          max-height="90%"
          style="height: calc(100vh - 298px)"
          ref="listTable"
          :align="allAlign"
          :data="tableData"
          show-overflow="tooltip"
          :edit-config="{ trigger: 'manual', mode: 'row' }"
        >
          <vxe-table-column field="recordCode" title="编号" min-width="100">
            <!-- <template v-slot="{ row }">
           <a @click.stop="openItemDetail(row)">{{ row.recordCode }}</a>
         </template> -->
          </vxe-table-column>
          <vxe-table-column field="trainNo" title="所属车辆" width="80"></vxe-table-column>
          <vxe-table-column field="lineName" title="所属线路" width="80"></vxe-table-column>
          <vxe-table-column
            field="recordName"
            title="开口项目名称"
            min-width="100"
            align="left"
            header-align="center"
          ></vxe-table-column>
          <vxe-table-column
            field="recordDesc"
            title="内容描述"
            min-width="120"
            align="left"
            header-align="center"
          ></vxe-table-column>
          <vxe-table-column field="createTime" title="创建日期" width="100"></vxe-table-column>
          <vxe-table-column
            field="orderName"
            title="所属工单"
            min-width="120"
            align="left"
            header-align="center"
          ></vxe-table-column>
          <vxe-table-column
            field="orderTaskName"
            title="关联故障/任务"
            min-width="120"
            align="left"
            header-align="center"
          ></vxe-table-column>
          <vxe-table-column field="status_dictText" title="状态" width="80"></vxe-table-column>
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
    </a-spin>
  </div>
</template>

<script>
import { getOpenItemRecord } from '@api/tirosProductionApi'
import moment from 'moment'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'OpenItemRecord',
  components: { LineSelectList },
  props: {
    trainNo: {
      type: String,
      default: '',
    },
    structureDetailId: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      date: null,
      queryParam: {
        lineId: '',
        searchText: '',
        status: '',
        trainNo: this.trainNo,
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      loading: false,
    }
  },
  watch: {
    trainNo: {
      immediate: true,
      handler(id) {
        this.queryParam.trainNo = id
        this.findList()
      },
    },
  },
  methods: {
    changeLine(data, edit) {
      this.dictTrainStr = ''
      if (data) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + data + '|train_struct_id'
      }
    },
    findList() {
      this.loading = true
      getOpenItemRecord(this.queryParam)
        .then((res) => {
          this.totalResult = res.result.total
          this.tableData = res.result.records
        })
        .finally(() => (this.loading = false))
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
  },
}
</script>

<style scoped>
</style>