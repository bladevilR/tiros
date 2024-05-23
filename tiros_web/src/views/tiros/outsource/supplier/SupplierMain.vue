<template>
  <a-row type="flex" :gutter="16">
    <a-col :md="4" :sm="24">
      <left-supplier v-model="objTypeId"></left-supplier>
    </a-col>
    <a-col :md="24-4" :sm="24">
      <right-supplier v-model="objTypeId"></right-supplier>
    </a-col>
  </a-row>

</template>

<script>
  import LeftSupplier from './LeftSupplierPage'
  import RightSupplier from './RightSupplierPage'
  export default {
    name: 'SupplierMain',
    components:{LeftSupplier,RightSupplier},
    data (){
      return{
        objTypeId:''
      }
    }
  }
</script>

<style scoped>

</style>