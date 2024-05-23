<template>
  <!-- 设备类型选择 -->
  <a-modal
    width="90%"
    centered
    :title="title"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
    :bodyStyle="{ height: '80vh' }"
  >
    <a-row type="flex" :gutter="16">
      <a-col :md="4" :sm="24">
        <div style="width:70%;margin:0 auto">
          <a-tree
            showLine
            checkStrictly
            :default-selected-keys="defaultKey"
            :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
            :replaceFields="{title: 'name',key:'id'}"
            :treeData="treeDataSource"
            @select="handleTreeSelect"
          />
        </div>
      </a-col>
      <a-col :md="24-4" :sm="24">
        <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">
              <a-col :md="7" :sm="24">
                <a-form-item label="名称">
                  <a-input placeholder="名称或编码" v-model="queryParam.searchText" allow-clear></a-input>
                </a-form-item>
              </a-col>

              <a-col :md="7" :sm="24">
                <a-form-item label="状态">
                  <j-dict-select-tag v-model="queryParam.status" dictCode="bu_enable" />
                </a-form-item>
              </a-col>
              <a-col :md="7" :sm="24">
                <a-form-item label="上级结构">
                  <!--v-model="queryParam.id"-->
                  <a-tree-select
                    tree-data-simple-mode
                    allow-clear
                    :tree-data="treeData"
                    placeholder="请选择"
                    v-model="selectParent"
                    :load-data="loadSelectNodeMethod"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="3" :sm="12">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-button @click="searchQuery">查询</a-button>
            </span>
              </a-col>
            </a-row>
          </a-form>
        </div>
        <div class="limitHeight">
          <vxe-table
            border
            max-height="90%"
            row-id="id"
            ref="listTable"
            :align="allAlign"
            :data="tableData"
            show-overflow="tooltip"
            :radio-config="!multiple ? {trigger: 'row', checkMethod: checkRadioMethod} : {}"
            :checkbox-config="multiple ? {checkStrictly: true, trigger: 'row', highlight: true, range: true, checkMethod: checkRadioMethod } : {}"
            :tree-config="{ lazy: true, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenMethod }"
          >
            <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
            <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
            <vxe-table-column field="name" title="名称" tree-node align="left"></vxe-table-column>
            <vxe-table-column field="code" title="编码" width="15%"></vxe-table-column>
            <vxe-table-column field="structType_dictText" title="类型" width="10%"></vxe-table-column>
            <vxe-table-column field="status_dictText" title="状态" width="10%"></vxe-table-column>
            <vxe-table-column field="placeCode" title="位置编码" width="15%"></vxe-table-column>
            <vxe-table-column field="placeDesc" title="位置描述" width="15%"></vxe-table-column>
          </vxe-table>
        </div>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script>
import { getTrainTypeListAll, trainTypeSysList } from '@/api/tirosApi'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'TrainAssetTypeTrainType',
  props: {
    trainTypedefId: {
      type: String,
      default: null
    },
    parentId: {
      type: String,
      default: null
    },
    multiple: {
      type: Boolean,
      default: true
    },
    title: {
      type: String,
      default: '设备类型选择'
    },
    // 1 系统 2子系统 3 部件 4 子部件
    canSelectType: {
      type: Array,
      default: () => {
        return [1, 2, 3, 4]
      }
    }
  },
  watch: {
    trainTypeId: {
      immediate: true,
      handler(id) {
        this.findSelectList()
        this.findList()
      }
    }
  },
  data () {
    return {
      trainTypeId:'',
      queryParam: {
        trainTypeId: '',
        id: '',
        searchText: '',
        status: null,
        structType: null
      },
      allAlign: 'center',
      tableData: [],
      treeData: [],
      selectParent: '',
      visible: false,
      confirmLoading: false,
      treeDataSource: [],
      defaultKey: [],
      tStyle: { 'text-align': 'center' }
    }
  },
  mounted () {
  },
  methods: {
    queryTreeData () {
      getTrainTypeListAll().then((res) => {
        if (res.success) {
          if(this.trainTypedefId){
            this.defaultKey[0] = this.trainTypedefId
          }else{
            this.defaultKey[0] = res.result[0].id
          }
          this.treeDataSource = res.result
          this.trainTypeId=this.defaultKey[0]
        }
      })
    },
    handleTreeSelect (selectedKeys, event) {
      if (selectedKeys.length > 0) {
        this.trainTypeId=event.node.dataRef.id
      }else{
        this.trainTypeId=''
      }
    },
    showModal () {
      this.visible = true
      this.queryTreeData()
    },
    handleOk () {
      let selectRecords = []
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords()
      } else {
        if(this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
      this.$emit('ok', selectRecords)
      this.visible = false
    },
    // 关闭
    handleCancel () {
      this.$emit('cancel')
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
    },
    // 加载列表数据
    findList () {
      this.loading = true
     /* if (this.parentId) {
        this.queryParam.id = this.parentId
      }
      if (this.selectParent) {
        this.queryParam.id = this.selectParent
      }*/
        this.queryParam.id = this.selectParent
        this.queryParam.trainTypeId=this.trainTypeId
      trainTypeSysList(this.queryParam).then((res) => {
        if (res.success) {
          this.loading = false
          this.tableData = res.result
        }
      })
    },
    // 加载上级结构的数据
    findSelectList () {
      const query = { id: this.parentId, trainTypeId: this.trainTypeId }
      if (this.parentId) {
        query.id = this.parentId
      }
      trainTypeSysList(query).then((res) => {
        if (res.success) {
          this.loading = false
          this.treeData = res.result.map((item) => {
            return this.genSelectTreeNode(item)
          })
        }
      })
    },
    // 加载列表子节点
    loadChildrenMethod ({ row }) {
      let param = {
        trainTypeId: this.trainTypeId,
        id: row.id
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
    // 加载上级节点树形节点的子数据
    loadSelectNodeMethod (node) {
      let param = {
        trainTypeId: this.trainTypeId,
        id: node.dataRef.id
      }
      return new Promise((resolve) => {
        trainTypeSysList(param).then((res) => {
          if (res.success) {
            res.result.map((item) => {
              this.treeData = this.treeData.concat(this.genSelectTreeNode(item))
            })
          } else {
            this.$message.warning(res.message)
          }
        })
        resolve()
      })
    },
    // 构造树节点
    genSelectTreeNode (node) {
      return {
        id: node.id,
        pId: node.parentId,
        value: node.id,
        title: node.name,
        isLeaf: node.hasChildren ? false : true
      }
    },
    searchQuery () {
      this.findList()
    },
    checkRadioMethod ({ row }) {
      //   1 系统 2子系统 3 部件 4 子部件
      if (this.canSelectType.indexOf(row.structType) > -1) {
        return true
      } else {
        return false
      }
    }
  }
}
</script>

<style scoped>
.limitTitle {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
  border-bottom: 1px solid #e1e1de;
}

.limitHeight {
  height: calc(80vh - 42px);
  overflow-y: auto;
}
</style>