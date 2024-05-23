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
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button type="primary" @click="handleAdd" v-has="'yearplan:add'">新增</a-button>
                <a-button @click="exportMaterialPlanEXFile">导出</a-button>
                <a-button @click="handleEdit(btnStatus.editRow)" v-has="'yearplan:delete'" :disabled="!btnStatus.edit">编辑</a-button>
                <a-button @click="deleteRecord" v-has="'yearplan:delete'" :disabled="!btnStatus.del">删除</a-button>
                <ProcessButtons
                  v-has="'year:plan:workflow'"
                  v-if="selectedRow"
                  @StartSuccess="onStartSuccess"
                  @StartFailure="onStartFailure"
                  @handleSuccess="onHandleSuccess"
                  @cancelSuccess="refreshList"
                  :solution-code="'WF_YEAR_PLAN'"
                  :business-key="selectedRow.id"
                  :business-title="selectedRow.title"
                  :process-instance-id="selectedRow.processInstanceId"
                  :variables="variables"
                  :title="selectedRow.wfStatus"
                ></ProcessButtons>
                <!--<a-dropdown>
                      <a-menu slot="overlay">
                        <a-menu-item key="1">发送</a-menu-item>
                        <a-menu-item key="2">审批</a-menu-item>
                      </a-menu>
                      <a-button type="primary">更多按钮 <a-icon type="down" /> </a-button>
                </a-dropdown>-->
              </a-space>
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
        <vxe-table-column field="title" title="计划名称" align="left" header-align="center" width="15%">
          <template v-slot="{ row }">
            <a @click.stop="yearPlanDetail(row)">{{ row.title }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="year" title="年份" width="100"></vxe-table-column>
        <vxe-table-column field="depotName" title="所属车辆段" width="100"></vxe-table-column>
        <vxe-table-column field="workshopName" align="left" header-align="center" title="所属车间" width="150"></vxe-table-column>
        <vxe-table-column field="middleAmount" title="架修数" width="100"></vxe-table-column>
        <vxe-table-column
          field="hightAmount"
          title="大修数"
          width="100"
          align="right"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="middleRepairFinish"
          title="架修完成"
          width="100"
          align="right"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="hightRepairFinish"
          title="大修完成"
          width="100"
          align="right"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column field="firstTime" title="首列时间" width="100"></vxe-table-column>
        <vxe-table-column
          field="finishPercent"
          title="完成进度"
          width="150"
          align="right"
          header-align="center"
        >
        <template v-slot="{ row }">
          <!--<div style="margin: 2px; background-color: #eeeeee; border: 1px solid #cdcdcd; width: 100%;height: 24px;">
            <div style="margin: 1px; background-color: #46cd11; border: 0px; width: 50%;height: 20px; line-height: 20px;">
              {{row.progress}}%
            </div>
          </div>-->
          <div class="progress">
            <span v-if="row.finishPercent > 10" class="bar" :style="{ width: row.finishPercent + '%' }"
              >{{ row.finishPercent }}%</span
            >
            <span
              v-else
              class="bar"
              :style="{ width: row.finishPercent + '%', 'justify-content': 'right', color: '#494848' }"
              >{{ row.finishPercent }}%</span
            >
          </div>
        </template>
        </vxe-table-column>
        <vxe-table-column
          field="wfStatus"
          align="left"
          header-align="center"
          title="当前审批"
          width="150"
        ></vxe-table-column>
        <vxe-table-column
          field="processCandidate"
          title="当前处理人"
          align="left"
          header-align="center"
          width="160"
        ></vxe-table-column>
        <vxe-table-column field="updateTime" title="更新日期" width="160"></vxe-table-column>
        <vxe-table-column
          field="updateBy"
          align="left"
          header-align="center"
          title="更新人员"
          width="100"
        ></vxe-table-column>
        <!-- <vxe-table-column title="操作" width="10%" fixed="right">
          <template v-slot="{ row }">
            <a @click.stop="handleEdit(row)" v-show="row.wfStatus === '未发起'" v-has="'yearplan:edit'">编辑</a>
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
      :title="'年计划编辑'"
      :width="'100%'"
      :visible="modalVisible"
      :confirmLoading="confirmLoading"
      dialogClass="fullDialog"
      @ok="handleOk"
      @cancel="modalVisible = false"
      cancelText="关闭"
      :destroyOnClose="true"
      :forceRender="true"
    >
      <plan-list-item ref="planFrom" @ok="saveOk" @fail="saveFail"></plan-list-item>
    </a-modal>
    <a-modal
      title='年计划详情'
      :width="'90%'"
      centered
      :visible='detailVisible'
      :footer='null'
      @cancel="detailVisible = false"
      cancelText='关闭'    
      :forceRender="true" 
    >
      <YearPlanDetailItem ref="YearPlanDetailItem" :is-readonly="true"/>
    </a-modal>
  </div>
</template>

<script>

import { delYearPlan, getYearPlanList,yearPlanExport } from '@/api/tirosDispatchApi'
import PlanListItem from '@views/tiros/dispatch/yearPlan/planListItem'
import ProcessButtons from '@views/workflow/ProcessButtons'
import YearPlanDetailItem from '@views/tiros/dispatch/yearPlan/YearPlanDetailItem'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  components: { PlanListItem, ProcessButtons, YearPlanDetailItem },
  data() {
    return {
      selectedRow: null,
      variables: {},
      detailVisible: false,
      modalVisible: false,
      confirmLoading: false,
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
      })
    }
  },
  mounted() {
    this.findList()
  },
  methods: {
    exportMaterialPlanEXFile() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if(selectRecords.length == 1){
        const record = selectRecords[0];
        yearPlanExport(this.queryParam.title + `年计划-${this.$moment(new Date()).format('YYYYMMDDhhmmss')}.xls`, {
          title: record.title,
          id: record.id,
        })
      }else if(selectRecords.length > 1){ 
        this.$message.warn('只能选中一个')
      }else{ 
        this.$message.error('尚未选中任何数据!')

      }
    },
    handleOk() {
      this.confirmLoading = true
      this.$refs.planFrom.save().catch(()=>{
        this.confirmLoading = false;
      })
    },
    saveOk(res) {
      this.confirmLoading = false
      this.$message.success(res.message)
      this.modalVisible = false
      this.findList()
    },
    saveFail(res) {
      this.confirmLoading = false
      this.$message.error(res.message)
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
    findList() {

      this.loading = true
      getYearPlanList(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result.records
          this.btnStatus.update()
        }
      })
    },
    yearPlanDetail(record) {
      this.detailVisible = true
      this.$nextTick(() => {
        this.$refs.YearPlanDetailItem.show(record.id)
      })
    },
    handleEdit(record) {
      this.modalVisible = true
      this.$nextTick(() => {
        this.$refs.planFrom.show(record.id)
      })
    },
    handleAdd() { 
      this.modalVisible = true
      this.$nextTick(() => {
        this.$refs.planFrom.show(null)
      })
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
            const idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delYearPlan('ids=' + idsStr)
              .then((res) => {
                if (res.success) {
                  this.$message.success(res.message)
                  this.loadData()
                } else {
                  this.$message.warning(res.message)
                }
              })
              .catch((err) => {
                console.error('删除年计划异常', err)
                this.$message.error('删除年计划异常')
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
      console.error('流程启动失败:', data)
    },
    // 流程处理成功
    onHandleSuccess(data) {
      this.refreshList()
    },
    refreshList() {
      this.findList()
      this.selectedRow = null
    },
    jump() {},
  },
}
</script>

