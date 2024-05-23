<template>
  <a-modal
    ref="locationModalRef"
    title="库位选择"
    :width="'80%'"
    :bodyStyle="{ height: '70vh' }"
    :visible="visible"
    :centered="true"
    :confirmLoading="confirmLoading"
    :closable="true"
    @ok="handleOk"
    @cancel="handleCancel"
    :forceRender="true"
    :destroyOnClose="true"
    :class="{ 'na-footer-none': modeType === 2 }"
  >
    <!-- 查询条件只有一个物料ID -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="库位选择">
              <a-checkbox-group
                v-model="warehouseLevelList"
                name="checkboxgroup"
                :options="options"
                @change="findList"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12">
            <a-space>
              <a-button type="primary" @click="findList">查询</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100% - 66px)" v-if="loading" id="location_table_main">
      <vxe-table
        border
        ref="listTable"
        align="center"
        height="auto"
        show-overflow="tooltip"
        :data.sync="tableData"
        :tree-config="{ key: 'warehouseId', children: 'children', expandAll: true }"
        :radio-config="{ highlight: true, trigger: 'row' }"
        :row-class-name="setRowClass"
        @cell-click="onCurrentChange"
      >
        <vxe-table-column type="radio" width="40px" v-if="modeType === 1" />
        <vxe-table-column
          field="sourceLocationName"
          width="200"
          title="所属组织"
          header-align="center"
          align="left"
          tree-node
        />
        <vxe-table-column
          width="80"
          field="warehouseLevel"
          title="库位级别"
          :formatter="formatLevel"
        />
        <vxe-table-column width="120" field="materialTypeCode" title="物资编码" />
        <vxe-table-column
          field="materialTypeName"
          title="物资名称"
          min-width="130"
          header-align="center"
          align="left"
        />
        <!-- <vxe-table-column width="91px" field="warehouseId" title="库位编码"  /> -->
        <!--        <vxe-table-column width="91px" field="warehouseName" title="库位名称" header-align="center" align="left"/>-->
        <vxe-table-column field="tradeNo" width="200" title="所属批次" />
        <vxe-table-column width="80" field="amount" title="当前库存" />
        <vxe-table-column width="80" field="usableAmount" title="可用库存"/>
        <vxe-table-column width="160" field="usedDetailInfo" title="占用详情" header-align="center" align="left" />
        <vxe-table-column width="80" field="price" title="价格" />
        <vxe-table-column width="80" field="unit" title="单位" />
        <vxe-table-column width="100" field="materialType_dictText" title="物料分类" />
        <vxe-table-column width="100" field="materialAttr_dictText" title="物料属性" />
        <vxe-table-column
          field="spec"
          title="物料描述"
          min-width="100"
          header-align="center"
          align="left"
        />
      </vxe-table>
    </div>
  </a-modal>
</template>

<script>
import { getStockList } from "@/api/tirosMaterialApi";

export default {
  name: "LocationList",
  props: {
    multiple: {
      type: Boolean,
      default: false,
    },
    //
    modeType: {
      type: Number,
      default: 1,
    },
  },
  data() {
    return {
      visible: false,
      confirmLoading: false,
      loading: false,
      tableData: [],
      selectMaterialTypeId: "",
      warehouseLevelList: [],
      options: [
        { label: "三级库", value: 3 },
        { label: "四级库", value: 4 },
      ],
    };
  },
  updated() {
    this.$nextTick(() => {
      this.loading = true;
    });
  },
  created() {},
  methods: {
    showModal(materialTypeId, applyDetailId, assignDetailId) {
      this.visible = true;
      this.selectMaterialTypeId = materialTypeId;
      this.findList(applyDetailId, assignDetailId);
    },
    findList(applyDetailId, assignDetailId) {
      getStockList({
        warehouseLevelList: this.warehouseLevelList,
        materialTypeId: this.selectMaterialTypeId,
        applyDetailId: applyDetailId,
        assignDetailId: assignDetailId
      }).then((res) => {
        if (res.success) {
          this.tableData = [];
          this.tableData = this.buildTree(res.result);
          this.$nextTick(() => {
            this.$refs.listTable.setAllTreeExpand(true);
          });
        }
      });
    },
    buildTree(list) {
      const idMapping = list.reduce((acc, el, i) => {
        acc[el.warehouseId] = i;
        return acc;
      }, {});
      let roots = [];
      list.forEach((el) => {
        // 判断根节点
        console.log(el.parentId)
        if (el.parentId === null) {
          roots.push(el);
        }else{
          // 用映射表找到父元素
          const parentEl = list[idMapping[el.parentId]];

          // console.log(parentEl)
          // // 把当前元素添加到父元素的`children`数组中  ！！！！！找不到父元素还添加个JB！！！！！
          // parentEl.children = [...(parentEl.children || []), el];

          if(parentEl){
            parentEl.children = [...(parentEl.children || []), el];
          }else{
            roots.push(el);
          }
        }
        
      });
      return roots;
    },
    formatLevel({ cellValue }) {
      let level = parseInt(cellValue);
      return level - 1;
    },
    // 确定
    handleOk() {
      let records = this.$refs.listTable.getRadioRecord();
      if (records.children) {
        this.$message.warn("该库位还有下级库位，请选择下级库位！");
        return;
      }
      this.$emit("ok", records ? [records] : []);
      this.visible = false;
    },
    saveOk() {
      // this.$emit('ok')
      // this.$message.success('保存成功')
      this.close();
    },
    saveFail() {
      this.confirmLoading = false;
    },
    // 关闭
    handleCancel() {
      this.close();
    },
    close() {
      // this.confirmLoading=false
      // this.$emit('close')
      this.visible = false;
      Object.assign(this.$data, this.$options.data());
    },
    // 打开托盘Modal
    openPalletModal() {
      // console.log('打开托盘！')
      this.$refs.palletSelect.blur();
    },
    // 打开库位选择
    openlocationModal() {
      // console.log('打开库位选择！')
      this.$refs.materialToolsModal.showModal();
      this.$refs.locationSelect.blur();
    },
    //选择行事件
    onCurrentChange({ row, $event }) {
      if (this.modeType === 2) {
        if (
          row.children &&
          row.children.length &&
          !/vxe-tree--node-btn/.test($event.target.className)
        ) {
          this.$refs.listTable.setTreeExpand(
            [row],
            !this.$refs.listTable.isTreeExpandByRow(row)
          );
        } else {
          this.$emit("ok", [row]);
          this.visible = false;
        }
      }
    },
    setRowClass({ row }) {
      return this.modeType === 2 ? "mode-type-choose" : "";
    },
  },
};
</script>
<style lang="less">
#location_table_main .mode-type-choose {
  cursor: pointer;
}
</style>
