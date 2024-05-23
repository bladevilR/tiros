<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="工装名称">
              <a-input placeholder="请输入工装名称" v-model="queryParam.name" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="工装编码">
              <a-input placeholder="请输入工装编码" v-model="queryParam.code" allowClear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择"
                dictCode="bu_tools_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
                 <a-button @click="findList">查询</a-button>
              <a-button type="primary" @click="handleAdd">新增</a-button>
              <a-button @click="handleEdit(btnStatus.editRow)" :disabled="!btnStatus.edit">编辑</a-button>
              <a-button @click="deleteRecord" :disabled="!btnStatus.del">删除</a-button>
              </a-space>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 225px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(100vh - 225px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :edit-config="{trigger: 'manual', mode: 'row'}"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        @checkbox-change="btnStatus.update()"
        @checkbox-all="btnStatus.update()"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="startTime" title="开始时间" width="160"></vxe-table-column>
        <vxe-table-column field="endTime" title="结束时间" width="160"></vxe-table-column>
        <vxe-table-column field="name" align="left" header-align="center" title="工装名称" width="120"></vxe-table-column>
        <vxe-table-column field="model" title="规格" min-width="120" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="80"></vxe-table-column>
        <vxe-table-column field="code" title="工装编码" width="120"></vxe-table-column>
        <vxe-table-column field="assetCode" title="资产编码" width="120"></vxe-table-column>
        <vxe-table-column field="warehouseName" title="库位" width="120"></vxe-table-column>
        <vxe-table-column field="entraceDate" title="入场使用日期" width="120"></vxe-table-column>
        <vxe-table-column field="groupName" title="所属工班" width="120"></vxe-table-column>

        <!-- <vxe-table-column title="操作" width="80">
          <template v-slot="{ row }">
            <a @click.stop="handleEdit(row)">编辑</a>
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
    <to-use-modal ref="modalForm" @ok="loadData()"></to-use-modal>
  </div>
</template>

<script>
import moment from 'moment'
import { getToolplanList, delToolplan } from '@/api/tirosDispatchApi'
import ToUseModal from '../modules/ToUseModal'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'ToUsePage',
  components: { ToUseModal },
  data () {
    return {
      queryParam: {
        name: '',
        code: '',
        status: '',
        planType: 1,
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      btnStatus: new TableBtnUtil(this, 'listTable'),
      count: ''
    }
  },
  created () {
    this.findList()
  },
  methods: {
    findList () {
      this.loading = true
      if (this.queryParam.date) {
        this.queryParam.date = moment(this.date).format('YYYY-MM-DD')
      } else {
        this.queryParam.date = ''
      }

      getToolplanList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
        this.btnStatus.update()
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
    handleEdit (record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
    },

    deleteRecord () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            var idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delToolplan('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
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