<template>
  <!-- 文件选择弹窗 -->
  <a-modal
    width="90%"
    :visible="visible"
    title="文件选择"
    centered
    :bodyStyle="{ height: '80vh' }"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <na-splitter :defaultSize="300" style="height: calc(80vh - 15px)">
      <div slot="left-panel" style="overflow-y: auto; overflow-x: hidden;padding-right: 2px">
        <a-directory-tree defaultExpandAll
                          :tree-data="treeData"
                          @select="onSelect"
                          :replaceFields="{title: 'name',key:'id'}"/>
      </div>
      <div slot="right-panel" style="padding-left: 5px;padding-right: 3px;">
        <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="findList">
            <a-row :gutter="24">
              <a-col :md="7" :sm="24">
                <a-form-item label="文件名称">
                  <a-input placeholder="请输入文件名称" v-model="queryParam.name" :allowClear="true"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="7" :sm="24">
                <a-form-item label="文件类型">
                  <a-select placeholder="请选择" v-model="queryParam.type" :allowClear="true">
                    <a-select-option :value="undefined">请选择</a-select-option>
                    <a-select-option value="">所有文件</a-select-option>
                    <a-select-option v-for="t in fileType" :key="t" :alue="t">
                      {{t}}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="3" :sm="8">
                <a-button @click="findList">查询</a-button>
              </a-col>
            </a-row>
          </a-form>
        </div>
        <div style="height: calc(80vh - 130px);">
          <vxe-table
            border
            row-id="id"
            ref="listTable"
            style="height: calc(80vh - 130px);"
            :align="allAlign"
            :data="tableData"
            show-overflow="tooltip"
            :edit-config="{trigger: 'manual', mode: 'row'}"
            :radio-config="{trigger: 'row'} "
            :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          >
            <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
            <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
            <vxe-table-column field="name" title="名称"  align="left" header-align="center"></vxe-table-column>
            <vxe-table-column field="type" title="类型" width="150" align="center"></vxe-table-column>
            <vxe-table-column field="size" title="大小(kb)" width="180" align="right" header-align="center"></vxe-table-column>
            <vxe-table-column field="uploadDate" title="上传日期" width="150"></vxe-table-column>
            <vxe-table-column field="remark" title="备注" width="200" align="left" header-align="center"></vxe-table-column>
          </vxe-table>
          <vxe-pager
            perfect
            :current-page.sync="queryParam.pageNo"
            :page-size.sync="queryParam.pageSize"
            :total="totalResult"
            :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
            @page-change="handlePageChange"
          ></vxe-pager>
        </div>
      </div>
    </na-splitter>
  </a-modal>
</template>

<script>
import { getDocumentTechFileTree, getDocumentPage, getFileType, getDocumentTree } from '@/api/tirosApi'
export default {
  name: 'TechFileList',
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
    id: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      fileType: [],
      queryParam: {
        id: '',
        name: '',
        showType: 2,
        type: null,
        pageNo: 1,
        pageSize: 10
      },
      allAlign: 'center',
      totalResult: 0,
      tableData: [],
      visible: false,
      confirmLoading: false,
      treeData: [],
    }
  },
  methods: {
    getFileType() {
      getFileType().then((res) => {
        this.fileType = res.result
      })
    },
    selectTree() {
      getDocumentTree({id:this.id}).then((res) => {
        if (res.success) {
          this.treeData = res.result
        }
      })

    /*  getDocumentTechFileTree().then((res) => {
        if (res.success) {
          this.treeData = res.result
        }
      })*/
    },
    showModal() {
      this.visible = true
      this.selectTree()
      this.getFileType()
      this.findList()
    },
    onSelect(selectedKeys, e) {
      if (selectedKeys.length) {
        let nodeData = e.node.dataRef
        this.queryParam.id = nodeData.id
      }
      this.findList()
    },
    findList() {
      getDocumentPage(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
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
      this.visible = false
    },
  },
}
</script>

<style>
</style>