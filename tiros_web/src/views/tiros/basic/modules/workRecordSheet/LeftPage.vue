<template>
  <a-card id="RecordLeftContent">
    <div class="typeTitle">作业项目</div>
    <div class="opBtn">
      <a-config-provider :auto-insert-space-in-button="false">
        <a-button size="small" @click="handleAdd">新增</a-button>
      </a-config-provider>
      <a-config-provider :auto-insert-space-in-button="false">
        <a-button size="small" @click="handleEdit" style="margin-left: 5px">修改</a-button>
      </a-config-provider>
      <a-config-provider :auto-insert-space-in-button="false">
        <a-button size="small" style="margin-left: 5px" @click="handleDel">删除</a-button>
      </a-config-provider>
    </div>

    <div style="width: 90%; margin: 0 auto">
      <!-- <a-tree
        showLine
        checkStrictly
        :default-selected-keys="defaultKey"
        :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
        :replaceFields="{key:'id',title:'reguTitle'}"
        :treeData="treeDataSource"
        @select="handleTreeSelect"
      /> -->
      <vxe-table
        border
        :stripe="false"
        row-id="id"
        ref="listTable"
        :align="allAlign"
        :data="treeDataSource"
        show-overflow="tooltip"
        :radio-config="{ highlight: true ,trigger: 'row'}"
        @radio-change="radioChangeEvent"
      >
        <vxe-table-column type="radio" width="40"></vxe-table-column>
        <!-- <vxe-table-column type="checkbox" width="40"></vxe-table-column> -->
        <vxe-table-column field="recIndex" title="序号" width="60"></vxe-table-column>
        <vxe-table-column field="reguTitle" title="项目名称" align="left" header-align="center"></vxe-table-column>
      </vxe-table>
    </div>
    <cat-item-modal ref="modalForm" :recordId="recordId" @ok="loadData()"></cat-item-modal>
  </a-card>
</template>

<script>
import { listOldWorkRecordCategory, deleteOldWorkRecordCategory } from '@/api/tirosApi'
import CatItemModal from './CatItemModal'

export default {
  name: 'TrainTypeSysLeft',
  props: ['recordId', 'value'],
  components: { CatItemModal },
  data() {
    return {
      cardLoading: true,
      loading: false,
      treeDataSource: [],
      selectedNode: {},
      defaultKey: [],
      allAlign: 'center',
      tStyle: { 'text-align': 'center' },
    }
  },
  created() {
    this.queryTreeData()
  },
  methods: {
    queryTreeData() {
      listOldWorkRecordCategory(this.recordId)
        .then((res) => {
          if (res.success) {
            this.defaultKey[0] = res.result[0].id
            this.selectedNode = res.result[0]
            this.$emit('input', res.result[0].id)
            this.treeDataSource = res.result
            this.$refs.listTable.setRadioRow(this.treeDataSource[0])
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
        let catId = event.node.dataRef.id
        this.$emit('input', catId)
      } else {
        this.selectedNode = {}
      }
    },
    radioChangeEvent({ row }) {
      this.selectedNode = row
      let catId = row.id
      this.$emit('input', catId)
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
    handleDel() {
      this.$confirm({
        content: '是否删除该作业项目',
         okText:'确认',
        cancelText:'取消',
        onOk: () => {
          if (this.selectedNode.id) {
            deleteOldWorkRecordCategory({ ids: this.selectedNode.id }).then((res) => {
              if (res.success) {
                this.$message.success(res.message)
                this.selectedNode = {}
                this.loadData()
              } else {
                this.$message.error(res.message)
              }
            })
          } else {
            this.$message.error('请先选择一条数据！')
          }
        },
      })

    },
    loadData() {
      this.queryTreeData()
    },
  },
}
</script>

<style  lang="less">
#RecordLeftContent {
  .ant-card-body {
    padding: 0 0 24px 0;
    height: calc(100vh - 70px);
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