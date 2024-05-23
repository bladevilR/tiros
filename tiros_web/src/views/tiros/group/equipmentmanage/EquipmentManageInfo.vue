<template>

<!--  <div style="height: calc(70vh - 5px); overflow-y: auto">-->
    <a-tabs :activeKey="activeKey" @change="changeTab">
      <a-tab-pane key="1" tab="基本信息">
        <equ-manage-info ref="equManageInfo"></equ-manage-info>
      </a-tab-pane>
      <a-tab-pane key="2" tab="使用记录">
        <equipment-use-record ref="useRecord" :id="id"></equipment-use-record>
      </a-tab-pane>
<!--      <a-tab-pane key="3" tab="维保记录">
        &lt;!&ndash;              <cert-info></cert-info>&ndash;&gt;
      </a-tab-pane>-->
    </a-tabs>
<!--  </div>-->

</template>

<script>
  import EquManageInfo from './EquManageInfo'
  import EquipmentUseRecord from '@views/tiros/group/equipmentmanage/EquipmentUseRecord'

  export default {
    name: 'EquipmentManageInfo',
    components: { EquManageInfo,EquipmentUseRecord },
    data() {
      return {
        activeKey: '1',
         id:'',
      }
    },
    methods: {
      show(record) {
        this.id=record.id
        this.$refs.equManageInfo.showToolData(record)
      },
      changeTab(activeKey) {
        this.activeKey = activeKey
        if(activeKey==='2'){
          setTimeout(()=>{this.$refs.useRecord.findList()},0)
        }
      },
      close() {
       this.activeKey='1'
      }
    }
  }
</script>

<style scoped>

</style>