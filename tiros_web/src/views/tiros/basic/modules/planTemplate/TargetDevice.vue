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
                @click="$refs.assetSelect.showModal()"
                >添加</a-button
              >
              <a-button type="dashed" @click="handleDel">删除</a-button>
            </a-space>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <vxe-table
      border
      ref="listTable"
      :align="allAlign"
      show-overflow="tooltip"
      :data="targetDeviceList"
      :checkbox-config="{ trigger: 'row', highlight: true }"
    >
      <vxe-table-column type="checkbox" width="40"></vxe-table-column>
      <vxe-table-column
        field="structDetailCode"
        title="编码"
        width="300"
      ></vxe-table-column>
      <vxe-table-column
        field="structDetailName"
        title="名称"
        align="left"
        header-align="center"
      ></vxe-table-column>
      <vxe-table-column
        field="structDetailStructType_dictText"
        title="类型"
        width="300"
      ></vxe-table-column>
      <!-- <vxe-table-column
        field="placeDesc"
        title="位置描述"
        align="left"
        header-align="center"
      ></vxe-table-column> -->
    </vxe-table>

    <train-structure-list
      ref="assetSelect"
      :lineId="lineId"
      :multiple="true"
      :canSelectType="[3, 4]"
      @ok="addAsset"
    ></train-structure-list>
  </div>
</template>

<script>
import TrainStructureList from "@views/tiros/common/selectModules/TrainStructureList";

export default {
  name: "TargetDevice",
  components: { TrainStructureList },
  props: {
    readOnly: {
      type: Boolean,
      default: false,
    },
    equipments:{
      type:Array,
      default(){
        return []
      }
    },
    planInfo: {
      type: Object,
      default(){
        return {}
      },
    },
    lineId: {
      default: '',
    },
    taskInfo: {
      type: Object,
      default(){
        return {}
      },
    },
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
  data() {
    return {
      allAlign: "center",
      targetDeviceList: [],
    };
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
        },
      });
    },
    addAsset(data) {
      let list = [];
      data.forEach((item1) => {
        console.log(this.targetDeviceList)
        if (!this.targetDeviceList.filter((item2) => item2.id == item1.id).length) {
          console.log(item1)
          console.log(this.planInfo,this.taskInfo)
          list.push({
            assetTypeId: item1.assetTypeId,
            assetTypeName: item1.assetTypeName,
            structDetailId: item1.id,
            structDetailCode: item1.code,
            structDetailName: item1.name,
            structDetailStructType_dictText: item1.structType_dictText,
            // placeDesc: item1.placeDesc,
            planId: this.planInfo.id,
            planTaskId: this.taskInfo.taskId,
          });
        } else {
          this.$message.warning(`${item1.name}-${item1.code}已存在列表中`);
        }
      });
      this.targetDeviceList = [...this.targetDeviceList, ...list];
      console.log(this.targetDeviceList)
      this.$emit('update:equipments', this.targetDeviceList)
    },
  },
};
</script>

<style></style>
