<template>
  <a-row type="flex" :gutter="16">
    <a-col :md="4" :sm="24">
      <left-quality v-model="assetTypeId"></left-quality>
    </a-col>
    <a-col :md="24-4" :sm="24">
      <right-quality :assetTypeId="assetTypeId"></right-quality>
    </a-col>
  </a-row>
</template>

<script>
  import LeftQuality from './LeftQuality'
  import RightQuality from './RightQuality'
  export default {
    name: 'Quality',
    components:{LeftQuality,RightQuality},
    data(){
      return{
        assetTypeId:''
      }
    }
  }
</script>

<style scoped>

</style>