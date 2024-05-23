<template>
  <a-modal
    title="托盘选择"
    :width="'80%'"
    :bodyStyle="{ height: '70vh' }"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :closable="true"
    :centered="true"
    @ok="handleOk"
    @cancel="handleCancel"
    :forceRender="true"
    :destroyOnClose="true"
    :class="{'na-footer-none': modeType === 2}"
  >
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="托盘编码">
              <a-input v-model="queryParam.searchText" @keyup.enter="findList" />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="物资类型">
              <a-select
                ref="materialSelect"
                allowClear
                v-model="materialTypeName"
                placeholder="请选择物料"
                :open="false"
                style="width: 100%"
                @change="clearMaterialChange"
                @dropdownVisibleChange="openMaterialSelectModal"
              >
                <a-icon slot="suffixIcon" type="ellipsis" @click.stop="queryParam.typeId = ''"/>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="使用状态">
              <j-dict-select-tag
                v-model="queryParam.useStatus"
                placeholder="请选择"
                dictCode="bu_pallet_use_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="托盘状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择"
                dictCode="bu_valid_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4">
            <a-space>
              <a-button type="primary" @click="findList">查询</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100% - 110px)" v-if="loading" id="pallet_table_main">
      <vxe-table
        border
        ref="listTable"
        align="center"
        height="auto"
        :data="tableData"
        show-overflow="tooltip"
        :radio-config="{ highlight: true, trigger: 'row' }"
        :row-class-name="setRowClass"
        @cell-click="onCurrentChange"
      >
        <vxe-table-column type="radio" width="60px" v-if="modeType === 1"/>
        <vxe-table-column field="code" title="托盘编码" />
        <vxe-table-column field="name" title="托盘名称" />
        <vxe-table-column
          field="materialTypeNames"
          title="物资类型"
          min-width="100"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column field="size" title="尺寸" />
        <vxe-table-column field="texture" title="材质" />
        <vxe-table-column field="useStatus_dictText" title="使用状态" width="120"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="80"/>
        <vxe-table-column field="remark" title="备注" />
      </vxe-table>
    </div>
    <vxe-pager
      perfect
      :current-page.sync="queryParam.pageNo"
      :page-size.sync="queryParam.pageSize"
      :total="page.totalResult"
      :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
      @page-change="handlePageChange"
    ></vxe-pager>
    <material-list ref="materialForm" :multiple="false" @ok="onSelectMaterial"></material-list>
  </a-modal>
</template>

<script>
import { getPalletList } from '@/api/tirosMaterialApi'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'

export default {
  name: 'PalletList',
  components:{ MaterialList },
  props: {
    multiple: {
      type: Boolean,
      default: false,
    },
    // 1, 默认选择 2：点击后直接选择
    modeType: {
      type: Number,
      default: 1
    }
  },
  data() {
    return {
      visible: false,
      confirmLoading: false,
      loading: false,
      tableData: [],
      selectMaterialTypeId: '',
      materialTypeName: '',
      queryParam: {
        pageNo: 1,
        pageSize: 10,
        searchText: '',
        typeId: null,
        useStatus: null,
        status: null
      },
      page: {
        totalResult: 3,
      },
    }
  },
  updated() {
    this.$nextTick(() => {
      this.loading = true
    })
  },
  created() {},
  methods: {
    showModal(materialTypeId = null, name = null) {
      this.visible = true
      this.selectMaterialTypeId = materialTypeId
      this.materialTypeName = name
      this.queryParam.typeId = materialTypeId
      this.findList()
      // this.$refs.applyReady.edit(id)
    },
    findList() {
      getPalletList(this.queryParam).then((res) => {
        if (res.success) {
          this.tableData = res.result.records
          this.page.totalResult = res.result.total
        }
      })
    },
    // 确定
    handleOk() {
      let records = this.$refs.listTable.getRadioRecord()
      if (records && records.useStatus) {
        this.$confirm({
          content: `该托盘正在使用中，确认继续选择该托盘？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            this.$emit('ok', records ? [records] : [])
            this.visible = false
          },
        })
        return
      }
      this.$emit('ok', records ? [records] : [])
      this.visible = false
    },
    saveOk() {
      // this.$emit('ok')
      // this.$message.success('保存成功')
      this.close()
    },
    saveFail() {
      this.confirmLoading = false
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    close() {
      // this.confirmLoading=false
      // this.$emit('close')
      this.visible = false
    },
    // 选择物资类别
    openMaterialSelectModal(open) {
      if(open){
        this.$refs.materialForm.showModal()
        this.$refs.materialSelect.blur()
      }
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
    //选择行事件
    onCurrentChange({row, $event}){
      if (this.modeType === 2) {
        // 托盘没有树结构，不需要展开树判断
        // if (row.children && row.children.length &&  !/vxe-tree--node-btn/.test($event.target.className)) {
        //   this.$refs.listTable.setTreeExpand([row], !this.$refs.listTable.isTreeExpandByRow(row))
        // } else {
           this.$emit('ok', [row])
           this.visible = false
        // }
      }
    },
    setRowClass(){
      return this.modeType === 2 ? 'mode-type-choose' : ''
    }
  },
}
</script>
<style lang="less">
#pallet_table_main .mode-type-choose{
  cursor: pointer;
}
</style>