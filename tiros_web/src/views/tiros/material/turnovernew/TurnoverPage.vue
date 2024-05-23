<template>
  <div class='bodyWrapper'>
    <!-- 查询区域 -->
    <div class='table-page-search-wrapper'>
      <a-form layout='inline'>
        <a-row :gutter='24'>
          <a-col :md='4' :sm='12'>
            <a-form-item label='编码或者名称'>
              <a-input placeholder='物资编码或者周转件名称' v-model='queryParam.searchText' allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='线路'>
              <line-select-list v-model='queryParam.lineId'></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md='8' :sm='24'>
            <span style='float: left; overflow: hidden' class='table-page-search-submitButtons'>
              <a-space>
                <a-button @click='findList'>查询</a-button>
                <a-button @click='handleEdit(btnStatus.editRow)' :disabled='!btnStatus.edit'>编辑</a-button>
                <a-button style="margin-left: 8px" @click="deleteRecord">删除</a-button>
                <a-upload
                  style='margin-left: 8px'
                  name='file'
                  :multiple='false'
                  :customRequest='customRequest'
                  :showUploadList='false'
                >
                  <a-button :loading='loading'>导入周转件</a-button>
                </a-upload>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style='height: calc(100vh - 225px)'>
      <vxe-table
        border
        :align='allAlign'
        ref='listTable'
        max-height='100%'
        style='height: calc(100vh - 225px)'
        :data='tableData'
        show-overflow='tooltip'
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        @checkbox-change='btnStatus.update()'
        @checkbox-all='btnStatus.update()'
      >
        <vxe-table-column type='checkbox' width='40' fixed='left'></vxe-table-column>
        <vxe-table-column field='materialTypeId' title='物资编码' width='120' />
        <vxe-table-column field='name' title='描述' min-width='200' align='left' header-align='center' />
<!--        <vxe-table-column field='unit' title='单位' width='80' />-->
        <vxe-table-column field='price' title='价格' width='80' header-align='center' align='right' />
<!--        <vxe-table-column field='taxRate' title='税率' width='80' header-align='center' align='right' />-->
        <vxe-table-column field='taxPrice' title='含税价格' width='80' header-align='center' align='right' />
        <vxe-table-column field='useAmount' title='消耗量' width='80' header-align='center' align='right' />
        <vxe-table-column field='useAmountPrice' title='总额' width='80' header-align='center' align='right' />
        <vxe-table-column field='orderCode' title='工单号' width='140' />
        <vxe-table-column field='outOrderCode' title='出库单号' width='160' />
        <vxe-table-column field='warehouseCode' title='子库' width='80' />
        <vxe-table-column field='useTime' title='消耗时间' width='120' />
        <vxe-table-column field='systemName' title='所属系统' width='120' />
        <vxe-table-column field='firstUsePlanName' title='首次投入使用' width='140' />
        <vxe-table-column field='serviceYear' title='使用年限' width='80' />
        <vxe-table-column field='serviceYearRemark' title='使用年限备注' width='140' />
        <vxe-table-column field='remark' title='备注' min-width='120' header-align='center' align='left' />
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
    <TurnoverModal ref='turnoverModal' @ok='loadData()'></TurnoverModal>
    <j-import-modal :sum='1' ref='turnoverImportModal' url='/material/turnover/importExcel'
                    @ok='loadData()'></j-import-modal>
  </div>
</template>

<script>
import 'moment/locale/zh-cn'
import { deleteTurnoverBatch, pageTurnover, importTurnoverFromExcel } from '@api/tirosMaterialApi'
import TurnoverModal from '@views/tiros/material/turnovernew/TurnoverModal'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import JImportModal from '@/components/jeecg/JImportModal'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'TurnoverPage',
  components: { TurnoverModal, LineSelectList, JImportModal },
  data() {
    return {
      loading: false,
      queryParam: {
        searchText: '',
        lineId: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      btnStatus: new TableBtnUtil(this, 'listTable'),
      dicType: ''
    }
  },

  created() {
    this.findList()
  },
  methods: {
    findList() {
      pageTurnover(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
        this.btnStatus.update()
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData() {
      this.findList()
    },
    handleEdit(record) {
      this.$refs.turnoverModal.edit(record)
      this.$refs.turnoverModal.title = '编辑'
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function(obj, index) {
                return obj.id
              })
              .join(',')
            deleteTurnoverBatch('ids=' + idsStr).then((res) => {
              if (res.success) {
                this.findList()
                this.$message.success(res.message)
              } else {
                this.$message.error(res.message)
              }
            })
          }
        })
      } else {
        this.$message.warn('尚未选中任何数据!')
      }
    },
    customRequest(data) {
      // 上传提交
      this.loading = true
      const formData = new FormData()
      formData.append('excelFile', data.file)
      this.saveFile(formData)
    },
    saveFile(formData) {
      importTurnoverFromExcel(formData).then((res) => {
        this.loading = false
        if (res.success) {
          this.$message.success(res.message)
          this.findList()
        } else {
          this.$message.error(res.message)
        }
      })
    }
  }
}
</script>