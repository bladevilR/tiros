<template>
  <div id="errorMonitorContent">
    <div class="compareContent">
      <div class="openModalDiv" @click="handleToggleSearch">比较分析条件设置</div>
      <div class="compareSetDiv table-page-search-wrapper" scrolling="auto" v-if="toggleSearchStatus">
        <a-row>
          <a-col :span="12" style="border-right:1px dashed #aaa">
            <a-form :form="formLeft">
              <a-row :gutter="12">
                <a-col :span="16">
                  <a-row>
                    <a-col :span="12">
                      <a-form-item :labelCol="{ span: 10 }" :wrapperCol="{ span: 14 }" label="车辆段">
                        <j-dict-select-tag
                          v-model="query1.depotId"
                          placeholder="请选择"
                          dictCode="bu_mtr_depot,name,id"
                        />
                      </a-form-item>
                    </a-col>
                    <a-col :span="12">
                      <a-form-item :labelCol="{ span: 10 }" :wrapperCol="{ span: 14 }" label="线路">
                        <line-select-list v-model="lineId1" :trigger-change="true"
                                          @change="handleLineLeft"></line-select-list>
                      </a-form-item>
                    </a-col>
                  </a-row>
                  <a-row>
                    <a-col :span="24">
                      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工班">
                        <j-dict-select-tag
                          v-model="query1.groupId"
                          dictCode="sys_depart,depart_name,id,org_category=4"
                          placeholder="请选择"
                        />
                      </a-form-item>
                    </a-col>
                  </a-row>
                  <a-row>
                    <a-col :span="24">
                      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆">
                        <j-dict-select-seach-tag
                          v-model="query1.trainNo"
                          :dictCode="dictTrainStr1"
                        />
                      </a-form-item>
                    </a-col>
                  </a-row>
                  <a-row>
                    <a-col :span="24">
                      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="系统">
                        <j-dict-select-tag
                          :triggerChange="true"
                          v-model="query1.sysId"
                          :dictCode="dictSysIdStr1"
                          placeholder="请选择"
                          @focus="handleFocusAssetTypeLeft"
                        />
                      </a-form-item>
                    </a-col>
                  </a-row>
                  <a-row>
                    <a-col :span="24">
                      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件">
