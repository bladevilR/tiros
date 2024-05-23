<template>
  <a-tabs :activeKey="activeKey" @change="changeTab">
    <a-tab-pane key="1" tab="基本信息">
      <equ-info></equ-info>
    </a-tab-pane>
    <a-tab-pane key="2" tab="使用记录">
<!--              <training-info></training-info>-->
    </a-tab-pane>
    <a-tab-pane key="3" tab="维保记录">
<!--              <cert-info></cert-info>-->
    </a-tab-pane>
  </a-tabs>
</template>

<script>
  import EquInfo from '../equipment/EquInfo'

  export default {
    name: 'EquipmentInfo',
    components:{ EquInfo },
    data() {
      return {
        activeKey: '1',
        queryParam: {
          id: this.$route.params.id
        }

      }
    },
    methods: {
      changeTab(activeKey) {
        console.log(activeKey)
        this.activeKey = activeKey
      }
    },


  }
</script>

<style scoped>

</style>