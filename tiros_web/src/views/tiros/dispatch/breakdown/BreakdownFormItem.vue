<template>
  <div style='height: calc(100% - 0px); overflow-y: auto'>
    <a-form-model ref='form' :model='faultModel' :rules='formRules' style='margin-top: 10px'>
      <div class='info-wrapper info-top-wrapper'>
        <h4>故障信息</h4>
        <a-row>
          <!-- <a-col :span="8">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="故障编码">
              <a-input v-model="faultModel.faultSn" placeholder="故障编码自动生成" :disabled="true" />
            </a-form-model-item>
          </a-col> -->
          <a-col :span='8'>
            <!-- v-decorator="['supplierName', validatorRules.supplierId]" -->
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='列计划' prop='planName'>
              <div @click='openPlanModel'>
                <a-select
                  allow-clear
                  placeholder='请选择列计划'
                  :open='false'
                  :showArrow='true'
                  :triggerChange='true'
                  v-model='faultModel.planName'
                  ref='myPlanSelect'
                  @change='changePlanSelect'
                >
                  <a-icon slot='suffixIcon' type='ellipsis' />
                </a-select>
              </div>
            </a-form-model-item>
          </a-col>


          <a-col :span='8'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='线路' prop='lineId'>
              <j-dict-select-tag
                :triggerChange='true'
                v-model='faultModel.lineId'
                dictCode='bu_mtr_line,line_name,line_id'
                @change='changeLine'
              />
            </a-form-model-item>
          </a-col>
          <a-col :span='8'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='车辆' prop='trainNo'>
              <j-dict-select-seach-tag
                :triggerChange='true'
                v-model='faultModel.trainNo'
                :dictCode='dictTrainStr'
                @focus='handleSysFocus'
                @change='handleTrainNo'
              />
            </a-form-model-item>
          </a-col>
          <a-col :span='8'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='发现阶段' prop='phase'>
              <j-dict-select-tag :triggerChange='true' v-model='faultModel.phase' dictCode='bu_fault_phase' />
            </a-form-model-item>
          </a-col>

          <a-col :span='8'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='发现时间' prop='happenTime'>
              <a-date-picker
                format='YYYY-MM-DD HH:mm'
                @change='
                  faultModel.reportTime = undefined;
                  handleModel.solutionTime = undefined;
                  faultModel.solutionTime = undefined;
                  reportTimeKey++;
                  solutionTimeKey++;
                '
                :show-time="{ format: 'HH:mm', hideDisabledOptions: true }"
                v-model='faultModel.happenTime'
                style='width: 100%'
              />
            </a-form-model-item>
          </a-col>
          <a-col :span='8'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='故障工位' prop='workStationName'>
              <!-- <j-dict-select-tag
                :triggerChange='true'
                v-model='faultModel.workStationId'
                dictCode='bu_fault_level'
              /> -->
              <!-- <a-select v-model="faultModel.workStationId">
                <a-select-option v-for="(val, index) in workStationsList" :key="index" :value="val.workstationId">{{
                  val.workstationName
                }}</a-select-option>
              </a-select> -->
              <a-select
                v-model='faultModel.workStationName'
                placeholder='请选择故障工位'
                :open='false'
                :showArrow='true'
                @focus='openSelectTypeModal(5)'
                ref='workStationSelect'
              >
                <div slot='suffixIcon'>
                  <a-icon
                    v-if='faultModel.workStationName'
                    @click.stop='clearValue(5)'
                    type='close-circle'
                    style='padding-right: 3px'
                  />
                  <a-icon type='ellipsis' />
                </div>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span='8'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='系统' prop='sysId'>
              <j-dict-select-tag
                :triggerChange='true'
                v-model='faultModel.sysId'
                :dict-code='dictTrainSys'
                @change='changeLinesubsys'
                @focus='handleSysFocus'
              />
            </a-form-model-item>
          </a-col>

          <!--            <a-col :span="8">
                        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="子系统" prop="subSysId">
                          <j-dict-select-tag
                            :triggerChange="true"
                            v-model="faultModel.subSysId"
                            :dictCode="dictCodesubsys"
                            @change="changeAsset"
                            @focus="handleSubSysFocus"
                          />
                        </a-form-model-item>
                      </a-col>-->
          <a-col :span='8'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='故障部件' prop='faultAssetName'>
              <!--                <a-select v-model="faultModel.faultAssetId" allowClear @focus="handleAssetFocus">
                                <a-select-option v-for="item in assetTypeData" :key="item.id" :value="item.id">
                                  {{item.assetName}}
                                </a-select-option>
                              </a-select>-->
              <a-select
                placeholder='请选择'
                v-model='faultModel.faultAssetName'
                :open='false'
                :showArrow='true'
                @focus='showAssetModal'
                ref='faultAssetSelect'
              >
                <a-icon slot='suffixIcon' type='ellipsis' />
              </a-select>
            </a-form-model-item>
          </a-col>

          <a-col :span='24'>
            <a-form-model-item :labelCol='labelCol2' :wrapperCol='wrapperCol2' label='故障描述' prop='faultDesc'>
              <a-textarea placeholder='请输入内容' v-model='faultModel.faultDesc' />
            </a-form-model-item>
          </a-col>

          <a-col :span='8'>
            <a-form-model-item :labelCol='{ span: 6 }' :wrapperCol='{ span: 18 }' label='其他属性'>
              <a-row>
                <a-col :span='24 / 3'> 是否正线:
                  <a-switch v-model='faultModel.faultOnline' />
                </a-col>
                <!--                <a-col :span='24 / 3'> 是否有责:-->
                <!--                  <a-switch v-model='faultModel.hasDuty' />-->
                <!--                </a-col>-->
                <a-col :span='24 / 3'> 是否委外:
                  <a-switch v-model='faultModel.outsource' />
                </a-col>
              </a-row>
            </a-form-model-item>
          </a-col>

          <a-col :span='8' v-if='faultModel.outsource'>
            <!-- v-decorator="['supplierName', validatorRules.supplierId]" -->
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='所属厂商' prop='outSupplierName'>
              <a-select
                v-model='faultModel.outSupplierName'
                placeholder='请选择厂商'
                :triggerChange='true'
                :open='false'
                :showArrow='true'
                @focus='openSupplierModal'
                ref='mySupplierSelect'
              >
                <a-icon slot='suffixIcon' type='ellipsis' />
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
      </div>
      <div class='info-wrapper info-top-wrapper'>
        <h4>提报信息</h4>
        <a-row>
          <a-col :span='8'>
            <a-form-model-item
              :labelCol='labelCol'
              :wrapperCol='wrapperCol'
              label='提报班组'
              prop='reportGroupId'
              ref='reportGroupTag'
            >
              <j-dict-select-tag :triggerChange='true' v-model='faultModel.reportGroupId' :dictCode='dictGroupStr' />
            </a-form-model-item>
          </a-col>
          <a-col :span='8'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='提报人员' prop='reportUserId'>
              <a-select
                ref='tmyuserSelect1'
                v-model='faultModel.reportUserName'
                placeholder='请选择人员'
                :open='false'
                @focus='showUserModal(1)'
              >
                <a-icon slot='suffixIcon' type='ellipsis' @click='showUserModal(1)' />
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span='8'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='责任工程师' prop='dutyEngineer'>
              <a-select
                placeholder='请选择责任工程师'
                v-model='faultModel.dutyEngineerName'
                :open='false'
                :showArrow='true'
                @focus='showUserModal(2)'
                ref='tmyuserSelect2'
              >
                <div slot='suffixIcon'>
                  <a-icon
                    v-if='faultModel.dutyEngineerName'
                    @click.stop='clearValue(6)'
                    type='close-circle'
                    style='padding-right: 3px'
                  />
                  <a-icon type='ellipsis' />
                </div>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span='8'>
            <a-form-model-item
              :labelCol='labelCol'
              :key="'reportTime' + reportTimeKey"
              :wrapperCol='wrapperCol'
              label='提报时间'
              prop='reportTime'
            >
              <a-date-picker
                :disabledDate='dateTimeDisabled1'
                :disabledTime='disabledDateTime1'
                @change='
                  handleModel.solutionTime = undefined;
                  faultModel.solutionTime = undefined;
                  solutionTimeKey++;
                '
                format='YYYY-MM-DD HH:mm'
                :show-time="faultModel.happenTime ? { format: 'HH:mm', hideDisabledOptions: true } : false"
                v-model='faultModel.reportTime'
                style='width: 100%'
              />
            </a-form-model-item>
          </a-col>
        </a-row>
      </div>
      <div class='info-wrapper info-top-wrapper'>
        <h4>处理信息</h4>
        <a-row>
          <a-col :span='8'>
            <a-form-model-item
              :labelCol='labelCol' :wrapperCol='wrapperCol'
              label='处理状态'
              prop='status'
            >
              <a-radio-group
                name='radioGroup'
                :disabled='fromWriteReport'
                v-model='faultModel.status'
                @change='changeFaultStatus'
              >
                <a-radio :value='0'> 未处理</a-radio>
                <a-radio :value='1'> 已处理</a-radio>
                <!-- <a-radio :value='2' v-if="formType === 1">
                  已关闭
                </a-radio> -->
                <a-radio :value='3'> 遗留故障</a-radio>
              </a-radio-group>
            </a-form-model-item>
          </a-col>

