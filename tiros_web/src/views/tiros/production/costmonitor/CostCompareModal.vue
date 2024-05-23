<template>
  <div id="MonitorContent">
    <div class="compareContent">
      <div class="openModalDiv" @click="handleToggleSearch">比较分析条件设置</div>
      <div class="compareSetDiv table-page-search-wrapper" scrolling="auto" v-if="toggleSearchStatus">
        <a-form :form="form" :rules="validatorRules">
          <a-col :span="12" style="border-right:1px dashed #aaa">
            <a-row :gutter="12">
              <a-col :span="16">
                <a-row>
                  <a-col :span="12">
                    <a-form-item :labelCol="{ span: 10 }" :wrapperCol="{ span: 14 }" label="车辆段">
                      <j-dict-select-tag
                        v-model="queryParam.query1.depotId"
                        placeholder="请选择"
                        dictCode="bu_mtr_depot,name,id"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :span="12">
                    <a-form-item :labelCol="{ span: 10 }" :wrapperCol="{ span: 14 }" label="线路">
<!--                      <j-dict-select-tag
                        v-model="lineId1"
                        dictCode="bu_mtr_line,line_name,line_id"
                      />-->
                      <line-select-list v-model="lineId1"></line-select-list>
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-row>
                  <a-col :span="24">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工班">
                      <j-dict-select-tag
                        v-model="queryParam.query1.groupId"
                        dictCode="sys_depart,depart_name,id,org_category=4"
                        placeholder="请选择"
                      />
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-row>
                  <a-col :span="24">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆">
                      <!--                        <j-dict-select-tag-->
                      <!--                          v-model="query1.trainNo"-->
                      <!--                          :dictCode="dictTrainStr1"-->
                      <!--                        />-->
                      <j-dict-select-seach-tag
                        :dictCode="dictTrainStr1"
                        v-model="trainNo1"
                      />
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-row>
                  <a-col :span="24">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="系统">
                      <j-dict-select-tag
                        :triggerChange="true"
                        v-model="queryParam.query1.sysId"
                        :dictCode="dictSysIdStr1"
                        placeholder="请选择"
                        @change="handleDicTrainAssetLeft"
                      />
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-row>
                  <a-col :span="24">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件">
                      <!--                        <j-dict-select-tag-->
                      <!--                          v-model="query1.assetId"-->
                      <!--                          placeholder="请选择"-->
                      <!--                          :dict-code="dicTrainAssetLeft"-->
                      <!--                        />-->
                      <a-select v-model="queryParam.query1.assetId" allowClear>
                        <a-select-option v-for="item in assetTypePart1" :key="item.id" :value="item.id">
                          {{ item.assetName }}
                        </a-select-option>
                      </a-select>
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-row :gutter="24">
                  <a-col :md="10">
                    <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="时间">
                      <j-dict-select-tag
                        v-model="queryParam.query1.dateType"
                        placeholder="请选择"
                        dictCode="bu_date_type"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :md="8" v-if="(queryParam.query1.dateType==='1'||queryParam.query1.dateType==='2')">
                    <a-form-item>
                      <a-date-picker
                        mode="year"
                        placeholder="请选择年份"
                        format="YYYY"
                        v-model="year1"
                        :open="yearPickShow1"
                        @panelChange="handlePanelChange1"
                        @openChange="handleOpenChange1"
                        style="width:100%"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :md="6" v-if="queryParam.query1.dateType==='2'">
                    <a-form-item>
                      <j-dict-select-tag
                        v-model="queryParam.query1.quarter"
                        placeholder="请选择"
                        dictCode="bu_quarter_type"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :md="14" v-if="queryParam.query1.dateType==='3'">
                    <a-form-item>
                      <a-month-picker :style="{width: '100%'}" placeholder="选择月份" v-model="queryParam.query1.month">
                      </a-month-picker>
                    </a-form-item>
                  </a-col>
                  <a-col :md="14" v-if="queryParam.query1.dateType==='4'">
                    <a-form-item>
                      <a-range-picker v-model="rangeDate1" :defaultPickerValue="defaultDateRange" >
                      </a-range-picker>
                    </a-form-item>
                  </a-col>
                </a-row>
              </a-col>
              <a-col :span="8">
                <p style="font-weight: bold;font-size: 15px;margin-top: 10px;float: left;margin-left: 38px">成本项</p>
                <a-form-item>
                  <a-checkbox-group
                    v-model="queryParam.query1.costItems"
                    style="width: 68%;"
                  >
                    <a-row>
                      <a-col :style="{float:'left',marginBottom:'5px'}">
                        <a-checkbox value="1" @change="selectQuery1Single">
                          列成本
                        </a-checkbox>
                      </a-col>
                    </a-row>
                    <a-row>
                      <a-col :style="{float:'left',marginBottom:'5px'}">
                        <a-checkbox value="2" @change="selectQuery1Average">
                          平均成本
                        </a-checkbox>
                      </a-col>
                    </a-row>
                  </a-checkbox-group>
                </a-form-item>
              </a-col>
            </a-row>
            <!--            </a-form>-->
          </a-col>
          <a-col :span="12">
            <!--            <a-form :form="formRight">-->
            <a-row :gutter="12">
              <a-col :span="16">
                <a-row>
                  <a-col :span="12">
                    <a-form-item :labelCol="{ span: 10 }" :wrapperCol="{ span: 14 }" label="车辆段">
                      <j-dict-select-tag
                        v-model="queryParam.query2.depotId"
                        placeholder="请选择"
                        dictCode="bu_mtr_depot,name,id"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :span="12">
                    <a-form-item :labelCol="{ span: 10 }" :wrapperCol="{ span: 14 }" label="线路">
