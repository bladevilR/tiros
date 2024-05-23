<template>
  <div>
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="预警">
              <a-input placeholder="预警描述" v-model="queryParam.alertDescribe" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择"
                dictCode="bu_work_measure_alert_status" />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="预警日期">
              <a-date-picker v-model="date"/>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button @click="findList">查询</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 225px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(100vh - 225px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column title="表单查看" min-width="120" align="left" header-align="center">
          <template v-slot="{ row }">
            <a @click.stop="jumpInfo(row)">{{row.formObjName}}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="alertTime" title="测量时间" width="160"/>
        <vxe-table-column field="groupName" title="作业工班" width="120"/>
        <vxe-table-column field="workstationNames" title="作业工位" min-width="120" align="left" header-align="center"/>
        <vxe-table-column field="taskName" title="作业工序" min-width="160" align="left" header-align="center"/>
        <vxe-table-column field="status_dictText" title="状态" width="80"/>
        <vxe-table-column field="alertMessage" title="预警描述" min-width="240" align="left" header-align="center"/>
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
    <form-template ref="formTemplate"></form-template>
  </div>
</template>

<script>
  import moment from 'moment'
  import { getEarlyWarningList } from '../../../../api/tirosQualityApi'
  import FormTemplate from '@views/tiros/common/form/FormTemplate'

  export default {
    name: 'EarlyWarningCheckPage',
    components: {FormTemplate},
    data() {
      return {
        date: null,
        queryParam: {
          alertDescribe: '',
          status: '',
          alertTime: null,
          pageNo: 1,
          pageSize: 10
        },
        totalResult: 0,
        allAlign: 'center',
        tableData: [],
        count: ''
      }
    },
    created() {
      this.findList()
    },
    methods: {
      findList() {
        this.loading = true
        if (this.date) {
          this.queryParam.sendDate = moment(this.date).format('YYYY-MM-DD')
        } else {
          this.queryParam.sendDate = ''
        }
        getEarlyWarningList(this.queryParam).then((res) => {
          this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result.records
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
      jumpInfo(row) {
        this.$refs.formTemplate.showModal(row)
      }
    }
  }
</script>

<style scoped>

</style>