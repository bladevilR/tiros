<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="4" :sm="24">
            <a-form-item label="线路">
              <line-select-list v-model="queryParam.lineId" @change="changeLine"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="车辆">
              <j-dict-select-seach-tag
                v-model="queryParam.trainNo"
                placeholder="请选择"
                @focus="handleSysFocus"
                :dictCode="dictTrainStr"
              />
            </a-form-item>
          </a-col>
<!--          <a-col :md="5" :sm="24">
            <a-form-item label="所属系统">
              <a-select
                v-model="assetTypeName"
                placeholder="请选择所属系统"
                :open="false"
                :showArrow="true"
                @focus="openAssetTypeModal"
                ref="myAssetTypeSelect">
                <div slot='suffixIcon' >
                  <a-icon v-if="queryParam.assetTypeId" @click.stop="clearValue"  type="close-circle" style="padding-right: 3px" />
                  <a-icon type='ellipsis' />
                </div>
              </a-select>
            </a-form-item>
          </a-col>-->
          <a-col :md="3" :sm="12">
            <a-form-item label="时间">
              <j-dict-select-tag
                v-model="queryParam.dateType"
                placeholder="请选择"
                dictCode="bu_date_type"
              />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="12" v-if="(queryParam.dateType==='1'||queryParam.dateType===1||queryParam.dateType==='2')">
            <a-form-item>
              <a-date-picker
                mode="year"
                placeholder="请选择年份"
                format="YYYY"
                v-model="year"
                :open="yearPickShow"
                @panelChange="handlePanelChange"
                @openChange="handleOpenChange"
                @change="handleChanYearChange"
                style="width:100%"
              />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="12" v-if="queryParam.dateType==='2'">
            <a-form-item >
              <j-dict-select-tag
                v-model="queryParam.quarter"
                placeholder="请选择"
                dictCode="bu_quarter_type"
              />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="12" v-if="queryParam.dateType==='3'">
            <a-form-item >
              <a-month-picker placeholder="选择月份" v-model="month" @change="handleMonthChange">
              </a-month-picker>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="12" v-if="queryParam.dateType==='4'">
            <a-form-item >
              <a-range-picker v-model="rangeDate" :defaultPickerValue="defaultDateRange" >
              </a-range-picker>
            </a-form-item>
          </a-col>
          <a-col :md="2" :sm="24">
            <a-button @click="findList">查询</a-button>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 225px)">
    <vxe-table
      border
      auto-resize
      ref="listTable"
      max-height="100%"
      style="height: calc(100vh - 225px)"
      :align="allAlign"
      :data="tableData"
      show-overflow="tooltip"
      :checkbox-config="{trigger: 'row', highlight: true, range: true}"
      :edit-config="{trigger: 'manual', mode: 'row'}"
    >
      <vxe-table-column type="checkbox" width="40" ></vxe-table-column>
      <vxe-table-column type="seq" title="序号" width="80" ></vxe-table-column>
      <vxe-table-column field="assetName" align="left" header-align="center" title="部件名称" min-width="100" ></vxe-table-column>
      <vxe-table-column field="sendAmount" title="送出数量" width="120" header-align="center" align="right">
      </vxe-table-column>
      <vxe-table-column field="returnAmount" title="返厂数量" width="120"  header-align="center" align="right">
      </vxe-table-column>
      <vxe-table-column field="failedAmount" title="报废数量" width="120" header-align="center" align="right"></vxe-table-column>
<!--      <vxe-table-column field="sysName" title="所属系统" min-width="180" header-align="center" align="left"></vxe-table-column>-->
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
    <asset-type ref="assetTypeForm" :multiple="false" @ok="addTarget"></asset-type>
  </div>
</template>

<script>
  import { getParts} from "@api/tirosOutsourceApi"
  import AssetType from './AssetType'
  import moment from 'moment'
  import { everythingIsEmpty } from '@/utils/util'
  import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

  export default {
    name: 'PartsStatistics',
    components:{AssetType,LineSelectList},
    data(){
      return {
        dictTrainStr: '',
        assetTypeName:'',
        year:null,
        month: null,
        component:null,
        rangeDate:[],
        queryParam: {
          searchText: '',
          lineId: '',
          trainNo: '',
          quarter:'',
          year: '',
          month:'',
          endDate:'',
          startDate:'',
          dateType:'',
          assetTypeId:'',
          pageNo: 1,
          pageSize: 10
        },
        totalResult: 0,
        allAlign: 'center',
        tableData: [],
        yearPick: null, //年选择器的值
        yearPickShow: false, //年选择器的显示隐藏
      }
    },
    watch: {
      'queryParam.dateType': {
        immediate: true,
        deep: true,
        handler (value) {
          this.queryParam.quarter = ''
          this.queryParam.year = ''
          this.queryParam.month = ''
          this.queryParam.endDate = ''
          this.queryParam.startDate = ''
          this.year = null
          this.month=null
        }
      }
    },
    created() {
      this.findList()
    },
    methods:{
      handleSysFocus() {
        if (!this.queryParam.lineId) this.$message.warn('请先选择线路!')
      },
      changeLine(data, edit) {
        this.dictTrainStr = ''
        if (data) {
          this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + data + '|train_struct_id'
        }
      },
      findList() {
        let { dateType, year, quarter, month,} = this.queryParam
        switch (dateType) {
        case '1':
          if (everythingIsEmpty(year)) {
            this.$message.warning('请选择年份！')
            return
          }
          break
        case '2':
          if (everythingIsEmpty(year)) {
            this.$message.warning('请选择年份！')
            return
          }
          if (everythingIsEmpty(quarter)) {
            this.$message.warning('请选择季度！')
            return
          }
          break
        case '3':
          if (everythingIsEmpty(this.month)) {
            this.$message.warning('请选择月份！')
            return
          }
          break
        case '4':
          if (everythingIsEmpty(this.rangeDate)) {
            this.$message.warning('请选择时间段！')
            return
          }
          break
        default:
          break
        }
        if (!everythingIsEmpty(this.rangeDate) && dateType === '4') {
          this.queryParam.startDate = moment(this.rangeDate[0]).format('YYYY-MM-DD')
          this.queryParam.endDate = moment(this.rangeDate[1]).format('YYYY-MM-DD')
        }
        if (!everythingIsEmpty(this.month) && dateType === '3') {
          this.queryParam.year = moment(this.month).year()
          this.queryParam.month = moment(this.month).month() + 1
        }
        getParts(this.queryParam).then((res) => {
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
      loadData() {
        this.findList()
        this.$emit('load')
      },
      openAssetTypeModal(){
        this.$refs.assetTypeForm.showModal()
        this.$refs.myAssetTypeSelect.blur()
      },
      addTarget(data) {
        if (data.length>0&&data[0]!=null) {
          this.queryParam.assetTypeId=data[0].id
          this.assetTypeName=data[0].name
        }
      },
      clearValue () {
        this.queryParam.assetTypeId=''
        this.assetTypeName=''
      },
      // 弹出日历和关闭日历的回调
      handleOpenChange(
        status) {
        this.yearPickShow = status
      },
      // 得到年份选择器的值
      handlePanelChange(value) {
        this.year = value
        this.queryParam.year = moment(value).format('YYYY')
        this.yearPickShow = false
      },
      handleChanYearChange (value){
        if(everythingIsEmpty(value)) this.queryParam.year=''
      },
      handleMonthChange(value){
        if(everythingIsEmpty(value)) {
          this.queryParam.year=''
          this.queryParam.month=''
        }
      },
    }
  }
</script>

<style scoped>

</style>