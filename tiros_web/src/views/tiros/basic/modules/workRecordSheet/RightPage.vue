<template>
  <a-card id="RecordRightContent">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item label="作业内容">
              <a-input placeholder="作业内容" v-model="workContent" allow-clear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="技术要求">
              <a-input placeholder="技术要求" v-model="techRequire" allow-clear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="12">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="searchQuery">查询</a-button>
                <a-button type="primary" @click="handleAdd">新增一条</a-button>
                <a-button :disabled="records.length != 1" @click="handleEdit(records[0])">编辑</a-button>
                <a-button :disabled="records.length != 1" @click="handleDel(records[0])">删除</a-button>
<!--                <a-button @click="handleBack">关闭</a-button>-->
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
      <div class="tableHeight" style="height: calc(100vh - 156px)">
        <vxe-table
          border
          row-id="id"
          ref="listTable"
          :align="allAlign"
          :data="tableData"
          show-overflow="tooltip"
          max-height="100%"
          style="height: calc(100vh - 156px)"
          @checkbox-change="checkboxChange"
          @checkbox-all="checkboxChange"
          :checkbox-config="{trigger: 'row', highlight: true, range: true}"
          :edit-config="{ trigger: 'manual', mode: 'row' }"
        >
          <vxe-table-column type="checkbox" width="40"></vxe-table-column>
          <vxe-table-column field="itemNo" title="序号" width="60"></vxe-table-column>
          <vxe-table-column field="workContent" title="作业内容" align="left" header-align="center">
            <template v-slot="{row}">
              {{row.workContent.replace(/[\r\n]/g,'')}}
            </template>
          </vxe-table-column>
          <vxe-table-column field="checkLevel_dictText" title="控制点" width="10%"></vxe-table-column>
          <vxe-table-column field="techRequire" title="技术要求" width="35%" align="left" header-align="center">
            <template v-slot="{row}">
              {{row.techRequire.replace(/[\r\n]/g,'')}}
            </template>
          </vxe-table-column>
        </vxe-table>
      </div>
    <record-item-modal
      ref="modalForm"
      @ok="loadData()"
      :workRecId="recordId"
      :categoryId="categoryId"
    ></record-item-modal>
  </a-card>
</template>

<script>
import RecordItemModal from './RecordItemModal'
import { listOldWorkRecordDetail, deleteOldWorkRecordDetail } from '@/api/tirosApi'
export default {
  components: { RecordItemModal },
  name: 'TrainTypeSysRight',
  props: ['recordId', 'value'],
  data() {
    return {
      records:[],
      categoryId: '',
      workContent: '',
      techRequire: '',
      allAlign: 'center',
      tableData: [],
    }
  },

  watch: {
    value: {
      immediate: true,
      handler(id) {
        this.categoryId = id
        this.findList()
      },
    },
  },
  methods: {
    checkboxChange(e) {
      this.records = e.records;
    },
    findList() {
      let param = {
        id: this.recordId,
        categoryId: this.categoryId,
        techRequire: this.techRequire,
        workContent: this.workContent,
      }
      this.loading = true
      listOldWorkRecordDetail(param).then((res) => {
        if (res.success) {
          this.loading = false
          this.tableData = res.result
          this.records = [];
        }
      })
    },

    searchQuery() {
      this.findList()
    },

    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleEdit(record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
    },
    handleDel(record) {
      this.$confirm({
          content: `是否删除选中${1}条数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            // const ids = record.map((item) => {
            //   return item.id //条件;
            // }).join(',');
            // 
            return deleteOldWorkRecordDetail(record.id).then((res) => {
              if (res.success) {
                this.$message.success(res.message)
                this.loadData()
              } else {
                this.$message.error(res.message)
              }
            })
          },
        })
      
    },
    handleBack() {
      this.$emit('close')
    },
    loadData() {
      this.findList()
    },
  },
}
</script>

<style lang="less">
#recordContent {
  #RecordRightContent {
    // border: none;
    .ant-card-body {
      padding: 10px;
      height: calc(100vh - 70px);
      overflow-y: auto;
    }
    .tableHeight {
     /* height: calc(100vh - 250px);*/
      // overflow-y: auto;
    }
  }
}
</style>