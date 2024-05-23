<template>
  <a-card id="leftEarlyWarningPage">
    <div class="typeTitle">
      <a-icon type="database" style="margin-right: 5px"/>
      仓库管理
    </div>
    <!--    <div class="btn">
          <a-button>同步仓库物资信息</a-button>
        </div>-->
    <div style="width:80%;margin:0 auto">
      <a-tree
        v-if="treeData.length"
        :tree-data="treeData"
        :replaceFields="{title: 'name',key:'id'}"
        :defaultSelectedKeys='defaultSelectedKeys'
        @select="selectPid"
        :load-data="onLoadData"
      />
    </div>
  </a-card>
</template>

<script>
import { listWarehouseChildren } from '@api/tirosMaterialApi'

export default {
  name: 'LeftEarlyWarningPage',
  props: ['value'],
  data() {
    return {
      defaultSelectedKeys: [],
      treeData: [],
      queryParam: {
        parentId: ''
      }
    }

  },
  mounted() {
    this.queryTreeData()
  },
  methods: {
    queryTreeData() {
      listWarehouseChildren(this.queryParam).then((res) => {
        if (res.success) {
          this.treeData = res.result
          this.defaultSelectedKeys = res.result.id
          this.$emit('input', this.defaultSelectedKeys)
        }
      })
    },
    onLoadData(treeNode) {
      let param = {
        parentId: treeNode.dataRef.id
      }
      return new Promise(resolve => {
        listWarehouseChildren(param).then((res) => {
          if (res.success) {
            treeNode.dataRef.children = res.result
            treeNode.dataRef.children.map((item) => {
              item.isLeaf = !item.hasChildren
            })
            this.treeData = [...this.treeData]
          } else {
            this.$message.warning(res.message)
          }
          resolve()
        })
      })
    },
    onSelectTree(id) {
      let param = {
        parentId: id
      }
      listWarehouseChildren(param).then((res) => {
        if (res.success) {
          let data = res.result
          data.map((item) => {
            item.isLeaf = !item.hasChildren
          })
          this.treeData = [...this.treeData]
        } else {
          this.$message.warning(res.message)
        }
      })
    },

    selectPid(selectedKeys, e) {
      this.$emit('input', selectedKeys[0])
    }
  }

}
</script>

<style lang="less">
#leftEarlyWarningPage {
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