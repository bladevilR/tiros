<template>
  <a-card id="reguLeftContent">
    <div class="typeTitle">组织机构</div>
    <div style="width:70%;margin:0 auto">
      <a-tree
        v-if="treeDataSource.length"
        showLine
        checkStrictly
        :defaultExpandAll="true"
        :default-selected-keys="defaultKey"
        :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
        :replaceFields="{title: 'name',key:'id'}"
        :treeData="treeDataSource"
        @select="handleTreeSelect"
      />
    </div>
  </a-card>
</template>

<script>
import { getStationtree } from '@/api/tirosApi'

export default {
  name: 'LeftPage',
  data() {
    return {
      cardLoading: true,
      loading: false,
      treeDataSource: [],
      selectedNode: {},
      defaultKey: [],
      curWorkShopDetail: {},
      tStyle: { 'text-align': 'center' },
    }
  },
  created() {
    this.queryTreeData()
  },
  methods: {
    queryTreeData() {
      getStationtree()
        .then((res) => {
          if (res.success) {
            this.defaultKey[0] = res.result[0].id
            this.treeDataSource = res.result
            this.$emit('input', '')
          }
        })
        .finally(() => {
          this.loading = false
          this.cardLoading = false
        })
    },
    handleTreeSelect(selectedKeys, e) {
      if (selectedKeys.length) {
        if (e.node.dataRef.type === 3) {
          this.$emit('input', e.node.dataRef.id)
        } else {
          this.$emit('input', "")
        }
        this.setNodeDetail(selectedKeys, e.node)
      } else {
        for (let key in this.curWorkShopDetail) {
          this.curWorkShopDetail[key] = ''
        }
        this.$emit('input', '')
        this.$emit('select', this.curWorkShopDetail)
      }
    },
    setNodeDetail(selectedKeys, node) {
      if (node.dataRef.type === 3) {
        this.curWorkShopDetail.workshopId = selectedKeys[0] || ''
        this.curWorkShopDetail.workshopName = node.title || ''
        this.curWorkShopDetail.depotId = node.vcTreeNode.dataRef.id || ''
        this.curWorkShopDetail.depotName = node.vcTreeNode.title || ''
        this.curWorkShopDetail.companyName = node.vcTreeNode.vcTreeNode.title || ''
        this.$emit('select', this.curWorkShopDetail)
      } else {
        this.curWorkShopDetail = {}
        this.$emit('select', this.curWorkShopDetail)
      }
    },
    loadData() {
      this.queryTreeData()
    },
  },
}
</script>

<style lang="less">
#reguLeftContent {
  .ant-card-body {
    padding: 0 0 24px 0;
    height: calc(100vh - 120px);
    overflow-y: auto;
  }
  .typeTitle {
    background: #eee;
    padding: 15px;
    text-align: center;
  }
}
</style>