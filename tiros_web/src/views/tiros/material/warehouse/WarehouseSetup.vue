<template>
  <a-row type="flex" :gutter="16">
    <a-col :md="5" :sm="24" >
      <left-warehouse v-model="warehouseId" ref="leftWarehouse"/>
    </a-col>
    <a-col :md="24-5" :sm="24">
      <right-warehouse  v-model="warehouseId" @load="loadData()"/>
    </a-col>
  </a-row>
</template>
<script>
  import LeftWarehouse from '../warehouse/LeftWarehousePage'
  import RightWarehouse from '../warehouse/RightWarehousePage'

  export default {
    name: 'WarehouseSetup',
    components: { LeftWarehouse ,RightWarehouse},
    data() {
      return {
        warehouseId: ''
      }
    },
    methods:{
      loadData(){
        this.$refs.leftWarehouse.onSelectTree(this.warehouseId)
        // this.$refs.leftWarehouse.queryTreeData()
      }
    }

  }
</script>
