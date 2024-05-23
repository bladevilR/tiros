<template>
  <div class="bodyWrapper">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="标题">
              <a-input placeholder="请输入内容" v-model="queryParam.title" allow-clear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择状态"
                dictCode="bu_repair_plan_status"
              />
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-button @click="findList">查询</a-button>
              <a-button type="primary" @click="handleAdd" style="margin-left: 8px" v-has="'farpaln:add'">新增</a-button>
              <a-button style="margin-left: 8px" v-has="'farpaln:edit'" :disabled="!btnStatus.edit" @click="handleEdit"
                >编辑</a-button
              >
              <a-button
                style="margin-left: 8px"
                @click="deleteRecord"
                v-has="'farpaln:delete'"
                :disabled="!btnStatus.del"
                >删除</a-button
              >
              <ProcessButtons
                ref="WfButtons"
                v-if="selectedRow"
                v-has="'long:plan:workflow'"
                @StartSuccess="onStartSuccess"
                @StartFailure="onStartFailure"
                @handleSuccess="onHandleSuccess"
                @cancelSuccess="refreshList"
                :solution-code="'WF_LONG_PLAN'"
                :business-key="selectedRow.id"
                :business-title="selectedRow.planName"
                :process-instance-id="selectedRow.processInstanceId"
                :variables="variables"
                :title="selectedRow.wfStatus"
              ></ProcessButtons>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 225px)">
      <vxe-table
        border
        auto-resize
        ref="listTable"
        max-height="100%"
        style="height: calc(100vh - 225px)"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        @checkbox-change="rowSelectChange"
        @checkbox-all="btnStatus.update()"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="title" title="规划标题" align="left" header-align="center" min-width="200">
          <template v-slot="{ row }">
            <a @click.stop="handleView(row)">{{ row.title }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="depotName" title="所属车辆段" width="150"></vxe-table-column>
        <vxe-table-column field="workshopName" align="left" header-align="center" title="所属车间" width="150"></vxe-table-column>
        <vxe-table-column field="startYear" title="开始年份" width="100"></vxe-table-column>
        <vxe-table-column field="endYear" title="结束年份" width="100"></vxe-table-column>
        <vxe-table-column
          field="middleRepair"
          title="架修总数"
          width="100"
          align="right"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="hightRepair"
          title="大修总数"
          width="100"
          align="right"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column field="wfStatus" title="当前审批" width="150"></vxe-table-column>
        <vxe-table-column
          field="processCandidate"
          title="当前处理人"
          align="left"
          header-align="center"
          width="160"
        ></vxe-table-column>
        <!-- <vxe-table-column field="updateTime" title="更新日期" width="150"></vxe-table-column>
        <vxe-table-column field="updateBy" title="更新人员" width="100"></vxe-table-column> -->
        <!-- <vxe-table-column title="操作" width="150" fixed="right">
          <template v-slot="{ row }">
            <a-space>
              <a @click.stop="handleEdit(row)" v-show="row.wfStatus === '未发起'" v-has="'farpaln:edit'">编辑</a>
              <a @click.stop="handleView(row)">查看</a>
            </a-space>
          </template>
        </vxe-table-column> -->
      </vxe-table>
    </div>
    <vxe-pager
      perfect
      :current-page.sync="queryParam.pageNo"
      :page-size.sync="queryParam.pageSize"
      :total="totalResult"
      :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
      @page-change="handlePageChange"
    ></vxe-pager>
    <a-modal
      :title="'远期规划'"
      :width="'80%'"
      centered
      :visible="visible"
      @ok="saveItem"
      @cancel="handleCancel"
      cancelText="关闭"
      :forceRender="true"
    >
      <plan-list-item ref="modalFrom" @ok="saveOk" @fail="saveFail" :is-readonly="false"></plan-list-item>
    </a-modal>

    <a-modal
      :title="'远期规划查看'"
      :width="'80%'"
      centered
      :visible="viewVisible"
      @cancel="viewVisible = false"
      :forceRender="true"
      :footer="null"
    >
      <planListView ref="modalFromView" :is-readonly="true" />
    </a-modal>
  </div>
</template>

<script>
import { getFarPlanList, delFarPlan } from '@/api/tirosDispatchApi'
import PlanListItem from '@views/tiros/dispatch/farPlan/planListItem'
import ProcessButtons from '@views/workflow/ProcessButtons'
import planListView from '@views/tiros/dispatch/farPlan/planListView'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  components: { PlanListItem, ProcessButtons, planListView },
  data() {
    return {
      visible: false,
      viewVisible: false,
      selectedRow: null,
      variables: {},
      queryParam: {
        title: '',
        status: null,
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      btnStatus: new TableBtnUtil(this, 'listTable',{
        attrs: [
          {
            key: 'edit',
            judge: (e) => {
              console.log(e)
              return e.wfStatus === '未发起'
            }
          },
        ],
      }),
    }
  },
  created() {
    this.findList()
  },
  methods: {
    handleCancel() {
      this.visible = false
      this.$refs.modalFrom.detailTotal = 0
    },
    rowSelectChange() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords && selectRecords.length == 1) {
        this.selectedRow = selectRecords[0]
      } else {
        this.selectedRow = null
      }
      this.btnStatus.update()
    },
    saveItem() {
      this.$refs.modalFrom.save()
    },
    saveOk() {
      this.visible = false
      this.$message.success('保存成功')
      this.loadData()
    },
    saveFail() {
      this.$message.error('保存失败')
    },
    findList() {
      this.loading = true
      getFarPlanList(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result.records
          this.btnStatus.update()
        }
      })
    },
    editTrack(data) {
      let editData = this.tableData.filter((d) => {
        return d.id == data.id
      })
      Object.keys(data).forEach((key) => {
        data[key] = editData[key]
      })
    },
    handleEdit() {
      /*this.$refs.modalFrom.title='编辑'
      this.$refs.modalFrom.show(record.id)*/
      this.visible = true
      // this.$refs.modalFrom.show(record.id)
      this.$refs.modalFrom.show(this.btnStatus.editRow.id)
    },
    handleView(row) {
      this.viewVisible = true
      this.$refs.modalFromView.show(row.id, true)
    },
    handleAdd() {
      this.visible = true
      this.$refs.modalFrom.show(null)
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (
          selectRecords.filter((r) => {
            return r.wfStatus != '未发起'
          }).length > 0
        ) {
          this.$message.warn('审批中或已审批的数据不能删除')
          return
        }

        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              }).join(',')
            delFarPlan('ids=' + idsStr).then((res) => {
              if (res.success) {
                this.$message.success(res.message)
                this.loadData()
              } else {
                this.$message.warning(res.message)
              }
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },

    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData() {
      this.findList()
    },
    // 流程启动成功回调
    onStartSuccess(data) {
      this.refreshList()
    },
    // 流程启动失败回调
    onStartFailure(data) {
      console.log('启动失败:', data)
    },
    // 流程处理成功
    onHandleSuccess(data) {
      this.refreshList()
    },
    refreshList() {
      this.findList()
      this.selectedRow = null
    },
    showEditBtn() {
      if (!this.$refs.listTable) {
        return false
      }
      return this.$refs.listTable.getCheckboxRecords().length === 1 ? true : false
    },
    showDelBtn() {
      if (!this.$refs.listTable) {
        return false
      }
      return this.$refs.listTable.getCheckboxRecords().length > 1 ? true : false
    },
  },
}
</script>

<style lang="less">
#planListContent {
  border: none;

  .ant-card-body {
    padding: 24px 24px 0;
    height: calc(100vh - 140px);
    overflow-y: auto;
  }

  .tableHeight {
    height: calc(100vh - 255px);
    // overflow-y: auto;
  }
}
</style>
