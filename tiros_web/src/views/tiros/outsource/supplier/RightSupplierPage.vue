<template>
  <a-card :body-style="cardStyle">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="请输入名称" v-model="queryParam.name" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="修程类型">
              <j-dict-select-tag
                v-model="queryParam.repairProgramId"
                placeholder="请选择"
                dictCode="bu_repair_program,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="所属线路">
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag v-model="queryParam.status" placeholder="请选择" dictCode="bu_effective" />
            </a-form-item>
          </a-col>
          <a-col :md="2" :sm="8">
            <a-button @click="findList">查询</a-button>
          </a-col>
          <a-col :md="24" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button @click="handleEdit" :disabled="!btnStatus.edit">编辑</a-button>
                <a-button @click="deleteRecord" :disabled="!btnStatus.del">删除</a-button>
                <!--              <a-upload-->
                <!--                name="file"-->
                <!--                :multiple="false"-->
                <!--                :customRequest="customRequest"-->
                <!--                :showUploadList="false">-->
                <!--               <a-button>导入</a-button>-->
                <!--               </a-upload>-->
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 285px)">
      <vxe-table
        border
        ref="listTable"
        max-height="100%"
        style="height: calc(100vh - 285px)"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        @checkbox-change="rowSelectChange"
        @checkbox-all="btnStatus.update()"
      >
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column
          field="name"
          title="厂商名称"
          fixed="left"
          header-align="center"
          align="left"
          width="180"
        ></vxe-table-column>
        <vxe-table-column
          field="shortName"
          title="厂商简称"
          header-align="center"
          align="left"
          width="180"
        ></vxe-table-column>
        <vxe-table-column field="category_dictText" title="厂商类别" width="8%"></vxe-table-column>
        <vxe-table-column
          field="repairProgramName"
          title="所属修程"
          header-align="center"
          width="140"
        ></vxe-table-column>
        <vxe-table-column field="lineName" title="所属线路" width="8%"></vxe-table-column>
        <vxe-table-column
          field="contactName"
          title="联系人员"
          align="left"
          header-align="center"
          width="8%"
        ></vxe-table-column>
        <vxe-table-column
          field="contactPhone"
          title="联系电话"
          width="15%"
          header-align="center"
          align="left"
        ></vxe-table-column>
        <vxe-table-column field="email" title="邮箱" width="18%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column
          field="address"
          title="地址"
          width="18%"
          header-align="center"
          align="left"
        ></vxe-table-column>
        <vxe-table-column
          field="appraise"
          title="评分"
          width="8%"
          header-align="center"
          align="right"
        ></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="8%"></vxe-table-column>
        <vxe-table-column
          field="remarks"
          title="备注"
          width="25%"
          header-align="center"
          align="left"
        ></vxe-table-column>
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
    <supplier-item-module ref="modalForm" @ok="loadData()" :assetId="value"></supplier-item-module>
  </a-card>
</template>

<script>
import SupplierItemModule from '../modules/SupplierItemModule'
import { delSupplier, getSupplierList } from '@api/tirosOutsourceApi'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'RightSupplierPage',
  components: { SupplierItemModule, LineSelectList },
  props: ['value'],
  data() {
    return {
      btnStatus: new TableBtnUtil(this, 'listTable', {}),
      queryParam: {
        category: null,
        name: '',
        objTypeId: '',
        status: null,
        lineId: '',
        repairProgramId: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      showEdit: false,
      cardStyle: {
        padding: '10px',
        height: 'calc(100vh - 110px)',
      },
    }
  },
  created() {
    this.findList()
  },
  watch: {
    value: {
      immediate: true,
      handler(id) {
        this.queryParam.objTypeId = id
        this.tableData = []
        this.findList()
      },
    },
  },
  methods: {
    rowSelectChange() {
      this.btnStatus.update()
    },
    findList() {
      getSupplierList(this.queryParam).then((res) => {
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
      this.$emit('load')
    },
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleEdit() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length == 1) {
        this.$refs.modalForm.edit(selectRecords[0])
        this.$refs.modalForm.title = '编辑'
      } else {
        this.$message.error('请选中一项数据!')
      }
    },
    // customRequest(data) { // 上传提交
    //   const formData = new FormData()
    //   formData.append('excelFile', data.file)
    //   this.saveFile(formData)
    // },
    saveFile(formData) {
      importWarehouse(formData).then((res) => {
        if (res.success) {
          this.$message.success(res.message)
        } else {
          this.$message.error(res.message)
        }
      })
    },
    getSelectEvent() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      this.$XModal.alert(selectRecords.length)
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
            delSupplier('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
  },
}
</script>

<style scoped lang="less">
</style>