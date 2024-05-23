<template>
  <div class="test-class" :style="`height: calc(100% - ${getButtonShow() ? '47px' : '4px'});`">
    <div na-flex-height-full v-if="operator !== 2 && operator !== 3">
      <a-form layout="inline" v-if="getButtonShow()">
        <a-row :gutter="24">
          <a-col :md="24" :sm="8">
            <a-form-item label="物料:">
              <a-input v-model="searchText" placeholder="物资编码或名称" @change="sortTable" allowClear></a-input>
            </a-form-item>
            <a-form-item label="物料分类:">
              <j-dict-select-tag
                v-model="searchCategory"
                dictCode="bu_material_type"
                triggerChange
                :hiddenArray="[1]"
                @change="sortTable"
                style="width: 160px"
              />
            </a-form-item>
            <a-form-item>
              <a-space>
                <a-button type="dashed" class="primary-color" @click="handleAdd">添加</a-button>
                <a-button type="dashed" @click="handleDel">删除</a-button>
                <!-- 暂时去掉，规定只有发料工单才需要发料，其他工单需要物料也是走发料工单 -->
                <a-tooltip placement="topLeft" v-if="false">
                  <template slot="title">
                    <span> "需发料?”- 表示该物资是否需要从车间材料员申请发放，如果为否，表示班组已有该物资了，无需领料，只是用来记录工单消耗物料数量</span>
                  </template>
                  <span type="link" style="color: #d43f3a;">“需发料?" 说明</span>
                </a-tooltip>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <div na-flex-height-full>
        <!-- 非提交工单时显示 -->
        <!-- {{operator + ',' + operator + ',' + orderType}} -->
        <!-- operator!==0 && operator!==1 && (orderType === 4 || orderType === 1) -->
        <vxe-table
          v-if="operator !== 4"
          border
          ref="listTable"
          align="center"
          max-height="auto"
          class="mytable-style"
          :row-class-name="rowClassName"
          :data.sync="taskMaterials"
          :edit-rules="validRules"
          :keep-source="true"
          :checkbox-config="{ highlight: true, range: true }"
          :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
          :filter-config="{ showIcon: false }"
          @blur="$refs.listTable ? $refs.listTable.clearValidate() : null"
        >
          <vxe-table-column type="seq" fixed="left" width="40"></vxe-table-column>
          <vxe-table-column type="checkbox" fixed="left" width="40"></vxe-table-column>
          <vxe-table-column v-if="tasks && tasks.length>1 && orderType !== 4 && orderType !== 5" field="taskName" title="工单任务" align="left" :edit-render="{ name: 'input' }">
            <template v-slot:edit="{ row }">
              <a-select
                v-model="row.orderTaskId"
                style="width: 100%"
                @change="taskSelectChange"
                @blur="$refs.listTable ? $refs.listTable.clearValidate() : null"
              >
                <a-select-option v-for="(task, index) in tasks" :value="task.id" :key="index">
                  {{ task.taskName }}
                </a-select-option>
              </a-select>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="code"
            title="物料编码"
            :edit-render="{ name: 'input' }"
            :filters="[{ data: '' }]"
            :filter-method="customStringFilterMethod"
            width="130"
          >
            <template v-slot:edit="{ row }">
              <a-select
                ref="formSelect"
                v-model="row.code"
                placeholder="请选择物料"
                :open="false"
                style="width: 100%"
                @focus="openMaterialSelectModal(row)"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="name"
            title="物料名称"
            :edit-render="{ name: 'input' }"
            align="left"
            header-align="center"
            width="200"
          >
            <template v-slot:edit="{ row }">
              {{ row.name }}
            </template>
          </vxe-table-column>
          <vxe-table-column field="spec" title="规格" align="left" header-align="center"></vxe-table-column>

          <vxe-table-column field="systemName"  title="所属系统" width="150" :edit-render="{ name: 'input' }" v-if="orderType !== 4 && orderType !== 5">
            <template v-slot:edit="{ row }">
              <j-dict-select-tag
                v-model="row.systemId"
                :dictCode="systemDictCode"
                style="width: 100%"
                @blur="$refs.listTable.clearValidate()"
                :allowClear="false"
                @select="onSystemChange"
              />
            </template>
          </vxe-table-column>
          <vxe-table-column field="workstationName" title="所属工位" width="150" :edit-render="{ name: 'input' }"  v-if="orderType !== 4 && orderType !== 5">
            <template v-slot:edit="{ row }">
              <a-select
                v-model="row.workstationName"
                placeholder="请选择工位"
                :open="false"
                style="width: 100%"
                @dropdownVisibleChange="openStationSelectModal()"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </template>
          </vxe-table-column>

          <vxe-table-column field="useCategory_dictText" title="类别" :edit-render="{ name: 'input' }" width="100">
            <template v-slot:edit="{ row }">
              <!-- jakgong 修改，工单中的额定物资，不可以修改类别，其他的都可以修改，  :disabled="row.useCategory == '1'"   :hiddenArray="[1]" :disabled="row.opType === 1" -->
              <j-dict-select-tag
                v-model="row.useCategory"
                dictCode="bu_material_type"
                style="width: 100%"
                :hiddenArray="[-1]"
                @blur="$refs.listTable.clearValidate()"
                :allowClear="false"
                @select="onCategoryChange"
              />
            </template>
          </vxe-table-column>
          <vxe-table-column field="opType_dictText" title="领用类型" width="100" v-if="false"></vxe-table-column>
          <vxe-table-column v-if="false" field="needDispatchin_dictText" title="需发料?"  :edit-render="{ name: 'input' }" width="100" >
            <template v-slot:edit="{ row }">
              <j-dict-select-tag
                v-model="row.needDispatchin"
                dictCode="bu_state"
                style="width: 100%"
                @select="onDispatchChange"
                @blur="$refs.listTable.clearValidate()"
                :allowClear="false"
                :disabled="true"
              />
            </template>
          </vxe-table-column>
          <vxe-table-column field="amount" title="所需数量" :edit-render="{ name: 'input' }" width="120" align="right" header-align="center">
            <template v-slot:edit="{ row }">
              <a-input-number v-model="row.amount" :defaultValue="1" :min="0" :max="999999" style="width: 100%" />
            </template>
          </vxe-table-column>
          <!-- 发料工单 工单查看时 才需要看发放数量、分配数量-->
          <vxe-table-column v-if="orderType === 4 && operator === -1" field="issueAmount" title="发放数量" width="120" align="right" header-align="center"></vxe-table-column>
