<template>
  <div id="myWorkContent">
    <na-splitter :defaultSize="350" style="height: calc(100vh - 120px)" @resize="resizeSplitter">
      <div slot="left-panel" style="overflow-y: auto; overflow-x: hidden; padding-right: 2px">
        <div class="titleBar bg-primary-1">任务搜索</div>
        <div class="table-page-search-wrapper">
          <a-form layout="inline">
            <a-row :gutter="12">
              <a-col :md="12" :sm="24">
                <a-form-item>
                  <a-date-picker format="YYYY-MM-DD" style="width: 100%" v-model="queryParam.startDate" />
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item>
                  <j-dict-select-tag
                    v-model="queryParam.status"
                    dictCode="bu_order_task_status"
                    placeholder="请选择状态"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item style="width: 100%">
                  <div @click="openPlanModel">
                    <a-select
                      allow-clear
                      placeholder="请选择列计划"
                      :open="false"
                      :showArrow="true"
                      v-model="queryParam.planName"
                      ref="myPlanSelect"
                      @change="changePlanSelect"
                    >
                      <a-icon slot="suffixIcon" type="ellipsis" />
                    </a-select>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item class="work-order-select-cont">
                  <span @click="openOrderModel">
                    <a-select
                      allow-clear
                      placeholder="请选择工单"
                      :open="false"
                      :showArrow="true"
                      v-model="queryParam.orderName"
                      ref="myOrderSelect"
                      @change="changeOrderSelect"
                    >
                      <a-icon slot="suffixIcon" type="ellipsis" />
                    </a-select>
                  </span>
                </a-form-item>
              </a-col>
              <a-col :md="24" :sm="24">
                <a-form-item>
                  <a-input placeholder="请输入任务名称" v-model="queryParam.taskName" allow-clear></a-input>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row>
              <a-form-item>
                <a-button type="primary" @click="loadTaskList" block>搜索</a-button>
              </a-form-item>
            </a-row>
          </a-form>
        </div>
        <div class="titleBar bg-primary-1">任务列表</div>
        <div class="list-wrapper">
          <vxe-table
            :stripe="true"
            border
            row-id="id"
            ref="listTable"
            :align="allAlign"
            :data="tableDataTask"
            max-height="100%"
            show-overflow="tooltip"
            :radio-config="{ trigger: 'row', highlight: true, reserve: true, checkRowKey: defaultSelectTaskRow }"
            @radio-change="radioChangeEvent"
          >
            <vxe-table-column type="radio" width="35"></vxe-table-column>
            <vxe-table-column field="taskName" title="任务名称" align="left" width="195"></vxe-table-column>
            <!-- <vxe-table-column field="progress" title="进度" align="left" width="60"></vxe-table-column>-->
            <vxe-table-column field="taskStatus_dictText" title="状态" align="left" width="65"></vxe-table-column>
            <vxe-table-column field="trainNo" title="车号" align="left" width="65"></vxe-table-column>
            <vxe-table-column field="orderCode" title="工单号" align="left" width="80"></vxe-table-column>
            <vxe-table-column field="orderName" title="工单名称" align="left" width="165"></vxe-table-column>
          </vxe-table>
        </div>
      </div>
      <div slot="right-panel" style="padding-left: 5px; padding-right: 3px">
        <a-tabs size="small">
          <a-tab-pane key="2" tab="作业要求">
            <div class="requireBox">
              <div class="requireTitle"><span> 安全要求</span></div>
              <p class="safeContent" v-if="requirement && requirement.safeNotice">
                {{ requirement.safeNotice }}
              </p>
            </div>
            <div class="requireBox">
              <div class="requireTitle"><span> 技术要求</span></div>
              <!--              <p v-if="requirement && requirement.requirement">{{ requirement.requirement }}</p>-->
              <p class="safeContent" v-if="requirement && requirement.requirement">
                {{ requirement.requirement }}
              </p>
            </div>
            <div class="requireBox">
              <div class="requireTitle"><span> 安全预想</span></div>
              <p class="safeContent" v-if="requirement && requirement.safetyExpectation" v-html="requirement.safetyExpectation"></p>
            </div>
          </a-tab-pane>
          <a-tab-pane key="1" tab="作业表单">
            <a-tabs size="small" v-if="taskFormsList.length" :defaultActiveKey="0" @change="selectForm">
              <a-tab-pane
                v-for="(form, formIndex) in taskFormsList"
                :key="formIndex"
                :tab="form.formName"
                :forceRender="true"
              >
                <div v-if="form.formType === 3">
                  <div class="buttonDiv">
                    <!-- 1 自检  2 互检  3 专检    4  抽检   5 过程检  6 过程检确认  7 交接检  8 交接检确认 9 专工验收 -->
                    <a-space>
                      <a-button @click="handleCheck(4, '抽检')">抽检</a-button>
                      <a-button @click="handleCheck(5, '过程检')">过程检</a-button>
                      <a-button v-has="'quailty:check6'" @click="handleCheck(6, '过程检确认')">过程检确认</a-button>
                      <a-button @click="handleCheck(7, '交接检')">交接检</a-button>
                      <a-button v-has="'quailty:check8'" @click="handleCheck(8, '交接检确认')">交接检确认</a-button>
                      <a-button v-has="'quailty:check9'" @click="handleCheck(9, '专工验收')">专工验收</a-button>
                      <a-dropdown>
                        <a-menu slot="overlay" @click="moreBtnClick">
                          <a-menu-item key="1"> 故障提报 </a-menu-item>
                          <a-menu-item key="2"> 设为开口项 </a-menu-item>
                          <a-menu-item key="3"> 创建整改项 </a-menu-item>
                        </a-menu>
                        <a-button>
                          更多
                          <a-icon type="down" />
                        </a-button>
                      </a-dropdown>
                    </a-space>
                  </div>
                  <vxe-table
                    :stripe="true"
                    border
                    auto-resize
                    row-id="id"
                    ref="recordListTable"
                    :align="allAlign"
                    v-if="curRecordForm"
                    :data="curRecordForm.detailList"
                    show-overflow="tooltip"
                    :edit-config="{ trigger: 'manual', mode: 'row' }"
                    :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
                  >
                    <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                    <vxe-table-column
                      field="checkLevel_dictText"
                      title="控制点"
                      align="center"
                      width="70"
                    ></vxe-table-column>
                    <vxe-table-column field="workContent" title="检修内容" align="left" width="250"></vxe-table-column>
                    <vxe-table-column field="result_dictText" title="结果" align="center" width="80"></vxe-table-column>
                    <vxe-table-column field="selfCheck" title="自检" align="center" width="80"></vxe-table-column>
                    <vxe-table-column field="guarderCheck" title="互检" align="center" width="80"></vxe-table-column>
                    <vxe-table-column field="monitorCheck" title="专检" align="center" width="80"></vxe-table-column>
                    <vxe-table-column field="workInfo" title="作业情况" align="center"></vxe-table-column>
                  </vxe-table>
                </div>
                <div v-if="form.formType === 1" style="height: calc(100vh - 210px)">
                  <div id="luckysheet" style="margin: 0px; padding: 0px; width: 100%; height: 100%"></div>
                </div>
              </a-tab-pane>
              <a-select
                v-if="curFormIndexList && curFormIndexList.length > 1"
                v-model="curCopyIndex"
                @change="changeFormIndex"
                slot="tabBarExtraContent"
                style="width: 200px"
              >
                <a-select-option
                  v-for="(rec, recordIndex) in curFormIndexList"
                  :key="rec.formIndex"
                  :value="recordIndex"
                >
                  {{ rec.formIndex }}-{{ rec.name }} {{ ' -' + rec.assetName }}
                </a-select-option>
              </a-select>
            </a-tabs>
            <div style="margin: 20px" v-if="!taskFormsList.length">无须填写作业记录</div>
          </a-tab-pane>
          <a-tab-pane key="4" tab="物料消耗">
            <div class="tableHeight">
              <vxe-table border ref="MaterialListTable" :align="allAlign" :data="materials" show-overflow="ellipsis">
                <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                <vxe-table-column field="code" title="物料编码" width="15%"></vxe-table-column>
                <vxe-table-column field="name" title="物料名称" width="15%"></vxe-table-column>
                <vxe-table-column field="amount" title="所需数量" width="15%"></vxe-table-column>
                <vxe-table-column
                  field="actAmount"
                  title="实际消耗"
                  align="left"
                  :edit-render="{ name: '$input', props: { type: 'number' } }"
                ></vxe-table-column>
                <vxe-table-column field="unit" title="单位" align="left" width="15%"></vxe-table-column>
                <vxe-table-column
                  field="remark"
                  title="消耗备注"
                  align="left"
                  :edit-render="{ name: '$input', props: { type: 'text' } }"
                ></vxe-table-column>
                <!-- <vxe-table-column title="操作" width="120">
                   <template v-slot="{ row }">
                     <template v-if="$refs.MaterialListTable.isActiveByRow(row)">
                       <a-space>
                         <a-button type="dashed" size="small" @click="saveMaterialRowEvent(row)">保存</a-button>
                         <a-button type="dashed" size="small" @click="cancelMaterialRowEvent(row)">取消</a-button>
                       </a-space>
                     </template>
                     <template v-else>
                       <a-button type="dashed" :disabled="!selectTask && selectTask.taskStatus === 2" size="small" @click="editMaterialRowEvent(row)">编辑</a-button>
                       &lt;!&ndash; <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>&ndash;&gt;
                     </template>
                   </template>
                 </vxe-table-column>-->
              </vxe-table>
            </div>
          </a-tab-pane>
          <a-tab-pane key="5" tab="工器具">
            <div class="tableHeight">
              <vxe-table
                border
                ref="ToolsListTable"
                :align="allAlign"
                :data="tools"
                show-overflow="ellipsis"
                :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
                :edit-config="{ trigger: 'click', mode: 'row' }"
              >
                <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                <vxe-table-column field="code" title="工器具类型编码"></vxe-table-column>
                <vxe-table-column field="name" title="工器具名称"></vxe-table-column>
                <vxe-table-column field="amount" title="所需数量"></vxe-table-column>
                <vxe-table-column field="unit" title="单位" align="left" width="15%"></vxe-table-column>
              </vxe-table>
            </div>
          </a-tab-pane>
          <a-tab-pane key="3" tab="必换件清单">
            <div class="tableHeight">
              <vxe-table
                border
                ref="MustListTable"
                :align="allAlign"
                :data="mustReplaces"
                show-overflow="ellipsis"
                :edit-config="{ trigger: 'click', mode: 'row' }"
              >
                <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                <vxe-table-column field="code" title="必换件编码" width="15%"></vxe-table-column>
                <vxe-table-column field="name" title="必换件名称" width="15%"></vxe-table-column>
                <vxe-table-column field="initNum" title="数量" width="15%"></vxe-table-column>
                <vxe-table-column field="unit" title="单位" align="left" width="15%"></vxe-table-column>
                <vxe-table-column field="changed" title="更换状态" align="left" width="15%"></vxe-table-column>
                <vxe-table-column
                  field="remark"
                  title="备注"
                  align="left"
                  :edit-render="{ name: '$input', props: { type: 'text' } }"
                ></vxe-table-column>
              </vxe-table>
            </div>
          </a-tab-pane>
          <a-tab-pane key="6" tab="工艺文件">
            <vxe-table
              border
              ref="fileListTable"
              :data="files"
              show-overflow="ellipsis"
              :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
            >
              <vxe-table-column title="操作" width="120">
                <template v-slot="{ row }">
                  <a-space>
                    <a-button type="dashed" size="small" @click="viewFile(row)">查看</a-button>
                    <a-button type="dashed" size="small" @click="downFile(row)">下载</a-button>
                  </a-space>
                </template>
              </vxe-table-column>
              <vxe-table-column field="fileName" title="文件名称" align="left"></vxe-table-column>
            </vxe-table>
          </a-tab-pane>
          <a-tab-pane key="7" tab="任务信息">
            <div style="padding: 20px">
              <div class="info-wrapper info-top-wrapper">
                <h4>工单信息</h4>
                <a-descriptions title="" bordered>
                  <a-descriptions-item label="工单编号">
                    {{ workOrderDetail.orderCode }}
                  </a-descriptions-item>
                  <a-descriptions-item label="工单类型">
                    {{ workOrderDetail.orderType_dictText }}
                  </a-descriptions-item>
                  <a-descriptions-item label="工单名称">
                    {{ workOrderDetail.orderName }}
                  </a-descriptions-item>
                  <a-descriptions-item label="作业班组">
                    {{ workOrderDetail.groupName }}
                  </a-descriptions-item>
                  <a-descriptions-item label="线路">
                    {{ workOrderDetail.lineName }}
                  </a-descriptions-item>
                  <a-descriptions-item label="车号">
                    {{ workOrderDetail.trainNo }}
                  </a-descriptions-item>
                  <a-descriptions-item label="开始日期">
                    {{ workOrderDetail.startTime }}
                  </a-descriptions-item>
                  <a-descriptions-item label="结束日期">
                    {{ workOrderDetail.finishTime }}
                  </a-descriptions-item>
                </a-descriptions>
              </div>
              <div class="info-wrapper info-top-wrapper">
                <h4>任务信息</h4>
                <a-descriptions title="" bordered>
                  <a-descriptions-item label="任务编码">
                    {{ taskInfo.taskNo }}
                  </a-descriptions-item>
                  <a-descriptions-item label="重要工序">
                    {{ taskInfo.important === 1 ? '是' : '否' }}
                  </a-descriptions-item>
                  <a-descriptions-item label="重要工序">
                    {{ taskInfo.outsource === 1 ? '是' : '否' }}
                  </a-descriptions-item>
                  <a-descriptions-item label="任务名称">
                    {{ taskInfo.taskName }}
                  </a-descriptions-item>
                  <a-descriptions-item label="作业手段">
                    {{ taskInfo.method_dictText }}
                  </a-descriptions-item>
                  <a-descriptions-item label="作业部件">
                    {{ taskInfo.assetTypeName }}
                  </a-descriptions-item>
                  <a-descriptions-item label="预计工时">
                    {{ taskInfo.workTime }}
                  </a-descriptions-item>
                  <a-descriptions-item label="任务描述">
                    {{ taskInfo.remark }}
                  </a-descriptions-item>
                </a-descriptions>
              </div>
            </div>
          </a-tab-pane>
          <a-tab-pane key="8" tab="附件">
            <vxe-table
              border
              align="center"
              ref="listTable"
              :data.sync="annexs"
              :keep-source="true"
              show-overflow="tooltip"
            >
              <vxe-table-column field="name" title="文件名称" align="left"></vxe-table-column>
              <vxe-table-column field="type" title="扩展名" width="150px"></vxe-table-column>
              <vxe-table-column field="size" title="文件大小(KB)" width="150px"></vxe-table-column>
              <vxe-table-column field="uid" title="-" width="150px">
                <template v-slot="{ row }">
                  <span v-if="row">
                    <a @click.stop="previewImage(row)">查看</a>
                    <a style="margin-left: 6px" @click.stop="handleDownload(row)">下载</a>
                  </span>
                </template>
              </vxe-table-column>
              <!-- <vxe-table-column field="savepath" title="文件路径" align="left"></vxe-table-column> -->
            </vxe-table>
          </a-tab-pane>
        </a-tabs>
      </div>
    </na-splitter>
    <train-plan-list ref="planModalForm" @ok="onSelectPlan"></train-plan-list>
    <work-order-select :order-status="[2]" ref="workOrderSelect" @ok="onSelectOrder"></work-order-select>
    <check-modal ref="checkModalForm" :title="checkModalTitle" @ok="onCheckRecord"></check-modal>
    <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
    <open-item-modal ref="openItemModal"></open-item-modal>
    <rectify-modal ref="rectifyModal"></rectify-modal>
    <BreakdownModal ref="breakdownModal"></BreakdownModal>
  </div>
