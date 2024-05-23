<template>
  <a-row type="flex" :gutter="16">
    <a-col :md="5" :sm="24">
      <left-page  @select="getTrainTypeId"/>
    </a-col>
    <a-col :md="24-5" :sm="24">
      <right-page  :trainStruct="trainStructId" :trainType="trainTypeId"/>
    </a-col>
  </a-row>
</template>
<script>
  import LeftPage from './modules/trainStructure/LeftPage'
  import RightPage from './modules/trainStructure/RightPage'

  export default {
    name: 'TrainStructure',
    components: { LeftPage ,RightPage },
    data() {
      return {
        trainStructId: '',
        trainTypeId:''
      }
    },

    methods: {
      getTrainTypeId(data){
        this.trainStructId = data.structId;
        this.trainTypeId = data.typeId
      }
    }
  }
</script>

