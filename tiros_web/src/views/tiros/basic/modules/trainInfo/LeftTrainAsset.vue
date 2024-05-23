<template>
  <a-card id="leftTrainAsset">
    <div class="typeTitle">
      <a-icon type="database" style="margin-right: 5px"/>
      车辆列表
    </div>
    <div style="width:100%; height: calc(100% - 52px); padding:0 10%; overflow-y: auto;">
      <a-tree
        show-line
        checkStrictly
        v-if="treeData.length"
        :tree-data="treeData"
        :default-selected-keys='defaultSelectedKeys'
        :replaceFields="{title:'name',key:'name'}"
        :expandedKeys.sync="expandedKeys"
        @select="handleTreeSelect"
      />
    </div>
  </a-card>

</template>
<script>
import { everythingIsEmpty } from '@/utils/util'
import { getTrainInfoTree } from '@api/tirosApi'

export default {
  name: 'leftTrainAsset',
  props: ['value'],
  data() {
    return {
      defaultSelectedKeys: [],
      selectedKeys: [],
      selectedNode: {},
      treeData: [],
      expandedKeys:[],
      queryParam: {
        parentId: ''
      }
    }
  },
  created() {
   // this.queryTreeData()
  },
  methods: {
    handleTreeSelect(selectedKeys, event) {
      let temp = {}
      if (selectedKeys.length > 0) {
        temp = { trainId: selectedKeys[0], trainTypeId: event.node.dataRef.trainTypeId }
      }
      this.$emit('input', temp)
    },
    queryTreeData() {
      getTrainInfoTree().then((res) => {
        if (res.success) {
          this.treeData = res.result
          if (!everythingIsEmpty(this.value)) {
            this.defaultSelectedKeys[0] = this.value.trainId
          } else {
            this.defaultSelectedKeys[0] = res.result[0].name
            let temp = { trainId: res.result[0].name, trainTypeId: res.result[0].trainTypeId }
            this.$emit('input', temp)
          }
          this.$nextTick((e) =>{
            this.expandedKeys = this.treeData.filter(item => item.children.find(e => e.name === this.value.trainId)).map(item => item.name)
          })
        }
      })
    },
    onSelectTree(id) {
      this.queryTreeData(id)
    }
  }

}
</script>
<style lang="less">
#leftTrainAsset {
  height: 100%;

  .ant-card-body {
    padding: 0 0 24px 0;
    height: 100%;
  }

  .typeTitle {
    background: #eee;
    padding: 15px;
    text-align: center;
  }

  .btn {
    padding: 10px 0;
    text-align: center;
    width: 100%;

  }
}
</style>