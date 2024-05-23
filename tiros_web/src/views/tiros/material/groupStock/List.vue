<template>
  <div class='bodyWrapper'>
    <!-- 查询区域 -->
    <div class='table-page-search-wrapper'>
      <a-form layout='inline'>
        <a-row :gutter='24'>
          <a-col :md='4' :sm='24'>
            <a-form-item label='物资'>
              <a-input placeholder='物资编号或名称' :disabled='searchTextDis' v-model='queryParam.searchText'
                       allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='物资类别'>
              <j-dict-select-tag v-model='queryParam.materialType' placeholder='请选择' dictCode='bu_material_type' />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='车辆'>
              <j-dict-select-seach-tag
                v-model='queryParam.trainNo'
                placeholder='请选择'
                dictCode='bu_train_info,train_no,train_no'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='班组'>
              <j-dict-select-tag
                @loaderData='dictLoaderData'
                ref='groupSelect'
                :disabled='disGroup'
                v-model='queryParam.groupId'
                placeholder='请选择班组'
                dictCode='bu_mtr_workshop_group,group_name,id'
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter='24'>
          <a-col :md='4' :sm='24'>
            <span style='float: left; overflow: hidden' class='table-page-search-submitButtons'>
              <a-space>
                <a-button @click='findList'>查询</a-button>
                 <a-button v-if='!isComponent' @click='setMaterialAttribute'>物资属性设置</a-button>
                 <a-button v-if='!isComponent' @click='recoverMaterialAttribute'>恢复车间属性</a-button>
                 <a-button v-if='!isComponent' @click='transToTurnover'>转入周转件</a-button>
                 <a-button v-if='!isComponent' v-has="'group:stock:import'"
                           @click='$refs.importModal.show()'>导入</a-button>
                 <a-button v-if='!isComponent' @click='exportGroupStock'>导出</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div :style='tableHeight.top'>
      <vxe-table
        border
        :align='allAlign'
        ref='listTable'
        :data='tableData'
        height='auto'
        show-overflow='tooltip'
        :radio-config="!multiple ? { trigger: 'row' } : {}"
        :checkbox-config="
          multiple ? { trigger: 'row', highlight: true, range: true } : {}
        "
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column
          v-if='multiple'
          type='checkbox'
          width='40'
        ></vxe-table-column>
        <vxe-table-column v-else type='radio' width='40'></vxe-table-column>
        <vxe-table-column
          field='materialTypeCode'
          title='物资编码'
          width='140'
          header-align='center'
          align='center'
        ></vxe-table-column>
        <vxe-table-column
          field='materialTypeName'
          title='物资名称'
          width='150'
          header-align='center'
          align='left'
        ></vxe-table-column>
        <vxe-table-column field='spec' min-width='180' header-align='center' align='left'
                          title='物资描述'></vxe-table-column>
        <vxe-table-column field='trainNo' title='车号' width='80'></vxe-table-column>
        <vxe-table-column field='groupName' title='所属班组' width='100'></vxe-table-column>
        <vxe-table-column field='tradeNo' header-align='center' align='left' width='160' title='所属批次' />
        <vxe-table-column field='amount' title='库存数量' width='100'></vxe-table-column>
        <vxe-table-column field='usableAmount' title='当前可用量' width='100'></vxe-table-column>
        <vxe-table-column field='usedDetailInfo' title='占用详情' width='180' header-align='center'
                          align='left'></vxe-table-column>
        <vxe-table-column field='useCategory_dictText' title='类别' width='80'></vxe-table-column>
        <vxe-table-column field='materialAttr_dictText' title='属性' width='80'></vxe-table-column>
        <vxe-table-column field='price' title='价格' width='80' header-align='center' align='right'></vxe-table-column>
        <vxe-table-column field='systemName' title='所属系统' width='160'></vxe-table-column>
        <vxe-table-column field='workstationName' title='所属工位' width='160'></vxe-table-column>
      </vxe-table>
    </div>
    <vxe-pager
      perfect
      :current-page.sync='queryParam.pageNo'
      :page-size.sync='queryParam.pageSize'
      :total='totalResult'
      :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
      @page-change='handlePageChange'
    ></vxe-pager>
    <group-stock-import ref='importModal' url='/import/groupStock' @ok='findList()'></group-stock-import>
    <material-attr-modal ref='materialAttr' :ids='ids' @ok='findList()'></material-attr-modal>
    <TransTurnoverModal ref='transTurnoverModal' :group-stock-id='ids' @ok='findList()'></TransTurnoverModal>
  </div>