<!--                      <j-dict-select-tag
                        v-model="lineId2"
                        dictCode="bu_mtr_line,line_name,line_id"
                      />-->
                      <line-select-list v-model="lineId2" ></line-select-list>
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-row>
                  <a-col :span="24">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工班">
                      <j-dict-select-tag
                        v-model="queryParam.query2.groupId"
                        dictCode="sys_depart,depart_name,id,org_category=4"
                        placeholder="请选择"
                      />
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-row>
                  <a-col :span="24">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆">
                      <!--                        <j-dict-select-tag-->
                      <!--                          v-model="query2.trainNo"-->
                      <!--                          :dictCode="dictTrainStr2"-->
                      <!--                        />-->
                      <j-dict-select-seach-tag
                        :dictCode="dictTrainStr2"
                        v-model="trainNo2"
                      />
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-row>
                  <a-col :span="24">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="系统">
                      <j-dict-select-tag
                        :triggerChange="true"
                        v-model="queryParam.query2.sysId"
                        :dictCode="dictSysIdStr2"
                        placeholder="请选择"
                        @change="handleDicTrainAssetRight"
                      />
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-row>
                  <a-col :span="24">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件">
                      <!--                        <j-dict-select-tag-->
                      <!--                          v-model="query2.assetId"-->
                      <!--                          placeholder="请选择"-->
                      <!--                          :dict-code="dicTrainAssetRight"-->
                      <!--                        />-->
                      <a-select v-model="queryParam.query2.assetId" allowClear>
                        <a-select-option v-for="item in assetTypePart2" :key="item.id" :value="item.id">
                          {{ item.assetName }}
                        </a-select-option>
                      </a-select>
                    </a-form-item>
                  </a-col>
                </a-row>
                <a-row :gutter="24">
                  <a-col :md="10">
                    <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="时间">
                      <j-dict-select-tag
                        v-model="queryParam.query2.dateType"
                        placeholder="请选择"
                        dictCode="bu_date_type"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :md="8" v-if="(queryParam.query2.dateType==='1'||queryParam.query2.dateType==='2')">
                    <a-form-item>
                      <a-date-picker
                        mode="year"
                        placeholder="请选择年份"
                        format="YYYY"
                        v-model="year2"
                        :open="yearPickShow2"
                        @panelChange="handlePanelChange2"
                        @openChange="handleOpenChange2"
                        style="width:100%"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :md="6" v-if="queryParam.query2.dateType==='2'">
                    <a-form-item>
                      <j-dict-select-tag
                        v-model="queryParam.query2.quarter"
                        placeholder="请选择"
                        dictCode="bu_quarter_type"
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :md="14" v-if="queryParam.query2.dateType==='3'">
                    <a-form-item>
                      <a-month-picker :style="{width: '100%'}" placeholder="选择月份" v-model="queryParam.query2.month">
                      </a-month-picker>
                    </a-form-item>
                  </a-col>
                  <a-col :md="14" v-if="queryParam.query2.dateType==='4'">
                    <a-form-item>
                      <a-range-picker v-model="rangeDate2" :defaultPickerValue="defaultDateRange" >
                      </a-range-picker>
                    </a-form-item>
                  </a-col>
                </a-row>
              </a-col>
              <a-col :span="8">
                <p style="font-weight: bold;font-size: 15px;margin-top: 10px;float: left;margin-left: 38px">成本项</p>
                <a-form-item>
                  <a-checkbox-group
                    v-model="queryParam.query2.costItems"
                    style="width: 68%;"
                  >
                    <a-row>
                      <a-col :style="{float:'left',marginBottom:'5px'}">
                        <a-checkbox value="1" @change="selectQuery2Single">
                          列成本
                        </a-checkbox>
                      </a-col>
                    </a-row>
                    <a-row>
                      <a-col :style="{float:'left',marginBottom:'5px'}">
                        <a-checkbox value="2" @change="selectQuery2Average">
                          平均成本
                        </a-checkbox>
                      </a-col>
                    </a-row>
                  </a-checkbox-group>
                </a-form-item>
              </a-col>
            </a-row>
          </a-col>
        </a-form>
        <a-button type="primary" @click="compareCostDiff">对比分析</a-button>
        <a-button style="margin-left: 8px" @click="handleReset">清空条件</a-button>
      </div>

    </div>
    <div class="chartsDiv">
      <a-row :gutter="24" style="margin-top: 20px">
        <a-col :span="23">
          <a-row v-if="costTrendDataList.length">
            <a-col :span="24">
              <bar-stacked :fields="costTrendFieldList" title="车辆架大修成本趋势" :dataSource="costTrendDataList"/>
            </a-col>
          </a-row>
          <a-row v-if="systemDataList.length">
            <a-col :span="24">
              <bar-multid :fields="systemFieldList" title="系统物料消耗对比" :dataSource="systemDataList"/>
            </a-col>
          </a-row>
          <a-row v-if="categoryDataList.length">
            <a-col :span="24">
              <bar-multid :fields="categoryFieldList" title="物料分类消耗对比" :dataSource="categoryDataList"/>
            </a-col>
          </a-row>
          <a-row v-if="mustSystemDataList.length">
            <a-col :span="24">
              <bar-multid :fields="mustSystemFieldList" title="必换件系统消耗对比" :dataSource="mustSystemDataList"/>
            </a-col>
          </a-row>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script>
