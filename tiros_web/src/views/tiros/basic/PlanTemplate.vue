<template>
  <div>
    <!-- 查询区域 -->
    <a-spin :tip="tipText" :spinning="syncFinancialLoading">
      <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="findList">
          <a-row :gutter="24">
            <a-col :md="5" :sm="24">
              <a-form-item label="名称或编码">
                <a-input placeholder="请输入内容" v-model="queryParam.searchContent" allowClear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="24">
              <a-form-item label="状态">
                <j-dict-select-tag v-model="queryParam.status" dictCode="bu_valid_status" />
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="24">
              <a-form-item label="修程">
                <j-dict-select-tag v-model="queryParam.repairProgramId" dictCode="bu_repair_program,name,id" />
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="24">
              <a-form-item label="车型">
                <j-dict-select-tag v-model="queryParam.trainTypeId" dictCode="bu_train_type,name,id" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col>
              <span class="table-page-search-submitButtons">
                <a-space>
                  <a-button @click="findList">查询</a-button>
                  <a-button type="primary" @click="handleAdd">新增模板</a-button>
                  <a-button :disabled="records.length != 1" @click="handleEdit(records[0])">编辑</a-button>
                  <a-button :disabled="records.length != 1" class="primary-color" @click="handleAddTask"
                    >任务管理</a-button
                  >
                  <a-button :disabled="records.length != 1" @click="linkForms">关联表单</a-button>
                  <a-button :disabled="records.length != 1" @click="copyAdd" :loading="loading">复制模板</a-button>
                  <a-button @click="syncFinancial">同步财务项目</a-button>
<!--                  <a-button :disabled="records.length < 1" @click="deleteRecord" v-has="'plantemplate:delete'"-->
<!--                    >删除</a-button-->
<!--                  >-->
                  <a-button :disabled="records.length < 1" @click="validRecord">启用</a-button>
                  <a-button :disabled="records.length < 1" @click="invalidRecord">禁用</a-button>
                  <a-button :loading="exportLoading" :disabled="records.length != 1" @click="exportGroupStock">导出关联数据</a-button>
                  <!--<a-button @click="importData">导入</a-button>-->
                </a-space>
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <div style="height: calc(100vh - 272px)">
        <vxe-table
          border
          ref="listTable"
          :align="allAlign"
          :data="tableData"
          max-height="100%"
          style="height: calc(100vh - 272px)"
          auto-resize
          show-overflow="tooltip"
          @checkbox-change="checkboxChange"
          @checkbox-all="checkboxChange"
          :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          :edit-config="{ trigger: 'manual', mode: 'row' }"
        >
          <vxe-table-column type="checkbox" width="40"></vxe-table-column>
          <vxe-table-column field="code" title="模板编号" width="150"></vxe-table-column>
          <vxe-table-column field="tpName" title="模板名称" header-align="center" align="left" min-width="250">
            <template v-slot="{ row }">
              <div>
                <a @click.stop="handleRead(row)">{{ row.tpName }}</a>
              </div>
            </template>
          </vxe-table-column>
          <vxe-table-column field="lineName" title="所属线路" width="100"></vxe-table-column>
          <vxe-table-column field="trainTypeName" title="车型" width="100"></vxe-table-column>
          <vxe-table-column field="groupQuantity" title="编组" width="80"></vxe-table-column>
          <vxe-table-column
            field="repairProgramName"
            align="left"
            header-align="center"
            title="修程类型"
            width="100"
          ></vxe-table-column>
          <vxe-table-column field="duration" title="工期" width="80"></vxe-table-column>
          <vxe-table-column field="status_dictText" title="状态" width="80">
            <template v-slot="{ row }">
              <div :style="{ backgroundColor: statusColor[row.status + ''], borderRadius: '4px' }">
                {{ row.status_dictText }}
              </div>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="reguName"
            align="left"
            header-align="center"
            title="关联规程"
            width="200"
          ></vxe-table-column>
          <vxe-table-column field="remark" title="备注" align="left" ></vxe-table-column>
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
    </a-spin>
    <a-modal
      centered
      title="任务管理"
      :width="'100%'"
      dialogClass="fullDialog  no-footer"
      :visible="taskManageVisible"
      @cancel="taskManageVisible = false"
      :footer="null"
      :destroyOnClose="true"
    >
      <TaskManageModal v-if="taskManageVisible" :planInfo="curPlan" :visible.sync="taskManageVisible"></TaskManageModal>
    </a-modal>
    <!--    <vxe-modal v-if="taskManageVisible" v-model="taskManageVisible" title="计划模版任务管理" min-width="800" min-height="600" :showHeader="false" fullscreen resize show-overflow >
          <template v-slot>
            <TaskManageModal :planInfo="curPlan" :visible.sync="taskManageVisible"></TaskManageModal>
          </template>
        </vxe-modal>-->
    <!--    <vxe-modal v-if="planViewVisible" v-model="planViewVisible" title="计划模版查看" min-width="800" min-height="600" :showHeader="false" fullscreen resize show-overflow >
          <template v-slot>
            <PlanDetailView :planInfoId="curPlan.id" :visible.sync="planViewVisible"></PlanDetailView>
          </template>
        </vxe-modal>-->
    <a-modal
      centered
      :width="'100%'"
      dialogClass="fullDialog no-title no-footer"
      :visible="planViewVisible"
      @cancel="planViewVisible = false"
      :footer="null"
      :destroyOnClose="true"
    >
      <Plan-detail-view v-if="planViewVisible" :planInfoId="curPlan.id" :visible.sync="planViewVisible"></Plan-detail-view>
    </a-modal>
    <!-- 计划模版编辑弹出 -->
    <PlanEditModal ref="modalForm" @ok="loadData()"></PlanEditModal>
    <PlanForms ref="planForms"></PlanForms>
    <import-modal ref="importModal" @ok="loadData()"></import-modal>
  </div>
