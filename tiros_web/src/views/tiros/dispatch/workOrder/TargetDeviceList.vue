<template>
  <div>
    <a-form v-if="!readOnly" layout="inline">
      <a-row :gutter="24">
        <a-col :md="6" :sm="8">
          <a-form-item>
            <a-space>
              <a-button
                type="dashed"
                class="primary-color"
                @click="$refs.trainAssetSelectModal.showModal()"
                >添加</a-button
              >
              <a-button type="dashed" @click="handleDel">删除</a-button>
            </a-space>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <div style="height: 208px">
      <vxe-table
        border
        ref="listTable"
        :align="allAlign"
        height="auto"
        show-overflow="tooltip"
        :data="targetDeviceList"
        :checkbox-config="{ trigger: 'row', highlight: true }"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column type="seq" width="50"></vxe-table-column>
        <vxe-table-column
            field="trainAssetName"
            title="名称"
            tree-node
            align="left"
            header-align="center"
        ></vxe-table-column>
        <vxe-table-column align="left" header-align="center" field="trainAssetNo" title="编码" width="12%"></vxe-table-column>
        <!-- <vxe-table-column field="status_dictText" title="状态" width="100"></vxe-table-column> -->
        <vxe-table-column field="trainAssetStructType_dictText" title="类型" width="10%"></vxe-table-column>
        <!-- <vxe-table-column
            field="assetTypeName"
            title="设备类型"
            width="13%"
            align="left"
            header-align="center"
        ></vxe-table-column> -->
      </vxe-table>
    </div>
    <!-- 旧的资产设备选择 -->
    <!-- <CarDeviceSelect
      ref="trainAssetSelectModal"
      :trainNo="trainNo"
      :lineId="lineId"
      :multiple="true" 
      @ok="addAsset"
    >
    </CarDeviceSelect> -->
    <!-- maximo资产设备选择 -->
    <CarDeviceSelectNew
      ref="trainAssetSelectModal"
      :trainNo="trainNo"
      :lineId="lineId"
      :multiple="true" 
      @ok="addAsset"
    >
    </CarDeviceSelectNew>
  </div>
</template>

<script>
// import CarDeviceSelect from "@views/tiros/common/selectModules/CarDeviceSelect";
import CarDeviceSelectNew from "@views/tiros/common/selectModules/CarDeviceSelectNew";

export default {
  name: "TargetDeviceList",
  components: { 
    // CarDeviceSelect, 
    CarDeviceSelectNew 
  },
  props: {
    orderTaskId:{
      default: '',
    },
    equipments:{
      type: Array,
      default: () => {
        return []
      },
    },
    readOnly: {
      type: Boolean,
      default: false,
    },
    lineId: {
      type: [String, Number],
      default: "",
    },
    trainNo: {
      type: [String, Number],
      default: "",
    },
  },
  data() {
    return {
      allAlign: "center",
      targetDeviceList: [],
    };
  },
  watch:{
    equipments:{
      handler(newValue){
          this.targetDeviceList = newValue
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    handleDel() {
      let m = this.$refs.listTable.getCheckboxRecords();
      console.log(m);
      if (m.length < 1) {
        this.$message.warn("请选择要删除的数据");
        return;
      }
      this.$confirm({
        content: `确认删除选中的数据？`,
        onOk: () => {
          m.map((item1) => {
            this.targetDeviceList.map((item2, index2) => {
              if (item1.id == item2.id) {
                this.targetDeviceList.splice(index2, 1);
              }
            });
          });
          this.$emit('update:equipments',this.targetDeviceList)
        },
      });
    },
    addAsset(data) {
      let list = [];
      data.forEach((item1) => {
        if (!this.targetDeviceList.filter((item2) => item2.trainAssetId == item1.id).length) {
          // let l_item = item1;
          // delete l_item.children;
          // l_item.trainAssetId = l_item.id;
          // l_item.trainAssetName = l_item
          // console.log(l_item.trainAssetId)
          // delete l_item.children;
          // delete l_item.id;
          // l_item.orderTaskId = this.orderTaskId;
          console.log(item1)
          list.push({
            orderTaskId: this.orderTaskId,
            trainAssetName: item1.name,
            trainAssetId: item1.id,
            trainAssetNo: item1.code,
            trainAssetStructType_dictText: item1.maximoAssetType,
            // assetTypeName: item1.assetTypeName,
          });
        } else {
          this.$message.warning(`${item1.name}-${item1.code}已存在列表中`);
        }
      });
      console.log(list);
      this.targetDeviceList = [...this.targetDeviceList, ...list];
      this.$emit('update:equipments',this.targetDeviceList)
    },
  },
};
</script>

<style></style>
