<template>
  <a-tabs :activeKey="activeKey" @change="changeTab" >
    <a-tab-pane key="1" tab="基本信息">
      <tools-info ref="toolsInfo"></tools-info>
    </a-tab-pane>
    <a-tab-pane key="2" tab="使用记录">
     <tool-use-record  ref='toolsUse' :id="id"></tool-use-record>
    </a-tab-pane>
<!--    <a-tab-pane key="3" tab="维保记录">
      &lt;!&ndash;              <cert-info></cert-info>&ndash;&gt;
    </a-tab-pane>-->
  </a-tabs>
</template>

<script>
  import ToolsInfo from '../toolsmanage/ToolsInfo'
  import ToolUseRecord from '@views/tiros/group/toolsmanage/ToolUseRecord'

  export default {
    name: 'ToolsManageInfo',
    components:{ ToolsInfo ,ToolUseRecord},
    data() {
      return {
        activeKey:'1',
        id:'',
      }
    },
    methods: {
      show(data){
           this.id=data.id
          this.$refs.toolsInfo.findList(data)
      },
      changeTab(activeKey) {
        this.activeKey = activeKey
        if(activeKey==='2'){
          setTimeout(()=>{this.$refs.toolsUse.findList()},0)
        }
      },
      close () {
        this.activeKey='1'
      }
    },
  }
</script>

<style scoped>

</style>