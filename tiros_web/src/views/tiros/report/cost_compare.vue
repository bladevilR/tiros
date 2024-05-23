<template>
  <div id="MonitorContent" class="bodyWrapper" style="padding: 8px">
    <!-- 新版查询面板 -->
    <div class="table-page-search-wrapper">
      <!-- 第一个条件 -->
      <a-form layout="inline">
        <!-- 第一行 -->
        <a-row :gutter="24">
          <a-col :md="4">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆段">
              <j-dict-select-tag
                v-model="queryParam.query1.depotId"
                placeholder="请选择"
                dictCode="bu_mtr_depot,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路" @change="handleLineLeft">
              <line-select-list v-model="lineId1"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆">
              <j-dict-select-seach-tag @focus="handleSysFocus" :dictCode="dictTrainStr1" v-model="trainNo1" />
            </a-form-item>
          </a-col>
          <a-col :md="4">
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

          <a-col :md="4">
            <a-space>
              <a-button @click="compareCostDiff">查询</a-button>
              <a-button @click="handleReset">清空条件</a-button>
              <a-button>
                <a :style="{ fontSize: '12px' }" @click="moreVisible = !moreVisible">
                  更多条件 <a-icon :type="moreVisible ? 'up' : 'down'" /> </a
              ></a-button>
            </a-space>
          </a-col>
        </a-row>
        <!-- 第二行 -->
        <a-row :gutter="24" v-if="moreVisible">
          <a-col :md="4">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件" style="padding-left: 14px">
              <!-- <a-select v-model="queryParam.query1.assetId" allowClear>
                <a-select-option v-for="item in assetTypePart1" :key="item.id" :value="item.id">
                  {{ item.assetName }}
                </a-select-option>
              </a-select> -->
              <a-select
                placeholder="请选择"
                v-model="assetNameLeft"
                :open="false"
                :showArrow="true"
                @focus="showTrainAssetType(1)"
                ref="trainAssetTypeSelectLeft"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="4">
            <a-form-item label="工班">
              <j-dict-select-tag
                v-model="queryParam.query1.groupId"
                dictCode="sys_depart,depart_name,id,org_category=4"
                placeholder="请选择"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :xl="8">
            <NaSelectDate ref="naDate1" :defaultType="null" @change="dateChange1"></NaSelectDate>
            <!-- <a-row :gutter="24">
              <a-col :md="10">
                <a-form-item label="时间">
                  <j-dict-select-tag
                    v-model="queryParam.query1.dateType"
                    placeholder="请选择"
                    dictCode="bu_date_type"
                    :triggerChange="true"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="(queryParam.query1.dateType === '1') ? 14 : 8" v-if="queryParam.query1.dateType === '1' || queryParam.query1.dateType === '2'">
                <a-form-item>
                  <a-date-picker
                    mode="year"
                    placeholder="请选择年份"
                    format="YYYY"
                    v-model="year1"
                    :open="yearPickShow1"
                    @panelChange="handlePanelChange1"
                    @openChange="handleOpenChange1"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="6" v-if="queryParam.query1.dateType === '2'">
                <a-form-item>
                  <j-dict-select-tag
                    v-model="queryParam.query1.quarter"
                    placeholder="请选择"
                    dictCode="bu_quarter_type"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="14" v-if="queryParam.query1.dateType === '3'">
                <a-form-item>
                  <a-month-picker :style="{ width: '100%' }" placeholder="选择月份" v-model="queryParam.query1.month">
                  </a-month-picker>
                </a-form-item>
              </a-col>
              <a-col :md="14" v-if="queryParam.query1.dateType === '4'">
                <a-form-item>
                  <a-range-picker v-model="rangeDate1" style="width: 100%" :defaultPickerValue="defaultDateRange" > </a-range-picker>
                </a-form-item>
              </a-col>
            </a-row> -->
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="成本种类">
              <a-checkbox-group v-model="queryParam.query1.costItems" style="width: 90%">
                <a-checkbox value="1" @change="selectQuery1Single"> 列成本 </a-checkbox>
                <a-checkbox value="2" @change="selectQuery1Average"> 平均成本 </a-checkbox>
              </a-checkbox-group>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24" v-if="moreVisible">
          <a-col :md="4" :sm="24">
            <a-form-item label="是否比较分析">
              <a-switch v-model="compare"></a-switch>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <!-- 分割线 -->
      <div style="border-top: 1px dashed rgb(170, 170, 170); margin: 10px 0 25px 0" v-if="moreVisible && compare"></div>
      <!-- 第二个条件 -->
      <a-form layout="inline" v-if="moreVisible && compare">
        <!-- 第一行 -->
        <a-row :gutter="24">
          <a-col :md="4">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆段">
              <j-dict-select-tag
                v-model="queryParam.query2.depotId"
                placeholder="请选择"
                dictCode="bu_mtr_depot,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路" @change="handleLineRight">
              <line-select-list v-model="lineId2"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆">
              <j-dict-select-seach-tag :dictCode="dictTrainStr2" v-model="trainNo2" />
            </a-form-item>
          </a-col>
          <a-col :md="4">
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
        <!-- 第二行 -->
        <a-row :gutter="24" v-if="moreVisible">
          <a-col :md="4">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件" style="padding-left: 14px">
              <!-- <a-select v-model="queryParam.query2.assetId" allowClear>
                <a-select-option v-for="item in assetTypePart1" :key="item.id" :value="item.id">
                  {{ item.assetName }}
                </a-select-option>
              </a-select> -->
              <a-select
                placeholder="请选择"
                v-model="assetNameRight"
                :open="false"
                :showArrow="true"
                @focus="showTrainAssetType(2)"
                ref="trainAssetTypeSelectRight"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="4">
            <a-form-item label="工班">
              <j-dict-select-tag
                v-model="queryParam.query2.groupId"
                dictCode="sys_depart,depart_name,id,org_category=4"
                placeholder="请选择"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :xl="8">
            <NaSelectDate ref="naDate2" :defaultType="null" @change="dateChange2"></NaSelectDate>
            <!-- <a-row :gutter="24">
              <a-col :md="10">
                <a-form-item label="时间">
                  <j-dict-select-tag
                    v-model="queryParam.query2.dateType"
                    placeholder="请选择"
                    dictCode="bu_date_type"
                    :triggerChange="true"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="(queryParam.query2.dateType === '1') ? 14 : 8" v-if="queryParam.query2.dateType === '1' || queryParam.query2.dateType === '2'">
                <a-form-item>
                  <a-date-picker
                    mode="year"
                    placeholder="请选择年份"
                    format="YYYY"
                    v-model="year2"
                    :open="yearPickShow2"
                    @panelChange="handlePanelChange2"
                    @openChange="handleOpenChange2"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="6" v-if="queryParam.query2.dateType === '2'">
                <a-form-item>
                  <j-dict-select-tag
                    v-model="queryParam.query2.quarter"
                    placeholder="请选择"
                    dictCode="bu_quarter_type"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="14" v-if="queryParam.query2.dateType === '3'">
                <a-form-item>
                  <a-month-picker :style="{ width: '100%' }" placeholder="选择月份" v-model="queryParam.query2.month">
                  </a-month-picker>
                </a-form-item>
              </a-col>
              <a-col :md="14" v-if="queryParam.query2.dateType === '4'">
                <a-form-item>
                  <a-range-picker v-model="rangeDate2" style="width: 100%" :defaultPickerValue="defaultDateRange" > </a-range-picker>
                </a-form-item>
              </a-col>
            </a-row> -->
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="成本种类">
              <a-checkbox-group v-model="queryParam.query2.costItems" style="width: 90%">
                <a-checkbox value="1" @change="selectQuery2Single"> 列成本 </a-checkbox>
                <a-checkbox value="2" @change="selectQuery2Average"> 平均成本 </a-checkbox>
              </a-checkbox-group>
            </a-form-item>
          </a-col>
        </a-row>
        <!-- <a-row :gutter="24" v-if="moreVisible" >
          <a-col :md="4" :sm="24">
            <a-form-item label="成本种类">
              <a-checkbox-group v-model="queryParam.query2.costItems" style="width: 90%">
                <a-checkbox value="1" @change="selectQuery2Single"> 列成本 </a-checkbox>
                <a-checkbox value="2" @change="selectQuery2Average"> 平均成本 </a-checkbox>
              </a-checkbox-group>
            </a-form-item>
          </a-col>
        </a-row> -->
      </a-form>
    </div>

    <div class="chartsDiv">
      <div
        v-if="
          !costTrendFieldList.length &&
          !systemFieldList.length &&
          !categoryFieldList.length &&
          !mustSystemFieldList.length
        "
        style="text-align: center; margin-top: 30px"
      >
        暂无数据
      </div>
      <a-row :gutter="24" style="margin-top: 20px">
        <a-col :span="23">
          <!-- <a-row v-if="costTrendDataList.length">
            <a-col :span="24">
              <bar-multid
                :fields="costTrendFieldList"
                title="车辆架大修成本趋势"
                :dataSource="costTrendDataList"
                :id="'barMultidEchart01'"
              />
            </a-col>
          </a-row> -->
          <a-row v-if="systemDataList.length">
            <a-col :span="24">
              <bar-multid
                :fields="systemFieldList"
                :title="compare ? '系统物料消耗对比' : '系统物料消耗'"
                :dataSource="systemDataList"
                :id="'barMultidEchart02'"
                :rotate="-26"
              />
            </a-col>
          </a-row>
          <a-row v-if="categoryDataList.length">
            <a-col :span="24">
              <bar-multid
                :fields="categoryFieldList"
                :title="compare ? '物料分类消耗对比' : '物料分类消耗'"
                :dataSource="categoryDataList"
                :id="'barMultidEchart03'"
              />
            </a-col>
          </a-row>
          <a-row v-if="mustSystemDataList.length">
            <a-col :span="24">
              <bar-multid
                :fields="mustSystemFieldList"
                :title="compare ? '必换件系统消耗对比' : '必换件系统消耗'"
                :dataSource="mustSystemDataList"
                :id="'barMultidEchart04'"
                :rotate="-26"
              />
            </a-col>
          </a-row>
        </a-col>
      </a-row>
    </div>
    <train-asset-type
      :trainTypeId="trainTypeId"
      :parentId="parentId"
      ref="trainAssetTypeModal"
      :multiple="false"
      :canSelectType="[3, 4]"
      @ok="handleTrainAssetType"
    ></train-asset-type>
  </div>
