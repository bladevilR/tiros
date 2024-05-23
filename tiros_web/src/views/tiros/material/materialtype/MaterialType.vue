<template>
  <div class='bodyWrapper'>
    <!-- 查询区域 -->
    <div class='table-page-search-wrapper'>
      <a-form layout='inline'>
        <a-row :gutter='24'>
          <a-col :md='4' :sm='12'>
            <a-form-item label='物资'>
              <a-input placeholder='物资编码或者名称' v-model='queryParam.searchText' allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='12'>
            <a-form-item label='物资种类'>
              <j-dict-select-tag
                v-model='queryParam.type'
                dictCode='bu_material_kind'
                trigger-change
                @change='handleKind'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='物资分类'>
              <j-dict-select-tag v-model='queryParam.category1' :dictCode='dicType' @focus='handleTypeFocus' />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='物资属性'>
              <j-dict-select-tag v-model='queryParam.attr' dictCode='bu_material_attr' />
            </a-form-item>
          </a-col>
          <a-col :md='8' :sm='24'>
            <span style='float: left; overflow: hidden' class='table-page-search-submitButtons'>
              <a-space>
                <a-button @click='findList'>查询</a-button>
                <a-button @click='handleEdit(btnStatus.editRow)' :disabled='!btnStatus.edit'>编辑</a-button>
                <a-button type='primary' @click='handleAdd' v-has="'material:add'">新增</a-button>
                <a-button @click='deleteRecord' v-has="'material:delete'">删除</a-button>
                <!--                <a-button @click="handleAttr">属性设置</a-button> 不许要了 -->
                <a-button @click='$refs.materialTypeImportModal.show()'>导入物资类型</a-button>
                <a-button @click='handleEditCanReplace(btnStatus.editRow)' :disabled='!btnStatus.edit'>设置可替换</a-button>
                <a-button @click='$refs.materialReplaceImportModal.show()'>导入可替换</a-button>
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
        <vxe-table-column field='code' title='物资编码' width='120'></vxe-table-column>
        <vxe-table-column field='name' title='物资名称' width='150' align='left' header-align='center'></vxe-table-column>
        <vxe-table-column
          field='spec'
          title='物资描述'
          min-width='100'
          header-align='center'
          align='left'
        ></vxe-table-column>
        <vxe-table-column field='price' title='单价' width='100' header-align='center' align='right'></vxe-table-column>
        <vxe-table-column field='unit' title='物资单位' width='100'></vxe-table-column>
        <vxe-table-column field='status_dictText' title='状态' width='80'></vxe-table-column>
        <vxe-table-column field='kind_dictText' title='物资种类' width='100'></vxe-table-column>
        <vxe-table-column field='category1_dictText' align='left' header-align='center' title='物资分类'
                          width='100'></vxe-table-column>
        <vxe-table-column field='category2_dictText' title='物资属性' width='100'></vxe-table-column>
        <vxe-table-column field='canReplace' title='可替换物资' width='160'></vxe-table-column>
        <vxe-table-column
          field='remark'
          title='备注'
          min-width='120'
          header-align='center'
          align='left'
        ></vxe-table-column>
        <!-- <vxe-table-column title="操作" width="80" fixed="right">
          <template v-slot="{ row }">
            <a @click.stop="handleEdit(row)">编辑</a>
          </template>
        </vxe-table-column> -->
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
    <stock-item-modal ref='modalForm' @ok='loadData()'></stock-item-modal>
    <material-type-modal ref='itemForm' @ok='loadData()'></material-type-modal>
    <MaterialCanReplaceModal ref='materialCanReplaceModal' @ok='loadData()' />
    <j-import-modal :sum='1' ref='materialTypeImportModal' url='/import/materialType' @ok='loadData()'></j-import-modal>
    <j-import-modal :sum='1' ref='materialReplaceImportModal' url='/material/can-replace/import'
                    @ok='loadData()'></j-import-modal>
  </div>
</template>

<script>
import 'moment/locale/zh-cn'
import { deleteMaterialType, getMaterialList } from '@api/tirosMaterialApi'
import StockItemModal from '@views/tiros/material/modules/StocktemModal'
import MaterialTypeModal from '@views/tiros/material/modules/MaterialTypeModal'
import MaterialCanReplaceModal from '@views/tiros/material/materialtype/MaterialCanReplaceModal'
import { everythingIsEmpty } from '@/utils/util'
import JImportModal from '@/components/jeecg/JImportModal'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'MaterialType',
  components: { StockItemModal, MaterialTypeModal, MaterialCanReplaceModal, JImportModal },
  data() {
    return {
      queryParam: {
        searchText: '',
        type: '',
        category1: '',
        attr: '',
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
    handleKind(data) {
      if (data) {
        if (data === '2') this.dicType = 'bu_tools_type'
        if (data === '1') this.dicType = 'bu_material_type'
      } else {
        this.dicType = ''
        this.queryParam.category1 = ''
      }
    },
    handleTypeFocus() {
      if (everythingIsEmpty(this.queryParam.type)) this.$message.warn('请先选择物资种类')
    },
    findList() {
      getMaterialList(this.queryParam).then((res) => {
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
    handleAdd() {
      this.$refs.itemForm.add()
      this.$refs.itemForm.title = '新增'
    },
    handleEdit(record) {
      this.$refs.itemForm.edit(record)
      this.$refs.itemForm.title = '编辑'
    },
    handleEditCanReplace(record) {
      this.$refs.materialCanReplaceModal.show(record)
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？ 删除会影响系统所有物资业务数据`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function(obj, index) {
                return obj.id
              })
              .join(',')
            deleteMaterialType('ids=' + idsStr).then((res) => {
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

    handleAttr() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0 && selectRecords.length === 1) {
        let materialTypeId = selectRecords[0].id
        this.$refs.modalForm.edit(materialTypeId)
        this.$refs.modalForm.title = '属性设置'
      } else if (selectRecords.length > 1) {
        this.$message.warn('只能选择一条数据!')
      } else {
        this.$message.warn('尚未选中任何数据!')
      }
    }
  }
}
</script>