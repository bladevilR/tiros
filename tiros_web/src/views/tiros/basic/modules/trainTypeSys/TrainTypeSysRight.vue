<template>
  <a-card id="trainSysRightContent">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="名称或编码" v-model="queryParam.searchText" allow-clear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag v-model="queryParam.status" dictCode="bu_enable" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="上级结构">
              <a-tree-select
                tree-data-simple-mode
                allow-clear
                :tree-data="selectTreeNode"
                placeholder="请选择"
                :load-data="loadSelectNodeMethod"
                v-model="queryParamId"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="12">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
              <a-button @click="searchQuery">查询</a-button>
              <a-button type="primary" @click="handleAdd">新增</a-button>
              <a-button @click="handleDel">删除</a-button>
               <a-button @click="handleBack">关闭</a-button>

                </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 115px)">
      <vxe-table
        max-height="100%"
        style="height: calc(100vh - 115px)"
        border
        row-id="id"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :edit-config="{trigger: 'manual', mode: 'row'}"
        :tree-config="{lazy: true, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenMethod}"
        :checkbox-config="{trigger: 'row', highlight: true,checkStrictly: true}">

        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="name" title="名称" tree-node align="left" header-align="center"></vxe-table-column>
        <vxe-table-column field="code" title="编码" width="150" align="left" header-align="center"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="100"></vxe-table-column>
        <vxe-table-column field="structType_dictText" title="类型" width="100"></vxe-table-column>
        <vxe-table-column field="turnover_dictText" title="是否周转件" width="100"></vxe-table-column>
<!--        <vxe-table-column field="placeId" title="位置编码" width="100" align="left"
                          header-align="center"></vxe-table-column>
        <vxe-table-column field="placeDesc" title="位置描述" width="100" align="left"
                          header-align="center"></vxe-table-column>-->
        <vxe-table-column title="操作" width="80">
          <template v-slot="{ row }">
            <a v-if="row.parentId" @click="handleEdit(row)">编辑</a>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    <train-type-sys-modal
      ref="modalForm"
      @ok="loadData()"
      :parent="selectParent"
      :trainTypeId="trainTypeId"
    ></train-type-sys-modal>
  </a-card>
</template>

<script>
import TrainTypeSysModal from './TrainTypeSysModal'
import { trainTypeSysList, delTrainTypeSys } from '@/api/tirosApi'
import { everythingIsEmpty } from '@/utils/util'

export default {
  components: { TrainTypeSysModal },
  name: 'TrainTypeSysRight',
  props: ['trainTypeId', 'value'],
  data () {
    return {
      queryParam: {
        trainTypeId: this.trainTypeId,
        id: '',
        searchText: '',
        status: null,
        structType: null
      },
      queryParamId: '',
      allAlign: 'center',
      tableData: [],
      selectTreeNode: [],
      selectParent: {},
      parentId: ''
    }
  },
  /*created() {
    this.findSelectList()
  },*/
  watch: {
    value: {
      immediate: false,
      handler (info) {
        this.queryParam.id = info ? info.sysId : ''
        this.parentId = info ? info.sysId : ''
        this.selectParent = info ? info : {}
        this.queryParamId = ''
        this.dataSource = []
        this.findList()
        this.findSelectList()
      }
    }
  },
  methods: {
    findList () {
      this.loading = true
      trainTypeSysList(this.queryParam).then((res) => {
        if (res.success) {
          this.loading = false
          this.tableData = res.result
        }
      })
    },
    findSelectList () {
      trainTypeSysList({ trainTypeId: this.trainTypeId }).then((res) => {
        if (res.success) {
          this.loading = false
          this.selectTreeNode = res.result.map((item) => {
            return this.genSelectTreeNode(item)
          })
        }
      })
    },

    loadChildrenMethod ({ row }) {
      let param = {
        trainTypeId: this.trainTypeId,
        id: row.id,
        searchText: this.queryParam.searchText,
        status: this.queryParam.status,
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
    loadSelectNodeMethod (node) {
      let param = {
        trainTypeId: this.trainTypeId,
        id: node.dataRef.id
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

    genSelectTreeNode (node) {
      return {
        id: node.id,
        pId: node.parentId,
        value: node.id,
        title: node.name,
        isLeaf: !node.hasChildren
      }
    },

    searchQuery () {
      if (everythingIsEmpty(this.queryParamId)) {
        this.queryParam.id = this.parentId
      } else {
        this.queryParam.id = this.queryParamId
      }
      this.findList()
    },
    /* selectMethod(){
       if(!everythingIsEmpty(this.queryParam.searchText)||
         !everythingIsEmpty(this.queryParam.status)||
         !everythingIsEmpty(this.queryParamId)){
         this.searchList()
       }else {

       }
     },*/

    handleAdd () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.length == 1) {
          this.selectParent = {
            sysId: selectRecords[0].id,
            sysName: selectRecords[0].name
          }
        } else {
          this.$message.error('只能选择一个上级节点!')
          return
        }
      } else {
        this.selectParent = this.value
      }
      setTimeout(() => {
        this.$refs.modalForm.add()
        this.$refs.modalForm.title = '新增'
      }, 0)
    },
    handleEdit (record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
    },
    handleDel () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `此操作将删除当前设备及其子级数据，是否继续？`,
          okText: '确认',
          cancelText: '取消',
          onOk: () => {
            var idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delTrainTypeSys({ ids: idsStr }).then((res) => {
              if (res.success) {
                this.$message.success(res.message)
                this.loadData()
              } else {
                this.$message.error(res.message)
              }
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    loadData () {
      this.findList()
    },
    handleBack () {
      this.$emit('close')
    }
  }
}
</script>

<style lang="less">
#trainSysRightContent {
  .ant-card-body {
    padding: 10px;
    height: calc(100vh - 30px);
    overflow-y: auto;
  }

  .tableHeight {
    min-height: calc(100vh - 255px);
    // overflow-y: auto;
  }
}
</style>