</template>

<script>
import PlanEditModal from '@views/tiros/basic/modules/planTemplate/PlanEditModal'
import PlanDetailView from '@views/tiros/basic/modules/planTemplate/PlanDetailView'
import TaskManageModal from '@views/tiros/basic/modules/planTemplate/TaskManageModal'
import { getPlanList, delPlanTemplate, validPlanTemplate, invalidPlanTemplate, copyPlanTemplate, exportTpPlanInfo } from '@/api/tirosApi'
import PlanForms from '@views/tiros/basic/modules/planTemplate/PlanForms'
import ImportModal from '@views/tiros/basic/modules/planTemplate/ImportModal'
import axios from 'axios'

export default {
  components: { PlanEditModal, TaskManageModal, PlanDetailView, PlanForms, ImportModal },
  data() {
    return {
      tipText:'正在同步中...',
      exportLoading:false,
      records: [],
      loading: false,
      taskManageVisible: false,
      planViewVisible: false,
      statusColor: {
        0: '#dedede',
        1: '#bad795',
      },
      queryParam: {
        repairProgramId: '',
        trainTypeId: '',
        status: '',
        searchContent: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      curPlan: null,
      syncFinancialLoading: false,
    }
  },
  mounted() {
    this.findList()
    /* this.initDictData()*/
  },
  methods: {
    exportGroupStock () {
      const item = this.records[0]
      this.exportLoading = true;
      this.tipText = '正在生成导出数据，请耐心等待......'
      this.syncFinancialLoading = true;
      exportTpPlanInfo(item.tpName + '.xls', {
        tpPlanId: item.id
      }).then((res)=>{
        console.log(res)
        this.tipText = this.$options.data().tipText;
        this.syncFinancialLoading = false;
        this.exportLoading = false;
      })
    },
    checkboxChange(e) {
      this.records = e.records
    },
    // 加载表格数据
    findList() {
      this.loading = true
      getPlanList(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.records = []
          this.tableData = res.result.records
        }
      })
    },
    handlePageChange() {
      this.findList()
    },
    handleEdit(record) {
      // this.$router.push(`/tiros/basic/plantemplate/item/${record.id}`)
      this.curPlan = record
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
    },
    handleAdd() {
      // this.$router.push('/tiros/basic/plantemplate/item/add')
      this.curPlan = null
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleAddTask() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length == 1) {
        this.curPlan = selectRecords[0]
        this.taskManageVisible = true
        // this.$router.push(`/tiros/basic/plantemplate/task/${selectRecords[0].id}`)
      } else {
        this.$message.error('请选择一条数据!')
      }
    },
    handleRead(record) {
      // this.$router.push(`/tiros/basic/plantemplate/detail/${record.id}`)
      this.curPlan = record
      this.planViewVisible = true
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}条数据？`,
          onOk: () => {
            const idsStr = selectRecords
              .map(function (obj) {
                return obj.id
              })
              .join(',')
            delPlanTemplate({ ids: idsStr })
              .then((res) => {
                if (res.success) {
                  this.$message.success('删除成功')
                  this.findList()
                } else {
                  console.error('删除计划模版失败：', res.message)
                  this.$message.error('删除失败')
                }
              })
              .catch((err) => {
                console.error('删除计划模版异常：', res.message)
                this.$message.error('删除异常')
              })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    validRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.filter((r) => { return r.status === 1 }).length > 0) {
          this.$message.warn('有效的数据不能启用')
          return
        }
        this.$confirm({
          content: `是否启用选中的计划模版？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            validPlanTemplate('ids=' + idsStr)
              .then((res) => {
                if (res.success) {
                  this.$message.success('启用成功')
                  this.findList()
                } else {
                  this.$message.error( res.message)
                }
              })
              .catch((err) => {
                this.$message.error('启用异常')
                console.log('启用异常：', err)
              })
          },
        })
      } else {
        this.$message.error('尚未选中数据!')
      }
    },
    invalidRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.filter((r) => { return r.status === 0 }).length > 0) {
          this.$message.warn('无效的数据不能禁用')
          return
        }
        this.$confirm({
          content: `是否禁用选中的计划模版？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            invalidPlanTemplate('ids=' + idsStr)
              .then((res) => {
                if (res.success) {
                  this.$message.success('禁用成功')
                  this.findList()
                } else {
                  this.$message.error( res.message)
                }
              })
              .catch((err) => {
                this.$message.error('禁用异常')
                console.log('禁用异常：', err)
              })
          },
        })
      } else {
        this.$message.error('尚未选中数据!')
      }
    },
    loadData() {
      this.findList()
    },
    copyAdd() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.length > 1) {
          this.$message.error('一次只能复制一条计划模版!')
        } else {
          this.loading = true
          copyPlanTemplate({ id: selectRecords[0].id })
            .then((res) => {
              if (res.success) {
                this.$message.success('复制成功')
                this.loadData()
              } else {
                this.$message.error(res.message)
                console.error('复制模版失败：', res.message)
              }
            })
            .catch((err) => {
              this.$message.success('复制异常')
              console.error('复制模版异常：', err)
            })
            .finally(() => {
              this.loading = false
            })
        }
      } else {
        this.$message.error('请选择要复制的计划模版!')
      }
    },
    syncFinancial(){
      this.syncFinancialLoading = true;
      const url = window._CONFIG["syncMaximo"] + "/third/maximo/read/init/finance";
      axios({
        url: url,
        // headers: { "X-Access-Token": token },
        method: "GET",
      })
        .then((res) => {
          this.syncFinancialLoading = false;
          if (res.data.success) {
            this.$message.warn("同步成功");
          }
        })
        .catch((err) => {
          this.syncFinancialLoading = false;
          this.$notification.error({
            message: "服务器出错了",
            description: "Network Error",
            duration: 4,
          });
        });
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
        this.$message.error('请选择要关联表单的计划模版!')
      }
    },
    importData() {
      this.$refs.importModal.title = '计划模板导入'
      this.$refs.importModal.showModal()
    },
  },
}
</script>

<style>
</style>