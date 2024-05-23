 <template>
  <div class="bodyWrapper" na-flex-height-full style="height: 100% !important">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="表单名称">
              <a-input v-model="queryParam.formSearchText" placeholder="检查表单名称" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="修程">
              <j-dict-select-tag v-model="queryParam.repairProgramId" dictCode="bu_repair_program,name,id" />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="线路">
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="10">
            <a-space>
              <a-button @click="findList">查询</a-button>
              <a-button :disabled="records.length !== 1" @click="openJudgeModal(records[0])">质量评定</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100% - 45px)">
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
        show-overflow="tooltip"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="name" title="检查表名称" min-width="150px" align="left" header-align="center">
          <template v-slot="{ row }">
            <a @click.stop="showFrom(row)">{{ row.name }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="lineName" title="线路" width="120px" />
        <vxe-table-column field="repairProgramName" title="修程" width="120px" />
        <vxe-table-column field="status_dictText" title="状态" width="120px" />
      </vxe-table>
    </div>
    <div style="height: 45px; background-color: #fff">
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="page.totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>
    <JobCheckTableView
      @brush="findList()"
      :fromData="fromData"
      :qualityEvaluation="true"
      ref="jobCheckTableView"
    ></JobCheckTableView>
    <a-modal
      title="作业质量评定"
      :width="800"
      :visible="visible"
      centered
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭"
      :destroyOnClose="true"
    >
      <a-form :form="form" ref="initForm">
        <a-form-item>
          <a-textarea
            :maxLength="201"
            v-decorator="['jdContent', validatorRules.jdContent]"
            :auto-size="{ minRows: 8, maxRows: 8 }"
          ></a-textarea>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>
<script>
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { pageFormCheckRecord, getWorkCheckForm, setJudgeRecord } from '@api/tirosQualityApi'
import JobCheckTableView from '@views/tiros/basic/modules/jobCheckSheet/JobCheckTableView'

export default {
  name: 'QualityEvaluate',
  components: { LineSelectList, pageFormCheckRecord, JobCheckTableView },
  data() {
    return {
      records: [],
      fromData: {},
      visible: false,
      tableData: [],
      form: this.$form.createForm(this),
      validatorRules: {
        jdContent: {
          rules: [
            { required: true, message: '请输入评定内容!' },
            { max: 200, message: '不能超过200个字符' },
          ],
        },
      },
      queryParam: {
        pageNo: 1,
        pageSize: 10,
        formSearchText: null,
        lineId: null,
        repairProgramId: null,
      },
      page: {
        totalResult: 1,
      },
      judgeQueryParam: {
        id: null,
        checkInstId: '',
        checkInstName: '',
        jdContent: '',
        jdTime: null,
        jdUserId: this.$store.getters.userInfo.id,
        jdUserName: this.$store.getters.userInfo.username,
      },
    }
  },
  mounted() {
    this.findList()
  },
  methods: {
    rangeChange({ records }) {
      this.records = records
    },
    findList() {
      pageFormCheckRecord(this.queryParam).then((res) => {
        if (res.success) {
          this.tableData = res.result.records
          this.records = []
          this.page.totalResult = res.result.total
        } else {
          this.$message.error(res.message)
        }
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    showFrom(row) {
      getWorkCheckForm({
        task2InstId: row.task2InstId,
        formInstId: row.id,
      }).then((res) => {
        this.fromData = row
        let record = res.result
        // record.workCheckItemList = record.itemList
        // record.judge = record.judgeList
        this.$refs.jobCheckTableView.show(record)
      })

      // this.$refs.jobCheckTableView.show(row)
    },
    openJudgeModal(row) {
      this.judgeQueryParam.checkInstId = row.id
      this.judgeQueryParam.checkInstName = row.name
      this.visible = true
      let record = row.judgeList.find((e) => e.jdUserId === this.$store.getters.userInfo.id)
      if (record) {
        this.judgeQueryParam.jdContent = record.jdContent
        this.judgeQueryParam.id = record.id
        this.form.getFieldDecorator('jdContent')
        this.$nextTick(() => {
          this.form.setFieldsValue({
            jdContent: this.judgeQueryParam.jdContent,
          })
        })
      } else {
        this.judgeQueryParam.jdContent = ''
        this.judgeQueryParam.id = null
        this.form.getFieldDecorator('jdContent')
        this.$nextTick(() => {
          this.form.setFieldsValue({
            jdContent: '',
          })
        })
      }
    },
    handleOk() {
      // setJudgeRecord
      this.form.validateFields((err, values) => {
        if (!err) {
          Object.assign(this.judgeQueryParam, values)
          setJudgeRecord(this.judgeQueryParam).then((res) => {
            if (res.success) {
              this.$message.success('保存成功')
              this.findList()
            } else {
              this.$message.error(res.message)
            }
          })
          this.visible = false
        }
      })
    },
    handleCancel() {
      this.visible = false
    },
  },
}
</script>
