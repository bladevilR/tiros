<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="4" :sm="12">
            <a-form-item label="表单">
              <a-input placeholder="输入表单编码或者名称" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="线路">
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
           <a-col :md="4" :sm="24">
            <a-form-item label="车辆">
                <j-dict-select-seach-tag
                    :triggerChange="true"
                    v-model="queryParam.trainNo"
                    dictCode="bu_train_info,train_no,train_no"
                />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="所属修程">
              <j-dict-select-tag
                v-model="queryParam.repairProgramId"
                placeholder="请选择"
                dictCode="bu_repair_program,name,id"
              />
            </a-form-item>
          </a-col>
           <a-col :md="6" :sm="12">
            <a-form-item label="时间">
              <a-range-picker format="YYYY-MM-DD" :placeholder="['开始时间', '结束时间']"  @change="onDateChange" :defaultPickerValue="defaultDateRange" />
            </a-form-item>
          </a-col>
          <a-col :md="2" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div :style="tableHeight.top">
      <vxe-table
        border
        :align="allAlign"
        ref="listTable"
        :max-height="tableHeight.size"
        :style="tableHeight.bottom"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column
          field="sysName"
          title="系统"
          width="100"
          header-align="center"
          align="right"
        ></vxe-table-column>
        <vxe-table-column field="workstationName" title="工位" width="100"></vxe-table-column>
        <vxe-table-column field="code" title="物资编码" width="180"></vxe-table-column>
        <vxe-table-column field="name" title="物资名称" width="180"></vxe-table-column>
        <vxe-table-column field="spec" title="物资描述" width="220"></vxe-table-column>
        <vxe-table-column field="materialTypeCategory" title="属性" width="100"></vxe-table-column>
        <vxe-table-column field="unit" title="单位" width="120"></vxe-table-column>
        <vxe-table-column field="needAmount" title="每列所需" width="120"></vxe-table-column>
        <vxe-table-column
          field="locationName"
          title="安装部位"
          min-width="100"
          header-align="center"
          align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="remark"
          title="备注"
          min-width="100"
          header-align="center"
          align="center"
        ></vxe-table-column>
        <!-- <vxe-table-column title="操作" width="80" fixed="right">
          <template v-slot="{ row }">
            <a @click.stop="handleEdit(row)">编辑</a>
          </template>
        </vxe-table-column> -->
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
    <will-change-item-modal ref="modalForm" @ok="loadData()"></will-change-item-modal>
    <borrow-detail-modal ref="detailForm"></borrow-detail-modal>
  </div>
</template>


<script>
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { getWillChangeList } from '@/api/tirosMaterialApi'
// import { getWillChangeList } from '@/api/tirosDispatchApi'

export default {
  name: 'List',
  components: { LineSelectList },
  data() {
    return {
      queryParam: {
        searchText: '',
        lineId: '',
        programId: '',
        systemId: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      tableHeight: {
        top: 'height: calc(100vh - 225px)',
        bottom: 'height: calc(100vh - 225px)',
        size: '100%',
      },
    }
  },

  created() {
    this.findList()
  },
  methods: {
      onDateChange(value, dateString){
      this.queryParam.startDate=dateString[0];
        this.queryParam.endDate=dateString[1];
    },
    findList() {
      getWillChangeList(this.queryParam).then((res) => {
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
  },
}
</script>