</template>


<script>
import { delApply, getGroupStockList, groupStockExport, recoverGroupStockAttribute } from '@/api/tirosMaterialApi'
import GroupStockImport from '@views/tiros/material/modules/GroupStockImport'
import MaterialAttrModal from '@views/tiros/material/modules/MaterialAttrModal'
import TransTurnoverModal from '@views/tiros/material/groupStock/TransTurnoverModal'

export default {
  name: 'List',
  props: {
    isComponent: {
      type: Boolean,
      default: false
    },
    multiple: {
      type: Boolean,
      default: true
    },
    groupId: {
      type: String,
      default: ''
    },
    materialTypeCode: {
      type: String,
      default: ''
    }
  },
  components: { GroupStockImport, MaterialAttrModal, TransTurnoverModal },
  data() {
    return {
      searchTextDis: false,
      disGroup: false,
      queryParam: {
        searchText: '',
        materialType: undefined,
        groupId: this.$store.getters.userInfo.departId,
        trainNo: null,
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      tableHeight: {
        top: this.isComponent ? 'height: calc(80vh - 183px)' : 'height: calc(100vh - 225px)',
        bottom: this.isComponent ? 'height: calc(80vh - 183px)' : 'height: calc(100vh - 225px)',
        size: '100%'
      },
      ids: ''
    }
  },
  mounted() {
    // setTimeout(()=>{

    // this.findList()
  },
  methods: {
    // 数据字典数据加载完成后触发
    dictLoaderData() {
      console.log('dictLoaderData')
      if (this.$store.getters.userInfo.departId) {
        let item = this.$refs.groupSelect.getSelectItem(this.$store.getters.userInfo.departId)
        if (item) {
          this.queryParam.groupId = this.$store.getters.userInfo.departId
          this.disGroup = true
        } else {
          this.queryParam.groupId = null
          this.disGroup = false
        }
        if (this.groupId) {
          this.queryParam.groupId = this.groupId
          this.disGroup = true
        }
        if (this.materialTypeCode) {
          this.queryParam.searchText = this.materialTypeCode
          this.searchTextDis = true
        }
      }
      this.findList()
    },
    getCheckboxRecords() {
      const result = this.multiple ? this.$refs.listTable.getCheckboxRecords() : this.$refs.listTable.getRadioRecord()
      console.log(result)
      return result
    },
    findList() {
      getGroupStockList(this.queryParam).then((res) => {
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
    exportGroupStock() {
      groupStockExport('班组库存导出.xls', {
        searchText: this.queryParam.searchText,
        materialType: this.queryParam.materialType,
        groupId: this.queryParam.groupId
      })
    },
    setMaterialAttribute() {
      let result = this.getCheckboxRecords()
      if (result.length > 0) {
        this.ids = result.map((obj) => obj.id).join(',')
        this.$refs.materialAttr.show()
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    recoverMaterialAttribute() {
      let result = this.getCheckboxRecords()
      if (result.length > 0) {
        let ids = result.map(obj => obj.id).join(',')
        let groupIds = result.map(obj => obj.groupId).join(',')
        recoverGroupStockAttribute('ids=' + ids + '&groupIds=' + groupIds).then((res) => {
          if (res.success) {
            this.$message.success(res.message)
            this.findList()
          } else {
            this.$message.error(res.message)
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    transToTurnover() {
      let selectRecords = this.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.length > 1) {
          this.$message.warning('只能选中一条数据!')
        } else {
          this.ids = selectRecords.map((obj) => obj.id).join(',')
          this.$refs.transTurnoverModal.show()
        }
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    }

  }
}
</script>