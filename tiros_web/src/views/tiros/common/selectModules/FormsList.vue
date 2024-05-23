<template>
  <!-- 表单选择弹窗 -->
  <a-modal
    width='90%'
    :title='title'
    centered
    :bodyStyle="{ height: '80vh' }"
    :visible='visible'
    :confirmLoading='confirmLoading'
    @ok='handleOk'
    @cancel='handleCancel'
    cancelText='关闭'
    :destroyOnClose='true'
  >
    <na-splitter :defaultSize='200' style='height: calc(80vh - 15px)'>
      <div slot='left-panel' style='overflow-y: auto; overflow-x: hidden; padding-right: 2px'>
        <!-- 这里的key 来自字典表 dict_code = bu_work_form_type
                SELECT * FROM SYS_DICT_ITEM sdi WHERE sdi.DICT_ID = '1321303108587917314';
         -->
        <a-menu mode='inline' @click='handleMenuClick' :default-selected-keys='defaultSelect'>
          <!--     不让选择旧的了     <a-menu-item :key="3">
                      <a-icon type="pie-chart" />
                      <span>作业记录表</span>
                    </a-menu-item>-->
          <a-menu-item :key='1'>
            <a-icon type='pie-chart' />
            <span>数据记录表</span>
          </a-menu-item>
          <a-menu-item :key='3'>
            <a-icon type='pie-chart' />
            <span>作业记录表【新】</span>
          </a-menu-item>
          <a-menu-item :key='4'>
            <a-icon type='pie-chart' />
            <span>检查记录表</span>
          </a-menu-item>
          <!--          <a-menu-item :key="2">
            <a-icon type="pie-chart" />
            <span>在线文件</span>
          </a-menu-item>-->
        </a-menu>
      </div>
      <div slot='right-panel' style='padding-left: 5px; padding-right: 3px'>
        <div>
          <div class='table-page-search-wrapper'>
            <a-form
              layout='inline'
              @keyup.enter.native='findList'
              :label-col='formItemLayout.labelCol'
              :wrapper-col='formItemLayout.wrapperCol'
            >
              <a-row :gutter='24'>
                <a-col :md='7' :sm='24'>
                  <a-form-item label='表单名称'>
                    <a-input placeholder='请输入表单名称' v-model='queryParam.name'></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md='4' :sm='24'>
                  <a-form-item
                    :label-col='formItemLayout.labelCol'
                    :wrapper-col='formItemLayout.wrapperCol'
                    label='线路'
                  >
                    <line-select-list v-model='queryParam.lineId'></line-select-list>
                  </a-form-item>
                </a-col>
                <a-col :md='5' :sm='24'>
                  <a-form-item
                    :label-col='formItemLayout.labelCol'
                    :wrapper-col='formItemLayout.wrapperCol'
                    label='所属修程'
                  >
                    <j-dict-select-tag
                      triggerChange
                      v-model='queryParam.repairProId'
                      dictCode='bu_repair_program,name,id'
                    />
                  </a-form-item>
                </a-col>
                <!--<a-col :md="7" :sm="24">
                  <a-form-item label="关联工位" style="width: 100%">
                    <a-select
                      show-search
                      :allowClear="true"
                      placeholder="选择工位"
                      option-filter-prop="children"
                      :filter-option="filterOption"
                      v-model="queryParam.workstationId"
                    >
                      <a-select-option v-for="(item,index) in stationData" :key="index" :value="item.value">{{item.title}}</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>-->
                <a-col :md='3' :sm='8'>
                  <a-form-item>
                    <a-button @click='findList'>查询</a-button>
                  </a-form-item>
                </a-col>
              </a-row>
            </a-form>
          </div>
          <div style='height: calc(80vh - 135px)'>
            <vxe-table
              border
              max-height='100%'
              style='height: calc(80vh - 135px)'
              ref='listTable'
              :align='allAlign'
              :data='tableData'
              show-overflow='tooltip'
              :edit-config="{ trigger: 'manual', mode: 'row' }"
              :radio-config="!multiple ? { trigger: 'row' } : {}"
              :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true } : {}"
            >
              <vxe-table-column v-if='multiple' type='checkbox' width='40'></vxe-table-column>
              <vxe-table-column v-else type='radio' width='40'></vxe-table-column>
              <vxe-table-column title='-' width='80'>
                <template v-slot='{ row }'>
                  <span>
                    <a @click.stop='handleSeeing(row)'>预览</a>
                  </span>
                </template>
              </vxe-table-column>
              <vxe-table-column
                field='title'
                :title="
                  formType === 3 ? '作业记录表名称' : formType === 1 ? '数据记录表' : formType === 4 ? '检查记录表' : ''
                "
                align='left'
                header-align='center'
              ></vxe-table-column>
              <vxe-table-column width='100' align='left' header-align='center' field='code'
                                title='编码'></vxe-table-column>
              <!--<vxe-table-column field="workGroupName" title="关联工位" ></vxe-table-column>-->
              <vxe-table-column field='lineName' title='线路' width='100'></vxe-table-column>
              <vxe-table-column
                v-if='formType === 4'
                field='repairProName'
                title='修程'
                width='100'
              ></vxe-table-column>

              <vxe-table-column
                v-if='formType === 1 || formType === 3'
                field='repairProgramName'
                title='修程'
                width='100'
              ></vxe-table-column>
              <vxe-table-column field='status_dictText' title='状态' width='100'>
                <template v-slot='{ row }'>
                  <div :style="{ backgroundColor: statusColor[row.status + ''], borderRadius: '4px' }">
                    {{ row.status_dictText }}
                  </div>
                </template>
              </vxe-table-column>
              <vxe-table-column
                field='reguName'
                title='所属规程'
                width='120'
              ></vxe-table-column>
              <vxe-table-column
                field='reguVersion'
                title='规程版本'
                width='100'
              ></vxe-table-column>
              <vxe-table-column field='remark' title='备注' align='left' header-align='center'></vxe-table-column>
            </vxe-table>
            <vxe-pager
              perfect
              :current-page.sync='queryParam.pageNo'
              :page-size.sync='queryParam.pageSize'
              :total='totalResult'
              :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
              @page-change='handlePageChange'
            ></vxe-pager>
          </div>
        </div>
        <RecordTableView ref='recordTableView'></RecordTableView>
        <JobCheckTableView ref='jobCheckTableView'></JobCheckTableView>
        <FormViewModal ref='formViewModal'></FormViewModal>
      </div>
    </na-splitter>
  </a-modal>
