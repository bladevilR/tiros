 <template>
  <div class="bodyWrapper" na-flex-height-full style="height: 100% !important">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24">
          <a-col :md="4" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="请输入名称" v-model="queryParam.name" allow-clear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="类型">
              <j-dict-select-tag triggerChange v-model="queryParam.formType" dictCode="bu_form_type" />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="24">
            <a-form-item label="线路">
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="修程">
              <j-dict-select-tag @change="handleProgram" triggerChange v-model="queryParam.repairProgramId" dictCode="bu_repair_program,name,id" />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="规程">
              <j-dict-select-tag
                triggerChange
                v-model="queryParam.reguId"
                :dictCode="dicStr"
                @focus="handleFocusRegu"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-space>
              <a-button @click="findList">查询</a-button>
              <a-button :disabled="records.length !=1 " type="primary" @click="setValue(records[0])"> 阈值设置 </a-button>
            </a-space>
          </a-col>
        </a-row>
        <!-- <a-row :gutter='24' :style="{ display: expand ? 'block' : 'none' }">
          
        </a-row> -->
        <!-- <a-row>
          <a-col :md="24" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button><a :style="{fontSize: '12px'}" @click='expand = !expand'>
                更多条件 <a-icon :type="expand ? 'up' : 'down'" />
                </a></a-button>
              </a-space>
            </span>
          </a-col>
        </a-row> -->
      </a-form>
    </div>
    <div style="height: calc(100% - 87px)">
      <vxe-table
        border
        ref="listTable"
        align="center"
        height="auto"
        auto-resize
        :data="tableData"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        @checkbox-change="rangeChange"
        @checkbox-all="rangeChange"
        show-overflow="tooltip"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="name" title="表单名称" align="left" header-align="center"
                          min-width="160">
          <template v-slot="{ row }">
            <a href="javascript:;" @click.stop="viewForm(row)">{{ row.name }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="code" title="编码" align="left" header-align="center" width="80"></vxe-table-column>
        <vxe-table-column field="type_dictText" title="类型" width="80"></vxe-table-column>
        <vxe-table-column field="lineName" title="线路" width="100"></vxe-table-column>
        <vxe-table-column field="repairProgramName" title="修程" width="100"></vxe-table-column>
        <vxe-table-column field="reguName" title="所属规程" width="120"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="80">
          <template v-slot="{ row }">
            <div :style="{ backgroundColor: statusColor[row.status + ''], borderRadius: '4px' }">
              {{ row.status_dictText }}
            </div>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    <div style="height: 45px; background-color: #fff">
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="page.totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>
    <FormModalView ref="FormModalView" />
  </div>
</template>
<script>
import { pageThresholdForm } from '@api/tirosQualityApi'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { everythingIsEmpty } from '@/utils/util'
import FormModalView from '@views/tiros/quality/thresholdvalue/formModal.vue';

export default {
  name: 'ThresholdValuePage',
  components:{LineSelectList,FormModalView},
  data() {
    return {
      // expand: false,
      assetTypeName: undefined,
      records:[],
      dicStr: '',
      statusColor: {
        0: '#dedede',
        1: '#bad795',
      },
      queryParam: {
        name: '',
        formType: null,
        lineId: '',
        repairProgramId: '',
        reguId: '',
        status: 1,
        pageNo: 1,
        pageSize: 10,
      },
      tableData: [],
      page: {
        totalResult: 1,
      },
    }
  },
  mounted() {
    this.findList()
  },
  methods: {
    setValue(row) {
      this.$refs.FormModalView.showModal(row);
    },
    viewForm (row) {
      this.$refs.FormModalView.showModal(row, true)
    },
    handleProgram (data) {
      if (data) {
        this.dicStr = `bu_repair_regu_info,name,id,status=1 and repair_pro_id='${data}'`
      } else {
        this.queryParam.reguId = '';
        this.dicStr = ''
      }
    },
    handleFocusRegu () {
      let programId = this.queryParam.repairProgramId
      if (everythingIsEmpty(programId)) this.$message.warn('请先选择修程类型')
    },
    rangeChange({ records }) {
      this.records = records;
    },
    findList() { 
      pageThresholdForm(this.queryParam).then((res) => {
        // this.totalResult = res.result.total
        this.page.totalResult = res.result.total
        this.tableData = res.result.records
        /*if (this.tableData && this.tableData.length > 0) {
            this.$refs.listTable.setRadioRow(this.tableData[0])
            this.loadForm(this.tableData[0].id)
          }*/
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
  },
}
</script>
