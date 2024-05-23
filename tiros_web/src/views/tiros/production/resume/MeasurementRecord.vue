<template>
  <div class="bodyWrapper">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="测点名称">
              <a-input placeholder="" v-model="queryParam.measureName"></a-input>
            </a-form-item>
          </a-col>
<!--          <a-col :md="6" :sm="24">
            <a-form-item label="测点类型">
              <j-dict-select-tag
                v-model="queryParam.type"
                placeholder="请选择"
                dictCode="bu_material_type"
              />
            </a-form-item>
          </a-col>-->
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
              <a-button @click="findList">查询</a-button>
                </a-space>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>
    <a-spin :spinning="loading">
    <div style="height: calc(100vh - 250px)">
      <vxe-table
        border
        max-height="90%"
        style="height: calc(100vh - 298px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="trainName" title="车辆" width="100"></vxe-table-column>
        <vxe-table-column field="trainAssetName" title="设备" width="120"></vxe-table-column>
        <vxe-table-column field="measureName" title="测量项名称" min-width="100" ></vxe-table-column>
        <vxe-table-column field="values" title="测量值" width="100" align="right"
                          header-align="center"></vxe-table-column>
        <vxe-table-column field="workUser" title="测量人" width="100"></vxe-table-column>
        <vxe-table-column field="workTime" title="测量日期" width="100"></vxe-table-column>
        <vxe-table-column field="alertMessage" title="预警信息" min-width="100" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="alertReason" title="预警原因"  min-width="100" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="fixValues" title="修正值" width="100" align="right" header-align="center"></vxe-table-column>
        <vxe-table-column field="tools" title="测量器具" width="100"></vxe-table-column>
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
    </a-spin>
  </div>
</template>

<script>
import {  getMeasureRecord } from '@api/tirosProductionApi'

export default {
  name: 'MeasurementRecord',
  props: {
    trainNo: {
      type: String,
      default: ''
    },
    structureDetailId: {
      type: String,
      default: ''
    },
  },
  data () {
    return {
      queryParam: {
        trainNo:this.trainNo,
        assetId:'',
        measureName: '',
        structureDetailId:this.structureDetailId,
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      loading:false
    }
  },
  watch: {
    structureDetailId: {
      immediate: true,
      handler (id) {
        this.queryParam.structureDetailId = id !== this.trainNo ? id : ''
        this.findList()
      }
    },
    trainNo: {
      immediate: true,
      handler (id) {
        this.queryParam.trainNo = id
        this.findList()
      }
    }
  },
  methods: {
    findList () {
      this.loading=true
      getMeasureRecord(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.tableData = res.result.records
      }).finally(()=>this.loading=false)
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    }
  }
}
</script>

<style scoped>

</style>