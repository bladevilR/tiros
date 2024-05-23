<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="4" :sm="12">
            <a-form-item label="名称">
              <a-input placeholder="输入编码或者名称" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="12">
            <a-form-item label="时间">
              <a-range-picker format="YYYY-MM-DD" :placeholder="['开始时间', '结束时间']"  @change="onDateChange" :defaultPickerValue="defaultDateRange" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="列计划" :labelCol="{span:5}" :wrapperCol="{span: 19}">
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
          <a-col :md="8" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button @click="handleEdit" :disabled="!btnStatus.edit">修改</a-button>
                <a-button @click="deleteRecord" :disabled="!btnStatus.delJudge">删除</a-button>
                <a-button @click="handlerReturn" :disabled="!btnStatus.delJudge">确认退料</a-button>
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
        @checkbox-change="btnStatus.update()"
        @checkbox-all="btnStatus.update()"
      >
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column field="billCode" title="退料单编码" width="130">
          <template v-slot="{row}">
              <a href="javascript:;" @click.stop="handleView(row)">{{row.billCode}}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="billName" align="left" header-align="center" title="退料单名称" width="280"></vxe-table-column>
        <vxe-table-column field="billDate" title="退料时间" width="140"></vxe-table-column>
        <vxe-table-column field="workOrderName" align="left" header-align="center" title="所属工单" width="220"></vxe-table-column>
        <vxe-table-column field="handleUserName" align="left" header-align="center" title="办理人" width="100"></vxe-table-column>
        <vxe-table-column field="groupName" align="left" header-align="center" title="所属班组" width="120"></vxe-table-column>
        <vxe-table-column field="status" title="状态" width="120">
            <template v-slot="{row}">
                <span v-if="row.status == 0">草稿</span>
                <span v-else-if="row.status == 1">已确认</span>
                <span v-else>未确认</span>
            </template>
        </vxe-table-column>
        <vxe-table-column
          field="remark"
          title="备注描述"
          min-width="100"
          header-align="center"
          align="left"
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
    <returned-item-modal ref="modalForm" @ok="loadData()"></returned-item-modal>
    <ReturnedViewModal ref="modalView"/>
    <TrainPlanList ref="trainPlanModal" @ok="onSelectPlan"></TrainPlanList>

  </div>
</template>


<script>
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { delReturnedItem, getReturnedList, confirmReturnedItem } from '../../../../api/tirosMaterialApi'
import ReturnedItemModal from '../modules/ReturnedItemModal'
import ReturnedViewModal from '../modules/ReturnedViewModal'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'
import TrainPlanList from '@views/tiros/common/selectModules/TrainPlanList'

export default {
  name: 'List',
  components: { ReturnedItemModal, ReturnedViewModal, LineSelectList,TrainPlanList },
  data() {
    return {
      planName:'',
      queryParam: {
        planId:'',
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
      btnStatus: new TableBtnUtil(this, 'listTable', {
        attrs:[
          {
            key: 'edit',
            judge: (e) => e.status !== 1
          },
          {
            key: 'delJudge',
            isDel: true,
            judge: (e) => e.status !== 1
          }
        ]
      }),
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
      onDateChange(value, dateString){
      this.queryParam.startDate=dateString[0];
        this.queryParam.endDate=dateString[1];
    },
    findList() {
      getReturnedList(this.queryParam).then((res) => {
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
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleView(row){
      this.$refs.modalView.show(row);
    },
    handleEdit(row) {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if(selectRecords[0].status===1){
        this.$message.warn('该物资已确认退料,不可修改!')
        return
      }
      if(row.id){
        this.$refs.modalForm.edit(row)
        this.$refs.modalForm.title = '编辑'
      }else if(selectRecords.length == 1){
          this.$refs.modalForm.edit(selectRecords[0])
            this.$refs.modalForm.title = '编辑'
      } else {
            this.$message.error('请选中一项数据!')
        }

    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            let sbs=selectRecords.filter(item=>{
              return item.status === 1
            })
            if (sbs.length > 0) {
              this.$message.warn('已确认的退料单无法删除！')
              return;
            }
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delReturnedItem('ids=' + idsStr).then((res) => {
              if(res.success){
                this.$message.success(res.message)
                this.findList()
              }else{
                this.$message.warning(res.message)
              }
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handlerReturn() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否设置选中${selectRecords.length}数据为确认退料？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            confirmReturnedItem('ids=' + idsStr).then((res) => {
              if(res.success){
                this.findList()
                this.$message.success(res.message)
              }else{
                this.$message.warning(res.message)
              }
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
  },
}
</script>