<template>
  <a-modal
    width='95%'
    :title='showTitle'
    :visible='visible'
    :confirmLoading='loading'
    @ok='handleOk'
    @cancel='handleCancel'
    cancelText='关闭'
    centered
    :destroyOnClose='true'
    :bodyStyle="{ height: '80vh' }"
  >
    <div>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='24'>
            <a-col :md='5' :sm='24'>
              <a-form-item label='表单名称'>
                <a-input placeholder='请输入内容' v-model='queryParam.searchText' allowClear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md='4' :sm='24'>
              <a-form-item label='表单类型'>
                <a-select v-model='queryParam.formType' allowClear>
                  <a-select-option :value='3'> 作业记录表</a-select-option>
                  <a-select-option :value='1'> 数据记录表</a-select-option>
                  <a-select-option :value='4'> 检查记录表</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md='6' :sm='8'>
              <a-form-item>
                <a-space>
                  <a-button class='primary-color' @click='loadForms'>查询</a-button>
                </a-space>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <div style='height:calc(80vh - 135px)'>
        <vxe-table
          border
          resizable
          max-height='100%'
          style='height: calc(80vh - 135px)'
          ref='listTable'
          align='center'
          :data='forms'
          highlight-current-row
          show-overflow='tooltip'
          :edit-config="{ trigger: 'manual', mode: 'row' }"
          :radio-config="!multiple ? { trigger: 'row' } : {}"
          :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true } : {}"
        >
          <vxe-table-column v-if='multiple' type='checkbox' width='40'></vxe-table-column>
          <vxe-table-column v-else type='radio' width='40'></vxe-table-column>
          <vxe-table-column
            field='title'
            title='表单名称'
            min-width='180'
            align='left'
            header-align='center'
          >
            <template v-slot='{row}'>
              <a @click='handleSeeing(row)'>{{ row.title }}</a>
            </template>
          </vxe-table-column>
          <vxe-table-column field='formType_dictText' title='表单类型' width='120'>
            <template v-slot='{ row }'>
              {{ row.formType !== 3 ? row.formType_dictText : row.workRecordType === 1 ? '作业记录表(老)' : '作业记录表(新)' }}
            </template>
          </vxe-table-column>
          <vxe-table-column field='reguName' title='所属规程' width='120' />
          <vxe-table-column field='reguVersion' title='规程版本' width='120' />
          <vxe-table-column field='trainStructName' title='关联设备' align='left' header-align='center' />
          <vxe-table-column field='remark' title='备注' align='left' header-align='center' />
        </vxe-table>
      </div>
      <vxe-pager
        perfect
        :current-page.sync='queryParam.pageNo'
        :page-size.sync='queryParam.pageSize'
        :total='totalResult'
        :layouts="[
            'PrevJump',
            'PrevPage',
            'Number',
            'NextPage',
            'NextJump',
            'Sizes',
            'FullJump',
            'Total',
          ]"
        @page-change='handlePageChange'
      ></vxe-pager>
    </div>
    <RecordTableView ref='recordTableView'></RecordTableView>
    <FormViewModal ref='formViewModal'></FormViewModal>
    <JobCheckTableView ref='jobCheckTableView'></JobCheckTableView>
  </a-modal>
</template>

<script>
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { pageTpPlanForms } from '@/api/tirosApi'
import { pageTrainPlanForm } from '@/api/tirosDispatchApi'
import RecordTableView from '@views/tiros/basic/modules/workRecordSheet/RecordTableView'
import JobCheckTableView from '@views/tiros/basic/modules/jobCheckSheet/JobCheckTableView'
import FormViewModal from '@views/tiros/basic/customform/FormViewModal'
import { getWorkcheck } from '@/api/tirosQualityApi'

export default {
  name: 'PlanFormsList',
  components: { RecordTableView, JobCheckTableView, FormViewModal, LineSelectList },
  props: {
    multiple: {
      type: Boolean,
      default: true
    },
    planType: {
      // 0表示计划模板，1表示列计划
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      queryParam: {
        tpPlanId: '',
        planId: '',
        searchText: '',
        formType: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      visible: false,
      loading: false,
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curRow: null,
      lineId: '',
      repairProgramId: '',
      forms: [],
      planId: '',
      trainTypeId: '',
      title: ''
    }
  },
  methods: {
    showTitle() {
      if (this.planType === 0) {
        return '计划模板已关联表单'
      } else if (this.planType === 1) {
        return '列计划已关联表单'
      } else {
        return ''
      }
    },
    show(planInfo) {
      this.planId = planInfo.id
      this.trainTypeId = planInfo.trainTypeId
      this.lineId = planInfo.lineId
      this.repairProgramId = planInfo.repairProgramId
      this.loadForms()
      this.visible = true
    },
    // 确认
    handleOk() {
      let selectRecords = []
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords()
      } else {
        if (this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
      this.$emit('ok', selectRecords)
      this.visible = false
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    // 关闭
    close() {
      this.$emit('close')
      this.visible = false
    },
    loadForms() {
      this.forms = []

      let obj
      if (this.planType === 0) {
        this.queryParam.tpPlanId = this.planId
        obj = pageTpPlanForms(this.queryParam)
      } else if (this.planType === 1) {
        this.queryParam.planId = this.planId
        obj = pageTrainPlanForm(this.queryParam)
      }

      obj
        .then((res) => {
          if (res.success) {
            this.forms = res.result.records
            this.totalResult = res.result.total
          } else {
            this.$message.error('获取计划表单失败')
            console.error('获取计划表单失败:', res.message)
          }
        })
        .catch((err) => {
          this.$message.error('获取计划表单异常')
          console.error('获取计划表单异常:', err)
        })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.loadForms()
    },
    handleSeeing(data) {
      if (data.formType === 1) {
        this.$refs.formViewModal.showModal(data.objId)
      }
      if (data.formType === 3) {
        if (data.workRecordType === 1) {
          this.$refs.recordTableView.show(data.objId)
        } else if (data.workRecordType === 2) {
          this.$refs.formViewModal.showModal(data.objId)
        }
      }
      if (data.formType === 4) {
        getWorkcheck({
          id: data.objId
        }).then((res) => {
          if (res.success && res.result) {
            let formData = res.result
            this.$refs.jobCheckTableView.show(formData)
          } else {
            this.$message.error('加载记录数据异常')
            console.error('加载记录数据失败', res.message)
          }
        })
      }
    }
  }
}
</script>

<style scoped>
</style>