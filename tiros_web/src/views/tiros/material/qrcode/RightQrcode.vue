<template>
  <a-card :body-style="cardStyle">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="车辆段">
              <j-dict-select-tag
                v-model="queryParam.depotId"
                placeholder="请选择车辆段"
                dictCode="bu_mtr_depot,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="车间">
              <j-dict-select-tag
                v-model="queryParam.workshopId"
                placeholder="请选择车间"
                dictCode="bu_mtr_workshop,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="仓库">
                <a-input placeholder="请输入仓库编码或者名称" v-model="queryParam.searchText" allowClear></a-input>
              </a-form-item>
          </a-col>
          <a-col :md="4" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button @click="findList">查询</a-button>
              <a-button style="margin-left: 8px" @click="print">打印</a-button>
            </span>
          </a-col>
          <a-col :md="4" :sm="8">
            <a-form-item  label="是否生成二维码">
              <a-switch v-model="state" @change="handleState" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
      <div v-if="state" style="height: calc(100vh - 250px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(100vh - 250px)"
        ref="listTable"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{trigger: 'row', highlight: true, range: true}"
        :edit-config="{trigger: 'manual', mode: 'row'}"
        :align="allAlign"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="warehouseCode" title="编码" width="15%"></vxe-table-column>
        <vxe-table-column field="warehouseName" title="名称" width="20%"></vxe-table-column>
        <vxe-table-column field="warehouseLocation" title="位置" width="20%"></vxe-table-column>
        <vxe-table-column field="warehouseType_dictText" title="类别" width="10%"></vxe-table-column>
        <vxe-table-column field="print_dictText" title="状态" width="10%"></vxe-table-column>
        <vxe-table-column field="warehouseRemark" title="备注" width="20%"></vxe-table-column>
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
  </a-card>
</template>
<script>
  import { selectWarehouse } from '@api/tirosMaterialApi'

  export  default {
    name:'RightQrcode',
    data (){
      return {
        state:false,
        queryParam: {
          searchText: '',
          depotId: '',
          workshopId:'',
          pageNo: 1,
          pageSize: 10
        },
        totalResult: 0,
        allAlign: 'center',
        tableData: [],
        cardStyle: {
          'padding': '10px',
          'height': 'calc(100vh - 120px)',
        }
      }
    },
    created() {
      this.findList()
    },
    methods:{
      findList(){
        selectWarehouse(this.queryParam).then((res) => {
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
      print(){
        let selectRecords = this.$refs.listTable.getCheckboxRecords()
        if (selectRecords.length > 0) {

        }else {
          this.$message.error('尚未选中任何数据!')
        }
      },
      handleState(checked){

      }
    }



  }
</script>