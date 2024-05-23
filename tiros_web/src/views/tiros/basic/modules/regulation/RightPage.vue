<template>
  <a-card id="reguRightContent">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="12">
          <a-col :md="7" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="名称" v-model="queryParam.title" allowClear></a-input>
            </a-form-item>
          </a-col >
          <a-col :md="7" :sm="24">
            <span class="table-page-search-submitButtons">
              <a-space>
              <a-button type="primary" @click="handleAdd">新增</a-button>
              <a-button :disabled="records.length != 1" :loading="editLoading" @click.stop="handleEdit(records[0])">编辑</a-button>
              <a-button :disabled="importBtn" @click="importRegulation">从其他项导入指导书</a-button>
              <a-button :disabled="records.length < 1" @click="handleDel" v-has="'regulation:detail:delete'">删除</a-button>
              <a-button @click="searchQuery">查询</a-button>
                <!--              <a-button @click="linkFile">关联工艺文件</a-button>-->
                <!--                 <a-button @click="importData">导入</a-button>-->
             </a-space>
            </span>
          </a-col>
<!--          <a-col :md="8" :sm="24">
            <a-form-item label="上级">
              <a-tree-select
                tree-data-simple-mode
                allow-clear
                :tree-data="selectTreeNode"
                placeholder="请选择"
                :load-data="loadSelectNodeMethod"
                v-model="queryParam.parentId"
              />
            </a-form-item>
          </a-col>-->
        </a-row>
      </a-form>
    </div>
    <div class="tableHeight" style="height: calc(100vh - 207px)">
      <vxe-table
        border
        resizable
        row-id="id"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        max-height="100%"
        style="height: calc(100vh - 207px)"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        :checkbox-config="{trigger: 'row',highlight: true,checkStrictly: true}"
        :tree-config="{ lazy: true, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenMethod }"
        :keep-source="true"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="no" title="序号" width="60"></vxe-table-column>
        <vxe-table-column field="title" title="名称" tree-node align="left" header-align="center"
                          min-width="140"></vxe-table-column>
        <vxe-table-column field="type_dictText" title="类型" width="100"></vxe-table-column>
        <vxe-table-column field="method_dictText" title="维保手段" width="100"></vxe-table-column>
        <vxe-table-column field="qualityLevel_dictText" title="质保等级" width="80"></vxe-table-column>
        <vxe-table-column field="outsource_dictText" title="是否委外" width="80"></vxe-table-column>
        <vxe-table-column field="safeNotice" title="安全提示" min-width="140" align="left"
                          header-align="center"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" min-width="120" align="left"
                          header-align="center"></vxe-table-column>
      </vxe-table>

    </div>

    <tech-file-list ref="TechFileModalForm" :multiple="true" @ok="addTarget"></tech-file-list>
    <regu-add-modal ref="reguAddModalForm" :reguId="reguId" :trainTypeId="trainTypeId" @ok="loadData()"
                    @reloadTreeChilds="reloadTreeChild()"></regu-add-modal>
    <import-modal ref="importModal" @ok="loadLeft"></import-modal>
    <ImportRegulationModal ref="regulationModal"/>
  </a-card>
</template>

<script>
import { getReguDeteil, delReguInfo, getReguDetailOtherInfo } from '@/api/tirosApi'
import ReguAddModal from '@views/tiros/basic/modules/regulation/ReguAddModal'
import TechFileList from '@views/tiros/common/selectModules/TechFileList'
import { everythingIsEmpty } from '@/utils/util'
import XEUtils from 'xe-utils'
import ImportModal from '@views/tiros/basic/modules/regulation/ImportModal'
import ImportRegulationModal from '@views/tiros/basic/modules/regulation/ImportRegulationModal'