<!--          <a-col :span='8' v-if='faultModel.status > 0 && faultModel.status !== 4'>-->
<!--            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='原因分类' prop='categoryId'>-->
<!--              <a-select-->
<!--                v-model='faultModel.categoryCodeDes'-->
<!--                placeholder='请选择所属分类'-->
<!--                :open='false'-->
<!--                :showArrow='true'-->
<!--                @focus='openSelectTypeModal(1)'-->
<!--                ref='categoryIdSelect'-->
<!--              >-->
<!--                <div slot='suffixIcon'>-->
<!--                  <a-icon-->
<!--                    v-if='faultModel.categoryCodeDes'-->
<!--                    @click.stop='clearValue(1)'-->
<!--                    type='close-circle'-->
<!--                    style='padding-right: 3px'-->
<!--                  />-->
<!--                  <a-icon type='ellipsis' />-->
<!--                </div>-->
<!--              </a-select>-->
<!--            </a-form-model-item>-->
<!--          </a-col>-->

          <a-col :span='8'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='故障代码' prop='faultCodeId'>
              <a-select
                v-model='faultModel.faultCodeCodeDes'
                placeholder='请选择所属故障代码'
                :open='false'
                :showArrow='true'
                @focus='openSelectTypeModal(2)'
                ref='faultCodeIdSelect'
              >
                <div slot='suffixIcon'>
                  <a-icon
                    v-if='faultModel.faultCodeId'
                    @click.stop='clearValue(2)'
                    type='close-circle'
                    style='padding-right: 3px'
                  />
                  <a-icon type='ellipsis' />
                </div>
              </a-select>
            </a-form-model-item>
          </a-col>

          <a-col :span='8' v-if='faultModel.status > 0 && faultModel.status !== 4'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='故障等级' prop='faultLevel'>
              <!-- <a-select v-model="faultModel.faultLevel">
                <a-select-option v-for="(val, index) in faultCodeLevelList" :key="index" :value="val.id">{{
                  val.levelName
                }}</a-select-option>
              </a-select> -->
              <j-dict-select-tag :triggerChange='true' v-model='faultModel.faultLevel' dictCode='bu_fault_level' />
            </a-form-model-item>
          </a-col>

        </a-row>
        <a-row v-if='faultModel.status > 0 && faultModel.status !== 4'>

<!--          <a-col :span='8' v-if='faultModel.status > 0 && faultModel.status !== 4'>-->
<!--            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='故障原因' prop='faultReasonId'>-->
<!--              <a-select-->
<!--                v-model='faultModel.reasonDesc'-->
<!--                placeholder='请选择所属故障代码'-->
<!--                :open='false'-->
<!--                :showArrow='true'-->
<!--                @focus='openSelectTypeModal(3)'-->
<!--                ref='faultReasonIdSelect'-->
<!--              >-->
<!--                <a-icon slot='suffixIcon' type='ellipsis' />-->
<!--              </a-select>-->
<!--            </a-form-model-item>-->
<!--          </a-col>-->