</template>

<script>
import moment from 'moment'
import BarHorizontal from '@/components/echart/BarHorizontal'
import Pie from '@/components/echart/Pie'
import { getCostCompare, getCostCountSixYear } from '@/api/tirosProductionApi'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import BarMultid from '@/components/echart/BarMultid'
import BarStacked from '@/components/chart/BarStacked'
import { listOrTreeTrainAsset } from '@api/tirosApi'
import { everythingIsEmpty } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import TrainAssetType from '@views/tiros/common/selectModules/TrainAssetType'
import NaSelectDate from '@comp/tiros/Na-select-date'

export default {
  name: 'CostCompare',
  mixins: [JeecgListMixin],
  props: ['data'],
  components: { BarStacked, BarMultid, BarHorizontal, Pie, LineSelectList, TrainAssetType, NaSelectDate },
  data() {
    return {
      moreVisible: false,
      compare: false,
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
          average: false,
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
          average: false,
        },
      },
      dictTrainStr2: '',
      dictTrainStr1: '',
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
        unhandled: null,
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
        trainNo2: [],
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 19 },
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 13 },
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 11 },
      },
      type: '',
      parentId: '',
      assetNameLeft: '',
      assetNameRight: '',
      trainTypeId: '',
      trainTypeIdLeft: '',
      trainTypeIdRight: '',
    }
  },
  created() {
    // this.getYearThead()
    this.compareCostDiff()
  },
  watch: {
    lineId1(newVal, oldVal) {
      if (newVal) {
        this.dictTrainStr1 = 'bu_train_info,train_no,train_no,line_id=' + newVal
      } else {
        this.dictTrainStr1 = ''
      }
    },
    lineId2(newVal, oldVal) {
      if (newVal) {
        this.dictTrainStr2 = 'bu_train_info,train_no,train_no,line_id=' + newVal
      } else {
        this.dictTrainStr2 = ''
      }
    },
  },
  methods: {
    handleSysFocus() {
      if (!this.lineId1) this.$message.warn('请先选择线路!')
    },
    selectQuery1Single() {
      this.queryParam.query1.costItems.splice(this.queryParam.query1.costItems.indexOf('2'), 1)
    },
    selectQuery1Average() {
      this.queryParam.query1.costItems.splice(this.queryParam.query1.costItems.indexOf('1'), 1)
    },
    selectQuery2Single() {
      this.queryParam.query2.costItems.splice(this.queryParam.query1.costItems.indexOf('2'), 1)
    },
    selectQuery2Average() {
      this.queryParam.query2.costItems.splice(this.queryParam.query1.costItems.indexOf('1'), 1)
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
          queryAllChildren: true,
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
          queryAllChildren: true,
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
      // getCostCountSixYear().then((res) => {
      //   if (res.success) {
      //     this.costTrendFieldList = res.result.fieldList
      //     this.costTrendDataList = res.result.dataList
      //   }
      // })
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
      // 是否进行比较分析
      this.queryParam.doCompare = this.compare
    },
    compareCostDiff() {
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
          getCostCompare(this.queryParam).then((res) => {
            if (res.success) {
              if (res.result.systemFieldList.length > 0) {
                this.systemFieldList = res.result.systemFieldList
                this.systemDataList = this.initDatas(res.result.systemDataList)
              } else {
                this.systemFieldList = []
                this.systemDataList = []
              }
              if (res.result.categoryFieldList.length > 0) {
                this.categoryFieldList = res.result.categoryFieldList
                this.categoryDataList = this.initDatas(res.result.categoryDataList)
              } else {
                this.categoryFieldList = []
                this.categoryDataList = []
              }
              if (res.result.mustSystemFieldList.length > 0) {
                this.mustSystemFieldList = res.result.mustSystemFieldList
                this.mustSystemDataList = this.initDatas(res.result.mustSystemDataList)
              } else {
                this.mustSystemFieldList = []
                this.mustSystemDataList = []
              }
              this.handleToggleSearch()
            }
          })
        }
      })
    },
    initDatas(data){
      let list = data;
      if(list.length>1){
        if(this.queryParam.query1.trainNo){
          list[0].type = this.queryParam.query1.trainNo + '车'
        }else if(this.queryParam.query1.lineId){
          list[0].type = this.queryParam.query1.lineId + '号线'
        } 
        if(this.queryParam.query2.trainNo){
          list[1].type = this.queryParam.query2.trainNo + '车'
        }else if(this.queryParam.query2.lineId){
          list[1].type = this.queryParam.query2.lineId + '号线'
        } 
      }
      return list
    },
    // formatEcharData(data, aliases){
    //   return data.map((e, index) =>{
    //     aliases.forEach(aliase => {
    //       if (!e.hasOwnProperty(aliase)) {
    //         e[aliase] = 0
    //       }
    //     });
    //     return e
    //   })
    // },
    handleReset() {
      this.assetNameLeft = ''
      this.assetNameRight = ''
      this.queryParam.query1 = {
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
        average: false,
      }
      this.queryParam.query2 = {
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
        average: false,
      }
      this.year1 = null
      this.year2 = null
      this.lineId1 = ''
      this.lineId2 = ''
      if (this.$refs.naDate1) {
        this.$refs.naDate1.reset()
      }
      if (this.$refs.naDate2) {
        this.$refs.naDate2.reset()
      }
    },
    // 弹出日历和关闭日历的回调
    handleOpenChange1(status) {
      this.yearPickShow1 = status
    },
    // 得到年份选择器的值
    handlePanelChange1(value) {
      this.year1 = value
      this.queryParam.query1.year = moment(value).format('YYYY')
      this.yearPickShow1 = false
    },
    // 弹出日历和关闭日历的回调
    handleOpenChange2(status) {
      this.yearPickShow2 = status
    },
    // 得到年份选择器的值
    handlePanelChange2(value) {
      this.year2 = value
      this.queryParam.query2.year = moment(value).format('YYYY')
      this.yearPickShow2 = false
    },
    dateChange1(value) {
      if (value.dateType) {
        this.queryParam.query1.dateType = value.dateType
        this.queryParam.query1.year = value.year
        this.queryParam.query1.quarter = value.quarter
        this.queryParam.query1.month = value.month
        this.queryParam.query1.startDate = value.dateRef[0]
        this.queryParam.query1.endDate = value.dateRef[1]
      } else {
        this.queryParam.query1.dateType = null
        this.queryParam.query1.year = null
        this.queryParam.query1.quarter = null
        this.queryParam.query1.month = null
        this.queryParam.query1.startDate = null
        this.queryParam.query1.endDate = null
      }
    },
    dateChange2(value) {
      if (value.dateType) {
        this.queryParam.query2.dateType = value.dateType
        this.queryParam.query2.year = value.year
        this.queryParam.query2.quarter = value.quarter
        this.queryParam.query2.month = value.month
        this.queryParam.query2.startDate = value.dateRef[0]
        this.queryParam.query2.endDate = value.dateRef[1]
      } else {
        this.queryParam.query2.dateType = null
        this.queryParam.query2.year = null
        this.queryParam.query2.quarter = null
        this.queryParam.query2.month = null
        this.queryParam.query2.startDate = null
        this.queryParam.query2.endDate = null
      }
    },
    showTrainAssetType(type) {
      this.type = type
      if (type == 1) {
        if (everythingIsEmpty(this.queryParam.query1.sysId)) {
          this.$message.warn('请先选择系统!')
          return
        }
        this.trainTypeId = this.trainTypeIdLeft
        this.parentId = this.queryParam.query1.sysId
        this.$nextTick(() => {
          this.$refs.trainAssetTypeModal.showModal()
          this.$refs.trainAssetTypeSelectLeft.blur()
        })
      } else {
        if (everythingIsEmpty(this.queryParam.query2.sysId)) {
          this.$message.warn('请先选择系统!')
          return
        }
        this.trainTypeId = this.trainTypeIdRight
        this.parentId = this.queryParam.query2.sysId
        this.$nextTick(() => {
          this.$refs.trainAssetTypeModal.showModal()
          this.$refs.trainAssetTypeSelectRight.blur()
        })
      }
    },
    handleTrainAssetType(data) {
      if (!everythingIsEmpty(data)) {
        if (this.type == 1) {
          this.queryParam.query1.assetId = data[0].id
          this.assetNameLeft = data[0].name
        } else {
          this.queryParam.query2.assetId = data[0].id
          this.assetNameRight = data[0].name
        }
      }
    },
    handleLineLeft(e, optionData) {
      if (optionData) this.trainTypeIdLeft = optionData.trainTypeId
    },
    handleLineRight(e, optionData) {
      if (optionData) this.trainTypeIdRight = optionData.trainTypeId
    },
  },
  computed: {
    title() {
      return this.depotName || ''
    },
  },
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