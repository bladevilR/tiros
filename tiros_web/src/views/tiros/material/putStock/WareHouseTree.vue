<template>
  <!-- 仓位选择弹窗 -->
  <a-modal
    :visible="visible"
    title="库位选择"
    centered
    :bodyStyle="{ maxHeight: '60vh' }"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <div class="scroll-main">
      <a-tree
        checkStrictly
        v-if="treeData.length"
        :tree-data="treeData"
        :default-selected-keys="defaultSelectedKeys"
        :replaceFields="{ title: 'name', key: 'id' }"
        @select="handleTreeSelect"
      />
    </div>
  </a-modal>
</template>

<script>
import { getWarehouseAllTree } from '@/api/tirosMaterialApi'

export default {
  name: 'WareHouseTree',
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
    id: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      queryParam: {
        bookId: '',
      },
      treeData: [],
      visible: false,
      defaultSelectedKeys: [],
      selectRecords: [],
      treeNodeId: '',
    }
  },
  methods: {
    showModal(treeNodeId) {
      this.treeNodeId = treeNodeId
      this.visible = true
      this.findList()
    },
    findList() {
      getWarehouseAllTree().then((res) => {
        if (res.success) {
          if (this.treeNodeId) {
            this.treeData = [this.getNode(res.result, this.treeNodeId)]
          } else {
            this.treeData = res.result
          }
          
          if (id) {
            this.defaultSelectedKeys[0] = id
          } else {
            this.defaultSelectedKeys[0] = res.result[0].id
          }
          // this.$emit('input', this.defaultSelectedKeys[0])
        }
      })
    },
    handleTreeSelect(selecteds, { selectedNodes }) {
      this.selectRecords = []
      selecteds.forEach((element, index) => {
        let record = {
          id: element,
          name: selectedNodes[index].data.props.name,
        }
        this.selectRecords.push(record)
      })
    },
    handleOk() {
      this.$emit('ok', this.selectRecords)
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
    getNode(data, id) {
      let result = {} // 运行结果
      function getTreeItem(data, id) {
        data.map((item) => {
          if (item.id == id) {
            result = item // 结果赋值
          } else {
            if (item.children) {
              getTreeItem(item.children, id)
            }
          }
        })
      }
      getTreeItem(data, id)
      return result
    },
  },
}
</script>
<style lang="less" scoped>
.scroll-main {
  max-height: calc(60vh - 20px);
  overflow-y: scroll;
}
</style>