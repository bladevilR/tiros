<template>
  <a-modal
    title="车间消耗编辑"
    :width="'100%'"
    centered
    dialogClass="fullDialog no-footer"
    :visible="visible"
    :footer="null"
    @cancel="close"
  >
    <EditOrderComponent v-if="visible" :business-key="orderId" @ok="ok" @fail="fail" @close="close"></EditOrderComponent>
  </a-modal>
</template>

<script>
import EditOrderComponent from '@views/tiros/dispatch/workOrder/special/EditOrderComponent'
export default {
  name: 'EditOrderModal',
  components: { EditOrderComponent },

  data () {
    return {
      visible: false,
      orderId: null,
    }
  },
  methods: {
    // 确定
    showModal (id) {
      this.orderId = id
      this.visible = true
    },
    // 关闭
    ok () {
      this.$message.success('保存成功')
      this.$emit('ok')
    },
    fail (data) {
      this.$message.error(data.res.message)
      this.$emit('fail')
    },
    close () {
      this.$emit('close')
      this.visible = false
    },
  }
}
</script>

<style scoped>

</style>