import moment from 'moment'
import BarHorizontal from '@/components/chart/BarHorizontal'
import Pie from '@/components/chart/Pie'
import { getCostCompare, getCostCountSixYear } from '@/api/tirosProductionApi'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import BarMultid from '../../../../components/chart/BarMultid'
import BarStacked from '../../../../components/chart/BarStacked'
import { listOrTreeTrainAsset } from '@api/tirosApi'
import { everythingIsEmpty } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'CostCompareModal',
  mixins: [JeecgListMixin],
  props: ['data'],
  components: { BarStacked, BarMultid, BarHorizontal, Pie ,LineSelectList},
  data() {
    return {
      queryParam: {
        query1: {
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
          costItems: [],
          single: false,
          average: false
        },
        query2: {
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
          costItems: [],
          single: false,
          average: false
        }
      },
      dictTrainStr2: 'bu_train_info,train_no,train_no,status=1',
      dictTrainStr1: 'bu_train_info,train_no,train_no,status=1',
      dictSysIdStr1: 'bu_train_asset_type,name,id,struct_type=1 and parent_id is null',
      dictSysIdStr2: 'bu_train_asset_type,name,id,struct_type=1 and parent_id is null',
      assetTypePart1: [],
      assetTypePart2: [],
      yearPick1: null, //年选择器的值
      yearPickShow1: false, //年选择器的显示隐藏
      yearPick2: null, //年选择器的值
      yearPickShow2: false, //年选择器的显示隐藏
      year1: null,
      year2: null,
      url: {},
      lineId1: '',
      lineId2: '',
      trainNo1: '',
      trainNo2: '',
      totalResult: 0,
      rangeDate1: [],
      rangeDate2: [],
      allAlign: 'center',
      expand: false,
      toggleSearchStatus: true,
      count: {
        total: null,
        handled: null,
        unhandled: null
      },
      costTrendFieldList: [],
      costTrendDataList: [],
      systemFieldList: [],
      systemDataList: [],
      categoryFieldList: [],
      categoryDataList: [],
      mustSystemFieldList: [],
      mustSystemDataList: [],
      form: this.$form.createForm(this),
      validatorRules: {
        trainNo1: [],
        trainNo2: []
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 19 }
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 13 }
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 11 }
      }
    }
  }
  ,
  created() {
    this.getYearThead()
    this.compareCostDiff()
  }
  ,
  watch: {
    lineId1(newVal, oldVal) {
      if (newVal) {
        this.dictTrainStr1 = 'bu_train_info,train_no,train_no,line_id=' + newVal
      } else {
        this.dictTrainStr1 = 'bu_train_info,train_no,train_no'
      }
    }
    ,
    lineId2(newVal, oldVal) {
      if (newVal) {
        this.dictTrainStr2 = 'bu_train_info,train_no,train_no,line_id=' + newVal
      } else {
        this.dictTrainStr2 = 'bu_train_info,train_no,train_no'
      }
    }
  }
  ,
  methods: {
    selectQuery1Single() {
        this.queryParam.query1.costItems.splice(this.queryParam.query1.costItems.indexOf('2'),1)
    },
    selectQuery1Average() {
      this.queryParam.query1.costItems.splice(this.queryParam.query1.costItems.indexOf('1'),1)
    },
    selectQuery2Single() {
      this.queryParam.query2.costItems.splice(this.queryParam.query1.costItems.indexOf('2'),1)
    },
    selectQuery2Average() {
      this.queryParam.query2.costItems.splice(this.queryParam.query1.costItems.indexOf('1'),1)
    },
    handleDicTrainAssetLeft(data) {
      if (data) {
        /*  this.dicTrainAssetLeft='bu_train_asset,asset_name,id,train_id = ' +
            '(select id from bu_train_info where train_no =\'' + this.query1.trainNo + '\') or ' +
            '(asset_type_id = \'' + this.query1.sysId + '\' or  asset_type_id in (' +
            'select id from bu_train_asset_type where struct_type=2 and parent_id= \'' + this.query1.sysId + '\') )'*/
        let assetQueryParam = {
          assetTypeId: this.queryParam.query1.sysId,
          trainNo: this.trainNo1,
          queryAllChildren: true
        }
        listOrTreeTrainAsset(assetQueryParam).then((res) => {
          this.assetTypePart1 = res.result
        })
      } else {
        this.assetTypePart1 = []
        this.dicTrainAssetLeft = 'bu_train_asset,asset_name,id'
      }
    },
    handleDicTrainAssetRight(data) {
      if (data) {
        /* this.dicTrainAssetRight='bu_train_asset,asset_name,id,train_id = ' +
           '(select id from bu_train_info where train_no =\'' + this.query2.trainNo + '\') or ' +
           '(asset_type_id = \'' + this.query2.sysId + '\' or  asset_type_id in (' +
           'select id from bu_train_asset_type where struct_type=2 and parent_id= \'' + this.query2.sysId + '\') )'*/
        let assetQueryParam = {
          assetTypeId: this.queryParam.query2.sysId,
          trainNo: this.trainNo2,
          queryAllChildren: true
        }
        listOrTreeTrainAsset(assetQueryParam).then((res) => {
          this.assetTypePart2 = res.result
        })
      } else {
        this.assetTypePart2 = []
        this.dicTrainAssetRight = 'bu_train_asset,asset_name,id'
      }
    },
    getYearThead() {
      getCostCountSixYear().then(res => {
        if (res.success) {
          this.costTrendFieldList = res.result.fieldList
          this.costTrendDataList = res.result.dataList
        }
      })
    },
    setQueryParams() {
      if (this.rangeDate1.length > 0 && this.queryParam.query1.dateType === '4') {
        this.queryParam.query1.startDate = moment(this.rangeDate1[0]).format('YYYY-MM-DD')
        this.queryParam.query1.endDate = moment(this.rangeDate1[1]).format('YYYY-MM-DD')
      }
      if (this.queryParam.query1.month && this.queryParam.query1.dateType === '3') {
        this.queryParam.query1.year = moment(this.queryParam.query1.month).year()
        this.queryParam.query1.month = moment(this.queryParam.query1.month).month() + 1
      }
      this.queryParam.query1.lineId = this.lineId1

      if (this.rangeDate2.length > 0 && this.queryParam.query2.dateType === '4') {
        this.queryParam.query2.startDate = moment(this.rangeDate2[0]).format('YYYY-MM-DD')
        this.queryParam.query2.endDate = moment(this.rangeDate2[1]).format('YYYY-MM-DD')
      }
      if (this.queryParam.query2.month && this.queryParam.query2.dateType === '3') {
        this.queryParam.query2.year = moment(this.queryParam.query2.month).year()
        this.queryParam.query2.month = moment(this.queryParam.query2.month).month() + 1
      }
      this.queryParam.query2.lineId = this.lineId2

      // 列成本、平均成本
      this.queryParam.query1.single = false
      this.queryParam.query1.average = false
      this.queryParam.query2.single = false
      this.queryParam.query2.average = false
      if (this.queryParam.query1.costItems.indexOf('1') > -1) {
        this.queryParam.query1.single = true
      }
      if (this.queryParam.query1.costItems.indexOf('2') > -1) {
        this.queryParam.query1.average = true
      }
      if (this.queryParam.query2.costItems.indexOf('1') > -1) {
        this.queryParam.query2.single = true
      }
      if (this.queryParam.query2.costItems.indexOf('2') > -1) {
        this.queryParam.query2.average = true
      }
      // 车辆号
      this.queryParam.query1.trainNo = this.trainNo1
      this.queryParam.query2.trainNo = this.trainNo2
    },
    compareCostDiff() {
      // this.jxList=[]
      // this.zbList=[]
      // this.cbList=[]
      // this.jxListR=[]
      // this.zbListR=[]
      // this.cbListR=[]

      this.setQueryParams()

      // 列成本必须指定车辆号trainNo
      let needTrainNo1 = false
      let needTrainNo2 = false
      if (this.queryParam.query1.costItems.indexOf('1') > -1) {
        this.validatorRules.trainNo1 = [{ required: true, message: '请选择车辆!' }]
        needTrainNo1 = true
      }
      if (this.queryParam.query2.costItems.indexOf('1') > -1) {
        this.validatorRules.trainNo2 = [{ required: true, message: '请选择车辆!' }]
        needTrainNo2 = true
      }
      if ((needTrainNo1 && everythingIsEmpty(this.trainNo1)) || (needTrainNo2 && everythingIsEmpty(this.trainNo2))) {
        this.$message.error('请选择列成本所属车辆!')
      }

      // 触发表单验证
      this.form.validateFields((err, values) => {
        if (!err) {
          getCostCompare(this.queryParam).then(res => {
            if (res.success) {
              // this.tableData = res.result
              this.systemFieldList = res.result.systemFieldList
              this.systemDataList = res.result.systemDataList
              this.categoryFieldList = res.result.categoryFieldList
              this.categoryDataList = res.result.categoryDataList
              this.mustSystemFieldList = res.result.mustSystemFieldList
              this.mustSystemDataList = res.result.mustSystemDataList

              this.handleToggleSearch()
            }
          })
        }
      })
    }
    ,
    handleReset() {
      this.query1 = []
      this.query2 = []
      this.year1 = null
      this.year2 = null
      this.lineId1 = ''
      this.lineId2 = ''
    }
    ,
    // 弹出日历和关闭日历的回调
    handleOpenChange1(
      status) {
      this.yearPickShow1 = status
    }
    ,
    // 得到年份选择器的值
    handlePanelChange1(value) {
      this.year1 = value
      this.query1.year = moment(value).format('YYYY')
      this.yearPickShow1 = false
    }
    ,
    // 弹出日历和关闭日历的回调
    handleOpenChange2(
      status) {
      this.yearPickShow2 = status
    }
    ,
    // 得到年份选择器的值
    handlePanelChange2(value) {
      this.year2 = value
      this.query2.year = moment(value).format('YYYY')
      this.yearPickShow2 = false
    }
  }
  ,
  computed: {
    title() {
      return this.depotName || ''
    }
  }
}
</script>

<style lang="less">
#MonitorContent {
  .redFont {
    color: red;
    font-weight: bold;
  }

  .compareContent {
    position: relative;
    width: 100%;
    border: 1px solid #eee;
    /*height: calc(100vh - 210px);*/
    text-align: center;

    .openModalDiv {
      display: inline-block;
      height: 30px;
      width: 300px;
      line-height: 30px;
      border-radius: 0 0 30px 30px;
      background: #eee;
      position: relative;
      top: 0;
      cursor: pointer;
    }

    .compareSetDiv {
      width: 100%;
      /*height: calc(100vh - 250px);*/
      padding: 10px;
      // background: chocolate;
      position: relative;
      top: 0;
      overflow-y: auto;
    }
  }
}

</style>