</template>

<script>
import { getWorkcheck } from '@/api/tirosQualityApi'
import { getWorkcheckList, pageCustomForm } from '@/api/tirosApi'
import { ajaxGetDictItems, getDictItemsFromCache } from '@/api/api'
import NaSplitter from '@comp/tiros/Na-splitter'
import RecordTableView from '@views/tiros/basic/modules/workRecordSheet/RecordTableView'
import JobCheckTableView from '@views/tiros/basic/modules/jobCheckSheet/JobCheckTableView'
import FormViewModal from '@views/tiros/basic/customform/FormViewModal'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'FormsList',
  components: { NaSplitter, RecordTableView, FormViewModal, LineSelectList, JobCheckTableView },
  props: {
    title: {
      type: String,
      default: '表单选择'
    },
    multiple: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      fileName: '',
      formItemLayout: {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 18 }
        }
      },
      statusColor: {
        0: '#dedede',
        1: '#bad795'
      },
      queryParam: {
        name: '',
        workstationId: '',
        lineId: '',
        repairProId: '',
        status: 1,
        pageNo: 1,
        pageSize: 10
      },
      formType: 3,
      workRecordType: 2,
      defaultSelect: [3],
      dictCode: 'bu_mtr_workstation,name,id',
      allAlign: 'center',
      totalResult: 0,
      tableData: [],
      visible: false,
      confirmLoading: false,
      stationData: [],
      isShowPlan: false
    }
  },
  methods: {
    handleSeeing(data) {
      if (this.formType === 3 && this.workRecordType === 1) {
        this.$refs.recordTableView.show(data.id)
      }
      if (this.formType === 1 || (this.formType === 3 && this.workRecordType === 2)) {
        this.$refs.formViewModal.showModal(data.id)
      }
      if (this.formType === 4) {
        getWorkcheck({
          id: data.id
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
    },
    showModal(data) {
      if (data && data.lineId) {
        this.queryParam.lineId = data.lineId
      }
      if (data && data.repairProgramId) {
        this.queryParam.repairProId = data.repairProgramId
      }
      this.visible = true
      this.isShowPlan = false
      this.formType = 3
      this.workRecordType = 2
      this.defaultSelect = [3]
      this.findList()
      // this.initDictData()
    },
    showModalByPlan(planId) {
      this.visible = true
      this.isShowPlan = true
      this.formType = 3
      this.workRecordType = 2
      this.defaultSelect = [3]
      this.queryParam.planId = planId
      this.findList()
      // this.initDictData()
    },
    initDictData() {
      //优先从缓存中读取字典配置
      if (getDictItemsFromCache(this.dictCode)) {
        this.stationData = getDictItemsFromCache(this.dictCode)
      } else {
        //根据字典Code, 初始化字典数组
        ajaxGetDictItems(this.dictCode).then((res) => {
          if (res.success) {
            this.stationData = res.result
          }
        })
      }
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    },
    initParamByFormType() {
      // 作业记录表- 新
      if (this.formType === 3) {
        this.queryParam.category = '1328995639249358850'
        this.queryParam.type = 1
        this.workRecordType = 2
      }
      // 数据记录表
      if (this.formType === 1) {
        this.queryParam.category = '1328995580210335746'
        this.queryParam.type = 1
        this.workRecordType = 0
      }
    },
    findList() {
      this.loading = true
      // // 获取作业记录表（旧）
      // if (this.formType === 3) {
      //   pageOldWorkRecord(this.queryParam).then((res) => {
      //     this.totalResult = res.result.total
      //     this.loading = false
      //     this.tableData = res.result.records
      //   })
      // }

      this.initParamByFormType()

      // 获取在线表单-数据记录表, 作业记录表新
      if (this.formType === 1 || (this.formType === 3 && this.workRecordType === 2)) {
        pageCustomForm(this.queryParam).then((res) => {
          if (res.success) {
            this.totalResult = res.result.total
            this.loading = false
            this.tableData = res.result.records
          }
        })
      }

      // 获取检查记录表
      if (this.formType === 4) {
        getWorkcheckList(this.queryParam).then((res) => {
          if (res.success) {
            this.totalResult = res.result.total
            this.loading = false
            this.tableData = res.result.records
          }
        })
      }
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleOk() {
      let selectRecords = []
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords()
      } else {
        if (this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
      let formTypeName = ''
      if (this.formType === 1) {
        formTypeName = '数据记录表'
      }
      if (this.formType === 3) {
        if (this.workRecordType === 1) {
          formTypeName = '作业记录表(老)'
        }
        if (this.workRecordType === 2) {
          formTypeName = '作业记录表(新)'
        }
      }
      if (this.formType === 4) {
        formTypeName = '检查记录表'
      }

      this.$emit('ok', {
        formType: this.formType,
        workRecordType: this.workRecordType,
        formTypeName: formTypeName,
        forms: selectRecords
      })
      this.visible = false
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    handleMenuClick(e) {
      if (this.formType !== e.key) {
        this.formType = e.key
        this.queryParam.name = ''
        // this.queryParam.lineId = ''
        // this.queryParam.repairProId = ''
        this.initParamByFormType()
        this.findList()
        // this.initDictData()
      }
    }
  }
}
</script>

<style scoped>
.limitTitle {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
  border-bottom: 1px solid #e1e1de;
}

.limitHeight {
  height: calc(80vh - 70px);
}
</style>