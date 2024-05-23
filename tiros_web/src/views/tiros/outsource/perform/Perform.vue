<template>
  <a-row type="flex" :gutter="16">
    <a-col :md="4" :sm="24">
      <left-perform v-model="id" :contract-ids="contractIds" v-on:changeContractIds="changeContractIds"></left-perform>
    </a-col>
    <a-col :md="24-4" :sm="24">
      <right-perform :id="id" :contract-ids="contractIds"></right-perform>
    </a-col>
  </a-row>

</template>

<script>
import LeftPerform from './LeftPerform'
import RightPerform from './RightPerform'

export default {
  name: 'Perform',
  components: { LeftPerform, RightPerform },
  data() {
    return {
      id: '',
      contractIds: ''
    }
  },
  methods: {
    changeContractIds(value) {
      this.contractIds = value
    }
  }
}
</script>

<style scoped>

</style>