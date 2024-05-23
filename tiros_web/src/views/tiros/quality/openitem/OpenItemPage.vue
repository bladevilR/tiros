<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="开口项">
              <a-input placeholder="请输入名称或编码" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="线路">
              <line-select-list v-model="queryParam.lineId" @change="changeLine"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="车辆">
              <j-dict-select-seach-tag
                :triggerChange="true"
                v-model="queryParam.trainNo"
                :dictCode="dictCodeStr"
                @focus="handleSysFocus"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag
                :triggerChange="true"
                v-model="queryParam.status"
                dictCode="bu_repair_leave_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="列计划">
              <a-select
                v-model="planName"
                placeholder="请选择列计划"
                :open="false"
                :showArrow="true"
                @focus="openTrainPlanModal"
                ref="planSelect"
              >
                <div slot="suffixIcon">
                  <a-icon
                    v-if="queryParam.planId"
                    @click.stop="clearValue"
                    type="close-circle"
                    style="padding-right: 3px"
                  />
                  <a-icon v-else type="ellipsis" />
                </div>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col>
            <span class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd" v-has="'zygcs:add'">新增</a-button>
                <a-button :disabled="!isCheckRow" @click="handleEdit" v-has="'zygcs:edit'">编辑</a-button>
                <a-button :disabled="!isCheckRows" @click="deleteRecord" v-has="'zygcs:del'">删除</a-button>
                <a-button :disabled="!isCheckRow" @click="closeRecord" v-has="'zygcs:close'">关闭</a-button>
                <a-button @click="findList">查询</a-button>
                <a-button @click="exportLeaveRecordEXFile">开口项台账导出</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 267px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(100vh - 267px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :edit-config="{trigger: 'manual', mode: 'row'}"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        @checkbox-change="rangeChange"
        @checkbox-all="rangeChange"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="recordCode" title="编号" min-width="100">
          <template v-slot="{ row }">
            <a @click.stop="openItemDetail(row)">{{ row.recordCode }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="trainNo" title="所属车辆" width="80"></vxe-table-column>
        <vxe-table-column field="lineName" title="所属线路" width="80"></vxe-table-column>
        <vxe-table-column field="recordName" title="开口项目名称" min-width="150" align="left"
                          header-align="center"></vxe-table-column>
        <vxe-table-column field="recordDesc" title="内容描述" min-width="120" align="left"
                          header-align="center"></vxe-table-column>
        <vxe-table-column field="createTime" title="创建日期" width="100"></vxe-table-column>
        <vxe-table-column field="orderName" title="所属工单" min-width="120" align="left"
                          header-align="center"></vxe-table-column>
        <vxe-table-column field="orderTaskName" title="关联故障/任务" min-width="120" align="left"
                          header-align="center"></vxe-table-column>
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
    <open-item-modal ref="modalForm" @ok="loadData()"></open-item-modal>
    <open-item-detail-modal ref="modalDetail"></open-item-detail-modal>
    <TrainPlanList ref="trainPlanModal" @ok="onSelectPlan"></TrainPlanList>

  </div>
</template>

<script>
import moment from 'moment'
import { closedOpenItem, delOpenItem, getOpenItemList,leaveRecordExport } from '@api/tirosQualityApi'
import OpenItemModal from '../modules/OpenItemModal'
import OpenItemDetailModal from '@views/tiros/quality/openitem/OpenItemDetailModal'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import TrainPlanList from '@views/tiros/common/selectModules/TrainPlanList'

export default {
  name: 'OpenItemPage',
  components: { OpenItemModal, OpenItemDetailModal, LineSelectList,TrainPlanList},
  data () {
    return {
      planName:'',
      queryParam: {
        planId:'',
        searchText: '',
        status: null,
        lineId: '',
        trainNo: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      count: '',
      dictCodeStr: '',
      isCheckRow: false,
      isCheckRows: false
    }
  },
  created () {
    this.findList()
  },
  methods: {
    handleSysFocus() {
      if (!this.queryParam.lineId) this.$message.warn('请先选择线路!')
    },
    openTrainPlanModal() {
      this.$refs.trainPlanModal.showModal()
      this.$refs.planSelect.blur()
    },
    clearValue() {
      this.queryParam.planId = ''
      this.planName = ''
    },
    onSelectPlan(data) {
      data.forEach((element) => {
        this.queryParam.planId = element.id
        this.planName = element.planName
      })
    },

    exportLeaveRecordEXFile() {
      leaveRecordExport(`开口项台账-${this.$moment(new Date()).format('YYYYMMDDhhmmss')}.xls`, {
        searchText: this.queryParam.searchText,
        status: this.queryParam.status,
        lineId: this.queryParam.lineId,
        trainNo: this.queryParam.trainNo,
      })

    },
    changeLine (data) {
      if (data) {
        this.dictCodeStr = 'bu_train_info,train_no,train_no,line_id=' + data
      } else {
        this.dictCodeStr = ''
      }
    },
    rangeChange ({ records }) {
      console.log(records)
      if(records.length === 1 && records[0].status !=2){
        this.isCheckRow = true;
      }else{
        this.isCheckRow = false;
      }
      this.isCheckRows = records.length > 0
    },
    openItemDetail (row) {
      this.$refs.modalDetail.show(row)
    },
    findList () {
      this.loading = true
      getOpenItemList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.isCheckRow = false;
        this.isCheckRows = false;
        this.tableData = res.result.records
      })

    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData () {
      this.findList()
    },
    handleAdd () {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleEdit () {
      let record = this.$refs.listTable.getCheckboxRecords()[0]
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
    },

    deleteRecord () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中的${selectRecords.length}条数据？`,
          onOk: () => {
            var idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delOpenItem('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    closeRecord () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      let that = this
      let isClose = false
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否关闭选中的${selectRecords.length}条数据？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                if (obj.status == 2) {
                  that.$message.warning('选中的开口项已经关闭!')
                  isClose = true
                }
                return obj.id
              })
              .join(',')
            if (!isClose) {
              closedOpenItem('ids=' + idsStr).then((res) => {
                this.findList()
                this.$message.success(res.message)
              })
            }
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    }
  }
}
</script>

<style scoped>

</style>