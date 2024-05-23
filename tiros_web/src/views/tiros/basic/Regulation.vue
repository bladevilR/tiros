<template>
  <a-row type="flex" :gutter="16">
    <a-col :md="5" :sm="24">
      <left-page ref="leftPage" v-model="reguId" :trainTypeId="trainTypeId"   @select="getTrainTypeId"/>
    </a-col>
    <a-col :md="24-5" :sm="24">
      <right-page v-model="reguId" :trainTypeId="trainTypeId" @ok="loadData" />
    </a-col>
  </a-row>
</template>
<script>
  import LeftPage from './modules/regulation/LeftPage'
  import RightPage from './modules/regulation/RightPage'
  export default {
    name: 'Regulation',
    components: { LeftPage ,RightPage},
    data() {
      return {
        reguId: this.$route.params.value?this.$route.params.value.reguId:null,
        trainTypeId:this.$route.params.value?this.$route.params.value.trainTypeId:null
      }
    },
    created() {
    },

    methods: {
      getTrainTypeId(data){
        this.reguId = data.reguId;
        this.trainTypeId = data.trainTypeId
      },

      loadData() {
        this.$refs.leftPage.loadData()
      }
    }
  }
</script>
