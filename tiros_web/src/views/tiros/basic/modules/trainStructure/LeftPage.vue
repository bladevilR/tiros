<template>
  <a-card id="reguLeftContent">
    <div class="typeTitle">车辆结构</div>
    <div class="opBtn">
      <a-config-provider :auto-insert-space-in-button="false">
        <a-button size="small" @click="handleAdd">新增</a-button>
      </a-config-provider>
      <a-config-provider :auto-insert-space-in-button="false">
        <a-button size="small" @click="handleCopy" style="margin-left: 5px" :loading="loading">复制</a-button>
      </a-config-provider>
      <a-config-provider :auto-insert-space-in-button="false">
        <a-button size="small" @click="handleEdit" style="margin-left: 5px">修改</a-button>
      </a-config-provider>
      <a-config-provider :auto-insert-space-in-button="false">
        <a-button size="small" @click="handleDel" style="margin-left: 5px">删除</a-button>
      </a-config-provider>
    </div>

    <div style="width: 70%; margin: 0 auto">
      <a-tree
        v-if="treeDataSource.length"
        showLine
        checkStrictly
        :default-selected-keys="defaultKey"
        :dropdownStyle="{ maxHeight: '200px', overflow: 'auto' }"
        :replaceFields="{ title: 'name', key: 'id' }"
        :treeData="treeDataSource"
        @select="handleTreeSelect"
      />
    </div>
    <left-item-modal ref="modalForm" @ok="loadData()"></left-item-modal>
  </a-card>
</template>

<script>
import LeftItemModal from './LeftItemModal'
import { getTrainStructList, delTrainStruct, copyTrainStruct } from '@/api/tirosApi'

export default {
  name: 'LeftPage',
  components: { LeftItemModal },
  data() {
    return {
      cardLoading: true,
      loading: false,
      treeDataSource: [],
      selectedNode: {},
      defaultKey: [],
      tStyle: { 'text-align': 'center' }
    }
  },
  created() {
    this.queryTreeData()
  },
  methods: {
    queryTreeData() {
      getTrainStructList()
        .then((res) => {
          if (res.success) {
            this.treeDataSource = res.result
            this.selectedNode = res.result[0]
            if (res.result[0]) {
              this.defaultKey[0] = res.result[0].id
              let temp = {
                structId: this.defaultKey[0],
                typeId: res.result[0].trainTypeId
              }
              this.$emit('select', temp)
            }
          }
        })
        .finally(() => {
          this.loading = false
          this.cardLoading = false
        })
    },
    handleTreeSelect(selectedKeys, event) {
      if (selectedKeys.length > 0) {
        this.selectedNode = event.node.dataRef
        let structId = event.node.dataRef.id
        let typeId = event.node.dataRef.trainTypeId
        let temp = {
          structId: structId,
          typeId: typeId
        }
        this.$emit('select', temp)
      } else {
        this.selectedNode = {}
        let temp = {
          structId: '',
          typeId: ''
        }
        this.$emit('select', temp)
      }
    },
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleEdit() {
      if (this.selectedNode.id) {
        this.$refs.modalForm.edit(this.selectedNode)
        this.$refs.modalForm.title = '编辑'
      } else {
        this.$message.error('请先选择一条数据！')
      }
    },
    handleCopy() {

      if (this.selectedNode.id) {
        console.log(this.selectedNode)
        let p = {
          code: this.selectedNode.code,
          id: this.selectedNode.id,
          lineId: this.selectedNode.lineId,
          name: this.selectedNode.name,
          remark: this.selectedNode.remark
        }
        this.loading=true
        copyTrainStruct(p).then((res) => {
          if (res.success) {
            this.$message.success(res.message)
            this.loadData()
          } else {
            this.$message.warning(res.message)
          }
        }).finally(()=>this.loading=false)
      } else {
        this.$message.error('请先选择一条数据！')
      }
    },
    handleDel() {
      if (this.selectedNode.id) {
        this.$confirm({
          content: `此操作将删除当前系统及其子级数据，是否继续？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            delTrainStruct({ ids: this.selectedNode.id }).then((res) => {
              if (res.success) {
                this.$message.success(res.message)

                // this.$emit('input', '')
                this.loadData()
              } else {
                this.$message.warning(res.message)
              }
            })
          }
        })
      } else {
        this.$message.error('请先选择一条数据！')
      }
    },
    loadData() {
      this.treeDataSource = []
      this.queryTreeData()
    }
  }
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

  .opBtn {
    padding: 10px 0;
    text-align: center;
  }
}
</style>