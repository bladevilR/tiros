<template>
  <a-spin :spinning="spinning" tip="请耐心等待，正在同步中···">
    <a-row type="flex" :gutter="16">
      <a-col :md="5" :sm="24">
        <left-put-stock @change="spinningChange" @ok="$refs.rightStock.findList()" v-model="warehouseId" ref="leftPutStock"></left-put-stock>
      </a-col>
      <a-col :md="24 - 5" :sm="24">
        <right-stock v-model="warehouseId" ref="rightStock" @selectTree="selectTree()"></right-stock>
      </a-col>
    </a-row>
  </a-spin>
</template>
<script>
import RightStock from "./RightStock";
import LeftPutStock from "./LeftPutStock";
export default {
  name: "Stock",
  components: { RightStock, LeftPutStock },
  data() {
    return {
      spinning:false,
      warehouseId: "",
    };
  },
  methods: {
    spinningChange(data){
      this.spinning = data;
    },
    selectTree() {
      this.$refs.leftPutStock.queryTreeData();
    },
  },
};
</script>
