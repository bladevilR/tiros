<template>
  <div class="bodyWrapper">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="工单">
              <a-input placeholder="工单编号或名称" v-model="queryParam.searchText"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择"
                dictCode="bu_group_order_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="工单日期">
              <a-date-picker v-model="date" :format="'YYYY-MM-DD'"></a-date-picker>
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
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="orderCode" title="工单编号" width="120"></vxe-table-column>
        <vxe-table-column field="orderName" title="工单名称" min-width="120"></vxe-table-column>
        <vxe-table-column field="orderType_dictText" title="工单类型" width="100">
          <template v-slot='{ row }'>
            {{row.orderType_dictText || row.orderType}}
          </template>
        </vxe-table-column>
        <vxe-table-column field="groupName" title="作业工班" width="100">
          <template v-slot='{ row }'>
            {{row.groupName || row.groupId}}
          </template>
        </vxe-table-column>
        <vxe-table-column field="lineName" title="线路" width="100"></vxe-table-column>
        <vxe-table-column field="trainName" title="车号" width="100"></vxe-table-column>
        <vxe-table-column field="startTime" title="计划开始" width="100"></vxe-table-column>
        <vxe-table-column field="planName" title="作业任务" min-width="140" align="left"
                          header-align="center"></vxe-table-column>
        <vxe-table-column field="orderStatus_dictText" title="工单状态" width="80"></vxe-table-column>
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
import { getWorkRecord } from '@api/tirosProductionApi'
import moment from 'moment'

export default {
  name: 'WorkRecord',
  props: {
    trainNo: {
      type: String,
      default: ''
    },
    structureDetailId: {
      type: String,
      default: ''
    },
  },
    data () {
      return {
        date:null,
        queryParam: {
          searchText: '',
          status: '',
          workOrderDate: '',
          trainNo: this.trainNo,
          structureDetailId: '',
          pageNo: 1,
          pageSize: 10
        },
        totalResult: 0,
        allAlign: 'center',
        tableData: [],
        loading:false
      }
    },
    watch: {
      structureDetailId: {
        immediate: true,
        handler (id) {
          this.queryParam.structureDetailId = id !== this.trainNo ? id : ''
          this.findList()
        }
      },
     trainNo: {
        immediate: true,
        handler (id) {
          this.queryParam.trainNo = id
          this.findList()
        }
      }
    },
    methods: {
      findList () {
        this.loading =true
        if(this.date){
          this.queryParam.workOrderDate = moment(this.date).format('YYYY-MM-DD')
        }else {
          this.queryParam.workOrderDate = ''
        }
        getWorkRecord(this.queryParam).then((res) => {
          this.totalResult = res.result.total
          this.tableData = res.result.records
        }).finally(()=>this.loading = false)
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