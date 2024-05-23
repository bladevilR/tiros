<template>
  <a-card id="trainSysLeftContent">
    <div class="typeTitle">系统名称</div>
    <a-spin :spinning="loading">
      <div class="opBtn">
        <a-config-provider :auto-insert-space-in-button="false">
          <a-button size="small" @click="handleAdd">新增</a-button>
        </a-config-provider>
        <a-config-provider :auto-insert-space-in-button="false">
          <a-button size="small" @click="handleEdit" style="margin-left:5px">修改</a-button>
        </a-config-provider>
        <a-config-provider :auto-insert-space-in-button="false">
          <!-- <a-popconfirm title="确定删除？" @confirm="handleDel" okText="确定" cancelText="取消"> -->
          <a-button size="small" style="margin-left:5px" @click="handleDel">删除</a-button>
          <!-- </a-popconfirm> -->
        </a-config-provider>
      </div>

      <div style="width:70%;margin:0 auto">
        <a-tree
          showLine
          checkStrictly
          :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
          :replaceFields="{title: 'name',key:'id'}"
          :treeData="treeDataSource"
          @select="handleTreeSelect"
          :selectedKeys="defaultKey"
        />
      </div>
    </a-spin>
    <train-type-sys-item-modal ref="modalForm" @ok="loadData" :trainTypeId="trainTypeId"></train-type-sys-item-modal>
  </a-card>
</template>

<script>
import { trainTypeSysList, delTrainTypeSys } from '@/api/tirosApi'
import TrainTypeSysItemModal from './TrainTypeSysItemModal'

export default {
  name: 'TrainTypeSysLeft',
  props: ['trainTypeId', 'value'],
  components: { TrainTypeSysItemModal },
  data () {
    return {
      cardLoading: true,
      loading: false,
      treeDataSource: [],
      selectedNode: {},
      defaultKey: [],
      tStyle: { 'text-align': 'center' }
    }
  },
  created () {
    this.queryTreeData()
  },
  methods: {
    queryTreeData () {
      let p = {
        trainTypeId: this.trainTypeId
      }
      trainTypeSysList(p)
        .then((res) => {
          if (res.success) {
            if (res.result && res.result.length > 0) {
              this.$set(this.defaultKey,0,res.result[0].id)
              this.selectedNode = res.result[0]
              let param = {
                sysId: res.result[0].id,
                sysName: this.selectedNode.name
              }
              this.$emit('input', param)
            }
            this.treeDataSource = res.result
          }
        }).finally(() => {
        this.loading = false
        this.cardLoading = false
      })
    },
    handleTreeSelect (selectedKeys, event) {
      if (selectedKeys.length > 0) {
        this.selectedNode = event.node.dataRef
        let param = {
          sysId: event.node.dataRef.id,
          sysName: event.node.dataRef.name
        }
        this.$emit('input', param)
        this.$set(this.defaultKey,0,selectedKeys[0])
      } else {
        this.selectedNode = {}
        this.defaultKey = []
        this.$emit('input', undefined)
      }
    },
    handleAdd () {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleEdit () {
      if (this.selectedNode.id) {
        this.$refs.modalForm.edit(this.selectedNode)
        this.$refs.modalForm.title = '编辑'
      } else {
        this.$message.error('请先选择一条数据！')
      }
    },
    handleDel () {
      if (this.selectedNode.id) {
        this.$confirm({
          content: `此操作将删除当前系统及其子级数据，是否继续？`,
          okText: '确认',
          cancelText: '取消',
          onOk: () => {
            delTrainTypeSys({ ids: this.selectedNode.id }).then((res) => {
              if (res.success) {
                this.$message.success(res.message)
                // this.$emit('input', '')
                this.loadData()
              } else {
                this.$message.error(res.message)
              }
            })
          }
        })
      } else {
        this.$message.error('请先选择一条数据！')
      }
    },
    loadData () {
      this.queryTreeData()
    }
  }
}
</script>

<style lang="less">
#trainSysLeftContent {
  .ant-card-body {
    padding: 0 0 24px 0;
    height: calc(100vh - 30px);
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