<template>
  <div class="bodyWrapper">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="托盘">
              <a-input placeholder="请输入编码或者名称" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="物资类型">
              <a-select
                ref="materialSelect"
                allowClear
                v-model="materialTypeName"
                placeholder="请选择物料"
                :open="false"
                style="width: 100%"
                @change="clearMaterialChange"
                @dropdownVisibleChange="openMaterialSelectModal()"
              >
                <a-icon slot="suffixIcon" type="ellipsis" @click.stop="queryParam.typeId = ''"/>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col>
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button @click="bindMaterialType" :disabled="!btnStatus.del">绑定物资类型</a-button>
                <a-button @click="handleEdit(btnStatus.editRow)" :disabled="!btnStatus.edit">编辑</a-button>
                <a-button @click="deleteRecord" :disabled="!btnStatus.del">删除</a-button>
              </a-space>
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
        @checkbox-change="btnStatus.update()"
        @checkbox-all="btnStatus.update()"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="code" title="托盘编码" width="100"></vxe-table-column>
        <vxe-table-column
          field="name"
          title="托盘名称"
          min-width="100"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="materialTypeNames"
          title="物资类型"
          min-width="100"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="palletSize"
          title="尺寸"
          min-width="100"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="texture"
          title="材质"
          min-width="100"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column field="useStatus_dictText" title="使用状态" width="120"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="80"></vxe-table-column>
        <vxe-table-column
          field="remark"
          title="备注"
          min-width="120"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <!-- <vxe-table-column title="操作" width="80">
          <template v-slot="{ row }">
            <a @click.stop="handleEdit(row)">编辑</a>
          </template>
        </vxe-table-column> -->
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
      <pallet-item-modal ref="modalForm" @ok="loadData()"></pallet-item-modal>
      <material-list ref="materModalForm" :multiple="true" @ok="selectmater"></material-list>
      <material-list ref="materialForm" :multiple="false" @ok="onSelectMaterial"></material-list>
    </div>
  </div>
</template>


<script>
import { delPallet,bindMType, getPalletList } from '../../../../api/tirosMaterialApi'
import PalletItemModal from '../modules/PalletItemModal'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  components: { PalletItemModal,MaterialList },
  data() {
    return {
      tableData: [],
      allAlign: 'center',
      materialTypeName: '',
      totalResult: 0,
      queryParam: {
        searchText: '',
        pageNo: 1,
        pageSize: 10,
      },
      btnStatus: new TableBtnUtil(this, 'listTable'),
    }
  },
  created() {
    this.findList()
  },
  methods: {
    loadData() {
      this.findList()
    },
    // 选择物资类别
    openMaterialSelectModal() {
      this.$refs.materialForm.showModal()
      this.$refs.materialSelect.blur()
    },
    // 清空物资
    clearMaterialChange(e){
      if(!e){
        this.queryParam.typeId = null
        this.materialTypeName = null
      }
    },
    onSelectMaterial(data){
      if (data.length) {
        console.log(data[0]);
        this.queryParam.typeId = data[0].id
        this.materialTypeName = data[0].name
      }
    },
    findList() {
      this.loading = true
      getPalletList(this.queryParam).then((res) => {
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
    handleEdit(record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
      this.$refs.modalForm.showBatch = true
      this.$refs.modalForm.isBatchAdd = false
    },
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
      this.$refs.modalForm.showBatch = false
    },
    bindMaterialType() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$refs.materModalForm.showModal();
      } else {
        this.$message.warn('尚未选中任何数据!')
      }
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            var idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delPallet('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    selectmater(data) {
      let records = [],
          selectRecords = this.$refs.listTable.getCheckboxRecords();
      data.forEach((element) => {
        records.push(element.id)
      })
      records = records.join(',')
      if(records.length<=0){
        this.$message.warn('请选择物资类型!')
        return
      }
      this.$confirm({
          content: `是否绑定物资至选中${selectRecords.length}条数据？`,
          onOk: () => {
            var idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            bindMType({
              materialTypes: records,
	            palletIds: idsStr
            }).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
    },
  },
}
</script>