<!--                        <a-select v-if="query1.sysId" v-model="query1.assetId" allowClear>
                          <a-select-option v-for="item in assetTypePart1" :key="item.id" :value="item.id">
                            {{ item.assetName }}
                          </a-select-option>
                        </a-select>
                        <j-dict-select-tag v-else
                                           v-model="query1.assetId"
                                           placeholder="请选择"
                                           :dictCode="dicTrainAssetLeft"
                                           @focus="handleFocusSysIdLeft"
                        />-->
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
                  </a-row>
                  <a-row :gutter="24">
                    <a-col :md="10">
                      <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="时间">
                        <j-dict-select-tag
                          v-model="query1.dateType"
                          placeholder="请选择"
                          dictCode="bu_date_type"
                          :triggerChange="true"
                          @change="handleResetTimeQuery1"
                        />
                      </a-form-item>
                    </a-col>
                    <a-col :md="8" v-if="(query1.dateType==='1'||query1.dateType==='2')">
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
                    <a-col :md="6" v-if="query1.dateType==='2'">
                      <a-form-item>
                        <j-dict-select-tag
                          v-model="query1.quarter"
                          placeholder="请选择"
                          dictCode="bu_quarter_type"
                        />
                      </a-form-item>
                    </a-col>
                    <a-col :md="14" v-if="query1.dateType==='3'">
                      <a-form-item>
                        <a-month-picker :style="{width: '100%'}" placeholder="选择月份" v-model="month1">
                        </a-month-picker>
                      </a-form-item>
                    </a-col>
                    <a-col :md="14" v-if="query1.dateType==='4'">
                      <a-form-item>
                        <a-range-picker v-model="rangeDate1" :defaultPickerValue="defaultDateRange" >
                        </a-range-picker>
                      </a-form-item>
                    </a-col>
                  </a-row>
                </a-col>
                <a-col :span="8">
                  <p style="font-weight: bold;font-size: 15px;margin-top: 10px;float: left;margin-left: 38px">故障种类</p>
                  <a-form-item>
                    <a-checkbox-group
                      v-model="faultTypes1"
                      style="width: 68%;"
                    >
                      <a-row>
                        <a-col :style="{float:'left',marginBottom:'5px'}">
                          <a-checkbox value="1">
                            架修期故障
                          </a-checkbox>
                        </a-col>
                      </a-row>
                      <a-row>
                        <a-col :style="{float:'left',marginBottom:'5px'}">
                          <a-checkbox value="2">
                            架修期A/B故障
                          </a-checkbox>
                        </a-col>
                      </a-row>
                      <a-row>
                        <a-col :style="{float:'left',marginBottom:'5px'}">
                          <a-checkbox value="3">
                            质保期故障
                          </a-checkbox>
                        </a-col>
                      </a-row>
                      <a-row>
                        <a-col :style="{float:'left',marginBottom:'5px'}">
                          <a-checkbox value="4">
                            质保期A/B故障
                          </a-checkbox>
                        </a-col>
                      </a-row>
                      <a-row>
                        <a-col :style="{float:'left',marginBottom:'5px'}">
                          <a-checkbox value="5">
                            质保期正线故障
                          </a-checkbox>
                        </a-col>
                      </a-row>
                      <a-row>
                        <a-col :style="{float:'left',marginBottom:'5px'}">
                          <a-checkbox value="6">
                            质保期正线有责故障
                          </a-checkbox>
                        </a-col>
                      </a-row>
                    </a-checkbox-group>
                  </a-form-item>
                </a-col>
              </a-row>
            </a-form>
          </a-col>
          <a-col :span="12">
            <a-form :form="formRight">
              <a-row :gutter="12">
                <a-col :span="16">
                  <a-row>
                    <a-col :span="12">
                      <a-form-item :labelCol="{ span: 10 }" :wrapperCol="{ span: 14 }" label="车辆段">
                        <j-dict-select-tag
                          v-model="query2.depotId"
                          placeholder="请选择"
                          dictCode="bu_mtr_depot,name,id"
                        />
                      </a-form-item>
                    </a-col>
                    <a-col :span="12">
                      <a-form-item :labelCol="{ span: 10 }" :wrapperCol="{ span: 14 }" label="线路">
                        <line-select-list v-model="lineId2" :trigger-change="true"
                                          @change="handleLineRight"></line-select-list>
                      </a-form-item>
                    </a-col>
                  </a-row>
                  <a-row>
                    <a-col :span="24">
                      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工班">
                        <j-dict-select-tag
                          v-model="query2.groupId"
                          dictCode="sys_depart,depart_name,id,org_category=4"
                          placeholder="请选择"
                        />
                      </a-form-item>
                    </a-col>
                  </a-row>
                  <a-row>
                    <a-col :span="24">
                      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆">
                        <j-dict-select-seach-tag
                          v-model="query2.trainNo"
                          :dictCode="dictTrainStr2"
                        />
                      </a-form-item>
                    </a-col>
                  </a-row>
                  <a-row>
                    <a-col :span="24">
                      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="系统">
                        <j-dict-select-tag
                          :triggerChange="true"
                          v-model="query2.sysId"
                          :dictCode="dictSysIdStr2"
                          placeholder="请选择"
                          @focus="handleFocusAssetTypeRight"
                        />
                      </a-form-item>
                    </a-col>
                  </a-row>
                  <a-row>
                    <a-col :span="24">
                      <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件">
