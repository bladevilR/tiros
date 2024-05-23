<template>
  <div id="MonitorContent">
    <div class="table-page-search-wrapper" v-if="searchFormShow">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24">
          <a-col :md="4" :sm="24">
            <a-form-item label="车辆段">
              <j-dict-select-tag
                v-model="queryParam.depotId"
                placeholder="请选择"
                dictCode="bu_mtr_depot,name,id"
                @changeName="changeDepotNAme"
              />
            </a-form-item>
          </a-col>

          <a-col :md="3" :sm="24">
            <a-form-item label="线路">
              <!--              <j-dict-select-tag
                              v-model="lineId"
                              dictCode="bu_mtr_line,line_name,line_id"
                            />-->
              <line-select-list v-model="lineId" @change="changeLine"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="24">
            <a-form-item label="车辆">
              <j-dict-select-seach-tag 
                :trigger-change="true"
                @change="handleTrainNo"
                @focus="handleSysFocus"
                v-model="queryParam.trainNo" 
                :dictCode="dictTrainStr" 
              />
              <!-- <j-dict-select-seach-tag
                :triggerChange="true"
                v-model="queryParam.trainId"
                :dictCode="dictTrainStr"
                @focus="handleSysFocus"
              /> -->
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="12">
            <a-form-item label="消耗时间">
              <j-dict-select-tag v-model="queryParam.dateType" placeholder="请选择" dictCode="bu_date_type" />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="12" v-if="queryParam.dateType === '1' || queryParam.dateType === '2'">
            <a-form-item>
              <a-date-picker
                mode="year"
                placeholder="请选择年份"
                format="YYYY"
                v-model="year"
                :open="yearPickShow"
                @panelChange="handlePanelChange"
                @openChange="handleOpenChange"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="12" v-if="queryParam.dateType === '2'">
            <a-form-item>
              <j-dict-select-tag v-model="queryParam.quarter" placeholder="请选择" dictCode="bu_quarter_type" />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="12" v-if="queryParam.dateType === '3'">
            <a-form-item>
              <a-month-picker placeholder="选择月份" v-model="monthSelect"> </a-month-picker>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="12" v-if="queryParam.dateType === '4'">
            <a-form-item>
              <a-range-picker v-model="rangeDate" :defaultPickerValue="defaultDateRange" > </a-range-picker>
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="getCostTabDetail">查询</a-button>
                <a-button @click="resetSearch">重置</a-button>
                <a-button>
                  <a :style="{ fontSize: '12px' }" @click="toggle">
                    更多条件 <a-icon :type="expand ? 'up' : 'down'" />
                  </a>
                </a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
        <a-row :gutter="24" :style="{ display: expand ? 'block' : 'none' }">
          <a-col :md="5" :sm="24">
            <a-form-item label="工班">
              <j-dict-select-tag v-model="queryParam.groupId" placeholder="请选择" :dictCode="dictGroupStr" />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="系统">
              <j-dict-select-tag
                :triggerChange="true"
                v-model="queryParam.sysId"
                placeholder="请选择"
                :dictCode="dictSysIdStr"
              />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <!-- <a-form-item label="部件">
              <a-select v-model="queryParam.assetId" allowClear>
                <a-select-option v-for="item in assetTypePart" :key="item.id" :value="item.id">
                  {{ item.assetName }}
                </a-select-option>
              </a-select>
            </a-form-item> -->
            <a-form-model-item label="部件">
              <a-select
                placeholder="请选择"
                v-model="faultAssetName"
                :open="false"
                :showArrow="true"
                @focus="showAssetModal"
                ref="faultAssetSelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div>
      <a-tabs defaultActiveKey="2" size="small" @change="changeTabs">
        <a-tab-pane key="2" tab="统计" style="height: calc(100vh - 218px); overflow: auto">
          <a-row v-if="sysDataList.length" style="margin-top: 20px">
            <a-col :span="23">
              <bar-multid title="系统物料消耗统计" :fields="sysFieldList" :dataSource="sysDataList" :rotate="-26" />
            </a-col>
          </a-row>
          <a-row>
            <a-col :span="11" >
              <pie id="pie1" title="物料消耗分类占比" tipName="消耗成本:" :dataSource="categoryDataList" />
            </a-col>
            <a-col :span="11" >
              <pie id="pie2" title="必换件消耗系统占比" tipName="消耗成本:" :dataSource="mustDataList" />
            </a-col>
            <a-col :span="11">
              <pie id="pie4" title="耗材消耗系统占比" tipName="消耗成本:" :dataSource="consumeDataList" />
            </a-col>
            <a-col :span="11" >
              <pie id="pie3" title="偶换件消耗系统占比" tipName="消耗成本:" :dataSource="randomDataList" />
            </a-col>
          </a-row>
        </a-tab-pane>
        <a-tab-pane key="1" tab="消耗明细" style="height: calc(100vh - 218px); overflow: auto">
          <p>
            总计物料金额： <span class="redFont">{{ count.total }}</span> 元， 其中必换件：<span class="redFont">{{
              count.must
            }}</span>
            元， 偶换件<span class="redFont">{{ count.random }}</span> 元， 耗材<span class="redFont">{{
              count.consume
            }}</span>
            元
          </p>
          <div>
            <vxe-table
              border
              resizable
              max-height="100%"
              style="height: calc(100vh - 308px)"
              ref="FormsListTable"
              :align="allAlign"
              :data="tableData"
              show-overflow="tooltip"
              :edit-config="{ trigger: 'manual', mode: 'row' }"
              :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
            >
              <vxe-table-column type="checkbox" width="40"></vxe-table-column>
              <vxe-table-column field="materialTypeCode" title="物料编码" width="120"></vxe-table-column>
              <vxe-table-column
                field="materialTypeName"
                title="物料名称"
                min-width="100"
                align="left"
                header-align="center"
              ></vxe-table-column>
              <vxe-table-column field="useCategory_dictText" title="物料类别" width="80"></vxe-table-column>
              <vxe-table-column
                field="consumeAmount"
                title="消耗数量"
                width="80"
                align="right"
                header-align="center"
              ></vxe-table-column>
              <vxe-table-column
                field="unitPrice"
                title="物料单价"
                width="80"
                align="right"
                header-align="center"
              ></vxe-table-column>
              <vxe-table-column
                field="consumeTotalPrice"
                title="消耗金额"
                width="80"
                align="right"
                header-align="center"
              ></vxe-table-column>
              <vxe-table-column field="lineName" title="所属线路" width="80"></vxe-table-column>
              <vxe-table-column field="trainNo" title="所属车辆" width="80"></vxe-table-column>
              <vxe-table-column field="orderCode" title="所属工单" width="120"></vxe-table-column>
              <vxe-table-column field="consumeTime" title="消耗时间" width="100"></vxe-table-column>
              <vxe-table-column field="groupName" title="消耗工班" width="120"></vxe-table-column>
              <vxe-table-column field="systemName" title="所属系统" width="120"></vxe-table-column>
              <vxe-table-column field="depotName" title="车辆段" width="120"></vxe-table-column>
            </vxe-table>
          </div>
          <vxe-pager
            perfect
            :current-page.sync="queryParam.pageNo"
            :page-size.sync="queryParam.pageSize"
            :total="totalResult"
            :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
            @page-change="handlePageChange"
          ></vxe-pager>
        </a-tab-pane>
        <!-- <a-tab-pane key="3" tab="比较分析">
          <cost-compare-modal :data="queryParam"></cost-compare-modal>
        </a-tab-pane> -->
      </a-tabs>
      <train-asset-list
        ref="assetSelect"
        :multiple="false"
        :canSelectType="[3, 4]"
        @ok="addFaultAsset"
      ></train-asset-list>
    </div>
  </div>
