<template>
  <a-row type="flex" :gutter="16">
    <a-col :md="5" :sm="24">
      <type-list v-model="typeId" />
    </a-col>
    <a-col :md="24 - 5" :sm="24">
      <a-card id="alertRightContent">
        <add-alert-accept v-model="typeId" :toggle="toggle" @changeToggle="changeToggleValue" />
        <alert-list v-model="typeId" :toggle="toggle" />
      </a-card>
    </a-col>
  </a-row>
</template>
<script>
import TypeList from './modules/alertAccept/TypeList'
import AlertList from './modules/alertAccept/AlertList'
import AddAlertAccept from './modules/alertAccept/AddAlertAccept'

export default {
  name: 'AlertAccept',
  components: { TypeList, AlertList, AddAlertAccept },
  data() {
    return {
      toggle: true,
      typeId: '',
    }
  },

  methods: {
    changeToggleValue(value) {
      this.toggle = value
    },
  },
}
</script>
<style lang="less">
#alertRightContent {
    border: none;
  .ant-card-body {
    padding: 24px 24px 0;
    // height: calc(100vh - 120px);
    // overflow-y: auto;
  }
}
</style>
