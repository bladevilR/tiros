<template>
  <div style="padding: 5px">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="作业记录表编码或名称" v-model="queryParam.name" allow-clear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="线路">
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="修程">
              <j-dict-select-tag @change="handleProgram" triggerChange v-model="queryParam.repairProId" dictCode="bu_repair_program,name,id" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="规程">
              <j-dict-select-tag
                triggerChange
                v-model="queryParam.reguId"
                :dictCode="dicStr"
                @focus="handleFocusRegu"
              />
            </a-form-item>
          </a-col>
<!--          <a-col :md="6" :sm="24">-->
<!--            <a-form-item label="工位">-->
<!--              <div @click="openModal">-->
<!--                <a-select-->
<!--                allow-clear-->
<!--                placeholder="请选择工位"-->
<!--                :open="false"-->
<!--                :showArrow="true"-->
<!--                v-model="workstationName"-->
<!--                ref="mySelect"-->
<!--              >-->
<!--                <a-icon slot="suffixIcon" type="ellipsis" />-->
<!--              </a-select>-->
<!--              </div>-->
<!--              -->
<!--            </a-form-item>-->
<!--          </a-col>-->

          <a-col :md="6" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
               <a-space>
                 <a-button @click="findList">查询</a-button>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button :disabled="records.length != 1" @click="handleEdit(records[0])">编辑</a-button>
                <a-button :disabled="records.length < 1" @click="deleteRecord" v-has="'recordsheet:delete'">删除</a-button>
                <a-button :disabled="records.length != 1" @click="goRecordDetail">记录表明细管理</a-button>
                <!-- <a-button :disabled="records.length != 1" @click="viewRecord">预览</a-button> -->
                <a-button :disabled="records.length != 1" @click="print">导出</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 272px)">
      <vxe-table
        border
        auto-resize
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        max-height="100%"
        style="height: calc(100vh - 272px)"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :checkbox-config="{trigger: 'row', highlight: true, range: true}"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="title" width="200" title="作业记录表名称" align="left" header-align="center">
          <template v-slot="{row}">
            <a @click.stop="handleSeeing(row)">{{row.title}}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="code" title="编码" width="100"></vxe-table-column>
        <vxe-table-column field="assetTypeName" title="关联设备" width="200"></vxe-table-column>
        <vxe-table-column field="reguName" title="技术规程" width="200">
          <template v-slot="{ row }">
            <span v-if="row.reguName">{{row.reguName + (row.reguVersion ? `(${row.reguVersion})` : '')}}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="lineName" title="线路" width="100"></vxe-table-column>
        <vxe-table-column field="repairProName" align="left" header-align="center" title="修程" width="100"></vxe-table-column>
        <vxe-table-column field="createGroupName" align="left" header-align="center" title="创建班组" width="150"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="80"></vxe-table-column>
        <vxe-table-column width="180" field="remark" align="left" header-align="center" title="备注"></vxe-table-column>
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

    <!-- 工位选择 -->
    <work-station-list ref="stationModalForm" @ok="addStation"></work-station-list>
    <!-- 新增修改记录表 -->
    <sheet-item-modal ref="recordModalForm" @ok="addRecord"></sheet-item-modal>
    <RecordTableView ref="recordTableView"></RecordTableView>
    <!-- <TablePrint ref="tablePrintRef"></TablePrint> -->
    <TableExport ref="tablePrintRef" />
    
    <record-detail ref="recordDetail" ></record-detail>
  </div>
</template>

<script>
import WorkStationList from '../common/selectModules/WorkStationList'
import SheetItemModal from './modules/workRecordSheet/SheetItemModal'
import { pageOldWorkRecord, deleteOldWorkRecord } from '@/api/tirosApi'
import RecordTableView from '@views/tiros/basic/modules/workRecordSheet/RecordTableView'
import RecordDetail from '@views/tiros/basic/modules/workRecordSheet/RecordDetail'
// import TablePrint from '@views/tiros/common/recordtable/TablePrint'
import TableExport from '@views/tiros/common/recordtable/TableExport.vue'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { everythingIsEmpty } from '@/utils/util'


export default {
  components: { WorkStationList, SheetItemModal, RecordTableView ,RecordDetail, TableExport, LineSelectList},
  name: 'WorkRecord',
  data() {
    return {
      records:[],
      dicStr: undefined,
      queryParam: {
        workstationId: '',
        name: '',
        createGroupId: '',
        repairProId: '',
        reguId: '',
        lineId: '',
        pageNo: 1,
        pageSize: 10,
      },
      clearStation:false,
      workstationName: undefined,
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
    }
  },
  created() {
    this.findList()
  },
  watch:{
    workstationName: {
      immediate: true,
      handler(newVal,oldVal) {
        if(!newVal){
          this.queryParam.workstationId=''
        }
      },
    },
  },
  methods: {
    handleFocusRegu () {
      let programId = this.queryParam.repairProId
      if (everythingIsEmpty(programId)) this.$message.warn('请先选择修程类型')
    },
    handleProgram (data) {
      if (data) {
        this.dicStr = `bu_repair_regu_info,name,id,status=1 and repair_pro_id='${data}'`
      } else {
        this.queryParam.reguId = '';
        this.dicStr = ''
      }
    },
    checkboxChange(e){
      this.records = e.records;
    },
    openModal() {
      this.$refs.stationModalForm.showModal()
    },
    findList() {
      this.loading = true
      // if (this.$store.getters.userInfo.username === 'admin') {
      //   this.queryParam.createGroupId = null
      // } else {
      //   this.queryParam.createGroupId=this.$store.getters.userInfo.departId
      // }

      pageOldWorkRecord(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.records = [];
          this.tableData = res.result.records
        }
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleEdit(record) {
      this.$refs.recordModalForm.edit(record)
      this.$refs.recordModalForm.title = '编辑'
    },
    handleAdd() {
      this.$refs.recordModalForm.add()
      this.$refs.recordModalForm.title = '新增'
    },
    addStation(data) {
      this.queryParam.workstationId = data[0].id
      this.workstationName = data[0].name
    },
    addRecord(data) {
      this.findList()
    },
    goRecordDetail() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length == 1) {
        this.$refs.recordDetail.show(selectRecords[0])
      } else {
        this.$message.error('请选择一条数据!')
      }
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}条数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            var idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            deleteOldWorkRecord({ ids: idsStr }).then((res) => {
              if (res.success) {
                this.findList()
                this.$message.success(res.message)
              } else {
                this.$message.error(res.message)
              }
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handleSeeing(row) {
      this.$refs.recordTableView.show(row.id)
    },
    viewRecord () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.length > 1) {
          this.$message.error('一次只能查看一条记录!')
        } else {
          this.$refs.recordTableView.show(selectRecords[0].id)
        }
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    print(){
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      this.$refs.tablePrintRef.printById(selectRecords[0].id)
    }
  },
}
</script>

<style lang="less">
#recordSheetContent {
  border: none;
  .ant-card-body {
    padding: 0px 15px 0px 0px;
    overflow-y: hidden;
  }
  .tableHeight {
    height: calc(100vh - 230px);
    // overflow-y: auto;
  }
}
</style>