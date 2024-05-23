<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="工具">
              <a-input placeholder="请输入名称或编码" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag v-model="queryParam.status" placeholder="请选择" dictCode="bu_tools_status" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button :disabled="records.length != 1" @click="handleSet(records[0])">分配责任人</a-button>
                <a-button @click="findList">查询</a-button>
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
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :cell-class-name="cellClassName"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column title="工具名称" min-width="150" align="left" header-align="center">
          <template v-slot="{ row }">
            <a @click.stop="jumpInfo(row)">{{ row.name }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="code" title="物资编码" width="200"></vxe-table-column>
        <vxe-table-column field="assetCode" title="资产编号" width="200"></vxe-table-column>
        <vxe-table-column field="toolType_dictText" title="架大修类别" width="100"></vxe-table-column>
        <vxe-table-column
          field="amount"
          title="数量"
          width="100"
          align="right"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column field="nextCheckTime" title="下次送检" width="150"></vxe-table-column>
        <vxe-table-column field="isOverTime" title="是否逾期" width="100">
          <template v-slot="{ row }">
            {{ getIsOverTimeStatus(row) }}
          </template>
        </vxe-table-column>
        <vxe-table-column field="isOverTimeDays" title="逾期天数" width="100">
          <template v-slot="{ row }">
            {{ getIsOverTimeDays(row) }}
          </template>
        </vxe-table-column>
        <vxe-table-column field="userName" title="保管人姓名" width="100"></vxe-table-column>
        <vxe-table-column field="entryDate" title="入库日期" width="100"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="100"></vxe-table-column>
        <vxe-table-column field="warehouseName" title="库位" width="200"></vxe-table-column>
        <vxe-table-column field="serviceInterval" title="送检间隔" width="100"></vxe-table-column>
        <vxe-table-column field="entraceDate" title="入场使用日期" width="110"></vxe-table-column>
        <vxe-table-column field="lifetime" title="使用寿命" width="100"></vxe-table-column>
        <vxe-table-column field="expirDate" title="有效期" width="100"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" width="180"></vxe-table-column>
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
    <tools-manage-modal ref="modalForm" @ok="loadData()"></tools-manage-modal>
    <user-list ref="UserModalForm" :multiple="false" @ok="addTarget"></user-list>

    <a-modal
      title="工具详情"
      :width="'90%'"
      centered
      :visible="visible"
      dialogClass="fullDialog no-footer"
      @cancel="handleCancel"
      :forceRender="true"
    >
      <tools-manage-info ref="toolsManageInfo"></tools-manage-info>
    </a-modal>
  </div>
</template>

<script>
import { getToolList, setTool } from '@api/tirosGroupApi'
import ToolsManageModal from '../modules/ToolsManageModal'
import Vue from 'vue'
import { USER_INFO } from '@/store/mutation-types'
import UserList from '@views/tiros/common/selectModules/UserList'
import ToolsManageInfo from '@views/tiros/group/toolsmanage/ToolsManageInfo'
export default {
  name: 'ToolsManagePage',
  components: { ToolsManageModal, UserList, ToolsManageInfo },
  data() {
    return {
      visible: false,
      queryParam: {
        searchText: '',
        status: '',
        groupId: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      count: '',
      editData: {},
      records:[],
    }
  },
  created() {
    this.findList()
  },
  methods: {
    checkboxChange(e){
      this.records = e.records;
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
      this.$refs.toolsManageInfo.close()
    },
    addTarget(data) {
      if (data.length) {
        this.editData['userId'] = data[0].id
        setTool(this.editData).then((res) => {
          if (res.success) {
            this.$message.success(res.message)
            this.findList()
          } else {
            this.$message.error(res.message)
          }
        })
      }
    },
    findList() {
      this.loading = true
      const userInfo = Vue.ls.get(USER_INFO)
      this.queryParam.groupId = userInfo.departId
      getToolList(this.queryParam).then((res) => {
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
    handleSet(record) {
      this.editData = record
      this.$refs.UserModalForm.showModal()
    },
    loadData() {
      this.findList()
    },
    jumpInfo(row) {
      this.visible = true
      this.$refs.toolsManageInfo.show({ id: row.id })
    },
    // 计算逾期天数
    getIsOverTimeDays(row) {
      if (row.nextCheckTime) {
        let dateNow = this.$moment(new Date()).format('YYYY-MM-DD')
        let diffDay = this.$moment(row.nextCheckTime).diff(dateNow, 'day')
        if (diffDay < 0) {
          return Math.abs(diffDay)
        }
      }
      return ''
    },
    // 转义逾期显示字符串
    getIsOverTimeStatus(row) {
      if (row.nextCheckTime) {
        let dateNow = this.$moment(new Date()).format('YYYY-MM-DD')
        let diffDay = this.$moment(row.nextCheckTime).diff(dateNow, 'day')
        return diffDay < 0 ? '是' : '否'
      } else {
        return '否'
      }
    },
    // 逾期数据单元格样式设定
    cellClassName({ row, rowIndex, column, columnIndex }) {
      let columnsArr = ['nextCheckTime', 'isOverTime', 'isOverTimeDays']
      if (columnsArr.includes(column.property)) {
        if (row.nextCheckTime) {
          let dateNow = this.$moment(new Date()).format('YYYY-MM-DD')
          let diffDay = this.$moment(row.nextCheckTime).diff(dateNow, 'day')

          if (column.property === 'nextCheckTime') {
            if (diffDay <= 0) {
              return 'table-cell-bg-red'
            }
            if (diffDay > 0 && diffDay <= 15) {
              return 'table-cell-bg-yellow'
            }
          }

          if (column.property === 'isOverTime') {
            if (diffDay < 0) {
              return 'table-cell-bg-red'
            }
          }
          if (column.property === 'isOverTimeDays') {
            if (diffDay < 0) {
              return 'table-cell-bg-red'
            }
          }
        }
      }
    },
  },
}
</script>

<style scoped>
</style>