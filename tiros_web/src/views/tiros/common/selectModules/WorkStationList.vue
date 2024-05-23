<template>
  <!-- 工位选择弹窗 -->
  <a-modal
    width="90%"
    title="工位选择"
    centered
    :bodyStyle="{ height: '80vh' }"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-row >
      <a-col :md="4" :sm="24" style="border-right: 1px dotted #D3E1F1; height: calc(80vh - 20px);margin-right: 8px;padding: 0px;">
        <a-tree
          v-if="treeData.length"
          defaultExpandAll
          :tree-data="treeData"
          :default-selected-keys="defaultKey"
          :dropdownStyle="{ maxHeight: '200px', overflow: 'auto' }"
          :replaceFields="{ title: 'name', key: 'id' }"
          @select="onSelect"
        />
      </a-col>
      <a-col :md="19" :sm="24">
        <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="getWorkStation">
            <a-row :gutter="24">
              <a-col :md="5" :sm="24">
                <a-form-item label="工位">
                  <a-input placeholder="请输入工位名称" v-model="queryParam.name" allow-clear></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="24">
                <a-form-item label="班组">
                  <j-dict-select-tag
                    v-model="queryParam.groupId"
                    dictCode="sys_depart,depart_name,id,org_category=4"
                    placeholder="请选择"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="24">
                <a-space>
                  <a-button @click="getWorkStation">查询</a-button>
                </a-space>
              </a-col>
            </a-row>
          </a-form>
        </div>
        <div class="limitHeight">
          <vxe-table
            border
            row-id="id"
            ref="listTable"
            :align="allAlign"
            :data="tableData"
            show-overflow="tooltip"
            :edit-config="{ trigger: 'manual', mode: 'row' }"
            :radio-config="{trigger: 'row'} "
            :checkbox-config="{ trigger: 'row', highlight: true, range: true, checkStrictly: true  }"
            max-height="100%"
            style="height: 100%"
          >
            <vxe-table-column v-if="multiple" type="checkbox" width="40" fixed="left"></vxe-table-column>
            <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
            <vxe-table-column field="stationNo" title="工位号" width="100"></vxe-table-column>
            <vxe-table-column field="name" title="工位名称" width="20%" align="left"></vxe-table-column>
            <vxe-table-column field="groupName" title="所属班组" width="20%" align="left"></vxe-table-column>
            <vxe-table-column field="location" title="位置" width="100" align="left"></vxe-table-column>
            <vxe-table-column field="content" title="作业内容" width="25%" align="left"></vxe-table-column>
            <vxe-table-column field="remark" title="备注" width="10%" align="left"></vxe-table-column>
          </vxe-table>
        </div>
        <vxe-pager
          perfect
          :current-page.sync="queryParam.pageNo"
          :page-size.sync="queryParam.pageSize"
          :total="page.totalResult"
          :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
          @page-change="handlePageChange"
        ></vxe-pager>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script>
import { getStationList, getStationtree } from '@/api/tirosApi'
export default {
  name: 'WorkStationList',
  props: {
    multiple: {
      type: Boolean,
      default: false,
    },
    groupId: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      visible: false,
      confirmLoading: false,
      treeData: [],
      tableData: [],
      allAlign: 'center',
      defaultKey: [],
      queryParam: {
        id: '',
        name: '',
        groupId: '',
        pageNo: 1,
        pageSize: 10,
      },
      page: {
        currentPage: 1,
        pageSize: 10,
        totalResult: 3,
      },
    }
  },
  mounted () {
    if (this.groupId) {
      this.queryParam.groupId = this.groupId
    }
  },
  methods: {
    showModal() {
      this.visible = true
      this.getTreeList()
    },
    getTreeList() {
      getStationtree().then((res) => {
        if (res.success) {
          this.treeData = res.result
          this.defaultKey[0] = res.result[0].id
          this.queryParam.id = res.result[0].id
          this.getWorkStation()
        }
      })
    },
    getWorkStation() {
      getStationList(this.queryParam).then((res) => {
        console.log(res)
        this.page.totalResult = res.result.total
        this.tableData = res.result.records
      })
    },
    onSelect(selectedKeys, event) {
      let selectedNode = event.node.dataRef
      if (selectedKeys.length > 0) {
        this.queryParam.id = selectedNode.id
        this.queryParam.pageNo = 1
        this.getWorkStation()
      }
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
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.getTreeList()
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
  },
}
</script>

<style scoped>
.limitTitle {
  font-size: 20px;
  font-weight: 600;
  height: 40px;
}
.limitHeight {
  height: calc(80vh - 112px);
  overflow-y: auto;
}
</style>