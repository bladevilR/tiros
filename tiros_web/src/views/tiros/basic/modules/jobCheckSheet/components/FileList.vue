<template>
  <div class="file-list-container">
    <div class="btn_cont" style="margin: 10px 0">
      <a-button style="margin-right: 6px" type="primary" @click="handleAdd">新增</a-button>
      <a-button @click="handleDelete">删除</a-button>
    </div>
    <vxe-table
      border
      ref="listTable"
      align="center"
      max-height="auto"
      :data.sync="data"
      :keep-source="true"
      :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
    >
      <vxe-table-column type="checkbox" width="40"></vxe-table-column>
      <vxe-table-column type="seq" title="序号" width="120"> </vxe-table-column>
      <vxe-table-column field="teckBookName" title="工艺文件" align="left"> </vxe-table-column>
      <vxe-table-column title="操作" width="80">
        <template v-slot="{ row }">
          <a @click.stop="showSop(row)">预览</a>
        </template>
      </vxe-table-column>
    </vxe-table>
    <FileItemModel ref="modalForm" @ok="addFileItemData" />
    <WorkSopView ref="workSopViewModal"></WorkSopView>
  </div>
</template>
<script>
import FileItemModel from './FileItemModel'
import WorkSopView from '@views/tiros/basic/modules/worksop/WorkSopView'

export default {
  components: { FileItemModel, WorkSopView },
  data() {
    return {}
  },
  props: {
    data: {
      type: Array,
      default() {
        return []
      },
    },
  },
  methods: {
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleDelete() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}条数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            this.$emit('del', selectRecords)
          },
        })
      } else {
        this.$message.error('请选择最少一条数据！')
      }
    },
    addFileItemData(data) {
      this.$emit('ok', data)
    },
    showSop(row) {
      this.$refs.workSopViewModal.showBook(row.teckBookId)
    },
  },
}
</script>
<style lang="less" scoped>
</style>