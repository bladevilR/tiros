<template>
  <div>
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="名称或编码" v-model="queryParam.title" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag v-model="queryParam.status" dictCode="bu_effective"/>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="上级">
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
          <a-col :md="10" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
               <a-space>
                  <a-button @click="searchQuery">查询</a-button>
               </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-spin :spinning="loading" >
    <div class="tableHeight" style="height: calc(100vh - 290px)">
      <vxe-table
        border
        resizable
        row-id="id"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        max-height="100%"
        style="height: calc(100vh - 290px)"
        :checkbox-config="{trigger: 'row', highlight: true,checkStrictly: true}"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        :tree-config="{ lazy: true, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenMethod }"
      >
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column field="assetName" title="设备名称" tree-node align="left" header-align="center" min-width="120"></vxe-table-column>
        <vxe-table-column field="assetNo" title="设备编码" width="120"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="100"></vxe-table-column>
        <vxe-table-column field="structType_dictText" title="设备类型" width="100"></vxe-table-column>
        <vxe-table-column field="vendor" title="厂商" width="120"></vxe-table-column>
        <vxe-table-column field="brand" title="品牌" width="120"></vxe-table-column>
        <vxe-table-column field="manufNo" title="出厂编号" width="120"></vxe-table-column>
        <vxe-table-column field="model" title="规格类型" min-width="140" align="left" header-align="center"></vxe-table-column>
      </vxe-table>
    </div>
    </a-spin>
  </div>
</template>

<script>
import {  getTrainAssetList } from '@api/tirosApi'

export default {
  name: 'TrainAssetInfo',
  props: ['trainNo'],
  data() {
    return {
      trainId: '',
      trainTypeId: '',
      selectParent: {},
      queryParam: {
        code: this.trainNo
      },
      allAlign: 'center',
      tableData: [],
      selectTreeNode: [],
      selectPid: '',
      loading:false
    }
  },
  created() {
     this.findSelectList()
     this.findList()
  },
  methods: {
    findList() {
      this.loading = true
      getTrainAssetList(this.queryParam).then((res) => {
        if (res.success) {
          this.tableData = res.result
        }
      }).finally(()=>this.loading = false)
    },
    findSelectList() {
      let param = {
        code: this.trainNo
      }
      getTrainAssetList(param).then((res) => {
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
        code: this.trainNo,
        id: row.id,
        searchText: this.queryParam.searchText,
        status: this.queryParam.status,
      }
      return new Promise((resolve) => {
        getTrainAssetList(param).then((res) => {
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
        code: this.trainId,
        id: node.dataRef.id
      }
      return new Promise((resolve) => {
        getTrainAssetList(param).then((res) => {
          if (res.success) {
            let childrenNode = res.result.map((item) => {
              this.selectTreeNode = this.selectTreeNode.concat(this.genSelectTreeNode(item))
            })
          } else {
            this.$message.error(res.message)
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
        title: node.assetName,
        isLeaf: node.hasChildren ? false : true
      }
    },

    searchQuery() {
      this.findList()
    },


    loadData() {
      this.queryParam.id = ''
      this.findSelectList()
      this.findList()
    },
  }
}
</script>

<style scoped>

</style>