<!--                        <a-select v-if="query2.sysId" v-model="query2.assetId" allowClear>
                          <a-select-option v-for="item in assetTypePart2" :key="item.id" :value="item.id">
                            {{ item.assetName }}
                          </a-select-option>
                        </a-select>
                        <j-dict-select-tag v-else
                                           v-model="query2.assetId"
                                           placeholder="请选择"
                                           :dictCode="dicTrainAssetRight"
                                           @focus="handleFocusSysIdRight"
                        />-->
                        <a-select
                          placeholder="请选择"
                          v-model="assetNameRight"
                          :open="false"
                          :showArrow="true"
                          @focus="showTrainAssetType(2)"
                          ref="trainAssetTypeSelectRight">
                          <a-icon slot="suffixIcon" type="ellipsis" />
                        </a-select>
                      </a-form-item>
                    </a-col>
                  </a-row>
                  <a-row :gutter="24">
                    <a-col :md="10">
                      <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="时间">
                        <j-dict-select-tag
                          v-model="query2.dateType"
                          placeholder="请选择"
                          dictCode="bu_date_type"
                          :triggerChange="true"
                          @change="handleResetTimeQuery2"
                        />
                      </a-form-item>
                    </a-col>
                    <a-col :md="8" v-if="(query2.dateType==='1'||query2.dateType==='2')">
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
                    <a-col :md="6" v-if="query2.dateType==='2'">
                      <a-form-item>
                        <j-dict-select-tag
                          v-model="query2.quarter"
                          placeholder="请选择"
                          dictCode="bu_quarter_type"
                        />
                      </a-form-item>
                    </a-col>
                    <a-col :md="14" v-if="query2.dateType==='3'">
                      <a-form-item>
                        <a-month-picker :style="{width: '100%'}" placeholder="选择月份" v-model="month2">
                        </a-month-picker>
                      </a-form-item>
                    </a-col>
                    <a-col :md="14" v-if="query2.dateType==='4'">
                      <a-form-item>
                        <a-range-picker v-model="rangeDate2" :defaultPickerValue="defaultDateRange" >
                        </a-range-picker>
                      </a-form-item>
                    </a-col>
                  </a-row>
                </a-col>
                <a-col :span="8">
                  <p style="font-weight: bold;font-size: 15px;margin-top: 10px;float: left;margin-left: 38px">故障种类</p>
                  <a-form-item>
                    <a-checkbox-group
                      v-model="faultTypes2"
                      style="width: 68%;"
                    >
                      <a-row>
                        <a-col :style="{float:'left',marginBottom:'5px'}">
                          <a-checkbox value="1">
                            架修期故障
                          </a-checkbox>
                        </a-col>
                      </a-row>
                      <a-row>
                        <a-col :style="{float:'left',marginBottom:'5px'}">
                          <a-checkbox value="2">
                            架修期A/B故障
                          </a-checkbox>
                        </a-col>
                      </a-row>
                      <a-row>
                        <a-col :style="{float:'left',marginBottom:'5px'}">
                          <a-checkbox value="3">
                            质保期故障
                          </a-checkbox>
                        </a-col>
                      </a-row>
                      <a-row>
                        <a-col :style="{float:'left',marginBottom:'5px'}">
                          <a-checkbox value="4">
                            质保期A/B故障
                          </a-checkbox>
                        </a-col>
                      </a-row>
                      <a-row>
                        <a-col :style="{float:'left',marginBottom:'5px'}">
                          <a-checkbox value="5">
                            质保期正线故障
                          </a-checkbox>
                        </a-col>
                      </a-row>
                      <a-row>
                        <a-col :style="{float:'left',marginBottom:'5px'}">
                          <a-checkbox value="6">
                            质保期正线有责故障
                          </a-checkbox>
                        </a-col>
                      </a-row>
                    </a-checkbox-group>
                  </a-form-item>
                </a-col>
              </a-row>
            </a-form>
          </a-col>
        </a-row>
        <a-button type="primary" @click="compareFaultDiff">对比分析</a-button>
        <a-button style="margin-left: 8px" @click="handleReset">清空条件</a-button>
      </div>

    </div>
    <div class="chartsDiv">
      <a-row :gutter="12">
        <a-col :span="12" style="border-right:1px dashed #aaa">
          <a-row v-if="jxList.length">
            <a-col :span="24">
              <bar-horizontal title="架修期系统故障分布" :dataSource="leftBarDataSource1" />
            </a-col>
            <a-col :span="24">
              <pie title="架修期系统故障占比" :dataSource="leftPieDataSource1" />
            </a-col>
          </a-row>
          <a-row v-if="zbList.length">
            <a-col :span="24">
              <bar-horizontal title="质保期系统故障分布" :dataSource="leftBarDataSource2" />
            </a-col>
            <a-col :span="24">
              <pie title="质保期系统故障占比" :dataSource="leftPieDataSource2" />
            </a-col>
          </a-row>
          <a-row v-if="cbList.length">
            <a-col :span="24">
              <bar-horizontal title="出保期系统故障分布" :dataSource="leftBarDataSource3" />
            </a-col>
            <a-col :span="24">
              <pie title="出保期系统故障占比" :dataSource="leftPieDataSource3" />
            </a-col>
          </a-row>
        </a-col>
        <a-col :span="12" style="border-right:1px dashed #aaa">
          <a-row v-if="jxListR.length">
            <a-col :span="24">
              <bar-horizontal title="架修期系统故障分布" :dataSource="rightBarDataSource1" />
            </a-col>
            <a-col :span="24">
              <pie title="架修期系统故障占比" :dataSource="rightPieDataSource1" />
            </a-col>
          </a-row>
          <a-row v-if="zbListR.length">
            <a-col :span="24">
              <bar-horizontal title="质保期系统故障分布" :dataSource="rightBarDataSource2" />
            </a-col>
            <a-col :span="24">
              <pie title="质保期系统故障占比" :dataSource="rightPieDataSource2" />
            </a-col>
          </a-row>
          <a-row v-if="cbListR.length">
            <a-col :span="24">
              <bar-horizontal title="出保期系统故障分布" :dataSource="rightBarDataSource3" />
            </a-col>
            <a-col :span="24">
              <pie title="出保期系统故障占比" :dataSource="rightPieDataSource3" />
            </a-col>
          </a-row>
        </a-col>
      </a-row>
    </div>
    <train-asset-type :trainTypeId="trainTypeId" :parentId="parentId" ref="trainAssetTypeModal" :multiple="false"
                      :canSelectType="[3,4]" @ok="handleTrainAssetType"></train-asset-type>
  </div>
