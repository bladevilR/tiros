<template>
  <a-card :body-style="cardStyle">
    <div>
      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="24">
            <a-col :md="8" :sm="24">
              <a-form-item label="仓库">
                <a-input placeholder="请输入仓库编码或者名称" v-model="queryParam.searchText" allowClear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="仓库类别">
                <j-dict-select-tag v-model="queryParam.type" placeholder="请选择" dictCode="bu_warehouse_type" />
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
                <a-space>
                  <a-button @click="findList">查询</a-button>
                </a-space>
              </span>
            </a-col>
          </a-row>
          <a-row>
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" :loading="loading" @click="handleAdd" style="margin-left: 8px">新增</a-button>
                <a-button style="margin-left: 8px" @click="handleEdit(btnStatus.editRow)" :disabled="!btnStatus.edit"
                  >编辑</a-button
                >
                <a-button style="margin-left: 8px" @click="deleteRecord" :disabled="!btnStatus.del">删除</a-button>
                <!-- <a-button style="margin-left: 8px" @click="handleCloseAccount">关账</a-button>-->
<!--            不需要单独导入4级库位信息了，放到4级库存导入一起了    <a-upload
                  style="margin-left: 8px"
                  name="file"
                  :multiple="false"
                  :customRequest="customRequest"
                  :showUploadList="false"
                >
                  <a-button :loading="loading">导入库位信息</a-button>
                </a-upload>-->
              </a-space>
            </span>
          </a-row>
        </a-form>
      </div>
      <div style="height: calc(100vh - 288px)">
        <vxe-table
          border
          resizable
          height="100%"
          ref="listTable"
          :align="allAlign"
          :data="tableData"
          show-overflow="tooltip"
          :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          :edit-config="{ trigger: 'manual', mode: 'row' }"
          @checkbox-change="btnStatus.update()"
          @checkbox-all="btnStatus.update()"
        >
          <vxe-table-column type="checkbox" width="40" />
          <vxe-table-column field="code" title="编码" width="120" align="left" header-align="center" />
          <vxe-table-column field="name" title="名称" width="180" align="left" header-align="center" />
          <vxe-table-column field="whLevel" title="级别" width="80" />
          <vxe-table-column field="type_dictText" title="类别" width="80" />
          <vxe-table-column field="close_dictText" title="是否关账" width="80" />
          <vxe-table-column field="lineName" title="线路" width="120" />
          <vxe-table-column field="depotName" title="车辆段" width="120" />
          <vxe-table-column field="location" title="位置描述" min-width="180" align="left" header-align="center" />
          <!-- <vxe-table-column title="操作" width="80" align="center">
            <template v-slot="{ row }">
              <a @click.stop="handleEdit(row)">编辑</a>
            </template>
          </vxe-table-column> -->
        </vxe-table>
      </div>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
      <warehouse-item-modal ref="modalForm" @ok="loadData()" :parentId="value"></warehouse-item-modal>
    </div>
  </a-card>
</template>

<script>
import { closeWarehouse, delWarehouseSetup, getWarehouseSetupList } from '@/api/tirosMaterialApi'
import WarehouseItemModal from '../modules/WarehouseItemModal'
import { importWarehouse } from '@api/tirosMaterialApi'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'RightWarehouse',
  props: ['value'],
  components: { WarehouseItemModal },
  data() {
    return {
      loading: false,
      queryParam: {
        searchText: '',
        type: null,
        pageNo: 1,
        pageSize: 10,
        parentId: '',
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      showEdit: false,
      cardStyle: {
        padding: '10px',
        height: 'calc(100vh - 120px)',
      },
      btnStatus: new TableBtnUtil(this, 'listTable'),
    }
  },
  created() {
    this.findList()
  },
  watch: {
    value: {
      immediate: true,
      handler(id) {
        this.queryParam.parentId = id
        this.tableData = []
        this.findList()
      },
    },
  },
  methods: {
    findList() {
      if(this.queryParam.searchText){
        this.queryParam.parentId=''
      }
      getWarehouseSetupList(this.queryParam).then((res) => {
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
      this.$emit('load')
    },
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleEdit(record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
    },
    handleCloseAccount() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否关账选中${selectRecords.length}数据？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            closeWarehouse('id=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    customRequest(data) {
      this.loading=true
      // 上传提交
      const formData = new FormData()
      formData.append('excelFile', data.file)
      this.saveFile(formData)
    },
    saveFile(formData) {
      if (this.queryParam.parentId) {
        if (this.queryParam.parentId !== '2') {
          this.loading = false
          this.$message.warn('请选择架大修二级仓库')
          return
        }

        formData.append('parentId', this.queryParam.parentId)
        importWarehouse(formData).then((res) => {
          this.loading = false
          if (res.success) {
            this.$message.success(res.message)
            this.findList()
            this.$emit('load')
          } else {
            this.$message.error(res.message)
          }
        })
      } else {
        this.loading = false
        this.$message.warn('请选择要导入的上级仓库')
      }
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
            delWarehouseSetup('ids=' + idsStr).then((res) => {
              this.findList()
              this.$emit('load')
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
<style scoped>
</style>