</template>

<script>
import moment from 'moment'
import clone from 'clone'
import TrainPlanList from '@/views/tiros/common/selectModules/TrainPlanList'
import WorkOrderSelect from '@views/tiros/common/selectModules/WorkOrderSelect'
import checkModal from './checkModal'
import taskSubmitModal from '@views/tiros/group/myWork/taskSubmitModal'
import ToolsList from '@views/tiros/common/selectModules/ToolsList'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import {
  getTaskRecordForm,
  getTaskRelevanceInfo,
  getTrainPlanOnlineFormById,
  getTrainPlanRecordFormById,
  getWorkTaskList,
} from '@/api/tirosGroupApi'
import { getFormContent, getSerialNumber } from '@/api/tirosApi'
import OpenItemModal from '@views/tiros/quality/modules/OpenItemModal'
import RectifyModal from '@views/tiros/quality/modules/RectifyModal'
import BreakdownModal from '@views/tiros/dispatch/breakdown/BreakdownModal'
import NaSplitter from '@comp/tiros/Na-splitter'
import { download } from '@api/tirosFileApi'

export default {
  components: {
    NaSplitter,
    TrainPlanList,
    WorkOrderSelect,
    checkModal,
    taskSubmitModal,
    ToolsList,
    DocPreviewModal,
    OpenItemModal,
    RectifyModal,
    BreakdownModal,
  },
  data() {
    return {
      tableDataTask: [],
      serialList: [],
      workOrderList: [],
      // activeTab: '1',
      allAlign: 'center',
      queryParam: {
        planId: '',
        planName: '',
        startDate: '',
        status: null,
        taskName: '',
        orderName: '',
        workOrderId: '',
      },

      taskFormsList: [],
      defaultSelectTaskRow: '',
      selectTask: null,
      // 表示是该表单的第几份（一个表单可能要填多份）
      curCopyIndex: 0,
      // 表单序号（表示是该工单的第几个表单）
      curFormIndex: 0,
      // 当前选中表单
      curForm: null,
      // 当前表单可选分数列表
      curFormIndexList: [],
      // 当前作业记录表对象
      curRecordForm: null,
      // 当前在线表单
      curOnlineForm: null,

      requirement: {},
      materials: [],
      tools: [],
      files: [],
      fileName: '',
      mustReplaces: [],
      taskDetail: {},
      workOrderDetail: {},
      taskInfo: {},
      curDepartId: this.$store.getters.userInfo.departId,
      defaultOptions: {
        container: 'luckysheet', //luckysheet为容器id
        title: 'sheet',
        column: 26, // 列数
        row: 50, // 行数
        lang: 'zh', // 设定表格语言
        allowEdit: true, // 允许编辑
        showinfobar: false, // 名称栏
        sheetFormulaBar: false,
        showsheetbar: false, // 底部sheet页
        showstatisticBar: true, // 底部计数栏
        enableAddRow: false, // 允许添加行
        enableAddCol: false, // 允许添加列
        showtoolbar: false, // 是否第二列显示工具栏
        showtoolbarConfig: {
          save: true,
          undoRedo: true,
        },
      },
      colNames: [],
      checkModalTitle: '自检',
      annexs: [],
    }
  },
  created() {
    this.loadTaskList()
  },
  beforeDestroy() {
    // 离开销毁Luckysheet
    luckysheet.destroy()
  },
  mounted() {
    for (let i = 65; i < 91; i++) {
      this.colNames.push(String.fromCharCode(i))
    }
  },
  methods: {
    previewImage(row) {
      this.previewFileName = row.name
      this.$refs.docPreview.handleFilePath(row.savepath)
      return false
    },
    async handleDownload(data) {
      // await this.handlePrivilege(data.id, 3)
      // if (!this.status) {
      //   this.$message.error('您没有权限!')
      // } else {
      download(data.savepath)
      // }
    },
    moment,
    // 检查
    handleCheck(type, title) {
      // 当前有多个作业记录表时就需要判断是第几个作业记录表
      let tableIndex = -1
      for (let i = 0; i <= this.curFormIndex; i++) {
        if (this.taskFormsList[i].formType === 3) {
          tableIndex++
        }
      }
      let selectRecords = this.$refs.recordListTable[tableIndex].getCheckboxRecords()
      if (selectRecords.length > 0) {
        let idsStr = selectRecords
          .map((obj, index) => {
            return obj.id
          })
          .join(',')
        this.checkModalTitle = title
        this.$refs.checkModalForm.showModal(idsStr, type)
      } else {
        this.$message.error('请选择要检查的作业记录明细!')
      }
    },
    // 检查回调
    onCheckRecord(result) {
      if (result.saveSuccess) {
        this.loadRecordFormDetail()
      }
    },
    openPlanModel() {
      this.$refs.planModalForm.showModal()
    },
    openOrderModel() {
      this.$refs.workOrderSelect.showModal()
    },
    onSelectPlan(data) {
      this.queryParam.planId = data[0].id
      this.queryParam.planName = data[0].planName
    },
    changePlanSelect(value) {
      if (!value) {
        this.queryParam.planId = ''
      }
    },
    onSelectOrder(data) {
      this.queryParam.workOrderId = data[0].id
      this.queryParam.orderName = data[0].orderName
    },
    changeOrderSelect(value) {
      if (!value) {
        this.queryParam.workOrderId = ''
      }
    },
    // 加载任务列表
    loadTaskList() {
      this.queryParam.startDate = this.startDate ? moment(this.startDate).format('YYYY-MM-DD') : undefined
      this.queryParam.orderStatusList = [2, 3, 4, 5]
      this.queryParam.orderWorkStatusList = [1, 2]
      getWorkTaskList(this.queryParam).then((res) => {
        if (res.success) {
          this.tableDataTask = res.result
          if (this.tableDataTask && this.tableDataTask.length > 0) {
            this.defaultSelectTaskRow = res.result[0].id
            this.selectTask = res.result[0]
            // 获取作业填报相关表单
            this.getWriteForm(this.defaultSelectTaskRow)
            this.getTaskInfo(this.defaultSelectTaskRow)
          }
        } else {
          this.$message.warning(res.message)
        }
      })
    },
    // 任务选择改变
    radioChangeEvent({ row }) {
      // 这里要判断任务是否为委外任务，如果是委外，需要看委外的处理界面
      // row.outTask 0 不是委外任务 1 委外送修任务 2 委外接收任务 3 其他委外任务
      this.selectTask = row

      if (row.outTask === 0) {
        this.getWriteForm(row.id)
        this.getTaskInfo(row.id)
      }
      if (row.outTask === 1) {
        // 委外送修
      }
      if (row.outTask === 2) {
        // 委外接收
      }
    },
    // 获取任务及关联信息
    getTaskInfo(id) {
      getTaskRelevanceInfo(id).then((res) => {
        if (res.success) {
          this.materials = res.result.materials
          this.requirement = res.result.requirement
          this.tools = res.result.tools
          this.files = res.result.otherData
          this.mustReplaces = res.result.mustReplaces
          this.taskDetail = res.result.taskDetail
          this.workOrderDetail = res.result.workOrder
          this.annexs = res.result.annexList
          this.taskInfo = res.result.task
        }
      })
    },
    // 获取要填写的表单
    getWriteForm(id) {
      this.curForm = null
      this.taskFormsList = []
      this.curFormIndex = 0
      getTaskRecordForm({ taskId: id }).then((res) => {
        if (res.success) {
          this.taskFormsList = res.result
          // console.log('formList:', this.taskFormsList)
          // 设置当前表单明细
          this.selectForm(0)
        }
      })
    },
    // 当前表单改变
    selectForm(data) {
      this.curFormIndex = data
      if (this.taskFormsList && this.taskFormsList.length > 0) {
        this.curForm = this.taskFormsList[this.curFormIndex]
        this.initForm()
      }
    },
    // 设置当前选中表单相关数据
    initForm() {
      if (!this.curForm) {
        return
      }
      // console.log('curForm:', this.curForm)
      // 可选列表
      this.curCopyIndex = 0
      this.curFormIndexList = this.curForm.choiceList
      this.curRecordForm = null
      this.curOnlineForm = null
      this.loadCopyFormDetail()
    },
    // 改变当前表单的第几份？
    changeFormIndex(value) {
      luckysheet.destroy()
      console.log('select value:', value)
      this.curCopyIndex = value
      // this.curForm = this.taskFormsList[this.curFormIndex]
      this.loadCopyFormDetail()
    },
    // 加载第几份表单明细
    loadCopyFormDetail() {
      // 作业记录表
      if (this.curForm.formType === 3) {
        this.loadRecordFormDetail()
      }
      // 在线自定义表单
      if (this.curForm.formType === 1) {
        this.loadOnlineFormDetail()
      }
    },
    // 获取作业记录明细
    loadRecordFormDetail() {
      // 获取作业记录表明细
      const params = {
        task2InstId: this.curFormIndexList[this.curCopyIndex].id,
        formInstId: this.curFormIndexList[this.curCopyIndex].formInstId,
        needChecks: true,
        needCategory: false,
        orderTaskId: this.selectTask.id,
      }
      getTrainPlanRecordFormById(params)
        .then((res) => {
          if (res.success) {
            this.curRecordForm = res.result
          } else {
            this.$message.error('获取作业记录表内容错误')
            console.error('获取作业记录表内容错误', res.message)
          }
        })
        .catch((err) => {
          this.$message.error('获取作业记录表内容异常')
          console.error('获取作业记录表内容异常', err)
        })
    },
    //  获取在线表单明细
    loadOnlineFormDetail() {
      const params = {
        task2InstId: this.curFormIndexList[this.curCopyIndex].id,
        formInstId: this.curFormIndexList[this.curCopyIndex].formInstId,
        needValues: false,
      }
      getTrainPlanOnlineFormById(params)
        .then((re) => {
          if (re.success) {
            // this.curOnlineForm = res.result
            // if (this.curOnlineForm.result) {
            //   this.initSheet(JSON.parse(this.curOnlineForm.result))
            // }

            // 改成记录值里面只记录数据，excel的其他信息不记录
            this.curOnlineForm = re.result
            /* if (this.curOnlineForm.result) {
             this.initSheet(JSON.parse(this.curOnlineForm.result))
           } else {
             getFormContent(this.curOnlineForm.formObjId).then(res => {
               if (res.success) {
                 this.initSheet(JSON.parse(res.result))
               }
             })
           }*/
            let data = null
            if (this.curOnlineForm) {
              if (this.curOnlineForm.result) {
                try {
                  data = JSON.parse(this.curOnlineForm.result)
                } catch (e) {
                  this.$message.error('转换表单填写结果异常')
                  console.error('转换表单填写结果异常', this.curOnlineForm.result)
                  return
                }
              }

              getFormContent(this.curOnlineForm.formObjId).then((res) => {
                if (res.success) {
                  let sheet = JSON.parse(res.result)
                  if (data) {
                    sheet.data = []
                    sheet.celldata = luckysheet.transToCellData(data)
                  }

                  this.initSheet(sheet)
                } else {
                  this.$message.error('获取表单模板数据失败')
                  console.error('获取表单模板数据失败', res.message)
                }
              })
            } else {
              this.$message.error('获取作业表单数据失败')
              console.error('获取作业表单数据失败：', re.message)
            }
          } else {
            // 没有获取到记录表的结果， 获取原始表单定义
            /* getFormContent(this.curForm.objId).then(res => {
             if (res.success) {
               this.initSheet(JSON.parse( res.result))
             }
           })*/
            this.$message.error('获取记录表内容错误')
            console.error('获取记录表内容错误', re.message)
          }
        })
        .catch((err) => {
          this.$message.error('获取记录表内容异常')
          console.error('获取记录表内容异常', err)
        })
    },
    // 初始化自定义表单
    initSheet(sheet) {
      const options = clone(this.defaultOptions)
      // 任务已经完成的不能编辑
      /*if ( this.selectTask && this.selectTask.taskStatus === 2) {
        options.showtoolbarConfig={}
        sheet.config.authority.allowRangeList = []
      }*/
      // 任何情况都不能编辑
      options.showtoolbarConfig = {}
      sheet.config.authority.allowRangeList = []
      options.data = [sheet]

      luckysheet.destroy()
      luckysheet.create(options)

      // 能编辑情况下，绑定保存南
      /*if ( this.selectTask && this.selectTask.taskStatus !== 2) {
        setTimeout(() => {
          document.querySelector('#luckysheet-icon-save').addEventListener('click', () => {
            this.saveCustomForm()
          })
        }, 1000)
      }*/
    },
    resizeSplitter() {
      setTimeout(() => {
        // console.log('resize:', $('#luckysheet').width())
        if (this.curForm && this.curForm.formType === 1) {
          luckysheet.resize()
        }
      }, 100)
    },
    viewFile(file) {
      // let filePath = window._CONFIG['onlinePreviewDomainURL'] + '?url=' + encodeURIComponent(file.savepath)
      this.fileName = file.fileName
      this.$refs.docPreview.handleFilePath(file.savePath)
    },
    downFile(file) {
      try {
        /*let downer = document.createElement("a");
        downer.setAttribute('download', '');// download属性
        downer.setAttribute('href',window._CONFIG['minioUrl'] + file.savePath);// href链接
        downer.click()*/
        download(file.savePath)
      } catch (e) {
        this.$message.error('下载异常！')
      }
    },
    moreBtnClick(e) {
      switch (e.key) {
        case '1':
          // console.log('故障提报')
          this.createFault()
          break
        case '2':
          // console.log('设为开口项')
          this.createOpenItem()
          break
        case '3':
          // console.log('创建整改项')
          this.createRectify()
          break
        default:
          console.log('default')
          break
      }
    },
    createOpenItem() {
      // 当前有多个作业记录表时就需要判断是第几个作业记录表
      let tableIndex = -1
      for (let i = 0; i <= this.curFormIndex; i++) {
        if (this.taskFormsList[i].formType === 3) {
          tableIndex++
        }
      }

      let selectRecords = this.$refs.recordListTable[tableIndex].getCheckboxRecords()
      if (selectRecords.length > 0) {
        let names = selectRecords
          .map((obj, index) => {
            return obj.workContent
          })
          .join(',')
        this.$refs.openItemModal.add(this.selectTask.orderId, this.selectTask.id, names)
      } else {
        this.$message.error('请选择要设为开口先项的作业记录明细!')
      }
    },
    createFault() {
      // this.$refs.breakdownModal.add()
      // 当前有多个作业记录表时就需要判断是第几个作业记录表
      let tableIndex = -1
      for (let i = 0; i <= this.curFormIndex; i++) {
        if (this.taskFormsList[i].formType === 3) {
          tableIndex++
        }
      }

      let selectRecords = this.$refs.recordListTable[tableIndex].getCheckboxRecords()
      if (selectRecords.length > 0) {
        /*let names = selectRecords.map((obj, index)=> {
          return obj.workContent
        }).join(',')*/
        if (selectRecords.length > 1) {
          this.$message.error('只能选择一条作业记录明细创建故障!')
          return
        }
        this.$refs.breakdownModal.add(this.selectTask.orderId, this.selectTask.id,2)
      } else {
        this.$message.error('请选择要创建故障的作业记录明细!')
      }
    },
    createRectify() {
      // 当前有多个作业记录表时就需要判断是第几个作业记录表
      let tableIndex = -1
      for (let i = 0; i <= this.curFormIndex; i++) {
        if (this.taskFormsList[i].formType === 3) {
          tableIndex++
        }
      }

      let selectRecords = this.$refs.recordListTable[tableIndex].getCheckboxRecords()
      if (selectRecords.length > 0) {
        let names = selectRecords
          .map((obj, index) => {
            return obj.workContent
          })
          .join(',')

        getSerialNumber({ moduleCode: 'JDXZG' }).then((res) => {
          if (res.success) {
            this.$refs.rectifyModal.add(this.selectTask.orderId, this.selectTask.id, names, res.result)
          } else {
            this.$message.error(res.message)
          }
        })
      } else {
        this.$message.error('请选择要创建整改的作业记录明细!')
      }
    },
  },
}
</script>

