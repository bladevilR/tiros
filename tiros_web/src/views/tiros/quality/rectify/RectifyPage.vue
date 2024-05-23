<template>
  <div>
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="4" :sm="24">
            <a-form-item label="整改">
              <a-input placeholder="请输入名称或编码" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag v-model="queryParam.status" placeholder="请选择" dictCode="bu_work_rectify_status" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
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
        </a-row>
        <a-row>
          <a-col>
            <span class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd" >新增</a-button>
                <a-button :disabled="records.length != 1" @click="handleEdit(records[0])">编辑</a-button>
                <a-button :disabled="records.length < 1" @click="deleteRecord">删除</a-button>
                <a-button :disabled="records.length < 1" @click="closeRecord">关闭整改</a-button>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 数据区域 -->
    <div style="height: calc(100vh - 267px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(100vh - 267px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="rectifyNo" title="编号" width="10%">
          <template v-slot="{row}">
            <a @click.stop="handleView(row)">{{row.rectifyNo}}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="lineName" title="所属线路" width="8%"></vxe-table-column>
        <vxe-table-column field="trainNo" title="所属车辆" width="10%"></vxe-table-column>
        <vxe-table-column field="title" title="任务名称" align="left" header-align="center" width="15%"></vxe-table-column>
        <vxe-table-column field="rectifyType_dictText" title="整改类型" width="10%"></vxe-table-column>
        <vxe-table-column field="groupName" align="left" header-align="center" title="责任工班" width="10%"></vxe-table-column>
        <!-- <vxe-table-column field="dutyUserName" title="责任人" width="8%"></vxe-table-column> -->

        <vxe-table-column field="sendDate" title="下达日期" width="8%"></vxe-table-column>
        <vxe-table-column field="finishDate" title="完成时间" width="8%"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="整改状态" width="8%"></vxe-table-column>
        <vxe-table-column title="附件描述" width="15%" align="left" header-align="center">
          <template v-slot="{ row }">
            <a v-for="(item, index) in row.annexList" :key="index" @click.stop="handleSeeing(item)">{{ item.title }}</a>
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
    <rectify-modal ref="modalForm" @ok="loadData()" :defaultFileList="defaultFileList"></rectify-modal>
    <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
    <RectifyView ref="rectifyViewRef"></RectifyView>
  </div>
</template>

<script>
import moment from 'moment'
import { getRectifyList, delRectify, closeRectify } from '@/api/tirosQualityApi'
import RectifyModal from '@views/tiros/quality/modules/RectifyModal'
import RectifyView from '@views/tiros/quality/modules/RectifyView'
import { randomString } from '@/utils/util'
import { isPrivilege } from '@/api/tirosApi'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'

export default {
  name: 'RectifyPage',
  components: { RectifyModal, DocPreviewModal, RectifyView },
  data() {
    return {
      records:[],
      date: null,
      queryParam: {
        searchText: '',
        rectifyType: '',
        status: '',
        sendDate: null,
        pageNo: 1,
        pageSize: 10,
      },
      defaultFileList: [],
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      count: '',
      filePath: '',
      fileName: '',
      status: false,
    }
  },
  created() {
    this.findList()
  },
  methods: {
    checkboxChange(e){
      this.records = e.records;
    },
    async handlePrivilege(id, operation) {
      let param = { id: id, operation: operation }
      await isPrivilege(param).then((res) => {
        this.status = res.result
      })
    },
    async handleSeeing(data) {
      await this.handlePrivilege(data.id, 2)
      if (!this.status) {
        this.$message.error('您没有权限!')
      } else {
        this.fileName = data.title
        this.$refs.docPreview.handleFilePath(data.address)
      }
    },
    findList() {
      this.loading = true
      if (this.date) {
        this.queryParam.sendDate = moment(this.date).format('YYYY-MM-DD')
      } else {
        this.queryParam.sendDate = ''
      }
      getRectifyList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.records = []
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
    handleAdd() {
      this.$refs.modalForm.add(null, null, null, randomString(8))
      // this.$refs.modalForm.rectifyNo= randomString(8)
      this.$refs.modalForm.title = '新增'
    },
    handleEdit(record) {
      if (record.annexList != null && record.annexList.length > 0) {
        record.annexList.map((item) => {
          this.defaultFileList.push({ uid: item.id, name: item.title, status: 'done', url: item.address })
        })
      }
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
    },

    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delRectify('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    closeRecord() {
      let records = this.$refs.listTable.getCheckboxRecords()
      if (records.length === 0) {
        this.$message.warn('至少选择一条记录')
        return
      }
      const _list = records.filter((item) => {
          return  item.status == 3 //条件;
      });
      if(_list.length>0){
        this.$message.warn('已关闭的整改不能再关闭')
        return
      }
      this.$confirm({
        content: `是否关闭选中${records.length}条数据？`,
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          let delList = records.map((item) => {
            return item.id
          })
          let formData = new FormData()
          formData.append('ids', delList.join(','))
          closeRectify(formData).then((res) => {
            if (res.success) {
              this.$message.success('关闭成功')
              this.findList()
            } else {
              this.$message.error(res.message)
            }
          })
        },
      })
    },
    handleView(record){
      console.log(record)
      this.$refs.rectifyViewRef.showView(record)
    }
  },
}
</script>

<style scoped>
</style>