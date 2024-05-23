<template>
<!-- 车辆具体设备选择，左边列出车辆结构，右边列出当前选中的节点设备及子设备(具体设备) -->
  <a-modal
    width="80%"
    title="设备选择"
    :visible="visible"
    :confirmLoading="loading"
    centered
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
    :bodyStyle="{height:'80vh'}"
  >
    <a-spin :spinning="loading">
    <na-splitter :defaultSize="250" style="height: calc(80vh - 15px)">
      <div slot="left-panel" style="overflow-y: auto; overflow-x: hidden;padding-right: 2px">
        <a-tree
          :tree-data="treeData"
          :replaceFields="{title: 'name',key:'id'}"
          @select="onSelectTreeNode"
          :load-data="onLoadTreeData"
        />
      </div>
      <div slot="right-panel" style="padding-left: 5px;padding-right: 3px; height: calc(100% - 0px)">
        <vxe-table
          border
          row-id="id"
          ref="listTable"
          :align="'center'"
          :data="tableData"
          show-overflow="tooltip"
          height="auto"
          max-height="100%"
          :edit-config="{ trigger: 'manual', mode: 'row' }"
          :radio-config="!multiple ? {trigger: 'row', checkMethod: selectAbleCheck} : {}"
          :checkbox-config="multiple ? { trigger: 'row', highlight: true,checkStrictly: true , checkMethod: selectAbleCheck} : {}"
        >
          <!--          :tree-config="{ lazy: true, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenRow }"-->
          <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
          <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
          <vxe-table-column field="assetName" title="名称" tree-node align="left" header-align="center" min-width="180"></vxe-table-column>
          <vxe-table-column field="assetNo" title="编码" width="150"></vxe-table-column>
          <vxe-table-column field="status_dictText" title="状态" width="100"></vxe-table-column>
          <vxe-table-column field="structType_dictText" title="类型" width="100"></vxe-table-column>
          <vxe-table-column field="assetTypeName" title="设备类型" width="100" align="left" header-align="center"></vxe-table-column>
<!--          <vxe-table-column field="placeId" title="所属位置" width="180" align="left" header-align="center"></vxe-table-column>-->
          <vxe-table-column field="placeDesc" title="位置描述" width="250" align="left" header-align="center"></vxe-table-column>
        </vxe-table>
      </div>
    </na-splitter>
    </a-spin>
  </a-modal>

</template>

<script>
import { getTrainAssetListByStructDetailId, getTrainStructDetailList } from '@api/tirosApi'
export default {
  name: 'TrainAssetList',
  props: {
    multiple: {
      type: Boolean,
      default: false,
    },
    canSelectType: {
      type: Array,
      default: ()=>{
        return [1,2,3, 4]
      }
    }
  },
  data () {
    return {
      visible: false,
      loading: false,
      treeData: [],
      tableData: [],
      defaultSelectedKeys: [],
      queryParam: {
        trainNo: '',
        structDetailId: ''
      }
    }
  },
  methods: {
    showModal (trainNo, structId) {
      this.queryParam.trainNo = trainNo
      this.trainStructId = structId
      this.queryParam.structDetailId = structId
      this.visible = true

      this.queryTreeData()
      this.loadTableData()
    },
    queryTreeData () {
      let param = {
        trainStructId: this.trainStructId,
      }
      getTrainStructDetailList(param).then(res => {
        if (res.success) {
          this.treeData = res.result
          // this.defaultSelectedKeys[0]=res.result[0].id
        } else {
          console.error('获取车辆结构树失败：', res.message)
          this.$message.warning('获取车辆结构树失败')
        }
      })
    },
    onLoadTreeData (treeNode) {
      // treeNode.dataRef.id
      let param = {
        trainStructId: this.trainStructId,
        id: treeNode.dataRef.id
      }
      return new Promise(resolve => {
        //  如果该节点加载过，则不再加载了
        if (treeNode.dataRef.children && treeNode.dataRef.children.length > 0) {
          resolve()
        } else {
          getTrainStructDetailList(param).then(res => {
            if (res.success) {
              treeNode.dataRef.children = res.result
              treeNode.dataRef.children .map((item) => {
                item.isLeaf=!item.hasChildren
              })
              this.treeData = [...this.treeData]
            } else {
              console.error('获取车辆结构树失败：', res.message)
              this.$message.warning('获取车辆结构树失败')
            }
            resolve()
          })
        }
      })
    },
    onSelectTreeNode (selectedKeys) {
      if (!selectedKeys || selectedKeys.length < 1) {
        this.queryParam.structDetailId = ''
      } else {
        this.queryParam.structDetailId = selectedKeys[0]
      }
      this.loadTableData()
    },
    loadTableData () {
      this.loading = true
      console.log('query:', this.queryParam)

      getTrainAssetListByStructDetailId(this.queryParam).then(res => {
        if (res.success) {
          this.tableData = res.result
        } else {
          this.$message.error('加载车辆设备数据失败')
          console.error('加载车辆设备数据失败', res.message)
        }
        this.loading=false
      })
      /*
      listOrTreeTrainAsset(this.queryParam).then(res => {
        if (res.success) {
          this.tableData = res.result
        } else {
          this.$message.error('加载车辆设备数据失败')
          console.error('加载车辆设备数据失败', res.message)
        }
        this.loading=false
      })*/
    },
    handleOk() {
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
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false;
      Object.assign(this.$data,this.$options.data());
    },
    selectAbleCheck ({ row }) {
      return true
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

</style>