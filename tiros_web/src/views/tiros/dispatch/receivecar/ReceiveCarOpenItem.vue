<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper" v-if="!isReadonly">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button  @click="handleDel">删除</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div :style="`height: calc(100vh - ${isReadonly ? '134' : '240'}px)`">
      <vxe-table
        border
        auto-resize
        ref="listTable"
        max-height="90%"
        :style="`height: calc(100vh - ${isReadonly ? '134' : '239'}px)`"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        @checkbox-all="selectAllRowsIds"
        @checkbox-change="selectRowsIds"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column type="seq" title="序号" width="80"></vxe-table-column>
        <vxe-table-column
          field="leaveContent"
          title="暂缓项目内容"
          min-width="100"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column field="assetTypeName" title="专业系统" min-width="100"></vxe-table-column>
        <vxe-table-column
          field="operation"
          title="工序名称"
          min-width="100"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="reason"
          title="暂缓原因"
          min-width="120"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="planHandle"
          title="计划处理措施"
          min-width="140"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <!--<vxe-table-column field="handleStatus_dictText" title="状态" min-width="140" align="left" header-align="center"></vxe-table-column>-->
        <vxe-table-column title="操作" width="80" fixed="right" v-if="!isReadonly">
          <template v-slot="{ row, rowIndex }">
            <a @click.stop="handleEdit(row, rowIndex)">编辑</a>
          </template>
        </vxe-table-column>
      </vxe-table>
      <!-- <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager> -->
    </div>
    <open-item-modal
      ref="openItemModal"
      @ok="loading = false"
      :trainTypeId="trainTypeId"
      :exchangeId="exchangeId"
      @addItem="addItem"
    ></open-item-modal>
  </div>
</template>

<script>
import { delExChangeLeave, getExChangeLeaveList } from '@api/tirosDispatchApi'
import OpenItemModal from '@views/tiros/dispatch/receivecar/OpenItemModal'
import { everythingIsEmpty } from '@/utils/util'
export default {
  name: 'ReceiveCarOpenItem',
  components: { OpenItemModal },
  props: ['trainTypeId', 'exchangeId', 'isReadonly'],
  data() {
    return {
      queryParam: {
        exchangeId: '',
        pageNo: 1,
        pageSize: 999,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      multipleSelection: [],
    }
  },
  mounted(){
    this.findList()
  },
  methods: {
    findList() {
      if (!everythingIsEmpty(this.exchangeId)) {
        this.queryParam.exchangeId = this.exchangeId
        getExChangeLeaveList(this.queryParam).then((res) => {
          this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result.records
          this.$bus.$emit('setReceiveCarOpenItemData', this.tableData)
        })
      }
    },
    selectAllRowsIds(row) {
      const that = this
      if (row.records.length) {
        row.records.map(function (obj, index) {
          that.multipleSelection.push(obj._XID)
        })
      } else {
        that.multipleSelection = []
      }
    },
    selectRowsIds(row) {
      if (this.multipleSelection.includes(row.rowid)) {
        this.multipleSelection.splice(this.multipleSelection.indexOf(row.rowid), 1)
      } else {
        this.multipleSelection.push(row.rowid)
      }
    },
    addItem(item, index) {
      // 添加
      if (index || index == 0) {
        for (let key in item) {
          this.$set(this.tableData[index], key, item[key])
          this.$forceUpdate()
        }
      } else {
        this.tableData.unshift(item)
        this.$refs.listTable.clearCheckboxRow()
        this.multipleSelection = []
      }
      this.$bus.$emit('setReceiveCarOpenItemData', this.tableData)
    },
    handleAdd() {
      this.$refs.openItemModal.title = '新增'
      this.$refs.openItemModal.add()
    },
    handleEdit(row, index) {
      this.$refs.openItemModal.title = '编辑'
      this.$refs.openItemModal.edit(row, index)
    },
    handleDel() {
      const that = this
      if (that.multipleSelection.length > 0) {
        that.$confirm({
          content: `是否删除选中${that.multipleSelection.length}数据？`,
          onOk: () => {
            that.multipleSelection.map(function (obj, index) {
              for (let i = 0; i < that.tableData.length; i++) {
                const tableDataItem = that.tableData[i]
                if (tableDataItem._XID == obj) {
                  that.tableData.splice(i, 1)
                  return
                }
              }
            })
            this.$refs.listTable.clearCheckboxRow()
            this.multipleSelection = []
            this.$bus.$emit('setReceiveCarOpenItemData', this.tableData)
          },
        })
      } else {
        that.$message.error('尚未选中任何数据!')
      }
    },
    // handleDel() {
    //   let selectRecords = this.$refs.listTable.getCheckboxRecords()
    //   console.log(this.$refs.listTable)
    //   if (selectRecords.length > 0) {
    //     this.$confirm({
    //       content: `是否删除选中${selectRecords.length}数据？`,
    //       onOk: () => {
    //         let idsStr = selectRecords
    //           .map(function (obj, index) {
    //             return obj.id
    //           })
    //           .join(',')
    //         delExChangeLeave('ids=' + idsStr).then((res) => {
    //           this.findList()
    //           this.$message.success(res.message)
    //         })
    //       },
    //     })
    //   } else {
    //     this.$message.error('尚未选中任何数据!')
    //   }
    // },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
  },
}
</script>

<style scoped>
</style>