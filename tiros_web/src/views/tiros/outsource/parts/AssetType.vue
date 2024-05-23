<template>
  <!-- 设备类型选择 -->
  <a-modal
    width="90%"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :bodyStyle="{ height: '60vh' }"
    :destroyOnClose="true"
  >
    <slot name="title">
      <div class="limitTitle">设备类型选择</div>
    </slot>
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item  label="车型">
              <j-dict-select-tag
                v-model="queryParam.trainTypeId"
                placeholder="请选择车型"
                dictCode="bu_train_type,name,id"/>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="名称或编码" v-model="queryParam.searchText" allow-clear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="5" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag v-model="queryParam.status" dictCode="bu_enable" />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="上级结构">
              <a-tree-select
                tree-data-simple-mode
                allow-clear
                :tree-data="selectTreeNode"
                placeholder="请选择"
                :load-data="loadSelectNodeMethod"
                v-model="queryParam.id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="12">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-button @click="searchQuery">查询</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="limitHeight">
      <vxe-table
        border
        row-id="id"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="ellipsis"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        :checkbox-config="{ checkStrictly: true }"
        :tree-config="{ lazy: true, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenMethod }"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
        <vxe-table-column field="name" title="名称" tree-node align="left"></vxe-table-column>
        <vxe-table-column field="code" title="编码" width="15%"></vxe-table-column>
        <vxe-table-column field="structType_dictText" title="类型" width="10%"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="10%"></vxe-table-column>
        <vxe-table-column field="placeCode" title="位置编码" width="15%"></vxe-table-column>
        <vxe-table-column field="placeDesc" title="位置描述" width="15%"></vxe-table-column>
      </vxe-table>
    </div>
  </a-modal>
</template>

<script>
import { trainTypeSysList } from '@/api/tirosApi'
export default {
  name: 'TrainAssetType',
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      queryParam: {
        trainTypeId: '',
        id: '',
        searchText: '',
        status: 1,
        structType: null,
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
      this.queryParam.trainTypeId = this.trainTypeId
      this.findSelectList()
      this.findList()
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
    findList(queryParam) {
      this.loading = true
      trainTypeSysList(this.queryParam).then((res) => {
        if (res.success) {
          this.loading = false
          this.tableData = res.result
        }
      })
    },
    findSelectList() {
      trainTypeSysList({ trainTypeId: this.trainTypeId }).then((res) => {
        if (res.success) {
          this.loading = false
          this.selectTreeNode = res.result.map((item) => {
            return this.genSelectTreeNode(item)
          })
        }
      })
    },

    loadChildrenMethod({ row }) {
      let param = {
        trainTypeId: this.trainTypeId,
        id: row.id,
      }
      return new Promise((resolve) => {
        trainTypeSysList(param).then((res) => {
          if (res.success) {
            resolve(res.result)
          } else {
            this.$message.error(`${res.msg}`)
          }
        })
      })
    },
    loadSelectNodeMethod(node) {
      let param = {
        trainTypeId: this.trainTypeId,
        id: node.dataRef.id,
      }
      return new Promise((resolve) => {
        trainTypeSysList(param).then((res) => {
          if (res.success) {
            let childrenNode = res.result.map((item) => {
              this.selectTreeNode = this.selectTreeNode.concat(this.genSelectTreeNode(item))
            })
          } else {
            this.$message.warning(res.message)
          }
        })
        resolve()
      })
    },

    genSelectTreeNode(node) {
      return {
        id: node.id,
        pId: node.parentId,
        value: node.id,
        title: node.name,
        isLeaf: node.hasChildren ? false : true,
      }
    },

    searchQuery() {
      this.findList()
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
  height: calc(60vh - 170px);
  overflow-y: auto;
}
</style>