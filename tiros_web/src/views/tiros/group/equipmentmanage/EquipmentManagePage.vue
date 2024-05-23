<template>
  <div>
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="4" :sm="24">
            <a-form-item label="工装">
              <a-input placeholder="请输入名称或编码" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择"
                dictCode="bu_tools_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="8">
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
        <vxe-table-column title="工装名称" min-width="160">
          <template v-slot="{ row }">
            <a @click.stop="jumpInfo(row)">{{row.name}}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="code" title="物资编码" width="120"></vxe-table-column>
        <vxe-table-column field="warehouseName" title="库位" width="120"></vxe-table-column>
        <vxe-table-column field="entraceDate" title="入场使用日期" width="120"></vxe-table-column>
        <vxe-table-column field="assetCode" title="资产编号" width="120"></vxe-table-column>
        <vxe-table-column field="lifetime" title="使用寿命" width="80"></vxe-table-column>
        <vxe-table-column field="amount" title="数量" width="80"></vxe-table-column>
        <vxe-table-column field="entryDate" title="入库日期" width="120"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="120"></vxe-table-column>
        <vxe-table-column field="groupName" title="所属班组" width="120"></vxe-table-column>
        <vxe-table-column field="supplierName" title="生产厂家" min-width="160"></vxe-table-column>
        <vxe-table-column field="model" title="规格" min-width="160"></vxe-table-column>
        <vxe-table-column title="操作" width="120" fixed="right">
          <template v-slot="{ row }">
            <a @click.stop="handleSet(row)">设置工装状态</a>
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
    <equipment-manage-modal ref="equipmentManageModal" @ok="loadData()"></equipment-manage-modal>
    <a-modal
      title='工装详情'
      :width="'90%'"
      centered
      :visible="infoVisible"
      dialogClass="fullDialog no-footer"
      @cancel="handleCancel"
      :forceRender="true"
      :destroyOnClose="true"
    >
      <equipment-manage-info ref="equipmentManageInfo"></equipment-manage-info>
    </a-modal>
  </div>
</template>

<script>
  import { getToolEquipmentList } from '@api/tirosGroupApi'
  import EquipmentManageModal from '../modules/EquipmentManageModal'
  import Vue from 'vue'
  import { USER_INFO } from '@/store/mutation-types'
  import EquipmentManageInfo from '../../group/equipmentmanage/EquipmentManageInfo'

  export default {
    name: 'EquipmentManagePage',
    components: { EquipmentManageInfo, EquipmentManageModal },
    data() {
      return {
        queryParam: {
          searchText: '',
          status: '',
          groupId: '',
          pageNo: 1,
          pageSize: 10
        },
        totalResult: 0,
        allAlign: 'center',
        tableData: [],
        count: '',
        infoVisible: false
      }
    },
    created() {
      this.findList()
    },
    methods: {
      findList() {
        this.loading = true
        const userInfo = Vue.ls.get(USER_INFO)
        this.queryParam.groupId = userInfo.departId
        getToolEquipmentList(this.queryParam).then((res) => {
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
      handleSet(record) {
        // console.log(record)
        this.$refs.equipmentManageModal.set(record)
        this.$refs.equipmentManageModal.title = '设置工装状态'
      },
      loadData() {
        this.findList()
      },
      jumpInfo(record) {
        this.infoVisible = true
        this.$nextTick(()=>{
          this.$refs.equipmentManageInfo.show(record)
        })

      },
      // 关闭
      handleCancel() {
        this.close()
      },
      close() {
        this.$emit('close')
        this.infoVisible = false
        this.$refs.equipmentManageInfo.close()
      }
    }
  }
</script>

<style scoped>

</style>