<style>
#myWorkContent .work-order-select-cont .ant-form-item-control-wrapper {
  width: 100%;
}
</style>

<style lang="less">
#luckysheet-wa-editor {
  padding: 3px 0 3px 15px !important;
}

#myWorkContent {
  .table-page-search-wrapper {
    margin-top: 5px;
  }

  .list-wrapper {
    padding: 0px;
    height: calc(100vh - 390px);
    overflow-x: auto;
  }

  .titleBar {
    width: 100%;
    font-size: 14px;
    padding: 8px;
    text-align: center;
    font-weight: bold;
  }

  .buttonDiv {
    padding: 10px 0;
  }

  .requireBox {
    min-height: 200px;
    padding: 10px;

    .requireTitle {
      border-bottom: 1px solid #eee;
      font-size: 18px;
      font-weight: bold;
      padding: 5px;

      span {
        border-bottom: 5px solid red;
        padding: 5px;
      }

      margin-bottom: 10px;
    }

    .safeContent {
      font-size: 15px;
      white-space: pre-line;
    }
  }

  .luckysheet-stat-area {
    background-color: transparent !important;
  }

  .info-wrapper {
    border: 1px solid #eee;
    position: relative;
    border-radius: 8px;
    padding: 15px;
    padding-top: 20px;
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
}
</style>
