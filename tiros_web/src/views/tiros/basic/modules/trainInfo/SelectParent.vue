<template>
  <!-- 设备类型选择 -->
  <a-modal
    width="90%"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :bodyStyle="{height:'60vh'}"
    :destroyOnClose="true"
  >
    <slot name="title">
      <div class="limitTitle">上级结构选择</div>
    </slot>
    <div class="limitHeight">
      <vxe-table
        border
        row-id="id"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="ellipsis"
        :edit-config="{trigger: 'manual', mode: 'row'}"
        :tree-config="{lazy: true, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenMethod}"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="5%"></vxe-table-column>
        <vxe-table-column field="assetName" title="名称" width="15%" tree-node></vxe-table-column>
        <vxe-table-column field="assetCode" title="编码" width="10%"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="15%"></vxe-table-column>
        <vxe-table-column field="assetType" title="类型" width="10%"></vxe-table-column>
      </vxe-table>
    </div>
  </a-modal>
</template>

<script>
import { getTrainAssetList } from '@/api/tirosApi'
export default {
  name: 'SelectParent',
  props: {
    trainId: {
      type: String,
      default: '',
    },
    multiple: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      queryParam: {
        code: '',
        id: '',
      },
      allAlign: 'center',
      tableData: [],
      selectTreeNode: [],

      visible: false,
      confirmLoading: false,
    }
  },

  methods: {
    showModal() {
      this.visible = true
      this.queryParam = {
        code: this.trainId,
        id: ''
      }
      this.findList()
    },
    handleOk() {
        console.log('eee')
      let selectRecords = []
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords()
      } else {
        if(this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
      console.log(selectRecords)
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
    findList(queryParam) {
      this.loading = true
      getTrainAssetList(this.queryParam).then((res) => {
        if (res.success) {
          this.loading = false
          this.tableData = res.result
        }
      })
    },
    loadChildrenMethod({ row }) {
      this.queryParam.id = row.id
      return new Promise((resolve) => {
        getTrainAssetList(this.queryParam).then((res) => {
          if (res.success) {
            resolve(res.result)
          } else {
            this.$message.error(`${res.msg}`)
          }
        })
      })
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