<template>
  <!-- <a-row type="flex" :gutter="16" > -->
<!--    <a-col :md="4" :sm="24" >-->
<!--      <left-early-warning-page v-model="warehouseId" ref="leftEarlyWarningPage"/>-->
<!--    </a-col>-->
    <!-- <a-col :md="24" :sm="24"> -->
      <right-early-warning-page  v-model="warehouseId" @load="loadData()" na-flex-height-full/>
    <!-- </a-col>
  </a-row> -->
</template>

<script>
  import LeftEarlyWarningPage from './LeftEarlyWarningPage'
  import RightEarlyWarningPage from './RightEarlyWarningPage'
  export default {
    name: 'EarlyWarningPage',
    components: { RightEarlyWarningPage, LeftEarlyWarningPage },
    data() {
      return {
        warehouseId: ''
      }
    },
    methods:{
      loadData(){
        this.$refs.leftEarlyWarningPage.onSelectTree(this.warehouseId)
      }
    }
  }
</script>

<style scoped>

</style>