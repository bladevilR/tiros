<template>
  <div class='bodyWrapper'>
    <div class='table-page-search-wrapper'>
      <a-form layout='inline'>
        <a-row :gutter='24'>
          <a-col :md='4' :sm='24'>
            <a-form-item label='修程'>
              <j-dict-select-tag
                v-model='queryParam.repairProgramId'
                placeholder='请选择'
                dictCode='bu_repair_program,name,id'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='班组:'>
              <j-dict-select-tag
                v-model='queryParam.groupId'
                placeholder='请选择'
                :dictCode="'bu_mtr_workshop_group,group_name,id'"
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='类型:'>
              <a-select v-model='queryParam.formRecordType' placeholder='请选择类型' allowClear>
                <a-select-option value=''>所有</a-select-option>
                <a-select-option value='1'>作业记录表</a-select-option>
                <a-select-option value='2'>数据记录表</a-select-option>
                <a-select-option value='3'>检查记录表</a-select-option>
                <a-select-option value='4'>工单附件</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md='6' :sm='24'>
            <a-form-item label='工单日期'>
              <a-date-picker v-model='date' :format="'YYYY-MM-DD'"></a-date-picker>
            </a-form-item>
          </a-col>
          <a-col :md='6' :sm='8'>
            <span style='float: left; overflow: hidden' class='table-page-search-submitButtons'>
              <a-space>
                <a-button @click='findList'>查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-spin :spinning='loading'>
      <div>
        <vxe-table
          border
          max-height='90%'
          style='height: calc(100vh - 298px)'
          ref='listTable'
          :align='allAlign'
          :data='tableData'
          show-overflow='tooltip'
          :edit-config="{ trigger: 'manual', mode: 'row' }"
        >
          <vxe-table-column type='checkbox' width='40'></vxe-table-column>
          <vxe-table-column field='formRecordName' title='表单名称' min-width='180' align='left' header-align='center'>
            <template v-slot='{ row }'>
              <a href='javascript:;' @click.stop='showDetail(row)'>{{ row.formRecordName }}</a>
            </template>
          </vxe-table-column>
          <vxe-table-column field='formRecordTypeName' title='表单类型' width='120'></vxe-table-column>
          <vxe-table-column field='orderName' title='所属工单' min-width='160' align='left' header-align='center' />
          <vxe-table-column field='orderTime' title='工单日期' width='100'></vxe-table-column>
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
    </a-spin>
    <ViewWorkForm ref='viewWorkForm' @ok='loadData()'></ViewWorkForm>
    <doc-preview-modal :title='previewFileName' ref='docPreview'></doc-preview-modal>
  </div>
</template>

<script>
import { getRelatedForm } from '@api/tirosProductionApi'
import ViewWorkForm from '@views/tiros/dispatch/workOrder/ViewWorkForm'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import moment from 'moment'

export default {
  name: 'FaultRecord',
  props: {
    trainNo: {
      type: String,
      default: ''
    },
    structureDetailId: {
      type: String,
      default: ''
    }
  },
  components: { ViewWorkForm, DocPreviewModal },
  data() {
    return {
      previewFileName: '',
      dicSysIdStr: `bu_train_asset_type,name,id,struct_type=1 and train_type_id=(select train_type_id from bu_train_info where train_no='${this.trainNo}')`,
      date: null,
      queryParam: {
        trainNo: this.trainNo,
        structureDetailId: '',
        repairProgramId: '',
        groupId: '',
        formRecordType: '',
        orderTime: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      loading: false
    }
  },
  watch: {
    structureDetailId: {
      immediate: true,
      handler(id) {
        this.queryParam.structureDetailId = id !== this.trainNo ? id : ''
        this.findList()
      }
    },
    trainNo: {
      immediate: true,
      handler(id) {
        this.queryParam.trainNo = id
        this.findList()
      }
    }
  },
  methods: {
    showDetail(row) {
      switch (row.formRecordType) {
        case 1:
          // 作业记录表
          this.jumpFormInfo(row)
          break
        case 2:
          // 数据记录表
          this.jumpFormInfo(row)
          break
        case 3:
          // 检查记录表
          this.jumpFormInfo(row)
          break
        case 4:
          // 附件
          this.previewImage(row)
          break
      }

    },
    jumpFormInfo(row) {
      row.instType = row.formType
      row.formInstId = row.formRecordId
      row.formName = row.formRecordName

      this.$refs.viewWorkForm.showModal(row, this.trainNo)
    },
    previewImage(row) {
      this.previewFileName = row.formRecordName
      this.$refs.docPreview.handleFilePath(row.orderAnnexPath)
    },
    findList() {
      this.loading = true
      if (this.date) {
        this.queryParam.orderTime = moment(this.date).format('YYYY-MM-DD')
      } else {
        this.queryParam.orderTime = ''
      }
      getRelatedForm(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.tableData = res.result.records
      }).finally(() => this.loading = false)
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData() {
      this.findList()
    }
  }
}
</script>

<style scoped>
</style>