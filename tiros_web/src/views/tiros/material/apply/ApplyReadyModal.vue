<template>
  <a-modal
    title="物资备料"
    :width="'80%'"
    :bodyStyle="{height:'70vh'}"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :closable="true"
    @ok="handleOk"
    @cancel="handleCancel"
    :forceRender="true"
    :destroyOnClose="true"
>
        <a-spin :spinning="confirmLoading">
            <ApplyReadyComponent ref="applyReady" @ok="saveOk" @fail="saveFail" @close="close"></ApplyReadyComponent>
        </a-spin>
  </a-modal>

</template>

<script>
import ApplyReadyComponent from '@views/tiros/material/apply/ApplyReadyComponent'

export default {
  name: 'ApplyReadyModal',
  components: { ApplyReadyComponent },
  data () {
    return {
      visible: false,
      confirmLoading: false,
    }
  },
  methods: {
    showModal (id) {
      this.visible=true
      this.$refs.applyReady.edit(id)
    },
    // 确定
    handleOk () {
      this.confirmLoading = true
      this.$refs.applyReady.save();
    },
    saveOk () {
      this.$emit('ok')
      this.$message.success('保存成功')
      this.close()
    },
    saveFail () {
      this.confirmLoading=false
    },
    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.confirmLoading=false
      this.$emit('close')
      this.visible = false
    },
  }
}
</script>