<template>
  <a-card id="structRightContent">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="8">
          <a-col :md="7" :sm="24">
            <a-form-item label="名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="名称或编码" v-model="queryParam.searchText" allow-clear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="24">
            <a-form-item label="状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag v-model="queryParam.status" dictCode="bu_enable" />
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="24">
            <a-form-item label="上级" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-tree-select
                tree-data-simple-mode
                allow-clear
                :tree-data="selectTreeNode"
                :replaceFields="{ title: 'title', value: 'id' }"
                placeholder="请选择"
                :load-data="loadSelectNodeMethod"
                @select="selectPid"
                v-model="queryParam.id"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col>
            <span class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="searchQuery">查询</a-button>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button :disabled="records.length != 1" @click="handleEdit(records[0])">编辑</a-button>
                <a-button :disabled="records.length < 1" @click="handleDel">删除</a-button>
                <a-button @click="importType" :loading="loading">导入创建</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="tableHeight" style="height: calc(100vh - 247px)">
      <vxe-table
        border
        row-id="id"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip" 
        height="auto"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :checkbox-config="{trigger: 'row', highlight: true,checkStrictly: true}"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        :tree-config="{ lazy: true, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenMethod }"
      >
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column field="name" title="名称" tree-node align="left" header-align="center"></vxe-table-column>
        <vxe-table-column field="code" title="编码" width="150" align="left" header-align="center"></vxe-table-column>
        <vxe-table-column field="structType_dictText" title="类型" width="120"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="100"></vxe-table-column>
        <vxe-table-column field="placeId" title="所属位置" width="150"></vxe-table-column>
        <vxe-table-column field="placeDesc" title="位置描述" width="180" align="left"
        header-align="center"></vxe-table-column>
      </vxe-table>
    </div>
    <train-asset-type
      ref="selectForm"
      :multiple="true"
      :trainTypeId="trainTypeId"
      @ok="importTarget"
    ></train-asset-type>
    <right-item-modal
      ref="modalForm"
      :multiple="true"
      :trainStructId="trainStructId"
      :trainTypeId="trainTypeId"
      :parent="selectParent"
      @ok="loadData"
    ></right-item-modal>
  </a-card>
</template>

<script>
import RightItemModal from './RightItemModal'
import TrainAssetType from '../../../common/selectModules/TrainAssetType'
import {
  getTrainStructList,
  delTrainStruct,
  getTrainStructDetailList,
  delTrainStructDetail,
  importTrainStruct
} from '@/api/tirosApi'
import { everythingIsEmpty } from '@/utils/util'

export default {
  components: { RightItemModal, TrainAssetType },
  name: 'RightPage',
  props: ['trainStruct', 'trainType'],
  data () {
    return {
      records:[],
       loading:false,
      queryParam: {
        id: '',
        searchText: '',
        status: null,
        trainStructId: ''
      },
      selectParent: {},
      importParam: {
        childId: '',
        parentId: '',
        trainStructId: ''
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 19 }
      },
      trainStructId: '',
      trainTypeId: '',
      allAlign: 'center',
      tableData: [],
      selectTreeNode: []
    }
  },
  watch: {
    trainStruct: {
      immediate: false,
      handler (id) {
        this.queryParam.trainStructId = id
        this.queryParam.id = ''
        this.importParam.trainStructId = id
        this.trainStructId = id
        this.findList()
        this.findSelectList()
      }
    },
    trainType: {
      immediate: true,
      handler (id) {
        this.trainTypeId = id
      }
    }
  },
  methods: {
    checkboxChange(e){
      this.records = e.records;
    },
    findList () {
      getTrainStructDetailList(this.queryParam).then((res) => {
        if (res.success) {
          this.records = [];
          this.tableData = res.result
        }
      })

    },
    findSelectList () {
      let param = {
        trainStructId: this.trainStructId
      }
      getTrainStructDetailList(param).then((res) => {
        if (res.success) {
          // this.tableData = res.result
          this.selectTreeNode = res.result.map((item) => {
            return this.genSelectTreeNode(item)
          })
        }
      })
    },

    loadChildrenMethod ({ row }) {
      let param = {
        trainStructId: this.trainStructId,
        id: row.id,
        searchText: this.queryParam.searchText,
        status: this.queryParam.status
      }
      return new Promise((resolve) => {
        getTrainStructDetailList(param).then((res) => {
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
        trainStructId: this.trainStructId,
        id: node.dataRef.id
      }
      return new Promise((resolve) => {
        getTrainStructDetailList(param).then((res) => {
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
        isLeaf: node.hasChildren ? false : true
      }
    },

    selectPid (value) {
      console.log(value)
    },
    searchQuery () {
      this.findList()
    },

    handleAdd () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length == 1) {
        this.importParam.parentId =
          this.trainStructId = selectRecords[0].trainStructId
        this.selectParent = {
          id: selectRecords[0].id,
          name: selectRecords[0].name,
          trainTypeId: selectRecords[0].trainTypeId,
          trainStructId: selectRecords[0].trainStructId
        }
      } else if (selectRecords.length > 1) {
        this.$message.error('最多选择1条数据!')
        return
      } else if (!everythingIsEmpty(this.trainStruct)) {
        this.selectParent = {}
      } else {
        this.$message.error('请选择车辆结构!')
        return
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
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            var idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delTrainStructDetail({ ids: idsStr }).then((res) => {
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
    importType () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length == 1) {
        this.importParam.parentId = selectRecords[0].id
        this.$refs.selectForm.showModal()
      } else if (selectRecords.length > 1) {
        this.$message.error('最多选择1条数据!')
      } else if (!everythingIsEmpty(this.trainStruct)) {
        this.importParam.parentId = ''
        this.$refs.selectForm.showModal()
      } else {
        this.$message.error('请选择车辆结构!')
      }
    },
    importTarget (data) {
      if (data.length) {
        let idsStr = data
          .map(function (obj, index) {
            return obj.id
          })
          .join(',')
        this.importParam.childId = idsStr
        this.loading=true
        importTrainStruct(this.importParam).then((res) => {
          if (res.success) {
            this.$message.success(res.message)
          } else {
            this.$message.warning(res.message)
          }
          this.loadData()
        }).finally(()=>this.loading=false)
      }
    },
    loadData () {
      this.findList()
      this.findSelectList()
    }
  }
}
</script>

<style lang="less">
#structRightContent {
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