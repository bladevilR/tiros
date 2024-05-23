<template>
  <a-modal
    title="发料工单编辑"
    :width="'100%'"
    centered
    dialogClass="fullDialog no-footer"
    :visible="visible"
    :footer="null"
    @cancel="close"
  >
    <EditWorkOrderComponent v-if="visible" ref="editForm" :business-key="orderId" :o-type="4" :from-type="3" @ok="ok" @fail="fail" @close="close"></EditWorkOrderComponent>
  </a-modal>
</template>

<script>
import EditWorkOrderComponent from '@views/tiros/dispatch/workOrder/EditWorkOrderComponent'

export default {
  name: 'WorkOrderModal',
  components: {
    EditWorkOrderComponent
  },
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