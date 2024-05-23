<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="部件">
              <a-input placeholder="请输入名称或编码" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="资产编码">
              <a-input placeholder="请输入资产编码" v-model="queryParam.assetCode" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24" >
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择"
                dictCode="bu_turnover_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24" >
            <a-form-item label="线路">
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button @click="findList">查询</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>
      <div style="height: calc(100vh - 225px)">
      <vxe-table
        border
        style="height: calc(100vh - 225px)"
        max-height="100%"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column title="物料名称" align="left" header-align="center" width="150">
          <template v-slot="{ row }">
            <a @click.stop="jumpInfo(row)">{{row.name}}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="materialCode" title="物料编码" width="120"></vxe-table-column>
        <vxe-table-column field="assetCode" title="资产编码" width="150"></vxe-table-column>
        <vxe-table-column field="manufNo" title="序列号" width="150"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="目前状态" width="120"></vxe-table-column>
        <vxe-table-column field="dutyUserName" align="left" header-align="center" title="保管人员" width="100"></vxe-table-column>
        <vxe-table-column field="groupName" align="left" header-align="center" title="所属工班" width="120"></vxe-table-column>
        <vxe-table-column field="price" align="right" header-align="center" title="单价" width="100"></vxe-table-column>
        <vxe-table-column field="systemName" title="所属系统" width="150"></vxe-table-column>
        <vxe-table-column field="currentLocation" align="left" header-align="center" title="当前位置" width="150"></vxe-table-column>
        <vxe-table-column field="outDate" title="出库日期" width="120"></vxe-table-column>
        <vxe-table-column field="outOrderNo" title="出库单号" width="150"></vxe-table-column>
        <vxe-table-column field="lineName" title="所属线路" width="100"></vxe-table-column>
        <vxe-table-column field="model" align="left" header-align="center" title="规格型号" width="150"></vxe-table-column>
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
      <rotable-manage-info ref="rotableInfo"></rotable-manage-info>
    </div>

  </div>
</template>

<script>
  import moment from 'moment'
  import { getRotablesManageList } from '@api/tirosGroupApi'
  import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
  import RotableManageInfo from '@views/tiros/group/rotablemanage/RotableManageInfo'

  export default {
    name: 'RotableManagePage',
    components:{LineSelectList,RotableManageInfo},
    data() {
      return {
        queryParam: {
          searchText: '',
          assetCode: '',
          status: '',
          lineId: '',
          pageNo: 1,
          pageSize: 10,
        },
        totalResult: 0,
        allAlign: 'center',
        tableData: [],
        count:''
      }
    },
    created() {
      this.findList()
    },
    methods: {
      findList() {
        this.loading = true
        getRotablesManageList(this.queryParam).then((res) => {
          this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result.records
        })
      },
      handlePageChange({ currentPage, pageSize }) {
        this.queryParam.pageNo = currentPage
        this.queryParam.pageSize = pageSize
        this.findList()
      },
      loadData() {
        this.findList()
      },
      jumpInfo(row) {
        this.$refs.rotableInfo.show(row.id)
      },
    }
  }
</script>

<style scoped>

</style>