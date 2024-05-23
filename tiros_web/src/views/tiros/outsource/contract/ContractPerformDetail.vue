<template>
  <a-modal
    :width="'85%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    
    @cancel="handleCancel"
    :closable="true"
    :footer="null"
    :destroyOnClose="true"
  >
    <div>
      <a-tabs :active-key="activeKey" @change="handleActive">
        <a-tab-pane key="1" tab="开工审核情况" :forceRender="true">
          <perform-resource :contractId="contractId"
                            :fileType=1
          ></perform-resource>
        </a-tab-pane>
        <a-tab-pane key="2" tab="计划实现情况">
          <perform-resource  :contractId="contractId"
                             :fileType=2>

          </perform-resource>

        </a-tab-pane>
        <a-tab-pane key="3" tab="质量情况">
          <perform-resource  :contractId="contractId"
                             :fileType=3></perform-resource>

        </a-tab-pane>
        <a-tab-pane key="4" tab="售后响应情况">
          <perform-resource  :contractId="contractId"
                             :fileType=4></perform-resource>
        </a-tab-pane>
      </a-tabs>
    </div>

  </a-modal>
</template>

<script>
import PerformResource from '@views/tiros/outsource/perform/PerformResource'
export default {
    name: 'PerformDetail',
    components:{PerformResource},
    data() {
      return {
        title:'',
        confirmLoading:false,
        visible: false,
        activeKey:'1',
        contractId: "",
        supplierId: ''
      }
    },
    methods: {
      show(value, supplierId){
        this.contractId=value
        this.supplierId = supplierId
        this.visible=true
      },
      handleActive(e){
        this.activeKey=e
      },
      // 关闭
      handleCancel () {
        this.close()
      },
      close () {
        this.$emit('close')
        this.visible = false
        this.activeKey='1'
      }
    }
    }
</script>

<style scoped>

</style>