<!--          <vxe-table-column v-if="orderType === 4 && operator === -1" field="assignAmount" title="分配数量" width="120" align="right" header-align="center"></vxe-table-column>-->
          <!-- 非工单提交时， 只有工单查看、工单关闭、工单检查时才需要显示实际消耗， 还有发料工单也不看-->
          <vxe-table-column v-if="orderType !== 4 && orderType !== 5 && (operator === -1 || operator===5 || operator === 6)" field="actAmount" title="实际消耗" width="120" align="right" header-align="center"></vxe-table-column>
          <vxe-table-column v-if="operator === 1 && showStock" field="stock" title="当前库存" width="120" align="right" header-align="center"></vxe-table-column>
          <vxe-table-column field="unit" title="单位" width="80">
            <template v-slot:edit="{ row }">
              {{ row.unit }}
            </template>
          </vxe-table-column>
          <vxe-table-column v-if="orderType !== 4 && orderType !== 5 && (operator === -1 || operator===5 || operator === 6)" field="remark" title="消耗备注" width="120" align="right" header-align="center"></vxe-table-column>
          <vxe-table-column field="sourceLocationAndPalletName" title="发放信息" width="380" v-if="operator!==0 && operator!==1 && (orderType === 4 || orderType === 5 || orderType === 1)"></vxe-table-column>
          <!--      <vxe-table-column field="remark" title="备注" :edit-render="{name: 'input'}">
                <template v-slot:edit="{row}">
                  <a-input placeholder="请输入备注" v-model="row.remark" />
                </template>
              </vxe-table-column>-->
          <vxe-table-column field="canReplace" title="可替换物资" width="150" v-if="operator === 1 && orderType !==4 && orderType !== 5">
            <template v-slot="{row}">
              <a-tooltip placement="top">
                <template slot="title">
                  <span>{{row.canReplaceSpec}}</span>
                </template>
                <span>{{row.canReplace}}</span>
              </a-tooltip>
            </template>
          </vxe-table-column>

          <vxe-table-column title="操作" width="120" v-if="getButtonShow()">
            <template v-slot="{ row }">
              <template v-if="$refs.listTable.isActiveByRow(row)">
                <a-space>
                  <a-button type="dashed" size="small" @click.stop="saveRowEvent(row)">保存</a-button>
                  <a-button type="dashed" size="small" @click.stop="cancelRowEvent(row)">取消</a-button>
                </a-space>
              </template>
              <template v-else>
                <a-button type="dashed" size="small" @click.stop="editRowEvent(row)">编辑</a-button>
                <!-- <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>-->
              </template>
            </template>
          </vxe-table-column>
        </vxe-table>
        <!-- 提交工单时显示 -->
        <vxe-table
          v-else
          border
          ref="listTable"
          align="center"
          max-height="auto"
          :data.sync="taskMaterials"
          :edit-rules="validRules"
          :keep-source="true"
          :checkbox-config="{ highlight: true, range: true }"
          :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
          :filter-config="{ showIcon: false }"
          @blur="$refs.listTable ? $refs.listTable.clearValidate() : null"
        >
          <vxe-table-column type="seq" fixed="left" width="40"></vxe-table-column>
          <vxe-table-column type="checkbox" fixed="left" width="40"></vxe-table-column>
          <vxe-table-column v-if="tasks && tasks.length>1 && orderType !== 4" field="taskName" title="工单任务" align="left"> </vxe-table-column>
          <vxe-table-column
            field="code"
            title="物料编码"
            :filters="[{ data: '' }]"
            :filter-method="customStringFilterMethod"
            width="130"
          >
          </vxe-table-column>
          <vxe-table-column field="name" title="物料名称" align="left" header-align="center" width="200"> </vxe-table-column>
          <vxe-table-column field="opType_dictText" title="领用类型" width="100" v-if="orderType !==4"></vxe-table-column>
          <vxe-table-column field="systemName"  title="所属系统" width="150"  v-if="orderType !==4"></vxe-table-column>
          <vxe-table-column field="workstationName" title="所属工位" width="150" v-if="orderType !==4"></vxe-table-column>
          <vxe-table-column field="useCategory_dictText" title="类别" width="100"> </vxe-table-column>
          <vxe-table-column field="amount" title="所需数量" width="120" align="right" header-align="center"> </vxe-table-column>
          <vxe-table-column
            v-if="operator!==0 && operator!==1"
            width="120"
            field="actAmount"
            title="实际消耗"
            :edit-render="{ name: '$input', props: { type: 'number', min: 0 } }"
            align="right" header-align="center"
          ></vxe-table-column>
          <vxe-table-column field="unit" title="单位" width="80"> </vxe-table-column>
          <vxe-table-column v-if="operator!==0 && operator!==1" field="remark" title="消耗备注" width="120"> </vxe-table-column>
          <vxe-table-column field="spec" title="规格" align="left" header-align="center"></vxe-table-column>
          <vxe-table-column field="sourceLocationAndPalletName" title="发放信息" width="380" v-if="operator!==0 && operator!==1 && (orderType === 4 || orderType === 1)"></vxe-table-column>
          <vxe-table-column title="操作" width="120" v-if="getButtonShow() || operator === 4">
            <template v-slot="{ row }">
              <template v-if="$refs.listTable.isActiveByRow(row)">
                <a-space>
                  <a-button type="dashed" size="small" @click.stop="saveRowEvent(row)">保存</a-button>
                  <a-button type="dashed" size="small" @click.stop="cancelRowEvent(row)">取消</a-button>
                </a-space>
              </template>
              <template v-else>
                <a-button type="dashed" size="small" @click.stop="editRowEvent(row)">编辑</a-button>
                <!-- <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>-->
              </template>
            </template>
          </vxe-table-column>
        </vxe-table>
      </div>
    </div>
    <!-- 发放管理 -->
    <div v-if="operator === 2">
      <apply-ready-component
        v-if="workOrderId"
        ref="applyReady"
        :business-key="workOrderId"
        :fromFlow="true"
        :order-type="orderType"
        @ok="saveOk"
        @fail="saveFail"
        @cancel="onCancel"
        @loaded="onLoaded"
      ></apply-ready-component>
    </div>
    <!-- 领料确认 -->
    <div v-if="operator === 3">
      <apply-confirm-component
        v-if="workOrderId"
        ref="applyConfirm"
        :business-key="workOrderId"
        :fromFlow="true"
        :order-type="orderType"
        @ok="saveOk"
        @fail="saveFail"
        @cancel="onCancel"
        @loaded="onLoaded"
      ></apply-confirm-component>
    </div>
    <material-list ref="modalForm" :repairProgramId="repairProgramId" :lineId="lineId" :multiple="false" :mode="selectMode" :group-id="groupId" :type-code-list="mTypeCode" :show-type-code="filterType" @ok="onSelectMaterial"></material-list>
    <material-list ref="modalFormMultiple" :repairProgramId="repairProgramId" :lineId="lineId" :multiple="true" :mode="selectMode" :group-id="groupId" :type-code-list="mTypeCode" :show-type-code="filterType" @ok="onSelectMaterialMultiple"></material-list>
    <task-select-modal ref="taskSelectModal" @ok="onSelectTask"></task-select-modal>
    <work-station-list ref="stationSelectModal"  @ok="onSelectStation" :group-id="groupId"></work-station-list>
  </div>
