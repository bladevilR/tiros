<template>
  <a-modal
    :title="title"
    :width="'85%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    dialogClass="fullDialog no-title no-footer"
    @cancel="handleCancel"
    :destroyOnClose="true"
  >
  <a-tabs :activeKey="activeKey" @change="changeTab">
    <a-tab-pane key="1" tab="基本信息">
      <material-manage-info :asset-id="id"></material-manage-info>
    </a-tab-pane>
    <a-tab-pane key="2" tab="使用记录">
     <use-record :asset-id="id"></use-record>
    </a-tab-pane>
    <a-tab-pane key="3" tab="故障记录">
      <fault-record :asset-id="id"></fault-record>
    </a-tab-pane>
    <a-tab-pane key="4" tab="测量记录">
   <measurement-record :asset-id="id"></measurement-record>
    </a-tab-pane>
    <a-tab-pane key="5" tab="维修记录">
    <work-record :asset-id="id"></work-record>
    </a-tab-pane>
    <a-tab-pane key="6" tab="文档资料">
      <doc-record ></doc-record>
    </a-tab-pane>
  </a-tabs>
  </a-modal>
</template>

<script>
  import MaterialManageInfo from '@views/tiros/group/rotablemanage/MaterialManageInfo'
  import UseRecord from '@views/tiros/group/rotablemanage/UseRecord'
  import FaultRecord from '@views/tiros/group/rotablemanage/FaultRecord'
  import MeasurementRecord from '@views/tiros/group/rotablemanage/MeasurementRecord'
  import WorkRecord from '@views/tiros/group/rotablemanage/WorkRecord'
  import DocRecord from '@views/tiros/group/rotablemanage/DocRecord'

  export default {
    name: 'RotableManageInfo',
    components:{ MaterialManageInfo ,UseRecord,FaultRecord,MeasurementRecord,WorkRecord,DocRecord},
    data() {
      return {
        activeKey: '1',
        visible:false,
        confirmLoading:false,
        title:"周转件",
         id:''
      }
    },
    methods: {
      changeTab(activeKey) {
        console.log(activeKey)
        this.activeKey = activeKey
      },
      show(id){
        this.visible=true
        this.id=id
      },
      // 关闭
      handleCancel () {
        this.close()
      },
      close () {
        this.$emit('close')
        this.visible = false
        this.activeKey = '1'
      },
    },
  }
</script>

<style scoped>

</style>