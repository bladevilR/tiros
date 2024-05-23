<template>
<!--  整改选择 -->
  <a-modal
    width="90%"
    title="整改记录选择"
    centered
    :visible="visible"
    :bodyStyle="{ height: '80vh' }"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="4" :sm="24">
            <a-form-item label="整改">
              <a-input placeholder="请输入名称或编码" v-model="queryParam.searchText"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择"
                dictCode="bu_work_rectify_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24" >
            <a-form-item label="下发日期">
              <a-date-picker v-model="date" />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="类型">
              <j-dict-select-tag
                v-model="queryParam.rectifyType"
                placeholder="请选择"
                dictCode="bu_work_rectify_type"
              />
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
    <!-- 数据区域 -->
    <div style="height: calc(80vh - 130px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(80vh - 130px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="ellipsis"
        :radio-config="!multiple ? {trigger: 'row'} : {}"
        :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true } : {}"
        :edit-config="{trigger: 'click', mode: 'cell', showIcon:'true'}"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
        <vxe-table-column field="rectifyNo" title="编号" min-width="120"></vxe-table-column>
        <vxe-table-column field="title" title="整改名称" align="left" min-width="120" header-align="center"></vxe-table-column>
        <vxe-table-column field="rectifyType_dictText" title="整改类型" width="80"></vxe-table-column>
        <vxe-table-column field="groupName" title="责任工班" width="100"></vxe-table-column>
        <vxe-table-column field="dutyUserName" title="责任人" width="100"></vxe-table-column>
        <vxe-table-column field="lineName" title="所属线路" width="100"></vxe-table-column>
        <vxe-table-column field="depotName" title="所属车辆段" width="120"></vxe-table-column>
        <vxe-table-column field="sendDate" title="下达日期" width="100"></vxe-table-column>
        <vxe-table-column field="finishDate" title="完成时间" width="100"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="整改状态" width="80"></vxe-table-column>
        <vxe-table-column title="附件描述" width="200" align="left" header-align="center">
          <template v-slot="{ row }">
            <a @click.stop="" v-for="item in row.annexList">{{item.title}}</a>
          </template>
        </vxe-table-column>
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
  </a-modal>
</template>

<script>
import moment from 'moment'
import { getRectifyList } from '@api/tirosQualityApi'

export default {
  name: 'RectifySelect',
  props: {
    multiple: {
      type: Boolean,
      default: false,
    },
  },
  data () {
    return {
      visible: false,
      date:null,
      queryParam: {
        searchText: '',
        rectifyType: '',
        status: '',
        sendDate:null,
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      count:''
    }
  },
  methods: {
    showModal() {
      this.visible = true
      this.findList()
    },
    findList() {
      if(this.date){
        this.queryParam.sendDate = moment(this.date).format('YYYY-MM-DD')
      }else {
        this.queryParam.sendDate = ''
      }
      getRectifyList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.tableData = res.result.records
      })
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
        if(this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
      this.$emit('ok', selectRecords)
      this.close()
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
  }
}
</script>

<style scoped>

</style>