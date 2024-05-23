<template>
  <div>
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="车辆段">
              <j-dict-select-tag
                v-model="queryParam.train"
                placeholder="请选择"
                dictCode="bu_mtr_depot,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="股道">
              <a-input placeholder="股道编号" v-model="queryParam.code" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button :disabled="records.length != 1" @click="handleEdit(records[0])">编辑</a-button>
                <!-- <a-button style="margin-left: 8px">修改</a-button> -->
                <a-button :disabled="records.length < 1"  @click="deleteRecord">删除</a-button>

              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 列表区 -->
    <div style="height: calc(100vh - 225px)">
      <vxe-table
        border
        auto-resize
        max-height="100%"
        style="height: calc(100vh - 225px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :checkbox-config="{trigger: 'row', highlight: true, range: true}"
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="code" title="股道编号" width="15%"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="15%"></vxe-table-column>
        <vxe-table-column field="depot" title="所属车辆段"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" header-align="center" align="left"></vxe-table-column>
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
    <track-item-modal ref="modalForm" @ok="loadData"></track-item-modal>
  </div>
</template>

<script>
import TrackItemModal from './modules/track/TrackItemModal'
import { getTrackList, delTrack } from '@/api/tirosApi'
export default {
  components: { TrackItemModal },
  data() {
    return {
      records:[],
      queryParam: {
        depotId: '',
        code: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: []
    }
  },
  created() {
    this.findList()
  },
  methods: {
    checkboxChange(e){
      this.records = e.records;
    },
    findList() {
      this.loading = true
      getTrackList(this.queryParam).then(res => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.records = [];
          this.tableData = res.result.records
        }
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleEdit(record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
    },
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}条数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords
              .map(function(obj, index) {
                return obj.id
              })
              .join(',')
            delTrack({ ids: idsStr }).then(res => {
              if(res.success){
                this.findList()
                this.$message.success(res.message)
              }else {
                this.$message.warning(res.message)
              }
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    loadData() {
      this.findList()
    }
  }
}
</script>

<style>
</style>