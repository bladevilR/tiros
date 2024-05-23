<template>
  <div class="bodyWrapper">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="部件">
              <a-input placeholder="名称" v-model="queryParam.assetName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="系统">
              <j-dict-select-tag
                v-model="queryParam.sysId"
                placeholder="请选择"
                :dictCode="dicSysIdStr"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="更换日期">
              <a-date-picker v-model="date"></a-date-picker>
            </a-form-item>
          </a-col>
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
        <vxe-table-column field="downAssetName" title="更换部件名称" min-width="120"></vxe-table-column>
        <vxe-table-column field="sysName" title="所属系统" width="120"></vxe-table-column>
        <vxe-table-column field="downAssetSn" title="原部件序列号" width="120"></vxe-table-column>
        <vxe-table-column field="upAssetSn" title="换上件序列号" width="120"></vxe-table-column>
        <vxe-table-column field="brand" title="更换原因" min-width="140" header-align="center"
                          align="left"></vxe-table-column>
        <vxe-table-column field="exchangeTime" title="更换时间" width="100"></vxe-table-column>
        <vxe-table-column field="workUser" title="更换人员" width="100"></vxe-table-column>
        <vxe-table-column field="alertDescribe" title="更换阶段" width="100"></vxe-table-column>
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
import { getChangeRecord} from '@api/tirosProductionApi'
import moment from 'moment'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'ReplaceRecord',
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
      date:null,
      queryParam: {
        trainNo:this.trainNo,
        assetId:'',
        assetName: '',
        sysId: '',
        structureDetailId:'',
        changeDate: '',
        pageNo: 1,
        pageSize: 10
      },
      dicSysIdStr:'bu_train_asset_type,name,id,struct_type=1',
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
        if(!everythingIsEmpty(id)){
          this.dicSysIdStr=`bu_train_asset_type,name,id,struct_type=1 and train_type_id=(select train_type_id from bu_train_info where train_no='${this.trainNo}')`
        }else{
          this.dicSysIdStr='bu_train_asset_type,name,id,struct_type=1'
        }
        this.findList()
      }
    }
  },
  methods: {
    findList () {
      this.loading=true
      if(this.date){
        this.queryParam.changeDate = moment(this.date).format('YYYY-MM-DD')
      }else {
        this.queryParam.changeDate = ''
      }
      getChangeRecord(this.queryParam).then((res) => {
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