<template>
  <a-row type="flex" :gutter="16">
    <a-col :md="4" :sm="24" >
<left-put-stock v-model="warehouseId"></left-put-stock>
    </a-col>
    <a-col :md="24-4" :sm="24">
<right-put-stock v-model="warehouseId"></right-put-stock>
    </a-col>
  </a-row>
</template>
<script>
  import RightPutStock from './RightPutStock'
  import LeftPutStock from './LeftPutStock'
  export  default {
    name:'PutStock',
    components:{RightPutStock,LeftPutStock},
    data(){
      return{
        warehouseId:''
      }
    }
  }
</script>