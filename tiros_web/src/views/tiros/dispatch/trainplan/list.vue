<template>
  <div>
    <div class='table-page-search-wrapper'>
      <a-form layout='inline' @keyup.enter.native='findList'>
        <a-row :gutter='24'>
          <a-col :md='4' :sm='24'>
            <a-form-item label='名称'>
              <a-input placeholder='请输入计划名称或车辆序号' v-model='queryParam.searchText' allow-clear></a-input>
            </a-form-item>
          </a-col>
          <!--<a-col :md="3" :sm="24">
            <a-form-item label="线路">
              <j-dict-select-tag
                v-model="queryParam.lineId"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>-->
          <a-col :md='4' :sm='24'>
            <a-form-item label='车辆'>
              <j-dict-select-seach-tag v-model='queryParam.trainNo'
                                       :dictCode="'bu_train_info,train_no,train_no,status=1'" />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='进度' class='a-form-item-width-gy'>
              <j-dict-select-tag v-model='queryParam.progressStatus' :dictCode="'bu_progress_status'" />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='审批'>
              <j-dict-select-tag v-model='queryParam.status' :dictCode="'bu_repair_plan_status'" />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='8'>
            <span style='float: left' class='table-page-search-submitButtons'>
              <a-space>
                <a-button @click='findList'>查询</a-button>
              </a-space>
            </span>
          </a-col>
          <a-col :md='24' :sm='8'>
            <span style='float: left' class='table-page-search-submitButtons'>
              <a-space>
                <a-button type='primary' @click='handleAdd' v-has="'train:plan:add'">新增</a-button>
                <a-button v-has="'train:plan:task'" type='dashed' @click='taskManage' :disabled='!btnStatus.beforeFlow'
                >任务管理</a-button
                >
                <a-button @click='linkForms' :disabled='!btnStatus.beforeFlow'
                          v-has="'train:plan:forms'">关联表单</a-button>
                <a-button
                  type='dashed'
                  @click='handleEdit(btnStatus.editRow)'
                  v-has="'train:plan:delete'"
                  :disabled='!btnStatus.edit'
                >编辑</a-button
                >
                <a-button type='dashed' @click='deleteRecord' :disabled='!btnStatus.del' v-has="'train:plan:delete'"
                >删除</a-button
                >
                <a-button type='dashed' :disabled='!btnStatus.stop' @click='stopRecord(1)'>
                  暂停
                </a-button>
                <a-button type='dashed' :disabled='!btnStatus.start' @click='stopRecord(2)'>
                  激活
                </a-button>
                <a-button
                  type='dashed'
                  :disabled='!btnStatus.flowEnd'
                  @click='taskAddOrderView = true'
                  :loading='generating'
                  v-has="'train:plan:gen'"
                >生成工单</a-button>
                <a-button @click='materialOrderModal=true' :disabled='!btnStatus.flowEnd'
                          v-has="'train:plan:gen:material'">创建发料工单</a-button>
                <a-button v-has="'train:plan:progress'" type='primary'
                          v-show='selectedRow && selectedRow.progress < 100 && selectedRow.progressStatus !== 0'
                          @click='$refs.verifyProgress.show(selectedRow)'>进度核实</a-button>
                <a-upload
                  style='margin-left: 8px'
                  name='file'
                  :multiple='false'
                  :customRequest='customRequestHistoryPlan'
                  :showUploadList='false'
                >
                  <a-button :loading='loading'>导入历史计划</a-button>
                </a-upload>
                <a-upload
                  style='margin-left: 8px'
                  name='file'
                  :multiple='false'
                  :customRequest='customRequestHistoryCost'
                  :showUploadList='false'
                >
                  <a-button v-has="'train:plan:importHistoryCost'"
                            v-show='selectedRow'
                            :loading='loading'>导入历史成本</a-button>
                </a-upload>

                  <a-dropdown>
                    <a-button>下载导入模板</a-button>
                    <a-menu slot='overlay'>
                      <a-menu-item>
                        <a @click.stop='downloadImportTemplate(1)' style='margin-left: 3px'>历史计划</a>
                      </a-menu-item>
                      <a-menu-item>
                        <a @click.stop='downloadImportTemplate(2)' style='margin-left: 3px'>历史成本</a>
                      </a-menu-item>
                    </a-menu>
                  </a-dropdown>
              </a-space>
            </span>
            <ProcessButtons
              ref='bts'
              v-if='selectedRow && 0 === selectedRow.historyData'
              v-has="'train:plan:workflow'"
              @StartSuccess='onStartSuccess'
              @StartFailure='onStartFailure'
              @handleSuccess='onHandleSuccess'
              @cancelSuccess='refreshList'
              :solution-code="'TRAINPLAN_APPROVED'"
              :business-key='selectedRow.id'
              :business-title='selectedRow.planName'
              :process-instance-id='selectedRow.processInstanceId'
              :variables='variables'
            ></ProcessButtons>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style='height: calc(100vh - 270px)'>
      <vxe-table
        border
        height='100%'
        ref='listTable'
        :align="'center'"
        :loading='loading'
        :data='tableData'
        :cell-style='cellStyle'
        show-overflow='tooltip'
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        @checkbox-change='rowSelectChange'
        @checkbox-all='btnStatus.update()'
      >
        <vxe-table-column type='checkbox' width='40'></vxe-table-column>
        <vxe-table-column field='planName' title='计划名称' align='left' header-align='center' width='18%'>
          <template v-slot='{ row }'>
            <a @click.stop='handleRead(row)'>{{ row.planName }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field='trainIndex'
          title='总列次'
          align='right'
          header-align='center'
          width='80'
        ></vxe-table-column>
        <vxe-table-column
          field='itemNo'
          title='修程次数'
          align='right'
          header-align='center'
          width='80'
        ></vxe-table-column>
        <vxe-table-column
          field='trainNo'
          title='车辆'
          align='center'
          header-align='center'
          width='8%'
        ></vxe-table-column>
        <vxe-table-column
          field='startDate'
          title='计划开始'
          align='center'
          header-align='center'
          width='100px'
        ></vxe-table-column>
        <vxe-table-column
          field='finishDate'
          title='计划完成'
          align='center'
          header-align='center'
          width='100px'
        ></vxe-table-column>
        <vxe-table-column
          field='duration'
          title='计划工期'
          align='center'
          header-align='center'
          width='8%'
        ></vxe-table-column>
        <vxe-table-column
          field='progressStatus_dictText'
          title='进度状态'
          align='left'
          header-align='center'
          width='110px'
        ></vxe-table-column>
        <vxe-table-column title='当前进度' align='left' header-align='center' width='220'>
          <template v-slot='{ row }'>
            <!--<div style="margin: 2px; background-color: #eeeeee; border: 1px solid #cdcdcd; width: 100%;height: 24px;">
              <div style="margin: 1px; background-color: #46cd11; border: 0px; width: 50%;height: 20px; line-height: 20px;">
                {{row.progress}}%
              </div>
            </div>-->
            <div class='progress' :title="'当前进度：'+row.progress+'%'">
              <span class='bar' :style="{ width: row.progress + '%',...progressStatusColor[row.progressStatus] }"
              >{{ row.progress ? `${row.progress}%` : '' }}</span
              >
              <!-- <span
                v-else
                class="bar"
                :style="{ width: row.progress + '%', 'justify-content': 'right', color: '#494848' }"
                >{{ row.progress }}%</span
              > -->
            </div>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field='wfStatus'
          title='当前审批'
          align='center'
          header-align='center'
          width='220'
        ></vxe-table-column>
        <vxe-table-column
          field='processCandidate'
          title='当前处理人'
          align='left'
          header-align='center'
          width='160'
        ></vxe-table-column>
        <vxe-table-column
          field='actStart'
          title='实际开始'
          align='center'
          header-align='center'
          width='100px'
        ></vxe-table-column>
        <vxe-table-column
          field='actFinish'
          title='实际完成'
          align='center'
          header-align='center'
          width='8%'
        ></vxe-table-column>
        <vxe-table-column
          field='actDuration'
          title='实际工期'
          align='center'
          header-align='center'
          width='8%'
        ></vxe-table-column>
        <vxe-table-column
          field='lineName'
          title='所属线路'
          align='center'
          header-align='center'
          width='8%'
        ></vxe-table-column>
        <vxe-table-column field='depotName' title='所属车辆段' width='150'></vxe-table-column>
        <vxe-table-column field='workshopName' align='left' header-align='center' title='所属车间'
                          width='150'></vxe-table-column>
        <!-- <vxe-table-column title="操作" width="150" align="left" header-align="center" fixed="right">
          <template v-slot="{ row }">
            <a-button
              class="margin-left8"
              size="small"
              type="dashed"
              @click.stop="handleEdit(row)"
              v-show="row.wfStatus === '未发起'"
              >编辑</a-button
            >
            <a-button class="margin-left8" size="small" type="dashed" @click.stop="handleRead(row)">查看</a-button>
          </template>
        </vxe-table-column> -->
      </vxe-table>
    </div>
    <vxe-pager
      perfect
      :loading='loading'
      :current-page.sync='queryParam.pageNo'
      :page-size.sync='queryParam.pageSize'
      :total='totalResult'
      :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
      @page-change='handlePageChange'
    ></vxe-pager>
    <a-modal
      centered
      :width="'100%'"
      title='列计划任务管理'
      dialogClass='fullDialog  no-footer'
      :visible='taskManageVisible'
      @cancel='taskManageVisible = false'
      :footer='null'
      :destroyOnClose='true'
    >
      <taskManage v-if='taskManageVisible' :planInfo='curPlan' :visible.sync='taskManageVisible'></taskManage>
    </a-modal>
    <!--    <vxe-modal v-if="taskManageVisible" v-model="taskManageVisible" title="计划任务管理" min-width="800" min-height="600" :showHeader="false" fullscreen resize show-overflow >
      <template v-slot>
        <taskManage :planInfo="curPlan" :visible.sync="taskManageVisible"></taskManage>
      </template>
    </vxe-modal>-->
    <!--    <vxe-modal v-if="planViewVisible" v-model="planViewVisible" title="列计划查看" min-width="800" min-height="600" :showHeader="false" fullscreen resize show-overflow >
      <template v-slot>
        <viewDetail :businessKey="curPlan.id" :visible.sync="planViewVisible"></viewDetail>
      </template>
    </vxe-modal>-->
    <a-modal
      centered
      :width="'100%'"
      :title='`列计划明细查看 - 总任务数：${detailData.total}，已完成：${detailData.finished}，未完成：${detailData.unFinished}`'
      dialogClass='fullDialog no-footer'
      :visible='planViewVisible'
      @cancel='planViewVisible = false'
      :footer='null'
      :destroyOnClose='true'
    >
      <viewDetail v-if='planViewVisible' :businessKey='curPlan.id' :visible.sync='planViewVisible'></viewDetail>
    </a-modal>
    <!-- 生成工单模态框 -->
    <a-modal
      :title="'生成工单'"
      :visible='taskAddOrderView'
      @ok='addOrder'
      @cancel='taskAddOrderView = false'
      cancelText='关闭'
      :destroyOnClose='true'
    >
      <div style='padding: 18px'>
        <span>生成日期： </span>
        <a-date-picker v-model='addWorkOrderDate' format='YYYY-MM-DD' />
      </div>
    </a-modal>
    <a-modal
      :title="'创建领料工单'"
      :visible='materialOrderModal'
      @ok='createMaterialOrder'
      @cancel='materialOrderModal = false'
      cancelText='关闭'
      :destroyOnClose='true'
    >
      <div style='padding: 18px'>
        <a-form-model ref='morderForm' :model='materialOrderForm' :rules='rules'>
          <a-form-model-item label='工班选择' :labelCol='{ span: 5 }' :wrapperCol='{ span: 19 }' prop='groups'>
            <a-select mode='multiple' v-model='materialOrderForm.groups' placeholder='请选择'>
              <a-select-option v-for='item in groupList' :key='item.value' :value='item.value'>
                {{ item.text || item.label }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
        </a-form-model>
      </div>
    </a-modal>
    <edit
      ref='editForm'
      v-if='editVisible'
      :visible.sync='editVisible'
      :plan='curPlan'
      :title='title'
      @save_success='saveSuccess'
    ></edit>
    <plan-forms ref='planForms'></plan-forms>
    <verifyProgress ref='verifyProgress' @close='closeVerify' />
  </div>
</template>

<script>
import edit from '@views/tiros/dispatch/trainplan/edit'
import verifyProgress from '@/views/tiros/dispatch/trainplan/verifyProgress.vue'
import {
  addTrainOrderGenrate,
  createMaterialOrder,
  delTrainPlan,
  getTrainPlanList,
  getUnFinishList,
  importHistoryPlanFromExcel,
  importPlanHistoryCostDataFromExcel,
  startTrainPlan,
  stopTrainPlan
} from '@api/tirosDispatchApi'
import taskManage from '@views/tiros/dispatch/trainplan/taskManage'
import viewDetail from '@views/tiros/dispatch/trainplan/viewDetail'
import ProcessButtons from '@views/workflow/ProcessButtons'
import PlanForms from '@views/tiros/dispatch/trainplan/PlanForms'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'
import { ajaxGetDictItems } from '@api/api'
import { download } from '@api/tirosFileApi'

export default {
  name: 'list',
  components: { edit, taskManage, viewDetail, ProcessButtons, PlanForms, verifyProgress },
  data() {
    return {
      detailData: {
        finished: 0,
        total: 0,
        unFinished: 0
      },
      progressStatusColor: {
        // 未开始
        '0': {
          backgroundColor: '#A9A9A9',
          color: '#fff'
        },
        // 正常(作业中)
        '1': {
          backgroundColor: '#1E90FF',
          color: '#fff'
        },
        // 逾期(作业中)
        '2': {
          backgroundColor: '#ee8c60',
          color: '#fff'
        },
        // 正常(完工)
        '3': {
          backgroundColor: '#008000',
          color: '#fff'
        },
        // 逾期(完工)
        '4': {
          backgroundColor: '#bccb1c',
          color: '#fff'
        },
        // 提前(完工)
        '5': {
          backgroundColor: '#1cff00',
          color: '#fff'
        },
        // 暂停中
        '6': {
          backgroundColor: '#817659',
          color: '#fff'
        }
      },
      dictGroupStr: 'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
      materialOrderForm: {
        groups: []
      },
      groupList: [],
      defaultGroups: [],
      materialOrderModal: false,
      rules: {
        groups: [
          { required: true, message: '请选择工班', trigger: 'blur' }
        ]
      },
      generating: false,
      selectedRow: null,
      variables: {},
      taskManageVisible: false,
      planViewVisible: false,
      editVisible: false,
      title: '列计划新增',
      taskAddOrderView: false,
      totalResult: 0,
      loading: false,
      queryParam: {
        searchText: '',
        lineId: '',
        trainNo: '',
        progressStatus: '',
        status: '',
        pageNo: 1,
        pageSize: 10
      },
      addWorkOrderDate: this.$moment(new Date()),
      tableData: [],
      btnStatus: new TableBtnUtil(this, 'listTable', {
        attrs: [
          {
            key: 'edit',
            judge: (e) => e.wfStatus === '未发起'
          },
          {
            key: 'stop',
            judge: (e) => e.wfStatus === '已结束'
          },
          {
            key: 'start',
            judge: (e) => e.progressStatus === 6
          },
          {
            key: 'beforeFlow',
            judge: (e) => e.wfStatus === '未发起'
          },
          {
            key: 'flowEnd',
            judge: (e) => e.wfStatus === '已结束' && (e.progressStatus === 0 || e.progressStatus === 1 || e.progressStatus === 2)
          }
        ]
      }),
      curPlan: {}
    }
  },
  computed: {
    showTaskAddOrderBtn() {
      if (this.selectedRow) {
        return this.selectedRow.wfStatus === '已结束'
      }
      return false
    }
  },
  mounted() {
    this.findList()
    this.loadGroup()
  },
  methods: {
    cellStyle({ row, rowIndex, column, columnIndex }) {
      if (['progressStatus_dictText'].indexOf(column.property) > -1) {
        let style = this.progressStatusColor[row.progressStatus]
        return { color: style.backgroundColor }
      }
    },
    createMaterialOrder() {
      this.$refs.morderForm.validate(valid => {
        if (valid) {
          // planId: this.selectedRow.id,
          let params = {
            planId: this.selectedRow.id,
            groupIdList: this.materialOrderForm.groups
          }
          // console.log('params:', params)
          createMaterialOrder(params).then(res => {
            // console.log(res)
            if (res.success) {
              this.$message.success(res.message)
              this.materialOrderModal = false
            } else {
              this.$message.error(res.message)
            }
          })
        }
      })
    },
    findList() {
      this.loading = true
      this.selectedRow = null
      getTrainPlanList(this.queryParam)
        .then((res) => {
          if (res.success) {
            this.totalResult = res.result.total
            this.tableData = res.result.records
          } else {
            this.$message.error('加载列计划列表失败')
            console.error('加载列计划列表失败：', res.message)
          }
          this.loading = false
          this.btnStatus.update()
        })
        .catch((err) => {
          this.loading = false
          console.error('加载列计划列表异常：', err)
        })
    },
    loadGroup() {
      ajaxGetDictItems(this.dictGroupStr, null).then((res) => {
        if (res.success) {
          this.materialOrderForm.groups = []
          res.result.forEach(item => {
            if (item.value !== 'wwlwb') {
              this.materialOrderForm.groups.push(item.value)
            }
          })
          this.groupList = res.result
        }
      })
    },
    handleAdd() {
      this.curPlan = {}
      this.title = '列计划新增'
      this.editVisible = true
    },
    handleEdit(row) {
      this.curPlan = row
      this.title = '列计划编辑'
      this.editVisible = true
    },
    handleRead(row) {
      getUnFinishList({ planId: row.id }).then((res) => {
        // console.log(res);
        if (res.success) {
          this.selectRecords = []
          this.detailData = { ...res.result }
          this.curPlan = row
          this.planViewVisible = true
        }
      })
    },
    stopRecord(type) {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      // console.log()
      const ids = selectRecords.map((item) => {
        return item.id //条件;
      }).join(',')
      // console.log(ids)
      this.$confirm({
        content: `确认要${type === 1 ? '暂停' : '激活'}该列计划吗？`,
        okText: '确认',
        cancelText: '取消',
        onOk: () => {
          const request = type === 1 ? stopTrainPlan : startTrainPlan
          return request(`ids=${ids}`).then((res) => {
            if (res.success) {
              this.$message.success(res.message)
              this.findList()
            } else {
              this.$message.warning(res.message)
            }
          })
        },
        onCancel: () => {
        }
      })

    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      // let flag =
      //   selectRecords.filter((r) => {
      //     return r.wfStatus !== '未发起'
      //   }).length > 0
      // if (flag) {
      //   this.$message.warn('审批中或已审批的数据不能删除')
      //   return
      // }
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}条数据？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function(obj, index) {
                return obj.id
              })
              .join(',')
            delTrainPlan('ids=' + idsStr)
              .then((res) => {
                if (res.success) {
                  this.$message.success('删除成功')
                  this.findList()
                } else {
                  console.error('删除列计划失败：', res.message)
                  this.$message.error('删除失败')
                }
              })
              .catch((err) => {
                console.error('删除列计划异常：', res.message)
                this.$message.error('删除异常')
              })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handlePageChange() {
      this.findList()
    },
    //保存列计划成功
    saveSuccess() {
      this.findList()
    },
    closeVerify(data) {
      if (data) {
        this.findList()
      }
    },
    taskManage() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length === 1) {
        this.curPlan = selectRecords[0]
        this.taskManageVisible = true
        // this.$router.push(`/tiros/basic/plantemplate/task/${selectRecords[0].id}`)
      } else {
        this.$message.error('请选择一条数据!')
      }
    },
    rowSelectChange() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords && selectRecords.length === 1) {
        this.selectedRow = selectRecords[0]
      } else {
        this.selectedRow = null
      }
      this.btnStatus.update()
    },
    onStartSuccess(data) {
      this.refreshList()
    },
    onStartFailure(data) {
      console.log('启动失败:', data)
    },
    onHandleSuccess(data) {
      this.refreshList()
    },
    addOrder() {
      if (this.addWorkOrderDate) {
        this.generating = true

        addTrainOrderGenrate({
          date: this.addWorkOrderDate.format('YYYY-MM-DD'),
          generateEarlierOrder: true,
          planId: this.selectedRow.id,
          startFlow: true
        }).then((res) => {
          this.generating = false
          if (res.success) {
            this.$message.success(res.result)
          } else {
            this.$message.error(res.message)
          }
        })
      }
      this.taskAddOrderView = false
    },
    refreshList() {
      this.findList()
      this.selectedRow = null
    },
    linkForms() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.length > 1) {
          this.$message.error('只能对一条记录进行操作!')
          return
        }
        this.$refs.planForms.show(selectRecords[0])
      } else {
        this.$message.error('请选择要关联表单的列计划!')
      }
    },
    customRequestHistoryPlan(data) {
      // 上传提交
      this.loading = true
      const formData = new FormData()
      formData.append('excelFile', data.file)
      this.saveFileHistoryPlan(formData)
    },
    saveFileHistoryPlan(formData) {
      importHistoryPlanFromExcel(formData).then((res) => {
        this.loading = false
        if (res.success) {
          this.$message.success(res.result)
          this.findList()
        } else {
          this.$message.error(res.message)
        }
      })
    },
    customRequestHistoryCost(data) {
      // 上传提交
      this.loading = true
      const formData = new FormData()
      formData.append('excelFile', data.file)
      this.saveFileHistoryCost(formData)
    },
    saveFileHistoryCost(formData) {
      console.log('formData this.selectedRow.id=', this.selectedRow.id)
      formData.append('planId', this.selectedRow.id)
      importPlanHistoryCostDataFromExcel(formData).then((res) => {
        this.loading = false
        if (res.success) {
          this.$message.success(res.result)
          this.findList()
        } else {
          this.$message.error(res.message)
        }
      })
    },
    downloadImportTemplate(type) {
      let filePath = ''
      if (type === 1) {
        filePath = '/共享文件/导入模板/导入历史列计划/导入历史列计划模板.xlsx'
      } else if (type === 2) {
        filePath = '/共享文件/导入模板/导入历史成本/导入历史成本模板.xlsx'
      } else {
        this.$message.warn('未知的导入模板类型')
      }
      download(filePath)
    }
  }
}
</script>

