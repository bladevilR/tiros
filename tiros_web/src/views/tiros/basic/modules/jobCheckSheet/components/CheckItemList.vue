<template>
  <div class="check-item-list-container">
    <div class="btn_cont" style="margin: 10px 0">
      <a-button style="margin-right: 6px" type="primary" @click="handleAdd">新增</a-button>
      <a-button style="margin-right: 6px" @click="handleEdit">修改</a-button>
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
      <vxe-table-column field="sortNo" title="序号" width="120"> </vxe-table-column>
      <vxe-table-column field="title" title="项点名称" align="left" width="280"> </vxe-table-column>
      <vxe-table-column field="checkLevel" title="等级" width="180">
        <template v-slot="{ row }">
          <a-rate disabled v-model="row.checkLevel" count="3"  />
        </template>
      </vxe-table-column>
      <vxe-table-column field="content" title="检查内容" align="left"> </vxe-table-column>
    </vxe-table>
    <!-- 检查项点 -->
    <CheckItemModel ref="modalForm" @ok="addCheckItemData" />
  </div>
</template>

<script>
import CheckItemModel from './CheckItemModel'

export default {
  components: { CheckItemModel },
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
    handleEdit() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length == 1) {
        this.$refs.modalForm.edit(selectRecords[0])
        this.$refs.modalForm.title = '编辑'
      } else {
        this.$message.error('请选择一条数据进行编辑！')
      }
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
    addCheckItemData(data) {
      this.$emit('ok', data)
    },
  },
}
</script>
<style lang="less" scoped>
</style>