<template>

    <a-tabs :activeKey="activeKey" @change="changeTab">
      <a-tab-pane key="1" tab="基本信息">
        <material-info></material-info>
      </a-tab-pane>
      <a-tab-pane key="2" tab="培训信息">
<!--        <training-info></training-info>-->
      </a-tab-pane>
      <a-tab-pane key="3" tab="作业证件">
<!--        <cert-info></cert-info>-->
      </a-tab-pane>
      <a-tab-pane key="4" tab="技能标签">
<!--        <skill-tags-info></skill-tags-info>-->
      </a-tab-pane>
      <a-tab-pane key="5" tab="技能分布">
<!--        <radar :dataSource="radarData" :height="600" />-->
      </a-tab-pane>
    </a-tabs>
</template>

<script>
  import MaterialInfo from './MaterialInfo'

  export default {
    name: 'RotablesInfo',
    components:{ MaterialInfo },
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
