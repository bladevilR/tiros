<template>
  <a-card id="reguLeftContent">
    <div class="typeTitle">架大修规程</div>
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
        <a-button size="small" @click="handleDel" style="margin-left: 5px" v-has="'regulation:delete'">删除</a-button>
      </a-config-provider>
    </div>

    <div style="width: 95%; margin: 0 auto">
      <a-tree
        :key="treeKey"
        showLine
        checkStrictly
        :default-selected-keys="defaultKey"
        :dropdownStyle="{ maxHeight: '200px', overflow: 'auto' }"
        :replaceFields="{ title: 'title', key: 'id' }"
        :treeData="treeDataSource"
        @select="handleTreeSelect"
      >
      </a-tree>
    </div>
    <left-item-modal ref="modalForm" @ok="loadData"></left-item-modal>
  </a-card>
</template>

<script>
import { getReguList, saveRegu, copyRegu, delRegu } from '@/api/tirosApi'
import LeftItemModal from './LeftItemModal'
import eventBus from '../../../../../utils/goBackEntity'

export default {
  name: 'LeftPage',
  props: ['value', 'trainTypeId'],
  components: { LeftItemModal },
  data() {
    return {
      cardLoading: true,
      loading: false,
      treeDataSource: [],
      selectedKeys: [],
      selectedNode: {},
      defaultKey: [],
      tStyle: { 'text-align': 'center' },
      treeKey: 0,
    }
  },
  mounted() {
    this.queryTreeData()
  },
  methods: {
    forceRerender() {
      ++this.treeKey
    },

    queryTreeData() {
      getReguList()
        .then((res) => {
          if (res.success) {
            let _result = res.result;
            Array.from(_result,(item,index)=>{   //将类数组对象或可迭代对象转化为数组。
                item.title = item.name && JSON.parse(JSON.stringify(item.name));
                if(item.version){
                  item.title += `.${item.version}`;
                }
            })
            // this.selectedNode = [];
            /*if (value && value != null) {
                this.defaultKey[0] = value.id
                this.selectedNode = value
              } else if (this.value != ''&&this.value!=null) {
                this.defaultKey[0] = this.value
                this.selectedNode = { trainTypeId: this.trainTypeId }
              } else {
                this.defaultKey[0] = _result[0].id
                this.selectedNode = _result[0]
              }*/
            if (_result.length > 0) {

              // const defaultKey = this.defaultKey[0]
              // if(!defaultKey){
              //   this.defaultKey[0] = _result[0].id
              //   this.selectedNode = _result[0]
              // }else{
              //   let _arr = _result.filter((item) => {
              //       return  item.id == defaultKey //条件;
              //   });
              //   this.selectedNode = _arr[0]
              // }

              // 修改时不进行自动选中第一个 只有删除才会默认选中第一个1 删除会清除选中信息 编辑不清楚选中  但是要更新数据 // =========/1s
              if (!this.selectedNode.id) {
                this.defaultKey[0] = _result[0].id
                this.selectedNode = _result[0]
              } else {
                const select = _result.filter((item) => {
                  return  item.id === this.selectedNode.id//条件;
                });
                this.selectedNode = select.length ? select[0] : _result[0];
                this.defaultKey[0] = this.selectedNode.id;
              }
              // ================/2e
              let temp = {
                reguId: this.defaultKey[0],
                trainTypeId: this.selectedNode.trainTypeId,
              }
              this.$emit('select', temp)
              this.treeDataSource = _result
            } else {
              this.$emit('select', { reguId: '', trainTypeId: '' })
              this.treeDataSource = []
            }
            this.forceRerender()
          }
        })
        .finally(() => {
          this.loading = false
          this.cardLoading = false
        })
    },
    handleTreeSelect(selectedKeys, event) {
      
      let temp
      if (selectedKeys.length > 0) {
        this.selectedNode = event.node.dataRef
        this.selectedKeys = [selectedKeys[0]]
        temp = {
          reguId: event.node.dataRef.id,
          trainTypeId: event.node.dataRef.trainTypeId,
        }
      } else {
        this.selectedNode = {}
        temp = { reguId: '', trainTypeId: '' }
      }
      this.$emit('select', temp)
      console.log(this.selectedNode.length , this.selectedNode)
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
        this.loading=true
        copyRegu(this.selectedNode.id).then((res) => {
          if (res.success) {
            this.$message.success(res.message)
            this.queryTreeData()
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
          content: `此操作将删除当前选中记录及其子级数据，是否继续？`,
          okText: '确认',
          cancelText: '取消',
          onOk: () => {
            let id = this.selectedKeys[0] ? this.selectedKeys[0] : this.defaultKey[0]
            delRegu(id).then((res) => {
              if (res.success) {
                this.$message.success(res.message)
                this.flush()
                this.queryTreeData()
              } else {
                this.$message.warning(res.message)
              }
            })
          },
        })
      } else {
        this.$message.error('请先选择一条数据！')
      }
    },
    loadData(val) {
      console.log(val)
      if (val) {
        // this.selectedKeys = []
        // this.selectedNode = {}
        // this.defaultKey = []
      }
      this.queryTreeData()
    },
    flush() {
      this.defaultKey = [];
      this.selectedNode = {}
      this.loadData()
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

  .opBtn {
    padding: 10px 0;
    text-align: center;
  }
}
</style>