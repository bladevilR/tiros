<template>
  <a-modal
    :width="'100%'"
    centered
    title="工单查看"
    dialogClass="fullDialog no-footer"
    :visible="visible"
    @cancel="visible=false"
    :footer="null"
    :destroyOnClose="true"
  >
    <ViewWorkOrderComponent v-if="bizKey" :business-key="bizKey" na-flex-height-full> </ViewWorkOrderComponent>
  </a-modal>
</template>

<script>
import ViewWorkOrderComponent from '@views/tiros/dispatch/workOrder/ViewWorkOrderComponent'
export default {
  name: 'ViewWorkOrderModal',
  components:{ ViewWorkOrderComponent},
  data () {
    return {
      visible: false,
      bizKey: null
    }
  },
  methods: {
    showModal (orderId) {
      this.visible = true
      this.bizKey = orderId
    },
    close () {
      this.$emit('close')
      this.bizKey=null
      this.visible = false
    }
  }
}
</script>

<style scoped>

</style>