<style lang="less">
.tableHeight {
  height: calc(100vh - 205px);
  // overflow-y: auto;
}
.progress {
  overflow: hidden;
  width: 100%;
  height: 24px;
  margin: 2px;
  background-color: #eae6e6;
  background-image: -moz-linear-gradient(top, #f5f5f5, #e0dede);
  background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#f5f5f5), to(#e0dede));
  background-image: -webkit-linear-gradient(top, #f5f5f5, #e0dede);
  background-image: -o-linear-gradient(top, #f5f5f5, #e0dede);
  background-image: linear-gradient(to bottom, #f5f5f5, #e0dede);
  background-repeat: repeat-x;
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#fff5f5f5', endColorstr='#fff9f9f9', GradientType=0);
  -webkit-box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
  -moz-box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
  -webkit-border-radius: 4px;
  -moz-border-radius: 4px;
  border-radius: 4px;
}
.progress .bar {
  width: 0%;
  height: 100%;
  color: #ffffff;
  float: left;
  font-size: 12px;
  text-align: center;
  align-items: center;
  display: flex;
  justify-content: center;
  text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25);
  background-color: #34d209;
  background-image: -moz-linear-gradient(top, #34d209, #2e9e0f);
  background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#34d209), to(#2e9e0f));
  background-image: -webkit-linear-gradient(top, #34d209, #2e9e0f);
  background-image: -o-linear-gradient(top, #34d209, #2e9e0f);
  background-image: linear-gradient(to bottom, #34d209, #2e9e0f);
  background-repeat: repeat-x;
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff149bdf', endColorstr='#ff0480be', GradientType=0);
  -webkit-box-shadow: inset 0 -1px 0 rgba(0, 0, 0, 0.15);
  -moz-box-shadow: inset 0 -1px 0 rgba(0, 0, 0, 0.15);
  box-shadow: inset 0 -1px 0 rgba(0, 0, 0, 0.15);
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-transition: width 0.6s ease;
  -moz-transition: width 0.6s ease;
  -o-transition: width 0.6s ease;
  transition: width 0.6s ease;
}
</style>
