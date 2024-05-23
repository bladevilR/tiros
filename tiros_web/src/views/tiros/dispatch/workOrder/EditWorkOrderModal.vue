<template>
  <a-modal
    title="工单编辑"
    :width="'100%'"
    centered
    dialogClass="fullDialog no-footer"
    :visible="visible"
    :footer="null"
    @cancel="close"
  >
  <EditWorkOrderComponent v-if="visible" ref="editForm" :business-key="orderId" :from-type="2" @ok="ok" @fail="fail" @close="close"></EditWorkOrderComponent>
  </a-modal>
</template>

<script>
import EditWorkOrderComponent from './EditWorkOrderComponent'

export default {
  components: {
    EditWorkOrderComponent
  },
  props: {

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
    // 创建故障工单
    createFaultOrder (orderInfo, tasks) {
      this.visible = true
      this.$nextTick(()=>{
        this.$refs.editForm.createFaultOrder(orderInfo, tasks)
      })
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

<style lang="less">
.ant-spin-nested-loading {
  height: 100% !important;
  .ant-spin-container {
    height: 100% !important;
  }
}

</style>
