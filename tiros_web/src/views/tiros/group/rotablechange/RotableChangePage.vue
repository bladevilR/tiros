<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="4" :sm="24">
            <a-form-item label="部件">
              <a-input placeholder="请输入名称或编码" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24" >
            <a-form-item label="系统">
              <j-dict-select-tag
                v-model="queryParam.systemId"
                placeholder="请选择"
                dictCode="bu_train_asset_type,name,id,struct_type=1 and parent_id is null"
              />
            </a-form-item>
          </a-col>

          <a-col :md="5" :sm="24">
            <a-form-item label="更换日期">
              <a-date-picker v-model="date" />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24" >
            <a-form-item label="车号">
              <j-dict-select-seach-tag
                v-model="queryParam.trainNo"
                placeholder="请选择"
                dictCode="bu_train_info,train_no,train_no"
              />
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button @click="findList">查询</a-button>
              <a-button style="margin-left: 8px" @click="deleteRecord">删除</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>
      <vxe-table
        border
        style="height: calc(100vh - 220px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="ellipsis"
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column title="更换部件名称" width="13%">
          <template v-slot="{ row }">
            <a @click.stop="jumpInfo(row)">{{row.downTurnoverName}}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="lineName" title="线路" width="10%"></vxe-table-column>
        <vxe-table-column field="trainNo" title="车号" width="10%"></vxe-table-column>
        <vxe-table-column field="systemName" title="所属系统" width="10%"></vxe-table-column>
        <vxe-table-column field="downTurnoverManufNo" title="原件序列号" width="10%"></vxe-table-column>
        <vxe-table-column field="downTurnoverPlaceName" title="原件安装位置" width="10%"></vxe-table-column>
        <vxe-table-column field="upTurnoverManufNo" title="新装件序号" width="10%"></vxe-table-column>
        <vxe-table-column field="upTurnoverTrainNo" title="新装件原车号" width="10%"></vxe-table-column>
        <vxe-table-column field="upTurnoverPlaceName" title="新装件原位置" width="10%"></vxe-table-column>
        <vxe-table-column field="changeDate" title="更换时间" width="10%"></vxe-table-column>
        <vxe-table-column field="userName" title="更换人员" width="10%"></vxe-table-column>
        <vxe-table-column field="reasonDesc" title="备注" width="20%"></vxe-table-column>
        <vxe-table-column title="操作" width="10%" fixed="right">
          <template v-slot="{ row }">
            <a @click.stop="handleEdit(row)">编辑</a>
          </template>
        </vxe-table-column>
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

  </div>
</template>

<script>
  import moment from 'moment'
  import { getRotableChangeList, delRotableChange } from '../../../../api/tirosGroupApi'

  export default {
    name: 'RotableChangePage',
    data() {
      return {
        date:null,
        queryParam: {
          searchText: '',
          systemId: '',
          trainNo: '',
          changeDate:null,
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
        this.dataSource = []

        this.loading = true
        if(this.date){
          this.queryParam.changeDate = moment(this.date).format('YYYY-MM-DD')
        }else {
          this.queryParam.changeDate = ''
        }
        getRotableChangeList(this.queryParam).then((res) => {
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

      handleEdit(record) {
        // console.log(record)
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.title = '编辑'
      },

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
              delRotableChange('ids=' + idsStr).then((res) => {
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
        // alert(row);
        this.$router.push({ path: `/tiros/group/rotablechange/${row.id}` })
      },
    }
  }
</script>

<style scoped>

</style>