</template>

<script>
import moment from 'moment'
import BarHorizontal from '@/components/chart/BarHorizontal'
import Pie from '@/components/chart/Pie'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { everythingIsEmpty } from '@/utils/util'
import { setcountFaultPAS } from '@api/tirosOutsourceApi'
import TrainAssetType from '@views/tiros/common/selectModules/TrainAssetType'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'compareOutModal',
  mixins: [JeecgListMixin],
  components: { BarHorizontal, Pie ,LineSelectList,TrainAssetType},
  data () {
    return {
      dictTrainStr2: 'bu_train_info,train_no,train_no,status=1',
      dictTrainStr1: 'bu_train_info,train_no,train_no,status=1',
      dictSysIdStr1: '',
      dictSysIdStr2: '',
      assetTypePart1: [],
      assetTypePart2: [],
      trainTypeId: '',
      parentId: '',
      queryParam: {
        query1: {},
        query2: {}
      },
      query1: {
        depotId: '',
        lineId: '',
        trainNo: '',
        groupId: '',
        sysId: '',
        assetId: '',
        faultTypes: [],
        dateType: '',
        year: '',
        quarter: '',
        month: '',
        startDate: '',
        endDate: ''
      },
      query2: {
        depotId: '',
        lineId: '',
        trainNo: '',
        groupId: '',
        sysId: '',
        assetId: '',
        faultTypes: [],
        dateType: '',
        year: '',
        quarter: '',
        month: '',
        startDate: '',
        endDate: ''
      },
      yearPick1: null, //年选择器的值
      yearPickShow1: false, //年选择器的显示隐藏
      yearPick2: null, //年选择器的值
      yearPickShow2: false, //年选择器的显示隐藏
      year1: null,
      year2: null,
      url: {},
      lineId1: '',
      lineId2: '',
      totalResult: 0,
      tableData1: [],
      tableData2: [],
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
      jxList: [],
      zbList: [],
      cbList: [],
      jxListR: [],
      zbListR: [],
      cbListR: [],
      formLeft: this.$form.createForm(this),
      formRight: this.$form.createForm(this),
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
      },
      dicTrainAssetRight: 'bu_train_asset,asset_name,id,(struct_type!=1 or struct_type!=2)',
      dicTrainAssetLeft: 'bu_train_asset,asset_name,id,(struct_type!=1 or struct_type!=2)',
      month1: null,
      month2: null,
      faultTypes1: [],
      faultTypes2: [],
      type: '',
      assetNameLeft:'',
      assetNameRight:'',
    }
  },
  created () {
    this.compareFaultDiff()
  },
  watch: {
    lineId1 (newVal, oldVal) {
      if (newVal) {
        this.dictTrainStr1 = 'bu_train_info,train_no,train_no,line_id=' + newVal
        this.dictSysIdStr1 = 'bu_train_asset_type,name,id,struct_type=1 and parent_id is null and train_type_id=(select train_type_id from ' +
          'bu_mtr_line where line_id=\'' + newVal + '\')'
      } else {
        this.dictTrainStr1 = ''
        this.dictSysIdStr1 = ''
      }
    },
    lineId2 (newVal, oldVal) {
      if (newVal) {
        this.dictTrainStr2 = 'bu_train_info,train_no,train_no,line_id=' + newVal
        this.dictSysIdStr2 = 'bu_train_asset_type,name,id,struct_type=1 and parent_id is null and train_type_id=(select train_type_id from ' +
          'bu_mtr_line where line_id=\'' + newVal + '\')'
      } else {
        this.dictTrainStr2 = ''
        this.dictSysIdStr2 = ''
      }
    }
  },
  methods: {
    showTrainAssetType (type) {
      this.type = type
      if (type == 1) {
        if (everythingIsEmpty(this.query1.sysId)) {
          this.$message.warn('请先选择系统!')
          return
        }
        this.trainTypeId = this.trainTypeIdLeft
        this.parentId = this.query1.sysId
        this.$nextTick(() => {
          this.$refs.trainAssetTypeModal.showModal()
          this.$refs.trainAssetTypeSelectLeft.blur()
        })
      } else {
        if (everythingIsEmpty(this.query2.sysId)) {
          this.$message.warn('请先选择系统!')
          return
        }
        this.trainTypeId = this.trainTypeIdRight
        this.parentId = this.query2.sysId
        this.$nextTick(() => {
          this.$refs.trainAssetTypeModal.showModal()
          this.$refs.trainAssetTypeSelectRight.blur()
        })
      }

    },
    handleTrainAssetType (data) {
      if (!everythingIsEmpty(data)) {
        if (this.type == 1) {
          this.query1.assetId = data[0].id
          this.assetNameLeft = data[0].name
        } else {
          this.query2.assetId = data[0].id
          this.assetNameRight = data[0].name
        }
      }
    },
    handleLineLeft (e, optionData) {
      if (optionData) this.trainTypeIdLeft = optionData.trainTypeId
    },
    handleLineRight (e, optionData) {
      if (optionData) this.trainTypeIdRight = optionData.trainTypeId
    },
    handleFocusAssetTypeLeft () {
      if (this.query1.assetId) this.query1.assetId = ''
      if (everythingIsEmpty(this.lineId1)) {
        this.$message.warn('请先选择线路!')
      }
    },
   /* handleFocusSysIdLeft () {
      let str = ''
      if (this.query1.trainNo) {
        str = str + ' and train_id=(select id from bu_train_info where train_no=\'' + this.query1.trainNo + '\')'
      }
      if (this.lineId1) {
        str = str + 'and line_id=\'' + this.lineId1 + '\''
      }
      this.dicTrainAssetLeft = 'bu_train_asset,asset_name,id,(struct_type!=1 or struct_type!=2)' + str
    },*/
    handleFocusAssetTypeRight () {
      if (this.query2.assetId) this.query2.assetId = ''
      if (everythingIsEmpty(this.lineId2)) {
        this.$message.warn('请先选择线路!')
      }
    },
   /* handleFocusSysIdRight () {
      let str = ''
      if (this.query2.trainNo) {
        str = str + ' and train_id=(select id from bu_train_info where train_no=\'' + this.query2.trainNo + '\')'
      }
      if (this.lineId2) {
        str = str + 'and line_id=\'' + this.lineId2 + '\''
      }
      this.dicTrainAssetRight = 'bu_train_asset,asset_name,id,(struct_type!=1 or struct_type!=2)' + str
    },
    handleDicTrainAssetLeft (data) {
      if (data) {
        /!*  this.dicTrainAssetLeft='bu_train_asset,asset_name,id,train_id = ' +
            '(select id from bu_train_info where train_no =\'' + this.query1.trainNo + '\') or ' +
            '(asset_type_id = \'' + this.query1.sysId + '\' or  asset_type_id in (' +
            'select id from bu_train_asset_type where struct_type=2 and parent_id= \'' + this.query1.sysId + '\') )'*!/
        let assetQueryParam = {
          assetTypeId: this.query1.sysId,
          trainNo: this.query1.trainNo,
          queryAllChildren: true
        }
        listOrTreeTrainAsset(assetQueryParam).then((res) => {
          this.assetTypePart1 = res.result
        })
      } else {
        this.assetTypePart1 = []
        this.query1.assetId = ''
        // this.dicTrainAssetLeft = 'bu_train_asset,asset_name,id'
      }
    },
    handleDicTrainAssetRight (data) {
      if (data) {
        /!* this.dicTrainAssetRight='bu_train_asset,asset_name,id,train_id = ' +
           '(select id from bu_train_info where train_no =\'' + this.query2.trainNo + '\') or ' +
           '(asset_type_id = \'' + this.query2.sysId + '\' or  asset_type_id in (' +
           'select id from bu_train_asset_type where struct_type=2 and parent_id= \'' + this.query2.sysId + '\') )'*!/
        let assetQueryParam = {
          assetTypeId: this.query2.sysId,
          trainNo: this.query2.trainNo,
          queryAllChildren: true
        }
        listOrTreeTrainAsset(assetQueryParam).then((res) => {
          this.assetTypePart2 = res.result
        })
      } else {
        this.assetTypePart2 = []
        this.query2.assetId = ''
        //this.dicTrainAssetRight = 'bu_train_asset,asset_name,id'
      }
    },*/
    compareFaultDiff () {
      this.handleToggleSearch()
      this.jxList = []
      this.zbList = []
      this.cbList = []
      this.jxListR = []
      this.zbListR = []
      this.cbListR = []
      if (this.rangeDate1.length > 0 && this.query1.dateType === '4') {
        this.query1.startDate = moment(this.rangeDate1[0]).format('YYYY-MM-DD')
        this.query1.endDate = moment(this.rangeDate1[1]).format('YYYY-MM-DD')
      }
      if (this.month1 && this.query1.dateType === '3') {
        this.query1.year = moment(this.month1).year()
        this.query1.month = moment(this.month1).month() + 1
      }
      this.query1.lineId = this.lineId1
      if (this.rangeDate2.length > 0 && this.query2.dateType === '4') {
        this.query2.startDate = moment(this.rangeDate2[0]).format('YYYY-MM-DD')
        this.query2.endDate = moment(this.rangeDate2[1]).format('YYYY-MM-DD')
      }
      if (this.month1 && this.query2.dateType === '3') {
        this.query2.year = moment(this.month2).year()
        this.query2.month = moment(this.month2).month() + 1
      }
      this.query2.lineId = this.lineId2
      if (this.faultTypes1) {
        this.query1.faultTypes = this.faultTypes1.map(Number)
      }
      if (this.faultTypes2) {
        this.query2.faultTypes = this.faultTypes2.map(Number)
      }
      this.queryParam.query1 = this.query1
      this.queryParam.query2 = this.query2
      setcountFaultPAS(this.queryParam).then(res => {
        if (res.success) {
          this.tableData1 = res.result.result1
          this.tableData2 = res.result.result2
          for (var i = 0; i < this.tableData1.length; i++) {
            if (this.tableData1[i].phase == 1) {
              this.jxList = this.tableData1[i].systemItemList
            } else if (this.tableData1[i].phase == 2) {
              this.zbList = this.tableData1[i].systemItemList
            } else if (this.tableData1[i].phase == 3) {
              this.cbList = this.tableData1[i].systemItemList
            }
          }
          for (var i = 0; i < this.tableData2.length; i++) {
            if (this.tableData2[i].phase == 1) {
              this.jxListR = this.tableData2[i].systemItemList
            } else if (this.tableData2[i].phase == 2) {
              this.zbListR = this.tableData2[i].systemItemList
            } else if (this.tableData2[i].phase == 3) {
              this.cbListR = this.tableData2[i].systemItemList
            }
          }
        }
      })
    },
    handleResetTimeQuery1 () {
      console.log('清空时间参数query1')
      console.log('this.query1 = ' + this.query1)
      console.log('this.query1 = ' + JSON.stringify(this.query1))
      this.query1.year = ''
      this.query1.quarter = ''
      this.query1.month = ''
      this.query1.startDate = ''
      this.query1.endDate = ''
    },
    handleResetTimeQuery2 () {
      console.log('清空时间参数query2')
      console.log('this.query2 = ' + this.query2)
      console.log('this.query2 = ' + JSON.stringify(this.query1))
      this.query2.year = ''
      this.query2.quarter = ''
      this.query2.month = ''
      this.query2.startDate = ''
      this.query2.endDate = ''
    },
    handleReset () {
      this.query1 = {}
      this.query2 = {}
      this.year1 = null
      this.year2 = null
      this.lineId1 = ''
      this.lineId2 = ''
      this.month1 = null
      this.month2 = null
      this.faultTypes1 = []
      this.faultTypes2 = []
      this.assetNameRight=''
      this.assetNameLeft=''
    },
    // 弹出日历和关闭日历的回调
    handleOpenChange1 (
      status) {
      this.yearPickShow1 = status
    },
    // 得到年份选择器的值
    handlePanelChange1 (value) {
      this.year1 = value
      this.query1.year = moment(value).format('YYYY')
      this.yearPickShow1 = false
    },
    // 弹出日历和关闭日历的回调
    handleOpenChange2 (
      status) {
      this.yearPickShow2 = status
    },
    // 得到年份选择器的值
    handlePanelChange2 (value) {
      this.year2 = value
      this.query2.year = moment(value).format('YYYY')
      this.yearPickShow2 = false
    }
  },
  computed: {
    title () {
      return this.depotName || ''
    },
    leftBarDataSource1 () {
      return this.jxList.map(item => {
        return {
          item: item.systemName,
          count: item.faultCount
        }
      })
    },
    leftPieDataSource1 () {
      return this.jxList.map(item => {
        return {
          item: item.systemName,
          // percent: Math.round((item.faultCount / this.totalCount) * 10000) / 100
          count: item.faultCount
        }
      })
    },
    leftBarDataSource2 () {
      return this.zbList.map(item => {
        return {
          item: item.systemName,
          count: item.faultCount
        }
      })
    },
    leftPieDataSource2 () {
      return this.zbList.map(item => {
        return {
          item: item.systemName,
          // percent: Math.round((item.faultCount / this.totalCount) * 10000) / 100
          count: item.faultCount
        }
      })
    },
    leftBarDataSource3 () {
      return this.cbList.map(item => {
        return {
          item: item.systemName,
          count: item.faultCount
        }
      })
    },
    leftPieDataSource3 () {
      return this.cbList.map(item => {
        return {
          item: item.systemName,
          // percent: Math.round((item.faultCount / this.totalCount) * 10000) / 100
          count: item.faultCount
        }
      })
    },
    rightBarDataSource1 () {
      return this.jxListR.map(item => {
        return {
          item: item.systemName,
          count: item.faultCount
        }
      })
    },
    rightPieDataSource1 () {
      return this.jxListR.map(item => {
        return {
          item: item.systemName,
          // percent: Math.round((item.faultCount / this.totalCount) * 10000) / 100
          count: item.faultCount
        }
      })
    },
    rightBarDataSource2 () {
      return this.zbListR.map(item => {
        return {
          item: item.systemName,
          count: item.faultCount
        }
      })
    },
    rightPieDataSource2 () {
      return this.zbListR.map(item => {
        return {
          item: item.systemName,
          // percent: Math.round((item.faultCount / this.totalCount) * 10000) / 100
          count: item.faultCount
        }
      })
    },
    rightBarDataSource3 () {
      return this.cbListR.map(item => {
        return {
          item: item.systemName,
          count: item.faultCount
        }
      })
    },
    rightPieDataSource3 () {
      return this.cbListR.map(item => {
        return {
          item: item.systemName,
          // percent: Math.round((item.faultCount / this.totalCount) * 10000) / 100
          count: item.faultCount
        }
      })
    }
  }
}
</script>

<style lang="less">
#errorMonitorContent {
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