export default {
  components: { TechFileList, ReguAddModal, ImportModal, ImportRegulationModal},
  name: 'RightPage',
  props: ['value', 'trainTypeId'],
  data () {
    return {
      editLoading: false,
      records:[],
      queryParam: {
        reguId: '',
        title: '',
        parentId: null
      },
      reguId: '',
      allAlign: 'center',
      tableData: [],
      selectTreeNode: [],
      selectPid: '',
      loadChild: [],
      curParentId: null
    }
  },

  watch: {
    value: {
      immediate: true,
      handler (id) {
        this.queryParam.parentId = ''
        this.queryParam.reguId = id
        this.reguId = id
        if (id != null) {
          this.findList()
          // this.findSelectList()
        }
      }
    }
  },
  beforeMount () {
  },
  computed:{
    importBtn(){
      const res = !this.records.length ? true : this.records.filter((item) => item.type != 2).length ? true : false;
      return res
    }
  },
  methods: {
    importRegulation(){
      const ids = this.records.map((item) => {
        return item.id //条件;
      });
      this.$refs.regulationModal.show(this.queryParam.reguId,ids);
    },
    checkboxChange(e){
      this.records = e.records;
    },
    findList () {
      this.loading = true
      // if(this.queryParam.reguId){
      getReguDeteil(this.queryParam).then((res) => {
        if (res.success) {
          this.loading = false
          this.records = [];
          this.tableData = res.result
        }
      })
      // }
    },
    findSelectList () {
      let param = {
        reguId: this.queryParam.reguId
      }
      getReguDeteil(param).then((res) => {
        if (res.success) {
          this.loading = false
          this.selectTreeNode = res.result.map((item) => {
            return this.genSelectTreeNode(item)
          })
        }
      })
    },
    reloadTreeChild (parentId) {
      if (!this.curParentId) {
        this.loadData()
        return;
      }

      let param = {
        reguId: this.queryParam.reguId,
        parentId: this.curParentId
      }
      getReguDeteil(param).then((res) => {
        if (res.success) {
          let childs = res.result
          this.findSetChild(this.curParentId, this.tableData, childs)
          // this.$refs.listTable.loadData(this.tableData)
          this.$forceUpdate();
        }
      })
    },
    findSetChild (id, items, childs) {
      for (let i = 0; i < items.length; i++) {
        let item = items[i]
        if (item.id === id) {
          console.log('set the child:', id)
          item.children = childs
          break
        }
        // 找子节点
        if (item.children && item.children.length > 0) {
          this.findSetChild(id, item.children, childs)
        }
      }
    },
    loadChildrenMethod ({ row }) {
      console.log(row)
      let param = {
        reguId: this.queryParam.reguId,
        parentId: row.id
      }
      return new Promise((resolve) => {
        getReguDeteil(param).then((res) => {
          if (res.success) {
            if(!res.result.length){
              row.hasChildren = false;
            }
            resolve(res.result)
          } else {
            this.$message.error(`${res.msg}`)
          }
        })
      })
    },
    loadSelectNodeMethod (node) {
      let param = {
        reguId: this.queryParam.reguId,
        parentId: node.dataRef.id
      }
      return new Promise((resolve) => {
        getReguDeteil(param).then((res) => {
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
        title: node.title,
        isLeaf: node.hasChildren ? false : true
      }
    },

    searchQuery () {
      this.findList()
    },

    handleAdd () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      let parent={}
      if (selectRecords.length > 0) {
        if (selectRecords.length === 1) {
          if (selectRecords[0].type === 2) {
            this.$message.warning('作业项下不能创建子节点！')
            return
          }
          parent = {
            id: selectRecords[0].id,
            name: selectRecords[0].title
          }
          this.curParentId = parent.id
        } else {
          this.$message.warning('只能选择一个上级！')
          return
        }
      }
      if (everythingIsEmpty(this.reguId)) {
        this.$message.warning('请选择一个规程项！')
        return
      }
      this.$refs.reguAddModalForm.add(parent)
      this.$refs.reguAddModalForm.title = '新增'
    },
    handleEdit (record) {
      /*let options = { children: 'children' }
      let searchProps = ['id']
      let data
      this.loadChild = XEUtils.searchTree(this.tableData, item => searchProps.find(key => item[key] === record.parentId), options)
      if (this.loadChild.length !== 0) {
        this.loadChild = this.loadChild[0]
      } else {
        this.loadChild = record
      }*/
      this.curParentId = record.parentId
      this.editLoading = true;
      getReguDetailOtherInfo({ id: record.id }).then((res) => {
        if (res.success) {
          //data = res.result
          this.$refs.reguAddModalForm.edit( res.result)
          this.$refs.reguAddModalForm.title = '编辑'
        }
        this.editLoading = false;
      }).catch((err) => {
        this.editLoading = false;
      });
    },
    handleDel () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `此操作将删除当前选中记录及其子级数据，是否继续？`,
          okText: '确认',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delReguInfo({ ids: idsStr }).then((res) => {
              if (res.success) {
                this.$message.success(res.message)
                this.loadData()
              } else {
                this.$message.warning(res.message)
              }
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    linkFile () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length == 1) {
        this.$refs.TechFileModalForm.showModal()
      } else {
        this.$message.error('请选择1条数据!')
      }
    },
    addTarget (data) {
    },
    loadData () {
      this.queryParam.id = ''
      this.findList(this.queryParam)
      // this.findSelectList()
    },
    loadLeft(){
      this.$emit("ok")
    },
    importData () {
      this.$refs.importModal.title = '规程导入'
      this.$refs.importModal.showModal()

    }
  }
}
</script>

<style lang="less">
#reguRightContent {
  .ant-card-body {
    padding: 10px;
    height: calc(100vh - 120px);
  }

  .tableHeight {
    min-height: calc(100vh - 255px);
    // overflow-y: auto;
  }
}
</style>