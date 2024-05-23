<template>
  <a-card id="stationRight">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item label="工位名称">
              <a-input placeholder="请输入工位名称" v-model="queryParam.name" allow-clear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="8" :sm="24">
            <a-form-item label="位置">
              <a-input placeholder="请输入位置" v-model="queryParam.location" allow-clear></a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col>
            <span class="table-page-search-submitButtons">
              <a-space>
                <a-button v-if="value" type="primary" @click="handleAdd">新增</a-button>
                <a-button :disabled="records.length != 1" @click="handleEdit(records[0])">编辑</a-button>
                <a-button @click="deleteRecord" :disabled="records.length < 1"  v-has="'workstation:delete'">删除</a-button>
                <a-button :disabled="records.length != 1" @click="linkSheet">关联表单</a-button>
                <a-button :disabled="!value" @click="showMapSetting">车间平面设置</a-button>
                <a-button @click="searchQuery">查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 292px)">
      <vxe-table
        border
        ref="listTable"
        max-height="100%"
        style="height: calc(100vh - 292px)"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :checkbox-config="{trigger: 'row', highlight: true, range: true}"
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column type="checkbox" width="48"></vxe-table-column>
        <vxe-table-column field="stationNo" title="工位号" width="8%"></vxe-table-column>
        <vxe-table-column field="name" title="工位名称" width="15%" align="left" header-align="center"></vxe-table-column>
        <vxe-table-column field="location" title="位置" width="15%" align="left" header-align="center"></vxe-table-column>
        <vxe-table-column field="content" title="作业内容" align="left" header-align="center"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="8%"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" align="left" header-align="center"></vxe-table-column>
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
    <station-item-modal ref="modalForm" :workShopDetail="detail" @ok="loadData"></station-item-modal>
    <StationForms ref="stationForms" @ok="loadData"></StationForms>
    <!--    <vxe-modal v-if="mapVisible && value" v-model="mapVisible" title="车间平面设置" :showHeader="false" fullscreen resize show-overflow >
          <template v-slot>
            <StationMap :depotId="detail.depotId" :workshopId="value" :visible.sync="mapVisible" style="width: 100%;height: 100%;"></StationMap>
          </template>
        </vxe-modal>-->
    <a-modal
      centered
      :width="'100%'"
      dialogClass="fullDialog no-title no-footer"
      :visible="mapVisible"
      @cancel="mapVisible=false"
      :footer="null"
      :destroyOnClose="true"
      :closable="false"
      :keyboard="false"
      v-if="mapVisible && value"
    >
      <StationMap :depotId="detail.depotId" :workshopId="value" :visible.sync="mapVisible" style="width: 100%;height: 100%;"></StationMap>
    </a-modal>
  </a-card>
</template>

<script>
import StationItemModal from './StationItemModal'
import { delStation, getStationList } from '@/api/tirosApi'
import StationMap from '@views/tiros/basic/modules/workStation/StationMap'
import StationForms from '@views/tiros/basic/modules/workStation/StationForms'

export default {
  components: { StationItemModal, StationMap, StationForms },
  name: 'RightPage',
  props: ['value', 'detail'],
  data() {
    return {
      records:[],
      queryParam: {
        name: '',
        location: '',
        id: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      selectTreeNode: [],
      selectPid: '',
      mapVisible: false
    }
  },
  watch: {
    value: {
      immediate: true,
      handler(id) {
        this.queryParam.id = id
        this.findList()
      }
    }
  },
  methods: {
    checkboxChange(e){
      this.records = e.records;
    },
    findList() {
      this.loading = true
      getStationList(this.queryParam).then((res) => {
        if (res.success) {
          this.loading = false
          this.totalResult = res.result.total
          this.records = [];
          this.tableData = res.result.records
        }
      })
    },
    searchQuery() {
      this.findList()
    },
    linkSheet() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length == 1) {
        // let stationId = selectRecords[0].id
        //this.$router.push({ path: `/tiros/linkedsheet/${stationId}`, query: { name: selectRecords[0].name } })
        this.$refs.stationForms.show(selectRecords[0])
      } else {
        this.$message.error('请选择1条工位数据!')
      }
    },
    handleEdit(record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
    },
    handleAdd() {
      if (this.detail.workshopId) {
        this.$refs.modalForm.add()
        this.$refs.modalForm.title = '新增'
      } else {
        this.$message.error('尚未选择新建工位所在车间！')
      }
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            var idsStr = selectRecords
              .map(function(obj, index) {
                return obj.id
              })
              .join(',')
            delStation('ids=' + idsStr).then((res) => {
              if (res.success) {
                this.loadData()
                this.$message.success(res.message)
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
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData() {
      this.findList()
    },
    showMapSetting() {
      this.mapVisible = true
    }
  }
}
</script>

<style lang="less">
#stationRight {
  .ant-card-body {
    padding: 10px;
    height: calc(100vh - 120px);
  }
}
</style>