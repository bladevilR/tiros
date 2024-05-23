<template>
  <!-- 文件选择弹窗 -->
  <a-modal
    width="90%"
    :visible="visible"
    title="作业指导步骤选择"
    centered
    :bodyStyle="{ height: '80vh' }"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <na-splitter class="booksetp-body" :defaultSize="300" style="min-height：90%">
      <div slot="left-panel" style="overflow-y: auto; overflow-x: hidden; padding-right: 2px; height: 100%">
        <a-card id="bookStepsContent" height="100%">
          <div class="typeTitle">作业指导书</div>
          <a-input-search v-model="serchValue" style="margin: 8px 0; width: 90%" placeholder="输入名称" />
          <div style="width: 70%; margin: 0 auto; min-height: 100%">
            <a-tree
              showLine
              :selectedKeys="selectedKeys"
              :tree-data="showTreeData"
              :replaceFields="{ title: 'fileName', key: 'id' }"
              @select="onSelect"
            >
            </a-tree>
          </div>
        </a-card>
      </div>
      <div slot="right-panel" style="padding-left: 5px; padding-right: 3px">
        <!-- <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="findList">
            <a-row :gutter="24">
              <a-col :md="7" :sm="24">
                <a-form-item label="文件名称">
                  <a-input placeholder="请输入文件名称" v-model="queryParam.name" :allowClear="true"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="3" :sm="8">
                <a-button @click="findList">查询</a-button>
              </a-col>
            </a-row>
          </a-form>
        </div> -->
        <div style="height: 100%">
          <vxe-table
            border
            row-id="id"
            ref="listTable"
            style="auto"
            :align="allAlign"
            :data="tableData"
            show-overflow="tooltip"
            :radio-config="{ trigger: 'row' }"
            :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          >
            <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
            <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
            <vxe-table-column field="stepNum" title="步骤" width="120px"></vxe-table-column>
            <vxe-table-column field="stepTitle" title="标题" align="left" header-align="center">
              <template v-slot="{ row }">
                <!--                <a>{{ row.stepTitle }}</a>-->
                {{ row.stepTitle }}
              </template>
            </vxe-table-column>
          </vxe-table>
        </div>
      </div>
    </na-splitter>
  </a-modal>
</template>

<script>
import { getSopDetailPage, getSopPage } from '@/api/tirosApi'

export default {
  name: 'SopDetailList',
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
        bookId: '',
      },
      allAlign: 'center',
      totalResult: 0,
      treeKey: 0,
      tableData: [],
      visible: false,
      confirmLoading: false,
      treeData: [],
      showTreeData: [],
      serchValue: '',
      selectedKeys: [],
    }
  },
  watch: {
    serchValue() {
      console.log('刷新')
      this.showTreeData = this.treeData
        .map((e) => {
          if (e.fileName.indexOf(this.serchValue) > -1) {
            return e
          }
          return null
        })
        .filter((e) => e)

      console.log(this.showTreeData)
    },
  },
  methods: {
    selectTree() {
      getSopPage({
        pageNo: 1,
        pageSize: 1000,
        searchText: '',
      }).then((res) => {
        if (res.success) {
          this.treeData = res.result.records
          this.showTreeData = res.result.records
          if (this.treeData.length > 0) {
            this.queryParam.bookId = this.treeData[0].id
            this.selectedKeys[0] = this.treeData[0].id
            console.log(this.treeData[0])
            this.selectData = this.treeData[0];
            this.findList()
          }
        }
      })
    },
    showModal() {
      this.visible = true
      this.selectTree()
    },
    onSelect(selectedKeys, e) {
      if (selectedKeys.length) {
        let nodeData = e.node.dataRef
        this.queryParam.bookId = nodeData.id
        this.selectedKeys[0] = nodeData.id
        console.log(nodeData)
        this.selectData = nodeData;
        this.findList()
      }
    },
    findList() {
      getSopDetailPage(this.queryParam).then((res) => {
        this.tableData = res.result
        console.log(this.tableData)
      })
    },
    handleOk() {
      let selectRecords = []
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords()
      } else {
        if (this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
      Array.from(selectRecords,(item)=>{
        item.bookName = this.selectData.fileName;
      }) 
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

<style lang="less">
#bookStepsContent {
  height: 100%;
  .ant-card-body {
    padding: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  .typeTitle {
    width: 100%;
    background: #eee;
    padding: 15px;
    text-align: center;
  }
}

.booksetp-body {
  height: 100%;
}
</style>