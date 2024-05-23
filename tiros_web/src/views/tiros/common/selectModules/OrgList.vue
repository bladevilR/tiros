<template>
  <!-- 机构选择弹窗 -->
  <a-modal
    width="90%"
    title="机构选择"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
    :bodyStyle="{height:'60vh'}"
  >
    <a-row>
      <a-col :md="5" :sm="24">
        <a-tree
          v-if="treeData.length"
          defaultExpandAll
          :tree-data="treeData"
          @select="onSelect"
          class="limitHeight"
        />
      </a-col>
      <a-col :md="19" :sm="24">
    <div class="limitHeight">
        <vxe-table
          border
          ref="listTable"
          :align="allAlign"
          :data="tableData"
          show-overflow="tooltip"
          :edit-config="{trigger: 'manual', mode: 'row'}"
        >
          <vxe-table-column v-if="multiple" type="checkbox" width="10%"></vxe-table-column>
          <vxe-table-column v-else type="radio" width="5%"></vxe-table-column>
          <vxe-table-column field="departName" title="机构名称" width="45%"></vxe-table-column>
          <vxe-table-column field="orgCode" title="机构编码" width="45%"></vxe-table-column>
        </vxe-table>
    </div>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script>
import { getDepartTree } from '@/api/tirosApi'
export default {
  name: 'OrgList',
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      visible: false,
      confirmLoading: false,
      treeData: [],
      defaultSelect: [],
      tableData: [],
      allAlign: 'center',
    }
  },
  created() {
    // this.getTreeList()
  },
  methods: {
    showModal() {
      this.visible = true
      this.getTreeList()
    },
    getTreeList() {
      getDepartTree().then((res) => {
        if (res.success) {
          this.treeData = res.result
        }
      })
    },
    onSelect(selectedKeys, e) {
      this.tableData = e.node.dataRef.children
    },
    handleOk() {
      let selectRecords = []
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords()
      } else {
        if(this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
      this.$emit('ok', selectRecords)
      this.visible = false
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
  },
}
</script>

<style scoped>
.limitTitle {
  font-size: 20px;
  font-weight: 600;
  height: 40px;
}
.limitHeight {
  height: calc(60vh - 78px);
  overflow-y: auto;
}
</style>