<!--          <a-col :span='8'>-->
<!--            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='处理措施' prop='faultSolutionId'>-->
<!--              <a-select-->
<!--                v-model='faultModel.solutionDesc'-->
<!--                placeholder='请选择所属故障代码'-->
<!--                :open='false'-->
<!--                :showArrow='true'-->
<!--                @focus='openSelectTypeModal(4)'-->
<!--                ref='faultSolutionIdSelect'-->
<!--              >-->
<!--                <a-icon slot='suffixIcon' type='ellipsis' />-->
<!--              </a-select>-->
<!--            </a-form-model-item>-->
<!--          </a-col>-->

          <a-col v-show='faultModel.faultCodeId' :span='24'>
            <a-form-model-item :labelCol='labelCol2' :wrapperCol='wrapperCol2' label='代码详情'>
              <a-space v-if='!getFaultdetailLoading'>
                <span style='color:#c1c1c1' v-for='(item,k) in getFaultdetailList'
                      :key='k'>故障{{ item.fltLevel_dictText }}：{{ item.fltCode }}-{{ item.fltName }}；</span>
                <span
                  style='color:#c1c1c1'>故障部件：{{ faultModel.faultCodeCode }}-{{ faultModel.faultCodeCodeDes }}；</span>
              </a-space>
              <a-icon v-else type='loading' />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row v-if='faultModel.status > 0 && faultModel.status !== 4'>
          <a-col :span='8'>
            <a-form-model-item
              :labelCol='labelCol'
              :key="'solutionTime' + solutionTimeKey + reportTimeKey"
              :wrapperCol='wrapperCol'
              label='处理时间'
              prop='solutionTime'
            >
              <a-date-picker
                :disabledDate='dateTimeDisabled2'
                :disabledTime='disabledDateTime2'
                format='YYYY-MM-DD HH:mm'
                :show-time="faultModel.reportTime ? { format: 'HH:mm', hideDisabledOptions: true } : false"
                @change='datePickerChange'
                v-model='handleModel.solutionTime'
                style='width: 100%'
              />
            </a-form-model-item>
          </a-col>

          <a-col :span='8'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='原因描述' prop='reasonDesc'>
              <a-textarea placeholder='请输入内容' v-model='faultModel.reasonDesc' />
            </a-form-model-item>
          </a-col>
          <a-col :span='8'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='措施描述' prop='solutionDesc'>
              <a-textarea placeholder='请输入内容' v-model='faultModel.solutionDesc' />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row v-if='faultModel.status > 0 && faultModel.status !== 4'>
          <a-col :span='8'>
            <a-form-model-item
              v-if='faultModel.status > 0 && faultModel.status !== 4'
              :labelCol='labelCol'
              :wrapperCol='wrapperCol'
              label='处理结果'
              prop='handleStatus'
            >
              <j-dict-select-tag :triggerChange='true' v-model='faultModel.handleStatus' dictCode='bu_handlestatus' />
            </a-form-model-item>
          </a-col>
          <a-col :span='8'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='处理班组' prop='solutionGroupId'>
              <j-dict-select-tag :triggerChange='true' v-model='faultModel.solutionGroupId' :dictCode='dictGroupStr' />
            </a-form-model-item>
          </a-col>
          <a-col :span='8'>
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='处理人员' prop='solutionUserId'>
              <a-select
                placeholder='请选择处理人员'
                v-model='handleModel.solutionUserName'
                :open='false'
                @focus='showUserModal(3)'
                ref='tmyuserSelect3'
              >
                <a-icon slot='suffixIcon' type='ellipsis' />
              </a-select>
            </a-form-model-item>
          </a-col>
          <!-- <a-col :span='8' v-if="formType === 1">
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='关闭时间' prop='closeTime'>
              <a-date-picker v-model='faultModel.closeTime' style='width:100%;' />
            </a-form-model-item>
          </a-col>
          <a-col :span='8' v-if="formType === 1">
            <a-form-model-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='关闭人员' prop='closeUserId'>
              <a-select
                placeholder='请选择关闭人员'
                v-model='faultModel.closeUserName'
                :open='false'
                :showArrow='true'
                @focus='showUserModal(4)'
                ref='tmyuserSelect4'
              >
                <a-icon slot='suffixIcon' type='ellipsis' />
              </a-select>
            </a-form-model-item>
          </a-col> -->
        </a-row>
      </div>
      <div class='clearfix'>
        <a-row>
          <a-col :span='12'>
            <DocUpload
              ref='upload'
              :default-file-list='defaultFileList'
              @uploaded='successUploadFile'
              @removed='onRemoveFile'
              @uploadFail='uploadFail'
              :show-upload='false'
              :isFileEmpty='true'
            ></DocUpload>
          </a-col>
        </a-row>
        <a-row
        >
          <a-divider orientation='left' style='font-size: 14px'>已上传列表</a-divider>
          <vxe-table
            style='margin-top: 12px'
            :data='annexList'
            show-overflow='tooltip'
            :show-header='false'
            :edit-config="{ trigger: 'manual', mode: 'row' }"
          >
            <div class=''></div>
            <vxe-table-column
              type='index'
              title='序号'
              width='50px'
              header-align='center'
              align='center'
            ></vxe-table-column>
            <vxe-table-column field='name' title='文件名' header-align='center' align='left'></vxe-table-column>
            <vxe-table-column
              field='annexType_dictText'
              title='附件类型'
              width='120px'
              header-align='center'
              align='center'
            ></vxe-table-column>
            <vxe-table-column title='操作' width='160px' header-align='center' align='center'>
              <template v-slot='{ row, rowIndex }'>
                <a style='margin-right: 12px' @click.stop='handleSeeing(row)'>查看</a>
                <a @click.stop='handleDelete(row, rowIndex)'>删除</a>
              </template>
            </vxe-table-column>
          </vxe-table>
        </a-row>
      </div>
    </a-form-model>
    <train-plan-list ref='planModalForm' @ok='onSelectPlan'></train-plan-list>
    <doc-preview-modal :title='fileName' ref='docPreview'></doc-preview-modal>
    <user-list
      ref='tUserModalForm'
      :dep-id="userSelectType === 1 ? faultModel.reportGroupId : userSelectType === 3 ? faultModel.solutionGroupId : ''"
      :multiple='false'
      @ok='onUserSelect'
    ></user-list>
    <fault-category-list ref='categorySelect' :multiple='false' @ok='addCategoryTarget'></fault-category-list>
    <fault-code-list ref='codeSelect' type='unit' :multiple='false' @ok='addCategoryTarget'></fault-code-list>
    <fault-reason-list ref='reasonSelect' :multiple='false' @ok='addCategoryTarget'></fault-reason-list>
    <fault-solution-list ref='solutionSelect' :multiple='false' @ok='addCategoryTarget'></fault-solution-list>
    <work-station-list ref='stationSelectModal' @ok='onSelectStation'></work-station-list>
    <!-- <train-structure-list
      ref="assetSelect"
      :multiple="false"
      :canSelectType="[3, 4]"
      @ok="addFaultAsset"
    ></train-structure-list> -->
    <CarDeviceSelectNew
      :canSelectType='[3, 4]'
      :trainNo='faultModel.trainNo'
      :lineId='faultModel.lineId'
      ref='assetSelect'
      :multiple='false'
      @ok='addFaultAsset'
    />
    <supplier-list ref='supplierModal' :multiple='false' @ok='supplierSelect'></supplier-list>
  </div>
