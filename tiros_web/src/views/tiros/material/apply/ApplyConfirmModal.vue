<template>
  <a-modal
    title="领用确认"
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
      <ApplyConfirmComponent ref="applyReady" @ok="saveOk" @fail="saveFail" @close="close"></ApplyConfirmComponent>
    </a-spin>
  </a-modal>
</template>

<script>
import ApplyConfirmComponent from '@views/tiros/material/apply/ApplyConfirmComponent'

export default {
    name: 'ApplyConfirmModal',
  components: { ApplyConfirmComponent },
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