<style>
.a-form-item-width-gy .ant-col.ant-form-item-control-wrapper {
  width: calc(100% - 77px);
}
</style>
<style scoped>
.progress {
  overflow: hidden;
  width: 100%;
  height: 24px;
  margin: 2px;
  background-color: #eae6e6;
  background-image: -moz-linear-gradient(top, #f5f5f5, #e0dede);
  background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#f5f5f5), to(#e0dede));
  background-image: -webkit-linear-gradient(top, #f5f5f5, #e0dede);
  background-image: -o-linear-gradient(top, #f5f5f5, #e0dede);
  background-image: linear-gradient(to bottom, #f5f5f5, #e0dede);
  background-repeat: repeat-x;
  /* filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#fff5f5f5', endColorstr='#fff9f9f9', GradientType=0); */
  -webkit-box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
  -moz-box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1);
  -webkit-border-radius: 4px;
  -moz-border-radius: 4px;
  border-radius: 4px;
}

.progress .bar {
  width: 0%;
  height: 100%;
  /* color: #ffffff; */
  float: left;
  font-size: 10px;
  text-align: center;
  align-items: center;
  display: flex;
  justify-content: center;
  text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25);
  /* background-color: #34d209;
  background-image: -moz-linear-gradient(top, #34d209, #2e9e0f);
  background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#34d209), to(#2e9e0f));
  background-image: -webkit-linear-gradient(top, #34d209, #2e9e0f);
  background-image: -o-linear-gradient(top, #34d209, #2e9e0f);
  background-image: linear-gradient(to bottom, #34d209, #2e9e0f);
  background-repeat: repeat-x;
  filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ff149bdf', endColorstr='#ff0480be', GradientType=0); */
  -webkit-box-shadow: inset 0 -1px 0 rgba(0, 0, 0, 0.15);
  -moz-box-shadow: inset 0 -1px 0 rgba(0, 0, 0, 0.15);
  box-shadow: inset 0 -1px 0 rgba(0, 0, 0, 0.15);
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-transition: width 0.6s ease;
  -moz-transition: width 0.6s ease;
  -o-transition: width 0.6s ease;
  transition: width 0.6s ease;
}
</style>