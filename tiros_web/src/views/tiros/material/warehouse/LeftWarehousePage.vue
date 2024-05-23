<template>
  <a-card id="leftWarehouse">
    <div class="typeTitle">
      <a-icon type="database" style="margin-right: 5px" />
      仓库管理
    </div>
    <!--    <div class="btn">
      <a-button>同步仓库物资信息</a-button>
    </div>-->
    <div style="width: 80%; margin: 0 auto">
      <a-tree
        checkStrictly
        v-if="treeData.length"
        :tree-data="treeData"
        :default-selected-keys="defaultSelectedKeys"
        :replaceFields="{ title: 'name', key: 'id' }"
        @select="handleTreeSelect"
      />
    </div>
  </a-card>
</template>
<script>
import { getWarehouseAllTree } from '@/api/tirosMaterialApi'

export default {
  name: 'LeftWarehouse',
  props: ['value'],
  data() {
    return {
      defaultSelectedKeys: [],
      selectedKeys: [],
      selectedNode: {},
      treeData: [],
      queryParam: {
        parentId: '',
      },
    }
  },
  created() {
    this.queryTreeData()
  },
  methods: {
    handleTreeSelect(selectedKeys, event) {
      let selectKey
      if (selectedKeys.length > 0) {
        selectKey = selectedKeys[0]
      }
      this.$emit('input', selectKey)
    },
    queryTreeData(id) {
      getWarehouseAllTree().then((res) => {
        if (res.success) {
          this.treeData = res.result
          if (id) {
            this.defaultSelectedKeys[0] = id
          } else {
            this.defaultSelectedKeys[0] = res.result[0].id
          }
          this.$emit('input', this.defaultSelectedKeys[0])
        }
      })
    },
    onSelectTree(id) {
      this.queryTreeData(id)
    },
  },
}
</script>
<style lang="less">
#leftWarehouse {
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

  .btn {
    padding: 10px 0;
    text-align: center;
    width: 100%;
  }
}
</style>