</template>

<script>
import moment from 'moment'
import { isPrivilege } from '@/api/tirosApi'
import UserList from '@views/tiros/common/selectModules/UserList'
import { addBreakdown, editBreakdown, getFaultCodeLevel, getFaultdetail } from '@api/tirosDispatchApi'
import { everythingIsEmpty, randomUUID } from '@/utils/util'
import DocUpload from '@views/tiros/common/doc/DocUpload'
import FaultCategoryList from '@views/tiros/common/selectModules/FaultCategoryList'
import FaultCodeList from '@views/tiros/common/selectModules/FaultCodeList'
import FaultReasonList from '@views/tiros/common/selectModules/FaultReasonList'
import FaultSolutionList from '@views/tiros/common/selectModules/FaultSolutionList'
// import TrainStructureList from '@views/tiros/common/selectModules/TrainStructureList'
import SupplierList from '@views/tiros/common/selectModules/SupplierList'
import WorkStationList from '@views/tiros/common/selectModules/WorkStationList'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import CarDeviceSelectNew from '@views/tiros/common/selectModules/CarDeviceSelectNew'
import TrainPlanList from '@/views/tiros/common/selectModules/TrainPlanList'
import { mapState } from 'vuex'

export default {
  name: 'BreakdownFormItem',
  components: {
    UserList,
    DocUpload,
    FaultCategoryList,
    FaultCodeList,
    FaultReasonList,
    FaultSolutionList,
    // TrainStructureList,
    SupplierList,
    DocPreviewModal,
    WorkStationList,
    CarDeviceSelectNew,
    TrainPlanList
  },
  props: {
    workStationsList: {
      type: Array,
      default: () => []
    },
    fromWriteReport: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      getFaultdetailLoading: false,
      getFaultdetailList: [],
      solutionTimeKey: 0,
      reportTimeKey: 0,
      dictGroupStr:
        'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
      defaultFileList: [],
      annexList: [],
      fileList: [],
      filePath: '',
      fileName: '',
      status: false,
      saveFlag: true,
      uploading: false,
      value: 1,
      faultModel: {
        planName: undefined,
        planId: undefined,
        reportTime: undefined,
        happenTime: undefined,
        solutionTime: undefined,
        reasonDesc: undefined,
        solutionDesc: undefined,
        workStationName: undefined
      },
      handleModel: {
        solutionTime: undefined
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      labelCol2: {
        xs: { span: 24 },
        sm: { span: 2 }
      },
      wrapperCol2: {
        xs: { span: 24 },
        sm: { span: 21 }
      },
      labelCol3: {
        xs: { span: 24 },
        sm: { span: 13 }
      },
      wrapperCol3: {
        xs: { span: 24 },
        sm: { span: 11 }
      },
      userSelectType: 1,
      dictTrainStr: '',
      dictCodefaultcode: 'bu_fault_code,fault_code,id',
      dictCodesubsys: '',
      dictTrainSys: '',
      confirmLoading: false,
      trainStructId: '',
      faultCodeLevelList: [],
      formRules: {
        faultSn: [],
        categoryId: [{ required: false, message: '请选择故障分类!', trigger: 'change' }],
        faultCodeId: [{ required: true, message: '请选择故障代码!', trigger: 'change' }],
        faultLevel: [{ required: true, message: '请选择故障等级!', trigger: 'change' }],
        workStationName: [{ required: false, message: '请选择故障工位!', trigger: 'change' }],
        happenTime: [{ required: true, message: '请选择故障发现时间!' }],
        lineId: [{ required: true, message: '请选择所属线路!', trigger: 'change' }],
        trainNo: [{ required: true, message: '请选择车辆!', trigger: 'change' }],
        sysId: [{ required: true, message: '请选择所属系统!', trigger: 'change' }],
        // subSysId: [{ required: true, message: '请选择子系统!', trigger: 'change' }],
        faultAssetName: [{ required: true, message: '请选择部件!', trigger: 'change' }],
        faultDesc: [
          { required: true, message: '请填写故障描述!', trigger: 'change' },
          { max: 249, message: '输入长度不能超过249字符!' }
        ],
        phase: [{ required: true, message: '请选择发现阶段!' }],
        outSupplierName: [{ required: true, message: '请选择厂商!', trigger: 'change' }],
        planName: [{ required: true, message: '请选择列计划!', trigger: 'change' }],
        faultOnline: [],
        hasDuty: [],
        outsource: [],
        reportGroupId: [{ required: true, message: '请选择提报班组!', trigger: 'change' }],
        reportUserId: [{ required: true, message: '请选择提报人员', trigger: 'change' }],
        dutyEngineer: [],

        status: [],
        closeTime: [],
        handleStatus: [{ required: true, message: '请选择处理结果!', trigger: 'change' }],
        faultReasonId: [{ required: true, message: '请选择故障原因!', trigger: 'change' }],
        faultSolutionId: [{ required: true, message: '请选择处理措施!', trigger: 'change' }],
        reasonDesc: [
          { required: true, message: '请输入原因描述!', trigger: 'change' },
          {
            max: 249,
            message: '输入长度不能超过249字符!'
          }
        ],
        solutionDesc: [
          { required: true, message: '请输入措施描述!', trigger: 'change' },
          {
            max: 249,
            message: '输入长度不能超过249字符!'
          }
        ],
        reportTime: [{ required: true, message: '请选择提报时间!', trigger: 'change' }],
        solutionTime: [{ required: true, message: '请选择故障处理时间!', trigger: 'change' }],
        solutionGroupId: [{ required: true, message: '请选择处理班组!', trigger: 'change' }],
        solutionUserId: [{ required: true, message: '请选择处理人员!', trigger: 'change' }],
        closeUserId: []
      },
      reason: {
        categoryId: '',
        faultCodeId: ''
      },
      solution: {
        categoryId: '',
        faultCodeId: '',
        faultReasonId: ''
      },
      reasonList: [],
      solutionList: [],
      faultId: '',
      formType: '',
      faultType: '',
      assetTypeData: [],
      isWriteSave: false
    }
  },
  computed: {
    ...mapState(['user'])
  },
  created() {
    if (this.fromWriteReport) {
      this.formRules.categoryId[0].required = true
      this.formRules.faultCodeId[0].required = true
      this.formRules.faultLevel[0].required = true
      this.formRules.workStationName[0].required = true
    }
  },
  methods: {
    onSelectPlan(data) {
      this.faultModel.planName = data[0].planName
      this.faultModel.planId = data[0].id
      this.faultModel.lineId = data[0].lineId
      this.faultModel.trainNo = data[0].trainNo
      this.changeLine(data[0].lineId)
      this.$refs.form.validateField('planName')
      this.$refs.form.validateField('lineId')
      this.$refs.form.validateField('trainNo')
    },
    changePlanSelect(value) {
      if (!value) {
        this.faultModel.planName = ''
        this.faultModel.planId = ''
        this.$refs.form.validateField('planName')
      }
    },
    openPlanModel() {
      this.$refs.planModalForm.showModal()
    },
    getFaultCodeLevel(id) {
      // 获取故障等级
      getFaultCodeLevel({
        id: id
      }).then((res) => {
        if (res.success && res.result) {
          this.$set(this.faultModel, 'faultLevel', res.result.levelValue)
        }
      })
    },
    getFaultdetail(id) {
      // 获取故障信息
      console.log(id)
      this.getFaultdetailLoading = true
      getFaultdetail({ code: id }).then((res) => {
        console.log(res)
        if (res.success && res.result.length) {
          this.getFaultdetailList = res.result.filter((item) => {
            return item.fltLevel !== 0 && item.fltLevel !== 3 //条件;
          })
          console.log(this.getFaultdetailList)
        }
        console.log(this.getFaultdetailList)
        this.getFaultdetailLoading = false
      }).catch((err) => {
        this.getFaultdetailLoading = false
      })
    },
    range(start, end) {
      const result = []
      for (let i = start; i < end; i++) {
        result.push(i)
      }
      return result
    },
    dateTimeDisabled1(current) {
      let happenTime = moment(this.faultModel.happenTime).format('YYYY-MM-DD')
      return !this.faultModel.happenTime || current <= moment(happenTime)
    },
    dateTimeDisabled2(current) {
      let reportTime = moment(this.faultModel.reportTime).format('YYYY-MM-DD')
      return !this.faultModel.reportTime || current <= moment(reportTime)
    },
    disabledDateTime1() {
      const happenTime = moment(this.faultModel.happenTime)
      const reportTime = moment(this.faultModel.reportTime)
      let h
      let m
      if (happenTime.format('YYYY-MM-DD') !== reportTime.format('YYYY-MM-DD')) {
        h = 0
        m = 0
      } else {
        h = this.faultModel.happenTime ? moment(happenTime).get('hours') : 0
        m = this.faultModel.happenTime ? moment(happenTime).get('minutes') + 1 : 0
      }
      return {
        disabledHours: () => this.range(0, h),
        disabledMinutes: () => this.range(0, m)
      }
    },
    disabledDateTime2() {
      const reportTime = moment(this.faultModel.reportTime)
      const solutionTime = moment(this.handleModel.solutionTime)
      let h
      let m
      if (reportTime.format('YYYY-MM-DD') !== solutionTime.format('YYYY-MM-DD')) {
        h = 0
        m = 0
      } else {
        h = this.faultModel.reportTime ? moment(reportTime).get('hours') : 0
        m = this.faultModel.reportTime ? moment(reportTime).get('minutes') + 1 : 0
      }
      return {
        disabledHours: () => this.range(0, h),
        disabledMinutes: () => this.range(0, m)
      }
    },
    handleSysFocus() {
      if (!this.faultModel.lineId) this.$message.warn('请先选择线路!')
    },
    /* handleSubSysFocus(){
       if(!this.faultModel.sysId) this.$message.warn("请先选择系统!")
       if(!this.faultModel.trainNo) this.$message.warn("请先选择车辆!")
     },*/
    handleAssetFocus() {
      // if(!this.faultModel.subSysId) this.$message.warn("请先选择子系统!")
      if (!this.faultModel.trainNo) this.$message.warn('请先选择车辆!')
    },
    handleTrainNo(val, optionData) {
      //console.log(val+""+JSON.stringify(optionData.extFields[0].train_struct_id))
      if (optionData.extFields) {
        this.trainStructId = optionData.extFields[0].train_struct_id
      }
    },
    showAssetModal() {
      if (!this.faultModel.trainNo) {
        this.$message.warn('请先选择车辆!')
      } else {
        this.$refs.assetSelect.showModal()
        this.$refs.faultAssetSelect.blur()
      }
    },
    openSelectTypeModal(type) {
      console.log(type)
      this.faultType = type
      switch (type) {
        case 1:
          this.$refs.categorySelect.showModal()
          this.$refs.categoryIdSelect.blur()
          break
        case 2:
          this.$refs.codeSelect.showModal()
          this.$refs.faultCodeIdSelect.blur()
          break
        case 3:
          this.$refs.reasonSelect.showModal()
          this.$refs.faultReasonIdSelect.blur()
          break
        case 4:
          this.$refs.solutionSelect.showModal()
          this.$refs.faultSolutionIdSelect.blur()
          break
        case 5:
          // 故障工位
          this.$refs.stationSelectModal.showModal()
          this.$refs.workStationSelect.blur()
          break
      }
    },
    addFaultAsset(data) {
      if (!everythingIsEmpty(data)) {
        this.faultModel.trainStructureId = data[0].structDetailId
        this.faultModel.faultAssetName = data[0].assetName
        this.faultModel.faultAssetId = data[0].id
        this.$refs.form.validateField('faultAssetName')
      }
    },
    addCategoryTarget(data) {
      if (data.length) {
        console.log(this.faultType)
        if (this.faultType === 1) {
          this.faultModel.categoryId = data[0].id
          this.faultModel.categoryCode = data[0].categoryCode
          this.faultModel.categoryCodeDes = data[0].categoryDesc

          // this.faultModel.faultCodeId = ''
          // this.faultModel.faultCodeCode = ''
          // this.faultModel.faultReasonId = ''
          // this.faultModel.faultReasonCode = ''
          // this.faultModel.faultSolutionId = ''
          // this.faultModel.faultSolutionCode = ''
          if (this.faultModel.faultLevel) {
            this.faultModel.faultLevel = ''
          }
          // this.$set(this.faultModel, 'reasonDesc', '')
          // this.$set(this.faultModel, 'solutionDesc', '')
          this.$refs.form.validateField('categoryId')
        }
        if (this.faultType === 2) {
          this.faultModel.faultCodeId = data[0].id
          this.faultModel.faultCodeCode = data[0].fltCode
          this.faultModel.faultCodeCodeDes = data[0].fltName
          // this.faultModel.faultReasonId = ''
          // this.faultModel.faultReasonCode = ''
          // this.faultModel.faultSolutionId = ''
          // this.faultModel.faultSolutionCode = ''
          // this.$set(this.faultModel, 'reasonDesc', '')
          // this.$set(this.faultModel, 'solutionDesc', '')
          if (this.faultModel.faultLevel) {
            this.faultModel.faultLevel = ''
          }
          this.$refs.form.validateField('faultCodeId')
          this.getFaultCodeLevel(this.faultModel.faultCodeId)

          this.getFaultdetail(this.faultModel.faultCodeId)
        }
        if (this.faultType === 3) {
          console.log(data[0])
          this.faultModel.faultReasonId = data[0].id
          this.faultModel.faultReasonCode = data[0].reasonCode
          // this.faultModel.faultSolutionId = ''
          // this.faultModel.faultSolutionCode = ''
          this.$set(this.faultModel, 'reasonDesc', data[0].reasonDesc)
          // this.$set(this.faultModel, 'solutionDesc', '')
          this.$refs.form.validateField('faultReasonId')
          this.$refs.form.validateField('reasonDesc')
        }
        if (this.faultType === 4) {
          this.faultModel.faultSolutionId = data[0].id
          this.faultModel.faultSolutionCode = data[0].solutionCode
          this.$set(this.faultModel, 'solutionDesc', data[0].solutionDesc)
          this.$refs.form.validateField('faultSolutionId')
          this.$refs.form.validateField('solutionDesc')
        }
      }
    },
    clearValue(type) {
      if (type === 1) {
        this.faultModel.categoryId = ''
        this.faultModel.categoryCode = ''
        this.faultModel.categoryCodeDes = ''
      }
      if (type === 2) {
        this.faultModel.faultCodeId = ''
        this.faultModel.faultCodeCode = ''
        this.faultModel.faultCodeCodeDes = ''
      }
      if (type === 5) {
        this.faultModel.workStationId = ''
        this.faultModel.workStationName = ''
      }

      if (type === 6) {
        // 责任工程师
        this.faultModel.dutyEngineerName = ''
        this.faultModel.dutyEngineer = ''
      }
    },
    // 文件上传完成
    successUploadFile(fileInfos) {
      fileInfos.map((item) => {
        Object.assign(item, { id: randomUUID() })
      })
      if (fileInfos && fileInfos.length > 0) {
        fileInfos.map((item) => {
          let obj = {
            annexType: 1,
            fileId: item.id,
            type: item.type,
            fileSize: item.fileSize,
            name: item.name,
            savepath: item.savepath
          }
          this.annexList.push(obj)
        })
        // 保存故障信息
        this.saveFaultInfo()
        // 在文档中心添加记录
        // addFile(fileInfos).then((res) => {
        //   if (res.success) {

        //   } else {
        //     this.$message.error('附件上传失败!')
        //   }
        // })
      } else {
        this.saveFaultInfo()
      }
    },
    saveFaultInfo() {
      let saveData = Object.assign({}, this.faultModel, {
        happenTime: moment(this.faultModel.happenTime).format('YYYY-MM-DD HH:mm:ss'),
        reportTime: moment(this.faultModel.reportTime).format('YYYY-MM-DD HH:mm:ss'),
        closeTime: this.faultModel.closeTime ? moment(this.faultModel.closeTime).format('YYYY-MM-DD HH:mm:ss') : null,
        faultOnline: this.faultModel.faultOnline ? 1 : 0,
        // hasDuty: this.faultModel.hasDuty ? 1 : 0,
        hasDuty: 0,
        outsource: this.faultModel.outsource ? 1 : 0
      })
      this.handleModel.solutionTime = moment(this.handleModel.solutionTime).format('YYYY-MM-DD HH:mm:ss')
      if (saveData.status > 0 && saveData.status !== 4) {
        saveData.handleRecordList = [
          Object.assign({}, this.handleModel, {
            faultReasonId: this.faultModel.faultReasonId,
            faultSolutionId: this.faultModel.faultSolutionId,
            reasonDesc: this.faultModel.reasonDesc,
            solutionDesc: this.faultModel.solutionDesc,
            solutionGroupId: this.faultModel.solutionGroupId
          })
        ]
      } else {
        saveData.handleRecordList = []
      }

      let req = null
      saveData['annexList'] = this.annexList
      if (!this.faultModel.id) {
        saveData['id'] = this.faultId
        req = addBreakdown(saveData)
      } else {
        req = editBreakdown(saveData)
      }
      req
        .then((res) => {
          if (res.success) {
            this.isWriteSave = true
            this.$emit('ok')
          } else {
            this.$emit('fail')
          }
        })
        .catch((err) => {
          console.error('保存异常：', err)
          this.$emit('fail')
        })
        .finally(() => {
        })
    },
    setBforeUploadStatus(val) {
      this.saveFlag = val
    },
    setUpLoadingEndStatus(val) {
      this.saveFlag = val
    },
    uploadFail(file) {
      this.$emit('fail')
    },
    onRemoveFile(file) {
    },
    beginUpload() {
      this.confirmLoading = true
      this.$refs.upload.beginUpload()
    },
    async handlePrivilege(id, operation) {
      let param = { id: id, operation: operation }
      await isPrivilege(param).then((res) => {
        this.status = res.result
      })
    },
    async handleSeeing(data) {
      this.fileName = data.name
      // this.filePath = window._CONFIG['onlinePreviewDomainURL'] + '?url=' + encodeURIComponent(data.savepath)
      this.$refs.docPreview.handleFilePath(data.savepath)
      /* await this.handlePrivilege(data.id, 2)
      if (!this.status) {
        this.$message.error('您没有权限!')
      } else {
        this.fileName = data.name
        // this.filePath = window._CONFIG['onlinePreviewDomainURL'] + '?url=' + encodeURIComponent(data.savepath)
        this.$refs.docPreview.handleFilePath(data.savepath)
      }*/
    },
    async handleDelete(data, index) {
      // 删除文件
      let that = this
      that.$confirm({
        title: '提示',
        content: '确定删除该条附件吗？',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk() {
          that.annexList.splice(index, 1)
          // deleteContractFiles({
          //   ids: data.id,
          // }).then((res) => {
          //   if (res.success) {
          //     that.annexList.splice(index, 1)
          //     that.$message.success(res.message)
          //   } else {
          //     that.$message.error(res.message)
          //   }
          // })
        },
        onCancel() {
        }
      })
    },
    /* handleChangeReason(data) {
       this.handleModel.faultReasonId = data
       this.reasonList.map(item => {
         if (item.id === data) {
           this.handleModel.reasonDesc = item.reasonDesc
           this.faultModel.reasonDesc = item.reasonDesc
         }
       })
       this.solution = {
         categoryId: this.faultModel.categoryId,
         faultCodeId: this.faultModel.faultCodeId,
         faultReasonId: data
       }
       getBreakdownSolution(this.solution).then((res) => {
         this.solutionList = res.result
       })
     },
     handleChangeSolution(data) {
       this.handleModel.faultSolutionId = data
       this.solutionList.map(item => {
         if (item.id === data) {
           this.handleModel.solutionDesc = item.solutionDesc
           this.faultModel.solutionDesc = item.solutionDesc
         }
       })
     },*/
    add(orderId, taskId, formType) {
      this.edit({
        workOrderId: orderId,
        orderTaskId: taskId,
        formType: formType,
        createType: formType //  1 表示来自调度， 2 表示来自工班
      })
    },
    edit(record) {
      const def = {
        faultOnline: false,
        hasDuty: false,
        outsource: false,
        status: 0,
        reportUserName: '',
        reportUserId: '',
        dutyEngineer: '',
        dutyEngineerName: '',
        closeUserId: '',
        closeUserName: '',
        categoryId: '',
        categoryCode: '',
        categoryCodeDes: '',
        faultCodeId: '',
        faultCodeCode: '',
        faultReasonId: '',
        faultReasonCode: '',
        faultSolutionId: '',
        faultSolutionCode: '',
        solutionUserId: '',
        solutionUserName: '',
        faultAssetId: '',
        reportGroupId: '',
        outSupplierId: '',
        outSupplierName: '',
        sysId: '',
        subSysId: '',
        trainNo: '',
        lineId: '',
        faultAssetName: '',
        trainStructureId: '',
        happenTime: '',
        workStationId: '',
        workStationName: '',
        solutionTime: '',
        solutionGroupId: '',
        solutionDesc: '',
        reasonDesc: '',
        reportTime: '',
        planName: '',
        planId: ''
      }
      record.workStationName = record.workstationName
      this.faultModel = Object.assign(def, record)
      if (this.faultModel.categoryCode) {
        this.isWriteSave = true
      }

      if (this.faultModel.reportTime) {
        this.faultModel.reportTime = this.$moment(this.faultModel.reportTime)
      }

      if (this.faultModel.faultCodeId) {
        console.log(111111111)
        this.getFaultdetail(this.faultModel.faultCodeId)
      }

      // 解决console 提示时间不是有效的moment时间的问题
      if (this.faultModel.happenTime) {
        this.faultModel.happenTime = this.$moment(this.faultModel.happenTime)
      }
      if (!everythingIsEmpty(this.faultModel.handleRecordList)) {
        this.handleModel = this.faultModel.handleRecordList[0]
        this.faultModel = Object.assign(this.faultModel, {
          faultReasonId: this.handleModel.faultReasonId,
          faultSolutionId: this.handleModel.faultSolutionId,
          reasonDesc: this.handleModel.reasonDesc,
          solutionDesc: this.handleModel.solutionDesc,
          solutionGroupId: this.handleModel.solutionGroupId,
          faultSolutionCode: this.handleModel.faultSolutionCode,
          faultReasonCode: this.handleModel.faultReasonCode,
          solutionUserId: this.handleModel.solutionUserId,
          solutionUserName: this.handleModel.solutionUserName,
          solutionTime: this.handleModel.solutionTime
        })
      }
      if (!everythingIsEmpty(record)) {
        // this.changeLinesubsys(record.sysId, true)
        // this.changeAsset(record.subSysId, true)
        this.faultModel = Object.assign(this.faultModel, {
          faultOnline: record.faultOnline === 1,
          hasDuty: record.hasDuty === 1,
          outsource: record.outsource === 1
        })
        if (record.annexList) this.annexList = record.annexList
      }
      // 工班新增默认选择当前工班和人员
      if (record.formType === 2) {
        if (
          this.faultModel.reportUserName === '' &&
          this.faultModel.reportUserId === '' &&
          this.$store.getters.userInfo.departIsGroup
        ) {
          this.faultModel.reportUserName = this.$store.getters.userInfo.realname
          this.faultModel.reportUserId = this.$store.getters.userInfo.id
        }
        if (everythingIsEmpty(this.faultModel.reportGroupId) && this.$store.getters.userInfo.departIsGroup) {
          this.$set(this.faultModel, 'reportGroupId', this.$store.getters.userInfo.departId)
        }
      }

      if (this.workStationsList.length > 0) {
        this.faultModel.workStationId = this.workStationsList[0].workstationId
        this.faultModel.workStationName = this.workStationsList[0].workstationName
      }

      this.formType = record.formType
      if (!this.faultModel.submitStatus) {
        this.faultModel.submitStatus = this.formType === 1 ? 1 : 0
      }

      // 手动触发一次是否处理 change 保证如果状态非手动发生变化此函数内逻辑执行一次
      this.changeFaultStatus()
    },
    handleChange({ file }) {
      if (file.status !== 'uploading') {
      }
    },
    changeFaultStatus() {
      if ((this.formType === 2 && this.faultModel.status !== 0) || this.fromWriteReport) {
        if (
          (!this.faultModel.solutionGroupId || this.faultModel.solutionGroupId === '') &&
          this.$store.getters.userInfo.departIsGroup
        ) {
          this.faultModel.solutionGroupId = this.$store.getters.userInfo.departId
        }

        if (
          (!this.handleModel.solutionUserId || !this.faultModel.solutionUserName) &&
          this.$store.getters.userInfo.departIsGroup
        ) {
          this.faultModel.solutionUserName = this.$store.getters.userInfo.realname
          this.faultModel.solutionUserId = this.$store.getters.userInfo.id
          this.handleModel.solutionUserName = this.$store.getters.userInfo.realname
          this.handleModel.solutionUserId = this.$store.getters.userInfo.id
        }
      }
      // if (this.formType === 1 && this.faultModel.status === 2) {
      //   this.faultModel.closeUserId = this.$store.getters.userInfo.id
      //   this.faultModel.closeUserName = this.$store.getters.userInfo.realname
      //   // this.faultModel.closeTime = moment(new Date()).format('YYYY-MM-DD')
      // }
    },
    changeClose(checked) {
      this.isClose = checked
    },
    showUserModal(type) {
      this.userSelectType = type
      this.$refs.tUserModalForm.showModal()
      switch (type) {
        case 1:
          this.$refs.tmyuserSelect1.blur()
          break
        case 2:
          this.$refs.tmyuserSelect2.blur()
          break
        case 3:
          this.$refs.tmyuserSelect3.blur()
          break
        case 4:
          this.$refs.tmyuserSelect4.blur()
          break
      }
    },
    onUserSelect(data) {
      if (data.length) {
        if (this.userSelectType === 1) {
          this.faultModel.reportUserName = data[0].realname
          this.faultModel.reportUserId = data[0].id
          this.$refs.form.validateField('reportUserId')
        }

        if (this.userSelectType === 2) {
          this.faultModel.dutyEngineerName = data[0].realname
          this.faultModel.dutyEngineer = data[0].id
          this.$refs.form.validateField('dutyEngineer')
        }

        if (this.userSelectType === 3) {
          this.handleModel.solutionUserName = data[0].realname
          this.handleModel.solutionUserId = data[0].id
          this.faultModel.solutionUserName = data[0].realname
          this.faultModel.solutionUserId = data[0].id
          this.$refs.form.validateField('solutionUserId')
        }

        if (this.userSelectType === 4) {
          this.faultModel.closeUserName = data[0].realname
          this.faultModel.closeUserId = data[0].id
          this.$forceUpdate()
          this.$refs.form.validateField('closeUserId')
        }
        this.$forceUpdate()
      }
    },
    datePickerChange(e) {
      // console.log(e)
      this.handleModel.solutionTime = e
      this.faultModel.solutionTime = e
      this.$refs.form.validateField('solutionTime')
    },
    openSupplierModal() {
      this.$refs.supplierModal.showModal()
      this.$refs.mySupplierSelect.blur()
    },
    supplierSelect(data) {
      if (!everythingIsEmpty(data)) {
        // this.$nextTick(() => {
        //   this.form.setFieldsValue({ supplierName: data[0].name })
        // })
        this.faultModel.outSupplierName = data[0].name
        this.faultModel.outSupplierId = data[0].id
        this.$refs.form.validateField('outSupplierName')
      }
    },
    changeLine(data, edit) {
      this.dictTrainStr = ''
      /* this.dictTrainSys = ''*/
      /*if (edit !== true) this.faultModel.sysId = ''*/
      if (data) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + data + '|train_struct_id'
        this.dictTrainSys = 'bu_train_asset_type,name,id,struct_type=1 and parent_id is null and train_type_id=' +
          '(select train_type_id from bu_mtr_line where line_id=' + data + ' )'
      }
    },
    changeLinesubsys(data, edit) {
      this.dictCodesubsys = ''
      if (edit !== true) {
        this.faultModel.subSysId = null
        this.faultModel.faultAssetId = null
      }
      if (data) {
        this.dictCodesubsys = 'bu_train_asset_type,name,id,struct_type=2 and parent_id=\'' + data + '\''
      }
    },
    /* changeAsset (data, edit) {
      if (edit !== true) this.faultModel.faultAssetId = null
      if (data) {
        if (this.faultModel.sysId) {
          listOrTreeTrainAsset({
            trainNo: this.faultModel.trainNo,
            assetTypeId: data,
            queryChild: true
          }).then((res) => {
            if (res.result.length > 0) {
              this.assetTypeData = res.result[0].children
            }
          })
        } else {
          this.$message.warn('请先选择系统!')
        }
      }
    },*/
    // 确定
    save() {
      const that = this
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (!that.faultModel.id) {
            that.faultId = randomUUID()
          } else {
            that.faultId = that.faultModel.id
          }
          this.beginUpload()
        } else {
          this.$message.warn('请填写必须填写的信息')
          this.$emit('cancel')
        }
      })
    },
    check() {
      return new Promise((resolve, reject) => {
        this.$refs.form.validate((valid) => {
          if (valid) {
            // this.saveFaultInfo()
            if (this.isWriteSave) {
              resolve(true)
            } else {
              reject('你还有没有保存故障信息, 请先保存故障信息')
            }
          } else {
            reject('请填写必须填写的信息')
          }
        })
      })
    },
    // 故障工位选择
    onSelectStation(data) {
      if (data && data.length > 0) {
        this.faultModel.workStationId = data[0].id
        this.faultModel.workStationName = data[0].name
        this.$refs.form.validateField('workStationName')
      }
    }
  }
}
</script>

<style lang='less' scoped>
.info-wrapper {
  border: 1px solid #eee;
  position: relative;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 20px;
}

.info-wrapper h4 {
  position: absolute;
  top: -14px;
  padding: 1px 8px;
  margin-left: 16px;
  color: #777;
  border-radius: 2px 2px 0 0;
  background: #fff;
  font-size: 14px;
  width: auto;
}
</style>