 <template>
  <div class="bodyWrapper">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="作业指导书">
              <a-input v-model="queryParam.formName" placeholder="作业指导书名称" allowClear></a-input>
            </a-form-item>
          </a-col>
          <!-- <a-col :md="4" :sm="24">
            <a-form-item label="修程">
              <j-dict-select-tag
                v-model="queryParam.repairProId"
                placeholder="请选择"
                dictCode="bu_repair_program,name,id"
              />
            </a-form-item>
          </a-col> -->
          <a-col :md="4" :sm="24">
            <a-form-item label="修程">
              <j-dict-select-tag triggerChange v-model="queryParam.repairProId" dictCode="bu_repair_program,name,id" />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="线路">
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="11">
            <span class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button :disabled="!isCheckRow" @click="handleEdit">编辑</a-button>
                <a-button :disabled="!isCheckRows" @click="handleDel">删除</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div class="table-page-body-wrapper" style="height: calc(100% - 52px)">
      <div class="table-body-context">
        <vxe-table
          border
          ref="listTable"
          align="center"
          height="auto"
          auto-resize
          :data="tableData"
          :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          @checkbox-change="rangeChange"
          @checkbox-all="rangeChange"
        >
          <vxe-table-column type="checkbox" width="60px" />
          <vxe-table-column field="fileNo" title="文件编号" />
          <vxe-table-column
            field="fileName"
            title="作业指导书名称"
            min-width="150px"
            align="left"
            header-align="center"
          >
            <template v-slot="{ row }">
              <a @click.stop="showSop(row)">{{ row.fileName }}</a>
            </template>
          </vxe-table-column>
          <vxe-table-column align="left" header-align="center" field="repairProgramName" title="修程" />
          <vxe-table-column field="lineName" title="线路" />
          <vxe-table-column field="status_dictText" title="状态" />
          <vxe-table-column field="exeTime" title="实施日期" />
          <!-- <vxe-table-column field="" title="更新日期" />
          <vxe-table-column field="" title="更新人员" /> -->
          <vxe-table-column field="fileVer" title="版本" />
          <!-- <vxe-table-column field="" title="备注" /> -->
        </vxe-table>
      </div>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="page.totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>
    <WorkSopModal ref="workSopModal" @change="findList"></WorkSopModal>
    <WorkSopView ref="workSopViewModal"></WorkSopView>
  </div>
</template>
<script>

import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import WorkSopModal from '@views/tiros/basic/modules/worksop/WorkSopModal'
import { getSopPage, delSopRecord } from '@/api/tirosApi'
import WorkSopView from '@views/tiros/basic/modules/worksop/WorkSopView'

export default {
  name: 'WorkSop',
  components: { WorkSopModal, WorkSopView, LineSelectList },
  data() {
    return {
      isCheckRow: false,
      isCheckRows: false,
      tableData: [],
      queryParam: {
        pageNo: 1,
        pageSize: 10,
        formName: '',
        repairProId: '',
        lineId: '',
      },
      page: {
        totalResult: 1,
      },
    }
  },
  mounted() {
    this.findList()
  },
  methods: {
    findList() {
      getSopPage(this.queryParam).then((res) => {
        if (res.success) {
          this.page.totalResult = res.result.total
          // console.log(res.result)
          this.tableData = res.result.records
        } else {
          this.$message.error('获取数据失败')
        }
      })
      this.isCheckRow = false
      this.isCheckRows = false
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleAdd() {
      this.$refs.workSopModal.add()
    },
    handleEdit() {
      let record = this.$refs.listTable.getCheckboxRecords()[0]
      this.$refs.workSopModal.edit(record)
    },
    handleDel() {
      let records = this.$refs.listTable.getCheckboxRecords()
      this.$confirm({
        content: `是否删除选中${records.length}条数据？`,
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          let delList = records.map((item) => {
            return item.id
          })
          let formData = new FormData()
          formData.append('ids', delList.join(','))
          delSopRecord(formData).then((res) => {
            if (res.success) {
              this.$message.success('删除成功')
              this.findList()
            } else {
              this.$message.error(res.message)
            }
          })
        },
      })
    },
    // 用户选择记录触发
    rangeChange({ records }) {
      this.isCheckRow = records.length === 1
      this.isCheckRows = records.length > 0
    },
    showSop(row) {
      this.$refs.workSopViewModal.showBook(row.id)
    },
  },
}
</script>