</template>

<script>
import CostCompareModal from '../costmonitor/CostCompareModal'
import moment from 'moment'
import { getCostCount, getCostPage, getCostStatistics } from '@api/tirosProductionApi'
import BarHorizontal from '@/components/echart/BarHorizontal'
import Pie from '@/components/echart/Pie'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import BarMultid from '@/components/echart/BarMultid'
import { everythingIsEmpty } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import TrainAssetList from '@views/tiros/common/selectModules/TrainAssetList'

export default {
  mixins: [JeecgListMixin],
  components: { BarMultid, CostCompareModal, BarHorizontal, Pie, LineSelectList, TrainAssetList },
  data() {
    return {
      dictGroupStr:
        "bu_mtr_workshop_group,group_name,id,workshop_id='" + this.$store.getters.userInfo.workshopId + "'|workshop_id",
      dictTrainStr: '',
      dictSysIdStr: 'bu_train_asset_type,name,id,struct_type=1 and parent_id is null',
      dicTrainAsset: 'bu_train_asset,asset_name,id',
      assetTypePart: [],
      monthSelect: '',
      queryParam: {
        depotId: '',
        lineId: '',
        trainNo: '',
        groupId: '',
        sysId: '',
        assetId: '',
        quarter: '',
        year: '',
        month: '',
        endDate: '',
        startDate: '',
        dateType: '',
        pageNo: 1,
        pageSize: 10,
      },
      trainStructId: '',
      yearPick: null, //年选择器的值
      yearPickShow: false, //年选择器的显示隐藏
      year: null,
      url: {},
      depotName: '',
      lineId: '',
      faultAssetName: '',
      sysFieldList: [],
      sysDataList: [],
      totalSystemItemList: [],
      categoryDataList: [],
      mustDataList: [],
      randomDataList: [],
      consumeDataList: [],
      totalResult: 0,
      tableData: [],
      rangeDate: [],
      allAlign: 'center',
      expand: false,
      count: {
        random: null,
        consume: null,
        must: null,
        total: null,
      },
      searchFormShow: true,
      selectedTabKey: '1',
      formLeft: this.$form.createForm(this),
      formRight: this.$form.createForm(this),
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 19 },
      },
    }
  },
  created() {
    this.getCostStatisticsDetail()
  },
  watch: {
    lineId(newVal, oldVal) {
      if (newVal) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + newVal + '|train_struct_id'
      } else {
        this.dictTrainStr = ''
      }
    },
  },
  methods: {
    getCostTabDetail() {
      if (this.selectedTabKey === '1') {
        this.getCostDetail()
      } else if (this.selectedTabKey === '2') {
        this.getCostStatisticsDetail()
      }
    },
    handleSysFocus() {
      if (!this.lineId) this.$message.warn('请先选择线路!')
    },
    //车辆改变
    handleTrainNo(val, optionData) {
      if (val && optionData && optionData.extFields && optionData.extFields.length) {
        this.trainStructId = optionData.extFields[0].train_struct_id
        console.log(this.trainStructId)
      }
    },
    setQueryParam() {
      if (!this.rangeDate) {
        this.rangeDate = []
      }
      if (this.rangeDate.length === 0) {
        this.queryParam.startDate = ''
        this.queryParam.endDate = ''
      }
      if (this.rangeDate.length > 0 && this.queryParam.dateType === '4') {
        this.queryParam.startDate = moment(this.rangeDate[0]).format('YYYY-MM-DD')
        this.queryParam.endDate = moment(this.rangeDate[1]).format('YYYY-MM-DD')
      }

      if (!this.monthSelect && this.queryParam.dateType === '3') {
        this.queryParam.year = ''
        this.queryParam.month = ''
      }
      if (this.monthSelect && this.queryParam.dateType === '3') {
        this.queryParam.year = moment(this.monthSelect).year()
        this.queryParam.month = moment(this.monthSelect).month() + 1
      }

      this.queryParam.lineId = this.lineId
    },
    getTotalCount() {
      this.setQueryParam()
      getCostCount(this.queryParam).then((res) => {
        if (res.success) {
          this.count = res.result
        } else {
          this.$message.warning(res.message)
        }
      })
    },
    findList() {
      this.setQueryParam()
      getCostPage(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result.records
        }
      })
    },
    getCostDetail() {
      this.getTotalCount()
      this.findList()
    },
    getCostStatisticsDetail() {
      this.setQueryParam()
      getCostStatistics(this.queryParam).then((res) => {
        if (res.success) {
          console.log('result:', JSON.stringify(res))

          this.categoryDataList = res.result.categoryDataList
          this.mustDataList = res.result.mustDataList
          this.randomDataList = res.result.randomDataList
          this.consumeDataList = res.result.consumeDataList
          this.sysFieldList = res.result.sysFieldList
          this.sysDataList = res.result.sysDataList
        } else {
          this.$message.warning(res.message)
        }
      })
    },
    toggle() {
      this.expand = !this.expand
    },
    changeDateType(type) {},
    changeDepotNAme(name) {
      this.depotName = name
    },
    changeTabs(key) {
      this.selectedTabKey = key
      if (key === '1') {
        this.searchFormShow = true
        this.getCostDetail()
      } else if (key === '2') {
        this.searchFormShow = true
        this.getCostStatisticsDetail()
      } else if (key === '3') {
        this.searchFormShow = false
      }
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    // 弹出日历和关闭日历的回调
    handleOpenChange(status) {
      this.yearPickShow = status
    },
    // 得到年份选择器的值
    handlePanelChange(value) {
      this.year = value
      this.queryParam.year = moment(value).format('YYYY')
      this.yearPickShow = false
    }, //部件选择框弹出
    showAssetModal(e) {
      if (!this.queryParam.trainNo) {
        this.$message.warn('请先选择车辆!')
      } else {
        this.$refs.assetSelect.showModal(this.queryParam.trainNo, this.trainStructId)
        this.$refs.faultAssetSelect.blur()
      }
    },
    //故障部件选择后的回调方法
    addFaultAsset(data) {
      if (!everythingIsEmpty(data)) {
        this.queryParam.assetId = data[0].id
        this.faultAssetName = data[0].assetName
      }
    },
    resetSearch() {
      this.queryParam = {
        depotId: '',
        lineId: '',
        trainNo: '',
        groupId: '',
        sysId: '',
        assetId: '',
        quarter: '',
        year: '',
        month: '',
        endDate: '',
        startDate: '',
        dateType: '',
        pageNo: 1,
        pageSize: 10,
      }
      this.faultAssetName = ''
      this.lineId = ''
      this.year = null
      this.rangeDate = []
    },
  },
  computed: {
    title() {
      return this.depotName || ''
    },
  },
}
</script>

<style scoped>
</style>