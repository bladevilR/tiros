<template>

  <a-modal
    :title="title"
    :width="'70%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <div style="height: calc(100vh - 225px)">
      <vxe-table
        border
        max-height="90%"
        style="height: calc(100vh - 275px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="stationNo" title="工位号" width="15%"></vxe-table-column>
        <vxe-table-column field="name" title="工位名称" width="15%"></vxe-table-column>
        <vxe-table-column field="location" title="位置" width="15%"></vxe-table-column>
        <vxe-table-column field="content" title="作业内容" width="30%"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" width="20%"></vxe-table-column>
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

  </a-modal>
</template>

<script>
import { addWorkstation, pageUnRelatedWorkstation } from '@api/tirosGroupApi'

export default {
  name: 'WookInfoModal',
  props: ['datagroup'],
  data() {
    return {
      queryParam: {
        groupId: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      count: '',
      title: '请选择工位',
      visible: false,
      isClose: false,
      confirmLoading: false,
      form: this.$form.createForm(this)
    }
  },
  created() {

  },
  methods: {
    findList() {
      this.loading = true
      this.queryParam.groupId = this.datagroup
      pageUnRelatedWorkstation(this.queryParam).then((res) => {
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
    changeClose(checked) {
      this.isClose = checked
    },
    showModal() {
      this.visible = true
      this.findList()
    },

    // 确定
    handleOk() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否关联选中${selectRecords.length}数据？`,
          onOk: () => {
            var idsStr = selectRecords
              .map(function(obj, index) {

                return obj.id
              })
              .join(',')
            var param = {
              workstationId: idsStr,
              groupId: this.queryParam.groupId
            }
            addWorkstation(param).then((res) => {
              this.findList()
              this.$message.success(res.message)
              this.$emit('ok')
            })
            this.confirmLoading = false
            this.close()
            this.visible = false
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    }
  }
}
</script>

<style scoped>

</style>