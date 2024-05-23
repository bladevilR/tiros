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
              <a-input placeholder="请输入资产编码或序列号" v-model="queryParam.assetCode" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择"
                dictCode="bu_turnover_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="线路">
<!--              <j-dict-select-tag
                v-model="queryParam.lineId"
                placeholder="请选择"
                dictCode="bu_mtr_line,line_name,line_id"
              />-->
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>

          </a-col>
          <a-col :md="5" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button @click="$refs.importModal.show()">导入</a-button>
             <!-- <a-button type="primary" @click="handleAdd" style="margin-left: 8px">新增</a-button>
              <a-button style="margin-left: 8px" @click="deleteRecord">删除</a-button>-->
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
        :checkbox-config="{trigger: 'row', highlight: true, range: true}"
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column title="部件名称" align="left" header-align="center" width="15%">
          <template v-slot="{ row }">
            <a @click.stop="jumpInfo(row)">{{row.name}}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="materialCode" title="部件编码" width="15%"></vxe-table-column>
        <vxe-table-column field="assetCode" title="资产编码" width="15%"></vxe-table-column>
        <vxe-table-column field="manufNo" title="序列号" width="10%"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="目前状态" width="10%"></vxe-table-column>
        <vxe-table-column field="dutyUserName" align="left" header-align="center" title="保管人员" width="10%"></vxe-table-column>
        <vxe-table-column field="groupName" align="left" header-align="center" title="所属工班" width="10%"></vxe-table-column>
        <vxe-table-column field="price" align="right" header-align="center" title="单价" width="10%"></vxe-table-column>
        <vxe-table-column field="systemName" title="所属系统" width="10%"></vxe-table-column>
        <vxe-table-column field="currentLocation" title="当前位置" width="15%"></vxe-table-column>
        <vxe-table-column field="outDate" title="出库日期" width="10%"></vxe-table-column>
        <vxe-table-column field="outOrderNo" title="出库单号" width="10%"></vxe-table-column>
        <vxe-table-column field="lineName" title="所属线路" width="10%"></vxe-table-column>
        <vxe-table-column field="model" align="left" header-align="center" title="规格型号" width="15%"></vxe-table-column>
       <!-- <vxe-table-column title="操作" width="10%" fixed="right">
          <template v-slot="{ row }">
            <a @click="handleEdit(row)">编辑</a>
          </template>
        </vxe-table-column>-->
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
    <rotables-item-modal ref="modalForm" @ok="loadData()"></rotables-item-modal>
  <rotable-manage-info ref="rotableInfo"></rotable-manage-info>
  <j-import-modal :sum="1" ref="importModal" url="/import/materialSparePart" @ok="loadData()"></j-import-modal>
</div>
</template>

<script>

  import { getRotablesList, delRotables } from '@/api/tirosMaterialApi'
  import RotablesItemModal from '../modules/RotablesItemModal'
  import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
  import RotableManageInfo from '@views/tiros/group/rotablemanage/RotableManageInfo'
  import JImportModal from '@/components/jeecg/JImportModal'

  export default {

    name: 'RotablesPage',
    components: { RotablesItemModal ,LineSelectList,RotableManageInfo,JImportModal},
    data() {
      return {
        queryParam: {
          assetCode: '',
          lineId: '',
          searchText: '',
          status: '',
          pageNo: 1,
          pageSize: 10,
        },
        totalResult: 0,
        allAlign: 'center',
        tableData: []
      }
    },
    created() {
      this.findList()
    },
    methods: {
      findList() {
        this.loading = true
        getRotablesList(this.queryParam).then((res) => {
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
      handleAdd() {
        this.$refs.modalForm.add()
        this.$refs.modalForm.title = '新增'
      },
      handleEdit(record) {
        console.log(record)
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.title = '编辑'
      },

      // getSelectEvent() {
      //   let selectRecords = this.$refs.listTable.getCheckboxRecords()
      //   this.$XModal.alert(selectRecords.length)
      // },
      deleteRecord() {
        let selectRecords = this.$refs.listTable.getCheckboxRecords()
        if (selectRecords.length > 0) {
          this.$confirm({
            content: `是否删除选中${selectRecords.length}数据？`,
            onOk: () => {
              var idsStr = selectRecords
                .map(function(obj, index) {
                  return obj.id
                })
                .join(',')
              delRotables('ids=' + idsStr).then((res) => {
                this.findList()
                this.$message.success(res.message)
              })
            }
          })
        } else {
          this.$message.error('尚未选中任何数据!')
        }
      },
      jumpInfo(row) {
        this.$refs.rotableInfo.show(row.id)
      },
    }
  }
</script>

<style scoped>
</style>