</template>

<script>
import MaterialList from '@views/tiros/common/selectModules/MaterialList'
import TaskSelectModal from '@views/tiros/dispatch/workOrder/TaskSelectModal'
import ApplyReadyComponent from '@views/tiros/material/apply/ApplyReadyComponent'
import ApplyConfirmComponent from '@views/tiros/material/apply/ApplyConfirmComponent'
import { randomUUID } from '@/utils/util'
import { getTrainPlanDetail } from '@api/tirosDispatchApi'
import {getLineInfo} from '@api/tirosApi'
import WorkStationList from '../../common/selectModules/WorkStationList'

export default {
  name: 'TaskMaterials',
  components: { TaskSelectModal, MaterialList, ApplyReadyComponent, ApplyConfirmComponent, WorkStationList },
  props: {
    taskMaterials: {
      type: Array,
      default: () => {
        return []
      }
    },
    tasks: {
      type: Array,
      default: () => {
        return []
      }
    },
    readOnly: {
      type: Boolean,
      default: false
    },
    selectTask: {
      type: Object,
      default: null
    },
    operator: {
      type: Number,
      default: 0
    },
    workOrderId: {
      type: String,
      default: null
    },
    orderType: {
      type: Number,
      default: null
    },
    lineId: {
      type: [Number, String],
      default: null
    },
    planId: {
      type: [Number, String],
      default: null
    },
    repairId: {
      type: [Number, String],
      default: null
    },
    groupId: {
      type: [Number, String],
      default: null
    }
  },
  watch:{
    orderType:{
      handler(){
        if (this.operator ===0 || this.operator === 1) {
          // 核实时，发料工单可以选择所要物料， 非发料工单只能选择其他物料
          // console.log('核实时，发料工单可以选择所要物料， 非发料工单只能选择其他物料')
          if (this.orderType === 4 || this.orderType === 5 ) {
            this.selectMode = [1,2]
            this.mTypeCode=[2,3,-1]
            this.filterType=true
            /*if(this.$store.getters.userInfo.departIsGroup) {
              this.groupId = this.$store.getters.userInfo.departId
            }*/
          } else {
            this.selectMode = [1,2];
            if(this.operator === 1){
              this.selectMode = [3,...this.selectMode]
            }
            this.mTypeCode=[2,3,-1]
            this.filterType=true
          }
        }
      },
      immediate: true,
    },
    repairId:{
      immediate: true,
      handler(val) {
        this.repairProgramId = val;
      }
    },
    planId:{
      immediate: true,
      handler(val) {
        if((this.selectMode.indexOf(2) > -1) && val && !this.repairId){
          // 获取计划信息及任务信息
          getTrainPlanDetail(val).then((res) => {
            if(res.success && res.result){
              this.repairProgramId = res.result.repairProgramId
            }
          })
        }
      },
    },
  },
  data() {
    return {
      systemDictCode: '',
      repairProgramId:'',
      showStock: false,
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curRow: null,
      validRules: {
        // taskName: [{ required: true, message: '请选工单任务', trigger: 'manual' }],
        code: [{ required: true, message: '请选择物料', trigger: 'manual' }],
        opType: [{ required: false }],
        opType_dictText: [{ required: false }],
        useCategory_dictText: [{ required: true, message: '类别必须选择', trigger: 'manual' }],
        needDispatchin_dictText: [{ required: true, message: '是否发料必须选择', trigger: 'manual' }],
        amount: [{ required: true, message: '数量必须填写', trigger: 'manual' }],
        systemName:[{ required: true, message: '所属系统必须选择', trigger: 'manual'}],
        workstationName:[{ required: true, message: '所属工位必须选择', trigger: 'manual'}]
      },
      tableHeight: {
        top: 'height:100%',
        bottom: 'height: calc(100vh - 160px)',
        size: 'auto',
      },
      searchText: '',
      searchCategory: null,
      selectMode: [1,2,3],
      mTypeCode: [-1],
      filterType: false,
      trainTypeId: ''
    }
  },
  mounted() {
    console.log('this.operator = ' + this.operator )

    if(this.operator !== 2 && this.operator !== 3) {
      this.$nextTick(() => {
        this.sortTable()
      })
      // 不是发料或领料
      this.$emit('loaded')
    }

    //
    if(this.lineId) {
      getLineInfo({ lineId: this.lineId }).then((res) => {
        if (res.success) {
          this.trainTypeId = res.result.trainTypeId
          //
          this.systemDictCode = 'bu_train_asset_type,name,id,train_type_id=\'' + this.trainTypeId + '\' AND struct_type=1'
        } else {
          this.systemDictCode = ''
        }
      })
    }

  },
  methods: {
    onLoaded (data) {
      // 发料或领料的相关数据已加载了
      this.$emit('loaded')
    },
    onCategoryChange(value, option) {
      let record = this.$refs.listTable.getActiveRecord()
      if (option) {
        record.row.useCategory_dictText = option.title
      } else {
        record.row.useCategory_dictText = undefined
      }

      this.$forceUpdate()
    },
    onSystemChange (value, option) {
      let record = this.$refs.listTable.getActiveRecord()
      record.row.systemId=value
      if (option) {
        record.row.systemName = option.title
      } else {
        record.row.systemName = undefined
      }

      this.$forceUpdate()
    },
    onDispatchChange (value, option) {
      let record = this.$refs.listTable.getActiveRecord()
      if (option) {
        record.row.needDispatchin_dictText = option.title
      } else {
        record.row.needDispatchin_dictText = undefined
      }

      this.$forceUpdate()
    },
    save(onlySave) {
      // 备料确认
      if (this.operator === 2) {
        if (this.$refs.applyReady && this.$refs.applyReady.save) {
          this.$refs.applyReady.save(onlySave)
        } else {
          this.$emit('fail', new Error('数据尚未加载完成，请稍后操作！'))
        }
        return
      }
      // 领用确认
      if (this.operator === 3) {
        if (this.$refs.applyConfirm && this.$refs.applyConfirm.save) {
          this.$refs.applyConfirm.save(onlySave)
        } else {
          this.$emit('fail', new Error('数据尚未加载完成，请稍后操作！'))
        }
      } else {
        this.$emit('fail', new Error('无效的操作类型，请联系关联员！'))
      }
    },
    saveOk() {
      this.$emit('ok')
    },
    saveFail(e) {
      this.$emit('fail', e)
    },
    onCancel () {
      this.$emit('cancel')
    },
    getButtonShow() {
      if (this.readOnly) {
        return false
      }
      if (this.operator === 0 || this.operator === 1 || this.operator === 2) {
        return true
      } else {
        return false
      }
    },
    handleAdd() {
      if (this.$refs.listTable.getActiveRecord()) {
        return
      }
      this.curEditMode = 1;
      this.$refs.modalFormMultiple.showModal()
      // let material = {
      //   id: randomUUID(),
      //   useCategory: null,
      // }
      // if (this.selectTask) {
      //   Object.assign(material, {
      //     orderTaskId: this.selectTask.id,
      //     taskName: this.selectTask.taskName,
      //     opType: 1,
      //     opType_dictText: '额定物资',
      //   })
      // } else {
      //   if (this.tasks.length) {
      //     Object.assign(material, {
      //       orderTaskId: this.tasks[0].id,
      //       taskName: this.tasks[0].taskName,
      //     })
      //   }
      // }

      // this.$refs.listTable.insertAt(material, -1).then(({ row }) => {
      //   this.$refs.listTable.setActiveCell(row, 'code')
      // })
    },
    handleDel() {
      let m = this.$refs.listTable.getCheckboxRecords()
      // 只要以生成的都不能删除 if (m.find((e) => e.opType === 2 && e.isGenOrder === 1)) {
      if (m.find((e) => e.isGenOrder === 1)) {
        this.$message.warn('已生成单据的临时新增类型数据不能删除')
        return
      }
      // 这里不需要判断啊，没生成都可以删除，m = m.filter((e) => e.opType === 1 || (e.opType === 2 && e.isGenOrder === 1))
      if (m.length > 0) {
        this.$confirm({
          content: `是否删除选中${m.length}条数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            m.map((item1) => {
              this.taskMaterials.map((item2, index2) => {
                if (item1.id === item2.id) {
                  this.taskMaterials.splice(index2, 1)
                }
              })
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    editRowEvent(row) {
      this.curEditMode = 2
      this.curRow = row
      this.$refs.listTable.setActiveRow(row)
    },
    delRowEvent(row) {
      this.taskMaterials.map((item2, index2) => {
        if (row.id === item2.id) {
          this.taskMaterials.splice(index2, 1)
        }
      })
    },
    openStationSelectModal() {
      this.$refs.stationSelectModal.showModal()
    },
    onSelectStation(records){
      let record = this.$refs.listTable.getActiveRecord()
      if (records.length && record) {
        record.row.workstationId = records[0].id
        record.row.workstationName=records[0].name
      }
      this.$forceUpdate()
    },
    saveRowEvent(row) {
      this.$refs.listTable.validate(row, (valid) => {
        if (!valid) {
          if (row.actAmount > row.amount) {
            this.$message.error('实际消耗不能大于所需数量')
            return
          }
/*          row.opType = 1
          row.opType_dictText = '额定物资'*/
          if (this.curEditMode === 1) {
            this.taskMaterials.push(row)
          }
          this.$refs.listTable.clearActived()
          this.curEditMode = 0
        } else {
          for (const validKey in valid) {
            let vals = valid[validKey]
            vals.forEach((item) => {
              if (item.rule) {
                this.$message.error(item.rule.message)
              }
            })
          }
        }
      })
    },
    cancelRowEvent(row) {
      this.$refs.listTable.clearActived()
      if (this.curEditMode === 1) {
        // 新增,点击取消
        this.$refs.listTable.remove(row)
      } else if (this.curEditMode === 2) {
        // 还原行数据
        this.$refs.listTable.revertData(row)
      }
      this.curEditMode = 0
    },
    openMaterialSelectModal(row) {
      this.curRow = row
      this.$refs.modalForm.showModal()
      this.$refs.formSelect.blur()
      this.$refs.listTable.clearValidate()
    },
    onSelectMaterial(data) {
      if (data && data.length > 0) {
        const material = data[0]

        data.forEach((element, index) => {
          if (this.taskMaterials.find((e) => e.materialTypeId === element.id)) {
            this.$message.warn(`你选择的${element.name}已经在列表中存在`)
            return
          }
          if (index > 0) {
            let newRow = {
              id: randomUUID(),
              orderTaskId: this.curRow.orderTaskId,
              taskName: this.curRow.taskName,
              opType: 2,
              opType_dictText: '临时新增',
              needDispatchin: this.orderType === 4 || this.orderType === 5 ? 1 : 0,  // 非发料工单添加的物资都不需要发放，要申请走发料工单
              needDispatchin_dictText: this.orderType === 4 || this.orderType === 5 ? '是' : '否',
              code: element.code,
              materialTypeId: element.id,
              name: element.name,
              kind: element.kind,
              unit: element.unit,
              kind_dictText: element.kind_dictText,
              useCategory: element.category1 ? element.category1 : null,
              useCategory_dictText: element.category1 ? element.category1_dictText : '',
              amount: element.num,
              actAmount: 0,
            }
            this.taskMaterials.push(newRow)
          } else {
            this.curRow.code = material.code
            this.curRow.name = material.name
            this.curRow.materialTypeId = material.id;
           /* 编辑时不要修改原有的类型 this.curRow.opType = 2
            this.curRow.opType_dictText = '额定物资'*/

            this.curRow.kind = material.kind
            this.curRow.unit = material.unit
            this.curRow.kind_dictText = material.kind_dictText
            this.curRow.useCategory = material.category1 ? material.category1 : null
            this.curRow.useCategory_dictText = material.category1 ? material.category1_dictText : ''
            this.curRow.amount = material.num
            this.curRow.actAmount = 0
            if (data.length > 1) {
              this.$refs.listTable.clearActived()
              if (this.curEditMode === 1) {
                this.taskMaterials.push(this.curRow)
              }
              this.curEditMode = 0
            }
          }
        })
      } else {
        this.curRow.code = ''
        this.curRow.materialTypeId = ''
        this.curRow.name = ''
        this.curRow.kind = ''
        this.curRow.kind_dictText = ''
        this.curRow.useCategory = ''
        this.curRow.useCategory_dictText = ''
        this.curRow.unit = ''
        this.curRow.amount = 0
        this.curRow.actAmount = 0
        this.curRow.needDispatchin = 1
        this.needDispatchin_dictText = '是'
      }
      // this.$refs.listTable.validate(this.$refs.listTable.getActiveRecord().row, (errMap) => {})
      // this.$refs.listTable.clearValidate()
    },
    onSelectMaterialMultiple(data){
      if (data && data.length > 0) {
        const taskMaterials = this.taskMaterials;
        data.forEach((element, index) => {
          let newRow = {
            id: randomUUID(),
            opType: 1,  // 新增工单、核实时添加的物料都默认为 额定物料吧
            opType_dictText: '额定物资',
            needDispatchin: this.orderType === 4 || this.orderType === 5 ? 1 : 0,  // 非发料工单添加的物资都不需要发放，要申请走发料工单
            needDispatchin_dictText: this.orderType === 4 || this.orderType === 5 ? '是' : '否',
            code: element.code,
            materialTypeId: element.id,
            name: element.name,
            kind: element.kind,
            unit: element.unit,
            kind_dictText: element.kind_dictText,
            useCategory: element.category1 ? element.category1 : null,
            useCategory_dictText: element.category1 ? element.category1_dictText : '',
            amount: element.num,
            actAmount: 0,
            isGenOrder: 0,
            spec: element.spec
          }

          if (taskMaterials.find((e) => e.materialTypeId === element.id)) {
            if(this.orderType === 4 || this.orderType ===5) {
              this.$message.warn(`你选择的${element.name}已经在列表中存在`)
              return
            } else {
              this.$confirm({
                title: '确认添加',
                content: `你选择的${element.name}已经在列表中存在，是否重复添加？`,
                onOk: () => {
                  this.insertNewMaterial(newRow)
                }
              })
            }
          }else{
            this.insertNewMaterial(newRow)
          }
        })
      }
    },
    // 插入新的物资
    insertNewMaterial (newMaterial) {
      if (this.selectTask) {
        Object.assign(newMaterial, {
          orderTaskId: this.selectTask.id,
          taskName: this.selectTask.taskName,
          /*                opType: 1,
                          opType_dictText: '额定物资',*/
        })
      } else {
        if (this.tasks.length) {
          Object.assign(newMaterial, {
            orderTaskId: this.tasks[0].id,
            taskName: this.tasks[0].taskName,
          })
        }
      }
      // 添加后，直接添加，取消编辑状态
      this.$refs.listTable.insertAt(newMaterial, -1)
      this.taskMaterials.push(newMaterial)
      /* if(data.length == 1){
         this.$refs.listTable.insertAt(newRow, -1).then(({ row }) => {
           this.$refs.listTable.setActiveCell(row, 'code')
         })
       }else{
         this.$refs.listTable.insertAt(newRow, -1)
         this.taskMaterials.push(newRow)
       }*/
    },
    openTaskSelectModal(row) {
      this.curRow = row
      this.$refs.taskSelectModal.showModal(this.tasks)
      this.$refs.taskSelect.blur()
    },
    onSelectTask(tasks) {
      if (tasks && tasks.length > 0) {
        let task = tasks[0]
        this.curRow.orderTaskId = task.id
        this.curRow.taskName = task.taskName
      } else {
        this.curRow.orderTaskId = ''
        this.curRow.taskName = ''
      }
    },
    taskSelectChange(value, option) {
      this.$refs.listTable.getActiveRecord().row.taskName = this.tasks[option.key].taskName
      // this.$refs.listTable.validate(this.$refs.listTable.getActiveRecord().row, (errMap) => {})
      // this.$refs.listTable.clearValidate()
    },
    clearValidate() {
      this.$nextTick(()=>{
        this.$refs.listTable && this.$refs.listTable.clearValidate()
      })
    },
    checkMaterialShow({ row }) {
      return ''
    },
    sortTable() {
      // const listTable = this.$refs.listTable
      if (this.$refs.listTable) {
        const column = this.$refs.listTable.getColumnByField('code')
        const option = column.filters[0]
        option.checked = true
        this.$refs.listTable.updateData()
      }
    },
    customStringFilterMethod({ row }) {
      const reg = new RegExp(this.searchText)
      const regName = new RegExp(this.searchText)
      if (this.operator !== 0 && this.operator !== 1) {
        //  非新建、核实时，只显示额定，临时新增且已生成领料单的
        return (reg.test(row.code) || regName.test(row.name)) &&
          (row.useCategory == this.searchCategory || !this.searchCategory) &&
          (row.opType === 1 || (row.opType === 2 && row.isGenOrder === 1))
      } else {
        // 新增、核实工单时，显示额定、临时的物资
        return (reg.test(row.code) || regName.test(row.name)) &&
          (row.useCategory == this.searchCategory || !this.searchCategory) &&
          (row.opType === 1 || row.opType === 2)
      }
    },
    refreshTable () {
      // this.$refs.listTable.reloadData(data)
      this.showStock=true
      this.$refs.listTable.updateData()
    },
    rowClassName ({ row, rowIndex }) {
      if (row.noenough && row.noenough === 1) {
        return "row-red"
      }
    },
  },
}
</script>

<style>
.mytable-style .vxe-body--row.row-red {
  background-color: #f6a1a1 !important;
}
</style>